package firemerald.mechanimation.tileentity.machine.base.fluids;

import javax.annotation.Nullable;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.tileentity.machine.base.ISaved;
import firemerald.mechanimation.util.EnumFace;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public interface IFluidInventory<T extends IFluidMachine<T>> extends IFluidHandler, ISaved
{
	public int numSlots();

	public IFluidSlot[] getFluidSlots();

	public IFluidSlot getFluidSlot(int slot);

	public int[] getSlots(EnumFace side);

	public IFluidSlot[] getFluidSlots(EnumFace side);

	public boolean isEmpty();

	public FluidOrGasStack getFluidOrGas(int slot);

	public default FluidStack getFluidStack(int slot)
	{
		return FluidOrGasStack.getFluidStackStatic(getFluidOrGas(slot));
	}

	public default GasStack getGasStack(int slot)
	{
		return FluidOrGasStack.getGasStackStatic(getFluidOrGas(slot));
	}

	@Override
	public default IFluidTankProperties[] getTankProperties()
	{
		return getFluidSlots();
	}

	@Override
	public default int fill(FluidStack resource, boolean doFill)
	{
		return fill(resource, doFill, null);
	}

	@Override
    @Nullable
    public default FluidStack drain(FluidStack resource, boolean doDrain)
    {
		return drain(resource, doDrain, null);
    }

	@Override
    @Nullable
    public default FluidStack drain(int maxDrain, boolean doDrain)
    {
		return drain(maxDrain, doDrain, null);
    }

	public default boolean canInsertFluidOrGas(int slot, FluidOrGasStack stack, @Nullable EnumFace face)
	{
    	IFluidSlot fluidSlot = getFluidSlot(slot);
    	return fluidSlot == null ? false : fluidSlot.canInsert(stack, face);
	}

	public default boolean canExtractFluidOrGas(int slot, FluidOrGasStack stack, @Nullable EnumFace face)
	{

    	IFluidSlot fluidSlot = getFluidSlot(slot);
    	return fluidSlot == null ? false : fluidSlot.canExtract(stack, face);
	}

	public default FluidOrGasStack insertFluidOrGas(int slot, FluidOrGasStack stack, boolean simulate, @Nullable EnumFace face)
	{
    	IFluidSlot fluidSlot = getFluidSlot(slot);
    	return fluidSlot == null ? null : fluidSlot.insertFluid(stack, simulate, face);
	}

	public default FluidOrGasStack extractFluidOrGas(int slot, int amount, boolean simulate, @Nullable EnumFace face)
	{
    	IFluidSlot fluidSlot = getFluidSlot(slot);
    	return fluidSlot == null ? null : fluidSlot.extractFluid(amount, simulate, face);
	}

    public default int receiveGas(int slot, GasStack receive, boolean doTransfer, @Nullable EnumFace face)
    {
    	IFluidSlot fluidSlot = getFluidSlot(slot);
    	return fluidSlot == null ? 0 : fluidSlot.receiveGas(receive, doTransfer, face);
    }

    public default GasStack drawGas(int slot, int amount, boolean doTransfer, @Nullable EnumFace face)
    {
    	IFluidSlot fluidSlot = getFluidSlot(slot);
    	return fluidSlot == null ? null : fluidSlot.drawGas(amount, doTransfer, face);
    }

    public default boolean canReceiveGas(int slot, Gas type, @Nullable EnumFace face)
    {
    	IFluidSlot fluidSlot = getFluidSlot(slot);
    	return fluidSlot == null ? false : fluidSlot.canReceiveGas(type, face);
    }

    public default boolean canDrawGas(int slot, Gas type, @Nullable EnumFace face)
    {
    	IFluidSlot fluidSlot = getFluidSlot(slot);
    	return fluidSlot == null ? false : fluidSlot.canDrawGas(type, face);
    }

    public default int fill(int slot, FluidStack resource, boolean doFill, @Nullable EnumFace face)
    {
    	IFluidSlot fluidSlot = getFluidSlot(slot);
    	return fluidSlot == null ? 0 : fluidSlot.fill(resource, doFill, face);
    }

    @Nullable
    public default FluidStack drain(int slot, FluidStack resource, boolean doDrain, @Nullable EnumFace face)
    {
    	IFluidSlot fluidSlot = getFluidSlot(slot);
    	return fluidSlot == null ? null : fluidSlot.drain(resource, doDrain, face);
    }

    @Nullable
    public default FluidStack drain(int slot, int maxDrain, boolean doDrain, @Nullable EnumFace face)
    {
    	IFluidSlot fluidSlot = getFluidSlot(slot);
    	return fluidSlot == null ? null : fluidSlot.drain(maxDrain, doDrain, face);
    }

	public default int fill(FluidStack resource, boolean doFill, @Nullable EnumFace face)
	{
		FluidStack fill = resource.copy();
		int filled = 0;
		IFluidSlot[] slots = face == null ? getFluidSlots() : getFluidSlots(face);
		for (IFluidSlot slot : slots)
		{
			int amount = slot.fill(fill, doFill, face);
			fill.amount -= amount;
			filled += amount;
		}
		return filled;
	}

	public default FluidStack drain(FluidStack resource, boolean doDrain, @Nullable EnumFace face)
	{
		FluidStack drain = resource.copy();
		FluidStack drained = null;
		IFluidSlot[] slots = face == null ? getFluidSlots() : getFluidSlots(face);
		for (IFluidSlot slot : slots)
		{
			FluidStack recieve = slot.drain(drain, doDrain, face);
			if (recieve != null)
			{
				if (drained == null) drained = recieve;
				else drained.amount += recieve.amount;
				if ((drain.amount -= recieve.amount) <= 0) break;
			}
		}
		return drained;
	}

	public default FluidStack drain(int maxDrain, boolean doDrain, @Nullable EnumFace face)
	{
		int drain = maxDrain;
		FluidStack drained = null;
		IFluidSlot[] slots = face == null ? getFluidSlots() : getFluidSlots(face);
		for (IFluidSlot slot : slots)
		{
			FluidStack recieve = slot.drain(drain, doDrain, face);
			if (recieve != null)
			{
				if (drained == null) drained = recieve;
				else drained.amount += recieve.amount;
				if ((drain -= recieve.amount) <= 0) break;
			}
		}
		return drained;
	}

    public default int receiveGas(GasStack receive, boolean doTransfer, @Nullable EnumFace face)
    {
    	GasStack fill = receive.copy();
		int filled = 0;
		IFluidSlot[] slots = face == null ? getFluidSlots() : getFluidSlots(face);
		for (IFluidSlot slot : slots)
		{
			int amount = slot.receiveGas(fill, doTransfer, face);
			fill.amount -= amount;
			filled += amount;
		}
		return filled;
    }

    public default GasStack drawGas(int amount, boolean doTransfer, @Nullable EnumFace face)
    {
		int drain = amount;
		GasStack drained = null;
		IFluidSlot[] slots = face == null ? getFluidSlots() : getFluidSlots(face);
		for (IFluidSlot slot : slots)
		{
			GasStack recieve = slot.drawGas(drain, doTransfer, face);
			if (recieve != null)
			{
				if (drained == null) drained = recieve;
				else drained.amount += recieve.amount;
				if ((amount -= recieve.amount) <= 0) break;
			}
		}
		return drained;
    }

    public default boolean canReceiveGas(Gas type, @Nullable EnumFace face)
    {
		IFluidSlot[] slots = face == null ? getFluidSlots() : getFluidSlots(face);
		for (IFluidSlot slot : slots) if (slot.canReceiveGas(type, face)) return true;
		return false;
    }

    public default boolean canDrawGas(Gas type, @Nullable EnumFace face)
    {
		IFluidSlot[] slots = face == null ? getFluidSlots() : getFluidSlots(face);
		for (IFluidSlot slot : slots) if (slot.canDrawGas(type, face)) return true;
		return false;
    }

    public default IFluidTankProperties[] getTankProperties(EnumFace face)
    {
    	return getFluidSlots(face);
    }
}