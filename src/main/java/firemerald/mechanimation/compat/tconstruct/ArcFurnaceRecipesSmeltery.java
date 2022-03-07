package firemerald.mechanimation.compat.tconstruct;

import java.util.Collection;
import java.util.stream.Collectors;

import firemerald.mechanimation.api.crafting.arc_furnace.IArcFurnaceRecipe;
import firemerald.mechanimation.api.crafting.arc_furnace.IArcFurnaceRecipes;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;

public class ArcFurnaceRecipesSmeltery implements IArcFurnaceRecipes
{
	@Override
	public IArcFurnaceRecipe getResult(ItemStack stack)
	{
		MeltingRecipe recipe = TinkerRegistry.getMelting(stack);
		return recipe == null ? null : new ArcFurnaceRecipeSmeltery(recipe);
	}

	@Override
	public Collection<IArcFurnaceRecipe> getAllResults()
	{
		return TinkerRegistry.getAllMeltingRecipies().stream().map(ArcFurnaceRecipeSmeltery::new).collect(Collectors.toList());
	}

	@Override
	public void init() {}
}