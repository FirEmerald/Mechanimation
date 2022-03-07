package firemerald.mechanimation.tileentity.machine.base.fluids;

import firemerald.mechanimation.util.EnumFace;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class FluidCapWrapper<T extends IFluidMachine<T>> implements IFluidHandler
{
	public final T machine;
	public final EnumFace face;

	public FluidCapWrapper(T machine, EnumFace face)
	{
		this.machine = machine;
		this.face = face;
	}

	@Override
	public IFluidTankProperties[] getTankProperties()
	{
		return machine.getFluidInventory().getTankProperties(face);
	}

	@Override
	public int fill(FluidStack resource, boolean doFill)
	{
		return machine.getFluidInventory().fill(resource, doFill, face);
	}

	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain)
	{
		return machine.getFluidInventory().drain(resource, doDrain, face);
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain)
	{
		return machine.getFluidInventory().drain(maxDrain, doDrain, face);
	}

}
