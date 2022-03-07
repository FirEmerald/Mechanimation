package firemerald.mechanimation.tileentity.machine.base.fluids;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.util.EnumFace;

public class FluidSlotInfo<T extends IFluidMachine<T>>
{
	public final String name;
	public final int slot;
	public final Function<? super T, Predicate<FluidOrGasStack>> canInsert, canExtract;
	public final ToIntFunction<? super T> maxCount, maxInsert, maxExtract;
	public final Function<? super T, EnumFace[]> sides;

	public FluidSlotInfo(String name, int slot, ToIntFunction<? super T> maxCount, ToIntFunction<? super T> maxInsert, Function<? super T, Predicate<FluidOrGasStack>> canInsert, ToIntFunction<? super T> maxExtract, Function<? super T, Predicate<FluidOrGasStack>> canExtract, Function<? super T, EnumFace[]> sides)
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

	public IFluidSlot makeSlot(T machine)
	{
		return new FluidSlot<>(machine, name, slot, maxCount.applyAsInt(machine), maxInsert.applyAsInt(machine), canInsert.apply(machine), maxExtract.applyAsInt(machine), canExtract.apply(machine));
	}
}