package firemerald.mechanimation.compat.jei.transfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import firemerald.mechanimation.inventory.container.ContainerMachine;
import firemerald.mechanimation.tileentity.machine.base.IGuiMachine;
import firemerald.mechanimation.tileentity.machine.base.items.IItemMachine;
import net.minecraft.inventory.Slot;

public class BasicMachineRecipeTransferInfo<T extends IItemMachine<T> & IGuiMachine<T>> implements IMachineRecipeTransferInfo<T>
{
	private final int recipeStartOffset;
	private final int recipeEndOffset;

	public BasicMachineRecipeTransferInfo(int recipeStartOffset, int recipeEndOffset)
	{
		this.recipeStartOffset = recipeStartOffset;
		this.recipeEndOffset = recipeEndOffset;
	}

	@Override
	public List<Slot> getRecipeSlots(ContainerMachine<T> container)
	{
		int recipeStart = container.index_max_hotbar + recipeStartOffset;
		if (recipeStart >= container.inventorySlots.size()) return Collections.emptyList();
		else
		{
			int recipeEnd = container.index_max_hotbar + recipeEndOffset;
			if (recipeEnd > container.index_max_machine) recipeEnd = container.index_max_machine;
			if (recipeEnd <= recipeStart) return Collections.emptyList();
			else
			{
				List<Slot> list = new ArrayList<>(recipeEnd - recipeStart);
				for (int i = recipeStart; i < recipeEnd; i++) list.add(container.getSlot(i));
				return list;
			}
		}
	}

	@Override
	public List<Slot> getInventorySlots(ContainerMachine<T> container)
	{
		List<Slot> list = new ArrayList<>(container.index_max_hotbar);
		for (int i = 0; i < container.index_max_hotbar; i++) list.add(container.getSlot(i));
		return list;
	}

	@Override
	public boolean canHandle(ContainerMachine<T> container)
	{
		return true;
	}
}