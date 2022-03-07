package firemerald.mechanimation.util.crafting;

import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import firemerald.craftloader.api.ICraftingFactory;
import firemerald.craftloader.api.RecipeKey;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.hydrotreater.HydrotreaterRecipe;
import firemerald.mechanimation.api.crafting.hydrotreater.IHydrotreaterRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.JsonContext;

public class HydrotreaterRecipeFactory implements ICraftingFactory<RecipeKey, IHydrotreaterRecipe>
{
	@Override
	public IHydrotreaterRecipe parse(RecipeKey key, JsonContext context, JsonObject obj)
	{
		int requiredHydrogen = JsonUtils.getInt(obj, "hydrogen_amount");
		if (requiredHydrogen <= 0) throw new JsonSyntaxException("hydrogen_amount must be positive, got " + requiredHydrogen);
		List<FluidOrGasStack> inputFluid = CraftingLoader.getFluids(obj, "input_fluid");
		FluidOrGasStack outputFluid = CraftingLoader.getFluidOptional(obj, "output_fluid");
		return new HydrotreaterRecipe(inputFluid, outputFluid, requiredHydrogen);
	}
}
