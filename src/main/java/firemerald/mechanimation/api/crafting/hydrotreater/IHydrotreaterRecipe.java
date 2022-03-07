package firemerald.mechanimation.api.crafting.hydrotreater;

import java.util.List;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;

public interface IHydrotreaterRecipe
{
	public boolean isInputValid(FluidOrGasStack fluid);

	public List<FluidOrGasStack> getFluidInput();

	public FluidOrGasStack getFluidOutput();

	public int getRequiredHydrogen();
}