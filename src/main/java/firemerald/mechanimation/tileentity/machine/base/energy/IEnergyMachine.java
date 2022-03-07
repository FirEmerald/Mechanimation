package firemerald.mechanimation.tileentity.machine.base.energy;

import firemerald.mechanimation.tileentity.machine.base.IMachine;
import firemerald.mechanimation.util.EnumFace;
import net.minecraft.util.EnumFacing;

public interface IEnergyMachine<T extends IEnergyMachine<T>> extends IMachine
{
	public IEnergyInventory<T> getEnergyInventory();

	public default void onEnergySlotChanged(int slot, int newEnergy)
	{
		this.setNeedsUpdate();
	}

	public default int getEnergyStored(EnumFacing from)
	{
		return this.getEnergyInventory().getEnergyStored(getFace(from));
	}

	public default int getMaxEnergyStored(EnumFacing from)
	{
		return this.getEnergyInventory().getMaxEnergyStored(getFace(from));
	}

	public default boolean canConnectEnergy(EnumFacing from)
	{
		EnumFace face = getFace(from);
		IEnergyInventory<T> inv = getEnergyInventory();
		return inv.canReceive(face) || inv.canExtract(face);
	}

	public default int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate)
	{
		return this.getEnergyInventory().receiveEnergy(maxReceive, simulate, getFace(from));
	}

	public default int extractEnergy(EnumFacing from, int maxExtract, boolean simulate)
	{
		return this.getEnergyInventory().extractEnergy(maxExtract, simulate, getFace(from));
	}
}