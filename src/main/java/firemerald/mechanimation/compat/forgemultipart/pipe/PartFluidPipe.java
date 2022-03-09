package firemerald.mechanimation.compat.forgemultipart.pipe;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import codechicken.multipart.PartMap;
import codechicken.multipart.TDynamicRenderPart;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.api.capabilities.Capabilities;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.client.gui.GuiUtils;
import firemerald.mechanimation.compat.forgemultipart.ForgeMultipartCompat;
import firemerald.mechanimation.compat.forgemultipart.ItemExtractor;
import firemerald.mechanimation.compat.forgemultipart.MultipartItems;
import firemerald.mechanimation.compat.forgemultipart.networking.PipeFluidSyncPacket;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTankInfo;
import mekanism.api.gas.IGasHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PartFluidPipe extends PartExtractablePipe implements ICapabilityProvider, TDynamicRenderPart, IGasHandler//TODO, IThermalRenderer
{
	public static final ResourceLocation TYPE = new ResourceLocation(MechanimationAPI.MOD_ID, "fluid_pipe");
	public static final ResourceLocation ICON = new ResourceLocation(MechanimationAPI.MOD_ID, "blocks/pipe/fluid_pipe");
	@SideOnly(Side.CLIENT)
	public static TextureAtlasSprite icon;
	public static final ResourceLocation EXTRACTOR_ICON = new ResourceLocation(MechanimationAPI.MOD_ID, "blocks/pipe/fluid_extractor");
	@SideOnly(Side.CLIENT)
	public static TextureAtlasSprite extractor_icon;
	public int[] flow = {PipeFlow.NONE, PipeFlow.NONE, PipeFlow.NONE, PipeFlow.NONE, PipeFlow.NONE, PipeFlow.NONE};
	public int[] prevTransferredFluid = new int[6];
	public int[] transferredFluid = new int[6];
	public int maxFluid = 1000;
	public int maxTransfer = 400;
	public int prevFluid = 0;
	public int nextFluid = 0;
	public FluidOrGasStack stored = null;
	public double offsetCenterX, offsetCenterY, offsetCenterZ;
	public double[] offsetConnection = new double[6];
	public double prevOffsetCenterX, prevOffsetCenterY, prevOffsetCenterZ;
	public double[] prevOffsetConnection = new double[6];

	@Override
	public void setTierNoWorldUpdate(EnumPipeTier tier)
	{
		super.setTierNoWorldUpdate(tier);
		this.maxFluid = tier.maxFluid;
		this.maxTransfer = tier.maxFluidTransfer;
	}

	@Override
	public boolean canPipeConnect(EnumFacing side, TileEntity tile)
	{
		if (tile != null)
		{
			if (tile instanceof IGasHandler) return true;
			EnumFacing opp = side.getOpposite();
			//if (tile.hasCapability(PipeApi.CAP_PIPE, opp) && tile.getCapability(PipeApi.CAP_PIPE, opp).getFlow() instanceof IFlowPower) return true;
			if (tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, opp))
			{
				IFluidHandler storage = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, opp);
				if (storage != null) return true;
			}
			return false;
		}
		else return false;
	}

	@Override
	public boolean canConnectToPipe(EnumFacing side, PartPipe pipe)
	{
		return pipe instanceof PartFluidPipe && super.canConnectToPipe(side, pipe);
	}

	@Override
	public void load(NBTTagCompound tag)
	{
		super.load(tag);
        if (tag.hasKey("fluid", 10)) stored = FluidOrGasStack.readFromNBT(tag.getCompoundTag("fluid"));
        else stored = null;
        if (tag.hasKey("flow", 99))
        {
        	short flow = tag.getShort("flow");
        	for (int i = 0; i < 6; i++) this.flow[i] = (flow >>> (i * 2)) & 3;
        }
        else for (int i = 0; i < 6; i++) flow[i] = 0;
	}

    @Override
    public void writeDesc(MCDataOutput packet)
    {
    	super.writeDesc(packet);
    	packet.writeInt(maxFluid);
    	packet.writeInt(maxTransfer);
    }

    @Override
    public void readDesc(MCDataInput packet)
    {
    	super.readDesc(packet);
    	maxFluid = packet.readInt();
    	maxTransfer = packet.readInt();
    }

	@Override
	public void save(NBTTagCompound tag)
	{
		super.save(tag);
		if (stored != null) tag.setTag("fluid", stored.writeToNBT(new NBTTagCompound()));
		short flow = 0;
		for (int i = 0; i < 6; i++) flow |= this.flow[i] << (i * 2);
		tag.setShort("flow", flow);
	}

	final Random rand = new Random();

	@Override
	public void update()
	{
		super.update();
		if (!world().isRemote)
		{
			for (int i = 0; i < 6; i++) if ((this.stored == null || this.stored.getAmount() < this.maxFluid) && extractors[i] && flow[i] != PipeFlow.OUT && this.transferredFluid[i] < this.maxTransfer)
			{
				EnumFacing direction = EnumFacing.getFront(i);
				TileEntity tile = world().getTileEntity(pos().offset(direction));
				if (tile != null)
				{
					if (tile instanceof TileMultipart) //ignore pipes
					{
						TMultiPart part = ((TileMultipart) tile).partMap(PartMap.CENTER.i);
						if (part instanceof PartFluidPipe)
						{
							PartFluidPipe pipe = (PartFluidPipe) part;
							if (pipe.isConnected(direction.getOpposite())) continue;
						}
					}
					if (stored == null)
					{
						IFluidHandler storage = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite());
						if (storage != null)
						{
							FluidStack drained = storage.drain(Math.min(maxTransfer - this.transferredFluid[i], this.maxFluid), true);
							if (drained != null)
							{
								stored = FluidOrGasStack.forFluid(drained);
								this.transferredFluid[i] += drained.amount;
								this.flow[i] = PipeFlow.IN;
								continue;
							}
						}
						IGasHandler handler = tile.getCapability(Capabilities.gasHandler, direction.getOpposite());
						if (handler != null && handler.canDrawGas(direction.getOpposite(), null))
						{
							GasStack drained = handler.drawGas(direction.getOpposite(), Math.min(maxTransfer - this.transferredFluid[i], this.maxFluid), true);
							if (drained != null)
							{
								stored = FluidOrGasStack.forGas(drained);
								this.transferredFluid[i] += drained.amount;
								this.flow[i] = PipeFlow.IN;
								continue;
							}
						}
					}
					else
					{
						if (stored.isFluid())
						{
							IFluidHandler storage = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite());
							if (storage != null)
							{
								FluidStack test = stored.getFluidStack().copy();
								test.amount = Math.min(maxTransfer - this.transferredFluid[i], this.maxFluid - stored.getAmount());
								FluidStack drained = storage.drain(test, true);
								if (drained != null)
								{
									stored.changeAmount(drained.amount);
									this.transferredFluid[i] += drained.amount;
									this.flow[i] = PipeFlow.IN;
									continue;
								}
							}
						}
						if (stored.isGas())
						{
							IGasHandler handler = tile.getCapability(Capabilities.gasHandler, direction.getOpposite());
							if (handler != null && handler.canDrawGas(direction.getOpposite(), stored.getGasStack().getGas()))
							{
								GasStack drained = handler.drawGas(direction.getOpposite(), Math.min(maxTransfer - this.transferredFluid[i], this.maxFluid - stored.getAmount()), true);
								if (drained != null)
								{
									stored.changeAmount(drained.amount);
									this.transferredFluid[i] += drained.amount;
									this.flow[i] = PipeFlow.IN;
									continue;
								}
							}
						}
					}
				}
			}
			for (int i = 0; i < 6; i++) if (flow[i] == PipeFlow.OUT) flow[i] = PipeFlow.NONE;
			FluidOrGasStack prevStored = stored == null ? null : stored.copy();
			if (this.stored != null)
			{
				int[] request = new int[6];
				for (int i = 0; i < 6; i++)
				{
					if (flow[i] == PipeFlow.NONE) request[i] = maxTransfer(EnumFacing.VALUES[i]);
					else request[i] = 0;
				}
				long total = 0;
				for (int i = 0; i < 6; i++) total += request[i];
				if (total > 0)
				{
					if (total > stored.getAmount()) for (int i = 0; i < 6; i++) request[i] = (int) Math.floor(((double) request[i] * stored.getAmount()) / total);
					for (int i = 0; i < 6; i++)
					{
						if (request[i] > 0 && flow[i] == PipeFlow.NONE) tryTransfer(EnumFacing.VALUES[i], Math.min(request[i], this.maxTransfer));
					}
				}
				if (this.stored.getAmount() > 0) evenPipes();
				if (this.stored.getAmount() <= 0) this.stored = null;
			}
			this.nextFluid = this.stored == null ? 0 : this.stored.getAmount();
			/*if (this.curPower != prevEnergy || !equals(prevTransferredEnergy, transferredEnergy))*/ sendFluidUpdate(prevStored);
			for (int i = 0; i < 6; i++) flow[i] = PipeFlow.NONE;
			System.arraycopy(transferredFluid, 0, prevTransferredFluid, 0, 6);
			for (int i = 0; i < 6; i++) transferredFluid[i] = 0;
			prevFluid = stored != null ? stored.getAmount() : 0;
		}
		else //update pipe flow
		{
			this.prevOffsetCenterX = this.offsetCenterX;
			this.prevOffsetCenterY = this.offsetCenterY;
			this.prevOffsetCenterZ = this.offsetCenterZ;
			System.arraycopy(this.offsetConnection, 0, this.prevOffsetConnection, 0, 6);
			final double maxSpeed = 0.0005;
			for (int i = 0; i < 6; i++) //TODO speed based on transferred
			{
				switch (flow[i])
				{
				case PipeFlow.IN:
				{
					EnumFacing dir = EnumFacing.VALUES[i];
					double speed = maxSpeed * this.transferredFluid[i];
					this.offsetConnection[i] -= speed;
					offsetCenterX -= dir.getFrontOffsetX() * speed * .5;
					offsetCenterY -= dir.getFrontOffsetY() * speed * .5;
					offsetCenterZ -= dir.getFrontOffsetZ() * speed * .5;
					break;
				}
				case PipeFlow.OUT:
				{
					EnumFacing dir = EnumFacing.VALUES[i];
					double speed = maxSpeed * this.transferredFluid[i];
					this.offsetConnection[i] += speed;
					offsetCenterX += dir.getFrontOffsetX() * speed * .5;
					offsetCenterY += dir.getFrontOffsetY() * speed * .5;
					offsetCenterZ += dir.getFrontOffsetZ() * speed * .5;
					break;
				}
				}
			}
		}
	}

	public void sendFluidUpdate(FluidOrGasStack fluid) //TODO on loaded by player
	{
		PipeFluidSyncPacket packet = new PipeFluidSyncPacket(pos(), prevFluid, fluid, nextFluid, transferredFluid, flow);
		for (EntityPlayer player : world().playerEntities) if (player instanceof EntityPlayerMP)
		{
			if (player.getDistanceSq(this.pos()) < 4096.0D) ForgeMultipartCompat.INSTANCE.network.sendTo(packet, (EntityPlayerMP) player);
		}
	}

	public void evenPipes()
	{
		PartFluidPipe[] pipes = new PartFluidPipe[6];
		float thisAmount = (float) this.stored.getAmount() / this.maxFluid;
		float targetAmount = thisAmount;
		float numPipes = 1;
		for (int i = 0; i < 6; i++)
		{
			EnumFacing direction = EnumFacing.VALUES[i];
			if (this.isConnected(direction))
			{
				TileEntity tile = world().getTileEntity(pos().offset(direction));
				if (tile instanceof TileMultipart)
				{
					TMultiPart part = ((TileMultipart) tile).partMap(PartMap.CENTER.i);
					if (part instanceof PartFluidPipe)
					{
						PartFluidPipe pipe = (PartFluidPipe) part;
						int j = EnumFacing.VALUES[i].getOpposite().getIndex();
						if (pipe.isConnected(direction.getOpposite()) && this.flow[i] == PipeFlow.NONE && pipe.flow[j] == PipeFlow.NONE)
						{
							if (pipe.stored == null || pipe.stored.isFluidEqual(stored))
							{
								pipes[i] = pipe;
								if (pipe.stored != null) targetAmount += (float) pipe.stored.getAmount() / pipe.maxFluid;
								numPipes++;
							}
						}
					}
				}
			}
		}
		if (numPipes <= 1) return;
		targetAmount /= numPipes;
		for (int i = 0; i < 6; i++) if (!extractors[i] && pipes[i] != null) //cannot push through an extractor
		{
			PartFluidPipe pipe = pipes[i];
			int pipeAmount = pipe.stored == null ? 0 : pipe.stored.getAmount();
			float amount = (float) pipeAmount / pipe.maxFluid;
			if (amount < targetAmount)
			{
				int toTransfer = MathHelper.ceil((targetAmount * pipe.maxFluid) - pipeAmount);
				if (toTransfer > this.stored.getAmount()) toTransfer = stored.getAmount();
				int j = EnumFacing.VALUES[i].getOpposite().getIndex();
				this.stored.changeAmount(-toTransfer);
				if (pipe.stored == null)
				{
					pipe.stored = stored.copy();
					pipe.stored.setAmount(toTransfer);
				}
				else pipe.stored.changeAmount(toTransfer);
				this.transferredFluid[i] = pipe.transferredFluid[j] = toTransfer;
				//System.out.println(toTransfer + ": " + this.stored.amount + ": " + pipe.stored.amount);
				this.flow[i] = PipeFlow.OUT;
				pipe.flow[j] = PipeFlow.IN;
				if (stored.getAmount() == 0) return;
			}
		}
	} //TODO just use change in fluid

	public int maxTransfer(EnumFacing direction)
	{
		if (this.isConnected(direction) && !this.extractors[direction.getIndex()])
		{
			TileEntity tile = world().getTileEntity(pos().offset(direction));
			if (tile != null)
			{
				if (tile instanceof TileMultipart) //ignore pipes
				{
					TMultiPart part = ((TileMultipart) tile).partMap(PartMap.CENTER.i);
					if (part instanceof PartFluidPipe)
					{
						PartFluidPipe pipe = (PartFluidPipe) part;
						if (pipe.isConnected(direction.getOpposite())) return 0;
					}
				}
				if (stored.isFluid())
				{
					IFluidHandler storage = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite());
					if (storage != null)
					{
						FluidStack test = stored.getFluidStack().copy();
						test.amount = Integer.MAX_VALUE;
						return storage.fill(test, false);
					}
					else return 0;
				}
				else if (stored.isGas())
				{
					IGasHandler handler = tile.getCapability(Capabilities.gasHandler, direction.getOpposite());
					if (handler != null && handler.canReceiveGas(direction.getOpposite(), stored.getGasStack().getGas()))
					{
						GasStack test = stored.getGasStack().copy();
						test.amount = Integer.MAX_VALUE;
						return handler.receiveGas(direction.getOpposite(), test, false);
					}
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
		if (!extractors[direction.ordinal()] && this.isConnected(direction)) //cannot output through extractors
		{
			TileEntity tile = world().getTileEntity(pos().offset(direction));
			if (tile != null)
			{
				if (tile instanceof TileMultipart) //ignore pipes
				{
					TMultiPart part = ((TileMultipart) tile).partMap(PartMap.CENTER.i);
					if (part instanceof PartFluidPipe)
					{
						PartFluidPipe pipe = (PartFluidPipe) part;
						if (pipe.isConnected(direction.getOpposite())) return;
					}
				}
				if (stored.isFluid())
				{
					IFluidHandler storage = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite());
					if (storage != null)
					{
						FluidStack fill = stored.getFluidStack().copy();
						fill.amount = amount;
						amount = storage.fill(fill, true);
						if (amount > 0)
						{
							int ind = direction.getIndex();
							stored.changeAmount(-amount);
							this.flow[ind] = PipeFlow.OUT;
							this.transferredFluid[ind] += amount;
						}
					}
				}
				else if (stored.isGas())
				{
					IGasHandler handler = tile.getCapability(Capabilities.gasHandler, direction.getOpposite());
					if (handler != null && handler.canReceiveGas(direction.getOpposite(), stored.getGasStack().getGas()))
					{
						GasStack fill = stored.getGasStack().copy();
						fill.amount = amount;
						amount = handler.receiveGas(direction.getOpposite(), fill, true);
						if (amount > 0)
						{
							int ind = direction.getIndex();
							stored.changeAmount(-amount);
							this.flow[ind] = PipeFlow.OUT;
							this.transferredFluid[ind] += amount;
						}
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
		return MultipartItems.FLUID_PIPE;
	}

	@Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
    	if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing != null && this.isConnected(facing)) return true;
    	else if (capability == Capabilities.gasHandler && facing != null && this.isConnected(facing)) return true;
    	else return false;
    }

    @SuppressWarnings("unchecked")
	@Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
    	if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
    	{
        	if (facing != null && this.isConnected(facing)) return (T) new IFluidHandler() {
				@Override
				public IFluidTankProperties[] getTankProperties()
				{
					return new IFluidTankProperties[] {new FluidTankProperties(PartFluidPipe.this.stored == null ? null : PartFluidPipe.this.stored.getFluidStack(), PartFluidPipe.this.maxFluid, true, false)};
				}

				@Override
				public int fill(FluidStack resource, boolean doFill)
				{
					int ind = facing.getIndex();
					if (PartFluidPipe.this.flow[ind] != PipeFlow.NONE && PartFluidPipe.this.flow[ind] != PipeFlow.IN) return 0;
					else if (PartFluidPipe.this.stored == null)
					{
						int amount = Math.min(PartFluidPipe.this.maxTransfer, resource.amount);
						if (doFill)
						{
							PartFluidPipe.this.stored = FluidOrGasStack.forFluid(resource.copy());
							PartFluidPipe.this.stored.setAmount(amount);
							PartFluidPipe.this.flow[ind] = PipeFlow.IN;
							PartFluidPipe.this.transferredFluid[ind] += amount;
						}
						return amount;
					}
					else if (PartFluidPipe.this.stored.isFluidEqual(resource))
					{
						int amount = Math.min(Math.min(PartFluidPipe.this.maxFluid - PartFluidPipe.this.stored.getAmount(), PartFluidPipe.this.maxTransfer), resource.amount);
						if (amount > 0)
						{
							if (doFill)
							{
								resource = resource.copy();
								resource.amount = amount;
								PartFluidPipe.this.stored.changeAmount(amount);
								PartFluidPipe.this.flow[ind] = PipeFlow.IN;
								PartFluidPipe.this.transferredFluid[ind] += amount;
							}
							return amount;
						}
						else return 0;
					}
					else return 0;
				}

				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain)
				{
					return null;
				}

				@Override
				public FluidStack drain(int maxDrain, boolean doDrain)
				{
					return null;
				}
        	};
        	else return null;
    	}
    	else if (capability == Capabilities.gasHandler)
    	{
    		if (facing != null && this.isConnected(facing)) return (T) this;
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

	@Override
	public boolean canRenderDynamic(int pass)
	{
		return pass == 0 && this.stored != null;
	}

	@Override
	public void renderDynamic(Vector3 pos, int pass, float partial)
	{
		if (this.stored != null)
		{
			int light = world().getCombinedLight(pos(), 0);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, light & 0xFFFF, (light >>> 16) & 0xFFFF);
            double max = MAX - (1f / 256f);
	        double cX = pos.x + .5;
	        double cY = pos.y + .5;
	        double cZ = pos.z + .5;
	        double prevAmount = (double) this.prevFluid / this.maxFluid;
	        double amount = (double) this.stored.getAmount() / this.maxFluid;
	        double nextAmount = (double) this.nextFluid / this.maxFluid;
	        double r = (max - .5) * amount;
	        GlStateManager.enableBlend();
	        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
	        GuiUtils.setColorFrom(stored);
			TextureAtlasSprite sprite = GuiUtils.getSprite(Minecraft.getMinecraft(), stored);
			Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			Tessellator t = Tessellator.getInstance();
			BufferBuilder b = t.getBuffer();
			b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
	        PartFluidPipe.renderFluid(pos, cX - r, cY - r, cZ - r, cX + r, cY + r, cZ + r,
	        		this.prevOffsetCenterX + (this.offsetCenterX - this.prevOffsetCenterX) * partial,
	        		this.prevOffsetCenterY + (this.offsetCenterY - this.prevOffsetCenterY) * partial,
	        		this.prevOffsetCenterZ + (this.offsetCenterZ - this.prevOffsetCenterZ) * partial,
	        		sprite, b);
	        for (int i = 0; i < 6; i++)
	        {
	        	EnumFacing side = EnumFacing.VALUES[i];
	        	if (this.isConnected(side))
	        	{
	                double sR = r * ((flow[i] == PipeFlow.IN ? prevAmount : flow[i] == PipeFlow.OUT ? nextAmount : amount) + (double) this.transferredFluid[i] / this.maxFluid);
	                switch (side)
	                {
	                case UP:
	                	renderFluid(pos, cX - sR, cY + r, cZ - sR, cX + sR, cY + .5, cZ + sR,
	                			0, this.prevOffsetConnection[i] + (this.offsetConnection[i] - this.prevOffsetConnection[i]) * partial, 0,
	                			sprite, b);
	                	break;
	                case DOWN:
	                	renderFluid(pos, cX - sR, cY - .5, cZ - sR, cX + sR, cY - r, cZ + sR,
	                			0, -(this.prevOffsetConnection[i] + (this.offsetConnection[i] - this.prevOffsetConnection[i]) * partial), 0,
	                			sprite, b);
	                	break;
	                case NORTH:
	        	        renderFluid(pos, cX - sR, cY - sR, cZ - .5, cX + sR, cY + sR, cZ - r,
	                			0, 0, -(this.prevOffsetConnection[i] + (this.offsetConnection[i] - this.prevOffsetConnection[i]) * partial),
	                			sprite, b);
	                	break;
	                case SOUTH:
	        	        renderFluid(pos, cX - sR, cY - sR, cZ + r, cX + sR, cY + sR, cZ + .5,
	                			0, 0, this.prevOffsetConnection[i] + (this.offsetConnection[i] - this.prevOffsetConnection[i]) * partial,
	                			sprite, b);
	                	break;
	                case EAST:
	        	        renderFluid(pos, cX + r, cY - sR, cZ - sR, cX + .5, cY + sR, cZ + sR,
	                			this.prevOffsetConnection[i] + (this.offsetConnection[i] - this.prevOffsetConnection[i]) * partial, 0, 0,
	                			sprite, b);
	                	break;
	                case WEST:
	        	        renderFluid(pos, cX - .5, cY - sR, cZ - sR, cX - r, cY + sR, cZ + sR,
	                			-(this.prevOffsetConnection[i] + (this.offsetConnection[i] - this.prevOffsetConnection[i]) * partial), 0, 0,
	                			sprite, b);
	                	break;
	                }
	        	}
	        }
			t.draw();
			GlStateManager.color(1f, 1f, 1f, 1f);
	        GlStateManager.disableBlend();
		}
	}

	private static void renderFluid(Vector3 base, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, double offX, double offY, double offZ, TextureAtlasSprite sprite, BufferBuilder b)
	{
		offX = MathHelper.frac(offX - minX + base.x);
		offY = MathHelper.frac(offY - minY + base.y);
		offZ = MathHelper.frac(offZ - minZ + base.z);
		double minTX = minX + offX;
		double minTY = minY + offY;
		double minTZ = minZ + offZ;
		double maxTX = offX > 0 ? maxX + offX - 1 : maxX;
		//double maxTY = offY > 0 ? maxY + offY - 1 : maxY;
		double maxTZ = offZ > 0 ? maxZ + offZ - 1 : maxZ;
		{
			double x = minX;
			while (x < maxX)
			{
				double nX = Math.min(maxX, x + 1);
				double uP;
				float u0;
				if (x < minTX)
				{
					u0 = sprite.getInterpolatedU(uP = (16 - (minTX - x) * 16));
					nX = Math.min(nX, minTX);
				}
				else
				{
					uP = 0;
					u0 = sprite.getMinU();
				}
				float u = sprite.getInterpolatedU(uP + (nX - x) * 16);
				double y = minY;
				while (y < maxY)
				{
					double nY = Math.min(maxY, y + 1);
					double vP;
					float v0;
					if (y < minTY)
					{
						v0 = sprite.getInterpolatedV(vP = (16 - (minTY - y) * 16));
						nY = Math.min(nY, minTY);
					}
					else
					{
						vP = 0;
						v0 = sprite.getMinV();
					}
					float v = sprite.getInterpolatedV(vP + (nY - y) * 16);
					b.pos(x , y , minZ).tex(u0, v0).normal(0, 0, -1).endVertex();
					b.pos(x , nY, minZ).tex(u0, v ).normal(0, 0, -1).endVertex();
					b.pos(nX, nY, minZ).tex(u , v ).normal(0, 0, -1).endVertex();
					b.pos(nX, y , minZ).tex(u , v0).normal(0, 0, -1).endVertex();
					y = nY;
				}
				x = nX;
			}
		}
		{
			double z = maxZ;
			while (z > minZ)
			{
				double nZ = Math.max(minZ, z - 1);
				double uP;
				float u2;
				if (z > maxTZ)
				{
					if (nZ < maxTZ) nZ = maxTZ;
					u2 = sprite.getInterpolatedU(uP = (1 - offZ) * 16);
				}
				else
				{
					uP = 16;
					u2 = sprite.getMaxU();
				}
				float u = sprite.getInterpolatedU(uP - (z - nZ) * 16);
				double y = minY;
				while (y < maxY)
				{
					double nY = Math.min(maxY, y + 1);
					double vP;
					float v0;
					if (y < minTY)
					{
						v0 = sprite.getInterpolatedV(vP = (16 - (minTY - y) * 16));
						nY = Math.min(nY, minTY);
					}
					else
					{
						vP = 0;
						v0 = sprite.getMinV();
					}
					float v = sprite.getInterpolatedV(vP + (nY - y) * 16);
					b.pos(minX, y , z ).tex(u2, v0).normal(-1, 0, 0).endVertex();
					b.pos(minX, nY, z ).tex(u2, v ).normal(-1, 0, 0).endVertex();
					b.pos(minX, nY, nZ).tex(u , v ).normal(-1, 0, 0).endVertex();
					b.pos(minX, y , nZ).tex(u , v0).normal(-1, 0, 0).endVertex();
					y = nY;
				}
				z = nZ;
			}
		}
		{
			double x = maxX;
			while (x > minX)
			{
				double nX = Math.max(minX, x - 1);
				double uP;
				float u2;
				if (x > maxTX)
				{
					if (nX < maxTX) nX = maxTX;
					u2 = sprite.getInterpolatedU(uP = (1 - offX) * 16);
				}
				else
				{
					uP = 16;
					u2 = sprite.getMaxU();
				}
				float u = sprite.getInterpolatedU(uP - (x - nX) * 16);
				double y = minY;
				while (y < maxY)
				{
					double nY = Math.min(maxY, y + 1);
					double vP;
					float v0;
					if (y < minTY)
					{
						v0 = sprite.getInterpolatedV(vP = (16 - (minTY - y) * 16));
						nY = Math.min(nY, minTY);
					}
					else
					{
						vP = 0;
						v0 = sprite.getMinV();
					}
					float v = sprite.getInterpolatedV(vP + (nY - y) * 16);
					b.pos(x , y , maxZ).tex(u2, v0).normal(0, 0, 1).endVertex();
					b.pos(x , nY, maxZ).tex(u2, v ).normal(0, 0, 1).endVertex();
					b.pos(nX, nY, maxZ).tex(u , v ).normal(0, 0, 1).endVertex();
					b.pos(nX, y , maxZ).tex(u , v0).normal(0, 0, 1).endVertex();
					y = nY;
				}
				x = nX;
			}
		}
		{
			double z = minZ;
			while (z < maxZ)
			{
				double nZ = Math.min(maxZ, z + 1);
				double uP;
				float u0;
				if (z < minTZ)
				{
					u0 = sprite.getInterpolatedU(uP = (16 - (minTZ - z) * 16));
					nZ = Math.min(nZ, minTZ);
				}
				else
				{
					uP = 0;
					u0 = sprite.getMinU();
				}
				float u = sprite.getInterpolatedU(uP + (nZ - z) * 16);
				double y = minY;
				while (y < maxY)
				{
					double nY = Math.min(maxY, y + 1);
					double vP;
					float v0;
					if (y < minTY)
					{
						v0 = sprite.getInterpolatedV(vP = (16 - (minTY - y) * 16));
						nY = Math.min(nY, minTY);
					}
					else
					{
						vP = 0;
						v0 = sprite.getMinV();
					}
					float v = sprite.getInterpolatedV(vP + (nY - y) * 16);
					b.pos(maxX, y , z ).tex(u0, v0).normal(1, 0, 0).endVertex();
					b.pos(maxX, nY, z ).tex(u0, v ).normal(1, 0, 0).endVertex();
					b.pos(maxX, nY, nZ).tex(u , v ).normal(1, 0, 0).endVertex();
					b.pos(maxX, y , nZ).tex(u , v0).normal(1, 0, 0).endVertex();
					y = nY;
				}
				z = nZ;
			}
		}
		{
			double x = minX;
			while (x < maxX)
			{
				double nX = Math.min(maxX, x + 1);
				double uP;
				float u0;
				if (x < minTX)
				{
					u0 = sprite.getInterpolatedU(uP = (16 - (minTX - x) * 16));
					nX = Math.min(nX, minTX);
				}
				else
				{
					uP = 0;
					u0 = sprite.getMinU();
				}
				float u = sprite.getInterpolatedU(uP + (nX - x) * 16);
				double z = minZ;
				while (z < maxZ)
				{
					double nZ = Math.min(maxZ, z + 1);
					double vP;
					float v0;
					if (z < minTZ)
					{
						v0 = sprite.getInterpolatedV(vP = (16 - (minTZ - z) * 16));
						nZ = Math.min(nZ, minTZ);
					}
					else
					{
						vP = 0;
						v0 = sprite.getMinV();
					}
					float v = sprite.getInterpolatedV(vP + (nZ - z) * 16);
					b.pos(x , maxY, z ).tex(u0, v0).normal(0, 1, 0).endVertex();
					b.pos(x , maxY, nZ).tex(u0, v ).normal(0, 1, 0).endVertex();
					b.pos(nX, maxY, nZ).tex(u , v ).normal(0, 1, 0).endVertex();
					b.pos(nX, maxY, z ).tex(u , v0).normal(0, 1, 0).endVertex();
					z = nZ;
				}
				x = nX;
			}
		}
		{
			double x = minX;
			while (x < maxX)
			{
				double nX = Math.min(maxX, x + 1);
				double uP;
				float u0;
				if (x < minTX)
				{
					u0 = sprite.getInterpolatedU(uP = (16 - (minTX - x) * 16));
					nX = Math.min(nX, minTX);
				}
				else
				{
					uP = 0;
					u0 = sprite.getMinU();
				}
				float u = sprite.getInterpolatedU(uP + (nX - x) * 16);
				double z = maxZ;
				while (z > minZ)
				{
					double nZ = Math.max(minZ, z - 1);
					double vP;
					float v2;
					if (z > maxTZ)
					{
						if (nZ < maxTZ) nZ = maxTZ;
						v2 = sprite.getInterpolatedV(vP = (1 - offZ) * 16);
					}
					else
					{
						vP = 16;
						v2 = sprite.getMaxV();
					}
					float v = sprite.getInterpolatedV(vP - (z - nZ) * 16);
					b.pos(x , minY, z ).tex(u0, v2).normal(0, -1, 0).endVertex();
					b.pos(x , minY, nZ).tex(u0, v ).normal(0, -1, 0).endVertex();
					b.pos(nX, minY, nZ).tex(u , v ).normal(0, -1, 0).endVertex();
					b.pos(nX, minY, z ).tex(u , v2).normal(0, -1, 0).endVertex();
					z = nZ;
				}
				x = nX;
			}
		}
	}

	@Override
	public Cuboid6 getRenderBounds()
	{
		return this.getBounds();
	}

	@Override
	public int receiveGas(EnumFacing side, GasStack stack, boolean doTransfer)
	{
		if (this.isConnected(side) && (this.stored == null || this.stored.isGasEqual(stack)))
		{
			int amount = Math.min(Math.min(stack.amount, this.maxTransfer), this.maxFluid - (stored == null ? 0 : stored.getAmount()));
			if (amount < 0) return 0;
			else if (amount > 0 && doTransfer)
			{
				if (this.stored == null)
				{
					this.stored = FluidOrGasStack.forGas(stack.copy());
					this.stored.setAmount(amount);
					int ind = side.getIndex();
					this.flow[ind] = PipeFlow.IN;
					this.transferredFluid[ind] += amount;
				}
				else this.stored.changeAmount(amount);
			}
			return amount;
		}
		else return 0;
	}

	@Override
	public GasStack drawGas(EnumFacing side, int amount, boolean doTransfer)
	{
		return null;
	}

	@Override
	public boolean canReceiveGas(EnumFacing side, Gas type)
	{
		return this.isConnected(side) && (this.stored == null || (this.stored.isGas() && this.stored.getGasStack().getGas() == type));
	}

	@Override
	public boolean canDrawGas(EnumFacing side, Gas type)
	{
		return false;
	}

	@Override
    public GasTankInfo[] getTankInfo()
    {
        return new GasTankInfo[] {
        		new GasTankInfo() {
					@Override
					public GasStack getGas()
					{
						return stored == null || !stored.isGas() ? null : stored.getGasStack();
					}

					@Override
					public int getStored()
					{
						return stored == null || !stored.isGas() ? null : stored.getAmount();
					}

					@Override
					public int getMaxGas()
					{
						return maxFluid;
					}
        		}
        };
    }

	@Override
	public boolean isValidExtractor(ItemStack item)
	{
		return item.getItem() == MultipartItems.EXTRACTOR && item.getItemDamage() == ItemExtractor.ID_FLUID;
	}

	@Override
	public ItemStack getExtractorItem(int side)
	{
		return new ItemStack(MultipartItems.EXTRACTOR, 1, ItemExtractor.ID_FLUID);
	}

	@Override
	public TextureAtlasSprite getExtractorIcon(EnumFacing side)
	{
		return extractor_icon == null ? Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite() : extractor_icon;
	}
}