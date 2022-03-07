package firemerald.mechanimation.compat.jei.transfer;

import java.util.List;

import firemerald.mechanimation.inventory.container.ContainerMachine;
import firemerald.mechanimation.tileentity.machine.base.IGuiMachine;
import firemerald.mechanimation.tileentity.machine.base.items.IItemMachine;
import net.minecraft.inventory.Slot;

public interface IMachineRecipeTransferInfo<T extends IItemMachine<T> & IGuiMachine<T>>
{
	/**
	 * Return true if this recipe transfer info can handle the given container instance.
	 *
	 * @since JEI 4.0.2
	 */
	boolean canHandle(ContainerMachine<T> container);

	/**
	 * Return a list of slots for the recipe area.
	 */
	List<Slot> getRecipeSlots(ContainerMachine<T> container);

	/**
	 * Return a list of slots that the transfer can use to get items for crafting, or place leftover items.
	 */
	List<Slot> getInventorySlots(ContainerMachine<T> container);

	/**
	 * Return false if the recipe transfer should attempt to place as many items as possible for all slots, even if one slot has less.
	 */
	default boolean requireCompleteSets()
	{
		return true;
	}
}
