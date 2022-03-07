package firemerald.mechanimation.util.crafting;

import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import firemerald.craftloader.api.ICraftingFactory;
import firemerald.craftloader.api.RecipeKey;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.distillation.DistillationRecipe;
import firemerald.mechanimation.api.crafting.distillation.IDistillationRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.JsonContext;

public class DistillationTowerRecipeFactory implements ICraftingFactory<RecipeKey, IDistillationRecipe>
{
	@Override
	public IDistillationRecipe parse(RecipeKey key, JsonContext context, JsonObject obj)
	{
		int requiredEnergy = JsonUtils.getInt(obj, "energy");
		if (requiredEnergy <= 0) throw new JsonSyntaxException("energy must be positive, got " + requiredEnergy);
		List<FluidOrGasStack> inputFluid = CraftingLoader.getFluids(obj, "input_fluid");
		FluidOrGasStack outputFluidTop = CraftingLoader.getFluidOptional(obj, "output_fluid_top");
		FluidOrGasStack outputFluidUpper = CraftingLoader.getFluidOptional(obj, "output_fluid_upper");
		FluidOrGasStack outputFluidMiddle = CraftingLoader.getFluidOptional(obj, "output_fluid_middle");
		FluidOrGasStack outputFluidBottom = CraftingLoader.getFluidOptional(obj, "output_fluid_bottom");
		return new DistillationRecipe(inputFluid, outputFluidTop, outputFluidUpper, outputFluidMiddle, outputFluidBottom, requiredEnergy);
	}
}
