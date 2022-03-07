package firemerald.mechanimation.util.flux;

import net.minecraft.nbt.NBTTagCompound;

public class FluxStorage implements IFluxStorage
{
	protected int energy;
	protected int capacity;
	protected int maxReceive;
	protected int maxExtract;

	public FluxStorage(int capacity)
	{
		this(capacity, capacity, capacity);
	}

	public FluxStorage(int capacity, int maxTransfer)
	{
		this(capacity, maxTransfer, maxTransfer);
	}

	public FluxStorage(int capacity, int maxReceive, int maxExtract)
	{
		this.capacity = capacity;
		this.maxReceive = maxReceive;
		this.maxExtract = maxExtract;
	}

	public FluxStorage readFromNBT(NBTTagCompound nbt)
	{
		this.energy = nbt.getInteger("Energy");
		if (energy > capacity) energy = capacity;
		return this;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		if (energy < 0) energy = 0;
		nbt.setInteger("Energy", energy);
		return nbt;
	}

	public FluxStorage setCapacity(int capacity)
	{
		this.capacity = capacity;
		if (energy > capacity) energy = capacity;
		return this;
	}

	public FluxStorage setMaxTransfer(int maxTransfer)
	{
		setMaxReceive(maxTransfer);
		setMaxExtract(maxTransfer);
		return this;
	}

	public FluxStorage setMaxReceive(int maxReceive)
	{
		this.maxReceive = maxReceive;
		return this;
	}

	public FluxStorage setMaxExtract(int maxExtract)
	{
		this.maxExtract = maxExtract;
		return this;
	}

	public int getMaxReceive()
	{
		return maxReceive;
	}

	public int getMaxExtract()
	{
		return maxExtract;
	}

	/**
	 * This function is included to allow for server to client sync. Do not call this externally to the containing Tile Entity, as not all IEnergyHandlers are guaranteed to have it.
	 */
	public void setEnergyStored(int energy)
	{
		this.energy = energy;
		if (this.energy > capacity) this.energy = capacity;
		else if (this.energy < 0) this.energy = 0;
	}

	/**
	 * This function is included to allow the containing tile to directly and efficiently modify the energy contained in the EnergyStorage. Do not rely on this externally, as not all IEnergyHandlers are guaranteed to have it.
	 */
	public void modifyEnergyStored(int energy)
	{
		this.energy += energy;
		if (this.energy > capacity) this.energy = capacity;
		else if (this.energy < 0) this.energy = 0;
	}

	/* IEnergyStorage */
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate)
	{
		int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
		if (!simulate) energy += energyReceived;
		return energyReceived;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate)
	{
		int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
		if (!simulate) energy -= energyExtracted;
		return energyExtracted;
	}

	@Override
	public int getEnergyStored()
	{
		return energy;
	}

	@Override
	public int getMaxEnergyStored()
	{
		return capacity;
	}
}