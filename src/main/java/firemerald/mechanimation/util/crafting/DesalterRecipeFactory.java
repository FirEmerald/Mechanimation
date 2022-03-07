package firemerald.mechanimation.util.crafting;

import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import firemerald.craftloader.api.ICraftingFactory;
import firemerald.craftloader.api.RecipeKey;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.desalter.DesalterRecipe;
import firemerald.mechanimation.api.crafting.desalter.IDesalterRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.JsonContext;

public class DesalterRecipeFactory implements ICraftingFactory<RecipeKey, IDesalterRecipe>
{
	@Override
	public IDesalterRecipe parse(RecipeKey key, JsonContext context, JsonObject obj)
	{
		int requiredWater = JsonUtils.getInt(obj, "water_amount");
		if (requiredWater <= 0) throw new JsonSyntaxException("water_amount must be positive, got " + requiredWater);
		List<FluidOrGasStack> inputFluid = CraftingLoader.getFluids(obj, "input_fluid");
		FluidOrGasStack outputFluid = CraftingLoader.getFluidOptional(obj, "output_fluid");
		return new DesalterRecipe(inputFluid, outputFluid, requiredWater);
	}
}
