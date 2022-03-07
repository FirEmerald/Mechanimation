package firemerald.mechanimation.compat.tconstruct;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.casting.ICastingRecipe;
import firemerald.mechanimation.api.crafting.casting.ICastingRecipes;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class CastingRecipesCasting implements ICastingRecipes
{
	public final Supplier<List<slimeknights.tconstruct.library.smeltery.ICastingRecipe>> recipes;
	public final ItemStack cast;

	public CastingRecipesCasting(Supplier<List<slimeknights.tconstruct.library.smeltery.ICastingRecipe>> recipes, ItemStack cast)
	{
		this.recipes = recipes;
		this.cast = cast;
	}

	@Override
	public ICastingRecipe getResult(FluidOrGasStack stack)
	{
		if (stack != null && stack.isFluid())
		{
			Fluid fluid = stack.getFluidStack().getFluid();
			return recipes.get().stream().filter(recipe -> recipe.matches(cast, fluid)).map(recipe -> new CastingRecipeCasting(cast, recipe)).findFirst().orElse(null);
		}
		else return null;
	}

	@Override
	public Collection<ICastingRecipe> getAllResults()
	{
		return recipes.get().stream().filter(recipe -> FluidRegistry.getRegisteredFluids().values().stream().anyMatch(fluid -> recipe.matches(cast, fluid))).map(recipe -> new CastingRecipeCasting(cast, recipe)).collect(Collectors.toSet());
	}

	@Override
	public void init() {}
}