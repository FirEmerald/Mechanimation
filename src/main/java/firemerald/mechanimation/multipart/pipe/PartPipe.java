package firemerald.mechanimation.multipart.pipe;

import java.util.ArrayList;
import java.util.List;

import buildcraft.api.transport.pipe.IPipe;
import buildcraft.api.transport.pipe.IPipeHolder;
import buildcraft.api.transport.pipe.PipeApi;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.raytracer.CuboidRayTraceResult;
import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import codechicken.microblock.HollowMicroblock;
import codechicken.microblock.ISidedHollowConnect;
import codechicken.multipart.IconHitEffects;
import codechicken.multipart.PartMap;
import codechicken.multipart.TIconHitEffectsPart;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TSlottedPart;
import codechicken.multipart.TileMultipart;
import firemerald.mechanimation.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class PartPipe extends TMultiPart implements ISidedHollowConnect, TSlottedPart, TIconHitEffectsPart, ITickable
{
	public boolean[] connections = new boolean[6];
	public byte connectionsMask;
	public EnumDyeColor color;
	public EnumPipeTier tier = EnumPipeTier.STONE;

	public static final double MIN = 5.0 / 16.0;
	public static final double MAX = 11.0 / 16.0;

	@Override
	public void update()
	{
		byte prevCon = connectionsMask;
		for (int i = 0; i < 6; i++) connections[i] = checkConnect(EnumFacing.VALUES[i]);
		if (prevCon != connectionsMask)
		{
			tile().resetCapCache();
			if (world().isRemote) world().markBlockRangeForRenderUpdate(this.pos(), this.pos());
			else world().notifyNeighborsOfStateChange(pos(), tile().blockType, true);
		}
	}

	public void setTierNoWorldUpdate(EnumPipeTier tier)
	{
		this.tier = tier;
	}

	public void markPipeForUpdate(EnumFacing side)
	{
		TileEntity tile = world().getTileEntity(pos().offset(side));
		if (tile != null)
		{
			IPipe pipe = tile.getCapability(PipeApi.CAP_PIPE, null);
			if (pipe == null)
			{
				IPipeHolder holder = tile.getCapability(PipeApi.CAP_PIPE_HOLDER, null);
				if (holder != null) pipe = holder.getPipe();
			}
			if (pipe != null) pipe.markForUpdate();
		}
	}

	@Override
	public void onWorldJoin()
	{
		for (int i = 0; i < 6; i++) connections[i] = checkConnect(EnumFacing.VALUES[i]);
	}

	@Override
	public int getHollowSize(int side)
	{
		return 6;
	}

	@Override
	public List<Cuboid6> getCollisionBoxes()
	{
		List<Cuboid6> list = new ArrayList<>();
		double minX = MIN, maxX = MAX, minY = MIN, maxY = MAX, minZ = MIN, maxZ = MAX;
		if (connections[EnumFacing.WEST.getIndex()]) minX = 0;
		if (connections[EnumFacing.EAST.getIndex()]) maxX = 1;
		if (connections[EnumFacing.DOWN.getIndex()]) minY = 0;
		if (connections[EnumFacing.UP.getIndex()]) maxY = 1;
		if (connections[EnumFacing.NORTH.getIndex()]) minZ = 0;
		if (connections[EnumFacing.SOUTH.getIndex()]) maxZ = 1;
		list.add(new Cuboid6(minX, MIN , MIN , maxX, MAX , MAX ));
		list.add(new Cuboid6(MIN , minY, MIN , MAX , maxY, MAX ));
		list.add(new Cuboid6(MIN , MIN , minZ, MAX , MAX , maxZ));
		return list;
	}

	@Override
	public Cuboid6 getBounds()
	{
		double minX = MIN, maxX = MAX, minY = MIN, maxY = MAX, minZ = MIN, maxZ = MAX;
		if (connections[EnumFacing.WEST.getIndex()]) minX = 0;
		if (connections[EnumFacing.EAST.getIndex()]) maxX = 1;
		if (connections[EnumFacing.DOWN.getIndex()]) minY = 0;
		if (connections[EnumFacing.UP.getIndex()]) maxY = 1;
		if (connections[EnumFacing.NORTH.getIndex()]) minZ = 0;
		if (connections[EnumFacing.SOUTH.getIndex()]) maxZ = 1;
		return new Cuboid6(minX, minY, minZ, maxX, maxY, maxZ);
	}

	public static class PipeSide
	{
		public final PartPipe pipe;
		public final EnumFacing side;

		public PipeSide(PartPipe pipe, EnumFacing side)
		{
			this.pipe = pipe;
			this.side = side;
		}
	}

	@Override
	public List<IndexedCuboid6> getSubParts()
	{
		List<IndexedCuboid6> list = new ArrayList<>();
		list.add(new IndexedCuboid6(this, new Cuboid6(MIN, MIN, MIN, MAX, MAX, MAX)));
		if (connections[EnumFacing.WEST .getIndex()]) list.add(new IndexedCuboid6(new PipeSide(this, EnumFacing.WEST ), new Cuboid6(0  , MIN, MIN, MIN, MAX, MAX)));
		if (connections[EnumFacing.EAST .getIndex()]) list.add(new IndexedCuboid6(new PipeSide(this, EnumFacing.EAST ), new Cuboid6(MAX, MIN, MIN, 1  , MAX, MAX)));
		if (connections[EnumFacing.DOWN .getIndex()]) list.add(new IndexedCuboid6(new PipeSide(this, EnumFacing.DOWN ), new Cuboid6(MIN, 0  , MIN, MAX, MIN, MAX)));
		if (connections[EnumFacing.UP   .getIndex()]) list.add(new IndexedCuboid6(new PipeSide(this, EnumFacing.UP   ), new Cuboid6(MIN, MAX, MIN, MAX, 1  , MAX)));
		if (connections[EnumFacing.NORTH.getIndex()]) list.add(new IndexedCuboid6(new PipeSide(this, EnumFacing.NORTH), new Cuboid6(MIN, MIN, 0  , MAX, MAX, MIN)));
		if (connections[EnumFacing.SOUTH.getIndex()]) list.add(new IndexedCuboid6(new PipeSide(this, EnumFacing.SOUTH), new Cuboid6(MIN, MIN, MAX, MAX, MAX, 1  )));
		return list;
	}

	public boolean checkConnect(EnumFacing side)
	{
		int mask = 1 << side.getIndex();
		if (canConnect(side))
		{
			if ((connectionsMask & mask) == 0)
			{
				markPipeForUpdate(side);
				connectionsMask |= mask;
			}
			return true;
		}
		else
		{
			if ((connectionsMask & mask) == mask)
			{
				markPipeForUpdate(side);
				connectionsMask &= ~mask;
			}
			return false;
		}
	}

	private boolean canConnect(EnumFacing side)
	{
		if (side == null) return false;
		else if (!this.isSideValid(side)) return false;
		else
		{
			TileEntity tile = world().getTileEntity(pos().offset(side));
			if (tile instanceof TileMultipart)
			{
				TMultiPart part = ((TileMultipart) tile).partMap(PartMap.CENTER.i);
				if (part instanceof PartPipe) return canConnectToPipe(side, (PartPipe) part);
			}
			return canPipeConnect(side, tile);
		}
	}

	public boolean canConnectToPipe(EnumFacing side, PartPipe pipe)
	{
		return (color == null || pipe.color == null || color == pipe.color) && pipe.isSideValid(side.getOpposite());
	}

	public abstract boolean canPipeConnect(EnumFacing side, TileEntity tile);

	public boolean isSideValid(EnumFacing side)
	{
		if (side == null) return false;
		else
		{
			TileMultipart tile = this.tile();
			if (tile == null) return false;
			else
			{
				TMultiPart part = tile.partMap(side.getIndex());
				return (part == null || part instanceof HollowMicroblock);
			}
		}
	}

	@Override
	public int getSlotMask()
	{
		return PartMap.CENTER.mask; //center
	}

	public boolean isConnected(EnumFacing side)
	{
		return connections[side.getIndex()];
	}

	@Override
	public ItemStack pickItem(CuboidRayTraceResult hit)
	{
		return getItem();
	}

	@Override
	public List<ItemStack> getDrops()
	{
		List<ItemStack> drops = new ArrayList<>();
		drops.add(getItem());
		return drops;
	}

	@Override
	public TextureAtlasSprite getBreakingIcon(CuboidRayTraceResult arg0)
	{
		return this.getFrameIcon();
	}

	@Override
	public TextureAtlasSprite getBrokenIcon(int arg0)
	{
		return this.getFrameIcon();
	}

	@Override
    @SideOnly(Side.CLIENT)
    public void addHitEffects(CuboidRayTraceResult hit, ParticleManager effectRenderer)
    {
        IconHitEffects.addHitEffects(this, hit, effectRenderer);
    }

	@Override
    @SideOnly(Side.CLIENT)
	public void addDestroyEffects(CuboidRayTraceResult hit, ParticleManager effectRenderer)
    {
        IconHitEffects.addDestroyEffects(this, effectRenderer);
    }

	@SideOnly(Side.CLIENT)
	public abstract BlockRenderLayer getRenderLayer();

    @Override
    public void writeDesc(MCDataOutput packet)
    {
    	packet.writeByte(color == null ? -1 : color.ordinal());
    	packet.writeString(tier.name());
    }

    @Override
    public void readDesc(MCDataInput packet)
    {
    	byte id = packet.readByte();
    	if (id < 0 || id >= EnumDyeColor.values().length) color = null;
    	else color = EnumDyeColor.values()[id];
    	setTierNoWorldUpdate(Utils.getEnum(packet.readString(), EnumPipeTier.values(), EnumPipeTier.STONE));
    }

	public ItemStack getItem()
	{
		ItemPartPipe item = this.getTheItem();
		return item.setTier(new ItemStack(item, 1, color == null ? 0 : (color.ordinal() + 1)), this.tier);
	}

	public abstract ItemPartPipe getTheItem();

	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite getFrameIcon()
	{
		return tier.icon == null ? Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite() : tier.icon;
	}

	@SideOnly(Side.CLIENT)
	public abstract TextureAtlasSprite getIcon();

	@SideOnly(Side.CLIENT)
	public float[] getDefaultColorValues()
	{
		return new float[] {.796f, .796f, .796f};
	}

	@Override
    @SideOnly(Side.CLIENT)
    public boolean renderStatic(Vector3 pos, BlockRenderLayer layer, CCRenderState ccrs)
    {
		int brightness = world().getCombinedLight(pos(), 0);
        int bU = brightness >>> 16 & 65535;
        int bV = brightness & 65535;
		if (layer == getRenderLayer())
		{
			float r, g, b;
			{
				float[] col = color == null ? this.getDefaultColorValues() : color.getColorComponentValues();
				r = col[0];
				g = col[1];
				b = col[2];
			}
			double minX = MIN, maxX = MAX, minY = MIN, maxY = MAX, minZ = MIN, maxZ = MAX;
			if (connections[EnumFacing.WEST.getIndex()]) minX = 0;
			if (connections[EnumFacing.EAST.getIndex()]) maxX = 1;
			if (connections[EnumFacing.DOWN.getIndex()]) minY = 0;
			if (connections[EnumFacing.UP.getIndex()]) maxY = 1;
			if (connections[EnumFacing.NORTH.getIndex()]) minZ = 0;
			if (connections[EnumFacing.SOUTH.getIndex()]) maxZ = 1;
			TextureAtlasSprite frame = getFrameIcon();
			TextureAtlasSprite icon = getIcon();
			{ //top
				float minU = frame.getInterpolatedU(minX * 16);
				float minV = frame.getInterpolatedV(minZ * 16);
				float maxU = frame.getInterpolatedU(maxX * 16);
				float maxV = frame.getInterpolatedV(maxZ * 16);
				ccrs.r.pos(pos.x + minX, pos.y + MAX, pos.z + minZ).color(1f, 1f, 1f, 1f).tex(minU, minV).lightmap(bU, bV).endVertex();
				ccrs.r.pos(pos.x + minX, pos.y + MAX, pos.z + maxZ).color(1f, 1f, 1f, 1f).tex(minU, maxV).lightmap(bU, bV).endVertex();
				ccrs.r.pos(pos.x + maxX, pos.y + MAX, pos.z + maxZ).color(1f, 1f, 1f, 1f).tex(maxU, maxV).lightmap(bU, bV).endVertex();
				ccrs.r.pos(pos.x + maxX, pos.y + MAX, pos.z + minZ).color(1f, 1f, 1f, 1f).tex(maxU, minV).lightmap(bU, bV).endVertex();
				minU = icon.getInterpolatedU(minX * 16);
				minV = icon.getInterpolatedV(minZ * 16);
				maxU = icon.getInterpolatedU(maxX * 16);
				maxV = icon.getInterpolatedV(maxZ * 16);
				ccrs.r.pos(pos.x + minX, pos.y + MAX, pos.z + minZ).color(r, g, b, 1).tex(minU, minV).lightmap(bU, bV).endVertex();
				ccrs.r.pos(pos.x + minX, pos.y + MAX, pos.z + maxZ).color(r, g, b, 1).tex(minU, maxV).lightmap(bU, bV).endVertex();
				ccrs.r.pos(pos.x + maxX, pos.y + MAX, pos.z + maxZ).color(r, g, b, 1).tex(maxU, maxV).lightmap(bU, bV).endVertex();
				ccrs.r.pos(pos.x + maxX, pos.y + MAX, pos.z + minZ).color(r, g, b, 1).tex(maxU, minV).lightmap(bU, bV).endVertex();
			}
			{ //bottom
				float minU = frame.getInterpolatedU(minX * 16);
				float minV = frame.getInterpolatedV((1 - minZ) * 16);
				float maxU = frame.getInterpolatedU(maxX * 16);
				float maxV = frame.getInterpolatedV((1 - maxZ) * 16);
				ccrs.r.pos(pos.x + minX, pos.y + MIN, pos.z + minZ).color(1f, 1f, 1f, 1f).tex(minU, minV).lightmap(bU, bV).endVertex();
				ccrs.r.pos(pos.x + maxX, pos.y + MIN, pos.z + minZ).color(1f, 1f, 1f, 1f).tex(maxU, minV).lightmap(bU, bV).endVertex();
				ccrs.r.pos(pos.x + maxX, pos.y + MIN, pos.z + maxZ).color(1f, 1f, 1f, 1f).tex(maxU, maxV).lightmap(bU, bV).endVertex();
				ccrs.r.pos(pos.x + minX, pos.y + MIN, pos.z + maxZ).color(1f, 1f, 1f, 1f).tex(minU, maxV).lightmap(bU, bV).endVertex();
				minU = icon.getInterpolatedU(minX * 16);
				minV = icon.getInterpolatedV((1 - minZ) * 16);
				maxU = icon.getInterpolatedU(maxX * 16);
				maxV = icon.getInterpolatedV((1 - maxZ) * 16);
				ccrs.r.pos(pos.x + minX, pos.y + MIN, pos.z + minZ).color(r, g, b, 1).tex(minU, minV).lightmap(bU, bV).endVertex();
				ccrs.r.pos(pos.x + maxX, pos.y + MIN, pos.z + minZ).color(r, g, b, 1).tex(maxU, minV).lightmap(bU, bV).endVertex();
				ccrs.r.pos(pos.x + maxX, pos.y + MIN, pos.z + maxZ).color(r, g, b, 1).tex(maxU, maxV).lightmap(bU, bV).endVertex();
				ccrs.r.pos(pos.x + minX, pos.y + MIN, pos.z + maxZ).color(r, g, b, 1).tex(minU, maxV).lightmap(bU, bV).endVertex();
			}
			{ //north
				float minU = frame.getInterpolatedU(minX * 16);
				float minV = frame.getInterpolatedV(minY * 16);
				float maxU = frame.getInterpolatedU(maxX * 16);
				float maxV = frame.getInterpolatedV(maxY * 16);
				ccrs.r.pos(pos.x + minX, pos.y + minY, pos.z + MIN).color(1f, 1f, 1f, 1f).tex(minU, minV).lightmap(bU, bV).endVertex();
				ccrs.r.pos(pos.x + minX, pos.y + maxY, pos.z + MIN).color(1f, 1f, 1f, 1f).tex(minU, maxV).lightmap(bU, bV).endVertex();
				ccrs.r.pos(pos.x + maxX, pos.y + maxY, pos.z + MIN).color(1f, 1f, 1f, 1f).tex(maxU, maxV).lightmap(bU, bV).endVertex();
				ccrs.r.pos(pos.x + maxX, pos.y + minY, pos.z + MIN).color(1f, 1f, 1f, 1f).tex(maxU, minV).lightmap(bU, bV).endVertex();
				minU = icon.getInterpolatedU(minX * 16);
				minV = icon.getInterpolatedV(minY * 16);
				maxU = icon.getInterpolatedU(maxX * 16);
				maxV = icon.getInterpolatedV(maxY * 16);
				ccrs.r.pos(pos.x + minX, pos.y + minY, pos.z + MIN).color(r, g, b, 1).tex(minU, minV).lightmap(bU, bV).endVertex();
				ccrs.r.pos(pos.x + minX, pos.y + maxY, pos.z + MIN).color(r, g, b, 1).tex(minU, maxV).lightmap(bU, bV).endVertex();
				ccrs.r.pos(pos.x + maxX, pos.y + maxY, pos.z + MIN).color(r, g, b, 1).tex(maxU, maxV).lightmap(bU, bV).endVertex();
				ccrs.r.pos(pos.x + maxX, pos.y + minY, pos.z + MIN).color(r, g, b, 1).tex(maxU, minV).lightmap(bU, bV).endVertex();
			}
			{ //south
				float minU = frame.getInterpolatedU((1 - minX) * 16);
				float minV = frame.getInterpolatedV(minY * 16);
				float maxU = frame.getInterpolatedU((1 - maxX) * 16);
				float maxV = frame.getInterpolatedV(maxY * 16);
				ccrs.r.pos(pos.x + minX, pos.y + minY, pos.z + MAX).color(1f, 1f, 1f, 1f).tex(minU, minV).lightmap(bU, bV).endVertex();
				ccrs.r.pos(pos.x + maxX, pos.y + minY, pos.z + MAX).color(1f, 1f, 1f, 1f).tex(maxU, minV).lightmap(bU, bV).endVertex();
				ccrs.r.pos(pos.x + maxX, pos.y + maxY, pos.z + MAX).color(1f, 1f, 1f, 1f).tex(maxU, maxV).lightmap(bU, bV).endVertex();
				ccrs.r.pos(pos.x + minX, pos.y + maxY, pos.z + MAX).color(1f, 1f, 1f, 1f).tex(minU, maxV).lightmap(bU, bV).endVertex();
				minU = icon.getInterpolatedU((1 - minX) * 16);
				minV = icon.getInterpolatedV(minY * 16);
				maxU = icon.getInterpolatedU((1 - maxX) * 16);
				maxV = icon.getInterpolatedV(maxY * 16);
				ccrs.r.pos(pos.x + minX, pos.y + minY, pos.z + MAX).color(r, g, b, 1).tex(minU, minV).lightmap(bU, bV).endVertex();
				ccrs.r.pos(pos.x + maxX, pos.y + minY, pos.z + MAX).color(r, g, b, 1).tex(maxU, minV).lightmap(bU, bV).endVertex();
				ccrs.r.pos(pos.x + maxX, pos.y + maxY, pos.z + MAX).color(r, g, b, 1).tex(maxU, maxV).lightmap(bU, bV).endVertex();
				ccrs.r.pos(pos.x + minX, pos.y + maxY, pos.z + MAX).color(r, g, b, 1).tex(minU, maxV).lightmap(bU, bV).endVertex();
			}
			{ //east
				float minU = frame.getInterpolatedU(minZ * 16);
				float minV = frame.getInterpolatedV(minY * 16);
				float maxU = frame.getInterpolatedU(maxZ * 16);
				float maxV = frame.getInterpolatedV(maxY * 16);
				ccrs.r.pos(pos.x + MAX, pos.y + minY, pos.z + minZ).color(1f, 1f, 1f, 1f).tex(minU, minV).lightmap(bU, bV).endVertex();
				ccrs.r.pos(pos.x + MAX, pos.y + maxY, pos.z + minZ).color(1f, 1f, 1f, 1f).tex(minU, maxV).lightmap(bU, bV).endVertex();
				ccrs.r.pos(pos.x + MAX, pos.y + maxY, pos.z + maxZ).color(1f, 1f, 1f, 1f).tex(maxU, maxV).lightmap(bU, bV).endVertex();
				ccrs.r.pos(pos.x + MAX, pos.y + minY, pos.z + maxZ).color(1f, 1f, 1f, 1f).tex(maxU, minV).lightmap(bU, bV).endVertex();
				minU = icon.getInterpolatedU(minZ * 16);
				minV = icon.getInterpolatedV(minY * 16);
				maxU = icon.getInterpolatedU(maxZ * 16);
				maxV = icon.getInterpolatedV(maxY * 16);
				ccrs.r.pos(pos.x + MAX, pos.y + minY, pos.z + minZ).color(r, g, b, 1).tex(minU, minV).lightmap(bU, bV).endVertex();
				ccrs.r.pos(pos.x + MAX, pos.y + maxY, pos.z + minZ).color(r, g, b, 1).tex(minU, maxV).lightmap(bU, bV).endVertex();
				ccrs.r.pos(pos.x + MAX, pos.y + maxY, pos.z + maxZ).color(r, g, b, 1).tex(maxU, maxV).lightmap(bU, bV).endVertex();
				ccrs.r.pos(pos.x + MAX, pos.y + minY, pos.z + maxZ).color(r, g, b, 1).tex(maxU, minV).lightmap(bU, bV).endVertex();
			}
			{ //west
				float minU = frame.getInterpolatedU((1 - minZ) * 16);
				float minV = frame.getInterpolatedV(minY * 16);
				float maxU = frame.getInterpolatedU((1 - maxZ) * 16);
				float maxV = frame.getInterpolatedV(maxY * 16);
				ccrs.r.pos(pos.x + MIN, pos.y + minY, pos.z + minZ).color(1f, 1f, 1f, 1f).tex(minU, minV).lightmap(bU, bV).endVertex();
				ccrs.r.pos(pos.x + MIN, pos.y + minY, pos.z + maxZ).color(1f, 1f, 1f, 1f).tex(maxU, minV).lightmap(bU, bV).endVertex();
				ccrs.r.pos(pos.x + MIN, pos.y + maxY, pos.z + maxZ).color(1f, 1f, 1f, 1f).tex(maxU, maxV).lightmap(bU, bV).endVertex();
				ccrs.r.pos(pos.x + MIN, pos.y + maxY, pos.z + minZ).color(1f, 1f, 1f, 1f).tex(minU, maxV).lightmap(bU, bV).endVertex();
				minU = icon.getInterpolatedU((1 - minZ) * 16);
				minV = icon.getInterpolatedV(minY * 16);
				maxU = icon.getInterpolatedU((1 - maxZ) * 16);
				maxV = icon.getInterpolatedV(maxY * 16);
				ccrs.r.pos(pos.x + MIN, pos.y + minY, pos.z + minZ).color(r, g, b, 1).tex(minU, minV).lightmap(bU, bV).endVertex();
				ccrs.r.pos(pos.x + MIN, pos.y + minY, pos.z + maxZ).color(r, g, b, 1).tex(maxU, minV).lightmap(bU, bV).endVertex();
				ccrs.r.pos(pos.x + MIN, pos.y + maxY, pos.z + maxZ).color(r, g, b, 1).tex(maxU, maxV).lightmap(bU, bV).endVertex();
				ccrs.r.pos(pos.x + MIN, pos.y + maxY, pos.z + minZ).color(r, g, b, 1).tex(minU, maxV).lightmap(bU, bV).endVertex();
			}
			renderAdditionalStatic(pos, layer, ccrs, bU, bV);
			return true;
		}
		else return renderAdditionalStatic(pos, layer, ccrs, bU, bV);
    }

    @SideOnly(Side.CLIENT)
	public boolean renderAdditionalStatic(Vector3 pos, BlockRenderLayer layer, CCRenderState ccrs, int bU, int bV)
    {
    	return false;
    }

	@Override
	public void load(NBTTagCompound tag)
	{
		super.load(tag);
		if (tag.hasKey("color", 99))
		{
			byte id = tag.getByte("color");
			if (id < 0 || id >= EnumDyeColor.values().length) color = null;
			else color = EnumDyeColor.values()[id];
		}
		else color = null;
		if (tag.hasKey("tier", 8)) setTierNoWorldUpdate(Utils.getEnum(tag.getString("tier"), EnumPipeTier.values(), EnumPipeTier.STONE));
		else setTierNoWorldUpdate(EnumPipeTier.STONE);
	}

	@Override
	public void save(NBTTagCompound tag)
	{
		super.save(tag);
		tag.setByte("color", (byte) (color == null ? -1 : color.ordinal()));
		tag.setString("tier", tier.name());
	}
}