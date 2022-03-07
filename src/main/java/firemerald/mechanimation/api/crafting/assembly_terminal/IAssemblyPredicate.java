package firemerald.mechanimation.api.crafting.assembly_terminal;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@FunctionalInterface
public interface IAssemblyPredicate
{
	public boolean isPartialInputValid(World world, ItemStack blueprint, int tier, ItemStack... existingInputs);
}