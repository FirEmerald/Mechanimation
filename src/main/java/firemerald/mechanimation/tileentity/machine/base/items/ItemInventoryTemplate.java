package firemerald.mechanimation.tileentity.machine.base.items;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ItemInventoryTemplate<T extends IItemMachine<T>>
{
	private final ItemSlotInfo<T>[] slots;
	private final Map<String, Integer> nameToIndex;

	@SuppressWarnings("unchecked")
	public ItemInventoryTemplate(Map<String, ItemSlotInfo<T>> slots)
	{
		this.slots = new ItemSlotInfo[slots.size()];
		this.nameToIndex = new HashMap<>(slots.size());
		slots.forEach((name, info) -> {
			ItemInventoryTemplate.this.slots[info.slot] = info;
			ItemInventoryTemplate.this.nameToIndex.put(name, info.slot);
		});
	}

	public int getIndex(String name)
	{
		if (!nameToIndex.containsKey(name)) throw new IllegalStateException("Attempted to get index of non-existant slot with ID " + name);
		return nameToIndex.get(name);
	}

	public ItemInventory<T> build(T machine)
	{
		return new ItemInventory<>(machine, slots);
	}

	public <U extends IItemInventory<T>> U build(Function<ItemSlotInfo<T>[], U> constructor)
	{
		return constructor.apply(slots);
	}
}