package firemerald.mechanimation.api.crafting.claus_plant;

import java.util.List;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import net.minecraft.item.ItemStack;

public class ClausPlantRecipe implements IClausPlantRecipe
{
	public final List<FluidOrGasStack> inputFluid;
	public final ItemStack outputItem;
	public final int requiredOxygen;

	public ClausPlantRecipe(List<FluidOrGasStack> inputFluid, ItemStack outputItem, int requiredOxygen)
	{
		this.inputFluid = inputFluid;
		this.outputItem = outputItem;
		this.requiredOxygen = requiredOxygen;
	}

	@Override
	public boolean isInputValid(FluidOrGasStack fluid)
	{
		if (fluid == null) return false;
		for (FluidOrGasStack stack : this.inputFluid) if (stack.isFluidEqual(fluid)) return true;
		return false;
	}

	@Override
	public List<FluidOrGasStack> getFluidInput()
	{
		return inputFluid;
	}

	@Override
	public ItemStack getItemOutput()
	{
		return outputItem;
	}

	@Override
	public int getRequiredOxygen()
	{
		return requiredOxygen;
	}
}