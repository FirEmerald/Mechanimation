package firemerald.mechanimation.compat.jei.recipe.desalter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.desalter.IDesalterRecipe;
import firemerald.mechanimation.compat.jei.JEICompatPlugin;
import firemerald.mechanimation.compat.jei.recipe.RecipeWrapperBase;
import firemerald.mechanimation.init.MechanimationFluids;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraftforge.fluids.FluidStack;

public class RecipeWrapperDesalter extends RecipeWrapperBase
{
	/* Recipe */
    protected IDesalterRecipe recipe;

	/* Animation */
	//protected IDrawableAnimated progress; TODO

	public RecipeWrapperDesalter(IGuiHelper guiHelper, IDesalterRecipe result, String uIdIn)
	{
		super(uIdIn);
		this.recipe = result;
	}

	@Override
	public void getIngredients(IIngredients ingredients)
	{
		List<List<FluidOrGasStack>> inputs = new ArrayList<>();
		inputs.add(recipe.getFluidInput());
		inputs.add(MechanimationFluids.forName("water", recipe.getRequiredWater()));
		ingredients.setInputLists(JEICompatPlugin.TYPE_FLUID, inputs);
		List<List<FluidOrGasStack>> outputs = new ArrayList<>();
		outputs.add(Collections.singletonList(recipe.getFluidOutput()));
		outputs.add(Collections.singletonList(FluidOrGasStack.forFluid(new FluidStack(MechanimationFluids.brine, recipe.getRequiredWater()))));
		ingredients.setOutputLists(JEICompatPlugin.TYPE_FLUID, outputs);
	}
}