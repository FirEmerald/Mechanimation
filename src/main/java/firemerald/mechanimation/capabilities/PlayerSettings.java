package firemerald.mechanimation.capabilities;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;

import firemerald.api.core.capabilities.NullStorage;
import firemerald.mechanimation.api.capabilities.Capabilities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class PlayerSettings implements ICapabilityProvider
{
	public static Capability<PlayerSettings> playerSettings = null;
	public static boolean registered = false;

	public static void register()
	{
		if (!registered)
		{
			try
			{
				CapabilityManager manager = CapabilityManager.INSTANCE;
				Field f = CapabilityManager.class.getDeclaredField("providers");
				f.setAccessible(true);
				@SuppressWarnings("unchecked")
				IdentityHashMap<String, Capability<?>> map = (IdentityHashMap<String, Capability<?>>) f.get(manager);
				playerSettings = Capabilities.register(PlayerSettings.class, new NullStorage<PlayerSettings>(), () -> null, manager, map);
			}
			catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
			{
				e.printStackTrace();
				FMLCommonHandler.instance().exitJava(0, false);
			}
			registered = true;
			boolean crash = false;
			if (playerSettings == null)
			{
				new Exception("playerSettings capability not set").printStackTrace();
				crash = true;
			}
			if (crash) FMLCommonHandler.instance().exitJava(0, false);
		}
	}

	public final EntityPlayer player;
	public boolean viewItemsInPipes = true;

	public PlayerSettings(EntityPlayer player)
	{
		this.player = player;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return capability == playerSettings;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		return capability == playerSettings ? (T) this : null;
	}
}