package firemerald.mechanimation.tileentity.machine.base.implementation.actual;

import java.util.function.Function;

import javax.annotation.Nullable;

import firemerald.mechanimation.api.capabilities.Capabilities;
import firemerald.mechanimation.tileentity.machine.base.fluids.FluidCapWrapper;
import firemerald.mechanimation.tileentity.machine.base.fluids.IFluidGuiMachine;
import firemerald.mechanimation.tileentity.machine.base.fluids.IFluidInventory;
import firemerald.mechanimation.tileentity.machine.base.items.IItemInventory;
import firemerald.mechanimation.util.EnumFace;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public abstract class TileEntityFluidItemMachineBase<T extends TileEntityFluidItemMachineBase<T, R>, R> extends TileEntityItemMachineBase<T, R> implements IFluidGuiMachine<T>
{
	@SuppressWarnings("unchecked")
	private final IFluidHandler[] facedFluidHandlers = new IFluidHandler[] {
			new FluidCapWrapper<>((T) this, EnumFace.values()[0]),
			new FluidCapWrapper<>((T) this, EnumFace.values()[1]),
			new FluidCapWrapper<>((T) this, EnumFace.values()[2]),
			new FluidCapWrapper<>((T) this, EnumFace.values()[3]),
			new FluidCapWrapper<>((T) this, EnumFace.values()[4]),
			new FluidCapWrapper<>((T) this, EnumFace.values()[5])
	};
	protected final IFluidInventory<T> fluidInventory;

	@SuppressWarnings("unchecked")
	public TileEntityFluidItemMachineBase(Function<T, IItemInventory<T>> itemInventory, Function<T, IFluidInventory<T>> fluidInventory)
	{
		super(itemInventory);
		this.fluidInventory = fluidInventory.apply((T) this);
	}

	@Override
	public IFluidInventory<T> getFluidInventory()
	{
		return fluidInventory;
	}

	@Override
	public void writeToShared(NBTTagCompound tag)
	{
		super.writeToShared(tag);
		getFluidInventory().writeToShared(tag);
	}

	@Override
	public void readFromShared(NBTTagCompound tag)
	{
		super.readFromShared(tag);
		getFluidInventory().readFromShared(tag);
	}

	@Override
	public void readFromDisk(NBTTagCompound tag)
	{
		super.readFromDisk(tag);
		getFluidInventory().readFromDisk(tag);
	}

	@Override
	public void writeToDisk(NBTTagCompound tag)
	{
		super.writeToDisk(tag);
		getFluidInventory().writeToDisk(tag);
	}

    @Override
    public boolean hasCapabilityLocal(Capability<?> capability, @Nullable EnumFacing facing)
    {
    	if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || capability == Capabilities.gasHandler)
    	{
    		return getFluidInventory().getSlots(getFace(facing)).length > 0;
    	}
    	else return super.hasCapabilityLocal(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
	public <S> S getCapabilityLocal(Capability<S> capability, @Nullable EnumFacing facing)
    {
    	if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
    	{
    		if (facing == null) return (S) this.getFluidInventory();
    		else return (S) facedFluidHandlers[getFace(facing).ordinal()];
    	}
    	else if (capability == Capabilities.gasHandler) return (S) this;
    	else return super.getCapabilityLocal(capability, facing);
    }
}