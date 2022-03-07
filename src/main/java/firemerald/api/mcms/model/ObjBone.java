package firemerald.api.mcms.model;

import java.util.List;

import org.joml.Matrix4d;

import firemerald.api.mcms.animation.Transformation;

public abstract class ObjBone<T extends ObjBone<T>> extends MeshedBone<T>
{
	public static class Actual extends ObjBone<Actual>
	{
		public Actual(String name, Transformation defaultTransform, Actual parent, List<int[][]> mesh, ObjData obj, Matrix4d transformation)
		{
			super(name, defaultTransform, parent, mesh, obj, transformation);
		}

		public Actual(String name, Transformation defaultTransform, Actual parent)
		{
			super(name, defaultTransform, parent);
		}

		@Override
		public Actual makeBone(String name, Transformation transform, Actual parent)
		{
			return new Actual(name, transform, parent);
		}

		@Override
		public String getXMLName()
		{
			return "bone";
		}

	}

	public ObjBone(String name, Transformation defaultTransform, T parent, List<int[][]> mesh, ObjData obj, Matrix4d transformation)
	{
		super(name, defaultTransform, parent);
		this.setMesh(obj.getTriangles(mesh, transformation));
	}

	public ObjBone(String name, Transformation defaultTransform, T parent)
	{
		super(name, defaultTransform, parent);
	}
}