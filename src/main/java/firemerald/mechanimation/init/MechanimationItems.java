package firemerald.mechanimation.init;

import firemerald.api.core.InitFunctions;
import firemerald.mechanimation.blocks.BlockMetal;
import firemerald.mechanimation.blocks.BlockMetalAlloy;
import firemerald.mechanimation.blocks.BlockOre;
import firemerald.mechanimation.blocks.machine.BlockArcFurnace;
import firemerald.mechanimation.blocks.machine.BlockAssemblyTerminal;
import firemerald.mechanimation.blocks.machine.BlockBlastFurnace;
import firemerald.mechanimation.blocks.machine.BlockCastingTable;
import firemerald.mechanimation.blocks.machine.BlockCatalyticReformer;
import firemerald.mechanimation.blocks.machine.BlockClausPlant;
import firemerald.mechanimation.blocks.machine.BlockDesalter;
import firemerald.mechanimation.blocks.machine.BlockDistillationTower;
import firemerald.mechanimation.blocks.machine.BlockElectrolyzer;
import firemerald.mechanimation.blocks.machine.BlockFluidReactor;
import firemerald.mechanimation.blocks.machine.BlockGenerator;
import firemerald.mechanimation.blocks.machine.BlockHydrotreater;
import firemerald.mechanimation.blocks.machine.BlockMeroxTreater;
import firemerald.mechanimation.blocks.machine.BlockPress;
import firemerald.mechanimation.blocks.machine.BlockPulverizer;
import firemerald.mechanimation.items.ItemBlockSubtyped;
import firemerald.mechanimation.items.ItemCarbonFiberArmor;
import firemerald.mechanimation.items.ItemCraftingMaterial;
import firemerald.mechanimation.items.ItemMachine;
import firemerald.mechanimation.items.ItemMechanimationSlab;
import firemerald.mechanimation.items.ItemVerticalMachine;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

