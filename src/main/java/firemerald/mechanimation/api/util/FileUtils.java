package firemerald.mechanimation.api.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import firemerald.api.data.PartiallyLoadedInputStream;
import firemerald.api.data.ResourceLoader;
import firemerald.mechanimation.api.MechanimationAPI;
import net.minecraft.util.ResourceLocation;

public class FileUtils
{
	public static void closeSafe(Closeable closable)
	{
		if (closable != null) try
		{
			closable.close();
		}
		catch (IOException e)
		{
			MechanimationAPI.LOGGER.warn("Failed to close Closable " + closable.toString());
		}
	}

	/**
	 * reads a text file resource, with BOM support. defaults to UTF-8 if no BOM present.
	 * @param file the resource
	 * @return the text contents of the resource
	 * @throws IOException
	 */
	public static String readText(ResourceLocation file) throws IOException
	{
		InputStream in = null;
		try
		{
			String text = readText(in = ResourceLoader.getResource(file));
			closeSafe(in);
			return text;
		}
		catch (IOException e)
		{
			closeSafe(in);
			throw e;
		}
	}

	/**
	 * reads a localized text file resource, with BOM support. defaults to UTF-8 if no BOM present. Defaults to en_us if possible.
	 * @param domain the resource domain
	 * @param unformmatedResource the unformatted resource path
	 * @param locale the localization code
	 * @return the text contents of the resource
	 * @throws IOException
	 */
	public static String readTextLocalized(String domain, String unformmatedResource, String locale) throws IOException
	{
		InputStream in = null;
		try
		{
			in = ResourceLoader.getResource(new ResourceLocation(domain, String.format(unformmatedResource, locale)));
		}
		catch (IOException e)
		{
			MechanimationAPI.LOGGER.warn("Failed to read localized text file " + domain + ":" + unformmatedResource + " from locale " + locale, e);
			e.printStackTrace();
			try
			{
				in = ResourceLoader.getResource(new ResourceLocation(domain, String.format(unformmatedResource, "en_us")));
			}
			catch (IOException e2)
			{
				throw new IOException("Failed to read localized text file " + domain + ":" + unformmatedResource + " from locale en_us", e2);
			}
		}
		try
		{
			String text = readText(in);
			closeSafe(in);
			return text;
		}
		catch (IOException e)
		{
			closeSafe(in);
			throw e;
		}
	}

	/**
	 * reads a localized text file resource, with BOM support. defaults to UTF-8 if no BOM present. Defaults to en_us if possible.
	 * @param unformmatedResource the unformatted resource path
	 * @param locale the localization code
	 * @return the text contents of the resource
	 * @throws IOException
	 */
	public static String readTextLocalized(String unformmatedResource, String locale) throws IOException
	{
		InputStream in = null;
		try
		{
			in = ResourceLoader.getResource(new ResourceLocation(String.format(unformmatedResource, locale)));
		}
		catch (IOException e)
		{
			MechanimationAPI.LOGGER.warn("Failed to read localized text file " + unformmatedResource + " from locale " + locale, e);
			e.printStackTrace();
			try
			{
				in = ResourceLoader.getResource(new ResourceLocation(String.format(unformmatedResource, "en_us")));
			}
			catch (IOException e2)
			{
				throw new IOException("Failed to read localized text file " + unformmatedResource + " from locale en_us", e2);
			}
		}
		try
		{
			String text = readText(in);
			closeSafe(in);
			return text;
		}
		catch (IOException e)
		{
			closeSafe(in);
			throw e;
		}
	}

	/**
	 * reads a localized text file resource, with BOM support. defaults to UTF-8 if no BOM present. Defaults to en_us if possible.
	 * @param unformmatedResource the unformatted resource
	 * @param locale the localization code
	 * @return the text contents of the resource
	 * @throws IOException
	 */
	public static String readTextLocalized(ResourceLocation unformmatedResource, String locale) throws IOException
	{
		return readTextLocalized(unformmatedResource.getResourceDomain(), unformmatedResource.getResourcePath(), locale);
	}

