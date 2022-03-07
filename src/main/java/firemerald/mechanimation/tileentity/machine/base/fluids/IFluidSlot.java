package firemerald.mechanimation.tileentity.machine.base.fluids;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.tileentity.IFluidTankInfo;
import firemerald.mechanimation.tileentity.machine.base.ISlot;
import firemerald.mechanimation.util.EnumFace;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import net.minecraftforge.fluids.FluidStack;

public interface IFluidSlot extends IFluidTankInfo, ISlot
{
    boolean isEmpty();

    FluidOrGasStack getFluid();

    public default FluidStack getFluidStack()
    {
    	return FluidOrGasStack.getFluidStackStatic(getFluid());
    }

    public default GasStack getGasStack()
    {
    	return FluidOrGasStack.getGasStackStatic(getFluid());
    }

    boolean canInsert(@Nullable FluidOrGasStack stack, @Nullable EnumFace side);

    boolean canExtract(@Nullable FluidOrGasStack stack, @Nullable EnumFace side);

    @Nonnull
    FluidOrGasStack insertFluid(@Nonnull FluidOrGasStack stack, boolean simulate, @Nullable EnumFace side);

    @Nonnull
    FluidOrGasStack extractFluid(int amount, boolean simulate, @Nullable EnumFace side);

    void clear();

    @Override
    @Nullable
    public default FluidOrGasStack getFluidOrGas()
    {
    	return getFluid();
    }

    @Override
    public default boolean canFill(@Nullable FluidOrGasStack type)
    {
    	return canInsert(type, null);
    }

    @Override
    public default boolean canDrain(@Nullable FluidOrGasStack type)
    {
    	return canExtract(type, null);
    }

    public default boolean isFluid()
    {
        FluidOrGasStack fluid = getFluid();
    	return fluid != null && fluid.isFluid();
    }

    public default boolean isGas()
    {
        FluidOrGasStack fluid = getFluid();
    	return fluid != null && fluid.isGas();
    }

	@Override
	public int getCapacity();

	public void setCapacity(int capacity);

    public default int fill(FluidStack resource, boolean doFill, @Nullable EnumFace side)
    {
    	FluidOrGasStack remaining = insertFluid(FluidOrGasStack.forFluid(resource), !doFill, side);
    	return resource.amount - FluidOrGasStack.getAmountStatic(remaining);
    }

    @Nullable
    public default FluidStack drain(FluidStack resource, boolean doDrain, @Nullable EnumFace side)
    {
    	if (!isFluid()  || !canDrain(FluidOrGasStack.forFluid(resource))) return null;
    	else return FluidOrGasStack.getFluidStackStatic(extractFluid(resource.amount, !doDrain, side));
    }

    @Nullable
    public default FluidStack drain(int maxDrain, boolean doDrain, @Nullable EnumFace side)
    {
    	if (!isFluid()) return null;
    	else return FluidOrGasStack.getFluidStackStatic(extractFluid(maxDrain, !doDrain, side));
    }

    public default int receiveGas(GasStack stack, boolean doTransfer, @Nullable EnumFace side)
    {
    	FluidOrGasStack remaining = insertFluid(FluidOrGasStack.forGas(stack), !doTransfer, side);
    	return stack.amount - FluidOrGasStack.getAmountStatic(remaining);
    }

    public default GasStack drawGas(int amount, boolean doTransfer, @Nullable EnumFace side)
    {
    	if (!isGas()) return null;
    	else return FluidOrGasStack.getGasStackStatic(extractFluid(amount, !doTransfer, side));
    }

    public default boolean canReceiveGas(Gas type, @Nullable EnumFace side)
    {
    	return canInsert(FluidOrGasStack.forGas(type), side);
    }

    public default boolean canDrawGas(Gas type, @Nullable EnumFace side)
    {
    	return canExtract(FluidOrGasStack.forGas(type), side);
    }

    public void setFluid(FluidOrGasStack stack);

    public default void add(FluidOrGasStack add)
    {
    	FluidOrGasStack has = getFluid();
    	if (has == null) has = add.copy();
    	else has.changeAmount(add.getAmount());
    	setFluid(has);
    }

    public default void remove(int remove)
    {
    	FluidOrGasStack has = getFluid();
    	if (has != null)
    	{
    		has.changeAmount(-remove);
    		if (has.getAmount() <= 0) has = null;
    	}
    	setFluid(has);
    }
}