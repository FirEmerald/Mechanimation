package firemerald.mechanimation.tileentity.machine.base.implementation.viewing.vertical;

import firemerald.mechanimation.tileentity.machine.base.IVerticalMachine;
import firemerald.mechanimation.tileentity.machine.base.fluids.IFluidMachine;
import firemerald.mechanimation.tileentity.machine.base.implementation.viewing.TileEntityFluidView;
import firemerald.mechanimation.util.EnumVerticalOrientation;

public abstract class TileEntityVerticalFluidView<T extends IFluidMachine<T> & IVerticalMachine> extends TileEntityFluidView<T> implements IVerticalMachine
{
	@Override
	public EnumVerticalOrientation getFront()
	{
		return viewedMachine == null ? EnumVerticalOrientation.SOUTH : viewedMachine.getFront();
	}

	@Override
	public void setFront(EnumVerticalOrientation orientation) {}
}