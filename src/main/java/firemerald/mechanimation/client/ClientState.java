package firemerald.mechanimation.client;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientState
{
	public static final Queue<Runnable> QUEUED_ACTIONS = new ConcurrentLinkedQueue<>();
	public static int clientTicks = 0;
	public static double prevAmbientTemp = Double.MIN_VALUE;
	public static double ambientTemp = Double.MIN_VALUE;
	public static float partialTick = 0;

	public static void setAmbientTemperature(double temp)
	{
		//System.out.println(temp);
		prevAmbientTemp = (ambientTemp != Double.MIN_VALUE) ? ambientTemp : temp;
		ambientTemp = temp;
	}

	public static float getAmbientTemperature(float partial)
	{
		return (float) (prevAmbientTemp + (ambientTemp - prevAmbientTemp) * partial);
	}
}