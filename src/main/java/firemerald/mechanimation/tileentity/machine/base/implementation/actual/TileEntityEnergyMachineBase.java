package firemerald.mechanimation.tileentity.machine.base.implementation.actual;

import java.util.function.Function;

import javax.annotation.Nullable;

import firemerald.mechanimation.tileentity.machine.base.energy.EnergyCapWrapper;
import firemerald.mechanimation.tileentity.machine.base.energy.IEnergyGuiMachine;
import firemerald.mechanimation.tileentity.machine.base.energy.IEnergyInventory;
import firemerald.mechanimation.util.EnumFace;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public abstract class TileEntityEnergyMachineBase<T extends TileEntityEnergyMachineBase<T, R>, R> extends TileEntityMachineBase<T, R> implements IEnergyGuiMachine<T>
{
	@SuppressWarnings("unchecked")
	private final IEnergyStorage[] facedEnergyHandlers = new IEnergyStorage[] {
			new EnergyCapWrapper<>((T) this, EnumFace.values()[0]),
			new EnergyCapWrapper<>((T) this, EnumFace.values()[1]),
			new EnergyCapWrapper<>((T) this, EnumFace.values()[2]),
			new EnergyCapWrapper<>((T) this, EnumFace.values()[3]),
			new EnergyCapWrapper<>((T) this, EnumFace.values()[4]),
			new EnergyCapWrapper<>((T) this, EnumFace.values()[5])
	};
	protected final IEnergyInventory<T> energyInventory;

	@SuppressWarnings("unchecked")
	public TileEntityEnergyMachineBase(Function<T, IEnergyInventory<T>> energyInventory)
	{
		this.energyInventory = energyInventory.apply((T) this);
	}

	@Override
	public IEnergyInventory<T> getEnergyInventory()
	{
		return energyInventory;
	}

	@Override
	public void writeToShared(NBTTagCompound tag)
	{
		super.writeToShared(tag);
		getEnergyInventory().writeToShared(tag);
	}

	@Override
	public void readFromShared(NBTTagCompound tag)
	{
		super.readFromShared(tag);
		getEnergyInventory().readFromShared(tag);
	}

	@Override
	public void readFromDisk(NBTTagCompound tag)
	{
		super.readFromDisk(tag);
		getEnergyInventory().readFromDisk(tag);
	}

	@Override
	public void writeToDisk(NBTTagCompound tag)
	{
		super.writeToDisk(tag);
		getEnergyInventory().writeToDisk(tag);
	}

    @Override
	public boolean hasCapabilityLocal(Capability<?> capability, @Nullable EnumFacing facing)
    {
    	if (capability == CapabilityEnergy.ENERGY)
    	{
    		return getEnergyInventory().getSlots(getFace(facing)).length > 0;
    	}
    	else return super.hasCapabilityLocal(capability, facing);
    }

    @Override
	@SuppressWarnings("unchecked")
	public <S> S getCapabilityLocal(Capability<S> capability, @Nullable EnumFacing facing)
    {
    	if (capability == CapabilityEnergy.ENERGY)
    	{
    		if (facing == null) return (S) this.getEnergyInventory();
    		else return (S) facedEnergyHandlers[getFace(facing).ordinal()];
    	}
    	else return super.getCapabilityLocal(capability, facing);
    }
}