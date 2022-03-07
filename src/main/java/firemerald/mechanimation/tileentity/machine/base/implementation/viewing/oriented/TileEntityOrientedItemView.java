package firemerald.mechanimation.tileentity.machine.base.implementation.viewing.oriented;

import firemerald.mechanimation.tileentity.machine.base.IOrientedMachine;
import firemerald.mechanimation.tileentity.machine.base.implementation.viewing.TileEntityItemView;
import firemerald.mechanimation.tileentity.machine.base.items.IItemMachine;
import firemerald.mechanimation.util.EnumOrientation;

public abstract class TileEntityOrientedItemView<T extends IItemMachine<T> & IOrientedMachine> extends TileEntityItemView<T> implements IOrientedMachine
{
	@Override
	public EnumOrientation getOrientation()
	{
		return viewedMachine == null ? EnumOrientation.UP_SOUTH : viewedMachine.getOrientation();
	}

	@Override
	public void setOrientation(EnumOrientation orientation) {}
}