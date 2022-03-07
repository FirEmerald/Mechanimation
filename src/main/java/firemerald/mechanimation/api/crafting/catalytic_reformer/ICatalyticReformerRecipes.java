package firemerald.mechanimation.api.crafting.catalytic_reformer;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.IRecipes;

public interface ICatalyticReformerRecipes extends IRecipes<ICatalyticReformerRecipe>
{
	public ICatalyticReformerRecipe getResult(FluidOrGasStack fluid);
}