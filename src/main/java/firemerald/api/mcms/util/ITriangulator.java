package firemerald.api.mcms.util;

@FunctionalInterface
public interface ITriangulator
{
	public static final ITriangulator FAN = face -> {
		if (face.length > 2)
		{
			if (face.length == 3) return new int[][][] {face}; //don't need to triangulate triangles!
			int[][][] triangles = new int[face.length - 2][3][];
			for (int i = 0; i < triangles.length; i++)
			{
				int[][] destination = triangles[i];
				destination[0] = face[0];
				destination[1] = face[i + 1];
				destination[2] = face[i + 2];
			}
			return triangles;
		}
		else return new int[0][3][];
	};

	public static final ITriangulator STRIP = face -> {
		if (face.length > 2)
		{
			if (face.length == 3) return new int[][][] {face}; //don't need to triangulate triangles!
			int[][][] triangles = new int[face.length - 2][3][];
			for (int i = 0; i < triangles.length; i++)
			{
				int[][] destination = triangles[i];
				destination[0] = face[(i + 1) & ~1];
				destination[1] = face[(i & ~1) + 1];
				destination[2] = face[i + 2];
			}
			return triangles;
		}
		else return new int[0][3][];
	};

	public int[][][] triangulate(int[][] face);
}