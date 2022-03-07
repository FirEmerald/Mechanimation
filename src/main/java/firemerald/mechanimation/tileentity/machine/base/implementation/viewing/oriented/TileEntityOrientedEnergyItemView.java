package firemerald.mechanimation.tileentity.machine.base.implementation.viewing.oriented;

import firemerald.mechanimation.tileentity.machine.base.IOrientedMachine;
import firemerald.mechanimation.tileentity.machine.base.energy.IEnergyMachine;
import firemerald.mechanimation.tileentity.machine.base.implementation.viewing.TileEntityEnergyItemView;
import firemerald.mechanimation.tileentity.machine.base.items.IItemMachine;
import firemerald.mechanimation.util.EnumOrientation;

public abstract class TileEntityOrientedEnergyItemView<T extends IEnergyMachine<T> & IItemMachine<T> & IOrientedMachine> extends TileEntityEnergyItemView<T> implements IOrientedMachine
{
	@Override
	public EnumOrientation getOrientation()
	{
		return viewedMachine == null ? EnumOrientation.UP_SOUTH : viewedMachine.getOrientation();
	}

	@Override
	public void setOrientation(EnumOrientation orientation) {}
}