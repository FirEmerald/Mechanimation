package firemerald.mechanimation.tileentity.machine.base.implementation.viewing.vertical;

import firemerald.mechanimation.tileentity.machine.base.IVerticalMachine;
import firemerald.mechanimation.tileentity.machine.base.energy.IEnergyMachine;
import firemerald.mechanimation.tileentity.machine.base.fluids.IFluidMachine;
import firemerald.mechanimation.tileentity.machine.base.implementation.viewing.TileEntityEnergyFluidView;
import firemerald.mechanimation.util.EnumVerticalOrientation;

public abstract class TileEntityVerticalEnergyFluidView<T extends IEnergyMachine<T> & IFluidMachine<T> & IVerticalMachine> extends TileEntityEnergyFluidView<T> implements IVerticalMachine
{
	@Override
	public EnumVerticalOrientation getFront()
	{
		return viewedMachine == null ? EnumVerticalOrientation.SOUTH : viewedMachine.getFront();
	}

	@Override
	public void setFront(EnumVerticalOrientation orientation) {}
}