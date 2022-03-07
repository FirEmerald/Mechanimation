package firemerald.mechanimation.client.gui.inventory;

import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.api.util.Rectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public interface IGuiElements
{
	public static final ResourceLocation GUI_ELEMENTS_TEXTURE = new ResourceLocation(MechanimationAPI.MOD_ID, "textures/gui/container/elements.png");
	public static final int WIDTH = 256, HEIGHT = 256;
	public static final float SCALE_U = 1f / WIDTH, SCALE_V = 1f / HEIGHT;
	public static final Rectangle ENERGY_BAR_TEX = Rectangle.ofSize(0, 0, 2, 40);
	public static final GuiProgressUp ENERGY_BAR_PROGRESS = new GuiProgressUp(ENERGY_BAR_TEX);
	public static final Rectangle WORK_BAR_TEX = Rectangle.ofSize(2, 0, 2, 40);
	public static final GuiProgressUp WORK_BAR_PROGRESS = new GuiProgressUp(WORK_BAR_TEX);
	public static final Rectangle FLUID_BAR_TEX = Rectangle.ofSize(4, 0, 6, 24);
	public static final GuiDecor FLUID_BAR_DECOR = new GuiDecor(FLUID_BAR_TEX);
	public static final Rectangle PROGRESS_RIGHT_TEX = Rectangle.ofSize(4, 24, 22, 16);
	public static final GuiProgressRight PROGRESS_RIGHT_PROGRESS = new GuiProgressRight(PROGRESS_RIGHT_TEX);
	public static final Rectangle PROGRESS_HALF_LEFT_TEX = Rectangle.ofSize(10, 0, 14, 6);
	public static final GuiProgressLeft PROGRESS_HALF_LEFT_PROGRESS = new GuiProgressLeft(PROGRESS_HALF_LEFT_TEX);
	public static final Rectangle PROGRESS_HALF_RIGHT_TEX = Rectangle.ofSize(10, 6, 14, 6);
	public static final GuiProgressRight PROGRESS_HALF_RIGHT_PROGRESS = new GuiProgressRight(PROGRESS_HALF_RIGHT_TEX);
	public static final Rectangle PROGRESS_BURN_TEX = Rectangle.ofSize(24, 0, 13, 13);
	public static final GuiProgressUp PROGRESS_BURN_PROGRESS = new GuiProgressUp(PROGRESS_BURN_TEX);

	public static void draw(ResourceLocation tex, int x, int y, float z, int w, int h, double u1, double v1, double u2, double v2)
	{
		Minecraft.getMinecraft().getTextureManager().bindTexture(tex);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x    , y    , z).tex(u1, v1).endVertex();
        bufferbuilder.pos(x    , y + h, z).tex(u1, v2).endVertex();
        bufferbuilder.pos(x + w, y + h, z).tex(u2, v2).endVertex();
        bufferbuilder.pos(x + w, y    , z).tex(u2, v1).endVertex();
        tessellator.draw();
	}

	public static abstract class GuiProgress
	{
		public final ResourceLocation tex;
		public final int w, h;
		public final float u1, v1, u2, v2;

		public GuiProgress(ResourceLocation tex, int w, int h, float u1, float v1, float u2, float v2)
		{
			this.tex = tex;
			this.w = w;
			this.h = h;
			this.u1 = u1;
			this.v1 = v1;
			this.u2 = u2;
			this.v2 = v2;
		}

		public abstract void render(int x, int y, float z, double progress);
	}

	public static abstract class GuiProgressBase extends GuiProgress
	{
		GuiProgressBase(Rectangle texBounds)
		{
			super(GUI_ELEMENTS_TEXTURE, texBounds.w, texBounds.h, texBounds.x1 * SCALE_U, texBounds.y1 * SCALE_V, texBounds.x2 * SCALE_U, texBounds.y2 * SCALE_V);
		}

		GuiProgressBase(ResourceLocation tex, int w, int h, float u1, float v1, float u2, float v2)
		{
			super(tex, w, h, u1, v1, u2, v2);
		}
	}

	public static class GuiDecor extends GuiProgressBase
	{
		public GuiDecor(Rectangle texBounds)
		{
			super(GUI_ELEMENTS_TEXTURE, texBounds.w, texBounds.h, texBounds.x1 * SCALE_U, texBounds.y1 * SCALE_V, texBounds.x2 * SCALE_U, texBounds.y2 * SCALE_V);
		}

		public GuiDecor(ResourceLocation tex, int w, int h, float u1, float v1, float u2, float v2)
		{
			super(tex, w, h, u1, v1, u2, v2);
		}

		@Override
		public void render(int x, int y, float z, double progress)
		{
			draw(tex, x, y, z, w, h, u1, v1, u2, v2);
		}
	}

	public static class GuiProgressDown extends GuiProgressBase
	{
		public GuiProgressDown(Rectangle texBounds)
		{
			super(GUI_ELEMENTS_TEXTURE, texBounds.w, texBounds.h, texBounds.x1 * SCALE_U, texBounds.y1 * SCALE_V, texBounds.x2 * SCALE_U, texBounds.y2 * SCALE_V);
		}

		public GuiProgressDown(ResourceLocation tex, int w, int h, float u1, float v1, float u2, float v2)
		{
			super(tex, w, h, u1, v1, u2, v2);
		}

		@Override
		public void render(int x, int y, float z, double progress)
		{
			if (progress > 0)
			{
				int p = (int) (progress * h);
				double pd = (double) p / (double) h;
				draw(tex, x, y, z, w, p, u1, v1, u2, lerp(v1, v2, pd));
			}
		}
	}

	public static class GuiProgressUp extends GuiProgressBase
	{
		public GuiProgressUp(Rectangle texBounds)
		{
			super(GUI_ELEMENTS_TEXTURE, texBounds.w, texBounds.h, texBounds.x1 * SCALE_U, texBounds.y1 * SCALE_V, texBounds.x2 * SCALE_U, texBounds.y2 * SCALE_V);
		}

		public GuiProgressUp(ResourceLocation tex, int w, int h, float u1, float v1, float u2, float v2)
		{
			super(tex, w, h, u1, v1, u2, v2);
		}

		@Override
		public void render(int x, int y, float z, double progress)
		{
			if (progress > 0)
			{
				int p = (int) (progress * h);
				double pd = (double) p / (double) h;
				draw(tex, x, y + h - p, z, w, p, u1, lerp(v2, v1, pd), u2, v2);
			}
		}
	}

	public static class GuiProgressRight extends GuiProgressBase
	{
		public GuiProgressRight(Rectangle texBounds)
		{
			super(GUI_ELEMENTS_TEXTURE, texBounds.w, texBounds.h, texBounds.x1 * SCALE_U, texBounds.y1 * SCALE_V, texBounds.x2 * SCALE_U, texBounds.y2 * SCALE_V);
		}

		public GuiProgressRight(ResourceLocation tex, int w, int h, float u1, float v1, float u2, float v2)
		{
			super(tex, w, h, u1, v1, u2, v2);
		}

		@Override
		public void render(int x, int y, float z, double progress)
		{
			if (progress > 0)
			{
				int p = (int) (progress * w);
				double pd = (double) p / (double) w;
				draw(tex, x, y, z, p, h, u1, v1, lerp(u1, u2, pd), v2);
			}
		}
	}

	public static class GuiProgressLeft extends GuiProgressBase
	{
		public GuiProgressLeft(Rectangle texBounds)
		{
			super(GUI_ELEMENTS_TEXTURE, texBounds.w, texBounds.h, texBounds.x1 * SCALE_U, texBounds.y1 * SCALE_V, texBounds.x2 * SCALE_U, texBounds.y2 * SCALE_V);
		}

		public GuiProgressLeft(ResourceLocation tex, int w, int h, float u1, float v1, float u2, float v2)
		{
			super(tex, w, h, u1, v1, u2, v2);
		}

		@Override
		public void render(int x, int y, float z, double progress)
		{
			if (progress > 0)
			{
				int p = (int) (progress * w);
				double pd = (double) p / (double) w;
				draw(tex, x + w - p, y, z, p, h, lerp(u2, u1, pd), v1, u2, v2);
			}
		}
	}

	public static int lerp(int v1, int v2, double a)
	{
		return (int) lerp((double) v1, (double) v2, a);
	}

	public static double lerp(double v1, double v2, double a)
	{
		return v1 + (v2 - v1) * a;
	}
}