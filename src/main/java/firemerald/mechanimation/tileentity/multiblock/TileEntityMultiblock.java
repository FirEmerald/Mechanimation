package firemerald.mechanimation.tileentity.multiblock;

import firemerald.mechanimation.blocks.BlockBasicMultiblock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class TileEntityMultiblock extends TileEntity
{
	public TileEntity getParent()
	{
		IBlockState blockState = world.getBlockState(pos);
		if (blockState.getBlock() instanceof BlockBasicMultiblock)
		{
			BlockPos parentPos = ((BlockBasicMultiblock<?>) blockState.getBlock()).getParentPos(world, pos, blockState);
			if (parentPos != null) return world.getTileEntity(parentPos);
			else return null;
		}
		else return null;
	}

    @Override
    @javax.annotation.Nullable
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @javax.annotation.Nullable net.minecraft.util.EnumFacing facing)
    {
    	TileEntity parent = getParent();
    	if (parent != null)
    	{
    		T t = parent.getCapability(capability, null);
    		if (t != null) return t;
    	}
        return super.getCapability(capability, facing);
    }
}