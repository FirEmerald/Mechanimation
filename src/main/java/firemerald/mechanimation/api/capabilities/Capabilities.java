package firemerald.mechanimation.api.capabilities;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import firemerald.api.core.capabilities.NullStorage;
import mekanism.api.gas.IGasHandler;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class Capabilities
{
	public static Capability<IGasHandler> gasHandler = null;

	public static boolean registered = false;

	public static void register() //run during init unless you only deal with these when Mechanimation is installed
	{
		if (!registered)
		{
			try //Because Forge's capability manager is literally broken. Honestly, why doesn't it return the instance and instead rely on an apparently unstable annotation system?
			{
				CapabilityManager manager = CapabilityManager.INSTANCE;
				Field f = CapabilityManager.class.getDeclaredField("providers");
				f.setAccessible(true);
				@SuppressWarnings("unchecked")
				IdentityHashMap<String, Capability<?>> map = (IdentityHashMap<String, Capability<?>>) f.get(manager);
				gasHandler = getOrRegister(IGasHandler.class, new NullStorage<IGasHandler>(), () -> null, manager, map);
			}
			catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
			{
				e.printStackTrace();
				FMLCommonHandler.instance().exitJava(0, false);
			}
			registered = true;
			boolean crash = false;
			if (gasHandler == null)
			{
				new Exception("gasHandler capability not set").printStackTrace();
				crash = true;
			}
			if (crash) FMLCommonHandler.instance().exitJava(0, false);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> Capability<T> register(Class<T> clazz, IStorage<T> storage, Callable<T> factory, CapabilityManager manager, Map<String, Capability<?>> map)
	{
		CapabilityManager.INSTANCE.register(clazz, storage, factory);
		return (Capability<T>) map.get(clazz.getName().intern());
	}

	public static <T> Capability<T> getOrRegister(Class<T> clazz, IStorage<T> storage, Callable<T> factory, CapabilityManager manager, Map<String, Capability<?>> map)
	{
		@SuppressWarnings("unchecked")
		Capability<T> cap = (Capability<T>) map.get(clazz.getName().intern());
		return cap == null ? register(clazz, storage, factory, manager, map) : cap;
	}
}