package firemerald.mechanimation.compat.jei.recipe.casting;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.stream.Collectors;

import firemerald.api.core.client.Translator;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.casting.EnumCastingType;
import firemerald.mechanimation.api.crafting.casting.ICastingRecipe;
import firemerald.mechanimation.compat.jei.JEICompatPlugin;
import firemerald.mechanimation.compat.jei.recipe.RecipeWrapperBase;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.client.Minecraft;

public class RecipeWrapperCasting extends RecipeWrapperBase
{
	private final ICastingRecipe recipe;
	private final String typeString, tempString;

	public RecipeWrapperCasting(IGuiHelper guiHelper, EnumCastingType type, ICastingRecipe recipe, String uIdIn)
	{
		super(uIdIn);
		this.recipe = recipe;
		this.typeString = Translator.translate("casting_type." + type.name().toLowerCase(Locale.ENGLISH));
		this.tempString = Translator.format("gui.mechanimation.jei.casting.temp", recipe.getTemperature(), recipe.getCooledTemperature());
	}

	@Override
	public void getIngredients(IIngredients ingredients)
	{
		Collection<FluidOrGasStack> input = recipe.getInput();
		ingredients.setInputLists(JEICompatPlugin.TYPE_FLUID, Collections.singletonList(input.stream().collect(Collectors.toList())));
		ingredients.setOutputLists(VanillaTypes.ITEM, Collections.singletonList(input.stream().map(stack -> recipe.getOutput(stack)).collect(Collectors.toList())));
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
	{
		JEICompatPlugin.progressRight.draw(minecraft, 25, 15);
		int w = minecraft.fontRenderer.getStringWidth(typeString);
		minecraft.fontRenderer.drawString(typeString, (recipeWidth - w) / 2, 0, Color.gray.getRGB());
		w = minecraft.fontRenderer.getStringWidth(tempString);
		minecraft.fontRenderer.drawString(tempString, (recipeWidth - w) / 2, 48, Color.gray.getRGB());
	}
}