package firemerald.api.mcms.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4d;
import org.joml.Vector3f;
import org.joml.Vector4d;
import org.joml.Vector4f;

import firemerald.api.mcms.animation.Transformation;
import firemerald.api.mcms.math.MathUtils;
import firemerald.api.mcms.util.RaytraceResult;

public abstract class MeshedBone<T extends MeshedBone<T>> extends ObjectBone<T> implements IMeshed
{
	public static class Actual extends MeshedBone<Actual> implements IMeshed
	{
		public Actual(String name, Transformation defaultTransform, Actual parent)
		{
			super(name, defaultTransform, parent);
		}

		public Actual(String name, Transformation defaultTransform, Actual parent, List<Vector3f[]> triangles)
		{
			super(name, defaultTransform, parent, triangles);
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

	public final List<Vector3f[]> mesh = new ArrayList<>();

	public MeshedBone(String name, Transformation defaultTransform, T parent)
	{
		super(name, defaultTransform, parent);
	}

	public MeshedBone(String name, Transformation defaultTransform, T parent, List<Vector3f[]> triangles)
	{
		this(name, defaultTransform, parent);
		setMesh(triangles);
	}

	@Override
	public List<Vector3f[]> getMesh()
	{
		return mesh;
	}

	public void setMesh(List<Vector3f[]> triangles)
	{
		this.mesh.clear();
		this.mesh.addAll(triangles);
	}

	@Override
	public RaytraceResult raytraceLocal(float fx, float fy, float fz, float dx, float dy, float dz, Map<String, Matrix4d> transformations, Matrix4d transformation)
	{
		RaytraceResult res = null;
		Float m = null;
		for (Vector3f[] triangle : mesh)
		{
			Vector4f p1 = MathUtils.toVector4f(transformation.transform(new Vector4d(triangle[0], 1)));
			Vector4f p2 = MathUtils.toVector4f(transformation.transform(new Vector4d(triangle[1], 1)));
			Vector4f p3 = MathUtils.toVector4f(transformation.transform(new Vector4d(triangle[2], 1)));
			Float d = MathUtils.rayTrace(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z, p3.x, p3.y, p3.z, fx, fy, fz, dx, dy, dz);
			if (d != null && d > 0 && (m == null || d < m)) m = d;
		}
		if (m != null && (res == null || m < res.m)) res = new RaytraceResult(this, m);
		return res;
	}
}