	/**
	 * reads a text file resource, even on servers or in world folders, with BOM support. defaults to UTF-8 if no BOM present.
	 * @param file the resource
	 * @return the text contents of the resource
	 * @throws IOException
	 */
	public static String readTextServer(ResourceLocation file, String subfolder) throws IOException
	{
		InputStream in = null;
		try
		{
			String text = readText(in = ResourceLoader.getResource(file, subfolder));
			closeSafe(in);
			return text;
		}
		catch (IOException e)
		{
			closeSafe(in);
			throw e;
		}
	}

	/**
	 * reads a text file resource, even on servers or in world folders, with BOM support. defaults to UTF-8 if no BOM present.
	 * @param file the resource
	 * @return the text contents of the resource
	 * @throws IOException
	 */
	public static String readTextServer(ResourceLocation file) throws IOException
	{
		InputStream in = null;
		try
		{
			String text = readText(in = ResourceLoader.getResource(file));
			closeSafe(in);
			return text;
		}
		catch (IOException e)
		{
			closeSafe(in);
			throw e;
		}
	}

	/**
	 * reads a text file resource, even on servers (but not in world folders), with BOM support. defaults to UTF-8 if no BOM present.
	 * @param file the resource
	 * @return the text contents of the resource
	 * @throws IOException
	 */
	public static String readTextServerWithoutWorld(ResourceLocation file) throws IOException
	{
		InputStream in = null;
		try
		{
			String text = readText(in = ResourceLoader.getResourceWithoutWorld(file));
			closeSafe(in);
			return text;
		}
		catch (IOException e)
		{
			closeSafe(in);
			throw e;
		}
	}

	/**
	 * reads an input stream, with BOM support. defaults to UTF-8 if no BOM present.
	 * @param in the input stream
	 * @return the text contents of the input stream
	 * @throws IOException if
	 */
	public static String readText(InputStream in) throws IOException
	{
		final byte BB = (byte) 0xBB, BF = (byte) 0xBF, EF = (byte) 0xEF, FE = (byte) 0xFE, FF = (byte) 0xFF;
		byte[] header = new byte[3];
		int length = in.read(header);
		Charset charset = StandardCharsets.UTF_8;
		int offset = 0;
		if (length == 0)
		{
			closeSafe(in);
			return "";
		}
		else if (length >= 2)
		{
			if (header[0] == FE)
			{
				if (header[1] == FF)
				{
					charset = StandardCharsets.UTF_16BE;
					offset = 2;
				}
			}
			else if (header[0] == FF)
			{
				if (header[1] == FE)
				{
					charset = StandardCharsets.UTF_16LE;
					offset = 2;
				}
			}
			else if (length >= 3 && header[0] == EF && header[1] == BB && header[2] >= BF)
			{
				charset = StandardCharsets.UTF_8;
				offset = 3;
			}
		}
		InputStream buffered = new PartiallyLoadedInputStream(in, header, length);
		buffered.read(new byte[offset]); //remove header
		Reader reader = new InputStreamReader(buffered, charset);
		StringBuilder builder = new StringBuilder();
		char[] data = new char[1024];
		while ((length = reader.read(data)) >= 0) builder.append(data, 0, length);
		closeSafe(reader);
		return builder.toString();
	}

    public static String getExtension(String filename)
    {
    	final char[] seperators = {'\\', '/'};
    	int lastSep = -1;
    	for (char sep : seperators)
    	{
    		int ind = filename.lastIndexOf(sep);
    		if (ind > lastSep) lastSep = ind;
    	}
        int lastDot;
        if (lastSep < 0) lastDot = filename.lastIndexOf('.');
        else
        {
            lastDot = filename.substring(lastSep + 1).lastIndexOf('.');
            if (lastDot >= 0)
            {
                lastDot += lastSep + 1;
            }
        }
        return lastDot >= 0 && lastDot > lastSep ? filename.substring(lastDot + 1) : "";
    }
}