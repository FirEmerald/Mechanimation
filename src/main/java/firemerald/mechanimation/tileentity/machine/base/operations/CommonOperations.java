package firemerald.mechanimation.tileentity.machine.base.operations;

import java.util.function.Function;
import java.util.function.ToIntFunction;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.tileentity.machine.base.energy.IEnergyMachine;
import firemerald.mechanimation.tileentity.machine.base.energy.IEnergySlot;
import firemerald.mechanimation.tileentity.machine.base.fluids.IFluidMachine;
import firemerald.mechanimation.tileentity.machine.base.fluids.IFluidSlot;
import firemerald.mechanimation.tileentity.machine.base.items.IItemMachine;
import firemerald.mechanimation.tileentity.machine.base.items.IItemSlot;
import firemerald.mechanimation.util.EnumFace;
import firemerald.mechanimation.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class CommonOperations
{
	public static <T extends IItemMachine<T> & IFluidMachine<T>> Function<T, Runnable> fluidFromItem(Function<T, IItemSlot> getItemSlot, Function<T, IFluidSlot> getFluidSlot)
	{
		return fluidFromItem(getItemSlot, getFluidSlot, machine -> Integer.MAX_VALUE);
	}

	public static <T extends IItemMachine<T> & IFluidMachine<T>> Function<T, Runnable> fluidFromItem(Function<T, IItemSlot> getItemSlot, Function<T, IFluidSlot> getFluidSlot, ToIntFunction<T> maxTransfer)
	{
		return machine -> {
			IItemSlot itemSlot = getItemSlot.apply(machine);
			if (itemSlot == null) return () -> {};
			IFluidSlot fluidSlot = getFluidSlot.apply(machine);
			if (fluidSlot == null) return () -> {};
			int max = maxTransfer.applyAsInt(machine);
			if (max <= 0) return () -> {};
			return () -> {
				ItemStack stack = itemSlot.getStack();
				if (!stack.isEmpty())
				{
					FluidOrGasStack fluid = fluidSlot.getFluid();
					if (Utils.isFluidOrGasProvider(stack, fluid))
					{
						FluidOrGasStack stored = Utils.getStoredFluid(stack, fluid);
						if (stored != null && stored.getAmount() > 0)
						{
							int wants = Math.min(max, Math.min(stored.getAmount(), fluidSlot.getCapacity() - FluidOrGasStack.getAmountStatic(fluid)));
							if (wants > 0)
							{
								FluidOrGasStack drained = Utils.tryDrain(stack, wants, false, fluid, itemSlot::setStack);
								if (drained != null && drained.getAmount() > 0)
								{
									if (fluid == null) fluid = drained.copy();
									else fluid.changeAmount(drained.getAmount());
									fluidSlot.setFluid(fluid);
								}
							}
						}
					}
				}
			};
		};
	}

	public static <T extends IItemMachine<T> & IFluidMachine<T>> Function<T, Runnable> fluidToItem(Function<T, IItemSlot> getItemSlot, Function<T, IFluidSlot> getFluidSlot)
	{
		return fluidToItem(getItemSlot, getFluidSlot, machine -> Integer.MAX_VALUE);
	}

	public static <T extends IItemMachine<T> & IFluidMachine<T>> Function<T, Runnable> fluidToItem(Function<T, IItemSlot> getItemSlot, Function<T, IFluidSlot> getFluidSlot, ToIntFunction<T> maxTransfer)
	{
		return machine -> {
			IItemSlot itemSlot = getItemSlot.apply(machine);
			if (itemSlot == null) return () -> {};
			IFluidSlot fluidSlot = getFluidSlot.apply(machine);
			if (fluidSlot == null) return () -> {};
			int max = maxTransfer.applyAsInt(machine);
			if (max <= 0) return () -> {};
			return () -> {
				ItemStack stack = itemSlot.getStack();
				if (!stack.isEmpty())
				{
					FluidOrGasStack fluid = fluidSlot.getFluid();
					if (FluidOrGasStack.getAmountStatic(fluid) > 0 && Utils.isFluidOrGasReceiver(stack, fluid))
					{
						FluidOrGasStack stored = Utils.getStoredFluid(stack, fluid);
						int wants = Math.min(max, Math.min(Utils.getMaxStoredFluid(stack, fluid) - FluidOrGasStack.getAmountStatic(stored), fluid.getAmount()));
						if (wants > 0)
						{
							FluidOrGasStack wantsStack = fluid.copy();
							wantsStack.setAmount(wants);
							int drained = Utils.tryFill(stack, wantsStack, false, itemSlot::setStack);
							if (drained  > 0)
							{
								fluid.changeAmount(-drained);
								if (fluid.getAmount() <= 0) fluid = null;
								fluidSlot.setFluid(fluid);
							}
						}
					}
				}
			};
		};
	}

	public static <T extends IItemMachine<T> & IEnergyMachine<T>> Function<T, Runnable> energyFromItem(Function<T, IItemSlot> getItemSlot, Function<T, IEnergySlot> getEnergySlot)
	{
		return energyFromItem(getItemSlot, getEnergySlot, machine -> Integer.MAX_VALUE);
	}

	public static <T extends IItemMachine<T> & IEnergyMachine<T>> Function<T, Runnable> energyFromItem(Function<T, IItemSlot> getItemSlot, Function<T, IEnergySlot> getEnergySlot, ToIntFunction<T> maxTransfer)
	{
		return machine -> {
			IItemSlot itemSlot = getItemSlot.apply(machine);
			if (itemSlot == null) return () -> {};
			IEnergySlot energySlot = getEnergySlot.apply(machine);
			if (energySlot == null) return () -> {};
			int max = maxTransfer.applyAsInt(machine);
			if (max <= 0) return () -> {};
			return () -> {
				ItemStack stack = itemSlot.getStack();
				if (!stack.isEmpty() && Utils.isEnergyProvider(stack))
				{
					int wants = Math.min(max, Math.min(Utils.getStoredEnergy(stack), energySlot.getMaxEnergyStored() - energySlot.getEnergyStored()));
					if (wants > 0)
					{
						int got = Utils.tryExtract(stack, wants, false);
						if (got > 0)
						{
							itemSlot.setStack(stack);
							energySlot.setEnergy(energySlot.getEnergyStored() + got);
						}
					}
				}
			};
		};
	}

	public static <T extends IItemMachine<T> & IEnergyMachine<T>> Function<T, Runnable> energyToItem(Function<T, IItemSlot> getItemSlot, Function<T, IEnergySlot> getEnergySlot)
	{
		return energyToItem(getItemSlot, getEnergySlot, machine -> Integer.MAX_VALUE);
	}

	public static <T extends IItemMachine<T> & IEnergyMachine<T>> Function<T, Runnable> energyToItem(Function<T, IItemSlot> getItemSlot, Function<T, IEnergySlot> getEnergySlot, ToIntFunction<T> maxTransfer)
	{
		return machine -> {
			IItemSlot itemSlot = getItemSlot.apply(machine);
			if (itemSlot == null) return () -> {};
			IEnergySlot energySlot = getEnergySlot.apply(machine);
			if (energySlot == null) return () -> {};
			int max = maxTransfer.applyAsInt(machine);
			if (max <= 0) return () -> {};
			return () -> {
				ItemStack stack = itemSlot.getStack();
				if (!stack.isEmpty() && Utils.isEnergyReceiver(stack))
				{
					int wants = Math.min(max, Math.min(energySlot.getEnergyStored(), Utils.getMaxStoredEnergy(stack) - Utils.getStoredEnergy(stack)));
					if (wants > 0)
					{
						int got = Utils.tryReceive(stack, wants, false);
						if (got > 0)
						{
							itemSlot.setStack(stack);
							energySlot.setEnergy(energySlot.getEnergyStored() - got);
						}
					}
				}
			};
		};
	}

	public static <T extends IEnergyMachine<T>> Function<T, Runnable> provideEnergy(Function<T, IEnergySlot> getEnergySlot, ToIntFunction<T> maxTransfer, Function<T, EnumFace[]> faces)
	{
		return machine -> {
			IEnergySlot energySlot = getEnergySlot.apply(machine);
			if (energySlot == null) return () -> {};
			int max = maxTransfer.applyAsInt(machine);
			if (max <= 0) return () -> {};
			EnumFace[] to = faces.apply(machine);
			if (to == null || to.length <= 0) return () -> {};
			return () -> {
				int pow = energySlot.getEnergyStored();
				int[] request = new int[to.length];
				long total = 0;
				for (int i = 0; i < request.length; i++) total += request[i] = maxTransfer(machine, machine.getFacing(to[i]), energySlot, max);
				if (total > 0)
				{
					if (total > pow) for (int i = 0; i < request.length; i++) request[i] = (int) Math.floor((request[i] * pow) / total);
					for (int i = 0; i < request.length; i++) if (request[i] > 0) tryTransfer(machine, machine.getFacing(to[i]), request[i], energySlot);
				}
			};
		};
	}

	public static <T extends IEnergyMachine<T>> int maxTransfer(T machine, EnumFacing direction, IEnergySlot info, int maxProvide)
	{
		TileEntity tile = machine.getTheWorld().getTileEntity(machine.getThePos().offset(direction));
		if (tile != null)
		{
			EnumFacing opp = direction.getOpposite();
			IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, opp);
			if (storage != null)
			{
				if (storage.canReceive()) return Math.min(storage.receiveEnergy(Integer.MAX_VALUE, true), maxProvide);
				else return 0;
			}
			else return 0;
		}
		else return 0;
	}

	public static <T extends IEnergyMachine<T>> void tryTransfer(T machine, EnumFacing direction, int amount, IEnergySlot info)
	{
		TileEntity tile = machine.getTheWorld().getTileEntity(machine.getThePos().offset(direction));
		if (tile != null)
		{
			IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, direction.getOpposite());
			if (storage != null)
			{
				if (storage.canReceive())
				{
					info.remove(storage.receiveEnergy(Math.min(amount, info.getEnergyStored()), false));
					machine.setNeedsUpdate();
				}
			}
		}
	}
}