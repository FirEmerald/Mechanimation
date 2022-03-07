package firemerald.mechanimation.config;

import java.util.LinkedHashMap;
import java.util.Map;

import firemerald.api.config.Category;
import firemerald.api.config.Config;
import firemerald.api.config.ConfigValueBase;
import firemerald.api.config.ConfigValueInt;
import firemerald.api.config.ConfigValueOreGen;
import firemerald.api.config.ConfigValueStringArray;
import firemerald.api.config.ConfigValueStringList;
import firemerald.mechanimation.Main;
import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.util.BlockStateEntry;
import firemerald.mechanimation.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Property;

public class CommonConfig extends Config
{
	public static final CommonConfig INSTANCE = new CommonConfig();

	public static class ConfigValueThermalBlocks extends ConfigValueBase
	{
		public final Map<Block, Map<BlockStateEntry, Float>> val = new LinkedHashMap<>();
		public final Map<Block, Map<BlockStateEntry, Float>> def = new LinkedHashMap<>();
		public final String[] defStrings;
		public final String comment;

		public ConfigValueThermalBlocks(Category category, String name, String comment)
		{
			super(category, name, comment);
			put(def, new BlockStateEntry(Blocks.ICE), 0);
			put(def, new BlockStateEntry(Blocks.PACKED_ICE), -10);
			put(def, new BlockStateEntry(Blocks.LIT_FURNACE), 220);
			put(def, new BlockStateEntry(Blocks.LIT_PUMPKIN), 52);
			put(def, new BlockStateEntry(Blocks.TORCH), 52);
			put(def, new BlockStateEntry(Blocks.FIRE), 600);
			put(def, new BlockStateEntry(Blocks.PORTAL), 217);
			put(def, new BlockStateEntry(Blocks.MAGMA), 141);
			//put(def, new BlockStateEntry(MechanimationBlocks.LAVA_STONE), 141); TODO
			this.val.putAll(def);
			defStrings = getStrings(def);
			this.comment = comment + " [default: " + ConfigValueStringArray.makeString(defStrings) + "]";
		}

		public void put(Map<Block, Map<BlockStateEntry, Float>> entries, BlockStateEntry entry, float temp)
		{
			Map<BlockStateEntry, Float> map = entries.get(entry.block);
			if (map == null) entries.put(entry.block, map = new LinkedHashMap<>());
			map.put(entry, temp);
		}

		public String[] getStrings(Map<Block, Map<BlockStateEntry, Float>> entries)
		{
			return entries.values().stream().flatMap(blockEntry -> blockEntry.entrySet().stream()).map(entry -> entry.getKey().toString() + ", " + entry.getValue().toString()).toArray(String[]::new);
		}

		public Property getProperty()
		{
			return config.get(category, name, defStrings, comment);
		}

		@Override
		public void loadConfig()
		{
			val.clear();
			String[] array = getProperty().getStringList();
			for (String string : array)
			{
				if (string.trim().startsWith("#")) continue;
				int split = string.lastIndexOf(',');
				if (split < 0) Main.logger().warn("Invalid blockstate-temperature mapping, missing state-temperature seperator ',': " + string);
				else
				{
					String stateString = string.substring(0, split).trim();
					String tempString = string.substring(split + 1).trim();
					BlockStateEntry blockState = Utils.getBlockStateEntry(stateString);
					if (blockState == null) Main.logger().warn("Invalid blockstate-temperature mapping, invalid block state string: " + string);
					else
					{
						try
						{
							float temp = Float.parseFloat(tempString);
							put(val, blockState, temp);
						}
						catch (NumberFormatException e)
						{
							Main.logger().warn("Invalid blockstate-temperature mapping, invalid temperature: " + string);
						}
					}
				}
			}

		}

		@Override
		public void saveConfig()
		{
			getProperty().set(getStrings(val));
		}

