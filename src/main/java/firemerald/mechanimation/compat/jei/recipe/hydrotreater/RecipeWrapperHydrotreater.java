package firemerald.mechanimation.compat.jei.recipe.hydrotreater;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.hydrotreater.IHydrotreaterRecipe;
import firemerald.mechanimation.client.gui.inventory.IGuiElements;
import firemerald.mechanimation.compat.jei.JEICompatPlugin;
import firemerald.mechanimation.compat.jei.recipe.RecipeWrapperFluxReciever;
import firemerald.mechanimation.init.MechanimationFluids;
import firemerald.mechanimation.tileentity.machine.hydrotreater.TileEntityHydrotreater;
import mekanism.api.gas.GasStack;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;

public class RecipeWrapperHydrotreater extends RecipeWrapperFluxReciever
{
	/* Recipe */
    protected IHydrotreaterRecipe recipe;

	public RecipeWrapperHydrotreater(IGuiHelper guiHelper, IHydrotreaterRecipe result, String uIdIn)
	{
		super(uIdIn, 1, 1, IGuiElements.ENERGY_BAR_TEX);
		this.recipe = result;
	}

	@Override
	public void getIngredients(IIngredients ingredients)
	{
		List<List<FluidOrGasStack>> inputs = new ArrayList<>();
		inputs.add(recipe.getFluidInput());
		inputs.add(MechanimationFluids.forName("hydrogen", recipe.getRequiredHydrogen()));
		ingredients.setInputLists(JEICompatPlugin.TYPE_FLUID, inputs);
		List<List<FluidOrGasStack>> outputs = new ArrayList<>();
		outputs.add(Collections.singletonList(recipe.getFluidOutput()));
		outputs.add(Collections.singletonList(FluidOrGasStack.forGas(new GasStack(MechanimationFluids.sodium, recipe.getRequiredHydrogen()))));
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
		return recipe.getRequiredHydrogen() * TileEntityHydrotreater.FLUX_PER_HYDROGEN;
	}
}