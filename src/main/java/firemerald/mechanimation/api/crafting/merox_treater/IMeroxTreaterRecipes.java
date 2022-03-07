package firemerald.mechanimation.api.crafting.merox_treater;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.IRecipes;

public interface IMeroxTreaterRecipes extends IRecipes<IMeroxTreaterRecipe>
{
	public IMeroxTreaterRecipe getResult(FluidOrGasStack fluid);
}