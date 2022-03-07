package firemerald.mechanimation.api.properties;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public abstract class PropertiesBase<T> implements Comparable<PropertiesBase<T>>
{
	public final int priority;

	public PropertiesBase(int priority)
	{
		this.priority = priority;
	}

	public abstract T getProperty(IBlockAccess world, BlockPos pos, IBlockState blockState, TileEntity tile);

	@Override
	public int compareTo(PropertiesBase<T> o)
	{
		return o.priority < priority ? -1 : o.priority > priority ? 1 : 0;
	}
}