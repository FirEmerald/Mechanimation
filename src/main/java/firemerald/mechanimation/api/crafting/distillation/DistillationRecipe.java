package firemerald.mechanimation.api.crafting.distillation;

import java.util.List;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;

public class DistillationRecipe implements IDistillationRecipe
{
	public final List<FluidOrGasStack> inputFluid;
	public final FluidOrGasStack outputFluidTop, outputFluidUpper, outputFluidMiddle, outputFluidBottom;
	public final int requiredFlux;

	public DistillationRecipe(List<FluidOrGasStack> inputFluid, FluidOrGasStack outputFluidTop, FluidOrGasStack outputFluidUpper, FluidOrGasStack outputFluidMiddle, FluidOrGasStack outputFluidBottom, int requiredFlux)
	{
		this.inputFluid = inputFluid;
		this.outputFluidTop = outputFluidTop;
		this.outputFluidUpper = outputFluidUpper;
		this.outputFluidMiddle = outputFluidMiddle;
		this.outputFluidBottom = outputFluidBottom;
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
	public FluidOrGasStack getFluidOutputTop()
	{
		return outputFluidTop;
	}

	@Override
	public FluidOrGasStack getFluidOutputUpper()
	{
		return outputFluidUpper;
	}

	@Override
	public FluidOrGasStack getFluidOutputMiddle()
	{
		return outputFluidMiddle;
	}

	@Override
	public FluidOrGasStack getFluidOutputBottom()
	{
		return outputFluidBottom;
	}

	@Override
	public int getRequiredFlux()
	{
		return requiredFlux;
	}
}