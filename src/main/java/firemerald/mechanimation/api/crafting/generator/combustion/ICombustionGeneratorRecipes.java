package firemerald.mechanimation.api.crafting.generator.combustion;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.IRecipes;

public interface ICombustionGeneratorRecipes extends IRecipes<ICombustionGeneratorRecipe>
{
	public ICombustionGeneratorRecipe getResult(FluidOrGasStack fuel);
}