package firemerald.mechanimation.compat.jei.recipe.merox_treater;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.merox_treater.IMeroxTreaterRecipe;
import firemerald.mechanimation.client.gui.inventory.IGuiElements;
import firemerald.mechanimation.compat.jei.JEICompatPlugin;
import firemerald.mechanimation.compat.jei.recipe.RecipeWrapperFluxReciever;
import firemerald.mechanimation.init.MechanimationFluids;
import firemerald.mechanimation.tileentity.machine.merox_treater.TileEntityMeroxTreater;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class RecipeWrapperMeroxTreater extends RecipeWrapperFluxReciever
{
	/* Recipe */
    protected IMeroxTreaterRecipe recipe;

	public RecipeWrapperMeroxTreater(IGuiHelper guiHelper, IMeroxTreaterRecipe result, String uIdIn)
	{
		super(uIdIn, 1, 1, IGuiElements.ENERGY_BAR_TEX);
		this.recipe = result;
	}

	@Override
	public String getUid()
	{
		return uId;
	}

	@Override
	public void getIngredients(IIngredients ingredients)
	{
		List<List<FluidOrGasStack>> inputs = new ArrayList<>();
		inputs.add(recipe.getFluidInput());
		inputs.add(MechanimationFluids.forName("oxygen", recipe.getRequiredOxygen()));
		ingredients.setInputLists(JEICompatPlugin.TYPE_FLUID, inputs);
		List<List<FluidOrGasStack>> outputs = new ArrayList<>();
		outputs.add(Collections.singletonList(recipe.getFluidOutput()));
		outputs.add(Collections.singletonList(FluidOrGasStack.forFluid(new FluidStack(FluidRegistry.WATER, 2 * recipe.getRequiredOxygen()))));
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
		return recipe.getRequiredOxygen() * TileEntityMeroxTreater.FLUX_PER_OXYGEN;
	}
}