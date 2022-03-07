package firemerald.mechanimation.tileentity.machine.base.energy;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import firemerald.mechanimation.util.EnumFace;

public class EnergyInventoryTemplateBuilder<T extends IEnergyMachine<T>>
{
	private final Map<String, EnergySlotInfo<T>> slots = new HashMap<>();
	private int slot = 0;

	public EnergyInventoryTemplateBuilder<T> addSlot(String name, ToIntFunction<? super T> maxEnergy, ToIntFunction<? super T> maxReceive, Function<? super T, Predicate<EnumFace>> canReceive, ToIntFunction<? super T> maxExtract, Function<? super T, Predicate<EnumFace>> canExtract, Function<T, EnumFace[]> sides)
	{
		return addSlot(name, (name2, slot) -> new EnergySlotInfo<>(name2, slot, maxEnergy, maxReceive, canReceive, maxExtract, canExtract, sides));
	}

	public EnergyInventoryTemplateBuilder<T> addSlot(String name, ToIntFunction<? super T> maxEnergy, ToIntFunction<? super T> maxReceive, Function<? super T, Predicate<EnumFace>> canReceive, ToIntFunction<? super T> maxExtract, Function<T, EnumFace[]> sides)
	{
		return addSlot(name, maxEnergy, maxReceive, canReceive, maxExtract, CommonEnergyPredicates.accept(), sides);
	}

	public EnergyInventoryTemplateBuilder<T> addSlot(String name, ToIntFunction<? super T> maxEnergy, ToIntFunction<? super T> maxReceive, ToIntFunction<? super T> maxExtract, Function<? super T, Predicate<EnumFace>> canExtract, Function<T, EnumFace[]> sides)
	{
		return addSlot(name, maxEnergy, maxReceive, CommonEnergyPredicates.accept(), maxExtract, canExtract, sides);
	}

	public EnergyInventoryTemplateBuilder<T> addSlot(String name, ToIntFunction<? super T> maxEnergy, ToIntFunction<? super T> maxReceive, ToIntFunction<? super T> maxExtract, Function<T, EnumFace[]> sides)
	{
		return addSlot(name, maxEnergy, maxReceive, CommonEnergyPredicates.accept(), maxExtract, CommonEnergyPredicates.accept(), sides);
	}

	public EnergyInventoryTemplateBuilder<T> addSlot(String name, BiFunction<String, Integer, EnergySlotInfo<T>> constructor)
	{
		if (slots.containsKey(name)) throw new IllegalStateException("Attempted to add a slot with existing ID " + name);
		slots.put(name, constructor.apply(name, slot));
		++slot;
		return this;
	}

	public EnergyInventoryTemplate<T> build()
	{
		return new EnergyInventoryTemplate<>(slots);
	}
}