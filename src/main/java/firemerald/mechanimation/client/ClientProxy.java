package firemerald.mechanimation.client;

import java.util.ArrayList;
import java.util.List;

import firemerald.api.mcms.animation.Transformation;
import firemerald.api.mcms.client.renderers.TileEntityRendererModel;
import firemerald.api.mcms.model.effects.BoneEffect;
import firemerald.api.mcms.model.effects.EffectRenderStage;
import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.client.boneeffect.EffectAssemblyDisplay;
import firemerald.mechanimation.common.CommonProxy;
import firemerald.mechanimation.config.ClientConfig;
import firemerald.mechanimation.mcms.FluidOrGasRenderEffect;
import firemerald.mechanimation.tileentity.machine.base.implementation.actual.TileEntityMachineBase;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;

public class ClientProxy extends CommonProxy
{
	public final List<ArtifactVersion> clientRecommended = new ArrayList<>();

	@Override
    public void onPreInitialization(FMLPreInitializationEvent event)
    {
		super.onPreInitialization(event);
		MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
		BoneEffect.registerBoneType(new ResourceLocation(MechanimationAPI.MOD_ID, "assembly_terminal"), (parent, element) -> {
			EffectAssemblyDisplay effect = new EffectAssemblyDisplay("assembly_input", parent, EffectRenderStage.POST_CHILDREN);
			effect.loadFromXML(element);
			return effect;
		});
    }

    @Override
	public void loadConfig()
    {
    	super.loadConfig();
    	ClientConfig.INSTANCE.loadConfig();
    }

	@Override
	public void onInitialization(FMLInitializationEvent event)
    {
		super.onInitialization(event);
		BoneEffect.registerBoneType(new ResourceLocation(MechanimationAPI.MOD_ID, "fluid_or_gas"), (parent, element) -> {
			BoneEffect bone = new FluidOrGasRenderEffect(element.getString("name", "unnamed fluid"), parent, new Transformation(), 0);
			bone.loadFromXML(element);
			return bone;
		});
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMachineBase.class, new TileEntityRendererModel<>());
    }

	@Override
	public EntityPlayer getPlayer()
	{
		return Minecraft.getMinecraft().player;
	}

	@Override
	public boolean isThePlayer(Entity entity)
	{
		return entity == Minecraft.getMinecraft().player;
	}
}