package firemerald.mechanimation.mcms;

import firemerald.api.mcms.model.ObjModel;
import firemerald.api.mcms.util.Loader;
import firemerald.api.mcms.util.Loader.Reference;
import firemerald.mechanimation.api.MechanimationAPI;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;

public enum MCMSModel
{
	//TODO fallback model
	PRESS_BASIC("press/basic/model.obj", MCMSSkeleton.PRESS_BASIC, MCMSEffects.PRESS_BASIC, true, false),
	PRESS_ADVANCED("press/advanced/model.obj", MCMSSkeleton.PRESS_ADVANCED, MCMSEffects.PRESS_ADVANCED, true, false),
	FLUID_REACTOR_ADVANCED("fluid_reactor/advanced/model.obj", MCMSSkeleton.FLUID_REACTOR_ADVANCED, MCMSEffects.FLUID_REACTOR_ADVANCED, true, false),
	ELECTROLYZER_ADVANCED("electrolyzer/advanced/model.obj", MCMSSkeleton.ELECTROLYZER_ADVANCED, MCMSEffects.ELECTROLYZER_ADVANCED, true, false),
	HYDROTREATER_ADVANCED("hydrotreater/advanced/model.obj", MCMSSkeleton.HYDROTREATER_ADVANCED, MCMSEffects.HYDROTREATER_ADVANCED, true, false),
	DESALTER_ADVANCED("desalter/advanced/model.obj", MCMSSkeleton.DESALTER_ADVANCED, MCMSEffects.DESALTER_ADVANCED, true, false),
	CATALYTIC_REFORMER_ADVANCED("catalytic_reformer/advanced/model.obj", MCMSSkeleton.CATALYTIC_REFORMER_ADVANCED, MCMSEffects.CATALYTIC_REFORMER_ADVANCED, true, false),
	DISTILLATION_TOWER_ADVANCED("distillation_tower/advanced/model.obj", MCMSSkeleton.DISTILLATION_TOWER_ADVANCED, MCMSEffects.DISTILLATION_TOWER_ADVANCED, true, false),
	MEROX_TREATER_ADVANCED("merox_treater/advanced/model.obj", MCMSSkeleton.MEROX_TREATER_ADVANCED, MCMSEffects.MEROX_TREATER_ADVANCED, true, false),
	CLAUS_PLANT_ADVANCED("claus_plant/advanced/model.obj", MCMSSkeleton.CLAUS_PLANT_ADVANCED, MCMSEffects.CLAUS_PLANT_ADVANCED, true, false),
	STIRLING_GENERATOR("generator/stirling/model.obj", MCMSSkeleton.STIRLING_GENERATOR, MCMSEffects.STIRLING_GENERATOR, true, false),
	COMBUSTION_GENERATOR("generator/combustion/model.obj", MCMSSkeleton.COMBUSTION_GENERATOR, MCMSEffects.COMBUSTION_GENERATOR, true, false),
	PULVERIZER_BASIC("pulverizer/basic/model.obj", MCMSSkeleton.PULVERIZER_BASIC, MCMSEffects.PULVERIZER_BASIC, true, false),
	BLAST_FURNACE("blast_furnace/model.obj", MCMSSkeleton.BLAST_FURNACE, MCMSEffects.BLAST_FURNACE, true, false),
	ARC_FURNACE_ADVANCED("arc_furnace/advanced/model.obj", MCMSSkeleton.ARC_FURNACE_ADVANCED, MCMSEffects.ARC_FURNACE_ADVANCED, true, false),
	CASTING_TABLE_BASIC_INGOT("casting_table/basic/ingot.obj", MCMSSkeleton.CASTING_TABLE_BASIC_INGOT, MCMSEffects.CASTING_TABLE_BASIC_INGOT, true, false),
	ASSEMBLY_TABLE_BASIC("assembly_table/basic/model.obj", MCMSSkeleton.ASSEMBLY_TABLE_BASIC, MCMSEffects.ASSEMBLY_TABLE_BASIC, true, false);

	public final ResourceLocation modelLoc;
	public final MCMSSkeleton skeleton;
	public final MCMSEffects effects;
	public final boolean isRenderOnly;
	public final boolean isPhysicsOnly;
	public final Reference<? extends ObjModel<?, ?>> model;

	MCMSModel(String model, MCMSSkeleton skeleton, MCMSEffects effects, boolean isRenderOnly, boolean isPhysicsOnly)
	{
		this.modelLoc = new ResourceLocation(MechanimationAPI.MOD_ID, model);
		this.skeleton = skeleton;
		this.effects = effects;
		this.isRenderOnly = isRenderOnly;
		this.isPhysicsOnly = isPhysicsOnly;
		if (!isRenderOnly || FMLCommonHandler.instance().getSide().isClient()) this.model = isPhysicsOnly || FMLCommonHandler.instance().getSide().isServer() ? Loader.INSTANCE.getServerObjModel(skeleton.skeletonLoc, modelLoc) : Loader.INSTANCE.getClientObjModel(skeleton.skeletonLoc, modelLoc, effects == null ? null : this.effects.effectsLoc);
		else this.model = new Reference<>(null);
	}
}