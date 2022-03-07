package firemerald.mechanimation.client.util;

import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import net.minecraft.client.Minecraft;

public class ClientUtil
{
	public static String getLocale()
	{
		return Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
	}

    public static boolean openWebpage(String uri)
    {
        try
        {
            return openWebpage(URI.create(uri));
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean openWebpage(URL url)
    {
        try
        {
            return openWebpage(url.toURI());
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean openWebpage(URI uri)
    {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE))
        {
            try
            {
                desktop.browse(uri);
                return true;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return false;
            }
        }
        else return false;
    }
}