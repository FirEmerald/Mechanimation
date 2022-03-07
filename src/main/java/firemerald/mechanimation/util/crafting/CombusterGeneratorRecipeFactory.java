package firemerald.mechanimation.util.crafting;

import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import firemerald.craftloader.api.ICraftingFactory;
import firemerald.craftloader.api.RecipeKey;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.generator.combustion.CombustionGeneratorRecipe;
import firemerald.mechanimation.api.crafting.generator.combustion.ICombustionGeneratorRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.JsonContext;

public class CombusterGeneratorRecipeFactory implements ICraftingFactory<RecipeKey, ICombustionGeneratorRecipe>
{
	@Override
	public ICombustionGeneratorRecipe parse(RecipeKey key, JsonContext context, JsonObject obj)
	{
		float speed = JsonUtils.getFloat(obj, "speed");
		if (speed <= 0) throw new JsonSyntaxException("speed must be positive, got " + speed);
		int ticksPerMillibucket = JsonUtils.getInt(obj, "ticks_per_millibucket");
		if (ticksPerMillibucket <= 0) throw new JsonSyntaxException("ticks_per_millibucket must be positive, got " + ticksPerMillibucket);
		List<FluidOrGasStack> inputFluid = CraftingLoader.getFluids(obj, "input_fluid");
		return new CombustionGeneratorRecipe(inputFluid, speed, ticksPerMillibucket);
	}
}
