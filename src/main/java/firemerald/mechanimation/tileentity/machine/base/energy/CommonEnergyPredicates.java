package firemerald.mechanimation.tileentity.machine.base.energy;

import java.util.function.Function;
import java.util.function.Predicate;

import firemerald.mechanimation.util.EnumFace;

public class CommonEnergyPredicates
{
	public static final Function<? extends IEnergyMachine<?>, Predicate<EnumFace>> ACCEPT = of(face -> true);
	public static final Function<? extends IEnergyMachine<?>, Predicate<EnumFace>> DENY = of(face -> false);

	@SuppressWarnings("unchecked")
	public static <T extends IEnergyMachine<?>> Function<T, Predicate<EnumFace>> accept()
	{
		return (Function<T, Predicate<EnumFace>>) ACCEPT;
	}

	@SuppressWarnings("unchecked")
	public static <T extends IEnergyMachine<?>> Function<T, Predicate<EnumFace>> deny()
	{
		return (Function<T, Predicate<EnumFace>>) DENY;
	}

	public static <T extends IEnergyMachine<?>> Function<T, Predicate<EnumFace>> of(Predicate<EnumFace> predicate)
	{
		return machine -> predicate;
	}

	public static <T extends IEnergyMachine<?>> Function<T, Predicate<EnumFace>> not(Function<T, Predicate<EnumFace>> pred)
	{
		return machine -> pred.apply(machine).negate();
	}
}