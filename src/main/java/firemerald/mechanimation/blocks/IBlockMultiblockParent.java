package firemerald.mechanimation.blocks;

import firemerald.mechanimation.util.EnumOrientation;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public interface IBlockMultiblockParent
{
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face, BlockPos multiBlockPos);

    public default boolean isPowered(World world, BlockPos pos, IBlockState blockState)
    {
        if (world.isBlockPowered(pos)) return true;
    	for (EnumFacing dir : EnumFacing.VALUES)
    	{
    		BlockPos newPos = pos.offset(dir);
    		IBlockState newState = world.getBlockState(newPos);
    		if (newState.getBlock() instanceof BlockBasicMultiblock && newState.getValue(BlockBasicMultiblock.DIRECTION) == dir.getOpposite()) //facing this
    		{
    			if (((BlockBasicMultiblock<?>) newState.getBlock()).isPowered(world, newPos, newState)) return true;
    		}
    	}
    	return false;
    }

    public boolean canPlaceAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState, EnumOrientation orientation);

    public default boolean canPlaceAt(Block block, ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side)
    {
    	return player.canPlayerEdit(pos, side, stack) && world.mayPlace(block, pos, false, side, player);
    }
}