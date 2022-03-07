package firemerald.mechanimation.tileentity.machine.base.items;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import firemerald.mechanimation.util.EnumFace;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemView<T extends IItemMachine<T>> implements IItemInventory<T>
{
	@Nullable
	public IItemInventory<T> viewing;
	protected int[] unsidedSlots = new int[0];
	protected int[][] sidedSlots = new int[EnumFace.values().length][0];

	public void setSidedSlots(int[][] sidedSlots)
	{
		this.sidedSlots = sidedSlots;
		List<Integer> allSlots = new ArrayList<>();
		for (int[] slots : sidedSlots) {
			for (int slot : slots)
				if (!allSlots.contains(slot)) allSlots.add(slot);
		}
		unsidedSlots = new int[allSlots.size()];
		for (int i = 0; i < unsidedSlots.length; ++i) unsidedSlots[i] = allSlots.get(i);
	}

	@Override
	public int getSlots()
	{
		return viewing == null ? 0 : viewing.getSlots();
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return viewing == null ? ItemStack.EMPTY : viewing.getStackInSlot(slot);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
	{
		return viewing == null ? stack : viewing.insertItem(slot, stack, simulate);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		return viewing == null ? ItemStack.EMPTY : viewing.extractItem(slot, amount, simulate);
	}

	@Override
	public int getSlotLimit(int slot)
	{
		return viewing == null ? 0 : viewing.getSlotLimit(slot);
	}

	@Override
	public void writeToShared(NBTTagCompound tag) {}

	@Override
	public void readFromShared(NBTTagCompound tag) {}

	@Override
	public void writeToDisk(NBTTagCompound tag) {}

	@Override
	public void readFromDisk(NBTTagCompound tag) {}

	@Override
	public IItemSlot getItemSlot(int slot)
	{
		return viewing == null ? null : viewing.getItemSlot(slot);
	}

	@Override
	public boolean isEmpty()
	{
		return viewing == null ? true : viewing.isEmpty();
	}

	@Override
	public int getInventoryLimit()
	{
		return viewing == null ? 0 : viewing.getInventoryLimit();
	}

	@Override
	public ItemStack decrStackSize(int slot, int count)
	{
		return viewing == null ? ItemStack.EMPTY : viewing.decrStackSize(slot, count);
	}

	@Override
	public ItemStack removeStackFromSlot(int slot)
	{
		return viewing == null ? ItemStack.EMPTY : viewing.removeStackFromSlot(slot);
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack)
	{
		return viewing == null ? false : viewing.canExtractItem(slot, stack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack)
	{
		return viewing == null ? false : viewing.canExtractItem(slot, stack);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack)
	{
		if (viewing != null) viewing.setInventorySlotContents(index, stack);
	}

	@Override
	public int[] getSlots(EnumFace face)
	{
		return viewing == null ? new int[0] : face == null ? unsidedSlots : sidedSlots[face.ordinal()];
	}

	@Override
	public ItemStack getStackInSlot(int index, EnumFace face)
	{
		if (viewing == null) return ItemStack.EMPTY;
		int[] slots = face == null ? unsidedSlots : sidedSlots[face.ordinal()];
		if (index < 0 || index >= slots.length) return ItemStack.EMPTY;
		else return viewing.getStackInSlot(slots[index]);
	}

	@Override
	public boolean canInsertItem(int index, ItemStack stack, EnumFace face)
	{
		if (viewing == null) return false;
		int[] slots = face == null ? unsidedSlots : sidedSlots[face.ordinal()];
		if (index < 0 || index >= slots.length) return false;
		else return viewing.canInsertItem(slots[index], stack);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFace face)
	{
		if (viewing == null) return false;
		int[] slots = face == null ? unsidedSlots : sidedSlots[face.ordinal()];
		if (index < 0 || index >= slots.length) return false;
		else return viewing.canExtractItem(slots[index], stack);
	}

	@Override
	public ItemStack insertItem(int index, ItemStack stack, boolean simulate, EnumFace face)
	{
		if (viewing == null) return stack;
		int[] slots = face == null ? unsidedSlots : sidedSlots[face.ordinal()];
		if (index < 0 || index >= slots.length) return stack;
		else return viewing.insertItem(slots[index], stack, simulate);
	}

	@Override
	public ItemStack extractItem(int index, int amount, boolean simulate, EnumFace face)
	{
		if (viewing == null) return ItemStack.EMPTY;
		int[] slots = face == null ? unsidedSlots : sidedSlots[face.ordinal()];
		if (index < 0 || index >= slots.length) return ItemStack.EMPTY;
		else return viewing.extractItem(slots[index], amount, simulate);
	}

	@Override
	public int getSlotLimit(int index, EnumFace face)
	{
		if (viewing == null) return 0;
		int[] slots = face == null ? unsidedSlots : sidedSlots[face.ordinal()];
		if (index < 0 || index >= slots.length) return 0;
		else return viewing.getSlotLimit(slots[index]);
	}

	@Override
	public void clear() {}
}