package firemerald.mechanimation.tileentity.machine.base.energy;

import java.util.HashMap;
import java.util.Map;

public class EnergyInventoryTemplate<T extends IEnergyMachine<T>>
{
	private final EnergySlotInfo<T>[] slots;
	private final Map<String, Integer> nameToIndex;

	@SuppressWarnings("unchecked")
	public EnergyInventoryTemplate(Map<String, EnergySlotInfo<T>> slots)
	{
		this.slots = new EnergySlotInfo[slots.size()];
		this.nameToIndex = new HashMap<>(slots.size());
		slots.forEach((name, info) -> {
			EnergyInventoryTemplate.this.slots[info.slot] = info;
			EnergyInventoryTemplate.this.nameToIndex.put(name, info.slot);
		});
	}

	public int getIndex(String name)
	{
		if (!nameToIndex.containsKey(name)) throw new IllegalStateException("Attempted to get index of non-existant slot with ID " + name);
		return nameToIndex.get(name);
	}

	public EnergyInventory<T> build(T machine)
	{
		return new EnergyInventory<>(machine, slots);
	}
}