package firemerald.mechanimation.util.crafting;

import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import firemerald.craftloader.api.ICraftingFactory;
import firemerald.craftloader.api.RecipeKey;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.electrolyzer.ElectrolyzerRecipe;
import firemerald.mechanimation.api.crafting.electrolyzer.IElectrolyzerRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.JsonContext;

public class ElectrolyzerRecipeFactory implements ICraftingFactory<RecipeKey, IElectrolyzerRecipe>
{
	@Override
	public IElectrolyzerRecipe parse(RecipeKey key, JsonContext context, JsonObject obj)
	{
		int requiredEnergy = JsonUtils.getInt(obj, "energy");
		if (requiredEnergy <= 0) throw new JsonSyntaxException("energy must be positive, got " + requiredEnergy);
		List<FluidOrGasStack> inputFluid = CraftingLoader.getFluids(obj, "input_fluid");
		FluidOrGasStack outputFluidPrimary = CraftingLoader.getFluidOptional(obj, "output_fluid_primary");
		FluidOrGasStack outputFluidSecondary = CraftingLoader.getFluidOptional(obj, "output_fluid_secondary");
		return new ElectrolyzerRecipe(inputFluid, outputFluidPrimary, outputFluidSecondary, requiredEnergy);
	}
}
