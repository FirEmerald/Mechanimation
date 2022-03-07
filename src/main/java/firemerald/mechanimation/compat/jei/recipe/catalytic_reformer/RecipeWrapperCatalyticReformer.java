package firemerald.mechanimation.compat.jei.recipe.catalytic_reformer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.catalytic_reformer.ICatalyticReformerRecipe;
import firemerald.mechanimation.client.gui.inventory.IGuiElements;
import firemerald.mechanimation.compat.jei.JEICompatPlugin;
import firemerald.mechanimation.compat.jei.recipe.RecipeWrapperFluxReciever;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;

public class RecipeWrapperCatalyticReformer extends RecipeWrapperFluxReciever
{
	/* Recipe */
    protected ICatalyticReformerRecipe recipe;

	public RecipeWrapperCatalyticReformer(IGuiHelper guiHelper, ICatalyticReformerRecipe result, String uIdIn)
	{
		super(uIdIn, 1, 1, IGuiElements.ENERGY_BAR_TEX);
		this.recipe = result;
	}

	@Override
	public void getIngredients(IIngredients ingredients)
	{
		ingredients.setInputLists(JEICompatPlugin.TYPE_FLUID, Collections.singletonList(recipe.getFluidInput()));
		List<List<FluidOrGasStack>> outputs = new ArrayList<>();
		outputs.add(Collections.singletonList(recipe.getFluidOutputPrimary()));
		outputs.add(Collections.singletonList(recipe.getFluidOutputSecondary()));
		ingredients.setOutputLists(JEICompatPlugin.TYPE_FLUID, outputs);
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
	{
		JEICompatPlugin.energyMeter.draw(minecraft, 1, 1);
		JEICompatPlugin.progressLeftHalf.draw(minecraft, 41, 17);
		JEICompatPlugin.progressRightHalf.draw(minecraft, 67, 17);
	}

	@Override
	public int getRFUsed()
	{
		return recipe.getRequiredFlux();
	}
}