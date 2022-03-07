package firemerald.mechanimation.api.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import firemerald.mechanimation.api.properties.BlockProperties;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author mccas
 *
 */
public class APIUtils
{
	public static final float BEAM_LENGTH = 1.5f;

	public static Stream<String> getDomains()
	{
		return FMLCommonHandler.instance().getSide().isClient() ? Minecraft.getMinecraft().getResourceManager().getResourceDomains().stream() : Loader.instance().getActiveModList().stream().map(ModContainer::getModId);
	}

	//from https://stackoverflow.com/questions/40050601/how-do-i-convert-a-camelcase-string-to-underscore-in-java-keeping-some-upper-cas
	public static String camelCaseToSnakeCase(String camel)
	{
		Matcher m = Pattern.compile("(?<=[a-z])[A-Z]").matcher(camel);
		StringBuffer sb = new StringBuffer();
		while (m.find()) m.appendReplacement(sb, "_" + m.group().toLowerCase());
		m.appendTail(sb);
		return sb.toString().toLowerCase(Locale.ENGLISH);
	}

	@FunctionalInterface
	public static interface BlockOperation
	{
		public boolean apply(World world, BlockPos blockPos, IBlockState blockState, TileEntity tile);
	}

	public static class BlockOperationTemp implements APIUtils.BlockOperation
	{
		public final AxisAlignedBB box;
		public final float minRad, maxRad;
		public final float ambientTemp;
		private float deltaTemp = 0;
		private float hits = 0;

		public BlockOperationTemp(AxisAlignedBB box, float minRad, float maxRad, float ambientTemp)
		{
			this.box = box;
			this.minRad = minRad;
			this.maxRad = maxRad;
			this.ambientTemp = ambientTemp;
		}

		public AxisAlignedBB getEffectiveBox()
		{
			return box.grow(maxRad);
		}

		@Override
		public boolean apply(World world, BlockPos blockPos, IBlockState blockState, TileEntity tile)
		{
			float dis = MathHelper.sqrt(APIUtils.getMinDisSqr(box, blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX() + 1, blockPos.getY() + 1, blockPos.getZ() + 1));
			float weight;
			if (dis >= maxRad) return false;
			else weight = (maxRad - dis) / (maxRad - minRad);
			hits += weight;
			if (blockState.getBlock() != Blocks.AIR)
			{
				if (dis < maxRad)
				{
					Float t = BlockProperties.getThermalTemp(world, blockPos, blockState.getActualState(world, blockPos), tile);
					if (t != null) this.deltaTemp += (t - ambientTemp) * weight;
				}
			}
			return false;
		}

		public float getTemp()
		{
			return hits > 0 ? ambientTemp + (deltaTemp / hits) : ambientTemp;
		}
	}