public class MechanimationItems
{
	public static final ItemCraftingMaterial CRAFTING_MATERIAL = new ItemCraftingMaterial();
	//public static final ItemFilledBottle FILLED_BOTTLE = new ItemFilledBottle();
	public static final ItemCarbonFiberArmor CARBON_FIBER_HELMET = new ItemCarbonFiberArmor(EntityEquipmentSlot.HEAD);
	public static final ItemCarbonFiberArmor CARBON_FIBER_CHESTPLATE = new ItemCarbonFiberArmor(EntityEquipmentSlot.CHEST);
	public static final ItemCarbonFiberArmor CARBON_FIBER_LEGGINGS = new ItemCarbonFiberArmor(EntityEquipmentSlot.LEGS);
	public static final ItemCarbonFiberArmor CARBON_FIBER_BOOTS = new ItemCarbonFiberArmor(EntityEquipmentSlot.FEET);
	public static final ItemBlockSubtyped<BlockOre> ORE = new ItemBlockSubtyped<>(MechanimationBlocks.ORE);
	public static final ItemBlockSubtyped<BlockMetal> METAL_BLOCK = new ItemBlockSubtyped<>(MechanimationBlocks.METAL_BLOCK);
	public static final ItemBlockSubtyped<BlockMetalAlloy> METAL_ALLOY_BLOCK = new ItemBlockSubtyped<>(MechanimationBlocks.METAL_ALLOY_BLOCK);
	public static final ItemBlock METAL_GRATE_BLOCK = new ItemBlock(MechanimationBlocks.METAL_GRATE_BLOCK);
	public static final ItemBlock METAL_GRATE = new ItemBlock(MechanimationBlocks.METAL_GRATE);
	public static final ItemMechanimationSlab METAL_GRATE_SLAB = new ItemMechanimationSlab(MechanimationBlocks.METAL_GRATE_SLAB);
	public static final ItemBlock METAL_GRATE_STAIRS = new ItemBlock(MechanimationBlocks.METAL_GRATE_STAIRS);
	public static final ItemBlock STEEL_PANEL = new ItemBlock(MechanimationBlocks.STEEL_PANEL);
	public static final ItemMechanimationSlab STEEL_SLAB = new ItemMechanimationSlab(MechanimationBlocks.STEEL_SLAB);
	public static final ItemBlock STEEL_STAIRS = new ItemBlock(MechanimationBlocks.STEEL_STAIRS);
	public static final ItemBlock TITANIUM_PANEL = new ItemBlock(MechanimationBlocks.TITANIUM_PANEL);
	public static final ItemMechanimationSlab TITANIUM_SLAB = new ItemMechanimationSlab(MechanimationBlocks.TITANIUM_SLAB);
	public static final ItemBlock TITANIUM_STAIRS = new ItemBlock(MechanimationBlocks.TITANIUM_STAIRS);
	public static final ItemBlock ASPHALT = new ItemBlock(MechanimationBlocks.ASPHALT);
	public static final ItemBlock REINFORCED_GLASS = new ItemBlock(MechanimationBlocks.REINFORCED_GLASS);
	public static final ItemBlock REINFORCED_GLASS_PANE = new ItemBlock(MechanimationBlocks.REINFORCED_GLASS_PANE);
	public static final ItemMachine<BlockPress> PRESS = new ItemMachine<>(MechanimationBlocks.PRESS);
	public static final ItemMachine<BlockFluidReactor> FLUID_REACTOR = new ItemMachine<>(MechanimationBlocks.FLUID_REACTOR);
	public static final ItemMachine<BlockElectrolyzer> ELECTROLYZER = new ItemMachine<>(MechanimationBlocks.ELECTROLYZER);
	public static final ItemMachine<BlockHydrotreater> HYDROTREATER = new ItemMachine<>(MechanimationBlocks.HYDROTREATER);
	public static final ItemMachine<BlockDesalter> DESALTER = new ItemMachine<>(MechanimationBlocks.DESALTER);
	public static final ItemMachine<BlockCatalyticReformer> CATALYTIC_REFORMER = new ItemMachine<>(MechanimationBlocks.CATALYTIC_REFORMER);
	public static final ItemVerticalMachine<BlockDistillationTower> DISTILLATION_TOWER = new ItemVerticalMachine<>(MechanimationBlocks.DISTILLATION_TOWER, true);
	public static final ItemMachine<BlockMeroxTreater> MEROX_TREATER = new ItemMachine<>(MechanimationBlocks.MEROX_TREATER);
	public static final ItemMachine<BlockClausPlant> CLAUS_PLANT = new ItemMachine<>(MechanimationBlocks.CLAUS_PLANT);
	public static final ItemMachine<BlockGenerator> GENERATOR = new ItemMachine<>(MechanimationBlocks.GENERATOR);
	public static final ItemMachine<BlockPulverizer> PULVERIZER = new ItemMachine<>(MechanimationBlocks.PULVERIZER);
	public static final ItemVerticalMachine<BlockBlastFurnace> BLAST_FURNACE = new ItemVerticalMachine<>(MechanimationBlocks.BLAST_FURNACE, true);
	public static final ItemMachine<BlockArcFurnace> ARC_FURNACE = new ItemMachine<>(MechanimationBlocks.ARC_FURNACE);
	public static final ItemMachine<BlockCastingTable> CASTING_TABLE = new ItemMachine<>(MechanimationBlocks.CASTING_TABLE);
	public static final ItemMachine<BlockAssemblyTerminal> ASSEMBLY_TERMINAL = new ItemMachine<>(MechanimationBlocks.ASSEMBLY_TERMINAL, true);

