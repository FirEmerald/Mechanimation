package firemerald.api.mcms.client.renderers;

import java.util.Map;

import javax.annotation.Nullable;

import org.joml.Matrix4d;

import firemerald.api.mcms.MCMSAPI;
import firemerald.api.mcms.animation.AnimationState;
import firemerald.api.mcms.model.IModel;
import firemerald.api.mcms.util.IModeledEntity;
import firemerald.api.mcms.util.MCMSUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class TileEntityRendererModel<T extends TileEntity & IModeledEntity> extends TileEntitySpecialRenderer<T>
{
	public final ItemStack itemStack = new ItemStack(Items.IRON_INGOT);

	@Override
    public void render(@Nullable T tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
		IModel<?, ?> model = getModel(tile, partialTicks);
		if (model != null)
		{
			if (tile.isTransparent())
			{
				if (!MCMSAPI.isViveCraft) GlStateManager.depthMask(true);
				else
				{
					GlStateManager.enableBlend();
					GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
				}
			}
			GlStateManager.pushMatrix();
			GlStateManager.translate((float) x + .5f, (float) y, (float) z + .5f);
			MCMSUtil.glMultMatrix(getRoot(tile, partialTicks));
			Map<String, Matrix4d> pose = model.getPose(getAnimationStates(tile, partialTicks));
			Runnable bind = bindTexture(tile, partialTicks);
			bind.run();
			model.render(pose, tile, bind);
			GlStateManager.popMatrix();
			if (tile.isTransparent())
			{
				if (!MCMSAPI.isViveCraft) GlStateManager.depthMask(false);
				else GlStateManager.disableBlend();
			}
		}
    }

	public IModel<?, ?> getModel(@Nullable T tile, float partial)
	{
		return tile == null ? null : tile.getModel(partial);
	}

	public Runnable bindTexture(@Nullable T tile, float partial)
	{
		if (tile != null) return tile.bindTexture(partial);
		else return () -> Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_MISSING_TEXTURE);
	}

	public AnimationState[] getAnimationStates(@Nullable T tile, float partial)
	{
		return tile == null ? new AnimationState[0] : tile.getAnimationStates(partial);
	}

	public Matrix4d getRoot(@Nullable T tile, float partial)
	{
		return tile == null ? new Matrix4d() : tile.getRootRender(partial);
	}
}