	/** optimized block parsing method, iterating through chunks and the blockStorageArrays so to avoid many, many potentially costly calls.
	 * optionally also acquires the tile entities in a more efficient manner than net.minecraft.world.World does.
	 * skips any block positions not in the world. **/
	public static boolean forEachBlock(World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, boolean doTileEntities, BlockOperation action)
	{
		BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
		int chunkMinX = minX >> 4;
		int chunkMinY = minY >> 4;
		int chunkMinZ = minZ >> 4;
		int chunkMaxX = (maxX + 15) >> 4;
		int chunkMaxY = (maxY + 15) >> 4;
		int chunkMaxZ = (maxZ + 15) >> 4;
		for (int chunkX = chunkMinX; chunkX <= chunkMaxX; chunkX++)
		{
			int startX = chunkX << 4;
			int endX = startX + 15;
			if (startX < minX) startX = minX;
			if (endX > maxX) endX = maxX;
			for (int chunkZ = chunkMinZ; chunkZ <= chunkMaxZ; chunkZ++)
			{
				int startZ = chunkZ << 4;
				int endZ = startZ + 15;
				if (startZ < minZ) startZ = minZ;
				if (endZ > maxZ) endZ = maxZ;
				Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
				if (chunk == null) continue;
				ExtendedBlockStorage[] blockStorageArray = chunk.getBlockStorageArray();
				for (int chunkY = chunkMinY; chunkY <= chunkMaxY; chunkY++)
				{
					if (chunkY < 0) continue;
					if (chunkY >= blockStorageArray.length) break;
					ExtendedBlockStorage blockStorage = blockStorageArray[chunkY];
					if (blockStorage == Chunk.NULL_BLOCK_STORAGE) continue;
					int startY = chunkY << 4;
					int endY = startY + 15;
					if (startY < minY) startY = minY;
					if (endY > maxY) endY = maxY;
					for (int posY = startY; posY <= endY; posY++) for (int posX = startX; posX <= endX; posX++) for (int posZ = startZ; posZ <= endZ; posZ++)
					{
						blockPos.setPos(posX, posY, posZ);
						IBlockState blockState = blockStorage.get(posX & 15, posY & 15, posZ & 15);
						TileEntity tile;
						if (doTileEntities)
						{
							//optimized from net.minecraft.world.World, because mojang are idiots.
				            if (world.processingLoadedTiles)
				            {
				            	tile = world.getPendingTileEntityAt(blockPos);
					            if (tile == null) tile = chunk.getTileEntity(blockPos, Chunk.EnumCreateEntityType.CHECK);
				            }
				            else
				            {
				            	tile = chunk.getTileEntity(blockPos, Chunk.EnumCreateEntityType.CHECK);
					            if (tile == null) tile = world.getPendingTileEntityAt(blockPos);
				            }
						}
						else tile = null;
						if (action.apply(world, blockPos, blockState, tile)) return true;
					}
				}
			}
		}
		return false;
	}

	/** optimized block parsing method, iterating through chunks and the blockStorageArrays so to avoid many, many potentially costly calls.
	 * optionally also acquires the tile entities in a more efficient manner than net.minecraft.world.World does.
	 * skips any block positions not in the world. **/
	public static boolean forEachBlock(World world, AxisAlignedBB aabb, boolean doTileEntities, BlockOperation action)
	{
		return forEachBlock(world, MathHelper.floor(aabb.minX), MathHelper.floor(aabb.minY), MathHelper.floor(aabb.minZ), MathHelper.floor(aabb.maxX), MathHelper.floor(aabb.maxY), MathHelper.floor(aabb.maxZ), doTileEntities, action);
	}

