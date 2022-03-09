package firemerald.mechanimation.init;

import firemerald.api.core.InitFunctions;
import firemerald.mechanimation.blocks.BlockAsphalt;
import firemerald.mechanimation.blocks.BlockCustomFluid;
import firemerald.mechanimation.blocks.BlockMechanimationSlab;
import firemerald.mechanimation.blocks.BlockMechanimationStairs;
import firemerald.mechanimation.blocks.BlockMetal;
import firemerald.mechanimation.blocks.BlockMetalAlloy;
import firemerald.mechanimation.blocks.BlockMetalGrate;
import firemerald.mechanimation.blocks.BlockOre;
import firemerald.mechanimation.blocks.BlockPanel;
import firemerald.mechanimation.blocks.BlockReinforcedGlass;
import firemerald.mechanimation.blocks.machine.BlockArcFurnace;
import firemerald.mechanimation.blocks.machine.BlockAssemblyTerminal;
import firemerald.mechanimation.blocks.machine.BlockAssemblyTerminalMultiblock;
import firemerald.mechanimation.blocks.machine.BlockBlastFurnace;
import firemerald.mechanimation.blocks.machine.BlockBlastFurnaceMultiblock;
import firemerald.mechanimation.blocks.machine.BlockCastingTable;
import firemerald.mechanimation.blocks.machine.BlockCatalyticReformer;
import firemerald.mechanimation.blocks.machine.BlockClausPlant;
import firemerald.mechanimation.blocks.machine.BlockDesalter;
import firemerald.mechanimation.blocks.machine.BlockDistillationTower;
import firemerald.mechanimation.blocks.machine.BlockDistillationTowerMultiblock;
import firemerald.mechanimation.blocks.machine.BlockElectrolyzer;
import firemerald.mechanimation.blocks.machine.BlockFluidReactor;
import firemerald.mechanimation.blocks.machine.BlockGenerator;
import firemerald.mechanimation.blocks.machine.BlockHydrotreater;
import firemerald.mechanimation.blocks.machine.BlockMeroxTreater;
import firemerald.mechanimation.blocks.machine.BlockPress;
import firemerald.mechanimation.blocks.machine.BlockPulverizer;
import firemerald.mechanimation.compat.tconstruct.CompatProviderTConstruct;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.registries.IForgeRegistry;

