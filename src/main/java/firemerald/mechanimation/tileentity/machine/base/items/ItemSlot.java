package firemerald.mechanimation.tileentity.machine.base.items;

import java.util.function.Predicate;

import javax.annotation.Nullable;

import firemerald.mechanimation.util.EnumFace;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemHandlerHelper;

public class ItemSlot<T extends IItemMachine<T>> implements IItemSlot
{
	public final T machine;
	public final String name;
	public final int slot;
	public int maxCount, maxInsert, maxExtract;
	public final Predicate<ItemStack> canInsert, canExtract;
	private ItemStack item = ItemStack.EMPTY;

	public ItemSlot(T machine, String name, int slot, int maxCount, int maxInsert, Predicate<ItemStack> canInsert, int maxExtract, Predicate<ItemStack> canExtract)
	{
		this.machine = machine;
		this.name = name;
		this.slot = slot;
		this.maxCount = maxCount;
		this.maxInsert = maxInsert;
		this.canInsert = canInsert;
		this.canExtract = canExtract;
		this.maxExtract = maxExtract;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public int getSlot()
	{
		return slot;
	}

	@Override
	public ItemStack getStack()
	{
		return item;
	}

	@Override
	public void setStack(ItemStack stack)
	{
        if (stack.getCount() > this.getStackLimit()) stack.setCount(this.getStackLimit());
		this.machine.onInventorySlotChanged(slot, this.item = (stack.isEmpty() ? ItemStack.EMPTY : stack));
	}

	@Override
	public boolean canInsert(ItemStack stack, EnumFace side)
	{
		return (item.isEmpty() || ItemHandlerHelper.canItemStacksStack(item, stack)) && canInsert.test(stack);
	}

	@Override
	public boolean canExtract(ItemStack stack, EnumFace side)
	{
		return canExtract.test(stack);
	}

	@Override
	public boolean isEmpty()
	{
		return item.isEmpty();
	}

	@Override
	public ItemStack decrStackSize(int count)
	{
		ItemStack ret = item.splitStack(count);
		setStack(item);
		return ret;
	}

	@Override
	public ItemStack removeStackFromSlot()
	{
		ItemStack ret = item;
		setStack(ItemStack.EMPTY);
		return ret;
	}

	@Override
	public int getStackLimit()
	{
		return maxCount;
	}

	@Override
	public void clear()
	{
		setStack(ItemStack.EMPTY);
	}

	@Override
	public ItemStack insertItem(ItemStack stack, boolean simulate, @Nullable EnumFace side)
	{
		if (!this.canInsert(stack, side) || item.getCount() >= maxCount) return stack;
		int canInsert = Math.min(maxInsert, Math.min(stack.getCount(), maxCount - item.getCount())); //min(maxReceive, toInsert, available)
		if (canInsert <= 0) return stack;
		else
		{
			ItemStack remainder = stack.copy();
			remainder.setCount(remainder.getCount() - canInsert);
			if (!simulate)
			{
				int count;
				if (item.isEmpty())
				{
					item = stack.copy();
					count = 0;
				}
				else count = item.getCount();
				item.setCount(count + canInsert);
				setStack(item);
			}
			return remainder;
		}
	}

	@Override
	public ItemStack extractItem(int amount, boolean simulate, @Nullable EnumFace side)
	{
		if (item.isEmpty() || !this.canExtract(item, side)) return ItemStack.EMPTY;
		int canExtract = Math.min(maxExtract, Math.min(amount, item.getCount())); //min(maxExtract, toExtract, available)
		if (canExtract <= 0) return ItemStack.EMPTY;
		else if (!simulate)
		{
			ItemStack ret = item.splitStack(canExtract);
			setStack(item);
			return ret;
		}
		else
		{
			ItemStack ret = item.copy();
			ret.setCount(canExtract);
			return ret;
		}
	}

	@Override
	public void writeToShared(NBTTagCompound tag)
	{
		if (!item.isEmpty()) tag.setTag(name, item.writeToNBT(new NBTTagCompound()));
	}

	@Override
	public void readFromShared(NBTTagCompound tag)
	{
		this.clear();
		if (tag.hasKey(name, 10)) setStack(new ItemStack(tag.getCompoundTag(name)));
	}

	@Override
	public void writeToDisk(NBTTagCompound tag) {}

	@Override
	public void readFromDisk(NBTTagCompound tag) {}

	@Override
	public void setStackLimit(int maxCount)
	{
		if (this.maxCount != maxCount)
		{
			if (item.getCount() > maxCount) item.setCount(maxCount);
			this.maxCount = maxCount;
			machine.setNeedsUpdate();
		}
	}
}