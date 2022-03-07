package firemerald.mechanimation.mcms;

import firemerald.mechanimation.api.MechanimationAPI;
import net.minecraft.util.ResourceLocation;

public interface MCMSTexture
{
	public static final ResourceLocation
	PRESS_BASIC = texture("press/basic/texture.png"),
	PRESS_ADVANCED = texture("press/advanced/texture.png"),

	FLUID_REACTOR_ADVANCED = texture("fluid_reactor/advanced/texture.png"),

	ELECTROLYZER_ADVANCED = texture("electrolyzer/advanced/texture.png"),

	HYDROTREATER_ADVANCED = texture("hydrotreater/advanced/texture.png"),

	DESALTER_ADVANCED = texture("desalter/advanced/texture.png"),

	CATALYTIC_REFORMER_ADVANCED = texture("catalytic_reformer/advanced/texture.png"),

	DISTILLATION_TOWER_ADVANCED = texture("distillation_tower/advanced/texture.png"),

	MEROX_TREATER_ADVANCED = texture("merox_treater/advanced/texture.png"),

	CLAUS_PLANT_ADVANCED = texture("claus_plant/advanced/texture.png"),

	STIRLING_GENERATOR = texture("generator/stirling/texture.png"),
	COMBUSTION_GENERATOR = texture("generator/combustion/texture.png"),

	PULVERIZER_BASIC = texture("pulverizer/basic/texture.png"),

	BLAST_FURNACE = texture("blast_furnace/texture.png"),
	ARC_FURNACE_ADVANCED = texture("arc_furnace/advanced/texture.png"),

	CASTING_TABLE_BASIC_INGOT = texture("casting_table/basic/ingot.png"),

	ASSEMBLY_TABLE_BASIC = texture("assembly_table/basic/texture.png");

	public static ResourceLocation texture(String name)
	{
		return new ResourceLocation(MechanimationAPI.MOD_ID, "mcms/" + name);
	}
}