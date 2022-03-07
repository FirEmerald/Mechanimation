package firemerald.mechanimation.compat.mekanism;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.electrolyzer.IElectrolyzerRecipe;
import firemerald.mechanimation.api.crafting.electrolyzer.IElectrolyzerRecipes;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.RecipeHandler.Recipe;
import mekanism.common.recipe.inputs.FluidInput;
import mekanism.common.recipe.machines.SeparatorRecipe;

public class MekanismElectrolyzerRecipes implements IElectrolyzerRecipes
{
	@Override
	public IElectrolyzerRecipe getResult(FluidOrGasStack fluid)
	{
		if (!fluid.isFluid()) return null;
		SeparatorRecipe recipe = RecipeHandler.getElectrolyticSeparatorRecipe(new FluidInput(fluid.getFluidStack()));
		return recipe == null ? null : new MekanismElectrolyzerRecipe(recipe);
	}

	@Override
	public Collection<? extends IElectrolyzerRecipe> getAllResults()
	{
		List<MekanismElectrolyzerRecipe> recipes = new ArrayList<>();
		Recipe.ELECTROLYTIC_SEPARATOR.get().values().forEach(recipe -> recipes.add(new MekanismElectrolyzerRecipe(recipe)));
		return recipes;
	}

	@Override
	public void init() {}
}