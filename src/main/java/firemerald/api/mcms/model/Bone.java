package firemerald.api.mcms.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.joml.Matrix4d;

import firemerald.api.mcms.animation.Transformation;
import firemerald.api.data.AbstractElement;
import firemerald.api.mcms.util.ISelfTyped;
import firemerald.api.mcms.util.MiscUtil;

public abstract class Bone<T extends Bone<T>> implements ISelfTyped<T>, IModelEditable, ITransformed
{
	public static class Actual extends Bone<Actual>
	{
		public Actual(String name, Transformation defaultTransform, @Nullable Actual parent)
		{
			super(name, defaultTransform, parent);
		}

		@Override
		public String getXMLName()
		{
			return "bone";
		}

		@Override
		public Actual makeBone(String name, Transformation transform, Actual parent)
		{
			return new Actual(name, transform, parent);
		}
	}

	protected String name;
	public final Transformation defaultTransform;
	public T parent;
	public final List<T> children = new ArrayList<>();
	public boolean visible = true;
	public boolean childrenVisible = true;

	public Bone(String name, Transformation defaultTransform, @Nullable T parent)
	{
		this.name = name;
		this.defaultTransform = defaultTransform;
		if (parent == null) this.parent = null;
		else
		{
			this.parent = parent;
			parent.children.add(self());
		}
	}

	public abstract String getXMLName();

	@Override
	public Collection<? extends IModelEditable> getChildren()
	{
		return children;
	}

	@Override
	public boolean hasChildren()
	{
		return !children.isEmpty();
	}

