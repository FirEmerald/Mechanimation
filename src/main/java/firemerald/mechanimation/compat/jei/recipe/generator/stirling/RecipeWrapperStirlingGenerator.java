package firemerald.mechanimation.compat.jei.recipe.generator.stirling;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Preconditions;

import firemerald.api.core.client.Translator;
import firemerald.mechanimation.client.gui.inventory.IGuiElements;
import firemerald.mechanimation.compat.jei.JEICompatPlugin;
import firemerald.mechanimation.compat.jei.recipe.RecipeWrapperBase;
import firemerald.mechanimation.tileentity.machine.generator.TileEntityStirlingGenerator;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class RecipeWrapperStirlingGenerator extends RecipeWrapperBase
{
	protected String uId;
	private final List<List<ItemStack>> inputs;
	private final String smeltCountString;
	private final String fluxProducedString;

	public RecipeWrapperStirlingGenerator(IGuiHelper guiHelper, Collection<ItemStack> input, int burnTime, String uIdIn)
	{
		super(uIdIn);
		Preconditions.checkArgument(burnTime > 0, "burn time must be greater than 0");
		List<ItemStack> inputList = new ArrayList<>(input);
		this.inputs = Collections.singletonList(inputList);
		this.smeltCountString = Translator.format("gui.mechanimation.jei.stirling_generator.burn_item", burnTime / (20f * TileEntityStirlingGenerator.BURN_TICKS_PER_TICK));
		this.fluxProducedString = Translator.format("mechanimation.jei.tooltip.rf.produced", burnTime * TileEntityStirlingGenerator.FLUX_PER_BURN_TICK);
	}

	@Override
	public void getIngredients(IIngredients ingredients)
	{
		ingredients.setInputLists(VanillaTypes.ITEM, inputs);
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
	{
		JEICompatPlugin.energyMeterFill.draw(minecraft, 1, 1);
		JEICompatPlugin.progressBurn.draw(minecraft, 74, 29);
		minecraft.fontRenderer.drawString(smeltCountString, 24, 13, Color.gray.getRGB());
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY)
	{
		if (mouseX >= 1 && mouseY >= 1 && mouseX < 1 + IGuiElements.ENERGY_BAR_TEX.w && mouseY < 1 + IGuiElements.ENERGY_BAR_TEX.h) return Collections.singletonList(fluxProducedString);
		else return Collections.emptyList();
	}
}