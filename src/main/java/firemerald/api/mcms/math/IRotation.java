package firemerald.api.mcms.math;

import org.joml.Quaterniond;

import firemerald.api.mcms.data.AbstractElement;

public interface IRotation
{
	public static final IRotation NONE = new IRotation() {
		@Override
		public void setFromQuaternion(Quaterniond q) {}

		@Override
		public void save(AbstractElement el) {}

		@Override
		public Quaterniond getQuaternion()
		{
			return new Quaterniond();
		}

		@Override
		public void load(AbstractElement el) {}

		@Override
		public IRotation copy()
		{
			return this;
		}

		@Override
		public String toString()
		{
			return "no rotation";
		}
	};

	public void setFromQuaternion(Quaterniond q);

	public void save(AbstractElement el);

	public Quaterniond getQuaternion();

	public void load(AbstractElement el);

	public IRotation copy();
}