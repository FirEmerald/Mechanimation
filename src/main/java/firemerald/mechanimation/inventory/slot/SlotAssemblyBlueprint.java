package firemerald.mechanimation.inventory.slot;

import firemerald.mechanimation.inventory.container.ContainerMachine;
import firemerald.mechanimation.tileentity.machine.assembly_terminal.TileEntityAssemblyTerminalBase;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SlotAssemblyBlueprint<T extends TileEntityAssemblyTerminalBase<T>> extends SlotMachine<T>
{
	public final ContainerMachine<T> container;
	public final Slot outputSlot;

	public SlotAssemblyBlueprint(T machine, ContainerMachine<T> container, Slot outputSlot, int slotIndex, int xPosition, int yPosition)
	{
		super(machine, slotIndex, xPosition, yPosition);
		this.container = container;
		this.outputSlot = outputSlot;
	}

	@Override
    public void onSlotChanged()
    {
		super.onSlotChanged();
		World world = machine.getWorld();
		BlockPos pos = machine.getPos();
		double x = pos.getX();
		double y = pos.getY();
		double z = pos.getZ();
		machine.onBlueprintUpdate(this.getStack(), world.isRemote ? stack -> {} : stack -> {
			container.mergeItemStack(stack, 0, container.index_max_hotbar, true);
			if (!stack.isEmpty()) InventoryHelper.spawnItemStack(world, x, y, z, stack);
		});
		machine.updateItemSlotsInContainer(container, outputSlot);
    }
}