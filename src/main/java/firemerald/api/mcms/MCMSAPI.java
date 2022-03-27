package firemerald.api.mcms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.eventbus.EventBus;

import firemerald.api.mcms.client.ReloadListener;
import firemerald.api.mcms.util.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MCMSAPI
{
	public static final Logger LOGGER = LogManager.getLogger("MCMS");
	public static final String JOML_VERSION = "1.10.4";
    public static final String JOML_URL = "https://repo1.maven.org/maven2/org/joml/joml/" + JOML_VERSION + "/joml-" + JOML_VERSION + ".jar";
    public static final byte[] JOML_MD5 = fromHex("2C810112C86EDD972853EB37AED752D0");
    private static boolean init = false;
    public static final String API_VERSION = "0.2.0";
    public static boolean isViveCraft = false;


    /**
     * run this during mod construction (usually inside a static constructor in your mod class). This will ensure that the JOML library is downloaded and added to the classpath.
     */
    public static void init()
    {
    	if (!init)
    	{
    		init = true;
            File mcDir = (File)(FMLInjectionData.data()[6]);
    		File file = new File(mcDir, "mods/mcms/joml-" + JOML_VERSION + ".jar");
    		boolean download = true;
    		byte[] bytes = new byte[4096];
    		if (file.exists())
    		{
    	    	InputStream in = null;
    	    	try
    	    	{
    	    		in = new FileInputStream(file);
    				MessageDigest digest = MessageDigest.getInstance("MD5");
    	    		int read;
    	    		while ((read = in.read(bytes)) > 0) digest.update(bytes, 0, read);
    	    		byte[] got = digest.digest();
    	    		if (got.length != JOML_MD5.length)
    	    		{
    	    			MCMSAPI.LOGGER.warn("Invalid MD5 for JOML library, was " + convertByteArrayToHexString(got) + ", should be " + convertByteArrayToHexString(JOML_MD5));
    	    		}
    	    		else
    	    		{
    	    			download = false;
    	    			for (int i = 0; i < got.length; i++) if (got[i] != JOML_MD5[i])
    	    			{
    	    				download = true;
    	    				MCMSAPI.LOGGER.warn("Invalid MD5 for JOML library, was " + convertByteArrayToHexString(got) + ", should be " + convertByteArrayToHexString(JOML_MD5));
    		    			break;
    	    			}
    	    			if (!download) MCMSAPI.LOGGER.info("JOML MD5 passed");
    	    		}
    	    	}
    	    	catch (IOException | NoSuchAlgorithmException e)
    	    	{
    	    		MCMSAPI.LOGGER.warn("Couldn't retrieve JOML MD5", e);
    	    	}
    	    	if (in != null) try
    	    	{
    	    		in.close();
    	    	} catch (IOException e) {}
    		}
    		if (download)
    		{
    			MCMSAPI.LOGGER.info("Downloading JOML library");
    	    	InputStream in = null;
    	    	OutputStream out = null;
    	    	try
    	    	{
    	    		URL jomlURL = new URL(JOML_URL);
    	    		file.getParentFile().mkdirs();
    	    		file.createNewFile();
    	    		in = jomlURL.openStream();
    	    		out = new FileOutputStream(file);
    	    		int read;
    	    		int downloaded = 0;
    	    		while ((read = in.read(bytes)) > 0)
    	    		{
    	    			downloaded += read;
    	    			out.write(bytes, 0, read);
    	    			MCMSAPI.LOGGER.debug(downloaded + " bytes");
    	    		}
    	    	}
    	    	catch (Exception e)
    	    	{
    	    		MCMSAPI.LOGGER.warn("Couldn't download JOML library", e);
    	    	}
    	    	if (in != null) try
    	    	{
    	    		in.close();
    	    	} catch (IOException e) {}
    	    	if (out != null) try
    	    	{
    	    		out.close();
    	    	} catch (IOException e) {}
    		}
    		if (file.exists())
    		{
    			try
    			{
    				Loader.instance().getModClassLoader().addFile(file);
    				MCMSAPI.LOGGER.info("Added JOML library from " + file.getAbsolutePath());
    			}
    			catch (MalformedURLException e)
    			{
    				MCMSAPI.LOGGER.warn("Could not add JOML library", e);
    			}
    		}
    		if (FMLCommonHandler.instance().getSide().isClient()) doClientStuff();
    		try //register an FML event handler for ServerStartingEvent to reload models
    		{
    			Field modController = getPrivateField(Loader.class, "modController");
    			Field controller = getPrivateField(LoadController.class, "masterChannel");
    			EventBus bus = (EventBus) controller.get(modController.get(Loader.instance()));
    			bus.register(EventListener.INSTANCE);
    		}
    		catch (Throwable e)
    		{
    			e.printStackTrace();
    			System.exit(0);
    		}
    		MinecraftForge.EVENT_BUS.register(EventListener.INSTANCE);
    	}
    }

    @SideOnly(Side.CLIENT)
    private static void doClientStuff()
    {
		try
		{
			if (Minecraft.class.getField("vrPlayer") != null)
			{
				isViveCraft = true;
			}
		}
		catch (Throwable e)
		{
			isViveCraft = false;
		}
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(new ReloadListener());
    }

    private static Field getPrivateField(Class<?> clazz, String name)
    {
    	for (Field f : clazz.getDeclaredFields())
    	{
    		if (f.getName().equals(name))
    		{
    			f.setAccessible(true);
    			return f;
    		}
    	}
		return null;
    }

	private static String convertByteArrayToHexString(byte[] arrayBytes)
	{
		StringBuffer stringBuffer = new StringBuffer();
		for (byte b : arrayBytes) stringBuffer.append(String.format("%02x", b & 255));
		return stringBuffer.toString();
	}

	protected static byte[] fromHex(String string)
	{
		byte[] vals = new byte[(string.length() + 1) / 2];
		for (int i = 0; i < vals.length; i++) vals[i] = (byte) Integer.parseInt(string.substring(i * 2, Math.min(i * 2 + 2, string.length())), 16);
		return vals;
	}
}