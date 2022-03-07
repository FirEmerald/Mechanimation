package firemerald.mechanimation.tileentity.machine.base.items;

import java.util.function.Function;
import java.util.function.Predicate;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.tileentity.machine.base.fluids.IFluidMachine;
import firemerald.mechanimation.tileentity.machine.base.fluids.IFluidSlot;
import firemerald.mechanimation.util.Utils;
import net.minecraft.item.ItemStack;

public class CommonItemPredicates
{
	public static final Function<? extends IItemMachine<?>, Predicate<ItemStack>> ACCEPT = of(stack -> true);
	public static final Function<? extends IItemMachine<?>, Predicate<ItemStack>> DENY = of(stack -> false);
	public static final Function<? extends IItemMachine<?>, Predicate<ItemStack>> PROVIDES_ENERGY = of(Utils::isEnergyProvider);
	public static final Function<? extends IItemMachine<?>, Predicate<ItemStack>> DOES_NOT_PROVIDE_ENERGY = of(stack -> !Utils.isEnergyProvider(stack));
	public static final Function<? extends IItemMachine<?>, Predicate<ItemStack>> RECEIVES_ENERGY = of(Utils::isEnergyReceiver);
	public static final Function<? extends IItemMachine<?>, Predicate<ItemStack>> DOES_NOT_RECEIVE_ENERGY = of(stack -> !Utils.isEnergyReceiver(stack));

	@SuppressWarnings("unchecked")
	public static <T extends IItemMachine<?>> Function<T, Predicate<ItemStack>> accept()
	{
		return (Function<T, Predicate<ItemStack>>) ACCEPT;
	}

	@SuppressWarnings("unchecked")
	public static <T extends IItemMachine<?>> Function<T, Predicate<ItemStack>> deny()
	{
		return (Function<T, Predicate<ItemStack>>) DENY;
	}

	@SuppressWarnings("unchecked")
	public static <T extends IItemMachine<?>> Function<T, Predicate<ItemStack>> providesEnergy()
	{
		return (Function<T, Predicate<ItemStack>>) PROVIDES_ENERGY;
	}

	@SuppressWarnings("unchecked")
	public static <T extends IItemMachine<?>> Function<T, Predicate<ItemStack>> doesNotProvideEnergy()
	{
		return (Function<T, Predicate<ItemStack>>) DOES_NOT_PROVIDE_ENERGY;
	}

	@SuppressWarnings("unchecked")
	public static <T extends IItemMachine<?>> Function<T, Predicate<ItemStack>> receivesEnergy()
	{
		return (Function<T, Predicate<ItemStack>>) RECEIVES_ENERGY;
	}

	@SuppressWarnings("unchecked")
	public static <T extends IItemMachine<?>> Function<T, Predicate<ItemStack>> doesNotReceiveEnergy()
	{
		return (Function<T, Predicate<ItemStack>>) DOES_NOT_RECEIVE_ENERGY;
	}

	public static <T extends IItemMachine<?>> Function<T, Predicate<ItemStack>> of(Predicate<ItemStack> predicate)
	{
		return machine -> predicate;
	}

	public static <T extends IItemMachine<?> & IFluidMachine<?>> Function<T, Predicate<ItemStack>> providesFluidTo(Function<T, IFluidSlot> getSlot)
	{
		return machine -> stack -> {
			IFluidSlot slot = getSlot.apply(machine);
			if (slot == null) return false;
			else
			{
				FluidOrGasStack extract = Utils.getStoredFluid(stack, slot.getFluid());
				return extract != null && slot.canInsert(extract, null);
			}
		};
	}

	public static <T extends IItemMachine<?> & IFluidMachine<?>> Function<T, Predicate<ItemStack>> doesNotProvideFluidTo(Function<T, IFluidSlot> getSlot)
	{
		return machine -> stack -> {
			IFluidSlot slot = getSlot.apply(machine);
			if (slot == null) return false;
			else
			{
				FluidOrGasStack extract = Utils.getStoredFluid(stack, slot.getFluid());
				return extract == null || !slot.canInsert(extract, null);
			}
		};
	}

	public static <T extends IItemMachine<?> & IFluidMachine<?>> Function<T, Predicate<ItemStack>> receivesFluidFrom(Function<T, IFluidSlot> getSlot)
	{
		return machine -> stack -> {
			IFluidSlot slot = getSlot.apply(machine);
			if (slot == null) return false;
			else return Utils.tryFill(stack, slot.getFluid(), true, null) > 0; //TODO
		};
	}

	public static <T extends IItemMachine<?> & IFluidMachine<?>> Function<T, Predicate<ItemStack>> doesNotReceiveFluidFrom(Function<T, IFluidSlot> getSlot)
	{
		return machine -> stack -> {
			IFluidSlot slot = getSlot.apply(machine);
			if (slot == null) return false;
			else return Utils.tryFill(stack, slot.getFluid(), true, null) <= 0; //TODO
		};
	}

	public static <T extends IItemMachine<?>> Function<T, Predicate<ItemStack>> not(Function<T, Predicate<ItemStack>> pred)
	{
		return machine -> pred.apply(machine).negate();
	}
}