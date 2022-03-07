package firemerald.mechanimation.util.crafting;

import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import firemerald.craftloader.api.CraftLoaderAPI;
import firemerald.craftloader.api.CraftingUtil;
import firemerald.craftloader.api.ICraftingFactory;
import firemerald.craftloader.api.RecipeKey;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.claus_plant.ClausPlantRecipe;
import firemerald.mechanimation.api.crafting.claus_plant.IClausPlantRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.JsonContext;

public class ClausPlantRecipeFactory implements ICraftingFactory<RecipeKey, IClausPlantRecipe>
{
	@Override
	public IClausPlantRecipe parse(RecipeKey key, JsonContext context, JsonObject obj)
	{
		int requiredOxygen = JsonUtils.getInt(obj, "oxygen_amount");
		if (requiredOxygen <= 0) throw new JsonSyntaxException("oxygen_amount must be positive, got " + requiredOxygen);
		List<FluidOrGasStack> inputFluid = CraftingLoader.getFluids(obj, "input_fluid");
		ItemStack outputItem = CraftingUtil.getResult(CraftLoaderAPI.getJsonElement(obj, "output_item"), context);
		return new ClausPlantRecipe(inputFluid, outputItem, requiredOxygen);
	}
}
