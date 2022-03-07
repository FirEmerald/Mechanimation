package firemerald.mechanimation.tileentity.machine.base.implementation.viewing;

import javax.annotation.Nullable;

import firemerald.mechanimation.api.capabilities.Capabilities;
import firemerald.mechanimation.tileentity.machine.base.fluids.FluidCapWrapper;
import firemerald.mechanimation.tileentity.machine.base.fluids.FluidView;
import firemerald.mechanimation.tileentity.machine.base.fluids.IFluidInventory;
import firemerald.mechanimation.tileentity.machine.base.fluids.IFluidMachine;
import firemerald.mechanimation.util.EnumFace;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public abstract class TileEntityFluidView<T extends IFluidMachine<T>> extends TileEntityViewBase<T> implements IFluidMachine<T>
{
	@SuppressWarnings("unchecked")
	private final IFluidHandler[] facedFluidHandlers = new IFluidHandler[] {
			new FluidCapWrapper<>((T) this, EnumFace.values()[0]),
			new FluidCapWrapper<>((T) this, EnumFace.values()[1]),
			new FluidCapWrapper<>((T) this, EnumFace.values()[2]),
			new FluidCapWrapper<>((T) this, EnumFace.values()[3]),
			new FluidCapWrapper<>((T) this, EnumFace.values()[4]),
			new FluidCapWrapper<>((T) this, EnumFace.values()[5])
	};
	public final FluidView<T> fluidView = new FluidView<>();

	@Override
	public IFluidInventory<T> getFluidInventory()
	{
		updateViewed();
		return fluidView;
	}

    @Override
    public boolean hasCapabilityLocal(Capability<?> capability, @Nullable EnumFacing facing)
    {
    	if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || capability == Capabilities.gasHandler)
    	{
    		return getFluidInventory().getSlots(getFace(facing)).length > 0;
    	}
    	else return super.hasCapabilityLocal(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
	public <S> S getCapabilityLocal(Capability<S> capability, @Nullable EnumFacing facing)
    {
    	if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
    	{
    		if (facing == null) return (S) this.getFluidInventory();
    		else return (S) facedFluidHandlers[getFace(facing).ordinal()];
    	}
    	else if (capability == Capabilities.gasHandler) return (S) this;
    	else return super.getCapabilityLocal(capability, facing);
    }

	@Override
	public void onViewChanged()
	{
		fluidView.viewing = viewedMachine == null ? null : viewedMachine.getFluidInventory();
	}
}