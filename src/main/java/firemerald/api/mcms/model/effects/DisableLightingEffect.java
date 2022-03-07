package firemerald.api.mcms.model.effects;

import javax.annotation.Nullable;

import firemerald.api.mcms.model.IEditableParent;
import firemerald.api.mcms.model.IRigged;
import firemerald.api.mcms.model.RenderBone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class DisableLightingEffect extends BoneEffect
{
	public DisableLightingEffect(String name, @Nullable RenderBone<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getXMLName()
	{
		return "no_lighting";
	}

	@Override
	public DisableLightingEffect cloneObject(RenderBone<?> clonedParent)
	{
		return new DisableLightingEffect(this.name, clonedParent);
	}

	@Override
	public DisableLightingEffect copy(IEditableParent newParent, IRigged<?, ?> iRigged)
	{
		if (newParent instanceof RenderBone<?>) return cloneObject((RenderBone<?>) newParent);
		else return null;
	}

	@Override
	public void doPreRender(@Nullable Object holder, Runnable defaultTex) //TODO check render state!
	{
		Minecraft.getMinecraft().entityRenderer.disableLightmap();
		GlStateManager.disableLighting();
	}

	@Override
	public void doPostRenderBone(@Nullable Object holder, Runnable defaultTex)
	{
	}

	@Override
	public void doPostRenderChildren(@Nullable Object holder, Runnable defaultTex)
	{
		Minecraft.getMinecraft().entityRenderer.enableLightmap();
		GlStateManager.enableLighting();
	}

	@Override
	public void doCleanUp() {}
}