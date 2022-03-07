package firemerald.mechanimation.tileentity.machine.base.energy;

import firemerald.mechanimation.util.EnumFace;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyCapWrapper<T extends IEnergyMachine<T>> implements IEnergyStorage
{
	public final T machine;
	public final EnumFace face;

	public EnergyCapWrapper(T machine, EnumFace face)
	{
		this.machine = machine;
		this.face = face;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate)
	{
		return machine.getEnergyInventory().receiveEnergy(maxReceive, simulate, face);
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate)
	{
		return machine.getEnergyInventory().extractEnergy(maxExtract, simulate, face);
	}

	@Override
	public int getEnergyStored()
	{
		return machine.getEnergyInventory().getEnergyStored(face);
	}

	@Override
	public int getMaxEnergyStored()
	{
		return machine.getEnergyInventory().getMaxEnergyStored(face);
	}

	@Override
	public boolean canExtract()
	{
		return machine.getEnergyInventory().canExtract(face);
	}

	@Override
	public boolean canReceive()
	{
		return machine.getEnergyInventory().canReceive(face);
	}
}
