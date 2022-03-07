package firemerald.mechanimation.api.tileentity;

import java.util.function.Predicate;

import javax.annotation.Nullable;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.IGasHandler;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public interface IFluidOrGasStackHandler extends IGasHandler, IFluidHandler
{
	@Nullable
	public boolean isValidSide(EnumFacing side);

	@Override
	public IFluidTankInfo[] getTankInfo();

	@Override
	public default IFluidTankInfo[] getTankProperties()
	{
		return getTankInfo();
	}

	public FluidOrGasStack receive(FluidOrGasStack resource, boolean simulate);

	public FluidOrGasStack extract(Predicate<FluidOrGasStack> type, int maxExtract, boolean simulate);

	public boolean canExtract(@Nullable FluidOrGasStack type);

	public boolean canReceive(@Nullable FluidOrGasStack type);

	@Override
    public default int fill(FluidStack resource, boolean doFill)
    {
		FluidOrGasStack received = receive(FluidOrGasStack.forFluid(resource), !doFill);
		return received == null ? 0 : received.getAmount();
    }

    @Override
    @Nullable
    public default FluidStack drain(FluidStack resource, boolean doDrain)
    {
    	FluidOrGasStack extracted = extract((stack) -> FluidOrGasStack.isFluidEqualStatic(stack, resource), resource.amount, !doDrain);
    	return extracted == null ? null : extracted.getFluidStack();
    }

    @Override
    @Nullable
    public default FluidStack drain(int maxDrain, boolean doDrain)
    {
    	FluidOrGasStack extracted = extract(FluidOrGasStack::isFluid, maxDrain, !doDrain);
    	return extracted == null ? null : extracted.getFluidStack();
    }

    @Override
    public default int receiveGas(EnumFacing side, GasStack stack, boolean doTransfer)
    {
    	if (isValidSide(side))
    	{
    		FluidOrGasStack received = receive(FluidOrGasStack.forGas(stack), !doTransfer);
    		return received == null ? 0 : received.getAmount();
    	}
    	else return 0;
    }

    @Override
    @Nullable
    public default GasStack drawGas(EnumFacing side, int amount, boolean doTransfer)
    {
    	if (isValidSide(side))
    	{
        	FluidOrGasStack extracted = extract(FluidOrGasStack::isGas, amount, !doTransfer);
        	return extracted == null ? null : extracted.getGasStack();
    	}
    	else return null;
    }

    @Override
    public default boolean canReceiveGas(EnumFacing side, Gas type)
    {
    	return this.isValidSide(side) && this.canReceive(FluidOrGasStack.forGas(type));
    }

    @Override
    public default boolean canDrawGas(EnumFacing side, Gas type)
    {
    	return this.isValidSide(side) && this.canExtract(FluidOrGasStack.forGas(type));
    }
}