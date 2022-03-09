package firemerald.mechanimation.compat.forgemultipart.pipe;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import buildcraft.api.transport.IInjectable;
import buildcraft.api.transport.pipe.IFlowItems;
import buildcraft.api.transport.pipe.PipeApi;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import codechicken.multipart.TDynamicRenderPart;
import firemerald.mechanimation.Main;
import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.capabilities.PlayerSettings;
import firemerald.mechanimation.compat.forgemultipart.ItemExtractor;
import firemerald.mechanimation.compat.forgemultipart.MultipartItems;
import firemerald.mechanimation.compat.forgemultipart.networking.PipeItemsSyncPacket;
import firemerald.mechanimation.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class PartItemPipe extends PartExtractablePipe implements TDynamicRenderPart, ICapabilityProvider, IInjectable
{
	public static final ResourceLocation TYPE = new ResourceLocation(MechanimationAPI.MOD_ID, "item_pipe");
	public static final ResourceLocation ICON = new ResourceLocation(MechanimationAPI.MOD_ID, "blocks/pipe/item_pipe");
	@SideOnly(Side.CLIENT)
	public static TextureAtlasSprite icon;
	public static final ResourceLocation EXTRACTOR_ICON = new ResourceLocation(MechanimationAPI.MOD_ID, "blocks/pipe/item_extractor");
	@SideOnly(Side.CLIENT)
	public static TextureAtlasSprite extractor_icon;

	public float[] transferAmount = new float[6];

	public final List<TravelingStack> stacks = new ArrayList<>();

	@Override
	public boolean canPipeConnect(EnumFacing side, TileEntity tile)
	{
		if (tile != null)
		{
			EnumFacing opp = side.getOpposite();
			if ((tile.hasCapability(PipeApi.CAP_PIPE, opp) && tile.getCapability(PipeApi.CAP_PIPE, opp).getFlow() instanceof IFlowItems) || (tile.hasCapability(PipeApi.CAP_INJECTABLE, null) && tile.getCapability(PipeApi.CAP_INJECTABLE, null).canInjectItems(opp))) return true;
			if (tile.hasCapability(PipeApi.CAP_INJECTABLE, null) && tile.getCapability(PipeApi.CAP_INJECTABLE, null).canInjectItems(opp)) return true;
			if (tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, opp))
			{
				IItemHandler inv = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, opp);
				if (inv != null && inv.getSlots() > 0) return true;
			}
			return false;
		}
		else return false;
	}

	@Override
	public boolean canConnectToPipe(EnumFacing side, PartPipe pipe)
	{
		return pipe instanceof PartItemPipe && super.canConnectToPipe(side, pipe);
	}

	@Override
	public void onRemoved()
	{
		if (!stacks.isEmpty()) for (TravelingStack stack : stacks) stack.dropItem(this);
	}

	@Override
	public void load(NBTTagCompound tag)
	{
		super.load(tag);
		stacks.clear();
		NBTTagList list = tag.getTagList("stacks", 10);
		if (list != null) for (int i = 0; i < list.tagCount(); i++) stacks.add(new TravelingStack(list.getCompoundTagAt(i)));
		list = tag.getTagList("transferAmount", 5);
		for (int i = 0; i < 6; i++) transferAmount[i] = list == null || i >= list.tagCount() ? 0 : list.getFloatAt(i);
	}

	@Override
	public void save(NBTTagCompound tag)
	{
		super.save(tag);
		NBTTagList list = new NBTTagList();
		for (TravelingStack stack : stacks) list.appendTag(stack.writeData());
		tag.setTag("stacks", list);
		list = new NBTTagList();
		for (int i = 0; i < 6; i++) list.appendTag(new NBTTagFloat(transferAmount[i]));
		tag.setTag("transferAmount", list);
	}

	@Override
	public void update()
	{
		super.update();
		if (world().isRemote && !ClientConfig.INSTANCE.showItems.val) if (!stacks.isEmpty()) stacks.clear();
		int size = stacks.size();
		//TODO combine stacks if possible
		for (int i = 0; i < size; i++)
		{
			TravelingStack stack = stacks.get(i);
			if (stack != null && stack.stack != null && !stack.stack.isEmpty() && stack.stack.getCount() > 0) stack.update(this);
			if (stack == null || stack.stack == null || stack.stack.isEmpty() || stack.stack.getCount() <= 0)
			{
				stacks.remove(i);
				i--;
				size--;
			}
		}
		if (!world().isRemote)
		{
			for (int i = 0; i < 6; i++)
			{
				if (transferAmount[i] < tier.maxItemExtract) transferAmount[i] += tier.maxItemExtract;
				if (transferAmount[i] > 0 && extractors[i])
				{
					EnumFacing direction = EnumFacing.getFront(i);
					EnumFacing extractDir = direction.getOpposite();
					TileEntity tile = world().getTileEntity(pos().offset(direction));
					if (tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, extractDir))
					{
						IItemHandler inv = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, extractDir);
						for (int slot = 0; transferAmount[i] > 0 && slot < inv.getSlots(); slot++)
						{
							ItemStack extracted = inv.extractItem(slot, MathHelper.ceil(transferAmount[i]), false);
							if (!extracted.isEmpty())
							{
								transferAmount[i] -= extracted.getCount();
								stacks.add(new TravelingStack(extracted, direction));
							}
						}
					}
				}
			}
			PipeItemsSyncPacket packet = new PipeItemsSyncPacket(this);
			for (EntityPlayer player : world().playerEntities) if (player instanceof EntityPlayerMP)
			{
				if (player.getDistanceSq(this.pos()) < 4096.0D)
				{
					PlayerSettings settings = player.getCapability(PlayerSettings.playerSettings, null);
					if (settings == null || settings.viewItemsInPipes) Main.network().sendTo(packet, (EntityPlayerMP) player);
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
		return MultipartItems.ITEM_PIPE;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderDynamic(Vector3 pos, int pass, float partial)
	{
		if (!this.stacks.isEmpty())
		{
			GL11.glPushMatrix();
			GL11.glTranslated(pos.x, pos.y, pos.z);
			int light = world().getCombinedLight(pos(), 0);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, light & 0xFFFF, (light >>> 16) & 0xFFFF);
			for (TravelingStack stack : this.stacks)
			{
				float[] pos2 = stack.getOffsetPos(partial);
				GlStateManager.pushMatrix();
				GlStateManager.translate(pos2[0], pos2[1], pos2[2]);
				GlStateManager.scale(.375f, .375f, .375f);
				Axis travelAxis;
				if (stack.travelDistance <= 10) travelAxis = stack.from.getAxis();
				else travelAxis = stack.to.getAxis();
				switch (travelAxis)
				{
				case X:
					GlStateManager.rotate(90, 0, 1, 0);
					break;
				case Y:
					GlStateManager.rotate(90, 1, 0, 0);
					break;
				default:
				}
				Minecraft.getMinecraft().getRenderItem().renderItem(stack.stack, TransformType.FIXED);
				GlStateManager.popMatrix();
			}
			GL11.glPopMatrix();
		}
	}

	@Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
    	if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != null && this.isConnected(facing)) return true;
    	else if (capability == PipeApi.CAP_INJECTABLE && (facing == null || this.isConnected(facing))) return true;
    	//else if (capability == CapUtil.CAP_ITEM_TRANSACTOR && facing != null && this.isConnected(facing)) return true;
    	else return false;
    }

    @SuppressWarnings("unchecked")
	@Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
    	if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
    	{
        	if (facing != null && this.isConnected(facing)) return (T) new IItemHandler() {
    			@Override
    			public int getSlots()
    			{
    				return 1;
    			}

    			@Override
    			public ItemStack getStackInSlot(int slot)
    			{
    				return ItemStack.EMPTY;
    			}

    			@Override
    			public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
    			{
    				if (!simulate)
    				{
    					PartItemPipe.this.stacks.add(new TravelingStack(stack, facing));
    				}
    				return ItemStack.EMPTY;
    			}

    			@Override
    			public ItemStack extractItem(int slot, int amount, boolean simulate)
    			{
    				return ItemStack.EMPTY;
    			}

    			@Override
    			public int getSlotLimit(int slot)
    			{
    				return Integer.MAX_VALUE;
    			}
        	};
        	else return null;
    	}
    	else if (capability == PipeApi.CAP_INJECTABLE)
    	{
    		if (facing == null) return (T) this;
    		else if (this.isConnected(facing)) return (T) new IInjectable() {
				@Override
				public boolean canInjectItems(EnumFacing from)
				{
					return from == facing && PartItemPipe.this.canInjectItems(from);
				}

				@Override
				public ItemStack injectItem(ItemStack stack, boolean doAdd, EnumFacing from, EnumDyeColor color, double speed)
				{
					if (from == facing) return PartItemPipe.this.injectItem(stack, doAdd, from, color, speed);
					else return stack;
				}

    		};
    		else return null;
    	}
    	/*
    	else if (capability == CapUtil.CAP_ITEM_TRANSACTOR)
    	{
    		if (facing != null && this.isConnected(facing)) return (T) new IItemTransactor() {
    			@Override
    			public ItemStack insert(ItemStack stack, boolean allOrNone, boolean simulate)
    			{
    				if (!simulate) PartItemPipe.this.stacks.add(new TravelingStack(stack, facing));
    				return ItemStack.EMPTY;
    			}

    			@Override
    			public ItemStack extract(IStackFilter filter, int min, int max, boolean simulate)
    			{
    				return ItemStack.EMPTY;
    			}
    		};
    		else return null;
    	}
    	*/
    	else return null;
    }

	@Override
	@SideOnly(Side.CLIENT)
	public Cuboid6 getRenderBounds()
	{
		return this.getBounds();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean canRenderDynamic(int pass)
	{
		return ClientConfig.INSTANCE.showItems.val && pass == 0;
	}

	public boolean canOutput(EnumFacing side)
	{
		return this.isConnected(side) && !this.extractors[side.ordinal()]; //do not allow items to output from extractors
	}

	public boolean canInput(EnumFacing side)
	{
		return this.isConnected(side);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite getIcon()
	{
		return icon == null ? Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite() : icon;
	}

	@Override
	public boolean canInjectItems(EnumFacing from)
	{
		return this.isConnected(from);
	}

	@Override
	public ItemStack injectItem(ItemStack stack, boolean doAdd, EnumFacing from, EnumDyeColor color, double speed)
	{
		if (this.isConnected(from))
		{
			if (doAdd) this.stacks.add(new TravelingStack(stack, from));
			return ItemStack.EMPTY;
		}
		else return stack;
	}


	@Override
	public boolean isValidExtractor(ItemStack item)
	{
		return item.getItem() == MultipartItems.EXTRACTOR && item.getItemDamage() == ItemExtractor.ID_ITEM;
	}

	@Override
	public ItemStack getExtractorItem(int side)
	{
		return new ItemStack(MultipartItems.EXTRACTOR, 1, ItemExtractor.ID_ITEM);
	}

	@Override
	public TextureAtlasSprite getExtractorIcon(EnumFacing side)
	{
		return extractor_icon == null ? Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite() : extractor_icon;
	}
}