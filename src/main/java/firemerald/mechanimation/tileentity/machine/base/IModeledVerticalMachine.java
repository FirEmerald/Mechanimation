package firemerald.mechanimation.tileentity.machine.base;

import org.joml.Matrix4d;

import firemerald.api.mcms.util.IModeledEntity;

public interface IModeledVerticalMachine extends IVerticalMachine, IModeledEntity
{
    @Override
	public default Matrix4d getRootTick()
	{
		return new Matrix4d().translate(0, .5, 0f).mul(getFront().transformation).translate(0, -.5, 0);
	}
}