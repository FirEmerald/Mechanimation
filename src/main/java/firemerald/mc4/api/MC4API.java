package firemerald.mc4.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.ResourceLocation;

public class MC4API
{
	public static final String API_VERSION = "0.0.0";
	public static final Logger LOGGER = LogManager.getLogger("MC4 API");

	public static final String MOD_ID = "mc4";
	public static final ResourceLocation MC4_LOCKABLE_CAP_NAME = new ResourceLocation(MOD_ID, "lockable"); //used for custom setting of capabilities
	public static final ResourceLocation MC4_SCANABLE_CAP_NAME = new ResourceLocation(MOD_ID, "scanable"); //used for custom setting of capabilities
	public static final ResourceLocation MC4_DAMAGE_SPECIFIC_CAP_NAME = new ResourceLocation(MOD_ID, "damage_specific"); //used for custom setting of capabilities
	public static final ResourceLocation MC4_GRAVITY_WELL_CAP_NAME = new ResourceLocation(MOD_ID, "gravity_well"); //used for custom setting of capabilities
	public static final ResourceLocation MC4_LIGHT_AURA_CAP_NAME = new ResourceLocation(MOD_ID, "light_aura"); //used for custom setting of capabilities
	public static final ResourceLocation MC4_WORLD_PROPS_NAME = new ResourceLocation(MOD_ID, "world_props"); //used for custom setting of capabilities
}