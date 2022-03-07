package firemerald.api.mcms.model.effects;

import javax.annotation.Nullable;

import org.joml.Matrix4d;

import firemerald.api.mcms.animation.Transformation;
import firemerald.api.mcms.data.AbstractElement;
import firemerald.api.mcms.model.IEditableParent;
import firemerald.api.mcms.model.RenderBone;

public abstract class PosedBoneEffect extends BoneEffect
{
	public PosedBoneEffect(String name, @Nullable RenderBone<?> parent, Transformation transform)
	{
		super(name, parent, transform);
	}

	public PosedBoneEffect(String name, @Nullable RenderBone<?> parent)
	{
		super(name, parent);
	}

	@Override
	public void loadFromXML(AbstractElement el)
	{
		super.loadFromXML(el);
		transform.load(el);
	}

	@Override
	public void saveToXML(AbstractElement el)
	{
		super.saveToXML(el);
		transform.save(el);
	}

	@Override
	public void movedTo(IEditableParent oldParent, IEditableParent newParent)
	{
		this.parent = oldParent instanceof RenderBone<?> ? (RenderBone<?>) oldParent : null;
		Matrix4d targetTransform = getTransformation();
		this.parent = newParent instanceof RenderBone<?> ? (RenderBone<?>) newParent : null;
		Matrix4d parentTransform = parent == null ? new Matrix4d() : parent.getTransformation();
		Matrix4d newTransform = parentTransform.invert().mul(targetTransform);
		this.transform.setFromMatrix(newTransform);
	}

	public float tX()
	{
		return transform.translation.x();
	}

	public void tX(float x)
	{
		transform.translation.x = x;
	}

	public float tY()
	{
		return transform.translation.y();
	}

	public void tY(float y)
	{
		transform.translation.y = y;
	}

	public float tZ()
	{
		return transform.translation.z();
	}

	public void tZ(float z)
	{
		transform.translation.z = z;
	}
}