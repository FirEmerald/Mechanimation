package firemerald.mechanimation.api.crafting.assembly_terminal;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IAssemblyRecipe extends IAssemblyBase
{
	public ItemStack getDefaultOutput();

	public int getDefaultUsedFlux();

	public ItemStack[][] getDefaultInput(ItemStack blueprint, int tier);

	public boolean isInputValid(World world, ItemStack blueprint, int tier, ItemStack... inputs);

	public ItemStack process(World world, ItemStack blueprint, int tier, boolean simulate, ItemStack... items);

	public int requiredFlux(World world, ItemStack blueprint, int tier, ItemStack... inputs);
}