package firemerald.mechanimation.tileentity.machine.base.items;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import firemerald.mechanimation.tileentity.machine.base.ISlot;
import firemerald.mechanimation.util.EnumFace;
import net.minecraft.item.ItemStack;

public interface IItemSlot extends ISlot
{
	public boolean isEmpty();

	public ItemStack getStack();

	public ItemStack decrStackSize(int count);

	public ItemStack removeStackFromSlot();

	public void setStack(ItemStack stack);

	public int getStackLimit();

	public void setStackLimit(int limit);

	public boolean canInsert(ItemStack stack, @Nullable EnumFace side);

	public boolean canExtract(ItemStack stack, @Nullable EnumFace side);

    @Nonnull
    public ItemStack insertItem(@Nonnull ItemStack stack, boolean simulate, @Nullable EnumFace side);

    @Nonnull
    public ItemStack extractItem(int amount, boolean simulate, @Nullable EnumFace side);

    public void clear();

	public default void add(ItemStack add)
	{
		ItemStack has = getStack();
		if (has.isEmpty()) has = add.copy();
		else has.grow(add.getCount());
		setStack(has);
	}

	public default void remove(int remove)
	{
		ItemStack has = getStack();
		if (!has.isEmpty()) has.shrink(remove);
		if (has.isEmpty()) has = ItemStack.EMPTY;
		setStack(has);
	}
}