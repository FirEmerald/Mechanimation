package firemerald.mechanimation.compat.thermalexpansion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cofh.thermalexpansion.util.managers.machine.CompactorManager;
import cofh.thermalexpansion.util.managers.machine.CompactorManager.CompactorRecipe;
import firemerald.mechanimation.api.crafting.press.IPressRecipe;
import firemerald.mechanimation.api.crafting.press.IPressRecipes;
import net.minecraft.item.ItemStack;

public class PressRecipesCompactor implements IPressRecipes
{
	@Override
	public IPressRecipe getResult(ItemStack primary)
	{
		CompactorRecipe recipe = CompactorManager.getRecipe(primary, CompactorManager.Mode.PLATE);
		if (recipe != null && !ThermalExpansionCompat.INSTANCE.addedCompactor.contains(recipe)) return new PressRecipeCompactor(recipe);
		else return null;
	}

	@Override
	public Collection<IPressRecipe> getAllResults()
	{
		List<IPressRecipe> list = new ArrayList<>();
		CompactorRecipe[] recipes = CompactorManager.getRecipeList(CompactorManager.Mode.PLATE);
		for (CompactorRecipe recipe : recipes) if (!ThermalExpansionCompat.INSTANCE.addedCompactor.contains(recipe)) list.add(new PressRecipeCompactor(recipe));
		return list;
	}

	@Override
	public void init() {}
}