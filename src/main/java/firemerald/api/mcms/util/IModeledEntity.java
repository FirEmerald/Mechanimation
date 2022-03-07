package firemerald.api.mcms.util;

import org.joml.Matrix4d;

import firemerald.api.mcms.animation.AnimationState;
import firemerald.api.mcms.model.IModel;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IModeledEntity
{
	public default boolean isTransparent()
	{
		return false;
	}

	public default Matrix4d getRootTick()
	{
		return new Matrix4d();
	}

	public AnimationState[] getAnimationStates(float partial);

	@SideOnly(Side.CLIENT)
	public default Matrix4d getRootRender(float partial)
	{
		return getRootTick();
	}

	@SideOnly(Side.CLIENT)
	public IModel<?, ?> getModel(float partial);

	@SideOnly(Side.CLIENT)
	public default Runnable bindTexture(float partial)
	{
		ResourceLocation tex = getTexture();
		return () -> Minecraft.getMinecraft().getTextureManager().bindTexture(tex);
	}

	@SideOnly(Side.CLIENT)
	public ResourceLocation getTexture();
}