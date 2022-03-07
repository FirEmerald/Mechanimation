package firemerald.mechanimation.compat.thermalexpansion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cofh.thermalexpansion.util.managers.machine.PulverizerManager;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager.PulverizerRecipe;
import firemerald.mechanimation.api.crafting.pulverizer.IPulverizerRecipe;
import firemerald.mechanimation.api.crafting.pulverizer.IPulverizerRecipes;
import net.minecraft.item.ItemStack;

public class PulverizerRecipesPulverizer implements IPulverizerRecipes
{
	@Override
	public IPulverizerRecipe getResult(ItemStack stack)
	{
		PulverizerRecipe recipe = PulverizerManager.getRecipe(stack);
		if (recipe != null && !ThermalExpansionCompat.INSTANCE.addedPulverizer.contains(recipe)) return new PulverizerRecipePulverizer(recipe);
		else return null;
	}

	@Override
	public Collection<IPulverizerRecipe> getAllResults()
	{
		List<IPulverizerRecipe> list = new ArrayList<>();
		PulverizerRecipe[] recipes = PulverizerManager.getRecipeList();
		for (PulverizerRecipe recipe : recipes) if (!ThermalExpansionCompat.INSTANCE.addedPulverizer.contains(recipe)) list.add(new PulverizerRecipePulverizer(recipe));
		return list;
	}

	@Override
	public void init() {}
}