package firemerald.mechanimation.api.crafting.assembly_terminal;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public interface IAssemblyBase extends IAssemblyPredicate
{
	public ResourceLocation getUniqueName();

	public ItemStack[] getDefaultBlueprint();

	public int getDefaultTier();

	public boolean isBlueprintValid(ItemStack blueprint, int tier);
}