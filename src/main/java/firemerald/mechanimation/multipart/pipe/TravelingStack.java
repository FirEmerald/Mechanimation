package firemerald.mechanimation.multipart.pipe;

import java.util.LinkedList;

import buildcraft.api.transport.pipe.PipeApi;
import codechicken.multipart.PartMap;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class TravelingStack
{
	public int travelDistance = 0;
	public EnumFacing from = EnumFacing.DOWN;
	public EnumFacing to = EnumFacing.UP;
	public ItemStack stack;
	public boolean setDirection = false;

	public TravelingStack(ItemStack stack, EnumFacing from)
	{
		this.stack = stack;
		travelDistance = 0;
		this.from = from;
	}

	public TravelingStack(NBTTagCompound tag)
	{
		travelDistance = tag.getInteger("travelDistance");
		from = EnumFacing.getFront(tag.getByte("from"));
		to = EnumFacing.getFront(tag.getByte("to"));
		stack = new ItemStack(tag.getCompoundTag("stack"));
		if (travelDistance > 10) setDirection = true;
	}

	public boolean canOutput(PartItemPipe src, TileEntity des, EnumFacing from, EnumFacing to)
	{
		if (!src.canOutput(from)) return false;
		else
		{
			PartItemPipe desPipe;
			if (des instanceof TileMultipart)
			{
				TMultiPart part = ((TileMultipart) des).partMap(PartMap.CENTER.i);
				if (part instanceof PartItemPipe) desPipe = (PartItemPipe) part;
				else desPipe = null;
			}
			else desPipe = null;
			if (desPipe != null && desPipe.canInput(to)) return true;
			else if (des != null)
			{
				if ((des.hasCapability(PipeApi.CAP_INJECTABLE, null) && des.getCapability(PipeApi.CAP_INJECTABLE, null).canInjectItems(to)) || (des.hasCapability(PipeApi.CAP_INJECTABLE, to) && des.getCapability(PipeApi.CAP_INJECTABLE, to).canInjectItems(to))) return true;
				if (des.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, to))
				{
					IItemHandler inv = des.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, to);
					if (inv != null && inv.getSlots() > 0) for (int i = 0; i < inv.getSlots(); i++) if (inv.isItemValid(i, stack)) return true;
				}
			}
			return false;
		}
	}

	public void output(PartItemPipe src, TileEntity des, EnumFacing from, EnumFacing to)
	{
		if (canOutput(src, des, from, to))
		{
			PartItemPipe desPipe;
			if (des instanceof TileMultipart)
			{
				TMultiPart part = ((TileMultipart) des).partMap(PartMap.CENTER.i);
				if (part instanceof PartItemPipe) desPipe = (PartItemPipe) part;
				else desPipe = null;
			}
			else desPipe = null;
			if (desPipe != null)
			{
				desPipe.stacks.add(new TravelingStack(stack.copy(), to));
				stack = ItemStack.EMPTY;
			}
			if (!src.world().isRemote && des != null)
			{
				if (des.hasCapability(PipeApi.CAP_INJECTABLE, null)) stack = des.getCapability(PipeApi.CAP_INJECTABLE, null).injectItem(stack, true, to, null, .05f);
				if (stack.isEmpty()) return;
				if (des.hasCapability(PipeApi.CAP_INJECTABLE, to)) stack = des.getCapability(PipeApi.CAP_INJECTABLE, to).injectItem(stack, true, to, null, .05f);
				if (stack.isEmpty()) return;
				if (des.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, to))
				{
					IItemHandler inv = des.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, to);
					if (inv != null && inv.getSlots() > 0) for (int i = 0; i < inv.getSlots(); i++)
					{
						if (inv.isItemValid(i, stack))
						{
							stack = inv.insertItem(i, stack, false);
							if (stack.isEmpty()) return;
						}
					}
				}
			}
		}
	}

	public void drop(PartItemPipe pipe)
	{
		dropItem(pipe);
		this.stack = ItemStack.EMPTY;
	}

	protected void setDirectionValid(PartItemPipe pipe)
	{
		LinkedList<EnumFacing> vD = new LinkedList<>();
		for (EnumFacing d : EnumFacing.values())
		{
			TileEntity tile = pipe.world().getTileEntity(pipe.pos().offset(d));
			if (tile != null && this.canOutput(pipe, tile, d, d.getOpposite())) vD.add(d);
		}
		if (vD.size() == 0) drop(pipe);
		else
		{
			vD.remove(from);
			if (vD.size() == 0) to = from;
			else to = vD.get(pipe.world().rand.nextInt(vD.size()));
		}
	}

	public NBTTagCompound writeData()
	{
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("travelDistance", travelDistance);
		tag.setByte("from", (byte) from.ordinal());
		tag.setByte("to", (byte) to.ordinal());
		tag.setTag("stack", stack.writeToNBT(new NBTTagCompound()));
		return tag;
	}

	public void update(PartItemPipe pipe)
	{
		travelDistance++;
		if (from == null) from = EnumFacing.DOWN;
		if (travelDistance >= 10 && !setDirection)
		{
			setDirectionValid(pipe);
			setDirection = true;
		}
		if (!pipe.canOutput(to))
		{
			if (travelDistance <= 10) setDirectionValid(pipe); //revalidate
			else if (pipe.isConnected(to)) //reverse
			{
				travelDistance = 20 - travelDistance;
				setDirection = false;
			}
			else drop(pipe);
		}
		else if (travelDistance > 20)
		{
			EnumFacing from = this.to;
			EnumFacing to = from.getOpposite();
			TileEntity tile = pipe.world().getTileEntity(pipe.pos().offset(from));
			if (tile != null) this.output(pipe, tile, from, to);
			if (stack != null && stack.getCount() > 0)
			{
				travelDistance = 0;
				this.from = from;
				setDirectionValid(pipe);
			}
		}
	}

	public void dropItem(PartItemPipe pipe)
	{
		if (pipe.world().isRemote) return;
		EnumFacing f = from;
		EnumFacing t = to;
		World world = pipe.world();
		double x = pipe.pos().getX() + .5;
		double y = pipe.pos().getY() + .5;
		double z = pipe.pos().getZ() + .5;
		int dis = 0;
		EnumFacing d;
		if (travelDistance < 10)
		{
			d = f;
			dis = 10 - travelDistance;
		}
		else
		{
			d = t;
			dis = travelDistance - 10;
		}
		x += dis * d.getFrontOffsetX() / 20d;
		y += dis * d.getFrontOffsetY() / 20d;
		z += dis * d.getFrontOffsetZ() / 20d;
		EntityItem item = new EntityItem(world, x, y, z, stack);
		world.spawnEntity(item);
	}

	@SideOnly(Side.CLIENT)
	public float[] getOffsetPos(float partial)
	{
		EnumFacing f = from;
		EnumFacing t = to;
		float dis = 0;
		EnumFacing d;
		if (travelDistance < 10)
		{
			d = f;
			dis = 10 - (travelDistance + partial);
		}
		else
		{
			d = t;
			dis = (travelDistance + partial) - 10;
		}
		float x = .5f + dis * d.getFrontOffsetX() / 20;
		float y = .5f + dis * d.getFrontOffsetY() / 20;
		float z = .5f + dis * d.getFrontOffsetZ() / 20;
		return new float[] { x, y, z };
	}

	public ItemStack getItemStack()
	{
		return stack;
	}
}