package firemerald.mechanimation.util.crafting;

import java.util.Collections;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import firemerald.craftloader.api.CraftingUtil;
import firemerald.craftloader.api.ICraftingFactory;
import firemerald.craftloader.api.RecipeKey;
import firemerald.craftloader.api.SizedIngredient;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.fluid_reactor.FluidReactorRecipe;
import firemerald.mechanimation.api.crafting.fluid_reactor.IFluidReactorRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.JsonContext;

public class FluidReactorRecipeFactory implements ICraftingFactory<RecipeKey, IFluidReactorRecipe>
{
	@Override
	public IFluidReactorRecipe parse(RecipeKey key, JsonContext context, JsonObject obj)
	{
		float requiredSeconds = JsonUtils.getFloat(obj, "seconds");
		if (requiredSeconds < 0) throw new JsonSyntaxException("seconds cannot be negative, got " + requiredSeconds);
		SizedIngredient inputItem = obj.has("input_item") ? CraftingUtil.getSizedIngredient(obj.get("input_item"), context) : null;
		List<FluidOrGasStack> inputFluidPrimary, inputFluidSecondary, inputFluidTertiary;
		if (obj.has("input_fluid_primary"))
		{
			inputFluidPrimary = CraftingLoader.getFluids(obj, "input_fluid_primary");
			if (obj.has("input_fluid_secondary"))
			{
				inputFluidSecondary = CraftingLoader.getFluids(obj, "input_fluid_secondary");
				if (obj.has("input_fluid_tertiary"))
				{
					inputFluidTertiary = CraftingLoader.getFluids(obj, "input_fluid_tertiary");
				}
				else
				{
					inputFluidTertiary = Collections.emptyList();
				}
			}
			else
			{
				inputFluidSecondary = Collections.emptyList();
				inputFluidTertiary = Collections.emptyList();
			}
		}
		else if (inputItem != null)
		{
			inputFluidPrimary = Collections.emptyList();
			inputFluidSecondary = Collections.emptyList();
			inputFluidTertiary = Collections.emptyList();
		}
		else throw new JsonSyntaxException("Cannot parse recipe - has no input!");
		ItemStack outputItem = obj.has("output_item") ? CraftingUtil.getResult(obj.get("output_item"), context) : null;
		FluidOrGasStack outputFluid;
		if (obj.has("output_fluid"))
		{
			outputFluid = CraftingLoader.getFluid(obj, "output_fluid");
		}
		else if (outputItem != null)
		{
			outputFluid = null;
		}
		else throw new JsonSyntaxException("Cannot parse recipe - has no output!");
		return new FluidReactorRecipe(inputItem, inputFluidPrimary, inputFluidSecondary, inputFluidTertiary, outputItem, outputFluid, requiredSeconds);
	}
}
