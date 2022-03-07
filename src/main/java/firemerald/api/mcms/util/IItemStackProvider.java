package firemerald.api.mcms.util;

import net.minecraft.item.ItemStack;

public interface IItemStackProvider
{
	public ItemStack getItemStack(int index);
}