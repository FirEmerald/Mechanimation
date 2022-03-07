package firemerald.mechanimation.tileentity.machine.base.items;

import javax.annotation.Nonnull;

import firemerald.mechanimation.tileentity.machine.base.ISaved;
import firemerald.mechanimation.util.EnumFace;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public interface IItemInventory<T extends IItemMachine<T>> extends IItemHandler, ISaved
{
	public IItemSlot getItemSlot(int slot);

	public boolean isEmpty();

	public int getInventoryLimit();

	public ItemStack decrStackSize(int slot, int count);

	public ItemStack removeStackFromSlot(int slot);

	public boolean canInsertItem(int slot, ItemStack stack);

	public boolean canExtractItem(int slot, ItemStack stack);

	@Override
    public default boolean isItemValid(int slot, @Nonnull ItemStack stack)
	{
		return canInsertItem(slot, stack);
	}

	public void setInventorySlotContents(int index, ItemStack stack);

	public int[] getSlots(EnumFace face);

	public ItemStack getStackInSlot(int index, EnumFace face);

	public boolean canInsertItem(int index, ItemStack stack, EnumFace face);

	public boolean canExtractItem(int index, ItemStack stack, EnumFace face);

    public ItemStack insertItem(int index, @Nonnull ItemStack stack, boolean simulate, EnumFace face);

    public ItemStack extractItem(int index, int amount, boolean simulate, EnumFace face);

    public int getSlotLimit(int index, EnumFace face);

    public default boolean isItemValid(int slot, @Nonnull ItemStack stack, EnumFace face)
	{
		return canInsertItem(slot, stack, face);
	}

	public void clear();
}