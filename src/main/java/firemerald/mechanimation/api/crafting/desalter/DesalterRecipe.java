package firemerald.mechanimation.api.crafting.desalter;

import java.util.List;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;

public class DesalterRecipe implements IDesalterRecipe
{
	public final List<FluidOrGasStack> inputFluid;
	public final FluidOrGasStack outputFluid;
	public final int requiredWater;

	public DesalterRecipe(List<FluidOrGasStack> inputFluid, FluidOrGasStack outputFluid, int requiredWater)
	{
		this.inputFluid = inputFluid;
		this.outputFluid = outputFluid;
		this.requiredWater = requiredWater;
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
	public int getRequiredWater()
	{
		return requiredWater;
	}
}