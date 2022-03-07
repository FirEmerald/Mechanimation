package firemerald.mechanimation.api.crafting.press;

import firemerald.mechanimation.api.crafting.IRecipes;
import net.minecraft.item.ItemStack;

public interface IPressRecipes extends IRecipes<IPressRecipe>
{
	public IPressRecipe getResult(ItemStack input);
}