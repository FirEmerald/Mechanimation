package firemerald.mechanimation.tileentity.machine.base.fluids;

import java.util.function.Function;
import java.util.function.Predicate;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;

public class CommonFluidPredicates
{
	public static final Function<? extends IFluidMachine<?>, Predicate<FluidOrGasStack>> ACCEPT = of(stack -> true);
	public static final Function<? extends IFluidMachine<?>, Predicate<FluidOrGasStack>> DENY = of(stack -> false);

	@SuppressWarnings("unchecked")
	public static <T extends IFluidMachine<?>> Function<T, Predicate<FluidOrGasStack>> accept()
	{
		return (Function<T, Predicate<FluidOrGasStack>>) ACCEPT;
	}

	@SuppressWarnings("unchecked")
	public static <T extends IFluidMachine<?>> Function<T, Predicate<FluidOrGasStack>> deny()
	{
		return (Function<T, Predicate<FluidOrGasStack>>) DENY;
	}

	public static <T extends IFluidMachine<T>> Function<T, Predicate<FluidOrGasStack>> of(Predicate<FluidOrGasStack> predicate)
	{
		return machine -> predicate;
	}

	public static <T extends IFluidMachine<T>> Function<T, Predicate<FluidOrGasStack>> not(Function<T, Predicate<FluidOrGasStack>> pred)
	{
		return machine -> pred.apply(machine).negate();
	}
}