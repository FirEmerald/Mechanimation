package firemerald.mechanimation.tileentity.machine.base.implementation.viewing.vertical;

import firemerald.mechanimation.tileentity.machine.base.IVerticalMachine;
import firemerald.mechanimation.tileentity.machine.base.implementation.viewing.TileEntityItemView;
import firemerald.mechanimation.tileentity.machine.base.items.IItemMachine;
import firemerald.mechanimation.util.EnumVerticalOrientation;

public abstract class TileEntityVerticalItemView<T extends IItemMachine<T> & IVerticalMachine> extends TileEntityItemView<T> implements IVerticalMachine
{
	@Override
	public EnumVerticalOrientation getFront()
	{
		return viewedMachine == null ? EnumVerticalOrientation.SOUTH : viewedMachine.getFront();
	}

	@Override
	public void setFront(EnumVerticalOrientation orientation) {}
}