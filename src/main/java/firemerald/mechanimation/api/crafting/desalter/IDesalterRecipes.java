package firemerald.mechanimation.api.crafting.desalter;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.IRecipes;

public interface IDesalterRecipes extends IRecipes<IDesalterRecipe>
{
	public IDesalterRecipe getResult(FluidOrGasStack fluid);
}