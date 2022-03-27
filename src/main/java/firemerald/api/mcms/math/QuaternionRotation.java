package firemerald.api.mcms.math;

import org.joml.Quaterniond;

import firemerald.api.data.AbstractElement;

public class QuaternionRotation implements IRotation
{
	public final Quaterniond q = new Quaterniond();

	public QuaternionRotation() {}

	public QuaternionRotation(Quaterniond q)
	{
		this.q.set(q);
	}

	@Override
	public void setFromQuaternion(Quaterniond q)
	{
		this.q.set(q);
	}

	@Override
	public void save(AbstractElement el)
	{
		/*if (q.x() != 0) */el.setDouble("qX", q.x());
		/*if (q.y() != 0) */el.setDouble("qY", q.y());
		/*if (q.z() != 0) */el.setDouble("qZ", q.z());
		/*if (q.w() != 1) */el.setDouble("qW", q.w());
	}

	@Override
	public Quaterniond getQuaternion()
	{
		return new Quaterniond(q);
	}

	@Override
	public void load(AbstractElement el)
	{
		q.set(el.getDouble("qX", 0), el.getDouble("qY", 0), el.getDouble("qZ", 0), el.getDouble("qW", 1));
	}

	@Override
	public IRotation copy()
	{
		return new QuaternionRotation(q);
	}

	@Override
	public String toString()
	{
		return "Quaternion " + q.toString();
	}
}