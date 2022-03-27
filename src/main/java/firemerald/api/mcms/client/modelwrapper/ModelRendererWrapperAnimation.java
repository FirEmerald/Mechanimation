package firemerald.api.mcms.client.modelwrapper;

import java.util.Collection;
import java.util.Map;

import org.joml.Matrix4d;

import firemerald.api.mcms.animation.IAnimation;
import firemerald.api.data.AbstractElement;
import firemerald.api.mcms.model.Bone;
import firemerald.api.mcms.model.IRigged;
import firemerald.api.mcms.model.ITransformsProvider;
import net.minecraft.client.model.ModelRenderer;

public class ModelRendererWrapperAnimation implements IAnimation
{
	public final IModelWrapper wrapper;

	public ModelRendererWrapperAnimation(IModelWrapper wrapper)
	{
		this.wrapper = wrapper;
	}

	@Override
	public String getElementName()
	{
		return null;
	}

	@Override
	public void save(AbstractElement el) {}

	@Override
	public void load(AbstractElement el) {}

	@Override
	public IAnimation cloneObject()
	{
		return new ModelRendererWrapperAnimation(wrapper);
	}

	@Override
	public Map<String, Matrix4d> getBones(Map<String, Matrix4d> map, float frame, Collection<? extends Bone<?>> bones)
	{
		for (ModelRenderer renderer : wrapper.getComponents()) getBones(map, renderer, wrapper.getName(renderer));
		return map;
	}

	public void getBones(Map<String, Matrix4d> map, ModelRenderer renderer, String name)
	{
		if (map.containsKey(name));
		{
			Matrix4d mat = map.get(name);
			if (mat == null) map.put(name, mat = new Matrix4d());
			mat.identity()
			.translate((renderer.offsetX + renderer.rotationPointX) / 16, (renderer.offsetY + renderer.rotationPointY) / 16, (renderer.offsetZ + renderer.rotationPointZ) / 16)
			.rotateZ(renderer.rotateAngleZ)
			.rotateY(renderer.rotateAngleY)
			.rotateX(renderer.rotateAngleX)
			.rotateX(Math.PI);
		}
		if (renderer.childModels != null) renderer.childModels.forEach(child -> getBones(map, child, wrapper.getName(child)));
	}

	@Override
	public float getLength()
	{
		return 0;
	}

	@Override
	public float getAnimTime(float timestamp)
	{
		return 0;
	}

	@Override
	public void reverseAnimation(IRigged<?, ?> rig) {}

	@Override
	public boolean isRelative()
	{
		return false;
	}

	@Override
	public void setRelative(boolean relative, ITransformsProvider transforms) {}

}
