package firemerald.mechanimation.compat.forgemultipart.networking;

import codechicken.multipart.PartMap;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import firemerald.api.core.networking.ClientPacket;
import firemerald.mechanimation.client.ClientState;
import firemerald.mechanimation.compat.forgemultipart.pipe.PartItemPipe;
import firemerald.mechanimation.compat.forgemultipart.pipe.TravelingStack;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PipeItemsSyncPacket extends ClientPacket
{
	public BlockPos pos;
	public TravelingStack[] stacks;

	public PipeItemsSyncPacket() {}

	public PipeItemsSyncPacket(PartItemPipe pipe)
	{
		this.pos = pipe.pos();
		this.stacks = pipe.stacks.toArray(new TravelingStack[pipe.stacks.size()]);
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.pos = new BlockPos(ByteBufUtils.readVarInt(buf, 5), ByteBufUtils.readVarInt(buf, 5), ByteBufUtils.readVarInt(buf, 5));
		this.stacks = new TravelingStack[ByteBufUtils.readVarInt(buf, 5)];
		for (int i = 0; i < stacks.length; i++) stacks[i] = new TravelingStack(ByteBufUtils.readTag(buf));
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeVarInt(buf, pos.getX(), 5);
		ByteBufUtils.writeVarInt(buf, pos.getY(), 5);
		ByteBufUtils.writeVarInt(buf, pos.getZ(), 5);
		ByteBufUtils.writeVarInt(buf, stacks.length, 5);
		for (TravelingStack stack : stacks) ByteBufUtils.writeTag(buf, stack.writeData());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleClientSide()
	{
		ClientState.QUEUED_ACTIONS.add(() -> {
			if (Minecraft.getMinecraft().world != null)
			{
				TileEntity tile = Minecraft.getMinecraft().world.getTileEntity(pos);
				if (tile instanceof TileMultipart)
				{
					TMultiPart part = ((TileMultipart) tile).partMap(PartMap.CENTER.i);
					if (part instanceof PartItemPipe)
					{
						PartItemPipe pipe = (PartItemPipe) part;
						pipe.stacks.clear();
						for (TravelingStack stack : stacks) pipe.stacks.add(stack);
					}
				}
			}
		});
	}

	public static class Handler extends ClientPacket.Handler<PipeItemsSyncPacket> {}
}
