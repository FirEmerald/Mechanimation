package firemerald.mechanimation.compat.tconstruct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.fluid_reactor.IFluidReactorRecipe;
import firemerald.mechanimation.api.crafting.fluid_reactor.IFluidReactorRecipes;
import firemerald.mechanimation.tileentity.machine.fluid_reactor.TileEntityFluidReactorAdvanced;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.smeltery.AlloyRecipe;

public class FluidReactorRecipesAlloy implements IFluidReactorRecipes
{
	@Override
	public IFluidReactorRecipe getResult(ItemStack item, FluidOrGasStack fluid1, FluidOrGasStack fluid2, FluidOrGasStack fluid3)
	{
		if ((fluid1 != null && !fluid1.isFluid()) || (fluid2 != null && !fluid2.isFluid()) || (fluid3 != null && !fluid3.isFluid())) return null;
		List<FluidStack> fluids = new ArrayList<>();
		if (fluid1 != null) fluids.add(fluid1.getFluidStack());
		if (fluid2 != null) fluids.add(fluid2.getFluidStack());
		if (fluid3 != null) fluids.add(fluid3.getFluidStack());
		return TinkerRegistry.getAlloys().stream().filter(FluidReactorRecipesAlloy::isRecipeValid).filter(recipe -> recipe.matches(fluids) > 0).map(FluidReactorRecipeAlloy::new).findFirst().orElse(null);
	}

	@Override
	public boolean isInputValid(ItemStack item, FluidOrGasStack fluid1, FluidOrGasStack fluid2, FluidOrGasStack fluid3)
	{
		if (item != null && !item.isEmpty()) return false;
		if ((fluid1 != null && !fluid1.isFluid()) || (fluid2 != null && !fluid2.isFluid()) || (fluid3 != null && !fluid3.isFluid())) return false;
		List<FluidStack> fluids = new ArrayList<>();
		if (fluid1 != null) fluids.add(fluid1.getFluidStack());
		if (fluid2 != null) fluids.add(fluid2.getFluidStack());
		if (fluid3 != null) fluids.add(fluid3.getFluidStack());
		return TinkerRegistry.getAlloys().stream().filter(FluidReactorRecipesAlloy::isRecipeValid).anyMatch(recipe -> {
			List<FluidStack> required = new ArrayList<>(recipe.getFluids());
			return fluids.stream().allMatch(fluid -> {
				int i;
				for (i = 0; i < required.size(); i++)
				{
					FluidStack stack = required.get(i);
					if (stack.isFluidEqual(fluid))
					{
						required.remove(i);
						return true;
					}
				}
				return false;
			});
		});
	}

	@Override
	public Collection<FluidReactorRecipeAlloy> getAllResults()
	{
		return TinkerRegistry.getAlloys().stream().filter(FluidReactorRecipesAlloy::isRecipeValid).map(FluidReactorRecipeAlloy::new).collect(Collectors.toList());
	}

	public static boolean isRecipeValid(AlloyRecipe recipe)
	{
		if (recipe.getResult() == null || recipe.getResult().amount <= 0 || recipe.getResult().amount > TileEntityFluidReactorAdvanced.TANK_VOLUME) return false;
		List<FluidStack> inputs = recipe.getFluids();
		if (inputs.isEmpty() || inputs.size() > 3) return false;
		for (FluidStack input : inputs) if (input != null && input.amount > TileEntityFluidReactorAdvanced.TANK_VOLUME) return false;
		return true;
	}

	@Override
	public void init() {}
}