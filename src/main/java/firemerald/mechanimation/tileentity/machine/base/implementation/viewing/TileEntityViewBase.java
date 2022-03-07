package firemerald.mechanimation.tileentity.machine.base.implementation.viewing;

import javax.annotation.Nullable;

import firemerald.mechanimation.tileentity.machine.base.IMachine;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public abstract class TileEntityViewBase<T> extends TileEntity implements IMachine
{
	private TileEntity viewedEntity = null;
	protected T viewedMachine = null;

	@Override
	public World getTheWorld()
	{
		return this.getWorld();
	}

	@Override
	public BlockPos getThePos()
	{
		return this.getPos();
	}

	@Override
	public void setNeedsUpdate() {}

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
		if (hasCapabilityLocal(capability, facing)) return true;
		else return super.hasCapability(capability, facing);
    }

    public boolean hasCapabilityLocal(Capability<?> capability, @Nullable EnumFacing facing)
    {
    	return false;
    }

	@Override
    @Nullable
    public <S> S getCapability(Capability<S> capability, @Nullable EnumFacing facing)
    {
		S has = getCapabilityLocal(capability, facing);
		if (has != null) return has;
		else return super.getCapability(capability, facing);
    }

	public <S> S getCapabilityLocal(Capability<S> capability, @Nullable EnumFacing facing)
    {
    	return null;
    }

	public abstract BlockPos getViewedPos();

	public abstract void onViewChanged();

	public abstract Class<? super T> getViewClass();

	@SuppressWarnings("unchecked")
	public void updateViewed()
	{
		TileEntity at;
		if (this.world != null)
		{
			BlockPos viewed = getViewedPos();
			at = world.getTileEntity(viewed);
		}
		else at = null;
		if (at != viewedEntity)
		{
			viewedEntity = at;
			if (at != null && getViewClass().isAssignableFrom(at.getClass()))
			{
				viewedMachine = (T) at;
				onViewChanged();
			}
			else if (viewedMachine != null) //change to null
			{
				viewedMachine = null;
				onViewChanged();
			}
		}
	}
}