package firemerald.mechanimation.tileentity.machine.base.items;

import firemerald.mechanimation.tileentity.machine.base.IMachine;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public interface IItemMachine<T extends IItemMachine<T>> extends IMachine, ISidedInventory
{
	public IItemInventory<T> getItemInventory();

	@Override
	public default int[] getSlotsForFace(EnumFacing side)
	{
		return getItemInventory().getSlots(getFace(side));
	}

	@Override
	public default boolean canInsertItem(int index, ItemStack stack, EnumFacing side)
	{
		return getItemInventory().canInsertItem(index, stack, getFace(side));
	}

	@Override
	public default boolean canExtractItem(int index, ItemStack stack, EnumFacing side)
	{
		return getItemInventory().canExtractItem(index, stack, getFace(side));
	}

	public default ItemStack insertItem(int index, ItemStack stack, boolean simulate, EnumFacing side)
	{
		return getItemInventory().insertItem(index, stack, simulate, getFace(side));
	}

	public default ItemStack extractItem(int index, int amount, boolean simulate, EnumFacing side)
	{
		return getItemInventory().extractItem(index, amount, simulate, getFace(side));
	}

	@Override
	public default int getSizeInventory()
	{
		return getItemInventory().getInventoryLimit();
	}

	@Override
	public default boolean isEmpty()
	{
		return getItemInventory().isEmpty();
	}

	@Override
	public default ItemStack getStackInSlot(int index)
	{
		return getItemInventory().getStackInSlot(index);
	}

	@Override
	public default ItemStack decrStackSize(int index, int count)
	{
		return getItemInventory().decrStackSize(index, count);
	}

	@Override
	public default ItemStack removeStackFromSlot(int index)
	{
		return getItemInventory().removeStackFromSlot(index);
	}

	@Override
	public default void setInventorySlotContents(int index, ItemStack stack)
	{
		getItemInventory().setInventorySlotContents(index, stack);
	}

	public default void onInventorySlotChanged(int slot, ItemStack itemStack)
	{
		this.markDirty();
		this.setNeedsUpdate();
	}

	@Override
	public default int getInventoryStackLimit()
	{
		return getItemInventory().getInventoryLimit();
	}

	@Override
	public default boolean isItemValidForSlot(int index, ItemStack stack)
	{
		return canInsertItem(index, stack, null);
	}
}