	public static void init(IForgeRegistry<Item> registry)
	{
		InitFunctions.addItem(CRAFTING_MATERIAL, "crafting_material", registry);
		InitFunctions.addItem(CARBON_FIBER_HELMET, "carbon_fiber_helmet", registry);
		InitFunctions.addItem(CARBON_FIBER_CHESTPLATE, "carbon_fiber_chestplate", registry);
		InitFunctions.addItem(CARBON_FIBER_LEGGINGS, "carbon_fiber_leggings", registry);
		InitFunctions.addItem(CARBON_FIBER_BOOTS, "carbon_fiber_boots", registry);
		InitFunctions.addItemBlock(ORE, registry);
		InitFunctions.addItemBlock(METAL_BLOCK, registry);
		InitFunctions.addItemBlock(METAL_ALLOY_BLOCK, registry);
		InitFunctions.addItemBlock(METAL_GRATE_BLOCK, registry);
		InitFunctions.addItemBlock(METAL_GRATE, registry);
		InitFunctions.addItemBlock(METAL_GRATE_SLAB, registry);
		InitFunctions.addItemBlock(METAL_GRATE_STAIRS, registry);
		InitFunctions.addItemBlock(STEEL_PANEL, registry);
		InitFunctions.addItemBlock(STEEL_SLAB, registry);
		InitFunctions.addItemBlock(STEEL_STAIRS, registry);
		InitFunctions.addItemBlock(TITANIUM_PANEL, registry);
		InitFunctions.addItemBlock(TITANIUM_SLAB, registry);
		InitFunctions.addItemBlock(TITANIUM_STAIRS, registry);
		InitFunctions.addItemBlock(ASPHALT, registry);
		InitFunctions.addItemBlock(REINFORCED_GLASS, registry);
		InitFunctions.addItemBlock(REINFORCED_GLASS_PANE, registry);
		InitFunctions.addItemBlock(PRESS, registry);
		InitFunctions.addItemBlock(FLUID_REACTOR, registry);
		InitFunctions.addItemBlock(ELECTROLYZER, registry);
		InitFunctions.addItemBlock(HYDROTREATER, registry);
		InitFunctions.addItemBlock(DESALTER, registry);
		InitFunctions.addItemBlock(CATALYTIC_REFORMER, registry);
		InitFunctions.addItemBlock(DISTILLATION_TOWER, registry);
		InitFunctions.addItemBlock(MEROX_TREATER, registry);
		InitFunctions.addItemBlock(CLAUS_PLANT, registry);
		InitFunctions.addItemBlock(GENERATOR, registry);
		InitFunctions.addItemBlock(PULVERIZER, registry);
		InitFunctions.addItemBlock(BLAST_FURNACE, registry);
		InitFunctions.addItemBlock(ARC_FURNACE, registry);
		InitFunctions.addItemBlock(CASTING_TABLE, registry);
		InitFunctions.addItemBlock(ASSEMBLY_TERMINAL, registry);

		ItemCarbonFiberArmor.MATERIAL.setRepairItem(new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.CARBON_FIBER));

