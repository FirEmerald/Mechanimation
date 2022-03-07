package firemerald.mechanimation.compat.tconstruct;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.smeltery.ICastingRecipe;

public class CastingRecipeCasting implements firemerald.mechanimation.api.crafting.casting.ICastingRecipe
{
	public final ItemStack cast;
	public final ICastingRecipe recipe;

	public CastingRecipeCasting(ItemStack cast, ICastingRecipe recipe)
	{
		this.cast = cast;
		this.recipe = recipe;
	}

	@Override
	public Collection<FluidOrGasStack> getInput()
	{
		return FluidRegistry.getRegisteredFluids().values().stream().filter(fluid -> recipe.matches(cast, fluid)).map(fluid -> FluidOrGasStack.forFluid(new FluidStack(fluid, recipe.getFluidAmount()))).collect(Collectors.toSet());
	}

	@Override
	public boolean isInputValid(FluidOrGasStack stack)
	{
		return stack.isFluid() && recipe.matches(cast, stack.getFluidStack().getFluid());
	}

	@Override
	public ItemStack getOutput()
	{
		return FluidRegistry.getRegisteredFluids().values().stream().filter(fluid -> recipe.matches(cast, fluid)).map(fluid -> recipe.getResult(cast, fluid)).findFirst().orElse(ItemStack.EMPTY);
	}

	@Override
	public ItemStack getOutput(FluidOrGasStack stack)
	{
		if (stack.isFluid() && recipe.matches(cast, stack.getFluidStack().getFluid())) return recipe.getResult(cast, stack.getFluidStack().getFluid());
		else return ItemStack.EMPTY;
	}

	@Override
	public int getTemperature()
	{
		try
		{
			Fluid f = FluidRegistry.getRegisteredFluids().values().stream().filter(fluid -> recipe.matches(cast, fluid)).findFirst().get();
			return f.getTemperature(new FluidStack(f, recipe.getFluidAmount())) - 300;
		}
		catch (NoSuchElementException e)
		{
			return 0;
		}
	}

	@Override
	public int getTemperature(FluidOrGasStack stack)
	{
		if (stack.isFluid() && recipe.matches(cast, stack.getFluidStack().getFluid())) return stack.getFluidStack().getFluid().getTemperature(stack.getFluidStack()) - 300;
		else return 0;
	}

	@Override
	public int getCooledTemperature()
	{
		return 40;
	}
}