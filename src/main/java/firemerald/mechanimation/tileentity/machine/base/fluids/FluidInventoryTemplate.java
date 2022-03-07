package firemerald.mechanimation.tileentity.machine.base.fluids;

import java.util.HashMap;
import java.util.Map;

public class FluidInventoryTemplate<T extends IFluidMachine<T>>
{
	private final FluidSlotInfo<T>[] slots;
	private final Map<String, Integer> nameToIndex;

	@SuppressWarnings("unchecked")
	public FluidInventoryTemplate(Map<String, FluidSlotInfo<T>> slots)
	{
		this.slots = new FluidSlotInfo[slots.size()];
		this.nameToIndex = new HashMap<>(slots.size());
		slots.forEach((name, info) -> {
			FluidInventoryTemplate.this.slots[info.slot] = info;
			FluidInventoryTemplate.this.nameToIndex.put(name, info.slot);
		});
	}

	public int getIndex(String name)
	{
		if (!nameToIndex.containsKey(name)) throw new IllegalStateException("Attempted to get index of non-existant slot with ID " + name);
		return nameToIndex.get(name);
	}

	public FluidInventory<T> build(T machine)
	{
		return new FluidInventory<>(machine, slots);
	}
}