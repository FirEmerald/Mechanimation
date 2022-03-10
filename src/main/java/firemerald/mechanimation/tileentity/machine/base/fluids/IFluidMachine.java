package firemerald.mechanimation.tileentity.machine.base.fluids;

import javax.annotation.Nonnull;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.tileentity.IFluidTankInfo;
import firemerald.mechanimation.mcms.IFluidOrGasStackProvider;
import firemerald.mechanimation.tileentity.machine.base.IMachine;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.IGasHandler;
import net.minecraft.util.EnumFacing;

public interface IFluidMachine<T extends IFluidMachine<T>> extends IFluidOrGasStackProvider, IMachine, IGasHandler
{
	public IFluidInventory<T> getFluidInventory();

	@Override
	public default FluidOrGasStack getFluidStack(int index)
	{
		return getFluidInventory().getFluidOrGas(index);
	}

	@Override
    @Nonnull
	public default IFluidTankInfo[] getTankInfo()
	{
		return getFluidInventory().getFluidSlots();
	}

	@Override
    public default int receiveGas(EnumFacing side, GasStack receive, boolean doTransfer)
    {
		return getFluidInventory().receiveGas(receive, doTransfer, getFace(side));
    }

	@Override
    public default GasStack drawGas(EnumFacing side, int amount, boolean doTransfer)
    {
		return getFluidInventory().drawGas(amount, doTransfer, getFace(side));
    }

	@Override
    public default boolean canReceiveGas(EnumFacing side, Gas type)
    {
		return getFluidInventory().canReceiveGas(type, getFace(side));
    }

	@Override
    public default boolean canDrawGas(EnumFacing side, Gas type)
    {
		return getFluidInventory().canDrawGas(type, getFace(side));
    }

	public default void onFluidSlotChanged(int slot, FluidOrGasStack newStack)
	{
		this.setNeedsUpdate();
	}

	@Override
	public default int getMaxFluid(int index)
	{
		IFluidSlot slot = getFluidInventory().getFluidSlot(index);
		return slot == null ? 0 : slot.getCapacity();
	}
}