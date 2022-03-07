package firemerald.mechanimation.util.crafting;

import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import firemerald.craftloader.api.ICraftingFactory;
import firemerald.craftloader.api.RecipeKey;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.merox_treater.IMeroxTreaterRecipe;
import firemerald.mechanimation.api.crafting.merox_treater.MeroxTreaterRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.JsonContext;

public class MeroxTreaterRecipeFactory implements ICraftingFactory<RecipeKey, IMeroxTreaterRecipe>
{
	@Override
	public IMeroxTreaterRecipe parse(RecipeKey key, JsonContext context, JsonObject obj)
	{
		int requiredOxygen = JsonUtils.getInt(obj, "oxygen_amount");
		if (requiredOxygen <= 0) throw new JsonSyntaxException("oxygen_amount must be positive, got " + requiredOxygen);
		List<FluidOrGasStack> inputFluid = CraftingLoader.getFluids(obj, "input_fluid");
		FluidOrGasStack outputFluid = CraftingLoader.getFluidOptional(obj, "output_fluid");
		return new MeroxTreaterRecipe(inputFluid, outputFluid, requiredOxygen);
	}
}
