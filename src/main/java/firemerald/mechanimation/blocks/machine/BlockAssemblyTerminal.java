package firemerald.mechanimation.blocks.machine;

import java.util.List;
import java.util.function.Supplier;

import firemerald.api.core.client.Translator;
import firemerald.mechanimation.blocks.IBlockMultiblockParent;
import firemerald.mechanimation.blocks.machine.BlockAssemblyTerminal.EnumVariant;
import firemerald.mechanimation.init.MechanimationBlocks;
import firemerald.mechanimation.tileentity.machine.assembly_terminal.TileEntityAssemblyTerminalBase;
import firemerald.mechanimation.tileentity.machine.assembly_terminal.TileEntityAssemblyTerminalBasic;
import firemerald.mechanimation.util.EnumFace;
import firemerald.mechanimation.util.EnumOrientation;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockAssemblyTerminal extends BlockMachineBase<TileEntityAssemblyTerminalBase<?>, EnumVariant> implements IBlockMultiblockParent
{
	public static enum EnumVariant implements IMachineVariant<TileEntityAssemblyTerminalBase<?>>
	{
		BASIC("basic", MapColor.IRON, 5f, 10f, TileEntityAssemblyTerminalBasic::new);

        private final String name;
        private final MapColor color;
        private final float hardness;
        private final float resistance;
        private final Supplier<TileEntityAssemblyTerminalBase<?>> tile;

        private EnumVariant(String name, MapColor color, float hardness, Supplier<TileEntityAssemblyTerminalBase<?>> tile)
        {
        	this(name, color, hardness, hardness * 5, tile);
        }

        private EnumVariant(String name, MapColor color, float hardness, float resistance, Supplier<TileEntityAssemblyTerminalBase<?>> tile)
        {
            this.name = name;
            this.color = color;
            this.hardness = hardness;
            this.resistance = resistance;
            this.tile = tile;
        }

        @Override
		public String toString()
        {
            return this.name;
        }

        @Override
		public String getName()
        {
            return this.name;
        }

		@Override
		public TileEntityAssemblyTerminalBase<?> newTile()
		{
			return tile.get();
		}

		@Override
		public MapColor getColor()
		{
			return color;
		}

		@Override
		public float getHardness()
		{
			return hardness;
		}

		@Override
		public float getExplosionResistance()
		{
			return resistance;
		}
	}

	public static final AxisAlignedBB BOX = new AxisAlignedBB(0, 0, -1, 2, 1, 1);

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag)
	{
		super.addInformation(stack, world, list, flag);
		list.add(Translator.translate("lore.mechanimation.assembly_terminal"));
	}

	@Override
	public EnumVariant getDefaultVariant()
	{
		return EnumVariant.BASIC;
	}

    @Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
    	AxisAlignedBB box = new AxisAlignedBB(0, 0, 0, 1, 1, 1);
    	TileEntity tile = source.getTileEntity(pos);
    	if (tile instanceof TileEntityAssemblyTerminalBase)
    	{
    		EnumOrientation orientation = ((TileEntityAssemblyTerminalBase<?>) tile).getOrientation();
    		EnumFacing back = orientation.getFacing(EnumFace.BACK);
    		EnumFacing left = orientation.getFacing(EnumFace.LEFT);
    		box = box.expand(back.getFrontOffsetX() + left.getFrontOffsetX(), back.getFrontOffsetY() + left.getFrontOffsetY(), back.getFrontOffsetZ() + left.getFrontOffsetZ());
    	}
    	return box;
    }

    @Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
    	TileEntity tile = worldIn.getTileEntity(pos);
    	if (tile instanceof TileEntityAssemblyTerminalBase)
    	{
    		EnumOrientation orientation = ((TileEntityAssemblyTerminalBase<?>) tile).getOrientation();
    		BlockPos posBack = pos.offset(orientation.getFacing(EnumFace.BACK));
    		BlockPos posLeft = pos.offset(orientation.getFacing(EnumFace.LEFT));
    		BlockPos posBackLeft = posBack.offset(orientation.getFacing(EnumFace.LEFT));
    		worldIn.setBlockState(posBack, MechanimationBlocks.ASSEMBLY_TERMINAL_MULTIBLOCK.getDefaultState().withProperty(BlockAssemblyTerminalMultiblock.DIRECTION, orientation.getFacing(EnumFace.FRONT)));
    		worldIn.setBlockState(posLeft, MechanimationBlocks.ASSEMBLY_TERMINAL_MULTIBLOCK.getDefaultState().withProperty(BlockAssemblyTerminalMultiblock.DIRECTION, orientation.getFacing(EnumFace.RIGHT)));
    		worldIn.setBlockState(posBackLeft, MechanimationBlocks.ASSEMBLY_TERMINAL_MULTIBLOCK.getDefaultState().withProperty(BlockAssemblyTerminalMultiblock.DIRECTION, orientation.getFacing(EnumFace.RIGHT)));
    	}
    }

    @Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
	{
        TileEntity tileentity = worldIn.getTileEntity(pos);
    	if (tileentity instanceof TileEntityAssemblyTerminalBase)
    	{
    		EnumOrientation orientation = ((TileEntityAssemblyTerminalBase<?>) tileentity).getOrientation();
    		BlockPos posBack = pos.offset(orientation.getFacing(EnumFace.BACK));
    		BlockPos posLeft = pos.offset(orientation.getFacing(EnumFace.LEFT));
    		BlockPos posBackLeft = posBack.offset(orientation.getFacing(EnumFace.LEFT));
    		worldIn.setBlockToAir(posBack);
    		worldIn.setBlockToAir(posLeft);
    		worldIn.setBlockToAir(posBackLeft);
    	}
        super.breakBlock(worldIn, pos, state);
	}

	@Override
	public boolean canPlaceAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState, EnumOrientation orientation)
	{
		BlockPos posBack = pos.offset(orientation.getFacing(EnumFace.BACK));
		BlockPos posLeft = pos.offset(orientation.getFacing(EnumFace.LEFT));
		BlockPos posBackLeft = posBack.offset(orientation.getFacing(EnumFace.LEFT));
		return
				this.canPlaceAt(this, stack, player, world, pos, side) &&
				this.canPlaceAt(this, stack, player, world, posBack, side) &&
				this.canPlaceAt(this, stack, player, world, posLeft, side) &&
				this.canPlaceAt(this, stack, player, world, posBackLeft, side);
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) //TODO bottom is solid?
	{
        return BlockFaceShape.SOLID;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face, BlockPos multiBlockPos)
	{
        return BlockFaceShape.SOLID;
	}

    @Override
	public boolean isPowered(World world, BlockPos pos, IBlockState blockState) //TODO
    {
    	/*
        if (world.isBlockPowered(pos)) return true;
    	{
    		BlockPos newPos = pos.offset(EnumFacing.UP);
    		IBlockState newState = world.getBlockState(newPos);
    		if (newState.getBlock() instanceof BlockOilDistillationTowerMultiblock) //facing this
    		{
    			if (((BlockOilDistillationTowerMultiblock<?>) newState.getBlock()).isPowered(world, newPos, newState)) return true;
    		}
    	}
    	*/
    	return false;
    }
}