package firemerald.api.mcms.model;

import java.util.Map;

import javax.annotation.Nullable;

import org.joml.Matrix4d;

import firemerald.api.mcms.animation.Transformation;
import firemerald.api.mcms.util.RaytraceResult;

public abstract class ObjectBone<T extends ObjectBone<T>> extends Bone<T> implements IRaytraceTarget
{
	public ObjectBone(String name, Transformation defaultTransform, @Nullable T parent)
	{
		super(name, defaultTransform, parent);
	}

	@Override
	public abstract T makeBone(String name, Transformation transform, T parent);

	public RaytraceResult raytrace(float fx, float fy, float fz, float dx, float dy, float dz, Map<String, Matrix4d> transformations, Matrix4d transformation)
	{
		RaytraceResult result = raytraceLocal(fx, fy, fz, dx, dy, dz, transformations, transformation);
		if (childrenVisible) for (T child : children)
		{
			Matrix4d transform = transformations.get(child.name);
			if (transform == null) transform = new Matrix4d(transformation);
			else transform = transformation.mul(transform, new Matrix4d());
			RaytraceResult res = child.raytrace(fx, fy, fz, dx, dy, dz, transformations, transform);
			if (res != null && (result == null || res.m < result.m)) result = res;
		}
		return result;
	}

	public abstract RaytraceResult raytraceLocal(float fx, float fy, float fz, float dx, float dy, float dz, Map<String, Matrix4d> transformations, Matrix4d transformation);
}