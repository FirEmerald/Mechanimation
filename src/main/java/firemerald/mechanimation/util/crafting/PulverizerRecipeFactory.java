package firemerald.mechanimation.util.crafting;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import firemerald.craftloader.api.CraftLoaderAPI;
import firemerald.craftloader.api.CraftingUtil;
import firemerald.craftloader.api.ICraftingFactory;
import firemerald.craftloader.api.RecipeKey;
import firemerald.craftloader.api.SizedIngredient;
import firemerald.mechanimation.api.crafting.pulverizer.IPulverizerRecipe;
import firemerald.mechanimation.api.crafting.pulverizer.PulverizerRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.JsonContext;

public class PulverizerRecipeFactory implements ICraftingFactory<RecipeKey, IPulverizerRecipe>
{
	@Override
	public IPulverizerRecipe parse(RecipeKey key, JsonContext context, JsonObject obj)
	{
		int requiredEnergy = JsonUtils.getInt(obj, "energy");
		if (requiredEnergy < 0) throw new JsonSyntaxException("energy cannot be negative, got " + requiredEnergy);
		SizedIngredient input = CraftingUtil.getSizedIngredient(CraftLoaderAPI.getJsonElement(obj, "input"), context);
		ItemStack output = CraftingUtil.getResult(CraftLoaderAPI.getJsonElement(obj, "output"), context);
		return new PulverizerRecipe(input, output, requiredEnergy);
	}
}
