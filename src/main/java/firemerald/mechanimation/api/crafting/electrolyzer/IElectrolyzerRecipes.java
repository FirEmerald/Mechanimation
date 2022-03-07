package firemerald.mechanimation.api.crafting.electrolyzer;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.IRecipes;

public interface IElectrolyzerRecipes extends IRecipes<IElectrolyzerRecipe>
{
	public IElectrolyzerRecipe getResult(FluidOrGasStack fluid);
}