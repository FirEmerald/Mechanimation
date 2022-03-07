package firemerald.mechanimation.api.crafting.distillation;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.IRecipes;

public interface IDistillationRecipes extends IRecipes<IDistillationRecipe>
{
	public IDistillationRecipe getResult(FluidOrGasStack fluid);
}