package firemerald.mechanimation.api.crafting.claus_plant;

import java.util.List;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import net.minecraft.item.ItemStack;

public interface IClausPlantRecipe
{
	public boolean isInputValid(FluidOrGasStack fluid);

	public List<FluidOrGasStack> getFluidInput();

	public ItemStack getItemOutput();

	public int getRequiredOxygen();
}