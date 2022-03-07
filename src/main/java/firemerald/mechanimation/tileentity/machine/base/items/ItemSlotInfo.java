package firemerald.mechanimation.tileentity.machine.base.items;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import firemerald.mechanimation.util.EnumFace;
import net.minecraft.item.ItemStack;

public class ItemSlotInfo<T extends IItemMachine<T>>
{
	public final String name;
	public final int slot;
	public final Function<? super T, Predicate<ItemStack>> canInsert, canExtract;
	public final ToIntFunction<? super T> maxCount, maxInsert, maxExtract;
	public final Function<T, EnumFace[]> sides;

	public ItemSlotInfo(String name, int slot, ToIntFunction<? super T> maxCount, ToIntFunction<? super T> maxInsert, Function<? super T, Predicate<ItemStack>> canInsert, ToIntFunction<? super T> maxExtract, Function<? super T, Predicate<ItemStack>> canExtract, Function<T, EnumFace[]> sides)
	{
		this.name = name;
		this.slot = slot;
		this.maxCount = maxCount;
		this.maxInsert = maxInsert;
		this.canInsert = canInsert;
		this.maxExtract = maxExtract;
		this.canExtract = canExtract;
		this.sides = sides;
	}

	public IItemSlot makeSlot(T machine)
	{
		return new ItemSlot<>(machine, name, slot, maxCount.applyAsInt(machine), maxInsert.applyAsInt(machine), canInsert.apply(machine), maxExtract.applyAsInt(machine), canExtract.apply(machine));
	}
}