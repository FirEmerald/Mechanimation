package firemerald.api.mcms.client.modelwrapper;

import java.util.Map;

import org.joml.Matrix4d;

import firemerald.api.mcms.animation.IAnimation;
import net.minecraft.client.model.ModelRenderer;

public class ModelRendererWrapperAnimationAlt extends ModelRendererWrapperAnimation
{
	public ModelRendererWrapperAnimationAlt(IModelWrapper wrapper)
	{
		super(wrapper);
	}

	@Override
	public IAnimation cloneObject()
	{
		return new ModelRendererWrapperAnimationAlt(wrapper);
	}

	public void getBones(Map<String, Matrix4d> map, ModelRenderer renderer)
	{
		if (renderer.boxName != null && map.containsKey(renderer.boxName));
		{
			Matrix4d mat = map.get(renderer.boxName);
			mat.identity()
			.translate(renderer.rotationPointX, renderer.rotationPointY, renderer.rotationPointZ)
			.rotateY(renderer.rotateAngleY)
			.rotateX(renderer.rotateAngleX)
			.rotateZ(renderer.rotateAngleZ);
		}
	}
}