package firemerald.mechanimation.util.crafting;

import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import firemerald.craftloader.api.ICraftingFactory;
import firemerald.craftloader.api.RecipeKey;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.catalytic_reformer.CatalyticReformerRecipe;
import firemerald.mechanimation.api.crafting.catalytic_reformer.ICatalyticReformerRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.JsonContext;

public class CatalyticReformerRecipeFactory implements ICraftingFactory<RecipeKey, ICatalyticReformerRecipe>
{
	@Override
	public ICatalyticReformerRecipe parse(RecipeKey key, JsonContext context, JsonObject obj)
	{
		int requiredEnergy = JsonUtils.getInt(obj, "energy");
		if (requiredEnergy <= 0) throw new JsonSyntaxException("energy must be positive, got " + requiredEnergy);
		List<FluidOrGasStack> inputFluid = CraftingLoader.getFluids(obj, "input_fluid");
		FluidOrGasStack outputFluidPrimary = CraftingLoader.getFluidOptional(obj, "output_fluid_primary");
		FluidOrGasStack outputFluidSecondary = CraftingLoader.getFluidOptional(obj, "output_fluid_secondary");
		return new CatalyticReformerRecipe(inputFluid, outputFluidPrimary, outputFluidSecondary, requiredEnergy);
	}
}
