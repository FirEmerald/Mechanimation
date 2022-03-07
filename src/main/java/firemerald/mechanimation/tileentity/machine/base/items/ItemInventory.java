package firemerald.mechanimation.tileentity.machine.base.items;

import firemerald.mechanimation.tileentity.machine.base.SidedSlots;
import firemerald.mechanimation.util.EnumFace;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemInventory<T extends IItemMachine<T>> implements IItemInventory<T>
{
	protected IItemSlot[] slots;
	protected final int[] slotIds;
	protected int[][] sidedSlots;
	protected int biggestSlotSize;

	public ItemInventory(T machine, ItemSlotInfo<T>[] slots)
	{
		int biggestSlotSize = 0;
		this.slots = new IItemSlot[slots.length];
		this.slotIds = new int[slots.length];
		SidedSlots<IItemSlot> sidedSlots = new SidedSlots<>();
		for (int i = 0; i < slots.length; ++i)
		{
			ItemSlotInfo<T> info = slots[i];
			IItemSlot slot = this.slots[i] = info.makeSlot(machine);
			if (this.slots[i].getStackLimit() > biggestSlotSize) biggestSlotSize = this.slots[i].getStackLimit();
			sidedSlots.addSlot(slot, info.sides.apply(machine));
			slotIds[i] = slot.getSlot();
		}
		this.biggestSlotSize = biggestSlotSize;
		this.sidedSlots = sidedSlots.getIndexArray();
	}

	@Override
	public int getSlots()
	{
		return slots.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		IItemSlot itemSlot = getItemSlot(slot);
		if (itemSlot == null) return ItemStack.EMPTY;
		else return itemSlot.getStack();
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
	{
		IItemSlot itemSlot = getItemSlot(slot);
		if (itemSlot == null) return stack;
		else return itemSlot.insertItem(stack, simulate, null);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		IItemSlot itemSlot = getItemSlot(slot);
		if (itemSlot == null) return ItemStack.EMPTY;
		else return itemSlot.extractItem(amount, simulate, null);
	}

	@Override
	public int getSlotLimit(int slot)
	{
		IItemSlot itemSlot = getItemSlot(slot);
		if (itemSlot == null) return 0;
		else return itemSlot.getStackLimit();
	}

	@Override
	public boolean isEmpty()
	{
		for (IItemSlot slot : slots) if (!slot.isEmpty()) return false;
		return true;
	}

	@Override
	public int getInventoryLimit()
	{
		return biggestSlotSize;
	}

	@Override
	public ItemStack decrStackSize(int slot, int count)
	{
		IItemSlot itemSlot = getItemSlot(slot);
		if (itemSlot == null) return null;
		else return itemSlot.decrStackSize(count);
	}

	@Override
	public ItemStack removeStackFromSlot(int slot)
	{
		IItemSlot itemSlot = getItemSlot(slot);
		if (itemSlot == null) return null;
		else return itemSlot.removeStackFromSlot();
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack)
	{
		IItemSlot itemSlot = getItemSlot(slot);
		if (itemSlot == null) return false;
		else return itemSlot.canInsert(stack, null);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack)
	{
		IItemSlot itemSlot = getItemSlot(slot);
		if (itemSlot == null) return false;
		else return itemSlot.canExtract(stack, null);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack)
	{
		IItemSlot itemSlot = getItemSlot(slot);
		if (itemSlot != null) itemSlot.setStack(stack);
	}

	@Override
	public int[] getSlots(EnumFace face)
	{
		return face == null ? slotIds : sidedSlots[face.ordinal()];
	}

	@Override
	public ItemStack getStackInSlot(int index, EnumFace face)
	{
		IItemSlot slot = getItemSlot(index, face);
		if (slot == null) return ItemStack.EMPTY;
		else return slot.getStack();
	}

	@Override
	public boolean canInsertItem(int index, ItemStack stack, EnumFace face)
	{
		IItemSlot slot = getItemSlot(index, face);
		if (slot == null) return false;
		else return slot.canInsert(stack, face);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFace face)
	{
		IItemSlot slot = getItemSlot(index, face);
		if (slot == null) return false;
		else return slot.canExtract(stack, face);
	}

	@Override
	public ItemStack insertItem(int index, ItemStack stack, boolean simulate, EnumFace face)
	{
		IItemSlot slot = getItemSlot(index, face);
		if (slot == null) return stack;
		else return slot.insertItem(stack, simulate, face);
	}

	@Override
	public ItemStack extractItem(int index, int amount, boolean simulate, EnumFace face)
	{
		IItemSlot slot = getItemSlot(index, face);
		if (slot == null)return ItemStack.EMPTY;
		else return slot.extractItem(amount, simulate, face);
	}

	@Override
	public int getSlotLimit(int index, EnumFace face)
	{
		IItemSlot slot = getItemSlot(index, face);
		if (slot == null) return 0;
		else return slot.getStackLimit();
	}

	@Override
	public void clear()
	{
		for (IItemSlot slot : slots) slot.clear();
	}

	@Override
	public void writeToDisk(NBTTagCompound tag)
	{
		NBTTagCompound storage = tag.getCompoundTag("itemStorage");
		for (IItemSlot slot : slots) slot.writeToDisk(storage);
		tag.setTag("itemStorage", storage);
	}

	@Override
	public void readFromDisk(NBTTagCompound tag)
	{
		NBTTagCompound storage = tag.getCompoundTag("itemStorage");
		for (IItemSlot slot : slots) slot.readFromDisk(storage);
	}

	@Override
	public void writeToShared(NBTTagCompound tag)
	{
		NBTTagCompound storage = new NBTTagCompound();
		for (IItemSlot slot : slots) slot.writeToShared(storage);
		tag.setTag("itemStorage", storage);
	}

	@Override
	public void readFromShared(NBTTagCompound tag)
	{
		NBTTagCompound storage = tag.getCompoundTag("itemStorage");
		for (IItemSlot slot : slots) slot.readFromShared(storage);
	}

	@Override
	public IItemSlot getItemSlot(int slot)
	{
		if (slot < 0 || slot >= slots.length) return null;
		else return slots[slot];
	}

	public IItemSlot getItemSlot(int index, EnumFace face)
	{
		int[] slots = face == null ? slotIds : sidedSlots[face.ordinal()];
		if (index < 0 || index >= slots.length) return null;
		else return getItemSlot(slots[index]);
	}
}