	@Override
	public boolean canBeChild(IModelEditable candidate)
	{
		return candidate instanceof Bone;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addChild(IModelEditable child)
	{
		if (child instanceof Bone && !this.children.contains(child)) this.children.add((T) child);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addChildBefore(IModelEditable child, IModelEditable position)
	{
		if (child instanceof Bone && !this.children.contains(child))
		{
			int pos = this.children.indexOf(position);
			if (pos < 0) pos = 0;
			this.children.add(pos, (T) child);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addChildAfter(IModelEditable child, IModelEditable position)
	{
		if (child instanceof Bone && !this.children.contains(child))
		{
			int pos = this.children.indexOf(position) + 1;
			if (pos <= 0) pos = this.children.size();
			this.children.add(pos, (T) child);
		}
	}

	@Override
	public void removeChild(IModelEditable child)
	{
		if (child instanceof Bone) this.children.remove(child);
	}

	@Override
	public int getChildIndex(IModelEditable child)
	{
		if (child instanceof Bone)
		{
			if (children.contains(child)) return children.indexOf(child);
			else return -1;
		}
		else return -1;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addChildAt(IModelEditable child, int index)
	{
		if (child instanceof Bone)
		{
			if (!children.contains(child))
			{
				if (index <= 0) index = 0;
				this.children.add(index, (T) child);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void movedTo(IEditableParent oldParent, IEditableParent newParent)
	{
		this.parent = oldParent instanceof Bone ? (T) oldParent : null;
		Matrix4d targetTransform = getTransformation();
		this.parent = newParent instanceof Bone ? (T) newParent : null;
		Matrix4d parentTransform = parent == null ? new Matrix4d() : parent.getTransformation();
		Matrix4d newTransform = parentTransform.invert().mul(targetTransform);
		this.defaultTransform.setFromMatrix(newTransform);
	}

	@SuppressWarnings("unchecked")
	@Override
	public IModelEditable copy(IEditableParent newParent, IRigged<?, ?> model) //TODO action
	{
		T bone = null;
		if (newParent instanceof Bone)
		{
			bone = this.cloneSingle((T) newParent, model);
			model.updateBonesList();
		}
		else if (newParent instanceof IModel)
		{
			bone = this.cloneSingle(null, model);
			((IModel<?, ?>) newParent).addChild(bone);
		}
		if (bone != null) copyChildren(bone, model);
		return bone;
	}

	public void copyChildren(T newParent, IRigged<?, ?> model)
	{
		for (T child : children) child.copy(newParent, model);
	}

	public void updateTex()
	{
		children.forEach(child -> child.updateTex());
	}

	@Override
	public Transformation getDefaultTransformation()
	{
		return this.defaultTransform;
	}

	public void setDefTransform(Map<String, Matrix4d> map)
	{
		map.put(this.name, defaultTransform.getTransformation());
		for (T bone : this.children) bone.setDefTransform(map);
	}

	public void cleanUp()
	{
		this.doCleanUp();
		for (T child : children) child.cleanUp();
	}

	public void doCleanUp() {}

	@Override
	public String toString()
	{
		return name + ":" + this.getClass().toString();
	}

	public float tX()
	{
		return defaultTransform.translation.x();
	}

	public void tX(float x)
	{
		defaultTransform.translation.x = x;
	}

	public float tY()
	{
		return defaultTransform.translation.y();
	}

	public void tY(float y)
	{
		defaultTransform.translation.y = y;
	}

	public float tZ()
	{
		return defaultTransform.translation.z();
	}

	public void tZ(float z)
	{
		defaultTransform.translation.z = z;
	}

	@Override
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void addBone(T bone)
	{
		this.children.add(bone);
	}

	public void removeBone(T child)
	{
		this.children.remove(child);
	}

	@Override
	public Matrix4d getTransformation()
	{
		Matrix4d mat = defaultTransform.getTransformation();
		if (parent != null) parent.getTransformation().mul(mat, mat);
		return mat;
	}

	public Matrix4d getTransformation(Map<String, Matrix4d> pose)
	{
		Matrix4d mat = pose.get(name);
		if (mat == null) mat = this.defaultTransform.getTransformation();
		if (parent != null) parent.getTransformation(pose).mul(mat, mat);
		return mat;
	}

	public void addToXML(AbstractElement addTo)
	{
		AbstractElement el = addTo.addChild(getXMLName());
		addDataToXML(el);
		addChildrenToXML(el);
	}

	public void addDataToXML(AbstractElement el)
	{
		saveData(el);
	}

	protected void saveData(AbstractElement el)
	{
		el.setString("name", name);
		defaultTransform.save(el);
	}

	public void addChildrenToXML(AbstractElement addTo)
	{
		this.children.forEach(child -> child.addToXML(addTo));
	}

	public void loadFromXML(AbstractElement el)
	{
		loadData(el);
		loadChildrenFromXML(el);
	}

	protected void loadData(AbstractElement el)
	{
		defaultTransform.load(el);
	}

	public void loadChildrenFromXML(AbstractElement el)
	{
		children.clear();
		for (AbstractElement child : el.getChildren()) tryLoadChild(child);
	}

	public void tryLoadChild(AbstractElement el)
	{
		if (el.getName().equals(getXMLName()))
		{
			String name = el.getString("name", "unnamed bone");
			Transformation transform = new Transformation(el);
			makeBone(name, transform, self()).loadFromXML(el);
		}
	}

	public abstract T makeBone(String name, Transformation transform, T parent);

	public void setTransforms(Bone<?> ref)
	{
		this.defaultTransform.rotation = ref.defaultTransform.rotation;
		this.defaultTransform.translation.set(ref.defaultTransform.translation);
		Bone<?>[] roots = this.children.toArray(new Bone[this.children.size()]);
		ref.children.forEach(bone -> {
			boolean flag = true;
			for (Bone<?> root : roots) if (root.name.equals(bone.name))
			{
				root.setTransforms(bone);
				flag = false;
				break;
			}
			if (flag)
			{
				T root = makeBone(bone.name, new Transformation(), self());
				root.setTransforms(bone);
			}
		});
	}

	@Override
	public int hashCode()
	{
		return name.hashCode();
	}

	public T cloneSingle(T clonedParent)
	{
		return makeBone(this.name, this.defaultTransform.copy(), clonedParent);
	}

	public T cloneSingle(T clonedParent, IRigged<?, ?> model)
	{
		return makeBone(MiscUtil.getNewBoneName(this.name, model), this.defaultTransform.copy(), clonedParent);
	}

	public T cloneToModel(T clonedParent)
	{
		return makeBone(this.name, this.defaultTransform.copy(), clonedParent);
	}

	public T cloneObject(T clonedParent)
	{
		T newBone = cloneSingle(clonedParent);
		this.children.forEach(child -> child.cloneObject(newBone));
		return newBone;
	}

	public void cloneProperties(Bone<?> from)
	{
		this.defaultTransform.set(from.defaultTransform);
	}

	@Override
	public boolean isVisible()
	{
		return visible;
	}

	@Override
	public void setVisible(boolean visible)
	{
		this.childrenVisible = this.visible = visible;
	}

	public Actual cloneToSkeleton(Actual parent)
	{
		return new Bone.Actual(name, defaultTransform.copy(), parent);
	}
}