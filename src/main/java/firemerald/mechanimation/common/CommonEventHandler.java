package firemerald.mechanimation.common;

import java.util.Random;

import firemerald.api.core.plugin.URLArtifactVersion;
import firemerald.mechanimation.Main;
import firemerald.mechanimation.capabilities.PlayerSettings;
import firemerald.mechanimation.config.CommonConfig;
import firemerald.mechanimation.fluid.CustomFluid;
import firemerald.mechanimation.init.MechanimationBlocks;
import firemerald.mechanimation.init.MechanimationItems;
import firemerald.mechanimation.init.MechanimationSounds;
import firemerald.mechanimation.networking.client.ServerSettingsSyncPacket;
import firemerald.mechanimation.util.Constants;
import firemerald.mechanimation.world.WorldGenCustom;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BlockEvent.CreateFluidSourceEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerConnectionFromClientEvent;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionRange;

public class CommonEventHandler
{
	@SubscribeEvent
	public void onItemRegistry(RegistryEvent.Register<Item> event)
	{
		MechanimationItems.init(event.getRegistry());
		if (FMLCommonHandler.instance().getSide().isClient()) MechanimationItems.registerModels();
	}

	@SubscribeEvent
	public void onBlockRegistry(RegistryEvent.Register<Block> event)
	{
		MechanimationBlocks.init(event.getRegistry());
	}

	@SubscribeEvent
	public void onSoundRegistry(RegistryEvent.Register<SoundEvent> event)
	{
		MechanimationSounds.register(event.getRegistry());
	}

	@SubscribeEvent
	public void AttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event)
	{
		if (event.getObject() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) event.getObject();
			event.addCapability(Constants.PLAYER_SETTINGS_CAP_NAME, new PlayerSettings(player));
		}
	}

	@SubscribeEvent
	public void onServerTick(ServerTickEvent event)
	{
		if (event.phase == Phase.END)
		{
			while (!CommonState.QUEUED_ACTIONS.isEmpty()) CommonState.QUEUED_ACTIONS.poll().run();
		}
	}

	@SubscribeEvent
	public void onServerConnectionFromClient(ServerConnectionFromClientEvent event)
	{
		Main.network().sendTo(new ServerSettingsSyncPacket(CommonConfig.INSTANCE), ((NetHandlerPlayServer) event.getHandler()).player);
		//System.exit(0);
	}

	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event)
	{
		if (Main.proxy().isThePlayer(event.player) || event.player.canUseCommandBlock()) //client player or opped players
		{
			Main.proxy().commonRecommended.forEach(dep -> {
				ITextComponent msg;
				if (dep instanceof URLArtifactVersion)
				{
					URLArtifactVersion depURL = (URLArtifactVersion) dep;
					String rangeStr;
					VersionRange range = depURL.getRange();
    				if (range != null)
    				{
    					if (range.hasRestrictions())
    					{
    	    				if (range.isUnboundedAbove()) rangeStr = range.getLowerBoundString() + " or higher";
    	    				else rangeStr = range.getLowerBoundString();
    					}
    					else
    					{
    						ArtifactVersion version = range.getRecommendedVersion();
    						if (version != null) rangeStr = version.getVersionString();
    						else rangeStr = "any version";
    					}
    				}
    				else rangeStr = "any version";
					msg = new TextComponentTranslation("msg.mechanimation.recommended.server.url", dep.getLabel(), rangeStr);
					msg.getStyle()
					.setUnderlined(true)
					.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, depURL.url))
					.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(depURL.url)));
				}
				else msg = new TextComponentTranslation("msg.mechanimation.recommended.server", dep.getLabel(), dep.getRangeString());
				event.player.sendMessage(msg);
			});
		}
	}

	@SubscribeEvent
	public void onCreateFluidSource(CreateFluidSourceEvent event)
	{
		Fluid fluid = FluidRegistry.lookupFluidForBlock(event.getState().getBlock());
		if (fluid instanceof CustomFluid)
		{
			if (((CustomFluid) fluid).isInfinite()) event.setResult(Result.ALLOW);
			else event.setResult(Result.DENY);
		}
	}

	@SubscribeEvent
	public void onChunkLoad(ChunkDataEvent.Load event)
	{
		byte retroGen;
		NBTTagCompound data = event.getData();
		if (data.getByte("mc4Retrogen") == 1) retroGen = 1; //old mc4 world
		else retroGen = event.getData().getByte("mechanimationRetrogen");
		World world = event.getWorld();
		Chunk chunk = event.getChunk();
		Random rand = chunk.getRandomWithSeed(world.getSeed());
		int chunkX = chunk.x;
		int chunkZ = chunk.z;
		CommonState.QUEUED_ACTIONS.add(() -> WorldGenCustom.INSTANCE.generate(rand, chunkX, chunkZ, world, retroGen));
	}

	@SubscribeEvent
	public void onChunkSave(ChunkDataEvent.Save event)
	{
		event.getData().setByte("mechanimationRetrogen", (byte) 1);
	}
}