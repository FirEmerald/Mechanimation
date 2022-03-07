package firemerald.mechanimation.api.crafting.distillation;

import java.util.List;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;

public interface IDistillationRecipe
{
	public boolean isInputValid(FluidOrGasStack fluid);

	public List<FluidOrGasStack> getFluidInput();

	public FluidOrGasStack getFluidOutputTop();

	public FluidOrGasStack getFluidOutputUpper();

	public FluidOrGasStack getFluidOutputMiddle();

	public FluidOrGasStack getFluidOutputBottom();

	public int getRequiredFlux();
}