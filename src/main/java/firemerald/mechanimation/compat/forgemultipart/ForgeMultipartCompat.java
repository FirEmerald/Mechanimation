package firemerald.mechanimation.compat.forgemultipart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import codechicken.microblock.BlockMicroMaterial;
import firemerald.api.core.IFMLEventHandler;
import firemerald.mechanimation.compat.forgemultipart.networking.PipeEnergySyncPacket;
import firemerald.mechanimation.compat.forgemultipart.networking.PipeFluidSyncPacket;
import firemerald.mechanimation.compat.forgemultipart.networking.PipeItemsSyncPacket;
import firemerald.mechanimation.compat.forgemultipart.pipe.EnumPipeTier;
import firemerald.mechanimation.compat.forgemultipart.pipe.PartEnergyPipe;
import firemerald.mechanimation.compat.forgemultipart.pipe.PartFluidPipe;
import firemerald.mechanimation.compat.forgemultipart.pipe.PartItemPipe;
import firemerald.mechanimation.init.MechanimationBlocks;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class ForgeMultipartCompat implements IFMLEventHandler
{
	public static final ForgeMultipartCompat INSTANCE = new ForgeMultipartCompat();
	public static final Logger LOGGER = LogManager.getLogger("Mechanimation CB Multipart Compat");

	public SimpleNetworkWrapper network;
	
	@Override
	public void onPreInitialization(FMLPreInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(this);
    	network = NetworkRegistry.INSTANCE.newSimpleChannel("mechanimation_fmp");
    	network.registerMessage(PipeEnergySyncPacket.Handler.class, PipeEnergySyncPacket.class, 0, Side.CLIENT);
    	network.registerMessage(PipeFluidSyncPacket.Handler.class, PipeFluidSyncPacket.class, 1, Side.CLIENT);
    	network.registerMessage(PipeItemsSyncPacket.Handler.class, PipeItemsSyncPacket.class, 2, Side.CLIENT);
		MultipartTabs.init();
	}
	
	@Override
	public void onInitialization(FMLInitializationEvent event)
	{
		MultipartItems.initMultipart();
	}
	
	@SubscribeEvent
	public void onItemRegistry(RegistryEvent.Register<Item> event)
	{
		MultipartItems.init(event.getRegistry());
		if (FMLCommonHandler.instance().getSide().isClient()) MultipartItems.registerModels();
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onBlockRegistry(RegistryEvent.Register<Block> event)
	{
		BlockMicroMaterial.createAndRegister(MechanimationBlocks.NICKEL_ORE);
		BlockMicroMaterial.createAndRegister(MechanimationBlocks.COPPER_ORE);
		BlockMicroMaterial.createAndRegister(MechanimationBlocks.ALUMINUM_ORE);
		BlockMicroMaterial.createAndRegister(MechanimationBlocks.TIN_ORE);
		BlockMicroMaterial.createAndRegister(MechanimationBlocks.SILVER_ORE);
		BlockMicroMaterial.createAndRegister(MechanimationBlocks.TUNGSTEN_ORE);
		BlockMicroMaterial.createAndRegister(MechanimationBlocks.TITANIUM_ORE);
		BlockMicroMaterial.createAndRegister(MechanimationBlocks.SULFUR_ORE);
		BlockMicroMaterial.createAndRegister(MechanimationBlocks.METAL_GRATE_BLOCK);
		BlockMicroMaterial.createAndRegister(MechanimationBlocks.NICKEL_BLOCK);
		BlockMicroMaterial.createAndRegister(MechanimationBlocks.COPPER_BLOCK);
		BlockMicroMaterial.createAndRegister(MechanimationBlocks.ALUMINUM_BLOCK);
		BlockMicroMaterial.createAndRegister(MechanimationBlocks.TIN_BLOCK);
		BlockMicroMaterial.createAndRegister(MechanimationBlocks.SILVER_BLOCK);
		BlockMicroMaterial.createAndRegister(MechanimationBlocks.TITANIUM_BLOCK);
		BlockMicroMaterial.createAndRegister(MechanimationBlocks.STEEL_BLOCK);
		BlockMicroMaterial.createAndRegister(MechanimationBlocks.REINFORCED_GLASS);
		BlockMicroMaterial.createAndRegister(MechanimationBlocks.ASPHALT);
	}
	
    @SubscribeEvent
    public void registerItemColors(ColorHandlerEvent.Item event)
    {
    	MultipartItems.registerColors(event.getItemColors());
    }

    @SubscribeEvent
    public void onTextureStitchPre(TextureStitchEvent.Pre event)
    {
		TextureMap map = event.getMap();
		for (EnumPipeTier tier : EnumPipeTier.values()) map.registerSprite(tier.iconRL);
		map.registerSprite(PartItemPipe.ICON);
		map.registerSprite(PartItemPipe.EXTRACTOR_ICON);
		map.registerSprite(PartEnergyPipe.ICON);
		map.registerSprite(PartFluidPipe.ICON);
		map.registerSprite(PartFluidPipe.EXTRACTOR_ICON);
    }

	@SubscribeEvent
	public void onTextureStitchPost(TextureStitchEvent.Post event)
	{
		TextureMap map = event.getMap();
		for (EnumPipeTier tier : EnumPipeTier.values()) tier.icon = map.getAtlasSprite(tier.iconRL.toString());
		PartItemPipe.icon = map.getAtlasSprite(PartItemPipe.ICON.toString());
		PartItemPipe.extractor_icon = map.getAtlasSprite(PartItemPipe.EXTRACTOR_ICON.toString());
		PartEnergyPipe.icon = map.getAtlasSprite(PartEnergyPipe.ICON.toString());
		PartFluidPipe.icon = map.getAtlasSprite(PartFluidPipe.ICON.toString());
		PartFluidPipe.extractor_icon = map.getAtlasSprite(PartFluidPipe.EXTRACTOR_ICON.toString());
	}
}