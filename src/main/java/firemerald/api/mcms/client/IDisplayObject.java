package firemerald.api.mcms.client;

import java.util.List;

import org.joml.Matrix3d;
import org.joml.Matrix4d;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import firemerald.api.mcms.math.MathUtils;
import firemerald.api.mcms.model.ObjData;
import net.minecraft.client.renderer.OpenGlHelper;

public interface IDisplayObject
{
	public static IDisplayObject makeNewObject(String name, List<int[][]> mesh, ObjData obj, Matrix4d m)
	{
		IDisplayObject object = makeNewObject(obj.hasColorData());
		Matrix3d n = null;
		if (m != null) n = m.transpose3x3(new Matrix3d()).invert();
		int drawMode = -1;
		object.startBuilding();
		for (int[][] face : mesh)
		{
			int mode;
			switch (face.length)
			{
			case 1: mode = GL11.GL_POINTS; break;
			case 2: mode = GL11.GL_LINES; break;
			case 3: mode = GL11.GL_TRIANGLES; break;
			case 4: mode = GL11.GL_QUADS; break;
			default: mode = -1; break;
			}
			if (mode != drawMode)
			{
				if (drawMode != -1) object.end();
				if ((drawMode = mode) != -1) object.begin(drawMode);
			}
			if (drawMode != -1)
			{
				for (int[] data : face) {
					int vert = data[0];
					int tex = data[1];
					int norm = data[2];
					if (norm >= 0)
					{
						Vector3f normal = obj.vertexNormals.get(norm);
						if (n != null) normal = n.transform(normal, new Vector3f());
						object.normal(normal.x(), normal.y(), normal.z());
					}
					else object.normal(0, 0, 0);
					if (tex >= 0)
					{
						Vector2f texture = obj.textureCoordinates.get(tex);
						object.texture(texture.x(), 1 - texture.y());
					}
					else object.texture(0, 0);
					Vector4f vertexCol = obj.vertices.get(vert).getRight();
					if (vertexCol != null) object.color(vertexCol.x, vertexCol.y, vertexCol.z, vertexCol.w);
					Vector4f vertexVec = new Vector4f(obj.vertices.get(vert).getLeft(), 1);
					if (m != null) vertexVec = MathUtils.toVector4f(m.transform(MathUtils.toVector4d(vertexVec)));
					object.position(vertexVec.x(), vertexVec.y(), vertexVec.z()).endVertex();
				}
			}
		}
		if (drawMode != -1) object.end();
		object.finishBuilding();
		return object;
	}

	public static IDisplayObject makeNewObject(boolean needsColor)
	{
		return OpenGlHelper.useVbo() ? needsColor ? new DisplayObjectVBO() : new DisplayObjectVBOColorless() : new DisplayObjectList();
	}

	public IDisplayObject startBuilding();

	public IDisplayObject begin(int mode);

	public IDisplayObject position(float x, float y, float z);

	public IDisplayObject texture(float u, float v);

	public IDisplayObject normal(float nx, float ny, float nz);

	public IDisplayObject color(float r, float g, float b, float a);

	public IDisplayObject endVertex();

	public IDisplayObject end();

	public IDisplayObject finishBuilding();

	public void draw();

	public void delete();
}