		registerOreDict();
	}

    public static void registerOreDict()
    {
    	OreDictionary.registerOre("dustCoal", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.COAL_DUST));
    	OreDictionary.registerOre("dustIron", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.IRON_DUST));
    	OreDictionary.registerOre("dustGold", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.GOLD_DUST));
    	OreDictionary.registerOre("dustNickel", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.NICKEL_DUST));
    	OreDictionary.registerOre("dustCopper", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.COPPER_DUST));
    	OreDictionary.registerOre("dustAluminum", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.ALUMINUM_DUST));
    	OreDictionary.registerOre("dustTin", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.TIN_DUST));
    	OreDictionary.registerOre("dustSilver", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.SILVER_DUST));
    	OreDictionary.registerOre("dustTungsten", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.TUNGSTEN_DUST));
    	OreDictionary.registerOre("dustTitanium", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.TITANIUM_DUST));
    	OreDictionary.registerOre("dustSteel", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.STEEL_DUST));

    	OreDictionary.registerOre("nuggetNickel", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.NICKEL_NUGGET));
    	OreDictionary.registerOre("nuggetCopper", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.COPPER_NUGGET));
    	OreDictionary.registerOre("nuggetAluminum", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.ALUMINUM_NUGGET));
    	OreDictionary.registerOre("nuggetTin", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.TIN_NUGGET));
    	OreDictionary.registerOre("nuggetSilver", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.SILVER_NUGGET));
    	OreDictionary.registerOre("nuggetTungsten", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.TUNGSTEN_NUGGET));
    	OreDictionary.registerOre("nuggetTitanium", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.TITANIUM_NUGGET));
    	OreDictionary.registerOre("nuggetSteel", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.STEEL_NUGGET));

    	OreDictionary.registerOre("ingotNickel", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.NICKEL_INGOT));
    	OreDictionary.registerOre("ingotCopper", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.COPPER_INGOT));
    	OreDictionary.registerOre("ingotAluminum", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.ALUMINUM_INGOT));
    	OreDictionary.registerOre("ingotTin", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.TIN_INGOT));
    	OreDictionary.registerOre("ingotSilver", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.SILVER_INGOT));
    	OreDictionary.registerOre("ingotTungsten", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.TUNGSTEN_INGOT));
    	OreDictionary.registerOre("ingotTitanium", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.TITANIUM_INGOT));
    	OreDictionary.registerOre("ingotSteel", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.STEEL_INGOT));

    	OreDictionary.registerOre("plateIron", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.IRON_PLATE));
    	OreDictionary.registerOre("plateGold", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.GOLD_PLATE));
    	OreDictionary.registerOre("plateNickel", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.NICKEL_PLATE));
    	OreDictionary.registerOre("plateCopper", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.COPPER_PLATE));
    	OreDictionary.registerOre("plateAluminum", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.ALUMINUM_PLATE));
    	OreDictionary.registerOre("plateTin", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.TIN_PLATE));
    	OreDictionary.registerOre("plateSilver", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.SILVER_PLATE));
    	OreDictionary.registerOre("plateTungsten", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.TUNGSTEN_PLATE));
    	OreDictionary.registerOre("plateTitanium", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.TITANIUM_PLATE));
    	OreDictionary.registerOre("plateSteel", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.STEEL_PLATE));

    	OreDictionary.registerOre("itemCircuit", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.CIRCUIT_BOARD));
    	OreDictionary.registerOre("dustSulfur", new ItemStack(CRAFTING_MATERIAL, 1, ItemCraftingMaterial.SULFUR));

    	OreDictionary.registerOre("oreNickel", new ItemStack(ORE, 1, BlockOre.EnumVariant.NICKEL.ordinal()));
    	OreDictionary.registerOre("oreCopper", new ItemStack(ORE, 1, BlockOre.EnumVariant.COPPER.ordinal()));
    	OreDictionary.registerOre("oreAluminum", new ItemStack(ORE, 1, BlockOre.EnumVariant.ALUMINUM.ordinal()));
    	OreDictionary.registerOre("oreTin", new ItemStack(ORE, 1, BlockOre.EnumVariant.TIN.ordinal()));
    	OreDictionary.registerOre("oreSilver", new ItemStack(ORE, 1, BlockOre.EnumVariant.SILVER.ordinal()));
    	OreDictionary.registerOre("oreTungsten", new ItemStack(ORE, 1, BlockOre.EnumVariant.TUNGSTEN.ordinal()));
    	OreDictionary.registerOre("oreTitanium", new ItemStack(ORE, 1, BlockOre.EnumVariant.TITANIUM.ordinal()));
    	OreDictionary.registerOre("oreSulfur", new ItemStack(ORE, 1, BlockOre.EnumVariant.SULFUR.ordinal()));

    	OreDictionary.registerOre("blockNickel", new ItemStack(METAL_BLOCK, 1, BlockMetal.EnumVariant.NICKEL.ordinal()));
    	OreDictionary.registerOre("blockCopper", new ItemStack(METAL_BLOCK, 1, BlockMetal.EnumVariant.COPPER.ordinal()));
    	OreDictionary.registerOre("blockAluminum", new ItemStack(METAL_BLOCK, 1, BlockMetal.EnumVariant.ALUMINUM.ordinal()));
    	OreDictionary.registerOre("blockTin", new ItemStack(METAL_BLOCK, 1, BlockMetal.EnumVariant.TIN.ordinal()));
    	OreDictionary.registerOre("blockSilver", new ItemStack(METAL_BLOCK, 1, BlockMetal.EnumVariant.SILVER.ordinal()));
    	OreDictionary.registerOre("blockTungsten", new ItemStack(METAL_BLOCK, 1, BlockMetal.EnumVariant.TUNGSTEN.ordinal()));
    	OreDictionary.registerOre("blockTitanium", new ItemStack(METAL_BLOCK, 1, BlockMetal.EnumVariant.TITANIUM.ordinal()));

    	OreDictionary.registerOre("blockSteel", new ItemStack(METAL_ALLOY_BLOCK, 1, BlockMetalAlloy.EnumVariant.STEEL.ordinal()));

    	OreDictionary.registerOre("blockGlassColorless", REINFORCED_GLASS);
    	OreDictionary.registerOre("blockGlass", REINFORCED_GLASS);
    	OreDictionary.registerOre("paneGlassColorless", REINFORCED_GLASS_PANE);
    	OreDictionary.registerOre("paneGlass", REINFORCED_GLASS_PANE);
    	OreDictionary.registerOre("blockGlassHardened", REINFORCED_GLASS);
    	OreDictionary.registerOre("paneGlassHardened", REINFORCED_GLASS_PANE);
    }

	@SideOnly(Side.CLIENT)
	public static void registerModels()
	{
		InitFunctions.registerItemModels(CRAFTING_MATERIAL);
		InitFunctions.registerItemModels(CARBON_FIBER_HELMET);
		InitFunctions.registerItemModels(CARBON_FIBER_CHESTPLATE);
		InitFunctions.registerItemModels(CARBON_FIBER_LEGGINGS);
		InitFunctions.registerItemModels(CARBON_FIBER_BOOTS);
		InitFunctions.registerItemBlockModels(ORE);
		InitFunctions.registerItemBlockModels(METAL_BLOCK);
		InitFunctions.registerItemBlockModels(METAL_ALLOY_BLOCK);
		InitFunctions.registerItemBlockModels(METAL_GRATE_BLOCK);
		InitFunctions.registerItemBlockModels(METAL_GRATE);
		InitFunctions.registerItemBlockModels(METAL_GRATE_SLAB);
		InitFunctions.registerItemBlockModels(METAL_GRATE_STAIRS);
		InitFunctions.registerItemBlockModels(STEEL_PANEL);
		InitFunctions.registerItemBlockModels(STEEL_SLAB);
		InitFunctions.registerItemBlockModels(STEEL_STAIRS);
		InitFunctions.registerItemBlockModels(TITANIUM_PANEL);
		InitFunctions.registerItemBlockModels(TITANIUM_SLAB);
		InitFunctions.registerItemBlockModels(TITANIUM_STAIRS);
		InitFunctions.registerItemBlockModels(ASPHALT);
		InitFunctions.registerItemBlockModels(REINFORCED_GLASS);
		InitFunctions.registerItemBlockModels(REINFORCED_GLASS_PANE);
		InitFunctions.registerItemBlockModels(PRESS);
		InitFunctions.registerItemBlockModels(FLUID_REACTOR);
		InitFunctions.registerItemBlockModels(ELECTROLYZER);
		InitFunctions.registerItemBlockModels(HYDROTREATER);
		InitFunctions.registerItemBlockModels(DESALTER);
		InitFunctions.registerItemBlockModels(CATALYTIC_REFORMER);
		InitFunctions.registerItemBlockModels(DISTILLATION_TOWER);
		InitFunctions.registerItemBlockModels(MEROX_TREATER);
		InitFunctions.registerItemBlockModels(CLAUS_PLANT);
		InitFunctions.registerItemBlockModels(GENERATOR);
		InitFunctions.registerItemBlockModels(PULVERIZER);
		InitFunctions.registerItemBlockModels(BLAST_FURNACE);
		InitFunctions.registerItemBlockModels(ARC_FURNACE);
		InitFunctions.registerItemBlockModels(CASTING_TABLE);
		InitFunctions.registerItemBlockModels(ASSEMBLY_TERMINAL);
	}
}