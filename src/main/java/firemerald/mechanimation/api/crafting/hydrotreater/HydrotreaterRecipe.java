package firemerald.mechanimation.api.crafting.hydrotreater;

import java.util.List;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;

public class HydrotreaterRecipe implements IHydrotreaterRecipe
{
	public final List<FluidOrGasStack> inputFluid;
	public final FluidOrGasStack outputFluid;
	public final int requiredHydrogen;

	public HydrotreaterRecipe(List<FluidOrGasStack> inputFluid, FluidOrGasStack outputFluid, int requiredHydrogen)
	{
		this.inputFluid = inputFluid;
		this.outputFluid = outputFluid;
		this.requiredHydrogen = requiredHydrogen;
	}

	@Override
	public boolean isInputValid(FluidOrGasStack fluid)
	{
		if (fluid == null) return false;
		for (FluidOrGasStack stack : this.inputFluid) if (stack.isFluidEqual(fluid)) return true;
		return false;
	}

	@Override
	public List<FluidOrGasStack> getFluidInput()
	{
		return inputFluid;
	}

	@Override
	public FluidOrGasStack getFluidOutput()
	{
		return outputFluid;
	}

	@Override
	public int getRequiredHydrogen()
	{
		return requiredHydrogen;
	}
}