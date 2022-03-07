package firemerald.mechanimation.compat.enderio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import firemerald.api.core.IFMLEventHandler;
import firemerald.api.mcms.util.ResourceLoader;
import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.api.util.FileUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;

public class EnderIOCompat implements IFMLEventHandler
{
	public static final EnderIOCompat INSTANCE = new EnderIOCompat();
	public static final Logger LOGGER = LogManager.getLogger("Mechanimation EnderIO Compat");
	public static final String XML_RECIPE_FILE = "recipe:xml:file";

	@Override
	public void onInitialization(FMLInitializationEvent event)
	{
		File recipes = new File("config/" + MechanimationAPI.MOD_ID + "/compat/ender_io_recipes.xml");
		copyResource(new ResourceLocation(MechanimationAPI.MOD_ID, "config/ender_io_recipes.xml"), recipes);
		FMLInterModComms.sendMessage("enderio", XML_RECIPE_FILE, recipes.toString());
	}

	public boolean copyResource(ResourceLocation resource, File destination)
	{
		InputStream in = null;
		OutputStream out = null;
		boolean ret;
		try
		{
			destination.getParentFile().mkdirs();
			destination.createNewFile();
			in = ResourceLoader.getResource(resource);
			out = new FileOutputStream(destination);
			byte[] data = new byte[4096];
			int read;
			while ((read = in.read(data)) > 0) out.write(data, 0, read);
			ret = true;
		}
		catch (IOException | SecurityException e)
		{
			ret = false;
			LOGGER.warn("Unable to copy resource " + resource + " to file " + destination, e);
		}
		FileUtils.closeSafe(in);
		FileUtils.closeSafe(out);
		return ret;
	}
}