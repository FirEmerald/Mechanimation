package firemerald.mechanimation.networking.client;

import firemerald.api.core.networking.ClientPacket;
import firemerald.mechanimation.config.CommonConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ServerSettingsSyncPacket extends ClientPacket
{
	public NBTTagCompound config;

	public ServerSettingsSyncPacket() {}

	public ServerSettingsSyncPacket(CommonConfig config)
	{
		this.config = config.saveNBT();
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		config = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeTag(buf, config);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleClientSide()
	{
		CommonConfig.INSTANCE.loadNBT(config);
	}

	public static class Handler extends ClientPacket.Handler<ServerSettingsSyncPacket> {}
}
