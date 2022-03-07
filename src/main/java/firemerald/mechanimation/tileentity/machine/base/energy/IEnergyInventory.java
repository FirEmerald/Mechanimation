package firemerald.mechanimation.tileentity.machine.base.energy;

import javax.annotation.Nullable;

import firemerald.mechanimation.tileentity.machine.base.ISaved;
import firemerald.mechanimation.util.EnumFace;
import net.minecraftforge.energy.IEnergyStorage;

public interface IEnergyInventory<T extends IEnergyMachine<T>> extends IEnergyStorage, ISaved
{
	public int numSlots();

	public IEnergySlot[] getEnergySlots();

	public IEnergySlot getEnergySlot(int slot);

	public int[] getSlots(EnumFace side);

	public IEnergySlot[] getEnergySlots(EnumFace side);

	public boolean isEmpty();

	public default int receiveEnergy(int maxReceive, boolean simulate, @Nullable EnumFace face)
	{
		int receive = maxReceive;
		int received = 0;
		IEnergySlot[] slots = face == null ? getEnergySlots() : getEnergySlots(face);
		for (IEnergySlot slot : slots)
		{
			int amount = slot.receiveEnergy(receive, simulate, face);
			receive -= amount;
			received += amount;
		}
		return received;
	}

	public default int extractEnergy(int maxExtract, boolean simulate, @Nullable EnumFace face)
	{
		int extract = maxExtract;
		int extracted = 0;
		IEnergySlot[] slots = face == null ? getEnergySlots() : getEnergySlots(face);
		for (IEnergySlot slot : slots)
		{
			int amount = slot.extractEnergy(extract, simulate, face);
			extract -= amount;
			extracted += amount;
		}
		return extracted;
	}

	public default int getEnergyStored(@Nullable EnumFace face)
	{
		int energy = 0;
		IEnergySlot[] slots = face == null ? getEnergySlots() : getEnergySlots(face);
		for (IEnergySlot slot : slots) energy += slot.getEnergyStored();
		return energy;
	}

	public default int getMaxEnergyStored(@Nullable EnumFace face)
	{
		int maxEnergy = 0;
		IEnergySlot[] slots = face == null ? getEnergySlots() : getEnergySlots(face);
		for (IEnergySlot slot : slots) maxEnergy += slot.getMaxEnergyStored();
		return maxEnergy;
	}

	public default boolean canExtract(@Nullable EnumFace face)
	{
		IEnergySlot[] slots = face == null ? getEnergySlots() : getEnergySlots(face);
		for (IEnergySlot slot : slots) if (slot.canExtract(face)) return true;
		return false;
	}

	public default boolean canReceive(@Nullable EnumFace face)
	{
		IEnergySlot[] slots = face == null ? getEnergySlots() : getEnergySlots(face);
		for (IEnergySlot slot : slots) if (slot.canReceive(face)) return true;
		return false;
	}

	@Override
	public default int receiveEnergy(int maxReceive, boolean simulate)
	{
		return receiveEnergy(maxReceive, simulate, null);
	}

	@Override
	public default int extractEnergy(int maxExtract, boolean simulate)
	{
		return extractEnergy(maxExtract, simulate, null);
	}

	@Override
	public default int getEnergyStored()
	{
		return getEnergyStored(null);
	}

	@Override
	public default int getMaxEnergyStored()
	{
		return getEnergyStored(null);
	}

	@Override
	public default boolean canExtract()
	{
		return canExtract(null);
	}

	@Override
	public default boolean canReceive()
	{
		return canReceive(null);
	}
}