public class MechanimationBlocks
{
	public static final BlockOre ORE = new BlockOre(MapColor.STONE);
	public static final IBlockState NICKEL_ORE = ORE.getDefaultState().withProperty(BlockOre.VARIANT, BlockOre.EnumVariant.NICKEL);
	public static final IBlockState COPPER_ORE = ORE.getDefaultState().withProperty(BlockOre.VARIANT, BlockOre.EnumVariant.COPPER);
	public static final IBlockState ALUMINUM_ORE = ORE.getDefaultState().withProperty(BlockOre.VARIANT, BlockOre.EnumVariant.ALUMINUM);
	public static final IBlockState TIN_ORE = ORE.getDefaultState().withProperty(BlockOre.VARIANT, BlockOre.EnumVariant.TIN);
	public static final IBlockState SILVER_ORE = ORE.getDefaultState().withProperty(BlockOre.VARIANT, BlockOre.EnumVariant.SILVER);
	public static final IBlockState TUNGSTEN_ORE = ORE.getDefaultState().withProperty(BlockOre.VARIANT, BlockOre.EnumVariant.TUNGSTEN);
	public static final IBlockState TITANIUM_ORE = ORE.getDefaultState().withProperty(BlockOre.VARIANT, BlockOre.EnumVariant.TITANIUM);
	public static final IBlockState SULFUR_ORE = ORE.getDefaultState().withProperty(BlockOre.VARIANT, BlockOre.EnumVariant.SULFUR);
	public static final BlockMetal METAL_BLOCK = new BlockMetal();
	public static final IBlockState NICKEL_BLOCK = METAL_BLOCK.getDefaultState().withProperty(BlockMetal.VARIANT, BlockMetal.EnumVariant.NICKEL);
	public static final IBlockState COPPER_BLOCK = METAL_BLOCK.getDefaultState().withProperty(BlockMetal.VARIANT, BlockMetal.EnumVariant.COPPER);
	public static final IBlockState ALUMINUM_BLOCK = METAL_BLOCK.getDefaultState().withProperty(BlockMetal.VARIANT, BlockMetal.EnumVariant.ALUMINUM);
	public static final IBlockState TIN_BLOCK = METAL_BLOCK.getDefaultState().withProperty(BlockMetal.VARIANT, BlockMetal.EnumVariant.TIN);
	public static final IBlockState SILVER_BLOCK = METAL_BLOCK.getDefaultState().withProperty(BlockMetal.VARIANT, BlockMetal.EnumVariant.SILVER);
	public static final IBlockState TITANIUM_BLOCK = METAL_BLOCK.getDefaultState().withProperty(BlockMetal.VARIANT, BlockMetal.EnumVariant.TITANIUM);
	public static final BlockMetalAlloy METAL_ALLOY_BLOCK = new BlockMetalAlloy();
	public static final IBlockState STEEL_BLOCK = METAL_ALLOY_BLOCK.getDefaultState().withProperty(BlockMetalAlloy.VARIANT, BlockMetalAlloy.EnumVariant.STEEL);
	public static final BlockMetalGrate METAL_GRATE_BLOCK = new BlockMetalGrate();
	public static final BlockPanel METAL_GRATE = new BlockPanel(METAL_GRATE_BLOCK.getDefaultState(), true);
	public static final BlockMechanimationSlab METAL_GRATE_SLAB = new BlockMechanimationSlab(METAL_GRATE_BLOCK.getDefaultState());
	public static final BlockMechanimationStairs METAL_GRATE_STAIRS = new BlockMechanimationStairs(METAL_GRATE_BLOCK.getDefaultState());
	public static final BlockPanel STEEL_PANEL = new BlockPanel(STEEL_BLOCK, true);
	public static final BlockMechanimationSlab STEEL_SLAB = new BlockMechanimationSlab(STEEL_BLOCK);
	public static final BlockMechanimationStairs STEEL_STAIRS = new BlockMechanimationStairs(STEEL_BLOCK);
	public static final BlockPanel TITANIUM_PANEL = new BlockPanel(TITANIUM_BLOCK, true);
	public static final BlockMechanimationSlab TITANIUM_SLAB = new BlockMechanimationSlab(TITANIUM_BLOCK);
	public static final BlockMechanimationStairs TITANIUM_STAIRS = new BlockMechanimationStairs(TITANIUM_BLOCK);
	public static final BlockAsphalt ASPHALT = new BlockAsphalt();
	public static final BlockReinforcedGlass REINFORCED_GLASS = new BlockReinforcedGlass();
	public static final BlockPanel REINFORCED_GLASS_PANE = new BlockPanel(REINFORCED_GLASS.getDefaultState(), true);
	//TODO sandstone wall states
	public static final BlockPress PRESS = new BlockPress();
	public static final BlockFluidReactor FLUID_REACTOR = new BlockFluidReactor();
	public static final BlockElectrolyzer ELECTROLYZER = new BlockElectrolyzer();
	public static final BlockHydrotreater HYDROTREATER = new BlockHydrotreater();
	public static final BlockDesalter DESALTER = new BlockDesalter();
	public static final BlockCatalyticReformer CATALYTIC_REFORMER = new BlockCatalyticReformer();
	public static final BlockDistillationTower DISTILLATION_TOWER = new BlockDistillationTower();
	public static final BlockDistillationTowerMultiblock<BlockDistillationTower> DISTILLATION_TOWER_MULTIBLOCK = new BlockDistillationTowerMultiblock<>(DISTILLATION_TOWER);
	public static final BlockMeroxTreater MEROX_TREATER = new BlockMeroxTreater();
	public static final BlockClausPlant CLAUS_PLANT = new BlockClausPlant();
	public static final BlockGenerator GENERATOR = new BlockGenerator();
	public static final BlockPulverizer PULVERIZER = new BlockPulverizer();
	public static final BlockBlastFurnace BLAST_FURNACE = new BlockBlastFurnace();
	public static final BlockBlastFurnaceMultiblock<BlockBlastFurnace> BLAST_FURNACE_MULTIBLOCK = new BlockBlastFurnaceMultiblock<>(BLAST_FURNACE);
	public static final BlockArcFurnace ARC_FURNACE = new BlockArcFurnace();
	public static final BlockCastingTable CASTING_TABLE = new BlockCastingTable();
	public static final BlockAssemblyTerminal ASSEMBLY_TERMINAL = new BlockAssemblyTerminal();
	public static final BlockAssemblyTerminalMultiblock<BlockAssemblyTerminal> ASSEMBLY_TERMINAL_MULTIBLOCK = new BlockAssemblyTerminalMultiblock<>(ASSEMBLY_TERMINAL);

	public static final BlockCustomFluid
	LIQUID_REDSTONE = new BlockCustomFluid(MechanimationFluids.LIQUID_REDSTONE, Material.LAVA),
	LIQUID_TUNGSTEN = new BlockCustomFluid(MechanimationFluids.LIQUID_TUNGSTEN, Material.LAVA),
	LIQUID_TITANIUM = new BlockCustomFluid(MechanimationFluids.LIQUID_TITANIUM, Material.LAVA);

	public static Block
	liquidIron,
	liquidGold,
	liquidNickel,
	liquidCopper,
	liquidAluminum,
	liquidTin,
	liquidSilver,
	liquidSteel;

	public static void preInit() {} //this is used to create the blocks before TC has a chance to see the fluids

