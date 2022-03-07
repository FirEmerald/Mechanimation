package firemerald.mechanimation.mcms;

import firemerald.api.mcms.model.effects.EffectsData;
import firemerald.api.mcms.util.Loader;
import firemerald.api.mcms.util.Loader.Reference;
import firemerald.mechanimation.api.MechanimationAPI;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum MCMSEffects
{
	PRESS_BASIC("press/basic/effects.bfx"),
	PRESS_ADVANCED("press/advanced/effects.bfx"),
	FLUID_REACTOR_ADVANCED("fluid_reactor/advanced/effects.bfx"),
	ELECTROLYZER_ADVANCED("electrolyzer/advanced/effects.bfx"),
	HYDROTREATER_ADVANCED("hydrotreater/advanced/effects.bfx"),
	DESALTER_ADVANCED("desalter/advanced/effects.bfx"),
	CATALYTIC_REFORMER_ADVANCED("catalytic_reformer/advanced/effects.bfx"),
	DISTILLATION_TOWER_ADVANCED("distillation_tower/advanced/effects.bfx"),
	MEROX_TREATER_ADVANCED("merox_treater/advanced/effects.bfx"),
	CLAUS_PLANT_ADVANCED("claus_plant/advanced/effects.bfx"),
	STIRLING_GENERATOR("generator/stirling/effects.bfx"),
	COMBUSTION_GENERATOR("generator/combustion/effects.bfx"),
	PULVERIZER_BASIC("pulverizer/basic/effects.bfx"),
	BLAST_FURNACE("blast_furnace/effects.bfx"),
	ARC_FURNACE_ADVANCED("arc_furnace/advanced/effects.bfx"),
	CASTING_TABLE_BASIC_INGOT("casting_table/basic/ingot.bfx"),
	ASSEMBLY_TABLE_BASIC("assembly_table/basic/effects.bfx");

	public final ResourceLocation effectsLoc;
	@SideOnly(Side.CLIENT)
	public Reference<EffectsData> effects;

	MCMSEffects(String effects)
	{
		this.effectsLoc = new ResourceLocation(MechanimationAPI.MOD_ID, effects);
		if (FMLCommonHandler.instance().getSide().isClient()) this.effects = Loader.INSTANCE.getEffectsData(effectsLoc);
	}
}