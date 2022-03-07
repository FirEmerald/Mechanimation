package firemerald.mechanimation.api.crafting.casting;

import java.util.Collection;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import net.minecraft.item.ItemStack;

public interface ICastingRecipe
{
	public Collection<FluidOrGasStack> getInput();

	public boolean isInputValid(FluidOrGasStack stack);

	public ItemStack getOutput();

	public default ItemStack getOutput(FluidOrGasStack stack)
	{
		return getOutput();
	}

	public int getTemperature();

	public default int getTemperature(FluidOrGasStack stack)
	{
		return getTemperature();
	}

	public int getCooledTemperature();

	public default int getCooledTemperature(FluidOrGasStack stack)
	{
		return getCooledTemperature();
	}
}
