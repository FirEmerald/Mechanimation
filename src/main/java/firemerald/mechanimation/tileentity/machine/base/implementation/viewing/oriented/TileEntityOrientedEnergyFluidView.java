package firemerald.mechanimation.tileentity.machine.base.implementation.viewing.oriented;

import firemerald.mechanimation.tileentity.machine.base.IOrientedMachine;
import firemerald.mechanimation.tileentity.machine.base.energy.IEnergyMachine;
import firemerald.mechanimation.tileentity.machine.base.fluids.IFluidMachine;
import firemerald.mechanimation.tileentity.machine.base.implementation.viewing.TileEntityEnergyFluidView;
import firemerald.mechanimation.util.EnumOrientation;

public abstract class TileEntityOrientedEnergyFluidView<T extends IEnergyMachine<T> & IFluidMachine<T> & IOrientedMachine> extends TileEntityEnergyFluidView<T> implements IOrientedMachine
{
	@Override
	public EnumOrientation getOrientation()
	{
		return viewedMachine == null ? EnumOrientation.UP_SOUTH : viewedMachine.getOrientation();
	}

	@Override
	public void setOrientation(EnumOrientation orientation) {}
}