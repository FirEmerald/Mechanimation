package firemerald.mechanimation.networking.client;

import codechicken.multipart.PartMap;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import firemerald.api.core.networking.ClientPacket;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.client.ClientState;
import firemerald.mechanimation.multipart.pipe.PartFluidPipe;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PipeFluidSyncPacket extends ClientPacket
{
	public BlockPos pos;
	public int prevFluid;
	public FluidOrGasStack storedFluid;
	public int nextFluid;
	public int[] transferredFluid;
	public short pipeFlow;

	public PipeFluidSyncPacket() {}

	public PipeFluidSyncPacket(BlockPos pos, int prevFluid, FluidOrGasStack stored, int nextFluid, int[] transferredFluid, int[] flow)
	{
		this.pos = pos;
		this.prevFluid = prevFluid;
		this.storedFluid = stored;
		this.nextFluid = nextFluid;
		this.transferredFluid = new int[6];
		System.arraycopy(transferredFluid, 0, this.transferredFluid, 0, 6);
		pipeFlow = 0;
		for (int i = 0; i < 6; i++) pipeFlow |= flow[i] << (i * 2);
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.pos = new BlockPos(ByteBufUtils.readVarInt(buf, 5), ByteBufUtils.readVarInt(buf, 5), ByteBufUtils.readVarInt(buf, 5));
		this.prevFluid = ByteBufUtils.readVarInt(buf, 5);
		this.storedFluid = FluidOrGasStack.readFromNBT(ByteBufUtils.readTag(buf));
		this.nextFluid = ByteBufUtils.readVarInt(buf, 5);
		this.transferredFluid = new int[6];
		for (int i = 0; i < 6; i++) transferredFluid[i] = ByteBufUtils.readVarInt(buf, 5);
		this.pipeFlow = buf.readShort();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeVarInt(buf, pos.getX(), 5);
		ByteBufUtils.writeVarInt(buf, pos.getY(), 5);
		ByteBufUtils.writeVarInt(buf, pos.getZ(), 5);
		ByteBufUtils.writeVarInt(buf, prevFluid, 5);
		ByteBufUtils.writeTag(buf, storedFluid != null ? storedFluid.writeToNBT(new NBTTagCompound()) : new NBTTagCompound());
		ByteBufUtils.writeVarInt(buf, nextFluid, 5);
		for (int i = 0; i < 6; i++) ByteBufUtils.writeVarInt(buf, transferredFluid[i], 5);
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
					if (part instanceof PartFluidPipe)
					{
						PartFluidPipe pipe = (PartFluidPipe) part;
						pipe.prevFluid = prevFluid;
						pipe.stored = storedFluid;
						pipe.nextFluid = nextFluid;
						System.arraycopy(this.transferredFluid, 0, pipe.transferredFluid, 0, transferredFluid.length);
						for (int i = 0; i < 6; i++) pipe.flow[i] = (pipeFlow >>> (i * 2)) & 3;
					}
				}
			}
		});
	}

	public static class Handler extends ClientPacket.Handler<PipeFluidSyncPacket> {}
}
