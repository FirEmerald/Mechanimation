package firemerald.mechanimation.compat.jei.recipe.press;

import java.util.Arrays;
import java.util.Collections;

import firemerald.mechanimation.api.crafting.press.IPressRecipe;
import firemerald.mechanimation.client.gui.inventory.IGuiElements;
import firemerald.mechanimation.compat.jei.JEICompatPlugin;
import firemerald.mechanimation.compat.jei.recipe.RecipeWrapperFluxReciever;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class RecipeWrapperPress extends RecipeWrapperFluxReciever
{
	/* Recipe */
    protected IPressRecipe recipe;

	public RecipeWrapperPress(IGuiHelper guiHelper, IPressRecipe result, String uIdIn)
	{
		super(uIdIn, 1, 1, IGuiElements.ENERGY_BAR_TEX);
		this.recipe = result;
	}

	@Override
	public void getIngredients(IIngredients ingredients)
	{
		ItemStack[] inputsPrimary = recipe.getInput();
		ingredients.setInputLists(VanillaTypes.ITEM, Collections.singletonList(Arrays.asList(inputsPrimary)));
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getOutput());
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
	{
		JEICompatPlugin.energyMeter.draw(minecraft, 1, 1);
		JEICompatPlugin.progressRight.draw(minecraft, 73, 27);
	}

	@Override
	public int getRFUsed()
	{
		return recipe.getRequiredEnergy();
	}
}