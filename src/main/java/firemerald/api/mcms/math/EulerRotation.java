package firemerald.api.mcms.math;

import org.joml.Vector3d;

import firemerald.api.data.AbstractElement;

public abstract class EulerRotation implements IRotation
{
	public final Vector3d vec = new Vector3d();

	public EulerRotation() {}

	public EulerRotation(Vector3d vec)
	{
		this.vec.set(vec);
	}

	@Override
	public void save(AbstractElement el)
	{
		if (vec.x() != 0) el.setDouble("rX", vec.x());
		if (vec.y() != 0) el.setDouble("rY", vec.y());
		if (vec.z() != 0) el.setDouble("rZ", vec.z());
	}

	@Override
	public void load(AbstractElement el)
	{
		vec.set(el.getDouble("rX", 0), el.getDouble("rY", 0), el.getDouble("rZ", 0));
	}
}