package firemerald.mechanimation.api.crafting.hydrotreater;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.IRecipes;

public interface IHydrotreaterRecipes extends IRecipes<IHydrotreaterRecipe>
{
	public IHydrotreaterRecipe getResult(FluidOrGasStack fluid);
}