package firemerald.mechanimation.mcms;

import firemerald.api.mcms.model.Skeleton;
import firemerald.api.mcms.util.Loader;
import firemerald.api.mcms.util.Loader.Reference;
import firemerald.mechanimation.api.MechanimationAPI;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;

public enum MCMSSkeleton
{
	PRESS_BASIC("press/basic/skeleton.skel", true),
	PRESS_ADVANCED("press/advanced/skeleton.skel", true),
	FLUID_REACTOR_ADVANCED("fluid_reactor/advanced/skeleton.skel", true),
	ELECTROLYZER_ADVANCED("electrolyzer/advanced/skeleton.skel", true),
	HYDROTREATER_ADVANCED("hydrotreater/advanced/skeleton.skel", true),
	DESALTER_ADVANCED("desalter/advanced/skeleton.skel", true),
	CATALYTIC_REFORMER_ADVANCED("catalytic_reformer/advanced/skeleton.skel", true),
	DISTILLATION_TOWER_ADVANCED("distillation_tower/advanced/skeleton.skel", true),
	MEROX_TREATER_ADVANCED("merox_treater/advanced/skeleton.skel", true),
	CLAUS_PLANT_ADVANCED("claus_plant/advanced/skeleton.skel", true),
	STIRLING_GENERATOR("generator/stirling/skeleton.skel", true),
	COMBUSTION_GENERATOR("generator/combustion/skeleton.skel", true),
	PULVERIZER_BASIC("pulverizer/basic/skeleton.skel", true),
	BLAST_FURNACE("blast_furnace/skeleton.skel", true),
	ARC_FURNACE_ADVANCED("arc_furnace/advanced/skeleton.skel", true),
	CASTING_TABLE_BASIC_INGOT("casting_table/basic/ingot.skel", true),
	ASSEMBLY_TABLE_BASIC("assembly_table/basic/skeleton.skel", true);

	public final ResourceLocation skeletonLoc;
	public final Reference<Skeleton> skeleton;
	public final boolean isRenderOnly;

	MCMSSkeleton(String skeleton, boolean isRenderOnly)
	{
		this.skeletonLoc = new ResourceLocation(MechanimationAPI.MOD_ID, skeleton);
		this.isRenderOnly = isRenderOnly;
		if (!isRenderOnly || FMLCommonHandler.instance().getSide().isClient()) this.skeleton = Loader.INSTANCE.getSkeleton(skeletonLoc);
		else this.skeleton = new Reference<>(null);
	}
}