package firemerald.api.mcms.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import org.joml.Matrix4d;
import org.joml.Vector3f;
import org.joml.Vector4d;
import org.joml.Vector4f;

import firemerald.api.mcms.animation.Transformation;
import firemerald.api.mcms.math.MathUtils;
import firemerald.api.mcms.model.IMeshed;
import firemerald.api.mcms.model.ObjData;
import firemerald.api.mcms.model.RenderBone;
import firemerald.api.mcms.util.RaytraceResult;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class RenderObjBone<T extends RenderObjBone<T>> extends RenderBone<T> implements IMeshed
{
	public static class Actual extends RenderObjBone<Actual>
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
			return new Actual(name, transform, parent); //TODO
		}

		@Override
		public String getXMLName()
		{
			return "bone";
		}

	}

	private Supplier<IDisplayObject> make;
	private IDisplayObject object;
	public final List<Vector3f[]> mesh = new ArrayList<>();

	public RenderObjBone(String name, Transformation defaultTransform, T parent, List<int[][]> mesh, ObjData obj, Matrix4d transformation)
	{
		super(name, defaultTransform, parent);
		make = () -> IDisplayObject.makeNewObject(name, mesh, obj, transformation);
		this.setMesh(obj.getTriangles(mesh, transformation));
	}

	public RenderObjBone(String name, Transformation defaultTransform, T parent)
	{
		super(name, defaultTransform, parent);
		make = () -> IDisplayObject.makeNewObject(false);
	}

	public IDisplayObject getDisplayObject()
	{
		if (make != null)
		{
			object = make.get();
			make = null;
		}
		return object;
	}

	@Override
	public List<Vector3f[]> getMesh()
	{
		return mesh;
	}

	@Override
	public void doRender(@Nullable Object holder, Runnable defaultTexture)
	{
		getDisplayObject().draw();
	}

	@Override
	public void doCleanUp()
	{
		if (object != null) object.delete();
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