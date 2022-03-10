package firemerald.mechanimation.mcms;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;

public interface IFluidOrGasStackProvider
{
	public FluidOrGasStack getFluidStack(int index);

	public int getMaxFluid(int index);
}
