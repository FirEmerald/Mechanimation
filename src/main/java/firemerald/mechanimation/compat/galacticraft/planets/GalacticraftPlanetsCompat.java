package firemerald.mechanimation.compat.galacticraft.planets;

import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import firemerald.api.core.IFMLEventHandler;
import firemerald.craftloader.api.CraftingReloadEvent;
import firemerald.mechanimation.api.crafting.assembly_terminal.AssemblyRecipes;
import firemerald.mechanimation.api.util.Rectangle;
import firemerald.mechanimation.api.util.Vec2i;
import firemerald.mechanimation.compat.galacticraft.ConfigGalacticraft;
import firemerald.mechanimation.compat.galacticraft.NasaWorkbenchAssemblyBlueprint;
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import micdoodle8.mods.galacticraft.core.GCItems;
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets;
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GalacticraftPlanetsCompat implements IFMLEventHandler
{
	public static final GalacticraftPlanetsCompat INSTANCE = new GalacticraftPlanetsCompat();
	public static final Logger LOGGER = LogManager.getLogger("Mechanimation Galacticraft Planets Compat");
	//mars
    private static final ResourceLocation TIER_2_ROCKET_TEXTURE = new ResourceLocation(GalacticraftPlanets.ASSET_PREFIX, "textures/gui/schematic_rocket_t2.png");
    public static final Supplier<ItemStack> TIER_2_ROCKET_BLUEPRINT = () -> new ItemStack(GCItems.schematic, 1, 1);
    private static final ResourceLocation CARGO_ROCKET_TEXTURE = new ResourceLocation(GalacticraftPlanets.ASSET_PREFIX, "textures/gui/schematic_rocket_cargo.png");
    public static final Supplier<ItemStack> CARGO_ROCKET_BLUEPRINT = () -> new ItemStack(MarsItems.schematic, 1, 1);
    //asteroids
    private static final ResourceLocation TIER_3_ROCKET_TEXTURE = new ResourceLocation(GalacticraftPlanets.ASSET_PREFIX, "textures/gui/schematic_rocket_t3.png");
    public static final Supplier<ItemStack> TIER_3_ROCKET_BLUEPRINT = () -> new ItemStack(MarsItems.schematic, 1, 0);
    private static final ResourceLocation ASTRO_MINER_TEXTURE = new ResourceLocation(GalacticraftPlanets.ASSET_PREFIX, "textures/gui/schematic_astro_miner.png");
    public static final Supplier<ItemStack> ASTRO_MINER_BLUEPRINT = () -> new ItemStack(MarsItems.schematic, 1, 2);

	@Override
	public void onPreInitialization(FMLPreInitializationEvent event)
	{
    	MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onCraftingReload(CraftingReloadEvent.Post event)
	{
		//mars module
		if (ConfigGalacticraft.INSTANCE.tier2RocketAssembly.val)
		{
			AssemblyRecipes.registerBlueprint(new NasaWorkbenchAssemblyBlueprint("tier_2_rocket", TIER_2_ROCKET_BLUEPRINT.get(), 0, TIER_2_ROCKET_TEXTURE, Rectangle.ofSize(20, 10, 147, 134), ConfigGalacticraft.INSTANCE.tier2RocketAssemblyFlux.val, () -> GalacticraftRegistry.getRocketT2Recipes().stream(), new Vec2i(122, 104),
		        new Vec2i(28, 9),
		        new Vec2i(19, 27),
		        new Vec2i(19, 45),
		        new Vec2i(19, 63),
		        new Vec2i(19, 81),
		        new Vec2i(19, 99),
		        new Vec2i(37, 27),
		        new Vec2i(37, 45),
		        new Vec2i(37, 63),
		        new Vec2i(37, 81),
		        new Vec2i(37, 99),
		        new Vec2i(1, 81),
		        new Vec2i(1, 99),
		        new Vec2i(55, 99),
		        new Vec2i(28, 117),
		        new Vec2i(55, 81),
		        new Vec2i(1, 117),
		        new Vec2i(55, 117),
		        new Vec2i(73, 2),
		        new Vec2i(99, 2),
		        new Vec2i(125, 2)
			));
		}
		if (ConfigGalacticraft.INSTANCE.cargoRocketAssembly.val)
		{
			AssemblyRecipes.registerBlueprint(new NasaWorkbenchAssemblyBlueprint("cargo_rocket", CARGO_ROCKET_BLUEPRINT.get(), 0, CARGO_ROCKET_TEXTURE, Rectangle.ofSize(20, 10, 147, 115), ConfigGalacticraft.INSTANCE.cargoRocketAssemblyFlux.val, () -> GalacticraftRegistry.getCargoRocketRecipes().stream(), new Vec2i(122, 86),
		        new Vec2i(28, 8),
		        new Vec2i(28, 26),
		        new Vec2i(19, 44),
		        new Vec2i(19, 62),
		        new Vec2i(19, 80),
		        new Vec2i(37, 44),
		        new Vec2i(37, 62),
		        new Vec2i(37, 80),
		        new Vec2i(1, 80),
		        new Vec2i(55, 80),
		        new Vec2i(28, 98),
		        new Vec2i(1, 98),
		        new Vec2i(55, 98),
		        new Vec2i(73, 2),
		        new Vec2i(99, 2),
		        new Vec2i(125, 2)
			));
		}
		//asteroids module
		if (ConfigGalacticraft.INSTANCE.tier3RocketAssembly.val)
		{
			AssemblyRecipes.registerBlueprint(new NasaWorkbenchAssemblyBlueprint("tier_3_rocket", TIER_3_ROCKET_BLUEPRINT.get(), 0, TIER_3_ROCKET_TEXTURE, Rectangle.ofSize(20, 10, 147, 134), ConfigGalacticraft.INSTANCE.tier3RocketAssemblyFlux.val, () -> GalacticraftRegistry.getRocketT3Recipes().stream(), new Vec2i(122, 104),
		        new Vec2i(28, 9),
		        new Vec2i(19, 27),
		        new Vec2i(19, 45),
		        new Vec2i(19, 63),
		        new Vec2i(19, 81),
		        new Vec2i(19, 99),
		        new Vec2i(37, 27),
		        new Vec2i(37, 45),
		        new Vec2i(37, 63),
		        new Vec2i(37, 81),
		        new Vec2i(37, 99),
		        new Vec2i(1, 81),
		        new Vec2i(1, 99),
		        new Vec2i(55, 99),
		        new Vec2i(28, 117),
		        new Vec2i(55, 81),
		        new Vec2i(1, 117),
		        new Vec2i(55, 117),
		        new Vec2i(73, 2),
		        new Vec2i(99, 2),
		        new Vec2i(125, 2)
			));
		}
		if (ConfigGalacticraft.INSTANCE.astroMinerAssembly.val)
		{
			AssemblyRecipes.registerBlueprint(new NasaWorkbenchAssemblyBlueprint("astro_miner", ASTRO_MINER_BLUEPRINT.get(), 0, ASTRO_MINER_TEXTURE, Rectangle.ofSize(7, 60, 160, 63), ConfigGalacticraft.INSTANCE.astroMinerAssemblyFlux.val, () -> GalacticraftRegistry.getAstroMinerRecipes().stream(), new Vec2i(135, 38),
		        new Vec2i(20, 1),
		        new Vec2i(38, 1),
		        new Vec2i(56, 1),
		        new Vec2i(74, 1),
		        new Vec2i(9, 19),
		        new Vec2i(27, 19),
		        new Vec2i(45, 19),
		        new Vec2i(63, 19),
		        new Vec2i(81, 19),
		        new Vec2i(37, 37),
		        new Vec2i(55, 37),
		        new Vec2i(73, 37),
		        new Vec2i(1, 43),
		        new Vec2i(19, 43)
			));
		}
	}
}