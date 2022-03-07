package firemerald.api.mcms.model;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.joml.Matrix4d;

import firemerald.api.mcms.animation.Transformation;
import firemerald.api.mcms.client.RenderObjBone;
import firemerald.api.mcms.model.effects.EffectsData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ObjModel<M extends ObjModel<M, T>, T extends Bone<T>> extends MultiModel<M, T>
{
	public static class Server extends ObjModel<Server, ObjBone.Actual> //server-side models, contains no render info
	{
		public Server(ObjData obj, Skeleton skeleton)
		{
			super(obj, skeleton);
		}

		@Override
		public ObjBone.Actual makeNew(String name, Transformation transformation, ObjBone.Actual parent)
		{
			return new ObjBone.Actual(name, transformation, parent);
		}

		@Override
		public Server newModel(List<ObjBone.Actual> base)
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ObjBone.Actual makeBone(String name, @Nullable Bone.Actual skeletonBone, @Nullable ObjBone.Actual parent)
		{
			return new ObjBone.Actual(name, skeletonBone != null ? skeletonBone.defaultTransform.copy() : new Transformation(), parent);
		}

		@Override
		public ObjBone.Actual makeBone(String name, @Nullable Bone.Actual skeletonBone, @Nullable ObjBone.Actual parent, Matrix4d transformation, ObjData obj)
		{
			return new ObjBone.Actual(name, skeletonBone != null ? skeletonBone.defaultTransform.copy() : new Transformation(), parent, obj.groupObjects.get(name), obj, transformation);
		}
	}

	@SideOnly(Side.CLIENT)
	public static class Client extends ObjModel<Client, RenderObjBone.Actual> //client-side model, contains render info
	{
		public Client(ObjData obj, Skeleton skeleton, @Nullable EffectsData effects)
		{
			super(obj, skeleton);
			if (effects != null) effects.apply(this);
		}

		@Override
		public RenderObjBone.Actual makeNew(String name, Transformation transformation, RenderObjBone.Actual parent)
		{
			return new RenderObjBone.Actual(name, transformation, parent);
		}

		@Override
		public Client newModel(List<RenderObjBone.Actual> base)
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public RenderObjBone.Actual makeBone(String name, @Nullable Bone.Actual skeletonBone, @Nullable RenderObjBone.Actual parent)
		{
			return new RenderObjBone.Actual(name, skeletonBone != null ? skeletonBone.defaultTransform.copy() : new Transformation(), parent);
		}

		@Override
		public RenderObjBone.Actual makeBone(String name, @Nullable Bone.Actual skeletonBone, @Nullable RenderObjBone.Actual parent, Matrix4d transformation, ObjData obj)
		{
			return new RenderObjBone.Actual(name, skeletonBone != null ? skeletonBone.defaultTransform.copy() : new Transformation(), parent, obj.groupObjects.get(name), obj, transformation);
		}
	}

	public ObjModel(ObjData obj, Skeleton skeleton)
	{
		List<String> unusedNames = new ArrayList<>();
		unusedNames.addAll(obj.groupObjects.keySet());
		skeleton.getRootBones().forEach(bone -> applySkeleton(bone, null, skeleton, new Matrix4d(), unusedNames, obj));
		unusedNames.forEach(name -> this.addRootBone(makeBone(name, null, null, new Matrix4d(), obj), false));
		this.updateBonesList();
	}

	private void applySkeleton(Bone.Actual bone, @Nullable T parent, Skeleton skeleton, Matrix4d transformation, List<String> unusedNames, ObjData obj)
	{
		unusedNames.remove(bone.name);
		T b;
		final Matrix4d trans = transformation.mul(bone.defaultTransform.getTransformation(), new Matrix4d());
		if (obj.groupObjects.get(bone.name) != null) b = makeBone(bone.name, bone, parent, trans.invert(new Matrix4d()), obj);
		else b = makeBone(bone.name, bone, parent);
		if (parent == null) this.addRootBone(b, false);
		bone.children.forEach(child -> applySkeleton(child, b, skeleton, trans, unusedNames, obj));
	}

	public abstract T makeBone(String name, @Nullable Bone.Actual skeletonBone, @Nullable T parent);

	public abstract T makeBone(String name, @Nullable Bone.Actual skeletonBone, @Nullable T parent, Matrix4d transformation, ObjData obj);
}