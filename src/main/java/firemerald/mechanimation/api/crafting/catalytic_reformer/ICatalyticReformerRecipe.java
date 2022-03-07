package firemerald.mechanimation.api.crafting.catalytic_reformer;

import java.util.List;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;

public interface ICatalyticReformerRecipe
{
	public boolean isInputValid(FluidOrGasStack fluid);

	public List<FluidOrGasStack> getFluidInput();

	public FluidOrGasStack getFluidOutputPrimary();

	public FluidOrGasStack getFluidOutputSecondary();

	public int getRequiredFlux();
}