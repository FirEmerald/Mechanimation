package firemerald.mechanimation.api.crafting.pulverizer;

import firemerald.mechanimation.api.crafting.IRecipes;
import net.minecraft.item.ItemStack;

public interface IPulverizerRecipes extends IRecipes<IPulverizerRecipe>
{
	public IPulverizerRecipe getResult(ItemStack stack);
}