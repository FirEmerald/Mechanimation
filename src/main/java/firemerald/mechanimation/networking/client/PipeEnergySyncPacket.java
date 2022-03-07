package firemerald.mechanimation.networking.client;

import codechicken.multipart.PartMap;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import firemerald.api.core.networking.ClientPacket;
import firemerald.mechanimation.client.ClientState;
import firemerald.mechanimation.multipart.pipe.PartEnergyPipe;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PipeEnergySyncPacket extends ClientPacket
{
	public BlockPos pos;
	public int prevStoredEnergy;
	public int storedEnergy;
	public int nextStoredEnergy;
	public int[] transferredEnergy;
	public short pipeFlow;

	public PipeEnergySyncPacket() {}

	public PipeEnergySyncPacket(BlockPos pos, int prevStored, int stored, int nextStored, int[] transferredEnergy, int[] flow)
	{
		this.pos = pos;
		this.prevStoredEnergy = prevStored;
		this.storedEnergy = stored;
		this.nextStoredEnergy = nextStored;
		this.transferredEnergy = new int[6];
		System.arraycopy(transferredEnergy, 0, this.transferredEnergy, 0, 6);
		pipeFlow = 0;
		for (int i = 0; i < 6; i++) pipeFlow |= flow[i] << (i * 2);
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.pos = new BlockPos(ByteBufUtils.readVarInt(buf, 5), ByteBufUtils.readVarInt(buf, 5), ByteBufUtils.readVarInt(buf, 5));
		this.prevStoredEnergy = ByteBufUtils.readVarInt(buf, 5);
		this.storedEnergy = ByteBufUtils.readVarInt(buf, 5);
		this.nextStoredEnergy = ByteBufUtils.readVarInt(buf, 5);
		this.transferredEnergy = new int[6];
		for (int i = 0; i < transferredEnergy.length; i++) transferredEnergy[i] = ByteBufUtils.readVarInt(buf, 5);
		this.pipeFlow = buf.readShort();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeVarInt(buf, pos.getX(), 5);
		ByteBufUtils.writeVarInt(buf, pos.getY(), 5);
		ByteBufUtils.writeVarInt(buf, pos.getZ(), 5);
		ByteBufUtils.writeVarInt(buf, prevStoredEnergy, 5);
		ByteBufUtils.writeVarInt(buf, storedEnergy, 5);
		ByteBufUtils.writeVarInt(buf, nextStoredEnergy, 5);
		for (int i = 0; i < 6; i++) ByteBufUtils.writeVarInt(buf, transferredEnergy[i], 5);
		buf.writeShort(pipeFlow);
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
					if (part instanceof PartEnergyPipe)
					{
						PartEnergyPipe pipe = (PartEnergyPipe) part;
						pipe.prevPower = prevStoredEnergy;
						pipe.curPower = storedEnergy;
						pipe.nextPower = nextStoredEnergy;
						System.arraycopy(transferredEnergy, 0, pipe.transferredEnergy, 0, transferredEnergy.length);
						for (int i = 0; i < 6; i++) pipe.flow[i] = (pipeFlow >>> (i * 2)) & 3;
					}
				}
			}
		});
	}

	public static class Handler extends ClientPacket.Handler<PipeEnergySyncPacket> {}
}
