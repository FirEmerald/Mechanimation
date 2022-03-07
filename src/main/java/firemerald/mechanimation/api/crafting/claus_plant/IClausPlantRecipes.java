package firemerald.mechanimation.api.crafting.claus_plant;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.IRecipes;

public interface IClausPlantRecipes extends IRecipes<IClausPlantRecipe>
{
	public IClausPlantRecipe getResult(FluidOrGasStack fluid);
}