	public static void init(IForgeRegistry<Block> registry)
	{
		InitFunctions.addBlock(ORE, "ore", registry);
		InitFunctions.addBlock(METAL_BLOCK, "metal_block", registry);
		InitFunctions.addBlock(METAL_ALLOY_BLOCK, "metal_alloy_block", registry);
		InitFunctions.addBlock(METAL_GRATE_BLOCK, "metal_grate_block", registry);
		InitFunctions.addBlock(METAL_GRATE, "metal_grate_panel", registry);
		InitFunctions.addBlock(METAL_GRATE_SLAB, "metal_grate_slab", registry);
		InitFunctions.addBlock(METAL_GRATE_STAIRS, "metal_grate_stairs", registry);
		InitFunctions.addBlock(STEEL_PANEL, "steel_panel", registry);
		InitFunctions.addBlock(STEEL_SLAB, "steel_slab", registry);
		InitFunctions.addBlock(STEEL_STAIRS, "steel_stairs", registry);
		InitFunctions.addBlock(TITANIUM_PANEL, "titanium_panel", registry);
		InitFunctions.addBlock(TITANIUM_SLAB, "titanium_slab", registry);
		InitFunctions.addBlock(TITANIUM_STAIRS, "titanium_stairs", registry);
		InitFunctions.addBlock(ASPHALT, "asphalt", registry);
		InitFunctions.addBlock(REINFORCED_GLASS, "reinforced_glass", registry);
		InitFunctions.addBlock(REINFORCED_GLASS_PANE, "reinforced_glass_pane", registry);
		InitFunctions.addBlock(PRESS, "press", registry);
		InitFunctions.addBlock(FLUID_REACTOR, "fluid_reactor", registry);
		InitFunctions.addBlock(ELECTROLYZER, "electrolyzer", registry);
		InitFunctions.addBlock(HYDROTREATER, "hydrotreater", registry);
		InitFunctions.addBlock(DESALTER, "desalter", registry);
		InitFunctions.addBlock(CATALYTIC_REFORMER, "catalytic_reformer", registry);
		InitFunctions.addBlock(DISTILLATION_TOWER, "distillation_tower", registry); //TODO
		InitFunctions.addBlock(DISTILLATION_TOWER_MULTIBLOCK, "distillation_tower_multiblock", registry); //TODO
		InitFunctions.addBlock(MEROX_TREATER, "merox_treater", registry);
		InitFunctions.addBlock(CLAUS_PLANT, "claus_plant", registry);
		InitFunctions.addBlock(GENERATOR, "generator", registry);
		InitFunctions.addBlock(PULVERIZER, "pulverizer", registry);
		InitFunctions.addBlock(BLAST_FURNACE, "blast_furnace", registry);
		InitFunctions.addBlock(BLAST_FURNACE_MULTIBLOCK, "blast_furnace_multiblock", registry);
		InitFunctions.addBlock(ARC_FURNACE, "arc_furnace", registry);
		InitFunctions.addBlock(CASTING_TABLE, "casting_table", registry);
		InitFunctions.addBlock(ASSEMBLY_TERMINAL, "assembly_terminal", registry);
		InitFunctions.addBlock(ASSEMBLY_TERMINAL_MULTIBLOCK, "assembly_terminal_multiblock", registry);

		InitFunctions.addBlock(LIQUID_REDSTONE, "liquid_redstone", registry);
		InitFunctions.addBlock(LIQUID_TUNGSTEN, "liquid_tungsten", registry);
		InitFunctions.addBlock(LIQUID_TITANIUM, "liquid_titanium", registry);
		if (!CompatProviderTConstruct.INSTANCE.isPresent())
		{
			InitFunctions.addBlock(liquidIron = new BlockCustomFluid(MechanimationFluids.liquidIron, Material.LAVA), "liquid_iron", registry);
			InitFunctions.addBlock(liquidGold = new BlockCustomFluid(MechanimationFluids.liquidGold, Material.LAVA), "liquid_gold", registry);
			InitFunctions.addBlock(liquidNickel = new BlockCustomFluid(MechanimationFluids.liquidNickel, Material.LAVA), "liquid_nickel", registry);
			InitFunctions.addBlock(liquidCopper = new BlockCustomFluid(MechanimationFluids.liquidCopper, Material.LAVA), "liquid_copper", registry);
			InitFunctions.addBlock(liquidAluminum = new BlockCustomFluid(MechanimationFluids.liquidAluminum, Material.LAVA), "liquid_aluminum", registry);
			InitFunctions.addBlock(liquidTin = new BlockCustomFluid(MechanimationFluids.liquidTin, Material.LAVA), "liquid_tin", registry);
			InitFunctions.addBlock(liquidSilver = new BlockCustomFluid(MechanimationFluids.liquidSilver, Material.LAVA), "liquid_silver", registry);
			InitFunctions.addBlock(liquidSteel = new BlockCustomFluid(MechanimationFluids.liquidSteel, Material.LAVA), "liquid_steel", registry);
		}
	}
}