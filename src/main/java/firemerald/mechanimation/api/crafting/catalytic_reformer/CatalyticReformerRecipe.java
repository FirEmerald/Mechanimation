package firemerald.mechanimation.api.crafting.catalytic_reformer;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;

public class CatalyticReformerRecipe implements ICatalyticReformerRecipe
{
	public final List<FluidOrGasStack> inputFluid;
	public final FluidOrGasStack outputFluidPrimary, outputFluidSecondary;
	public final int requiredFlux;

	public CatalyticReformerRecipe(List<FluidOrGasStack> inputFluid, @Nonnull FluidOrGasStack outputFluidPrimary, @Nullable FluidOrGasStack outputFluidSecondary, int requiredFlux)
	{
		this.inputFluid = inputFluid;
		this.outputFluidPrimary = outputFluidPrimary;
		this.outputFluidSecondary = outputFluidSecondary;
		this.requiredFlux = requiredFlux;
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
	public FluidOrGasStack getFluidOutputPrimary()
	{
		return outputFluidPrimary;
	}

	@Override
	public FluidOrGasStack getFluidOutputSecondary()
	{
		return outputFluidSecondary;
	}

	@Override
	public int getRequiredFlux()
	{
		return requiredFlux;
	}
}