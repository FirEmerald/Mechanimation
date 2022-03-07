package firemerald.mechanimation.items;

import firemerald.api.core.items.ICustomSubtypes;
import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.blocks.IBlockMachine;
import firemerald.mechanimation.blocks.IBlockMultiblockParent;
import firemerald.mechanimation.tileentity.machine.base.IOrientedMachine;
import firemerald.mechanimation.util.EnumOrientation;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMachine<T extends Block & IBlockMachine> extends ItemMultiTexture implements ICustomSubtypes
{
	public final boolean useRegistryNameModel;

	@SideOnly(Side.CLIENT)
	public static class Renderer extends TileEntityItemStackRenderer
	{
		public static final Renderer INSTANCE = new Renderer();

	    @Override
		public void renderByItem(ItemStack stack, float partialTicks)
	    {
	    	Block block = Block.getBlockFromItem(stack.getItem());
	    	if (block instanceof IBlockMachine)
	    	{
	    		TileEntity tile = ((IBlockMachine) block).getToRender(stack);
	    		if (tile != null) TileEntityRendererDispatcher.instance.render(tile, 0, 0, 0, 0, partialTicks);
	    	}
	    }
	}

	public ItemMachine(T block)
	{
		this(block, false);
	}

	public ItemMachine(T block, boolean useRegistryNameModel)
	{
		super(block, block, (stack) -> block.getName(stack));
		this.useRegistryNameModel = useRegistryNameModel;
		if (FMLCommonHandler.instance().getSide().isClient()) doClientStuff();
	}

	@SideOnly(Side.CLIENT)
	public void doClientStuff()
	{
		setTileEntityItemStackRenderer(Renderer.INSTANCE);
	}

	public EnumOrientation getOrientation(EnumFacing side, float hitX, float hitY, float hitZ, EnumOrientation def)
	{
		EnumFacing front;
        hitX -= 0.5f;
        hitY -= 0.5f;
        hitZ -= 0.5f;
        switch (side)
        {
		case DOWN:
		case UP:
			if (hitX > hitZ)
			{
				if (hitX > -hitZ) front = EnumFacing.EAST;
				else front = EnumFacing.NORTH;
			}
			else
			{
				if (hitZ > -hitX) front = EnumFacing.SOUTH;
				else front = EnumFacing.WEST;
			}
			break;
		case NORTH:
		case SOUTH:
			if (hitX > hitY)
			{
				if (hitX > -hitY) front = EnumFacing.EAST;
				else front = EnumFacing.DOWN;
			}
			else
			{
				if (hitY > -hitX) front = EnumFacing.UP;
				else front = EnumFacing.WEST;
			}
			break;
		case WEST:
		case EAST:
			if (hitZ > hitY)
			{
				if (hitZ > -hitY) front = EnumFacing.SOUTH;
				else front = EnumFacing.DOWN;
			}
			else
			{
				if (hitY > -hitZ) front = EnumFacing.UP;
				else front = EnumFacing.NORTH;
			}
			break;
			default:
				front = side;
        }
        return EnumOrientation.getOrientation(side, front, def);
	}

    @Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();

        if (!block.isReplaceable(worldIn, pos))
        {
            pos = pos.offset(facing);
        }

        ItemStack itemstack = player.getHeldItem(hand);

        if (!itemstack.isEmpty())
        {
        	EnumOrientation orientation = this.block instanceof IBlockMachine ? getOrientation(facing, hitX, hitY, hitZ, null) : EnumOrientation.UP_SOUTH;
        	if (orientation != null)
        	{
                int i = this.getMetadata(itemstack.getMetadata());
                IBlockState iblockstate1 = this.block.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, i, player, hand);
                if (this.block instanceof IBlockMultiblockParent ? ((IBlockMultiblockParent) this.block).canPlaceAt(itemstack, player, worldIn, pos, facing, hitX, hitY, hitZ, iblockstate1, orientation) : (player.canPlayerEdit(pos, facing, itemstack) && worldIn.mayPlace(this.block, pos, false, facing, player)))
                {
                    if (placeBlockAt(itemstack, player, worldIn, pos, facing, hitX, hitY, hitZ, iblockstate1, orientation))
                    {
                        iblockstate1 = worldIn.getBlockState(pos);
                        SoundType soundtype = iblockstate1.getBlock().getSoundType(iblockstate1, worldIn, pos, player);
                        worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                        itemstack.shrink(1);
                    }
                    return EnumActionResult.SUCCESS;
                }
                else return EnumActionResult.FAIL;
        	}
            else return EnumActionResult.FAIL;
        }
        else return EnumActionResult.FAIL;
    }

    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState, EnumOrientation orientation)
    {
        if (!world.setBlockState(pos, newState, 11)) return false;

        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == this.block)
        {
            setTileEntityNBT(world, player, pos, stack);
        	TileEntity tile = world.getTileEntity(pos);
        	if (tile instanceof IOrientedMachine)
        	{
        		IOrientedMachine machine = (IOrientedMachine) tile;
                machine.setOrientation(orientation);
        	}
            this.block.onBlockPlacedBy(world, pos, state, player, stack);

            if (player instanceof EntityPlayerMP)
                CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos, stack);
        }

        return true;
    }

	@Override
	public void registerModels()
	{
		NonNullList<ItemStack> items = NonNullList.create();
		block.getSubBlocks(CreativeTabs.SEARCH, items);
		items.forEach(item -> ModelLoader.setCustomModelResourceLocation(this, item.getItemDamage(), useRegistryNameModel ? new ModelResourceLocation(block.getRegistryName().toString()) : new ModelResourceLocation(MechanimationAPI.MOD_ID + ":tile_entity_renderer")));
	}
}