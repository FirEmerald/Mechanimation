package firemerald.mechanimation.api.crafting.merox_treater;

import java.util.List;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;

public interface IMeroxTreaterRecipe
{
	public boolean isInputValid(FluidOrGasStack fluid);

	public List<FluidOrGasStack> getFluidInput();

	public FluidOrGasStack getFluidOutput();

	public int getRequiredOxygen();
}