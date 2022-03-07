package firemerald.mechanimation.tileentity.machine.base;

import firemerald.mechanimation.util.EnumFace;
import firemerald.mechanimation.util.EnumVerticalOrientation;
import net.minecraft.util.EnumFacing;

public interface IVerticalMachine extends IMachine
{
	public EnumVerticalOrientation getFront();

	public void setFront(EnumVerticalOrientation orientation);

	@Override
	public default EnumFace getFace(EnumFacing side)
	{
		return getFront().getFace(side);
	}

	@Override
	public default EnumFacing getFacing(EnumFace face)
	{
		return getFront().getFacing(face);
	}
}