package firemerald.mechanimation.tileentity.machine.base.implementation.viewing.oriented;

import firemerald.mechanimation.tileentity.machine.base.IOrientedMachine;
import firemerald.mechanimation.tileentity.machine.base.fluids.IFluidMachine;
import firemerald.mechanimation.tileentity.machine.base.implementation.viewing.TileEntityFluidItemView;
import firemerald.mechanimation.tileentity.machine.base.items.IItemMachine;
import firemerald.mechanimation.util.EnumOrientation;

public abstract class TileEntityOrientedFluidItemView<T extends IFluidMachine<T> & IItemMachine<T> & IOrientedMachine> extends TileEntityFluidItemView<T> implements IOrientedMachine
{
	@Override
	public EnumOrientation getOrientation()
	{
		return viewedMachine == null ? EnumOrientation.UP_SOUTH : viewedMachine.getOrientation();
	}

	@Override
	public void setOrientation(EnumOrientation orientation) {}
}