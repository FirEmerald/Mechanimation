package firemerald.api.mcms.util;

import org.joml.Matrix4d;
import org.joml.Quaterniond;
import org.lwjgl.BufferUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.server.FMLServerHandler;

public class MCMSUtil //TODO proper MCMSAPI class
{
	public static MinecraftServer getServer()
	{
		return FMLCommonHandler.instance().getSide().isClient() ? getClientServer() : getServerServer();
	}

	@SideOnly(Side.CLIENT)
	public static MinecraftServer getClientServer()
	{
		return Minecraft.getMinecraft().getIntegratedServer();
	}

	@SideOnly(Side.SERVER)
	public static MinecraftServer getServerServer()
	{
		return FMLServerHandler.instance().getServer();
	}

	@SideOnly(Side.CLIENT)
	public static void glMultMatrix(Quaterniond q)
	{
		glMultMatrix(q.get(new Matrix4d()));
	}

	@SideOnly(Side.CLIENT)
	public static void glMultMatrix(Matrix4d m)
	{
		GlStateManager.multMatrix(m.get(BufferUtils.createFloatBuffer(16)));
	}
}