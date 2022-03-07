package firemerald.mechanimation.compat.jei;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Preconditions;

import firemerald.api.core.IFMLEventHandler;
import firemerald.mechanimation.compat.jei.transfer.PacketAssemblyTerminalRecipeTransfer;
import mezz.jei.JustEnoughItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.config.Config;
import mezz.jei.gui.textures.Textures;
import mezz.jei.startup.JeiStarter;
import mezz.jei.startup.ProxyCommonClient;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class JEICompat implements IFMLEventHandler
{
	public static final JEICompat INSTANCE = new JEICompat();
	public static final Logger LOGGER = LogManager.getLogger("Mechanimation JEI Compat");
	private static final Field STARTER_FIELD, TEXTURES_FIELD, PLUGINS_FIELD;

	public SimpleNetworkWrapper network;

	private static Field getDeclaredPrivateField(Class<?> clazz, String fieldName)
	{
		for (Field f : clazz.getDeclaredFields()) if (f.getName().equals(fieldName))
		{
			f.setAccessible(true);
			return f;
		}
		return null;
	}

	static
	{
		Field starterField = null, texturesField = null, pluginsField = null;
		if (FMLCommonHandler.instance().getSide().isClient())
		{
			try
			{
				starterField = getDeclaredPrivateField(ProxyCommonClient.class, "starter");
			}
			catch (Throwable e)
			{
				LOGGER.warn("Could not fetch \"starter\" field of ProxyCommonClient.class", e);
			}
			try
			{
				texturesField = getDeclaredPrivateField(ProxyCommonClient.class, "textures");
			}
			catch (Throwable e)
			{
				LOGGER.warn("Could not fetch \"textures\" field of ProxyCommonClient.class", e);
			}
			try
			{
				pluginsField = getDeclaredPrivateField(ProxyCommonClient.class, "plugins");
			}
			catch (Throwable e)
			{
				LOGGER.warn("Could not fetch \"plugins\" field of ProxyCommonClient.class", e);
			}
		}
		STARTER_FIELD = starterField;
		TEXTURES_FIELD = texturesField;
		PLUGINS_FIELD = pluginsField;
	}

	@Override
    public void onPreInitialization(FMLPreInitializationEvent event)
    {
		ConfigJEI.INSTANCE.loadConfig();
    	network = NetworkRegistry.INSTANCE.newSimpleChannel("mechanimationjei");
		network.registerMessage(PacketAssemblyTerminalRecipeTransfer.Handler.class, PacketAssemblyTerminalRecipeTransfer.class, 0, Side.SERVER);
    }

	@Override
	public void onInitialization(FMLInitializationEvent event)
	{
		if (PLUGINS_FIELD != null)
		{
			ProxyCommonClient proxy = (ProxyCommonClient) JustEnoughItems.getProxy();
			try
			{
				@SuppressWarnings("unchecked")
				List<IModPlugin> plugins = (List<IModPlugin>) PLUGINS_FIELD.get(proxy);
				plugins.add(new JEICompatPlugin());
			}
			catch (Throwable e)
			{
				LOGGER.warn("Could not force add JEI plugin", e);
			}
		}
	}

	@Override
    public void onServerStarted(FMLServerStartedEvent event)
	{
		if (STARTER_FIELD != null && TEXTURES_FIELD != null && PLUGINS_FIELD != null)
		{
			ProxyCommonClient proxy = (ProxyCommonClient) JustEnoughItems.getProxy();
			JeiStarter starter;
			try
			{
				starter = (JeiStarter) STARTER_FIELD.get(proxy);
				// check that JEI has been started before. if not, do nothing
				if (starter.hasStarted()) {
					if (Config.isDebugModeEnabled()) {
						LOGGER.info("Restarting JEI.", new RuntimeException("Stack trace for debugging"));
					} else {
						LOGGER.info("Restarting JEI.");
					}
					Textures textures = (Textures) TEXTURES_FIELD.get(proxy);
					@SuppressWarnings("unchecked")
					List<IModPlugin> plugins = (List<IModPlugin>) PLUGINS_FIELD.get(proxy);
					Preconditions.checkNotNull(textures);
					Minecraft.getMinecraft().addScheduledTask(() -> starter.start(plugins, textures));
				}
			}
			catch (Throwable e)
			{
				LOGGER.warn("Could not force restart JEI", e);
			}
		}
	}
}