package firemerald.mechanimation.util.flux;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyStorageWrapper implements IEnergyStorage
{
	public final IFluxHandler handler;
	public final EnumFacing side;

	public EnergyStorageWrapper(IFluxHandler handler, EnumFacing side)
	{
		this.handler = handler;
		this.side = side;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate)
	{
		return handler instanceof IFluxReceiver ? ((IFluxReceiver) handler).receiveEnergy(side, maxReceive, simulate) : 0;
	}
	@Override
	public int extractEnergy(int maxExtract, boolean simulate)
	{
		return handler instanceof IFluxProvider ? ((IFluxProvider) handler).extractEnergy(side, maxExtract, simulate) : 0;
	}
	@Override
	public int getEnergyStored()
	{
		return handler.getEnergyStored(side);
	}
	@Override
	public int getMaxEnergyStored()
	{
		return handler.getMaxEnergyStored(side);
	}

	@Override
	public boolean canExtract()
	{
		return handler instanceof IFluxProvider;
	}

	@Override
	public boolean canReceive()
	{
		return handler instanceof IFluxReceiver;
	}
}