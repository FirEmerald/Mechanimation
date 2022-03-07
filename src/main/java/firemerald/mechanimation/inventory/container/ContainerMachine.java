package firemerald.mechanimation.inventory.container;

import firemerald.mechanimation.tileentity.machine.base.IGuiMachine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerMachine<T extends IGuiMachine<T>> extends Container
{
    public final T machine;
    protected int[] fields;
    public final int index_max_inventory, index_max_hotbar;
    public int index_max_machine;

    public ContainerMachine(InventoryPlayer playerInventory, T machine)
    {
    	this.machine = machine;
    	this.fields = new int[machine.getFieldCount()];
    	int invX = machine.inventoryOffsetX();
    	int invY = machine.inventoryOffsetY();
        for (int i = 0; i < 3; ++i) for (int j = 0; j < 9; ++j) this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, invX + j * 18, invY + i * 18));
        index_max_inventory = this.inventorySlots.size();
        for (int k = 0; k < 9; ++k) this.addSlotToContainer(new Slot(playerInventory, k, invX + k * 18, invY + 58));
        index_max_hotbar = this.inventorySlots.size();
    	machine.addItemSlotsToContainer(this);
    	index_max_machine = this.inventorySlots.size();
    }

    protected void removeLastSlotFromContainer() //TODO drop item
    {
    	this.inventorySlots.remove(this.inventorySlots.size() - 1);
    	this.inventoryItemStacks.remove(this.inventoryItemStacks.size() - 1);
    }

    @Override
    public Slot addSlotToContainer(Slot slotIn)
    {
    	return super.addSlotToContainer(slotIn);
    }

    @Override
	public void addListener(IContainerListener listener)
    {
        super.addListener(listener);
        if (this.machine instanceof IInventory) listener.sendAllWindowProperties(this, (IInventory) this.machine);
    }

    /**
     * Looks for changes made in the container, sends them to every listener.
     */
    @Override
	public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        listeners.forEach(listener -> {
            for (int i = 0; i < fields.length; i++) if (fields[i] != machine.getField(i)) listener.sendWindowProperty(this, i, machine.getField(i));
        });
        for (int i = 0; i < fields.length; i++) fields[i] = machine.getField(i);
    }

    @Override
	@SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data)
    {
        this.machine.setField(id, data);
    }

    /**
     * Determines whether supplied player can use this container
     */
    @Override
	public boolean canInteractWith(EntityPlayer playerIn)
    {
        return this.machine.isUsableByPlayer(playerIn);
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
    @Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        Slot slot = this.getSlot(index);
        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            ItemStack itemstack = itemstack1.copy();
            if (index < index_max_inventory) //player inventory
            {
                this.mergeItemStack(itemstack1, index_max_hotbar, index_max_machine, false); //transfer to machine
                this.mergeItemStack(itemstack1, index_max_inventory, index_max_hotbar, false); //transfer to hotbar
            }
            else if (index < index_max_hotbar) //player hotbar
            {
                this.mergeItemStack(itemstack1, index_max_hotbar, index_max_machine, false); //transfer to machine
                this.mergeItemStack(itemstack1, 0, index_max_inventory, false); //transfer to inventory
            }
            else //machine inventory
            {
                this.mergeItemStack(itemstack1, 0, index_max_hotbar, true); //transfer to hotbar or inventory
            }
            slot.onSlotChange(itemstack1, itemstack);
            if (itemstack1.getCount() == itemstack.getCount()) return ItemStack.EMPTY;
            if (itemstack1.isEmpty()) slot.putStack(ItemStack.EMPTY);
            slot.onSlotChanged();
            return itemstack;
        }
        else return ItemStack.EMPTY;
    }

    @Override
    public boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection)
    {
    	return super.mergeItemStack(stack, startIndex, endIndex, reverseDirection);
    }
}