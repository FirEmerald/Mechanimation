package firemerald.api.mcms.model;

import java.util.Map;

import javax.annotation.Nullable;

import org.joml.Matrix4d;

import firemerald.api.mcms.util.IClonableObject;
import firemerald.api.mcms.util.RaytraceResult;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IModel<M extends IModel<M, T>, T extends Bone<T>> extends IRaytraceTarget, IRigged<IModel<M, T>, T>, IClonableObject<IModel<M, T>>
{
	@SideOnly(Side.CLIENT)
	public void render(Map<String, Matrix4d> pos, @Nullable Object holder, Runnable defaultTexture);

	public void cleanUp();

	public default RaytraceResult rayTrace(float fx, float fy, float fz, float dx, float dy, float dz, Map<String, Matrix4d> map)
	{
		return rayTrace(fx, fy, fz, dx, dy, dz, map, new Matrix4d());
	}

	public RaytraceResult rayTrace(float fx, float fy, float fz, float dx, float dy, float dz, Map<String, Matrix4d> map, Matrix4d root);

	@Override
	public default String getElementName()
	{
		return "model";
	}

	public void updateTex();
}