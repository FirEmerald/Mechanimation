package firemerald.mechanimation.tileentity.machine.base.fluids;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.util.EnumFace;

public class FluidInventoryTemplateBuilder<T extends IFluidMachine<T>>
{
	private final Map<String, FluidSlotInfo<T>> slots = new HashMap<>();
	private int slot = 0;

	public FluidInventoryTemplateBuilder<T> addSlot(String name, ToIntFunction<? super T> maxCount, ToIntFunction<? super T> maxInsert, Function<? super T, Predicate<FluidOrGasStack>> canInsert, ToIntFunction<? super T> maxExtract, Function<? super T, Predicate<FluidOrGasStack>> canExtract, Function<T, EnumFace[]> sides)
	{
		return addSlot(name, (name2, slot) -> new FluidSlotInfo<>(name2, slot, maxCount, maxInsert, canInsert, maxExtract, canExtract, sides));
	}

	public FluidInventoryTemplateBuilder<T> addSlot(String name, ToIntFunction<? super T> maxCount, ToIntFunction<? super T> maxInsert, Function<? super T, Predicate<FluidOrGasStack>> canInsert, ToIntFunction<? super T> maxExtract, Function<T, EnumFace[]> sides)
	{
		return addSlot(name, maxCount, maxInsert, canInsert, maxExtract, CommonFluidPredicates.accept(), sides);
	}

	public FluidInventoryTemplateBuilder<T> addSlot(String name, ToIntFunction<? super T> maxCount, ToIntFunction<? super T> maxInsert, ToIntFunction<? super T> maxExtract, Function<? super T, Predicate<FluidOrGasStack>> canExtract, Function<T, EnumFace[]> sides)
	{
		return addSlot(name, maxCount, maxInsert, CommonFluidPredicates.accept(), maxExtract, canExtract, sides);
	}

	public FluidInventoryTemplateBuilder<T> addSlot(String name, ToIntFunction<? super T> maxCount, ToIntFunction<? super T> maxInsert, ToIntFunction<? super T> maxExtract, Function<T, EnumFace[]> sides)
	{
		return addSlot(name, maxCount, maxInsert, CommonFluidPredicates.accept(), maxExtract, CommonFluidPredicates.accept(), sides);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public FluidInventoryTemplateBuilder<T> addSlot(String name, BiFunction<String, Integer, FluidSlotInfo> constructor)
	{
		if (slots.containsKey(name)) throw new IllegalStateException("Attempted to add a slot with existing ID " + name);
		slots.put(name, constructor.apply(name, slot));
		++slot;
		return this;
	}

	public FluidInventoryTemplate<T> build()
	{
		return new FluidInventoryTemplate<>(slots);
	}
}