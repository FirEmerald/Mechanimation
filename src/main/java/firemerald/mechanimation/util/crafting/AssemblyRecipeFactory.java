package firemerald.mechanimation.util.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import firemerald.craftloader.api.CraftLoaderAPI;
import firemerald.craftloader.api.CraftingUtil;
import firemerald.craftloader.api.ICraftingFactory;
import firemerald.craftloader.api.SizedIngredient;
import firemerald.mechanimation.api.crafting.assembly_terminal.AssemblyRecipe;
import firemerald.mechanimation.api.crafting.assembly_terminal.IAssemblyRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.JsonContext;

public class AssemblyRecipeFactory implements ICraftingFactory<AssemblyRecipeKey, IAssemblyRecipe>
{
	@Override
	public IAssemblyRecipe parse(AssemblyRecipeKey key, JsonContext context, JsonObject obj)
	{
		ItemStack output = CraftingUtil.getResult(CraftLoaderAPI.getJsonElement(obj, "output"), context);
		JsonArray inAr = JsonUtils.getJsonArray(obj, "input");
		if (inAr.size() == 0) throw new JsonSyntaxException("Invalid input, expected to find a JsonArray of JsonElement");
		if (inAr.size() > key.blueprint.getMaxUsedInputs()) throw new JsonSyntaxException("Too many inputs - got " + inAr.size() + ", limit is " + key.blueprint.getMaxUsedInputs());
		SizedIngredient[] inputs = new SizedIngredient[inAr.size()];
		for (int i = 0; i < inAr.size(); i++) inputs[i] = CraftingUtil.getSizedIngredient(inAr.get(i), context);
		int tier = JsonUtils.getInt(obj, "tier", 0);
		if (tier < 0) throw new JsonSyntaxException("tier cannot be negative, got " + tier);
		int requiredEnergy = JsonUtils.getInt(obj, "energy", 0);
		if (requiredEnergy < 0) throw new JsonSyntaxException("energy cannot be negative, got " + requiredEnergy);
		Ingredient blueprint;
		if (obj.has("blueprint")) blueprint = CraftingUtil.getIngredient(obj.get("blueprint"), context);
		else blueprint = key.blueprint.blueprint;
		return new AssemblyRecipe(key.name, blueprint, tier, requiredEnergy, output, inputs);
	}
}
