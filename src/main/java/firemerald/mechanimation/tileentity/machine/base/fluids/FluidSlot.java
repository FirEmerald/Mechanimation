package firemerald.mechanimation.tileentity.machine.base.fluids;

import java.util.function.Predicate;

import javax.annotation.Nullable;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.util.EnumFace;
import net.minecraft.nbt.NBTTagCompound;

public class FluidSlot<T extends IFluidMachine<T>> implements IFluidSlot
{
	public final T machine;
	public final String name;
	public final int slot;
	public int capacity, maxInsert, maxExtract;
	public final Predicate<FluidOrGasStack> canInsert, canExtract;
	protected FluidOrGasStack fluid = null;

	public FluidSlot(T machine, String name, int slot, int capacity, int maxInsert, Predicate<FluidOrGasStack> canInsert, int maxExtract, Predicate<FluidOrGasStack> canExtract)
	{
		this.machine = machine;
		this.name = name;
		this.slot = slot;
		this.capacity = capacity;
		this.maxInsert = maxInsert;
		this.canInsert = canInsert;
		this.canExtract = canExtract;
		this.maxExtract = maxExtract;
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
	public FluidOrGasStack getFluid()
	{
		return fluid;
	}

	@Override
	public void setFluid(FluidOrGasStack stack)
	{
        if (FluidOrGasStack.getAmountStatic(stack) > this.getCapacity()) stack.setAmount(this.getCapacity());
        this.machine.onFluidSlotChanged(slot, this.fluid = (stack != null && stack.getAmount() <= 0 ? null : stack));
	}

	@Override
	public boolean canInsert(FluidOrGasStack stack, EnumFace side)
	{
		return (fluid == null || FluidOrGasStack.isFluidEqualStatic(fluid, stack)) && canInsert.test(stack);
	}

	@Override
	public boolean canExtract(FluidOrGasStack stack, EnumFace side)
	{
		return (stack == null || FluidOrGasStack.isFluidEqualStatic(fluid, stack)) && canExtract.test(stack);
	}

	@Override
	public boolean isEmpty()
	{
		return fluid == null;
	}

	@Override
	public int getCapacity()
	{
		return capacity;
	}

	@Override
	public void clear()
	{
		setFluid(null);
	}

	@Override
	public FluidOrGasStack insertFluid(FluidOrGasStack stack, boolean simulate, @Nullable EnumFace side)
	{
		if (!this.canInsert(stack, side) || FluidOrGasStack.getAmountStatic(fluid) >= capacity) return stack;
		int canInsert = Math.min(maxInsert, Math.min(stack.getAmount(), capacity - FluidOrGasStack.getAmountStatic(fluid))); //min(maxReceive, toInsert, available)
		if (canInsert <= 0) return stack;
		else
		{
			FluidOrGasStack remainder = stack.copy();
			remainder.setAmount(remainder.getAmount() - canInsert);
			if (!simulate)
			{
				int count;
				if (fluid == null)
				{
					fluid = stack.copy();
					count = 0;
				}
				else count = fluid.getAmount();
				fluid.setAmount(count + canInsert);
				setFluid(fluid);
			}
			return remainder;
		}
	}

	@Override
	public FluidOrGasStack extractFluid(int amount, boolean simulate, @Nullable EnumFace side)
	{
		if (fluid == null || !this.canExtract(fluid, side)) return null;
		int canExtract = Math.min(maxExtract, Math.min(amount, FluidOrGasStack.getAmountStatic(fluid))); //min(maxExtract, toExtract, available)
		if (canExtract <= 0) return null;
		else if (!simulate)
		{
			FluidOrGasStack ret = fluid.splitStack(canExtract);
			setFluid(fluid);
			return ret;
		}
		else
		{
			FluidOrGasStack ret = fluid.copy();
			ret.setAmount(canExtract);
			return ret;
		}
	}

	@Override
	public void writeToDisk(NBTTagCompound tag) {}

	@Override
	public void readFromDisk(NBTTagCompound tag) {}

	@Override
	public void writeToShared(NBTTagCompound tag)
	{
		tag.setTag(name, FluidOrGasStack.writeToNBT(fluid, new NBTTagCompound()));
	}

	@Override
	public void readFromShared(NBTTagCompound tag)
	{
		clear();
		if (tag.hasKey(name, 10)) this.setFluid(FluidOrGasStack.readFromNBT(tag.getCompoundTag(name)));
	}

	@Override
	public void setCapacity(int capacity)
	{
		if (this.capacity != capacity)
		{
			if (FluidOrGasStack.getAmountStatic(fluid) > capacity) fluid.setAmount(capacity);
			this.capacity = capacity;
			machine.setNeedsUpdate();
		}
	}
}