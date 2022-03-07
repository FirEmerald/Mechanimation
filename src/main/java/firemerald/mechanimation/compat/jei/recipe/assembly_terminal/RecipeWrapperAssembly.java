package firemerald.mechanimation.compat.jei.recipe.assembly_terminal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import firemerald.mechanimation.api.crafting.assembly_terminal.IAssemblyBlueprint;
import firemerald.mechanimation.api.crafting.assembly_terminal.IAssemblyRecipe;
import firemerald.mechanimation.api.util.Rectangle;
import firemerald.mechanimation.client.gui.inventory.IGuiElements;
import firemerald.mechanimation.compat.jei.JEICompatPlugin;
import firemerald.mechanimation.compat.jei.recipe.RecipeWrapperFluxReciever;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class RecipeWrapperAssembly extends RecipeWrapperFluxReciever
{
	protected String uId;
	/* Recipe */
    protected IAssemblyBlueprint blueprint;
    protected IAssemblyRecipe recipe;
	protected IDrawableStatic background;
	protected IDrawableAnimated progressBar;

	public RecipeWrapperAssembly(IGuiHelper guiHelper, IAssemblyBlueprint blueprint, IAssemblyRecipe recipe, String uIdIn)
	{
		super(uIdIn, 1, 73, IGuiElements.ENERGY_BAR_TEX);
		this.blueprint = blueprint;
		this.recipe = recipe;
		ItemStack bStack = recipe.getDefaultBlueprint()[0];
		Rectangle bounds = blueprint.getBlueprintImageBounds(bStack, recipe.getDefaultTier());
		this.background = guiHelper.createDrawable(blueprint.blueprintImage(recipe.getDefaultBlueprint()[0], recipe.getDefaultTier()), bounds.x1, bounds.y1, bounds.w, bounds.h);
	}

	@Override
	public String getUid()
	{
		return uId;
	}

	@Override
	public void getIngredients(IIngredients ingredients)
	{
		List<List<ItemStack>> inputs = new ArrayList<>();
		inputs.add(Arrays.asList(recipe.getDefaultBlueprint()));
		Arrays.stream(recipe.getDefaultInput(recipe.getDefaultBlueprint()[0], recipe.getDefaultTier())).map(data -> Arrays.asList(data)).forEach(inputs::add);
		ingredients.setInputLists(VanillaTypes.ITEM, inputs);
		ingredients.setOutputLists(VanillaTypes.ITEM, Collections.singletonList(Arrays.asList(recipe.getDefaultOutput())));
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
	{
		JEICompatPlugin.workMeterFill.draw(minecraft, 1, 27);
		JEICompatPlugin.energyMeter.draw(minecraft, 1, 73);
		background.draw(minecraft, RecipeCategoryAssembly.SLOTS_OFFSET_X + (168 - background.getWidth()) / 2, RecipeCategoryAssembly.SLOTS_OFFSET_Y + (130 - background.getHeight()) / 2);
	}

	@Override
	public int getRFUsed()
	{
		return recipe.getDefaultUsedFlux();
	}
}