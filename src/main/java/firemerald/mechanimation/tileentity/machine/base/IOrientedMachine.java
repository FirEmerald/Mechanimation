package firemerald.mechanimation.tileentity.machine.base;

import firemerald.mechanimation.util.EnumFace;
import firemerald.mechanimation.util.EnumOrientation;
import net.minecraft.util.EnumFacing;

public interface IOrientedMachine extends IMachine
{
	public EnumOrientation getOrientation();

	public void setOrientation(EnumOrientation orientation);

	@Override
	public default EnumFace getFace(EnumFacing side)
	{
		return getOrientation().getFace(side);
	}

	@Override
	public default EnumFacing getFacing(EnumFace face)
	{
		return getOrientation().getFacing(face);
	}
}