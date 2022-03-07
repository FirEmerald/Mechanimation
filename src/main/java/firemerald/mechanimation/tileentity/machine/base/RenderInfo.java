package firemerald.mechanimation.tileentity.machine.base;

import firemerald.mechanimation.client.gui.inventory.IGuiElements.GuiProgress;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderInfo
{
	public final int x, y, x2, y2, w, h;
	public final GuiProgress render;

	public RenderInfo(int x, int y, GuiProgress render)
	{
		this(x, y, render.w, render.h, render);
	}

	public RenderInfo(int x, int y, int w, int h, GuiProgress render)
	{
		this.x = x;
		this.y = y;
		this.x2 = x + w;
		this.y2 = y + h;
		this.w = w;
		this.h = h;
		this.render = render;
	}
}