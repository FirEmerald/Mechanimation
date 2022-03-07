package firemerald.mechanimation.api.crafting.arc_furnace;

import firemerald.mechanimation.api.crafting.IRecipes;
import net.minecraft.item.ItemStack;

public interface IArcFurnaceRecipes extends IRecipes<IArcFurnaceRecipe>
{
	public IArcFurnaceRecipe getResult(ItemStack stack);
}