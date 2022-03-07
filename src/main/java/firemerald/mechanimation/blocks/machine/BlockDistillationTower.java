package firemerald.mechanimation.blocks.machine;

import java.util.List;
import java.util.function.Supplier;

import firemerald.api.core.client.Translator;
import firemerald.mechanimation.blocks.IBlockMultiblockParent;
import firemerald.mechanimation.blocks.machine.BlockDistillationTower.EnumVariant;
import firemerald.mechanimation.init.MechanimationBlocks;
import firemerald.mechanimation.tileentity.machine.distillation_tower.TileEntityDistillationTower;
import firemerald.mechanimation.tileentity.machine.distillation_tower.TileEntityDistillationTowerAdvanced;
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

public class BlockDistillationTower extends BlockVerticalMachineBase<TileEntityDistillationTower<?>, EnumVariant> implements IBlockMultiblockParent
{
	public static enum EnumVariant implements IMachineVariant<TileEntityDistillationTower<?>>
	{
		ADVANCED("advanced", MapColor.IRON, 5f, 10f, TileEntityDistillationTowerAdvanced::new);

        private final String name;
        private final MapColor color;
        private final float hardness;
        private final float resistance;
        private final Supplier<TileEntityDistillationTower<?>> tile;

        private EnumVariant(String name, MapColor color, float hardness, Supplier<TileEntityDistillationTower<?>> tile)
        {
        	this(name, color, hardness, hardness * 5, tile);
        }

        private EnumVariant(String name, MapColor color, float hardness, float resistance, Supplier<TileEntityDistillationTower<?>> tile)
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
		public TileEntityDistillationTower<?> newTile()
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

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag)
	{
		super.addInformation(stack, world, list, flag);
		list.add(Translator.translate("lore.mechanimation.distillation_tower"));
	}

	public static final AxisAlignedBB BOX = new AxisAlignedBB(0, 0, 0, 1, 3, 1);

    @Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
    	return BOX;
    }

    @Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
    	TileEntity tile = worldIn.getTileEntity(pos);
    	if (tile instanceof TileEntityDistillationTower)
    	{
    		worldIn.setBlockState(pos.add(0, 1, 0), MechanimationBlocks.DISTILLATION_TOWER_MULTIBLOCK.getDefaultState().withProperty(BlockDistillationTowerMultiblock.IS_TOP, false));
    		worldIn.setBlockState(pos.add(0, 2, 0), MechanimationBlocks.DISTILLATION_TOWER_MULTIBLOCK.getDefaultState().withProperty(BlockDistillationTowerMultiblock.IS_TOP, true));
    	}
    }

    @Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
	{
        TileEntity tileentity = worldIn.getTileEntity(pos);
    	if (tileentity instanceof TileEntityDistillationTower)
    	{
    		worldIn.setBlockToAir(pos.add(0, 1, 0));
    		worldIn.setBlockToAir(pos.add(0, 2, 0));
    	}
        super.breakBlock(worldIn, pos, state);
	}

	@Override
	public boolean canPlaceAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState, EnumOrientation orientation)
	{
		BlockPos pos1 = pos.offset(orientation.up);
		return this.canPlaceAt(this, stack, player, world, pos, side) && this.canPlaceAt(this, stack, player, world, pos1, side) && this.canPlaceAt(this, stack, player, world, pos1.offset(orientation.up), side);
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
	{
        if (face == EnumFacing.DOWN) return BlockFaceShape.SOLID;
        else return BlockFaceShape.UNDEFINED;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face, BlockPos multiBlockPos)
	{
		return BlockFaceShape.UNDEFINED;
	}

    @Override
	public boolean isPowered(World world, BlockPos pos, IBlockState blockState)
    {
        if (world.isBlockPowered(pos)) return true;
    	{
    		BlockPos newPos = pos.offset(EnumFacing.UP);
    		IBlockState newState = world.getBlockState(newPos);
    		if (newState.getBlock() instanceof BlockDistillationTowerMultiblock) //facing this
    		{
    			if (((BlockDistillationTowerMultiblock<?>) newState.getBlock()).isPowered(world, newPos, newState)) return true;
    		}
    	}
    	return false;
    }

	@Override
	public EnumVariant getDefaultVariant()
	{
		return EnumVariant.ADVANCED;
	}
}