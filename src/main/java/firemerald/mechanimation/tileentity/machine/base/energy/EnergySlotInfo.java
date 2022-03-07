package firemerald.mechanimation.tileentity.machine.base.energy;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import firemerald.mechanimation.util.EnumFace;

public class EnergySlotInfo<T extends IEnergyMachine<T>>
{
	public final String name;
	public final int slot;
	public final ToIntFunction<? super T> maxEnergy, maxReceive, maxExtract;
	public final Function<? super T, Predicate<EnumFace>> canReceive, canExtract;
	public final Function<T, EnumFace[]> sides;

	public EnergySlotInfo(String name, int slot, ToIntFunction<? super T> maxEnergy, ToIntFunction<? super T> maxReceive, Function<? super T, Predicate<EnumFace>> canReceive, ToIntFunction<? super T> maxExtract, Function<? super T, Predicate<EnumFace>> canExtract, Function<T, EnumFace[]> sides)
	{
		this.name = name;
		this.slot = slot;
		this.maxEnergy = maxEnergy;
		this.maxReceive = maxReceive;
		this.canReceive = canReceive;
		this.maxExtract = maxExtract;
		this.canExtract = canExtract;
		this.sides = sides;
	}

	public IEnergySlot makeSlot(T machine)
	{
		return new EnergySlot<>(machine, name, slot, maxEnergy.applyAsInt(machine), maxReceive.applyAsInt(machine), canReceive.apply(machine), maxExtract.applyAsInt(machine), canExtract.apply(machine));
	}
}