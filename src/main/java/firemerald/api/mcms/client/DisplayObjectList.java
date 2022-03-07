package firemerald.api.mcms.client;

import org.lwjgl.opengl.GL11;

import firemerald.api.mcms.util.EventListener;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;

public class DisplayObjectList implements IDisplayObject
{
	public int displayList;
	public float x, y, z;
	public float u, v;
	public float nx, ny, nz;
	public float r, g, b, a;
	public boolean setTex, setNormal, setColor;

	@Override
	public DisplayObjectList startBuilding()
	{
		displayList = GLAllocation.generateDisplayLists(1);
		GlStateManager.glNewList(displayList, GL11.GL_COMPILE);
		setTex = true;
		setNormal = true;
		setColor = true;
		r = 1;
		g = 1;
		b = 1;
		a = 1;
		return this;
	}

	@Override
	public DisplayObjectList begin(int mode)
	{
		GlStateManager.glBegin(mode);
		return this;
	}

	@Override
	public DisplayObjectList position(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	@Override
	public DisplayObjectList texture(float u, float v)
	{
		if (this.u != u || this.v != v)
		{
			this.u = u;
			this.v = v;
			setTex = true;
		}
		return this;
	}

	@Override
	public DisplayObjectList normal(float nx, float ny, float nz)
	{
		if (this.nx != nx || this.ny != ny || this.nz != nz)
		{
			this.nx = nx;
			this.ny = ny;
			this.nz = nz;
			setNormal = true;
		}
		return this;
	}

	@Override
	public DisplayObjectList color(float r, float g, float b, float a)
	{
		if (this.r != r || this.g != g || this.b != b || this.a != a)
		{
			this.r = r;
			this.g = g;
			this.b = b;
			this.a = a;
			setColor = true;
		}
		return this;
	}

	@Override
	public DisplayObjectList endVertex()
	{
		if (setColor)
		{
			GlStateManager.color(r, g, b, a);
			setColor = false;
		}
		if (setNormal)
		{
			GlStateManager.glNormal3f(nx, ny, nz);
			setNormal = false;
		}
		if (setTex)
		{
			GlStateManager.glTexCoord2f(u, v);
			setTex = false;
		}
		GlStateManager.glVertex3f(x, y, z);
		return this;
	}

	@Override
	public DisplayObjectList end()
	{
		GlStateManager.glEnd();
		return this;
	}

	@Override
	public DisplayObjectList finishBuilding()
	{
		GlStateManager.glEndList();
		return this;
	}

	@Override
	public void draw()
	{
		GlStateManager.callList(displayList);
	}

	@Override
	public void delete()
	{
		EventListener.INSTANCE.enqueueAction(() -> {
			GLAllocation.deleteDisplayLists(displayList);
			displayList = -1;
		});
	}
}