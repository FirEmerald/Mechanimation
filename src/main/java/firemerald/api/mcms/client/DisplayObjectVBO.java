package firemerald.api.mcms.client;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import firemerald.api.mcms.util.EventListener;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;

public class DisplayObjectVBO implements IDisplayObject
{
	public final List<int[]> buffers = new ArrayList<>();
	public float x, y, z;
	public float u, v;
	public float nx, ny, nz;
	public float r = 1, g = 1, b = 1, a = 1;
	public int buffer, mode, numVertices = 0;
    private ByteBuffer byteBuffer;
    public static final int VERTEX_BYTES = 36;
    public static final int ALLOC_SIZE = VERTEX_BYTES * 4096;

    private void growBuffer()
    {
    	int curSize = this.byteBuffer.capacity();
    	int curPos = this.byteBuffer.position();
    	ByteBuffer bytebuffer = GLAllocation.createDirectByteBuffer(curSize + ALLOC_SIZE);
    	this.byteBuffer.position(0);
    	bytebuffer.put(this.byteBuffer);
    	bytebuffer.rewind();
    	this.byteBuffer = bytebuffer;
    	this.byteBuffer.position(curPos);
    }

	@Override
	public DisplayObjectVBO startBuilding()
	{
		byteBuffer = GLAllocation.createDirectByteBuffer(ALLOC_SIZE);
		return this;
	}

	@Override
	public DisplayObjectVBO begin(int mode)
	{
		this.mode = mode;
		this.buffer = OpenGlHelper.glGenBuffers();
		return this;
	}

	@Override
	public DisplayObjectVBO position(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	@Override
	public DisplayObjectVBO texture(float u, float v)
	{
		this.u = u;
		this.v = v;
		return this;
	}

	@Override
	public DisplayObjectVBO normal(float nx, float ny, float nz)
	{
		this.nx = nx;
		this.ny = ny;
		this.nz = nz;
		return this;
	}

	@Override
	public DisplayObjectVBO color(float r, float g, float b, float a)
	{
		this.r = r < 0 ? 0 : r > 1 ? 1 : r;
		this.g = g < 0 ? 0 : g > 1 ? 1 : g;
		this.b = b < 0 ? 0 : b > 1 ? 1 : b;
		this.a = a < 0 ? 0 : a > 1 ? 1 : a;
		return this;
	}

	@Override
	public DisplayObjectVBO endVertex()
	{
		if (byteBuffer.capacity() < (byteBuffer.position() + VERTEX_BYTES)) growBuffer();
		byteBuffer.putFloat(x);
		byteBuffer.putFloat(y);
		byteBuffer.putFloat(z);
		byteBuffer.putFloat(u);
		byteBuffer.putFloat(v);
		byteBuffer.putFloat(nx);
		byteBuffer.putFloat(ny);
		byteBuffer.putFloat(nz);
		byteBuffer.put((byte) (r * 255));
		byteBuffer.put((byte) (g * 255));
		byteBuffer.put((byte) (b * 255));
		byteBuffer.put((byte) (a * 255));
		numVertices++;
		return this;
	}

	@Override
	public DisplayObjectVBO end()
	{
        this.byteBuffer.limit(byteBuffer.position());
        this.byteBuffer.rewind();
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, buffer);
        OpenGlHelper.glBufferData(OpenGlHelper.GL_ARRAY_BUFFER, byteBuffer, 35044);
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);
        this.byteBuffer.limit(byteBuffer.capacity());
        this.byteBuffer.rewind();
		buffers.add(new int[] {buffer, mode, numVertices});
		numVertices = 0;
		buffer = -1;
		mode = -1;
		return this;
	}

	@Override
	public DisplayObjectVBO finishBuilding()
	{
		byteBuffer = null;
		return this;
	}

	@Override
	public void draw()
	{
        GlStateManager.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GlStateManager.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        GlStateManager.glEnableClientState(GL11.GL_NORMAL_ARRAY);
        GlStateManager.glEnableClientState(GL11.GL_COLOR_ARRAY);
		for (int[] buffer : buffers) {
			OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, buffer[0]);
	        GlStateManager.glVertexPointer(3, GL11.GL_FLOAT, VERTEX_BYTES, 0);
	        GlStateManager.glTexCoordPointer(2, GL11.GL_FLOAT, VERTEX_BYTES, 12);
	        GL11.glNormalPointer(GL11.GL_FLOAT, VERTEX_BYTES, 20);
	        GlStateManager.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, VERTEX_BYTES, 32);
	        GlStateManager.glDrawArrays(buffer[1], 0, buffer[2]);
		}
        GlStateManager.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        GlStateManager.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        GlStateManager.glDisableClientState(GL11.GL_NORMAL_ARRAY);
        GlStateManager.glDisableClientState(GL11.GL_COLOR_ARRAY);
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);
	}

	@Override
	public void delete()
	{
		buffers.forEach(buffer -> EventListener.INSTANCE.enqueueAction(() -> OpenGlHelper.glDeleteBuffers(buffer[0])));
		buffers.clear();
	}
}