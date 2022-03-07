package firemerald.mechanimation.compat.tconstruct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.fluid_reactor.IFluidReactorRecipe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.smeltery.AlloyRecipe;

public class FluidReactorRecipeAlloy implements IFluidReactorRecipe
{
	public final AlloyRecipe recipe;

	public FluidReactorRecipeAlloy(AlloyRecipe recipe)
	{
		this.recipe = recipe;
	}

	@Override
	public ItemStack[] getItemInput()
	{
		return new ItemStack[0];
	}

	@Override
	public Object getTrueItemInput()
	{
		return null;
	}

	@Override
	public boolean isInputValid(ItemStack item, FluidOrGasStack fluid1, FluidOrGasStack fluid2, FluidOrGasStack fluid3)
	{
		if (item != null && !item.isEmpty()) return false;
		List<FluidStack> fluids = recipe.getFluids();
		if ((fluid1 != null && !fluids.stream().anyMatch(fluid1::isFluidEqual)) || (fluid2 != null && !fluids.stream().anyMatch(fluid2::isFluidEqual))) return false;
		if (fluid3 != null && !fluids.stream().anyMatch(fluid3::isFluidEqual)) return false;
		return true;
	}

	@Override
	public boolean isInputValidComplete(ItemStack item, FluidOrGasStack fluid1, FluidOrGasStack fluid2, FluidOrGasStack fluid3)
	{
		if ((fluid1 != null && !fluid1.isFluid()) || (fluid2 != null && !fluid2.isFluid()) || (fluid3 != null && !fluid3.isFluid())) return true;
		List<FluidStack> fluids = new ArrayList<>();
		if (fluid1 != null) fluids.add(fluid1.getFluidStack());
		if (fluid2 != null) fluids.add(fluid2.getFluidStack());
		if (fluid3 != null) fluids.add(fluid3.getFluidStack());
		return recipe.matches(fluids) > 0;
	}

	@Override
	public int getRequiredCount(ItemStack item, FluidOrGasStack fluid1, FluidOrGasStack fluid2, FluidOrGasStack fluid3)
	{
		return 0;
	}

	@Override
	public List<FluidOrGasStack> getFluidInputPrimary()
	{
		return recipe.getFluids().size() > 0 ? Collections.singletonList(FluidOrGasStack.forFluid(recipe.getFluids().get(0))) : Collections.emptyList();
	}

	@Override
	public List<FluidOrGasStack> getFluidInputSecondary()
	{
		return recipe.getFluids().size() > 1 ? Collections.singletonList(FluidOrGasStack.forFluid(recipe.getFluids().get(1))) : Collections.emptyList();
	}

	@Override
	public List<FluidOrGasStack> getFluidInputTertiary()
	{
		return recipe.getFluids().size() > 2 ? Collections.singletonList(FluidOrGasStack.forFluid(recipe.getFluids().get(2))) : Collections.emptyList();
	}

	@Override
	public ItemStack getItemOutput()
	{
		return ItemStack.EMPTY;
	}

	@Override
	public FluidOrGasStack getFluidOutput()
	{
		return FluidOrGasStack.forFluid(recipe.getResult());
	}

	@Override
	public float getRequiredSeconds()
	{
		int maxProcess = recipe.getResult().amount;
		List<FluidStack> inputs = recipe.getFluids();
		if (inputs.size() > 0)
		{
			maxProcess = Math.max(maxProcess, inputs.get(0).amount);
			if (inputs.size() > 1)
			{
				maxProcess = Math.max(maxProcess, inputs.get(1).amount);
				if (inputs.size() > 2)
				{
					maxProcess = Math.max(maxProcess, inputs.get(2).amount);
				}
			}
		}
		return maxProcess * .001f;
	}
}