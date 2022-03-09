package firemerald.mechanimation.common;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import org.lwjgl.opengl.Display;

import firemerald.api.core.IProxy;
import firemerald.api.core.plugin.ITransformer;
import firemerald.api.core.plugin.URLArtifactVersion;
import firemerald.mechanimation.Main;
import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.api.capabilities.Capabilities;
import firemerald.mechanimation.api.properties.BlockProperties;
import firemerald.mechanimation.capabilities.PlayerSettings;
import firemerald.mechanimation.client.ClientProxy;
import firemerald.mechanimation.client.util.ClientUtil;
import firemerald.mechanimation.config.CommonConfig;
import firemerald.mechanimation.init.MechanimationBlocks;
import firemerald.mechanimation.init.MechanimationFluids;
import firemerald.mechanimation.init.MechanimationParticles;
import firemerald.mechanimation.init.MechanimationStats;
import firemerald.mechanimation.networking.client.MechanimationParticleSpawnPacket;
import firemerald.mechanimation.networking.client.ServerSettingsSyncPacket;
import firemerald.mechanimation.networking.client.TileGUIPacket;
import firemerald.mechanimation.networking.server.MachineGUIPacket;
import firemerald.mechanimation.networking.server.TileGUIClosedPacket;
import firemerald.mechanimation.plugin.Core;
import firemerald.mechanimation.properties.ThermalTempsConfig;
import firemerald.mechanimation.properties.ThermalTempsFluid;
import firemerald.mechanimation.tileentity.machine.arc_furnace.TileEntityArcFurnaceAdvanced;
import firemerald.mechanimation.tileentity.machine.arc_furnace.TileEntityBlastFurnace;
import firemerald.mechanimation.tileentity.machine.arc_furnace.TileEntityBlastFurnaceTop;
import firemerald.mechanimation.tileentity.machine.assembly_terminal.TileEntityAssemblyTerminalBasic;
import firemerald.mechanimation.tileentity.machine.assembly_terminal.TileEntityAssemblyTerminalPart;
import firemerald.mechanimation.tileentity.machine.casting_table.TileEntityCastingTableBasic;
import firemerald.mechanimation.tileentity.machine.catalytic_reformer.TileEntityCatalyticReformerAdvanced;
import firemerald.mechanimation.tileentity.machine.claus_plant.TileEntityClausPlantAdvanced;
import firemerald.mechanimation.tileentity.machine.desalter.TileEntityDesalterAdvanced;
import firemerald.mechanimation.tileentity.machine.distillation_tower.TileEntityDistillationTowerAdvanced;
import firemerald.mechanimation.tileentity.machine.distillation_tower.TileEntityDistillationTowerMiddle;
import firemerald.mechanimation.tileentity.machine.distillation_tower.TileEntityDistillationTowerTop;
import firemerald.mechanimation.tileentity.machine.electrolyzer.TileEntityElectrolyzerAdvanced;
import firemerald.mechanimation.tileentity.machine.fluid_reactor.TileEntityFluidReactorAdvanced;
import firemerald.mechanimation.tileentity.machine.generator.TileEntityCombustionGenerator;
import firemerald.mechanimation.tileentity.machine.generator.TileEntityStirlingGenerator;
import firemerald.mechanimation.tileentity.machine.hydrotreater.TileEntityHydrotreaterAdvanced;
import firemerald.mechanimation.tileentity.machine.merox_treater.TileEntityMeroxTreaterAdvanced;
import firemerald.mechanimation.tileentity.machine.press.TileEntityPressAdvanced;
import firemerald.mechanimation.tileentity.machine.press.TileEntityPressBasic;
import firemerald.mechanimation.tileentity.machine.pulverizer.TileEntityPulverizerBasic;
import firemerald.mechanimation.tileentity.multiblock.TileEntityMultiblockFluxReceiver;
import firemerald.mechanimation.util.crafting.CraftingLoader;
import firemerald.mechanimation.util.crafting.OreDictUtil;
import firemerald.mechanimation.world.WorldGenCustom;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.client.SplashProgress;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionRange;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy implements IProxy
{
	public final List<ArtifactVersion> commonRecommended = new ArrayList<>();

	@SuppressWarnings("deprecation")
	public void ensureDependencies()
	{
    	List<ArtifactVersion> missing = new ArrayList<>();
    	List<ArtifactVersion> recommended = new ArrayList<>();
    	Core.METADATA.dependencies.forEach(dep -> {
    		ModContainer depCont = FMLCommonHandler.instance().findContainerFor(dep.getLabel());
    		if (depCont == null || !dep.containsVersion(depCont.getProcessedVersion())) missing.add(dep);
    	});
    	Core.COMMON_RECOMMENDED.forEach(dep -> {
    		ModContainer depCont = FMLCommonHandler.instance().findContainerFor(dep.getLabel());
    		if (depCont == null || !dep.containsVersion(depCont.getProcessedVersion()))
    		{
    			commonRecommended.add(dep);
    			recommended.add(dep);
    		}
    	});
    	if (FMLCommonHandler.instance().getSide().isClient()) //add client recommended mods
    	{
    		ClientProxy proxy = (ClientProxy) this;
        	Core.CLIENT_RECOMMENDED.forEach(dep -> {
        		ModContainer depCont = FMLCommonHandler.instance().findContainerFor(dep.getLabel());
        		if (depCont == null || !dep.containsVersion(depCont.getProcessedVersion()))
        		{
        			proxy.clientRecommended.add(dep);
        			recommended.add(dep);
        		}
        	});
    	}
    	if (missing.size() > 0)
    	{
    		if (FMLCommonHandler.instance().getSide().isClient())
    		{
        	    JFrame frame = new JFrame("Mechanimation is missing the following dependencies:");
        	    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        	    frame.setSize(420, (missing.size() + recommended.size()) * 42);
        	    Container container = frame.getContentPane();
        	    container.setLayout(new FlowLayout());
        		missing.forEach(dep -> {
    	    	    JButton button = new JButton();
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
        	    	    button.setText(dep.getLabel() + ": " + rangeStr + " (Click to download)");
        	    	    button.setToolTipText(depURL.url);
        	    	    button.addActionListener(event -> ClientUtil.openWebpage(depURL.url));
        			}
        			else button.setText(dep.getLabel() + ": " + dep.getRangeString());
    	    	    button.setHorizontalAlignment(SwingConstants.LEFT);
    	    	    button.setBorderPainted(true);
    	    	    button.setOpaque(true);
    	    	    button.setBackground(Color.WHITE);
    	    	    container.add(button);
        		});
        		recommended.forEach(dep -> {
    	    	    JButton button = new JButton();
        			if (dep instanceof URLArtifactVersion)
        			{
        				URLArtifactVersion depURL = (URLArtifactVersion) dep;
        				String rangeStr;
        				VersionRange range = depURL.getRange();
    					if (range != null && range.hasRestrictions())
    					{
    	    				if (range.isUnboundedAbove()) rangeStr = range.getLowerBoundString() + " or higher";
    	    				else rangeStr = range.getLowerBoundString();
    					}
    					else rangeStr = "any version";
        	    	    button.setText("(recommended) " + dep.getLabel() + ": " + rangeStr + " (Click to download)");
        	    	    button.setToolTipText(depURL.url);
        	    	    button.addActionListener(event -> ClientUtil.openWebpage(depURL.url));
        			}
        			else button.setText("(recommended) " + dep.getLabel() + ": " + dep.getRangeString());
    	    	    button.setHorizontalAlignment(SwingConstants.LEFT);
    	    	    button.setBorderPainted(true);
    	    	    button.setOpaque(true);
    	    	    button.setBackground(Color.WHITE);
    	    	    container.add(button);
        		});
        		frame.addWindowListener(new WindowListener() {
    				@Override
    				public void windowActivated(WindowEvent arg0) {}

    				@Override
    				public void windowClosed(WindowEvent arg0)
    				{
    					FMLCommonHandler.instance().exitJava(-1, false);
    				}

    				@Override
    				public void windowClosing(WindowEvent arg0) {}

    				@Override
    				public void windowDeactivated(WindowEvent arg0) {}

    				@Override
    				public void windowDeiconified(WindowEvent arg0) {}

    				@Override
    				public void windowIconified(WindowEvent arg0) {}

    				@Override
    				public void windowOpened(WindowEvent arg0) {}
        		});
        	    SplashProgress.pause();
                Display.destroy();
                frame.setLocationRelativeTo(null);
        	    frame.setVisible(true);
        	    frame.requestFocus();
        	    while (true)
        	    {
        	    	try
        	    	{
    					Thread.sleep(100);
    				}
        	    	catch (InterruptedException e) {}
        	    }
    		}
    		else
    		{
    			Main.LOGGER.fatal("Mechanimation is missing the following dependencies:");
        		missing.forEach(dep -> {
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
        				Main.LOGGER.fatal(dep.getLabel() + ": " + rangeStr + " from " + depURL.url);
        			}
        			else Main.LOGGER.fatal(dep.getLabel() + ": " + dep.getRangeString());
        		});
        		recommended.forEach(dep -> {
        			if (dep instanceof URLArtifactVersion)
        			{
        				URLArtifactVersion depURL = (URLArtifactVersion) dep;
        				String rangeStr;
        				VersionRange range = depURL.getRange();
    					if (range != null && range.hasRestrictions())
    					{
    	    				if (range.isUnboundedAbove()) rangeStr = range.getLowerBoundString() + " or higher";
    	    				else rangeStr = range.getLowerBoundString();
    					}
    					else rangeStr = "any version";
        				Main.LOGGER.fatal("(recommended) " + dep.getLabel() + ": " + rangeStr + " from " + depURL.url);
        			}
        			else Main.LOGGER.fatal("(recommended) " + dep.getLabel() + ": " + dep.getRangeString());
        		});
    		}
    	}
	}

    @Override
	public void onConstruction(FMLConstructionEvent event)
    {
    	ensureDependencies();
    	FluidRegistry.enableUniversalBucket();
    }

	@Override
    public void onPreInitialization(FMLPreInitializationEvent event)
    {
    	loadConfig();
		MechanimationFluids.init();
		MechanimationBlocks.preInit();
    	PlayerSettings.register();
    	//NetworkRegistry.INSTANCE.registerGuiHandler(this, new MC4GuiHandler());
    	SimpleNetworkWrapper network = Main.instance().network = NetworkRegistry.INSTANCE.newSimpleChannel("mechanimation");
		int p = 0;
		network.registerMessage(ServerSettingsSyncPacket.Handler.class, ServerSettingsSyncPacket.class, p++, Side.CLIENT);
		network.registerMessage(TileGUIPacket.Handler.class, TileGUIPacket.class, p++, Side.CLIENT);
		network.registerMessage(MechanimationParticleSpawnPacket.Handler.class, MechanimationParticleSpawnPacket.class, p++, Side.CLIENT);

		network.registerMessage(TileGUIClosedPacket.Handler.class, TileGUIClosedPacket.class, p++, Side.SERVER);
		network.registerMessage(MachineGUIPacket.Handler.class, MachineGUIPacket.class, p++, Side.SERVER);

		NetworkRegistry.INSTANCE.registerGuiHandler(MechanimationAPI.MOD_ID, new GuiHandler());
    	MinecraftForge.EVENT_BUS.register(new CommonEventHandler());
		BlockProperties.registerThermalTemps(ThermalTempsFluid.INSTANCE);
		BlockProperties.registerThermalTemps(ThermalTempsConfig.INSTANCE);
		/*
		CraftingHelper.register(new ResourceLocation(MC4API.MOD_ID, "subtyped"), new VariantIngredientFactory());
		CraftingHelper.register(new ResourceLocation(MC4API.MOD_ID, "bucket"), new BucketIngredientFactory());
		CraftingHelper.register(new ResourceLocation(MC4API.MOD_ID, "pipe"), new PipeIngredientFactory());

		CraftingHelper.register(new ResourceLocation(MC4API.MOD_ID, "dummy"), new DummyRecipeFactory());
		CraftingHelper.register(new ResourceLocation(MC4API.MOD_ID, "crafting_shaped"), new ShapedIngredientRecipeFactory());
		CraftingHelper.register(new ResourceLocation(MC4API.MOD_ID, "crafting_shapeless"), new ShapelessIngredientRecipeFactory());
		CraftingHelper.register(new ResourceLocation(MC4API.MOD_ID, "bucket_shaped"), new ShapedBucketIngredientRecipeFactory());
		CraftingHelper.register(new ResourceLocation(MC4API.MOD_ID, "bucket_shapeless"), new ShapelessBucketIngredientRecipeFactory());
		*/
    }

    public void loadConfig()
    {
    	CommonConfig.INSTANCE.loadConfig();
    }

	@Override
    public void onInitialization(FMLInitializationEvent event)
    {
		CraftingLoader.initLoaders();
		MechanimationParticles.registerParticleTypes();
    	Capabilities.register();
		GameRegistry.registerTileEntity(TileEntityPressBasic.class, new ResourceLocation(MechanimationAPI.MOD_ID, "press_basic"));
		GameRegistry.registerTileEntity(TileEntityPressAdvanced.class, new ResourceLocation(MechanimationAPI.MOD_ID, "press_advanced"));
		GameRegistry.registerTileEntity(TileEntityFluidReactorAdvanced.class, new ResourceLocation(MechanimationAPI.MOD_ID, "fluid_reactor_advanced"));
		GameRegistry.registerTileEntity(TileEntityElectrolyzerAdvanced.class, new ResourceLocation(MechanimationAPI.MOD_ID, "electrolyzer_advanced"));
		GameRegistry.registerTileEntity(TileEntityHydrotreaterAdvanced.class, new ResourceLocation(MechanimationAPI.MOD_ID, "hydrotreater_advanced"));
		GameRegistry.registerTileEntity(TileEntityDesalterAdvanced.class, new ResourceLocation(MechanimationAPI.MOD_ID, "desalter_advanced"));
		GameRegistry.registerTileEntity(TileEntityCatalyticReformerAdvanced.class, new ResourceLocation(MechanimationAPI.MOD_ID, "catalytic_reformer_advanced"));
		GameRegistry.registerTileEntity(TileEntityDistillationTowerAdvanced.class, new ResourceLocation(MechanimationAPI.MOD_ID, "distillation_tower_advanced"));
		GameRegistry.registerTileEntity(TileEntityDistillationTowerMiddle.class, new ResourceLocation(MechanimationAPI.MOD_ID, "oil_distillation_tower_middle"));
		GameRegistry.registerTileEntity(TileEntityDistillationTowerTop.class, new ResourceLocation(MechanimationAPI.MOD_ID, "oil_distillation_tower_top"));
		GameRegistry.registerTileEntity(TileEntityMeroxTreaterAdvanced.class, new ResourceLocation(MechanimationAPI.MOD_ID, "merox_treater_advanced"));
		GameRegistry.registerTileEntity(TileEntityClausPlantAdvanced.class, new ResourceLocation(MechanimationAPI.MOD_ID, "claus_plant_advanced"));
		GameRegistry.registerTileEntity(TileEntityStirlingGenerator.class, new ResourceLocation(MechanimationAPI.MOD_ID, "stirling_generator"));
		GameRegistry.registerTileEntity(TileEntityCombustionGenerator.class, new ResourceLocation(MechanimationAPI.MOD_ID, "combustion_generator"));
		GameRegistry.registerTileEntity(TileEntityPulverizerBasic.class, new ResourceLocation(MechanimationAPI.MOD_ID, "pulverizer_basic"));
		GameRegistry.registerTileEntity(TileEntityBlastFurnace.class, new ResourceLocation(MechanimationAPI.MOD_ID, "blast_furnace"));
		GameRegistry.registerTileEntity(TileEntityBlastFurnaceTop.class, new ResourceLocation(MechanimationAPI.MOD_ID, "blast_furnace_top"));
		GameRegistry.registerTileEntity(TileEntityArcFurnaceAdvanced.class, new ResourceLocation(MechanimationAPI.MOD_ID, "arc_furnace_advanced"));
		GameRegistry.registerTileEntity(TileEntityCastingTableBasic.class, new ResourceLocation(MechanimationAPI.MOD_ID, "casting_table_basic"));
		GameRegistry.registerTileEntity(TileEntityAssemblyTerminalBasic.class, new ResourceLocation(MechanimationAPI.MOD_ID, "assembly_terminal_basic"));
		GameRegistry.registerTileEntity(TileEntityAssemblyTerminalPart.class, new ResourceLocation(MechanimationAPI.MOD_ID, "assembly_terminal_multiblock"));
		GameRegistry.registerTileEntity(TileEntityMultiblockFluxReceiver.class, new ResourceLocation(MechanimationAPI.MOD_ID, "multiblock_flux_receiver"));
		GameRegistry.registerWorldGenerator(WorldGenCustom.INSTANCE, 0);
		MechanimationStats.init();
	}

	@Override
    public void onPostInitialization(FMLPostInitializationEvent event)
    {
    	OreDictUtil.init();
    }

	@Override
    public void onServerAboutToStart(FMLServerAboutToStartEvent event)
	{
		//reload any configs that may have been overwritten by a server
		CommonConfig.INSTANCE.loadConfig();
	}

	@Override
    public void onServerStarting(FMLServerStartingEvent event)
    {
    	if (ITransformer.IS_DEOBFUSCATED) event.getServer().setOnlineMode(false);
    }

	public EntityPlayer getPlayer()
	{
		return null;
	}

	public boolean isThePlayer(Entity entity)
	{
		return false;
	}
}