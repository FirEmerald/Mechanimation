package firemerald.mechanimation.compat.jei.recipe.claus_plant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.claus_plant.IClausPlantRecipe;
import firemerald.mechanimation.client.gui.inventory.IGuiElements;
import firemerald.mechanimation.compat.jei.JEICompatPlugin;
import firemerald.mechanimation.compat.jei.recipe.RecipeWrapperFluxReciever;
import firemerald.mechanimation.init.MechanimationFluids;
import firemerald.mechanimation.tileentity.machine.claus_plant.TileEntityClausPlant;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class RecipeWrapperClausPlant extends RecipeWrapperFluxReciever
{
	/* Recipe */
    protected IClausPlantRecipe recipe;

	public RecipeWrapperClausPlant(IGuiHelper guiHelper, IClausPlantRecipe result, String uIdIn)
	{
		super(uIdIn, 1, 1, IGuiElements.ENERGY_BAR_TEX);
		this.recipe = result;
	}

	@Override
	public void getIngredients(IIngredients ingredients)
	{
		List<List<FluidOrGasStack>> inputs = new ArrayList<>();
		inputs.add(recipe.getFluidInput());
		inputs.add(MechanimationFluids.forName("oxygen", recipe.getRequiredOxygen()));
		ingredients.setInputLists(JEICompatPlugin.TYPE_FLUID, inputs);
		List<List<ItemStack>> itemOutputs = new ArrayList<>();
		itemOutputs.add(Collections.singletonList(recipe.getItemOutput()));
		ingredients.setOutputLists(VanillaTypes.ITEM, itemOutputs);
		List<List<FluidOrGasStack>> fluidOutputs = new ArrayList<>();
		fluidOutputs.add(Collections.singletonList(FluidOrGasStack.forFluid(new FluidStack(FluidRegistry.WATER, 2 * recipe.getRequiredOxygen()))));
		ingredients.setOutputLists(JEICompatPlugin.TYPE_FLUID, fluidOutputs);
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
	{
		JEICompatPlugin.energyMeter.draw(minecraft, 1, 1);
	}

	@Override
	public int getRFUsed()
	{
		return recipe.getRequiredOxygen() * TileEntityClausPlant.FLUX_PER_OXYGEN;
	}
}