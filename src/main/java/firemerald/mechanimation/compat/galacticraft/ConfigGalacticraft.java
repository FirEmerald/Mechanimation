package firemerald.mechanimation.compat.galacticraft;

import firemerald.api.config.Category;
import firemerald.api.config.Config;
import firemerald.api.config.ConfigValueBoolean;
import firemerald.api.config.ConfigValueInt;
import firemerald.mechanimation.api.MechanimationAPI;
import net.minecraft.util.ResourceLocation;

public class ConfigGalacticraft extends Config
{
	public static final ConfigGalacticraft INSTANCE = new ConfigGalacticraft();

	public final ConfigValueBoolean tier1RocketAssembly;
	public final ConfigValueInt tier1RocketAssemblyFlux;
	public final ConfigValueBoolean moonBuggyAssembly;
	public final ConfigValueInt moonBuggyAssemblyFlux;
	public final ConfigValueBoolean tier2RocketAssembly;
	public final ConfigValueInt tier2RocketAssemblyFlux;
	public final ConfigValueBoolean cargoRocketAssembly;
	public final ConfigValueInt cargoRocketAssemblyFlux;
	public final ConfigValueBoolean tier3RocketAssembly;
	public final ConfigValueInt tier3RocketAssemblyFlux;
	public final ConfigValueBoolean astroMinerAssembly;
	public final ConfigValueInt astroMinerAssemblyFlux;
	public final ConfigValueBoolean disableNasaWorkbench;

	public ConfigGalacticraft()
	{
		super(new ResourceLocation(MechanimationAPI.MOD_ID, "compat/galacticraft.cfg"));
		Category crafting = new Category(this, "crafting", "Crafting options");
		tier1RocketAssembly = new ConfigValueBoolean(crafting, "tier_1_rocket_assembly", true, "Allow NASA workbech recipes for tier 1 rockets");
		tier1RocketAssemblyFlux = new ConfigValueInt(crafting, "tier_1_rocket_assembly_flux", 10000, 0, Integer.MAX_VALUE, "RF required to craft a tier 1 rocket");
		moonBuggyAssembly = new ConfigValueBoolean(crafting, "moon_buggy_assembly", true, "Allow NASA workbech recipes for moon buggies");
		moonBuggyAssemblyFlux = new ConfigValueInt(crafting, "moon_buggy_assembly_flux", 10000, 0, Integer.MAX_VALUE, "RF required to craft a moon buggy");
		tier2RocketAssembly = new ConfigValueBoolean(crafting, "tier_2_rocket_assembly", true, "Allow NASA workbech recipes for tier 2 rockets");
		tier2RocketAssemblyFlux = new ConfigValueInt(crafting, "tier_2_rocket_assembly_flux", 20000, 0, Integer.MAX_VALUE, "RF required to craft a tier 2 rocket");
		cargoRocketAssembly = new ConfigValueBoolean(crafting, "cargo_rocket_assembly", true, "Allow NASA workbech recipes for cargo rockets");
		cargoRocketAssemblyFlux = new ConfigValueInt(crafting, "cargo_rocket_assembly_flux", 20000, 0, Integer.MAX_VALUE, "RF required to craft a cargo rocket");
		tier3RocketAssembly = new ConfigValueBoolean(crafting, "tier_3_rocket_assembly", true, "Allow NASA workbech recipes for tier 3 rockets");
		tier3RocketAssemblyFlux = new ConfigValueInt(crafting, "tier_3_rocket_assembly_flux", 40000, 0, Integer.MAX_VALUE, "RF required to craft a tier 3 rocket");
		astroMinerAssembly = new ConfigValueBoolean(crafting, "astro_miner_assembly", true, "Allow NASA workbech recipes for astro miners");
		astroMinerAssemblyFlux = new ConfigValueInt(crafting, "astro_miner_assembly_flux", 40000, 0, Integer.MAX_VALUE, "RF required to craft an astro miner");
		disableNasaWorkbench = new ConfigValueBoolean(crafting, "disable_nasa_workbench", false, "Disable the crafting of the NASA workbench, effectively forcing players to use a assembly terminal instead.");
	}
}