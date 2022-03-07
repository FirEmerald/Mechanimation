package firemerald.mechanimation.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.ResourceLocation;

public class MechanimationAPI
{
	public static final String API_VERSION = "0.0.1";
	public static final Logger LOGGER = LogManager.getLogger("Mechanimation MCMSAPI");

	public static final String MOD_ID = "mechanimation";
	public static final ResourceLocation MECHANIMATION_DAMAGE_SPECIFIC_CAP_NAME = new ResourceLocation(MOD_ID, "damage_specific"); //used for custom setting of capabilities
}