package firemerald.mechanimation.compat.mekanism;

import java.util.Collections;
import java.util.List;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.electrolyzer.IElectrolyzerRecipe;
import mekanism.common.integration.forgeenergy.ForgeEnergyIntegration;
import mekanism.common.recipe.machines.SeparatorRecipe;

public class MekanismElectrolyzerRecipe implements IElectrolyzerRecipe
{
	public final SeparatorRecipe recipe;

	public MekanismElectrolyzerRecipe(SeparatorRecipe recipe)
	{
		this.recipe = recipe;
	}

	@Override
	public boolean isInputValid(FluidOrGasStack fluid)
	{
		if (!fluid.isFluid()) return false;
		else return recipe.getInput().ingredient.isFluidEqual(fluid.getFluidStack());
	}

	@Override
	public List<FluidOrGasStack> getFluidInput()
	{
		return Collections.singletonList(FluidOrGasStack.forFluid(recipe.getInput().ingredient));
	}

	@Override
	public FluidOrGasStack getFluidOutputPrimary()
	{
		return FluidOrGasStack.forGas(recipe.recipeOutput.rightGas);
	}

	@Override
	public FluidOrGasStack getFluidOutputSecondary()
	{
		return FluidOrGasStack.forGas(recipe.recipeOutput.leftGas);
	}

	@Override
	public int getRequiredFlux()
	{
		return ForgeEnergyIntegration.toForge(recipe.energyUsage);
	}
}