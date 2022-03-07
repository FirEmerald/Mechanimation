package firemerald.mechanimation.api.crafting.casting;

import java.util.List;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import net.minecraft.item.ItemStack;

public class CastingRecipe implements ICastingRecipe
{
	public final List<FluidOrGasStack> input;
	public final ItemStack output;
	public final int temperature, cooledTemperature;

	public CastingRecipe(List<FluidOrGasStack> input, ItemStack output, int temperature, int cooledTemperature)
	{
		this.input = input;
		this.output = output.copy();
		this.temperature = temperature;
		this.cooledTemperature = cooledTemperature;
	}

	@Override
	public List<FluidOrGasStack> getInput()
	{
		return input;
	}

	@Override
	public ItemStack getOutput()
	{
		return output;
	}

	@Override
	public int getTemperature()
	{
		return temperature;
	}

	@Override
	public int getCooledTemperature()
	{
		return cooledTemperature;
	}

	@Override
	public boolean isInputValid(FluidOrGasStack fluid)
	{
		if (fluid == null) return false;
		return this.input.parallelStream().anyMatch(stack -> stack.isFluidEqual(fluid));
	}
}