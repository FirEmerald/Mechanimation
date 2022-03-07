package firemerald.mechanimation.api.crafting.arc_furnace;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import net.minecraft.item.ItemStack;

public interface IArcFurnaceRecipe
{
	public ItemStack[] getInput();

	public Object getTrueInput();

	public boolean isInputValid(ItemStack stack);

	public int getRequiredCount(ItemStack stack);

	public FluidOrGasStack getOutput();

	public default FluidOrGasStack getOutput(ItemStack stack)
	{
		return getOutput();
	}

	public int getTemperature();

	public default int getTemperature(ItemStack stack)
	{
		return getTemperature();
	}

	public int getVolume();

	public default int getVolume(ItemStack stack)
	{
		return getVolume();
	}

	public default boolean isToolMeltingRecipe()
	{
		return false;
	}
}
