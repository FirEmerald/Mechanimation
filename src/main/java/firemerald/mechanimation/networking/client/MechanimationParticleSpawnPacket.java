package firemerald.mechanimation.networking.client;

import firemerald.api.core.networking.ClientPacket;
import firemerald.mechanimation.client.ClientState;
import firemerald.mechanimation.init.MechanimationParticles;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MechanimationParticleSpawnPacket extends ClientPacket
{
	private int id;
	private double x, y, z, mX, mY, mZ;
	private boolean ignoreRange, minParticles;
	private int[] args = new int[0];

	public MechanimationParticleSpawnPacket() {}

	public MechanimationParticleSpawnPacket(int id, double x, double y, double z, double mX, double mY, double mZ, boolean ignoreRange, boolean minParticles, int... args)
	{
		this.id = id;
		this.x = x;
		this.y = y;
		this.z = z;
		this.mX = mX;
		this.mY = mY;
		this.mZ = mZ;
		this.ignoreRange = ignoreRange;
		this.minParticles = minParticles;
		this.args = args;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		id = ByteBufUtils.readVarInt(buf, 5);
		x = buf.readDouble();
		y = buf.readDouble();
		z = buf.readDouble();
		mX = buf.readDouble();
		mY = buf.readDouble();
		mZ = buf.readDouble();
		byte meta = buf.readByte();
		ignoreRange = (meta & 1) == 1;
		minParticles = (meta & 2) == 2;
		int numArgs = ByteBufUtils.readVarInt(buf, 5);
		args = new int[numArgs];
		for (int i = 0; i < numArgs; i++) args[i] = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeVarInt(buf, id, 5);
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
		buf.writeDouble(mX);
		buf.writeDouble(mY);
		buf.writeDouble(mZ);
		buf.writeByte((ignoreRange ? 1 : 0) | (minParticles ? 2 : 0));
		ByteBufUtils.writeVarInt(buf, args.length, 5);
		for (int arg : args)
			buf.writeInt(arg);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleClientSide()
	{
		ClientState.QUEUED_ACTIONS.add(() -> {
			MechanimationParticles.spawnParticle(id, ignoreRange, minParticles, x, y, z, mX, mY, mZ, args);
		});
	}

	public static class Handler extends ClientPacket.Handler<MechanimationParticleSpawnPacket> {}
}
