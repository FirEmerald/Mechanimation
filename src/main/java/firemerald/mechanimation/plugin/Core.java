package firemerald.mechanimation.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;

import firemerald.api.core.plugin.URLArtifactVersion;
import firemerald.mechanimation.Main;
import net.minecraftforge.fml.client.FMLFileResourcePack;
import net.minecraftforge.fml.client.FMLFolderResourcePack;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;

public class Core extends DummyModContainer
{
	public static LoadController loadController;
	public static final ModMetadata METADATA = new ModMetadata();
	public static final List<ArtifactVersion> COMMON_RECOMMENDED = Lists.newArrayList();
	public static final List<ArtifactVersion> CLIENT_RECOMMENDED = Lists.newArrayList();
	static final String ES_MAJOR_VERSION = "7.5";
	static
	{
		METADATA.authorList = new ArrayList<>();
    	METADATA.authorList.add("FirEmerald");
    	METADATA.credits = "FirEmerald";
    	METADATA.description = "Animated machines? Now that's cool!";
    	METADATA.modId = "mechanimation";
    	METADATA.name = "Mechanimation";
    	METADATA.version = Plugin.MECHANIMATION_VERSION;
    	METADATA.logoFile = "assets/mechanimation/textures/logo.png";
    	//METADATA.dependencies.add(new URLArtifactVersion("Code Chicken Lib", "codechickenlib", true, "https://www.curseforge.com/minecraft/mc-mods/codechicken-lib-1-8/files/all?filter-game-version=1738749986%3a628"));
    	METADATA.dependencies.add(new URLArtifactVersion("CraftLoader", "craftloader", true, "https://www.curseforge.com/minecraft/mc-mods/codechicken-lib-1-8/files/all?filter-game-version=1738749986%3a628")); //TODO mod URL
    	//METADATA.dependencies.add(new URLArtifactVersion("Forge Multipart CBE", "forgemultipartcbe", true, "https://www.curseforge.com/minecraft/mc-mods/cb-multipart/files/all?filter-game-version=1738749986%3a628"));
    	METADATA.dependants.add(new DefaultArtifactVersion("jaopca", true)); //must run before JAOPCA
	}
	private static Core instance;

	public static Core getInstance()
	{
		return instance;
	}

    public Core()
    {
        super(METADATA);
        instance = this;
    }

    @Override
    public File getSource()
    {
        return Plugin.instance().getLocation();
    }

    @Override
    public Class<?> getCustomResourcePackClass()
    {
        return getSource().isDirectory() ? FMLFolderResourcePack.class : FMLFileResourcePack.class;
    }

    @Override
    public String getGuiClassName()
    {
    	return "firemerald.mechanimation.client.ModGuiFactory";
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller)
    {
    	loadController = controller;
    	bus.register(new Main());
        return true;
    }

    @Override
    public Object getMod()
    {
        return Main.instance();
    }
}