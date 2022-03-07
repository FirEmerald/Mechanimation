package firemerald.mc4.api.capabilities;

import firemerald.api.core.function.FloatConsumer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IThermalRenderer
{
	public static class CapabilityInstance
	{
		public static Capability<IThermalRenderer> capability;
	}

	@SideOnly(Side.CLIENT)
	public void renderThermals(float partial, FloatConsumer setTemp);
}