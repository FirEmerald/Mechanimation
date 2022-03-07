package firemerald.mechanimation.api.crafting.fluid_reactor;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.IRecipes;
import net.minecraft.item.ItemStack;

public interface IFluidReactorRecipes extends IRecipes<IFluidReactorRecipe>
{
	public IFluidReactorRecipe getResult(ItemStack item, FluidOrGasStack fluid1, FluidOrGasStack fluid2, FluidOrGasStack fluid3);

	public boolean isInputValid(ItemStack item, FluidOrGasStack fluid1, FluidOrGasStack fluid2, FluidOrGasStack fluid3);
}