package firemerald.mechanimation.tileentity.machine.base.energy;

import javax.annotation.Nullable;

import firemerald.mechanimation.tileentity.machine.base.ISlot;
import firemerald.mechanimation.util.EnumFace;
import net.minecraftforge.energy.IEnergyStorage;

public interface IEnergySlot extends IEnergyStorage, ISlot
{
	public int receiveEnergy(int maxReceive, boolean simulate, @Nullable EnumFace side);

	public int extractEnergy(int maxExtract, boolean simulate, @Nullable EnumFace side);

    public boolean canExtract(@Nullable EnumFace side);

    public boolean canReceive(@Nullable EnumFace side);

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
    public default boolean canExtract()
    {
    	return canExtract(null);
    }

    @Override
    public default boolean canReceive()
    {
    	return canReceive(null);
    }

	public boolean isEmpty();

	public void clear();

	public void setMaxEnergyStored(int maxEnergy);

	public void setEnergy(int energy);

	public default void add(int add)
	{
		setEnergy(getEnergyStored() + add);
	}

	public default void remove(int remove)
	{
		setEnergy(getEnergyStored() - remove);
	}
}