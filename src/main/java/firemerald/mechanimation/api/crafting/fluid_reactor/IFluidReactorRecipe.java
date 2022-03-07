package firemerald.mechanimation.api.crafting.fluid_reactor;

import java.util.List;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import net.minecraft.item.ItemStack;

public interface IFluidReactorRecipe
{
	public ItemStack[] getItemInput();

	public Object getTrueItemInput();

	public boolean isInputValid(ItemStack item, FluidOrGasStack fluid1, FluidOrGasStack fluid2, FluidOrGasStack fluid3);

	public boolean isInputValidComplete(ItemStack item, FluidOrGasStack fluid1, FluidOrGasStack fluid2, FluidOrGasStack fluid3);

	public int getRequiredCount(ItemStack item, FluidOrGasStack fluid1, FluidOrGasStack fluid2, FluidOrGasStack fluid3);

	public List<FluidOrGasStack> getFluidInputPrimary();

	public List<FluidOrGasStack> getFluidInputSecondary();

	public List<FluidOrGasStack> getFluidInputTertiary();

	public ItemStack getItemOutput();

	public FluidOrGasStack getFluidOutput();

	public float getRequiredSeconds();
}