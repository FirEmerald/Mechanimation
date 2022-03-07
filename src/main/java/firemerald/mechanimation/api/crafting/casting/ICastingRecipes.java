package firemerald.mechanimation.api.crafting.casting;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.IRecipes;

public interface ICastingRecipes extends IRecipes<ICastingRecipe>
{
	public ICastingRecipe getResult(FluidOrGasStack stack);
}