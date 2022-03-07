package firemerald.mechanimation.tileentity.machine.base.items;

import firemerald.mechanimation.util.EnumFace;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class ItemCapWrapper<T extends IItemMachine<T>> implements IItemHandler
{
	public final T machine;
	public final EnumFace face;

	public ItemCapWrapper(T machine, EnumFace face)
	{
		this.machine = machine;
		this.face = face;
	}

	@Override
	public int getSlots()
	{
		return machine.getItemInventory().getSlots(face).length;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return machine.getItemInventory().getStackInSlot(slot, face);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
	{
		return machine.getItemInventory().insertItem(slot, stack, simulate, face);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		return machine.getItemInventory().extractItem(slot, amount, simulate, face);
	}

	@Override
	public int getSlotLimit(int slot)
	{
		return machine.getItemInventory().getSlotLimit(slot, face);
	}

}