	/** optimized block parsing method, iterating through chunks and the blockStorageArrays so to avoid many, many potentially costly calls.
	 * optionally also acquires the tile entities in a more efficient manner than net.minecraft.world.World does.
	 * skips any block positions not in the world.
	 * ignores non-colliding blocks. **/
	public static boolean forEachBlockCollision(World world, Entity entity, AxisAlignedBB box, boolean doTileEntities, BlockOperation action)
	{
		int minX = MathHelper.floor(box.minX) - 1;
		int maxX = MathHelper.ceil(box.maxX) + 1;
		int minY = MathHelper.floor(box.minY) - 1;
		int maxY = MathHelper.ceil(box.maxY) + 1;
		int minZ = MathHelper.floor(box.minZ) - 1;
		int maxZ = MathHelper.ceil(box.maxZ) + 1;
		BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
		int chunkMinX = minX >> 4;
		int chunkMinY = minY >> 4;
		int chunkMinZ = minZ >> 4;
		int chunkMaxX = (maxX + 15) >> 4;
		int chunkMaxY = (maxY + 15) >> 4;
		int chunkMaxZ = (maxZ + 15) >> 4;
		List<AxisAlignedBB> list = new ArrayList<>();
		for (int chunkX = chunkMinX; chunkX <= chunkMaxX; chunkX++)
		{
			int startX = chunkX << 4;
			int endX = startX + 15;
			if (startX < minX) startX = minX;
			if (endX > maxX) endX = maxX;
			for (int chunkZ = chunkMinZ; chunkZ <= chunkMaxZ; chunkZ++)
			{
				int startZ = chunkZ << 4;
				int endZ = startZ + 15;
				if (startZ < minZ) startZ = minZ;
				if (endZ > maxZ) endZ = maxZ;
				Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
				if (chunk == null) continue;
				ExtendedBlockStorage[] blockStorageArray = chunk.getBlockStorageArray();
				for (int chunkY = chunkMinY; chunkY <= chunkMaxY; chunkY++)
				{
					if (chunkY < 0) continue;
					if (chunkY >= blockStorageArray.length) break;
					ExtendedBlockStorage blockStorage = blockStorageArray[chunkY];
					if (blockStorage == Chunk.NULL_BLOCK_STORAGE) continue;
					int startY = chunkY << 4;
					int endY = startY + 15;
					if (startY < minY) startY = minY;
					if (endY > maxY) endY = maxY;
					for (int posY = startY; posY <= endY; posY++) for (int posX = startX; posX <= endX; posX++) for (int posZ = startZ; posZ <= endZ; posZ++)
					{
						blockPos.setPos(posX, posY, posZ);
						IBlockState blockState = blockStorage.get(posX & 15, posY & 15, posZ & 15);
						blockState.addCollisionBoxToList(world, blockPos, box, list, entity, false);
						if (!list.isEmpty())
						{
							TileEntity tile;
							if (doTileEntities)
							{
								//optimized from net.minecraft.world.World, because mojang are idiots.
					            if (world.processingLoadedTiles)
					            {
					            	tile = world.getPendingTileEntityAt(blockPos);
						            if (tile == null) tile = chunk.getTileEntity(blockPos, Chunk.EnumCreateEntityType.CHECK);
					            }
					            else
					            {
					            	tile = chunk.getTileEntity(blockPos, Chunk.EnumCreateEntityType.CHECK);
						            if (tile == null) tile = world.getPendingTileEntityAt(blockPos);
					            }
							}
							else tile = null;
							if (action.apply(world, blockPos, blockState, tile)) return true;
						}
					}
				}
			}
		}
		return false;
	}

	public static Vec3d getNearestPoint(double x, double y, double z, AxisAlignedBB box)
	{
		double px, py, pz;
		if (x <= box.minX) px = box.minX;
		else if (x >= box.maxX) px = box.maxX;
		else px = x;
		if (y <= box.minY) py = box.minY;
		else if (y >= box.maxY) py = box.maxY;
		else py = y;
		if (z <= box.minZ) pz = box.minZ;
		else if (z >= box.maxZ) pz = box.maxZ;
		else pz = z;
		return new Vec3d(px, py, pz);
	}

	public static boolean intersects(double x, double y, double z, double r, AxisAlignedBB box)
	{
		double dx, dy, dz;
		if (x < box.minX) dx = box.minX - x;
		else if (x > box.maxX) dx = x - box.maxX;
		else dx = 0;
		if (y < box.minY) dy = box.minY - y;
		else if (y > box.maxY) dy = y - box.maxY;
		else dy = 0;
		if (z < box.minZ) dz = box.minZ - z;
		else if (z > box.maxZ) dz = z - box.maxZ;
		else dz = 0;
		return (dx * dx + dy * dy + dz * dz) <= r * r;
	}

	public static boolean intersects(double x, double y, double z, double rh, double rv, AxisAlignedBB box)
	{
		if (rh == 0)
		{
			if (rv == 0)
			{
				return box.minX == x && box.maxX == x && box.minY == y && box.maxY == y && box.minZ == z && box.maxZ == z;
			}
			else
			{
				double dz;
				if (x < box.minX || x > box.maxX || z < box.minZ || z > box.maxZ) return false;
				if (z < box.minZ) dz = (box.minZ - z) / rh;
				else if (z > box.maxZ) dz = (z - box.maxZ) / rh;
				else return true;
				return Math.abs(dz) <= Math.abs(rv);
			}
		}
		else
		{
			if (rv == 0)
			{
				if (y < box.minY || y > box.maxY) return false;
				double dx, dz;
				if (x < box.minX) dx = (box.minX - x) / rh;
				else if (x > box.maxX) dx = (x - box.maxX) / rh;
				else dx = 0;
				if (z < box.minZ) dz = (box.minZ - z) / rh;
				else if (z > box.maxZ) dz = (z - box.maxZ) / rh;
				else dz = 0;
				return (dx * dx + dz * dz) <= rh * rh;
			}
			else
			{
				double dx, dy, dz;
				if (x < box.minX) dx = (box.minX - x) / rh;
				else if (x > box.maxX) dx = (x - box.maxX) / rh;
				else dx = 0;
				if (y < box.minY) dy = (box.minY - y) / rv;
				else if (y > box.maxY) dy = (y - box.maxY) / rv;
				else dy = 0;
				if (z < box.minZ) dz = (box.minZ - z) / rh;
				else if (z > box.maxZ) dz = (z - box.maxZ) / rh;
				else dz = 0;
				return (dx * dx + dz * dz + dy * dy) <= 1;
			}
		}
	}

