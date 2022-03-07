package firemerald.mechanimation.compat.jei.recipe.distillation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.distillation.IDistillationRecipe;
import firemerald.mechanimation.client.gui.inventory.IGuiElements;
import firemerald.mechanimation.compat.jei.JEICompatPlugin;
import firemerald.mechanimation.compat.jei.recipe.RecipeWrapperFluxReciever;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;

public class RecipeWrapperDistillation extends RecipeWrapperFluxReciever
{
	/* Recipe */
    protected IDistillationRecipe recipe;

	public RecipeWrapperDistillation(IGuiHelper guiHelper, IDistillationRecipe result, String uIdIn)
	{
		super(uIdIn, 1, 1, IGuiElements.ENERGY_BAR_TEX);
		this.recipe = result;
	}

	@Override
	public void getIngredients(IIngredients ingredients)
	{
		ingredients.setInputLists(JEICompatPlugin.TYPE_FLUID, Collections.singletonList(recipe.getFluidInput()));
		List<List<FluidOrGasStack>> outputs = new ArrayList<>();
		outputs.add(Collections.singletonList(recipe.getFluidOutputTop()));
		outputs.add(Collections.singletonList(recipe.getFluidOutputUpper()));
		outputs.add(Collections.singletonList(recipe.getFluidOutputMiddle()));
		outputs.add(Collections.singletonList(recipe.getFluidOutputBottom()));
		ingredients.setOutputLists(JEICompatPlugin.TYPE_FLUID, outputs);
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
	{
		JEICompatPlugin.energyMeter.draw(minecraft, 1, 1);
	}

	@Override
	public int getRFUsed()
	{
		return recipe.getRequiredFlux();
	}
}