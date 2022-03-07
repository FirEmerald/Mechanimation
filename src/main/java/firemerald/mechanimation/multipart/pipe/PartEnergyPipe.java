package firemerald.mechanimation.multipart.pipe;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.multipart.PartMap;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import firemerald.api.core.function.FloatConsumer;
import firemerald.mc4.api.capabilities.IThermalRenderer;
import firemerald.mechanimation.Main;
import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.init.MechanimationItems;
import firemerald.mechanimation.networking.client.PipeEnergySyncPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PartEnergyPipe extends PartPipe implements ICapabilityProvider, IThermalRenderer
{
	public static final ResourceLocation TYPE = new ResourceLocation(MechanimationAPI.MOD_ID, "energy_pipe");
	public static final ResourceLocation ICON = new ResourceLocation(MechanimationAPI.MOD_ID, "blocks/pipe/energy_pipe");
	@SideOnly(Side.CLIENT)
	public static TextureAtlasSprite icon;
	public int[] flow = {PipeFlow.NONE, PipeFlow.NONE, PipeFlow.NONE, PipeFlow.NONE, PipeFlow.NONE, PipeFlow.NONE};
	public int[] prevTransferredEnergy = new int[6];
	public int[] transferredEnergy = new int[6];
	public int maxPower = 2500;
	public int maxTransfer = 1000;
	public int prevPower = 0;
	public int curPower = 0;
	public int nextPower = 0;

	@Override
	public boolean canPipeConnect(EnumFacing side, TileEntity tile)
	{
		if (tile != null)
		{
			EnumFacing opp = side.getOpposite();
			//if (tile.hasCapability(PipeApi.CAP_PIPE, opp) && tile.getCapability(PipeApi.CAP_PIPE, opp).getFlow() instanceof IFlowPower) return true;
			if (tile.hasCapability(CapabilityEnergy.ENERGY, opp))
			{
				IEnergyStorage energy = tile.getCapability(CapabilityEnergy.ENERGY, opp);
				if (energy != null) return true;
			}
			return false;
		}
		else return false;
	}

	@Override
	public void setTierNoWorldUpdate(EnumPipeTier tier)
	{
		super.setTierNoWorldUpdate(tier);
		this.maxPower = tier.maxPower;
		this.maxTransfer = tier.maxPowerTransfer;
	}

	@Override
	public boolean canConnectToPipe(EnumFacing side, PartPipe pipe)
	{
		return pipe instanceof PartEnergyPipe && super.canConnectToPipe(side, pipe);
	}

	@Override
	public void load(NBTTagCompound tag)
	{
		super.load(tag);
		curPower = tag.getInteger("power");
        if (tag.hasKey("flow", 99))
        {
        	short flow = tag.getShort("flow");
        	for (int i = 0; i < 6; i++) this.flow[i] = (flow >>> (i * 2)) & 3;
        }
        else for (int i = 0; i < 6; i++) flow[i] = 0;
	}

	@Override
	public void save(NBTTagCompound tag)
	{
		super.save(tag);
		tag.setInteger("power", curPower);
		short flow = 0;
		for (int i = 0; i < 6; i++) flow |= this.flow[i] << (i * 2);
		tag.setShort("flow", flow);
	}

    @Override
    public void writeDesc(MCDataOutput packet)
    {
    	super.writeDesc(packet);
    	packet.writeInt(maxPower);
    	packet.writeInt(maxTransfer);
    }

    @Override
    public void readDesc(MCDataInput packet)
    {
    	super.readDesc(packet);
    	maxPower = packet.readInt();
    	maxTransfer = packet.readInt();
    }

	final Random rand = new Random();

	@Override
	public void update()
	{
		super.update();
		if (!world().isRemote)
		{
			for (int i = 0; i < 6; i++) if (flow[i] == PipeFlow.OUT) flow[i] = PipeFlow.NONE;
			int pow = this.curPower;
			if (this.curPower > 0)
			{
				int[] request = new int[EnumFacing.VALUES.length];
				for (int i = 0; i < request.length; i++)
				{
					if (flow[i] == PipeFlow.NONE) request[i] = maxTransfer(EnumFacing.VALUES[i]);
					else request[i] = 0;
				}
				long total = 0;
				for (int element : request)
					total += element;
				if (total > 0)
				{
					if (total > curPower) for (int i = 0; i < request.length; i++) request[i] = (int) Math.floor((request[i] * curPower) / total);
					for (int i = 0; i < request.length; i++)
					{
						if (request[i] > 0 && flow[i] == PipeFlow.NONE) tryTransfer(EnumFacing.VALUES[i], request[i]);
					}
				}
				//if (this.curPower > 0) evenPipes();
			}
			nextPower = curPower;
			/*if (this.curPower != prevEnergy || !equals(prevTransferredEnergy, transferredEnergy))*/ sendPowerUpdate(pow);
			for (int i = 0; i < 6; i++) flow[i] = PipeFlow.NONE;
			System.arraycopy(transferredEnergy, 0, prevTransferredEnergy, 0, prevTransferredEnergy.length);
			for (int i = 0; i < transferredEnergy.length; i++) transferredEnergy[i] = 0;
			prevPower = curPower;
		}
	}

	public void evenPipes()
	{
		PartEnergyPipe[] pipes = new PartEnergyPipe[6];
		int maxPower = this.maxPower, totalPower = 0;
		boolean canEven = false;
		for (int i = 0; i < 6; i++)
		{
			EnumFacing direction = EnumFacing.VALUES[i];
			if (this.isConnected(direction))
			{
				TileEntity tile = world().getTileEntity(pos().offset(direction));
				if (tile instanceof TileMultipart)
				{
					TMultiPart part = ((TileMultipart) tile).partMap(PartMap.CENTER.i);
					if (part instanceof PartEnergyPipe)
					{
						PartEnergyPipe pipe = (PartEnergyPipe) part;
						if (pipe.isConnected(direction.getOpposite()))
						{
							pipes[i] = pipe;
							maxPower += pipe.maxPower;
							totalPower += pipe.curPower;
							canEven = true;
						}
					}
				}
			}
		}
		if (!canEven) return;
		for (int i = 0; i < 6; i++) if (pipes[i] != null)
		{
			PartEnergyPipe pipe = pipes[i];
			int targetAmount = ((this.curPower + totalPower) * pipe.maxPower) / maxPower;
			int toTransfer = (targetAmount - pipe.curPower);
			if (toTransfer > this.maxTransfer) toTransfer = this.maxTransfer;
			if (toTransfer > pipe.maxTransfer) toTransfer = pipe.maxTransfer;
			if (toTransfer > 0)
			{
				targetAmount = toTransfer + pipe.curPower;
				int j = EnumFacing.VALUES[i].getOpposite().getIndex();
				this.transferredEnergy[i] = pipe.transferredEnergy[j] = toTransfer;
				this.curPower -= toTransfer;
				totalPower -= targetAmount;
				pipe.curPower = targetAmount;
				this.flow[i] = PipeFlow.OUT;
				pipe.flow[j] = PipeFlow.IN;
			}
		}
	}

	public void sendPowerUpdate(int curPower) //TODO on loaded by player
	{
		PipeEnergySyncPacket packet = new PipeEnergySyncPacket(pos(), prevPower, curPower, nextPower, transferredEnergy, flow);
		for (EntityPlayer player : world().playerEntities) if (player instanceof EntityPlayerMP)
		{
			if (player.getDistanceSq(this.pos()) < 4096.0D) Main.network().sendTo(packet, (EntityPlayerMP) player);
		}
	}

	public int maxTransfer(EnumFacing direction) //TODO kinesis pipes (maybe)
	{
		if (this.isConnected(direction))
		{
			TileEntity tile = world().getTileEntity(pos().offset(direction));
			if (tile != null)
			{
				/*
				if (tile instanceof TileMultipart) //ignore pipes
				{
					TMultiPart part = ((TileMultipart) tile).partMap(PartMap.CENTER.i);
					if (part instanceof PartEnergyPipe)
					{
						PartEnergyPipe pipe = (PartEnergyPipe) part;
						if (pipe.isConnected(direction.getOpposite())) return 0;
					}
				}*/
				EnumFacing opp = direction.getOpposite();
				IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, opp);
				if (storage != null)
				{
					if (storage.canReceive()) return storage.receiveEnergy(Integer.MAX_VALUE, true);
					else return 0;
				}
				else return 0;
			}
			else return 0;
		}
		else return 0;
	}

	public void tryTransfer(EnumFacing direction, int amount)
	{
		if (this.isConnected(direction))
		{
			TileEntity tile = world().getTileEntity(pos().offset(direction));
			if (tile != null)
			{
				/* TODO
				if (tile instanceof TileMultipart) //ignore pipes
				{
					TMultiPart part = ((TileMultipart) tile).partMap(PartMap.CENTER.i);
					if (part instanceof PartEnergyPipe)
					{
						PartEnergyPipe pipe = (PartEnergyPipe) part;
						if (pipe.isConnected(direction.getOpposite())) return;
					}
				}
				*/
				IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, direction.getOpposite());
				if (storage != null)
				{
					if (storage.canReceive())
					{
						int ind = direction.getIndex();
						int transferred = storage.receiveEnergy(Math.min(amount, maxTransfer), false);
						this.curPower -=  transferred;
						this.transferredEnergy[ind] += transferred;
						this.flow[ind] = PipeFlow.OUT;
					}
				}
			}
		}
	}

	@Override
	public ResourceLocation getType()
	{
		return TYPE;
	}

	@Override
	public ItemPartPipe getTheItem()
	{
		return MechanimationItems.ENERGY_PIPE;
	}

	@Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
    	if (FMLCommonHandler.instance().getSide().isClient() && capability == IThermalRenderer.CapabilityInstance.capability) return true;
    	else if (capability == CapabilityEnergy.ENERGY && facing != null && this.isConnected(facing)) return true;
    	else return false;
    }

    @SuppressWarnings("unchecked")
	@Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
    	if (FMLCommonHandler.instance().getSide().isClient() && capability == IThermalRenderer.CapabilityInstance.capability) return (T) this;
    	else if (capability == CapabilityEnergy.ENERGY)
    	{
        	if (facing != null && this.isConnected(facing)) return (T) new IEnergyStorage() {
				@Override
				public int receiveEnergy(int maxReceive, boolean simulate)
				{
					int ind = facing.getIndex();
					if (PartEnergyPipe.this.flow[ind] != PipeFlow.NONE && PartEnergyPipe.this.flow[ind] != PipeFlow.IN) return 0;
					else
					{
						int received = Math.min(Math.min(PartEnergyPipe.this.maxPower - PartEnergyPipe.this.curPower, PartEnergyPipe.this.maxTransfer), maxReceive);
						if (!simulate)
						{
							PartEnergyPipe.this.curPower += received;
							PartEnergyPipe.this.transferredEnergy[ind] += received;
							PartEnergyPipe.this.flow[ind] = PipeFlow.IN;
						}
						return received;
					}
				}

				@Override
				public int extractEnergy(int maxExtract, boolean simulate) //cannot pull from pipes, only push
				{
					return 0;
				}

				@Override
				public int getEnergyStored()
				{
					return PartEnergyPipe.this.curPower;
				}

				@Override
				public int getMaxEnergyStored()
				{
					return PartEnergyPipe.this.maxPower;
				}

				@Override
				public boolean canExtract()
				{
					return false;
				}

				@Override
				public boolean canReceive()
				{
					return true;
				}
        	};
        	else return null;
    	}
    	else return null;
    }

	public boolean canOutput(EnumFacing side)
	{
		return this.isConnected(side);
	}

	public boolean canInput(EnumFacing side)
	{
		return this.isConnected(side);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite getIcon()
	{
		return icon == null ? Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite() : icon;
	}

	private boolean hasPower()
	{
		if (this.curPower > 0) return true;
		else for (int trans : this.transferredEnergy) if (trans > 0) return true;
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderThermals(float partial, FloatConsumer setTemp)
	{
		if (hasPower())
		{
			setTemp.accept(1);
			GlStateManager.disableTexture2D();
	        Tessellator t = Tessellator.getInstance();
	        BufferBuilder b = t.getBuffer();
	        b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_NORMAL);
	        double cX = pos().getX() + .5;
	        double cY = pos().getY() + .5;
	        double cZ = pos().getZ() + .5;
	        double prevAmount = (double) this.prevPower / this.maxPower;
	        double amount = (double) this.curPower / this.maxPower;
	        double nextAmount = (double) this.nextPower / this.maxPower;
	        double r = (PartPipe.MAX - .5) * amount;
	        drawCuboid(cX - r, cY - r, cZ - r, cX + r, cY + r, cZ + r, b);
	        for (int i = 0; i < 6; i++)
	        {
	        	if (this.isConnected(EnumFacing.VALUES[i]))
	        	{
	                double sR = (MAX - .5) * ((flow[i] == PipeFlow.IN ? prevAmount : flow[i] == PipeFlow.OUT ? nextAmount : amount) + (double) this.transferredEnergy[i] / this.maxPower);
	                switch (EnumFacing.VALUES[i])
	                {
	                case UP:
	                	drawCuboid(cX - sR, cY + r, cZ - sR, cX + sR, cY + .5, cZ + sR, b);
	                	break;
	                case DOWN:
	                	drawCuboid(cX - sR, cY - .5, cZ - sR, cX + sR, cY - r, cZ + sR, b);
	                	break;
	                case NORTH:
	                	drawCuboid(cX - sR, cY - sR, cZ - .5, cX + sR, cY + sR, cZ - r, b);
	                	break;
	                case SOUTH:
	                	drawCuboid(cX - sR, cY - sR, cZ + r, cX + sR, cY + sR, cZ + .5, b);
	                	break;
	                case EAST:
	                	drawCuboid(cX + r, cY - sR, cZ - sR, cX + .5, cY + sR, cZ + sR, b);
	                	break;
	                case WEST:
	                	drawCuboid(cX - .5, cY - sR, cZ - sR, cX - r, cY + sR, cZ + sR, b);
	                	break;
	                }
	        	}
	        }
	        t.draw();
			GlStateManager.enableTexture2D();
		}
	}

	private static void drawCuboid(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, BufferBuilder b)
	{
		{ //top
			b.pos(minX, maxY, minZ).normal(0, 1, 0).endVertex();
			b.pos(minX, maxY, maxZ).normal(0, 1, 0).endVertex();
			b.pos(maxX, maxY, maxZ).normal(0, 1, 0).endVertex();
			b.pos(maxX, maxY, minZ).normal(0, 1, 0).endVertex();
		}
		{ //bottom
			b.pos(minX, minY, minZ).normal(0, -1, 0).endVertex();
			b.pos(maxX, minY, minZ).normal(0, -1, 0).endVertex();
			b.pos(maxX, minY, maxZ).normal(0, -1, 0).endVertex();
			b.pos(minX, minY, maxZ).normal(0, -1, 0).endVertex();
		}
		{ //north
			b.pos(minX, minY, minZ).normal(0, 0, -1).endVertex();
			b.pos(minX, maxY, minZ).normal(0, 0, -1).endVertex();
			b.pos(maxX, maxY, minZ).normal(0, 0, -1).endVertex();
			b.pos(maxX, minY, minZ).normal(0, 0, -1).endVertex();
		}
		{ //south
			b.pos(minX, minY, maxZ).normal(0, 0, 1).endVertex();
			b.pos(maxX, minY, maxZ).normal(0, 0, 1).endVertex();
			b.pos(maxX, maxY, maxZ).normal(0, 0, 1).endVertex();
			b.pos(minX, maxY, maxZ).normal(0, 0, 1).endVertex();
		}
		{ //east
			b.pos(maxX, minY, minZ).normal(1, 0, 0).endVertex();
			b.pos(maxX, maxY, minZ).normal(1, 0, 0).endVertex();
			b.pos(maxX, maxY, maxZ).normal(1, 0, 0).endVertex();
			b.pos(maxX, minY, maxZ).normal(1, 0, 0).endVertex();
		}
		{ //west
			b.pos(minX, minY, minZ).normal(-1, 0, 0).endVertex();
			b.pos(minX, minY, maxZ).normal(-1, 0, 0).endVertex();
			b.pos(minX, maxY, maxZ).normal(-1, 0, 0).endVertex();
			b.pos(minX, maxY, minZ).normal(-1, 0, 0).endVertex();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float[] getDefaultColorValues()
	{
		return new float[] {1, 0, 0};
	}
}