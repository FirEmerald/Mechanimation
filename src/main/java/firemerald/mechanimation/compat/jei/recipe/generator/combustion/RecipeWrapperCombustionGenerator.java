package firemerald.mechanimation.compat.jei.recipe.generator.combustion;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import firemerald.api.core.client.Translator;
import firemerald.mechanimation.api.crafting.generator.combustion.ICombustionGeneratorRecipe;
import firemerald.mechanimation.client.gui.inventory.IGuiElements;
import firemerald.mechanimation.compat.jei.JEICompatPlugin;
import firemerald.mechanimation.compat.jei.recipe.RecipeWrapperBase;
import firemerald.mechanimation.tileentity.machine.generator.TileEntityCombustionGenerator;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;

public class RecipeWrapperCombustionGenerator extends RecipeWrapperBase
{
	/* Recipe */
    protected ICombustionGeneratorRecipe recipe;

    public final String burnTimeString, speedString, rfProducedMaxString;

	public RecipeWrapperCombustionGenerator(IGuiHelper guiHelper, ICombustionGeneratorRecipe result, String uIdIn)
	{
		super(uIdIn);
		this.recipe = result;
		this.burnTimeString = Translator.format("gui.mechanimation.jei.combustion_generator.burn_time", result.ticksPerMillibucket());
		this.speedString = Translator.format("gui.mechanimation.jei.combustion_generator.speed", result.speed() * 60);
		this.rfProducedMaxString = Translator.format("mechanimation.jei.tooltip.rf.produced.maximum", result.speed() * (TileEntityCombustionGenerator.FLUX_PER_ROTATION / 20));
	}

	@Override
	public String getUid()
	{
		return uId;
	}

	@Override
	public void getIngredients(IIngredients ingredients)
	{
		ingredients.setInputLists(JEICompatPlugin.TYPE_FLUID, Collections.singletonList(recipe.fuel()));
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
	{
		JEICompatPlugin.energyMeterFill.draw(minecraft, 1, 1);
		minecraft.fontRenderer.drawString(burnTimeString, 18, 0, Color.gray.getRGB());
		minecraft.fontRenderer.drawString(speedString, 18, 8, Color.gray.getRGB());
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY)
	{
		if (mouseX >= 1 && mouseY >= 1 && mouseX < 1 + IGuiElements.ENERGY_BAR_TEX.w && mouseY < 1 + IGuiElements.ENERGY_BAR_TEX.h) return Collections.singletonList(rfProducedMaxString);
		else return Collections.emptyList();
	}
}