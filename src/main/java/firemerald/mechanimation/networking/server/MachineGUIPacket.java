package firemerald.mechanimation.networking.server;

import firemerald.api.core.networking.ServerPacket;
import firemerald.mechanimation.common.CommonState;
import firemerald.mechanimation.inventory.container.ContainerMachine;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MachineGUIPacket extends ServerPacket
{
	private byte id;
	private ByteBuf tilePacket;

	public MachineGUIPacket() {}

	@SideOnly(Side.CLIENT)
	public MachineGUIPacket(byte id, ByteBuf packet)
	{
		this.id = id;
		this.tilePacket = packet.copy();
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		id = buf.readByte();
		tilePacket = buf.copy();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeByte(id);
		buf.writeBytes(tilePacket.copy());
	}

	@Override
	public void handleServerSide(EntityPlayerMP player)
	{
		CommonState.QUEUED_ACTIONS.add(() -> {
			if (player.openContainer instanceof ContainerMachine) ((ContainerMachine<?>) player.openContainer).machine.onGuiPacket(id, tilePacket.copy());
		});
	}

	public static class Handler extends ServerPacket.Handler<MachineGUIPacket> {}
}
