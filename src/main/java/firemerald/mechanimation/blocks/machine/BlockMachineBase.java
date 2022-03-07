package firemerald.mechanimation.blocks.machine;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import firemerald.api.core.blocks.ICustomStateMapper;
import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.blocks.IBlockMachine;
import firemerald.mechanimation.blocks.ICustomBlockHighlight;
import firemerald.mechanimation.client.renderer.IBlockHighlight;
import firemerald.mechanimation.common.GuiHandler;
import firemerald.mechanimation.init.MechanimationTabs;
import firemerald.mechanimation.tileentity.machine.base.implementation.actual.TileEntityMachineBase;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.stats.StatBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockMachineBase<T extends TileEntityMachineBase<?, ?>, E extends Enum<E> & IMachineVariant<T>> extends BlockContainer implements ICustomBlockHighlight, ICustomStateMapper, IBlockMachine
{
	public abstract E getDefaultVariant();

	private E[] variants;
	private PropertyEnum<E> variantProp;

    @SideOnly(Side.CLIENT)
    private Map<E, T> renderTiles;

	public BlockMachineBase()
	{
		super(Material.IRON);
		this.setDefaultState(this.blockState.getBaseState().withProperty(variantProp, getDefaultVariant()));
		this.setSoundType(SoundType.METAL);
		this.setCreativeTab(MechanimationTabs.MACHINES);
		if (FMLCommonHandler.instance().getSide().isClient())
		{
			renderTiles = new HashMap<>();
			for (E variant : variants)
			{
				T renderTile = variant.newTile();
				renderTiles.put(variant, renderTile);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TileEntity getToRender(ItemStack stack)
	{
		return renderTiles.get(this.getStateFromMeta(stack.getItem().getMetadata(stack)).getValue(variantProp));
	}

	@Override
	public String getName(ItemStack stack)
	{
		return getStateFromMeta(stack.getItemDamage()).getValue(variantProp).getName();
	}

    @Override
	public boolean isOpaqueCube(IBlockState state)
    {
    	return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
	public int damageDropped(IBlockState state)
    {
    	return getMetaFromState(state);
    }

	@Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items)
    {
		for (int i = 0; i < variants.length; i++) items.add(new ItemStack(this, 1, i));
    }

    @Override
	protected BlockStateContainer createBlockState()
    {
    	@SuppressWarnings("unchecked")
		Class<E> variantClass = (Class<E>) getDefaultVariant().getClass();
        return new BlockStateContainer(this, variantProp = PropertyEnum.create("variant", variantClass, variants = variantClass.getEnumConstants()));
    }

	@Override
    public IBlockState getStateFromMeta(int meta)
    {
		if (meta < 0 || meta >= variants.length) meta = 0;
		return this.getDefaultState().withProperty(variantProp, variants[meta]);
    }

	@Override
    public int getMetaFromState(IBlockState state)
    {
		E variant = state.getValue(variantProp);
		for (int i = 0; i < variants.length; i++) if (variants[i] == variant) return i;
		return 0;
    }

	@Override
	public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		return state.getValue(variantProp).getColor();
	}

	@Override
	public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos)
	{
		return blockState.getValue(variantProp).getHardness();
	}

	@Override
	public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion)
	{
		return world.getBlockState(pos).getValue(variantProp).getExplosionResistance();
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return getStateFromMeta(meta).getValue(variantProp).newTile();
	}

    @Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof IInventory)
        {
            InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);
        }
        super.breakBlock(worldIn, pos, state);
    }

	@Override
	@SideOnly(Side.CLIENT)
	public IBlockHighlight getBlockHighlight(EntityPlayer player, RayTraceResult trace)
	{
		return IBlockHighlight.MACHINE;
	}

    /**
     * Called when the block is right clicked by a player.
     */
    @Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)
        {
            return true;
        }
        else
        {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof TileEntityMachineBase)
            {
            	TileEntityMachineBase<?, ?> press = (TileEntityMachineBase<?, ?>) tileentity;
                if (playerIn instanceof EntityPlayerMP && press.isLocked() && !playerIn.canOpen(press.getLockCode()) && !playerIn.isSpectator())
                {
                	((EntityPlayerMP) playerIn).connection.sendPacket(new SPacketChat(new TextComponentTranslation("container.isLocked", new Object[] {press.getDisplayName()}), ChatType.GAME_INFO));
                	((EntityPlayerMP) playerIn).connection.sendPacket(new SPacketSoundEffect(SoundEvents.BLOCK_CHEST_LOCKED, SoundCategory.BLOCKS, playerIn.posX, playerIn.posY, playerIn.posZ, 1.0F, 1.0F));
                    return false;
                }
            	playerIn.openGui(MechanimationAPI.MOD_ID, GuiHandler.ID_MACHINE, worldIn, pos.getX(), pos.getY(), pos.getZ());
            	StatBase interactionStat = press.getInteractionStat();
            	if (interactionStat != null) playerIn.addStat(interactionStat);
            }
            return true;
        }
    }

	@Override
	@SideOnly(Side.CLIENT)
	public IStateMapper getStateMapper()
	{
		return (new StateMap.Builder()).ignore(variantProp).build();
	}

	public PropertyEnum<E> getVariantProperty()
	{
		return variantProp;
	}
}