	public static boolean isFullyContained(double x, double y, double z, double r, AxisAlignedBB box)
	{
		if (r == 0) return box.minX == x && box.maxX == x && box.minY == y && box.maxY == y && box.minZ == z && box.maxZ == z;
		double dx = Math.max(x - box.minX, box.maxX - x); //largest X distance
		double dy = Math.max(y - box.minY, box.maxY - y); //largest Y distance
		double dz = Math.max(z - box.minZ, box.maxZ - z); //largest Z distance
		return (dx * dx + dy * dy + dz * dz) <= r * r;
	}

	public static boolean isFullyContained(double x, double y, double z, double rh, double rv, AxisAlignedBB box)
	{
		if (rh == 0)
		{
			if (rv == 0)
			{
				return box.minX == x && box.maxX == x && box.minY == y && box.maxY == y && box.minZ == z && box.maxZ == z;
			}
			else
			{
				return box.minX == x && box.maxX == x && box.minY >= (y - rv) && box.maxY <= (y + rv) && box.minZ == z && box.maxZ == z;
			}
		}
		else
		{
			if (rv == 0)
			{
				if (box.minY != y || box.maxY != y) return false;
				double dx = Math.max(x - box.minX, box.maxX - x); //largest X distance
				double dz = Math.max(z - box.minZ, box.maxZ - z); //largest Z distance
				return (dx * dx + dz * dz) <= (rh * rh);
			}
			else
			{
				double dx = Math.max(x - box.minX, box.maxX - x) / rh; //largest X distance
				double dy = Math.max(y - box.minY, box.maxY - y) / rv; //largest Y distance
				double dz = Math.max(z - box.minZ, box.maxZ - z) / rh; //largest Z distance
				return (dx * dx + dy * dy + dz * dz) <= 1;
			}
		}
	}

