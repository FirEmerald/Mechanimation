package firemerald.mechanimation.api.crafting.desalter;

import java.util.List;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;

public interface IDesalterRecipe
{
	public boolean isInputValid(FluidOrGasStack fluid);

	public List<FluidOrGasStack> getFluidInput();

	public FluidOrGasStack getFluidOutput();

	public int getRequiredWater();
}