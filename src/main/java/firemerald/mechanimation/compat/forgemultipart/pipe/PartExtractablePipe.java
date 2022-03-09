package firemerald.mechanimation.compat.forgemultipart.pipe;

import java.util.List;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.raytracer.CuboidRayTraceResult;
import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class PartExtractablePipe extends PartPipe
{
	public static final double MIN_E = 4.0 / 16.0;
	public static final double MAX_E = 12.0 / 16.0;
	public static final double MIN_E_2 = 3.0 / 16.0;
	public static final double MAX_E_2 = 13.0 / 16.0;

	public boolean[] extractors = new boolean[6];
	public byte extractorsMask;

	public void dropExtractor(int index)
	{
		if (extractors[index])
		{
			ItemStack item = this.getExtractorItem(index);
			if (item != null) //TODO drop
			extractors[index] = false;
			extractorsMask &= ~(1 << index);
			this.tile().markDirty();
		}
	}

	public boolean addExtractor(int index, ItemStack item)
	{
		if (!extractors[index] && connections[index] && isValidExtractor(item))
		{
			if (world() != null && !world().isRemote)
			{
				extractors[index] = true;
				extractorsMask |= 1 << index;
			}
			this.tile().markDirty();
			return true;
		}
		else return false;
	}

	public abstract boolean isValidExtractor(ItemStack item);

	@Override
    public boolean activate(EntityPlayer player, CuboidRayTraceResult hit, ItemStack item, EnumHand hand)
    {
		if (hit.cuboid6.data instanceof PipeSide)
		{
			int side = ((PipeSide) hit.cuboid6.data).side.getIndex();
			if (this.extractors[side]) //remove
			{
				if (world() != null && !world().isRemote)
				{
					dropExtractor(side);
					this.sendDescUpdate();
					world().notifyNeighborsOfStateChange(pos(), tile().blockType, true);
				}
				return true;
			}
			else if (addExtractor(side, item))
			{
				if (world() != null && !world().isRemote)
				{
					this.sendDescUpdate();
					world().notifyNeighborsOfStateChange(pos(), tile().blockType, true);
					if (!player.capabilities.isCreativeMode) item.shrink(1);
				}
				return true;
			}
			else return false;
		}
		else return false;
    }

	@Override
	public void update()
	{
		super.update();
		if (this.world() != null && !this.world().isRemote)
		{
			//TODO update extractors
			byte prevExt = extractorsMask;
			for (int i = 0; i < 6; i++) if (!connections[i] && extractors[i]) dropExtractor(i);
			if (prevExt != extractorsMask)
			{
				this.sendDescUpdate();
				world().notifyNeighborsOfStateChange(pos(), tile().blockType, true);
			}
		}
	}

	@Override
	public int getHollowSize(int side)
	{
		return extractors[side] ? 8 : 6;
	}

	@Override
	public List<Cuboid6> getCollisionBoxes()
	{
		List<Cuboid6> list = super.getCollisionBoxes();
		if (extractors[EnumFacing.WEST .getIndex()]) list.add(new Cuboid6(0      , MIN_E  , MIN_E  , MIN_E_2, MAX_E  , MAX_E  ));
		if (extractors[EnumFacing.EAST .getIndex()]) list.add(new Cuboid6(MAX_E_2, MIN_E  , MIN_E  , 1      , MAX_E  , MAX_E  ));
		if (extractors[EnumFacing.DOWN .getIndex()]) list.add(new Cuboid6(MIN_E  , 0      , MIN_E  , MAX_E  , MIN_E_2, MAX_E  ));
		if (extractors[EnumFacing.UP   .getIndex()]) list.add(new Cuboid6(MIN_E  , MAX_E_2, MIN_E  , MAX_E  , 1      , MAX_E  ));
		if (extractors[EnumFacing.NORTH.getIndex()]) list.add(new Cuboid6(MIN_E  , MIN_E  , 0      , MAX_E  , MAX_E  , MIN_E_2));
		if (extractors[EnumFacing.SOUTH.getIndex()]) list.add(new Cuboid6(MIN_E  , MIN_E  , MAX_E_2, MAX_E  , MAX_E  , 1      ));
		return list;
	}

	@Override
	public Cuboid6 getBounds()
	{
		double minX = MIN, maxX = MAX, minY = MIN, maxY = MAX, minZ = MIN, maxZ = MAX;
		if (extractors[EnumFacing.UP.getIndex()] || extractors[EnumFacing.DOWN.getIndex()])
		{
			minX = MIN_E;
			minZ = MIN_E;
			maxX = MAX_E;
			maxZ = MAX_E;
		}
		if (extractors[EnumFacing.NORTH.getIndex()] || extractors[EnumFacing.SOUTH.getIndex()])
		{
			minX = MIN_E;
			minY = MIN_E;
			maxX = MAX_E;
			maxY = MAX_E;
		}
		if (extractors[EnumFacing.EAST.getIndex()] || extractors[EnumFacing.WEST.getIndex()])
		{
			minY = MIN_E;
			minZ = MIN_E;
			maxY = MAX_E;
			maxZ = MAX_E;
		}
		if (connections[EnumFacing.WEST.getIndex()]) minX = 0;
		if (connections[EnumFacing.EAST.getIndex()]) maxX = 1;
		if (connections[EnumFacing.DOWN.getIndex()]) minY = 0;
		if (connections[EnumFacing.UP.getIndex()]) maxY = 1;
		if (connections[EnumFacing.NORTH.getIndex()]) minZ = 0;
		if (connections[EnumFacing.SOUTH.getIndex()]) maxZ = 1;
		return new Cuboid6(minX, minY, minZ, maxX, maxY, maxZ);
	}

	public static class PipeExtractorSide extends PipeSide
	{
		public PipeExtractorSide(PartPipe pipe, EnumFacing side)
		{
			super(pipe, side);
		}
	}

	@Override
	public List<IndexedCuboid6> getSubParts()
	{
		List<IndexedCuboid6> list = super.getSubParts();
		if (extractors[EnumFacing.WEST .getIndex()]) list.add(new IndexedCuboid6(new PipeExtractorSide(this, EnumFacing.WEST ), new Cuboid6(0      , MIN_E  , MIN_E  , MIN_E_2, MAX_E  , MAX_E  )));
		if (extractors[EnumFacing.EAST .getIndex()]) list.add(new IndexedCuboid6(new PipeExtractorSide(this, EnumFacing.EAST ), new Cuboid6(MAX_E_2, MIN_E  , MIN_E  , 1      , MAX_E  , MAX_E  )));
		if (extractors[EnumFacing.DOWN .getIndex()]) list.add(new IndexedCuboid6(new PipeExtractorSide(this, EnumFacing.DOWN ), new Cuboid6(MIN_E  , 0      , MIN_E  , MAX_E  , MIN_E_2, MAX_E  )));
		if (extractors[EnumFacing.UP   .getIndex()]) list.add(new IndexedCuboid6(new PipeExtractorSide(this, EnumFacing.UP   ), new Cuboid6(MIN_E  , MAX_E_2, MIN_E  , MAX_E  , 1      , MAX_E  )));
		if (extractors[EnumFacing.NORTH.getIndex()]) list.add(new IndexedCuboid6(new PipeExtractorSide(this, EnumFacing.NORTH), new Cuboid6(MIN_E  , MIN_E  , 0      , MAX_E  , MAX_E  , MIN_E_2)));
		if (extractors[EnumFacing.SOUTH.getIndex()]) list.add(new IndexedCuboid6(new PipeExtractorSide(this, EnumFacing.SOUTH), new Cuboid6(MIN_E  , MIN_E  , MAX_E_2, MAX_E  , MAX_E  , 1      )));
		return list;
	}

	public abstract ItemStack getExtractorItem(int side);

	@Override
	public ItemStack pickItem(CuboidRayTraceResult hit)
	{
		if (hit.cuboid6.data instanceof PipeExtractorSide) return getExtractorItem(((PipeExtractorSide) hit.cuboid6.data).side.getIndex());
		else return getItem();
	}

	@Override
	public List<ItemStack> getDrops()
	{
		List<ItemStack> drops = super.getDrops();
		for (int i = 0; i < 6; i++) if (extractors[i]) drops.add(getExtractorItem(i));
		return drops;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public abstract BlockRenderLayer getRenderLayer();

    @Override
    public void writeDesc(MCDataOutput packet)
    {
    	super.writeDesc(packet);
    	packet.writeByte(extractorsMask);
    }

    @Override
    public void readDesc(MCDataInput packet)
    {
    	super.readDesc(packet);
    	setExtractors(packet.readByte());
    }

    public void setExtractors(byte extractorsMask)
    {
    	this.extractorsMask = extractorsMask;
    	for (int i = 0; i < 6; i++) extractors[i] = (extractorsMask & (1 << i)) != 0;
    }


    @SideOnly(Side.CLIENT)
    public abstract TextureAtlasSprite getExtractorIcon(EnumFacing side);

	@Override
    @SideOnly(Side.CLIENT)
	public boolean renderAdditionalStatic(Vector3 pos, BlockRenderLayer layer, CCRenderState ccrs, int bU, int bV)
	{
		if (layer == BlockRenderLayer.CUTOUT && extractorsMask != 0)
		{
			if (extractors[EnumFacing.WEST .getIndex()]) renderCuboid(pos, 0      , MIN_E  , MIN_E  , MIN_E_2, MAX_E  , MAX_E  , getExtractorIcon(EnumFacing.WEST ), ccrs, bU, bV);
			if (extractors[EnumFacing.EAST .getIndex()]) renderCuboid(pos, MAX_E_2, MIN_E  , MIN_E  , 1      , MAX_E  , MAX_E  , getExtractorIcon(EnumFacing.EAST ), ccrs, bU, bV);
			if (extractors[EnumFacing.DOWN .getIndex()]) renderCuboid(pos, MIN_E  , 0      , MIN_E  , MAX_E  , MIN_E_2, MAX_E  , getExtractorIcon(EnumFacing.DOWN ), ccrs, bU, bV);
			if (extractors[EnumFacing.UP   .getIndex()]) renderCuboid(pos, MIN_E  , MAX_E_2, MIN_E  , MAX_E  , 1      , MAX_E  , getExtractorIcon(EnumFacing.UP   ), ccrs, bU, bV);
			if (extractors[EnumFacing.NORTH.getIndex()]) renderCuboid(pos, MIN_E  , MIN_E  , 0      , MAX_E  , MAX_E  , MIN_E_2, getExtractorIcon(EnumFacing.NORTH), ccrs, bU, bV);
			if (extractors[EnumFacing.SOUTH.getIndex()]) renderCuboid(pos, MIN_E  , MIN_E  , MAX_E_2, MAX_E  , MAX_E  , 1      , getExtractorIcon(EnumFacing.SOUTH), ccrs, bU, bV);
			return true;
		}
		else return false;
	}

    @SideOnly(Side.CLIENT)
    public void renderCuboid(Vector3 pos, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, TextureAtlasSprite icon, CCRenderState ccrs, int bU, int bV)
    {
		{ //top
			float minU = icon.getInterpolatedU(minX * 16);
			float minV = icon.getInterpolatedV(minZ * 16);
			float maxU = icon.getInterpolatedU(maxX * 16);
			float maxV = icon.getInterpolatedV(maxZ * 16);
			ccrs.r.pos(pos.x + minX, pos.y + maxY, pos.z + minZ).color(1f, 1f, 1f, 1f).tex(minU, minV).lightmap(bU, bV).endVertex();
			ccrs.r.pos(pos.x + minX, pos.y + maxY, pos.z + maxZ).color(1f, 1f, 1f, 1f).tex(minU, maxV).lightmap(bU, bV).endVertex();
			ccrs.r.pos(pos.x + maxX, pos.y + maxY, pos.z + maxZ).color(1f, 1f, 1f, 1f).tex(maxU, maxV).lightmap(bU, bV).endVertex();
			ccrs.r.pos(pos.x + maxX, pos.y + maxY, pos.z + minZ).color(1f, 1f, 1f, 1f).tex(maxU, minV).lightmap(bU, bV).endVertex();
		}
		{ //bottom
			float minU = icon.getInterpolatedU(minX * 16);
			float minV = icon.getInterpolatedV((1 - minZ) * 16);
			float maxU = icon.getInterpolatedU(maxX * 16);
			float maxV = icon.getInterpolatedV((1 - maxZ) * 16);
			ccrs.r.pos(pos.x + minX, pos.y + minY, pos.z + minZ).color(1f, 1f, 1f, 1f).tex(minU, minV).lightmap(bU, bV).endVertex();
			ccrs.r.pos(pos.x + maxX, pos.y + minY, pos.z + minZ).color(1f, 1f, 1f, 1f).tex(maxU, minV).lightmap(bU, bV).endVertex();
			ccrs.r.pos(pos.x + maxX, pos.y + minY, pos.z + maxZ).color(1f, 1f, 1f, 1f).tex(maxU, maxV).lightmap(bU, bV).endVertex();
			ccrs.r.pos(pos.x + minX, pos.y + minY, pos.z + maxZ).color(1f, 1f, 1f, 1f).tex(minU, maxV).lightmap(bU, bV).endVertex();
		}
		{ //north
			float minU = icon.getInterpolatedU(minX * 16);
			float minV = icon.getInterpolatedV(minY * 16);
			float maxU = icon.getInterpolatedU(maxX * 16);
			float maxV = icon.getInterpolatedV(maxY * 16);
			ccrs.r.pos(pos.x + minX, pos.y + minY, pos.z + minZ).color(1f, 1f, 1f, 1f).tex(minU, minV).lightmap(bU, bV).endVertex();
			ccrs.r.pos(pos.x + minX, pos.y + maxY, pos.z + minZ).color(1f, 1f, 1f, 1f).tex(minU, maxV).lightmap(bU, bV).endVertex();
			ccrs.r.pos(pos.x + maxX, pos.y + maxY, pos.z + minZ).color(1f, 1f, 1f, 1f).tex(maxU, maxV).lightmap(bU, bV).endVertex();
			ccrs.r.pos(pos.x + maxX, pos.y + minY, pos.z + minZ).color(1f, 1f, 1f, 1f).tex(maxU, minV).lightmap(bU, bV).endVertex();
		}
		{ //south
			float minU = icon.getInterpolatedU((1 - minX) * 16);
			float minV = icon.getInterpolatedV(minY * 16);
			float maxU = icon.getInterpolatedU((1 - maxX) * 16);
			float maxV = icon.getInterpolatedV(maxY * 16);
			ccrs.r.pos(pos.x + minX, pos.y + minY, pos.z + maxZ).color(1f, 1f, 1f, 1f).tex(minU, minV).lightmap(bU, bV).endVertex();
			ccrs.r.pos(pos.x + maxX, pos.y + minY, pos.z + maxZ).color(1f, 1f, 1f, 1f).tex(maxU, minV).lightmap(bU, bV).endVertex();
			ccrs.r.pos(pos.x + maxX, pos.y + maxY, pos.z + maxZ).color(1f, 1f, 1f, 1f).tex(maxU, maxV).lightmap(bU, bV).endVertex();
			ccrs.r.pos(pos.x + minX, pos.y + maxY, pos.z + maxZ).color(1f, 1f, 1f, 1f).tex(minU, maxV).lightmap(bU, bV).endVertex();
		}
		{ //east
			float minU = icon.getInterpolatedU(minZ * 16);
			float minV = icon.getInterpolatedV(minY * 16);
			float maxU = icon.getInterpolatedU(maxZ * 16);
			float maxV = icon.getInterpolatedV(maxY * 16);
			ccrs.r.pos(pos.x + maxX, pos.y + minY, pos.z + minZ).color(1f, 1f, 1f, 1f).tex(minU, minV).lightmap(bU, bV).endVertex();
			ccrs.r.pos(pos.x + maxX, pos.y + maxY, pos.z + minZ).color(1f, 1f, 1f, 1f).tex(minU, maxV).lightmap(bU, bV).endVertex();
			ccrs.r.pos(pos.x + maxX, pos.y + maxY, pos.z + maxZ).color(1f, 1f, 1f, 1f).tex(maxU, maxV).lightmap(bU, bV).endVertex();
			ccrs.r.pos(pos.x + maxX, pos.y + minY, pos.z + maxZ).color(1f, 1f, 1f, 1f).tex(maxU, minV).lightmap(bU, bV).endVertex();
		}
		{ //west
			float minU = icon.getInterpolatedU((1 - minZ) * 16);
			float minV = icon.getInterpolatedV(minY * 16);
			float maxU = icon.getInterpolatedU((1 - maxZ) * 16);
			float maxV = icon.getInterpolatedV(maxY * 16);
			ccrs.r.pos(pos.x + minX, pos.y + minY, pos.z + minZ).color(1f, 1f, 1f, 1f).tex(minU, minV).lightmap(bU, bV).endVertex();
			ccrs.r.pos(pos.x + minX, pos.y + minY, pos.z + maxZ).color(1f, 1f, 1f, 1f).tex(maxU, minV).lightmap(bU, bV).endVertex();
			ccrs.r.pos(pos.x + minX, pos.y + maxY, pos.z + maxZ).color(1f, 1f, 1f, 1f).tex(maxU, maxV).lightmap(bU, bV).endVertex();
			ccrs.r.pos(pos.x + minX, pos.y + maxY, pos.z + minZ).color(1f, 1f, 1f, 1f).tex(minU, maxV).lightmap(bU, bV).endVertex();
		}
    }

	@Override
	public void load(NBTTagCompound tag)
	{
		super.load(tag);
    	setExtractors(tag.getByte("extractors"));
	}

	@Override
	public void save(NBTTagCompound tag)
	{
		super.save(tag);
		tag.setByte("extractors", extractorsMask);
	}
}