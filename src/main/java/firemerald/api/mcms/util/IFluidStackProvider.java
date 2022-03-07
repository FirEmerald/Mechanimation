package firemerald.api.mcms.util;

import net.minecraftforge.fluids.FluidStack;

public interface IFluidStackProvider
{
	public FluidStack getFluidStack(int index);

	public int getMaxFluid(int index);
}