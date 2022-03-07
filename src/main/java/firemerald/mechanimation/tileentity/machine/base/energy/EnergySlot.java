package firemerald.mechanimation.tileentity.machine.base.energy;

import java.util.function.Predicate;

import javax.annotation.Nullable;

import firemerald.mechanimation.util.EnumFace;
import net.minecraft.nbt.NBTTagCompound;

public class EnergySlot<T extends IEnergyMachine<T>> implements IEnergySlot
{
	public final T machine;
	public final String name;
	public final int slot;
	public final Predicate<EnumFace> canReceive, canExtract;
	public int maxEnergy, maxReceive, maxExtract;
	private int energy = 0;

	public EnergySlot(T machine, String name, int slot, int maxEnergy, int maxReceive,  Predicate<EnumFace> canReceive, int maxExtract,  Predicate<EnumFace> canExtract)
	{
		this.machine = machine;
		this.name = name;
		this.slot = slot;
		this.maxEnergy = maxEnergy;
		this.maxReceive = maxReceive;
		this.canReceive = canReceive;
		this.maxExtract = maxExtract;
		this.canExtract = canExtract;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public int getSlot()
	{
		return slot;
	}

	@Override
	public int getEnergyStored()
	{
		return energy;
	}

	@Override
	public void setEnergy(int energy)
	{
        if (energy > maxEnergy) energy = maxEnergy;
        this.machine.onEnergySlotChanged(slot, this.energy = energy);
	}

	@Override
	public boolean isEmpty()
	{
		return energy <= 0;
	}

	@Override
	public int getMaxEnergyStored()
	{
		return maxEnergy;
	}

	@Override
	public void clear()
	{
		setEnergy(0);
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate, @Nullable EnumFace side)
	{
		if (energy >= maxEnergy) return 0;
		else
		{
			int canReceive = Math.min(this.maxReceive, Math.min(maxReceive, maxEnergy - energy)); //min(maxReceive, toInsert, available)
			if (!simulate) setEnergy(energy + canReceive);
			return canReceive;
		}
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate, @Nullable EnumFace side)
	{
		if (energy <= 0) return 0;
		else
		{
			int canExtract = Math.min(this.maxExtract, Math.min(maxExtract, energy)); //min(maxExtract, toExtract, available)
			if (!simulate) setEnergy(energy + canExtract);
			return canExtract;
		}
	}

	@Override
	public boolean canExtract(EnumFace side)
	{
		return canExtract.test(side);
	}

	@Override
	public boolean canReceive(EnumFace side)
	{
		return canReceive.test(side);
	}

	@Override
	public void writeToDisk(NBTTagCompound tag) {}

	@Override
	public void readFromDisk(NBTTagCompound tag) {}

	@Override
	public void writeToShared(NBTTagCompound tag)
	{
		tag.setInteger(name, energy);
	}

	@Override
	public void readFromShared(NBTTagCompound tag)
	{
		clear();
		if (tag.hasKey(name, 99)) this.setEnergy(tag.getInteger(name));
	}

	@Override
	public void setMaxEnergyStored(int maxEnergy)
	{
		if (this.maxEnergy != maxEnergy)
		{
			if (this.energy > maxEnergy) this.energy = maxEnergy;
			this.maxEnergy = maxEnergy;
			machine.setNeedsUpdate();
		}
	}
}