package firemerald.mechanimation.compat.jei.recipe.fluid_reactor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.fluid_reactor.IFluidReactorRecipe;
import firemerald.mechanimation.compat.jei.JEICompatPlugin;
import firemerald.mechanimation.compat.jei.recipe.RecipeWrapperBase;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraftforge.fluids.FluidStack;

public class RecipeWrapperFluidReactor extends RecipeWrapperBase
{
	/* Recipe */
    protected IFluidReactorRecipe recipe;

	public RecipeWrapperFluidReactor(IGuiHelper guiHelper, IFluidReactorRecipe result, String uIdIn)
	{
		super(uIdIn);
		this.recipe = result;
	}

	@Override
	public void getIngredients(IIngredients ingredients)
	{
		ingredients.setInputLists(VanillaTypes.ITEM, Collections.singletonList(Arrays.asList(recipe.getItemInput())));
		{
			List<List<FluidOrGasStack>> fluidInputs = new ArrayList<>();
			fluidInputs.add(recipe.getFluidInputPrimary());
			fluidInputs.add(recipe.getFluidInputSecondary());
			fluidInputs.add(recipe.getFluidInputTertiary());
			ingredients.setInputLists(JEICompatPlugin.TYPE_FLUID, fluidInputs);
		}
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getItemOutput());
		{
			List<List<FluidStack>> fluidOutput = new ArrayList<>();
			fluidOutput.add(recipe.getFluidOutput() == null || !recipe.getFluidOutput().isFluid() ? Collections.emptyList() : Collections.singletonList(recipe.getFluidOutput().getFluidStack()));
			ingredients.setOutputLists(VanillaTypes.FLUID, fluidOutput);
		}
		{
			ingredients.setOutputLists(JEICompatPlugin.TYPE_FLUID, Collections.singletonList(Collections.singletonList(recipe.getFluidOutput())));
		}
	}
}