	public static float getMinDisSqr(AxisAlignedBB a, double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
	{
		return APIUtils.getMinDisSqr(a.minX, a.minY, a.minZ, a.maxX, a.maxY, a.maxZ, minX, minY, minZ, maxX, maxY, maxZ);
	}

	public static float getMinDisSqr(double x, double y, double z, AxisAlignedBB a)
	{
		return APIUtils.getMinDisSqr(x, y, z, x, y, z, a.minX, a.minY, a.minZ, a.maxX, a.maxY, a.maxZ);
	}

	public static float getMinDisSqr(double x, double y, double z, double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
	{
		return APIUtils.getMinDisSqr(x, y, z, x, y, z, minX, minY, minZ, maxX, maxY, maxZ);
	}

	public static double getMinDisSqr(AxisAlignedBB aabb1, AxisAlignedBB aabb2)
	{
		return APIUtils.getMinDisSqr(aabb1.minX, aabb1.minY, aabb1.minZ, aabb1.maxX, aabb1.maxY, aabb1.maxZ, aabb2.minX, aabb2.minY, aabb2.minZ, aabb2.maxX, aabb2.maxY, aabb2.maxZ);
	}

	public static float getMinDisSqr(double minX1, double minY1, double minZ1, double maxX1, double maxY1, double maxZ1, double minX2, double minY2, double minZ2, double maxX2, double maxY2, double maxZ2)
	{
		double o1, o2;
		o1 = minX1 - maxX2;
		o2 = minX2 - maxX1;
		float ox = (o1 <= 0 && o2 <= 0) ? 0 : (float) Math.max(o1, o2);
		o1 = minY1 - maxY2;
		o2 = minY2 - maxY1;
		float oy = (o1 <= 0 && o2 <= 0) ? 0 : (float) Math.max(o1, o2);
		o1 = minZ1 - maxZ2;
		o2 = minZ2 - maxZ1;
		float oz = (o1 <= 0 && o2 <= 0) ? 0 : (float) Math.max(o1, o2);
		return ox * ox + oy * oy + oz * oz;
	}

	public static float getMinDisSqr(double minX1, double minZ1, double maxX1, double maxZ1, double minX2, double minZ2, double maxX2, double maxZ2)
	{
		double o1, o2;
		o1 = minX1 - maxX2;
		o2 = minX2 - maxX1;
		float ox = (o1 <= 0 && o2 <= 0) ? 0 : (float) Math.max(o1, o2);
		o1 = minZ1 - maxZ2;
		o2 = minZ2 - maxZ1;
		float oz = (o1 <= 0 && o2 <= 0) ? 0 : (float) Math.max(o1, o2);
		return ox * ox + oz * oz;
	}

	public static float getMinDisSqr(double x, double z, double minX, double minZ, double maxX, double maxZ)
	{
		return getMinDisSqr(x, z, x, z, minX, minZ, maxX, maxZ);
	}

	public static float getMinDis(double v, double min, double max)
	{
		return getMinDis(v, v, min, max);
	}

	public static float getMinDis(double min1, double max1, double min2, double max2)
	{
		double o1, o2;
		o1 = min1 - max2;
		o2 = min2 - max1;
		return (o1 <= 0 && o2 <= 0) ? 0 : (float) Math.max(o1, o2);
	}

	@SideOnly(Side.CLIENT)
    public static URL getURLForResource(ResourceLocation resource)
    {
        String s = String.format("%s:%s:%s", "resource", resource.getResourceDomain(), resource.getResourcePath());
        URLStreamHandler urlstreamhandler = new URLStreamHandler()
        {
            @Override
			protected URLConnection openConnection(URL p_openConnection_1_)
            {
                return new URLConnection(p_openConnection_1_)
                {
                    @Override
					public void connect() throws IOException
                    {
                    }
                    @Override
					public InputStream getInputStream() throws IOException
                    {
                        return Minecraft.getMinecraft().getResourceManager().getResource(resource).getInputStream();
                    }
                };
            }
        };

        try
        {
            return new URL((URL)null, s, urlstreamhandler);
        }
        catch (MalformedURLException var4)
        {
            throw new Error("TODO: Sanely handle url exception! :D");
        }
    }

    public static EnumFacing approximateFace(double outX, double outY, double outZ)
    {
		if (Math.abs(outY) >= Math.abs(outX)) //y >= x
		{
			if (Math.abs(outY) >= Math.abs(outZ)) //y >= x && y >= z
			{
				return outY > 0 ? EnumFacing.UP : EnumFacing.DOWN;
			}
			else //z > y >= x
			{
				return outZ > 0 ? EnumFacing.SOUTH : EnumFacing.NORTH;
			}
		}
		else //x > y
		{
			if (Math.abs(outX) >= Math.abs(outZ)) //x >= z && x > y
			{
				return outX > 0 ? EnumFacing.EAST : EnumFacing.WEST;
			}
			else //z > x > y
			{
				return outZ > 0 ? EnumFacing.SOUTH : EnumFacing.NORTH;
			}
		}
    }

	public static float celsiusFromBiomeTemp(float temp)
	{
		return temp * 17.5f + 7;
	}

	public static float getBiomeTempCelsius(World world, BlockPos pos)
	{
		if (world.provider.getDimension() == -1) return 217;
		return celsiusFromBiomeTemp(world.getBiome(pos).getTemperature(pos));
	}
}