		@Override
		public void loadNBT(NBTTagCompound tag)
		{
			val.clear();
			NBTTagCompound values = tag.getCompoundTag(name);
			if (values == null) def.forEach((key, val) -> this.val.put(key, val));
			else values.getKeySet().forEach(key -> {
				BlockStateEntry blockState = Utils.getBlockStateEntry(key);
				if (blockState == null) Main.logger().warn("Invalid blockstate-temperature mapping, invalid block state string: " + key);
				else put(val, blockState, values.getFloat(key));
			});
		}

		@Override
		public void saveNBT(NBTTagCompound tag)
		{
			NBTTagCompound values = new NBTTagCompound();
			val.values().stream().flatMap(blockEntry -> blockEntry.entrySet().stream()).forEach(entry -> values.setFloat(entry.getKey().toString(), entry.getValue()));
			tag.setTag(name, values);
		}
	}

	public final ConfigValueOreGen overworldNickel, overworldCopper, overworldAluminum, overworldTin, overworldSilver, overworldTungsten, overworldTitanium, overworldRadion, overworldBendezium, overworldDenzium, overworldMaldium, overworldDarkCrystal, overworldLightCrystal, overworldSulfur;
	public final ConfigValueStringList oreDictBlacklist;
	public final ConfigValueThermalBlocks thermalBlocks;
	public final ConfigValueInt autoCraftingRF;

	public CommonConfig()
	{
		super(new ResourceLocation(MechanimationAPI.MOD_ID, "common.cfg"));
		Category world = new Category(this, "world", "world options");
		overworldNickel = new ConfigValueOreGen(world, "nickel", true, 4, 2, 5, 20);
		overworldCopper = new ConfigValueOreGen(world, "copper", true, 8, 8, 40, 75);
		overworldAluminum = new ConfigValueOreGen(world, "aluminum", true, 8, 8, 40, 75);
		overworldTin = new ConfigValueOreGen(world, "tin", true, 8, 7, 20, 55);
		overworldSilver = new ConfigValueOreGen(world, "silver", true, 7, 4, 5, 30);
		overworldTungsten = new ConfigValueOreGen(world, "tungsten", true, 8, 2, 0, 16);
		overworldTitanium = new ConfigValueOreGen(world, "titanium", true, 8, 1.125, 0, 16);
		overworldRadion = new ConfigValueOreGen(world, "radion", true, 8, 5, 8, 50);
		overworldBendezium = new ConfigValueOreGen(world, "bendezium", false, 3, 2.25f, 0, 16);
		overworldDenzium = new ConfigValueOreGen(world, "denzium", false, 3, 2.25f, 0, 16);
		overworldMaldium = new ConfigValueOreGen(world, "maldium", false, 1, .625f, 0, 8);
		overworldDarkCrystal = new ConfigValueOreGen(world, "dark_crystal", "dark crystal", false, 5, 24, 0, 96);
		overworldLightCrystal = new ConfigValueOreGen(world, "light_crystal", "light crystal", false, 5, 24, 0, 96);
		overworldSulfur = new ConfigValueOreGen(world, "sulfur", "sulfur", false, 5, 3, 0, 12);
		Category global = new Category(this, "machines", "Global machine options");
		oreDictBlacklist = new ConfigValueStringList(global, "ore_dict_blacklist", new String[] {
				"Redstone",
				"Lapis",
				"Coal",
				"Quartz",
				"Glowstone",
				"Sulfur",
				"Sulphur"}, "Blacklist for automatic ore dictionary machine recipe materials");
		Category crafting = new Category(this, "Crafting", "Crafting options");
		autoCraftingRF = new ConfigValueInt(crafting, "auto_crafting_rf", 100, -1, Integer.MAX_VALUE, "RF required to craft a single item in the assembly table using crafting table recipes (crafting table as blueprint). set to 0 for instant, -1 to disable.");
		Category blocks = new Category(this, "Blocks", "Block options");
		thermalBlocks = new ConfigValueThermalBlocks(blocks, "thermal_blocks", "List of blocks and temperatures. format is 'blockstate, temp', where temp is in degrees Celsius. Incomplete blockstates will ignore properies not present in the given state.");
	}
}