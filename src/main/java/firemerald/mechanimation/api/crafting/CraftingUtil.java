package firemerald.mechanimation.api.crafting;

import java.util.List;

public class CraftingUtil
{
	public static boolean containsFluid(List<FluidOrGasStack> fluids, FluidOrGasStack target)
	{
		if (target == null) return false;
		for (FluidOrGasStack stack : fluids) if (stack.isFluidEqual(target)) return true;
		return false;
	}

	public static FluidOrGasStack getFluid(List<FluidOrGasStack> fluids, FluidOrGasStack target)
	{
		if (target == null) return null;
		for (FluidOrGasStack stack : fluids) if (stack.isFluidEqual(target)) return stack;
		return null;
	}
	public static boolean containsFluidWithSize(List<FluidOrGasStack> fluids, FluidOrGasStack target)
	{
		if (target == null) return false;
		for (FluidOrGasStack stack : fluids) if (stack.isFluidEqual(target) && stack.getAmount() <= target.getAmount()) return true;
		return false;
	}

	public static FluidOrGasStack getFluidWithSize(List<FluidOrGasStack> fluids, FluidOrGasStack target)
	{
		if (target == null) return null;
		for (FluidOrGasStack stack : fluids) if (stack.isFluidEqual(target) && stack.getAmount() <= target.getAmount()) return stack;
		return null;
	}
}