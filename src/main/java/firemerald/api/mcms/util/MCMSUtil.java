package firemerald.api.mcms.util;

import org.joml.Matrix4d;
import org.lwjgl.BufferUtils;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MCMSUtil //TODO proper MCMSAPI class
{
	@SideOnly(Side.CLIENT)
	public static void glMultMatrix(Matrix4d m)
	{
		GlStateManager.multMatrix(m.get(BufferUtils.createFloatBuffer(16)));
	}
}