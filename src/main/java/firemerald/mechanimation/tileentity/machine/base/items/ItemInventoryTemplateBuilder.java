package firemerald.mechanimation.tileentity.machine.base.items;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import firemerald.mechanimation.util.EnumFace;
import net.minecraft.item.ItemStack;

public class ItemInventoryTemplateBuilder<T extends IItemMachine<T>>
{
	private final Map<String, ItemSlotInfo<T>> slots = new HashMap<>();
	private int slot = 0;

	public ItemInventoryTemplateBuilder<T> addSlot(String name, ToIntFunction<? super T> maxCount, ToIntFunction<? super T> maxInsert, Function<? super T, Predicate<ItemStack>> canInsert, ToIntFunction<? super T> maxExtract, Function<? super T, Predicate<ItemStack>> canExtract, Function<T, EnumFace[]> sides)
	{
		return addSlot(name, (name2, slot) -> new ItemSlotInfo<>(name2, slot, maxCount, maxInsert, canInsert, maxExtract, canExtract, sides));
	}

	public ItemInventoryTemplateBuilder<T> addSlot(String name, ToIntFunction<? super T> maxCount, ToIntFunction<? super T> maxInsert, Function<? super T, Predicate<ItemStack>> canInsert, ToIntFunction<? super T> maxExtract, Function<T, EnumFace[]> sides)
	{
		return addSlot(name, maxCount, maxInsert, canInsert, maxExtract, CommonItemPredicates.accept(), sides);
	}

	public ItemInventoryTemplateBuilder<T> addSlot(String name, ToIntFunction<? super T> maxCount, ToIntFunction<? super T> maxInsert, ToIntFunction<? super T> maxExtract, Function<? super T, Predicate<ItemStack>> canExtract, Function<T, EnumFace[]> sides)
	{
		return addSlot(name, maxCount, maxInsert, CommonItemPredicates.accept(), maxExtract, canExtract, sides);
	}

	public ItemInventoryTemplateBuilder<T> addSlot(String name, ToIntFunction<? super T> maxCount, ToIntFunction<? super T> maxInsert, ToIntFunction<? super T> maxExtract, Function<T, EnumFace[]> sides)
	{
		return addSlot(name, maxCount, maxInsert, CommonItemPredicates.accept(), maxExtract, CommonItemPredicates.accept(), sides);
	}

	public ItemInventoryTemplateBuilder<T> addSlot(String name, BiFunction<String, Integer, ItemSlotInfo<T>> constructor)
	{
		if (slots.containsKey(name)) throw new IllegalStateException("Attempted to add a slot with existing ID " + name);
		slots.put(name, constructor.apply(name, slot));
		++slot;
		return this;
	}

	public ItemInventoryTemplate<T> build()
	{
		return new ItemInventoryTemplate<>(slots);
	}
}