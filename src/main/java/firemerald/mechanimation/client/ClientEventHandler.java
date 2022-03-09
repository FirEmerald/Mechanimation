package firemerald.mechanimation.client;

import firemerald.api.core.plugin.URLArtifactVersion;
import firemerald.mechanimation.Main;
import firemerald.mechanimation.api.util.APIUtils;
import firemerald.mechanimation.api.util.APIUtils.BlockOperationTemp;
import firemerald.mechanimation.blocks.ICustomBlockHighlight;
import firemerald.mechanimation.client.renderer.IBlockHighlight;
import firemerald.mechanimation.compat.mekanism.CompatProviderMekanism;
import mekanism.api.gas.GasRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionRange;

public class ClientEventHandler
{
	@SubscribeEvent
	public void onClientTick(ClientTickEvent event)
	{
		Minecraft minecraft = Minecraft.getMinecraft();
		if (event.phase == Phase.END)
		{
			EntityPlayerSP player = minecraft.player;
			if (player != null)
			{
				BlockOperationTemp op = new BlockOperationTemp(player.getEntityBoundingBox(), 0, 3, APIUtils.getBiomeTempCelsius(player.world, player.getPosition()));
				APIUtils.forEachBlock(player.world, op.getEffectiveBox(), false, op);
				ClientState.setAmbientTemperature(op.getTemp());
			}
			if (minecraft.world != null) while (!ClientState.QUEUED_ACTIONS.isEmpty()) ClientState.QUEUED_ACTIONS.poll().run();
			else ClientState.QUEUED_ACTIONS.clear();
			ClientState.clientTicks++;
		}
	}

	@SubscribeEvent
	public void onRenderTick(RenderTickEvent event)
	{
		if (event.phase == TickEvent.Phase.START)
		{
			ClientState.partialTick = event.renderTickTime;
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onDrawBlockHighlight(DrawBlockHighlightEvent event)
	{
		RayTraceResult result = event.getTarget();
		if (result.typeOfHit == Type.BLOCK)
		{
	        ItemStack currentStack = event.getPlayer().getHeldItemMainhand();
	        Item currentItem = currentStack.getItem();
	        ICustomBlockHighlight customHighlight = null;
	        if (currentItem instanceof ICustomBlockHighlight) customHighlight = (ICustomBlockHighlight) currentItem;
	        else if (currentItem instanceof ItemBlock)
	        {
	        	Block block = ((ItemBlock) currentItem).getBlock();
	        	if (block instanceof ICustomBlockHighlight) customHighlight = (ICustomBlockHighlight) block;
	        }
        	if (customHighlight != null)
        	{
        		IBlockHighlight highlight = customHighlight.getBlockHighlight(event.getPlayer(), result);
        		if (highlight != null) highlight.render(event.getPlayer(), result, event.getPartialTicks());
        	}
		}
	}

    @SubscribeEvent
    public void onTextureStitchPre(TextureStitchEvent.Pre event)
    {
		TextureMap map = event.getMap();
		if (!CompatProviderMekanism.INSTANCE.isPresent()) GasRegistry.getRegisteredGasses().forEach(gas -> gas.registerIcon(map));
    }

	@SubscribeEvent
	public void onTextureStitchPost(TextureStitchEvent.Post event)
	{
		TextureMap map = event.getMap();
		if (!CompatProviderMekanism.INSTANCE.isPresent()) GasRegistry.getRegisteredGasses().forEach(gas -> gas.updateIcon(map));
	}

	private boolean shownInstallChat = false;

	@SubscribeEvent
	public void onClientConnectedToServerEvent(ClientConnectedToServerEvent event)
	{
		if (!shownInstallChat)
		{
			((ClientProxy) Main.proxy()).clientRecommended.forEach(dep -> {
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
					msg = new TextComponentTranslation("msg.mechanimation.recommended.url", dep.getLabel(), rangeStr);
					msg.getStyle()
					.setUnderlined(true)
					.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, depURL.url))
					.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(depURL.url)));
				}
				else msg = new TextComponentTranslation("msg.mechanimation.recommended", dep.getLabel(), dep.getRangeString());
				ClientState.QUEUED_ACTIONS.add(() -> {
					GuiIngame gui = Minecraft.getMinecraft().ingameGUI;
					if (gui != null) gui.addChatMessage(ChatType.SYSTEM, msg);
				});
			});
			shownInstallChat = true;
		}
	}
}