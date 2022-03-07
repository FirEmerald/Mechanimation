package firemerald.mechanimation.util.crafting;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import firemerald.craftloader.api.CraftLoaderAPI;
import firemerald.craftloader.api.CraftingUtil;
import firemerald.craftloader.api.ICraftingFactory;
import firemerald.craftloader.api.RecipeKey;
import firemerald.craftloader.api.SizedIngredient;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.arc_furnace.ArcFurnaceRecipe;
import firemerald.mechanimation.api.crafting.arc_furnace.IArcFurnaceRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.JsonContext;

public class ArcFurnaceRecipeFactory implements ICraftingFactory<RecipeKey, IArcFurnaceRecipe>
{
	@Override
	public IArcFurnaceRecipe parse(RecipeKey key, JsonContext context, JsonObject obj)
	{
		SizedIngredient input = CraftingUtil.getSizedIngredient(CraftLoaderAPI.getJsonElement(obj, "input"), context);
		FluidOrGasStack output = CraftingLoader.getFluid(obj, "output");
		int temperature = JsonUtils.getInt(obj, "temperature", output.getFluidStack().getFluid().getTemperature(output.getFluidStack()));
		if (temperature < 300) throw new JsonSyntaxException("temperature cannot be below 300, got " + temperature);
		return new ArcFurnaceRecipe(input, output, temperature - 300);
	}
}
