package firemerald.mechanimation.mcms;

import firemerald.api.mcms.animation.IAnimation;
import firemerald.api.mcms.util.Loader;
import firemerald.api.mcms.util.Loader.Reference;
import firemerald.mechanimation.api.MechanimationAPI;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;

public enum MCMSAnimation
{
	PRESS_BASIC_OPENING("press/basic/opening.anim", true),
	PRESS_BASIC_CLOSING("press/basic/closing.anim", true),
	PRESS_BASIC_RUNNING("press/basic/running.anim", true),

	PRESS_ADVANCED_OPENING("press/advanced/opening.anim", true),
	PRESS_ADVANCED_CLOSING("press/advanced/closing.anim", true),
	PRESS_ADVANCED_RUNNING("press/advanced/running.anim", true),

	FLUID_REACTOR_ADVANCED_RUNNING("fluid_reactor/advanced/running.anim", true),
	HYDROTREATER_ADVANCED_RUNNING("hydrotreater/advanced/running.anim", true),
	CATALYTIC_REFORMER_ADVANCED_RUNNING("catalytic_reformer/advanced/running.anim", true),
	STIRLING_GENERATOR_RUNNING("generator/stirling/running.anim", true),
	COMBUSTION_GENERATOR_RUNNING("generator/combustion/running.anim", true),
	PULVERIZER_BASIC_RUNNING("pulverizer/basic/running.anim", true),

	ARC_FURNACE_ADVANCED_RUNNING("arc_furnace/advanced/running.anim", false);

	public final ResourceLocation animationLoc;
	public final boolean isRenderOnly;
	public final Reference<IAnimation> animation;

	MCMSAnimation(String animation, boolean isRenderOnly)
	{
		this.animationLoc = new ResourceLocation(MechanimationAPI.MOD_ID, animation);
		this.isRenderOnly = isRenderOnly;
		if (!isRenderOnly || FMLCommonHandler.instance().getSide().isClient()) this.animation = Loader.INSTANCE.getAnimation(animationLoc);
		else this.animation = new Reference<>(null);
	}
}