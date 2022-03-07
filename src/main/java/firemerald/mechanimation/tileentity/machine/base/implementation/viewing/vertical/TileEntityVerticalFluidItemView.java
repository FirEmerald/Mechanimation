package firemerald.mechanimation.tileentity.machine.base.implementation.viewing.vertical;

import firemerald.mechanimation.tileentity.machine.base.IVerticalMachine;
import firemerald.mechanimation.tileentity.machine.base.fluids.IFluidMachine;
import firemerald.mechanimation.tileentity.machine.base.implementation.viewing.TileEntityFluidItemView;
import firemerald.mechanimation.tileentity.machine.base.items.IItemMachine;
import firemerald.mechanimation.util.EnumVerticalOrientation;

public abstract class TileEntityVerticalFluidItemView<T extends IFluidMachine<T> & IItemMachine<T> & IVerticalMachine> extends TileEntityFluidItemView<T> implements IVerticalMachine
{
	@Override
	public EnumVerticalOrientation getFront()
	{
		return viewedMachine == null ? EnumVerticalOrientation.SOUTH : viewedMachine.getFront();
	}

	@Override
	public void setFront(EnumVerticalOrientation orientation) {}
}