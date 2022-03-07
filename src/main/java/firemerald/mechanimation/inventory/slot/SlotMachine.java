package firemerald.mechanimation.inventory.slot;

import firemerald.mechanimation.tileentity.machine.base.items.IItemMachine;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotMachine<T extends IItemMachine<T>> extends Slot
{
	public final T machine;

    public SlotMachine(T machine, int slotIndex, int xPosition, int yPosition)
    {
        super(machine, slotIndex, xPosition, yPosition);
        this.machine = machine;
    }

    /**
     * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
     */
    @Override
	public boolean isItemValid(ItemStack stack)
    {
		return machine.isItemValidForSlot(getSlotIndex(), stack);
    }
}