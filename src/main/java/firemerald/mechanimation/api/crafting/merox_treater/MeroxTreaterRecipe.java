package firemerald.mechanimation.api.crafting.merox_treater;

import java.util.List;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;

public class MeroxTreaterRecipe implements IMeroxTreaterRecipe
{
	public final List<FluidOrGasStack> inputFluid;
	public final FluidOrGasStack outputFluid;
	public final int requiredOxygen;

	public MeroxTreaterRecipe(List<FluidOrGasStack> inputFluid, FluidOrGasStack outputFluid, int requiredOxygen)
	{
		this.inputFluid = inputFluid;
		this.outputFluid = outputFluid;
		this.requiredOxygen = requiredOxygen;
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
	public int getRequiredOxygen()
	{
		return requiredOxygen;
	}
}