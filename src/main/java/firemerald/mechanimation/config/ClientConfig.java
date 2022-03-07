package firemerald.mechanimation.config;

import firemerald.api.config.Category;
import firemerald.api.config.Config;
import firemerald.api.config.ConfigValueBoolean;
import firemerald.mechanimation.api.MechanimationAPI;
import net.minecraft.util.ResourceLocation;

public class ClientConfig extends Config
{
	public static final ClientConfig INSTANCE = new ClientConfig();

	public final ConfigValueBoolean showItems;

	public ClientConfig()
	{
		super(new ResourceLocation(MechanimationAPI.MOD_ID, "client.cfg"));
		Category render = new Category(this, "render", "Render options");
		showItems = new ConfigValueBoolean(render, "pipe_items", true, "Render in-pipe items. Also controls the synchronization of items in pipes between client and server.");
	}
}