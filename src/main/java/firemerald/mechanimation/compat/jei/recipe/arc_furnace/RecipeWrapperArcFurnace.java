package firemerald.mechanimation.compat.jei.recipe.arc_furnace;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import firemerald.api.core.client.Translator;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.arc_furnace.IArcFurnaceRecipe;
import firemerald.mechanimation.compat.jei.JEICompatPlugin;
import firemerald.mechanimation.compat.jei.recipe.RecipeWrapperBase;
import firemerald.mechanimation.init.MechanimationFluids;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class RecipeWrapperArcFurnace extends RecipeWrapperBase
{
	protected String uId;
	private final IArcFurnaceRecipe recipe;
	private final String smeltTempString;

	public RecipeWrapperArcFurnace(IGuiHelper guiHelper, IArcFurnaceRecipe recipe, String uIdIn)
	{
		super(uIdIn);
		this.recipe = recipe;
		this.smeltTempString = Translator.format("gui.mechanimation.temp", recipe.getTemperature()); //TODO
	}

	@Override
	public String getUid()
	{
		return uId;
	}

	@Override
	public void getIngredients(IIngredients ingredients)
	{
		List<ItemStack> inputs = Arrays.asList(recipe.getInput());
		List<ItemStack> newInputs = new ArrayList<>();
		List<FluidOrGasStack> outputs = new ArrayList<>();
		for (ItemStack input : inputs)
		{
			FluidOrGasStack output;
			newInputs.add(input);
			outputs.add(output = recipe.getOutput(input));
			if (output.isFluid() && output.getFluidStack().getFluid() == MechanimationFluids.liquidIron) //steeling
			{
				newInputs.add(input);
				outputs.add(FluidOrGasStack.forFluid(new FluidStack(MechanimationFluids.liquidSteel, output.getAmount())));
			}
		}
		ingredients.setInputLists(VanillaTypes.ITEM, Collections.singletonList(newInputs));
		ingredients.setOutputLists(JEICompatPlugin.TYPE_FLUID, Collections.singletonList(outputs));
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
	{
		JEICompatPlugin.progressRight.draw(minecraft, 25, 15);
		minecraft.fontRenderer.drawString(smeltTempString, 0, 0, Color.gray.getRGB());
		//TODO if (recipe.isSteelingRecipe()) minecraft.fontRenderer.drawString(Translator.translate("gui.mechanimation.jei.arc_furnace.steeling"), 0, 34, Color.gray.getRGB());
	}
}