package firemerald.mechanimation.util.crafting;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.google.common.collect.ImmutableSet;

import firemerald.mechanimation.Main;
import firemerald.mechanimation.api.crafting.casting.EnumCastingType;
import firemerald.mechanimation.config.CommonConfig;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictUtil
{
	public static final Map<String, Map<EnumMaterialType, OreDict>> MATERIALS = new LinkedHashMap<>();

	public static void init()
	{
		Map<String, Map<EnumMaterialType, OreDict>> materialsPre = new LinkedHashMap<>();
		List<String> blacklist = CommonConfig.INSTANCE.oreDictBlacklist.val;
		for (String oreName : OreDictionary.getOreNames()) //check all entries
		{
			try
			{
				ItemStack item = OreDictionary.getOres(oreName).stream().filter(stack -> stack != null).findFirst().get(); //get first item
				for (EnumMaterialType type : EnumMaterialType.values()) //check against all types
				{
					try
					{
						String prefix = type.prefixes.stream().filter(str -> oreName.startsWith(str)).findFirst().get(); //get the prefix
						String name = oreName.substring(prefix.length()); //the material name
						if (!blacklist.contains(name)) //is not a blacklisted material
						{
							Map<EnumMaterialType, OreDict> entries = materialsPre.get(name); //get entries for material
							if (entries == null) materialsPre.put(name, entries = new LinkedHashMap<>()); //set if it didn't exist
							entries.put(type, new OreDict(oreName, item)); //add this entry
						}
						break; //no further checking
					}
					catch (NoSuchElementException e) {} //is not this type
				}
			}
			catch (NoSuchElementException e) {} //has no item
		}
		materialsPre.forEach((name, entries) -> {
			if (entries.size() > 1) //has multiple entries
			{
				MATERIALS.put(name, entries);
			}
		});
		Main.logger().debug("detected materials: " + MATERIALS);
	}

	public static enum EnumMaterialType
	{
		INGOT(EnumCastingType.INGOT, 144, true, "ingot"),
		GEM(EnumCastingType.GEM, 144, false, "gem"),
		BLOCK(EnumCastingType.BLOCK, 1296, true, "block"),
		NUGGET(EnumCastingType.NUGGET, 16, true, "nugget"),
		ORE(null, 288, false, "ore"),
		DUST_DIRTY(null, 0, false, "dustDirty"),
		DUST_TINY(null, 16, false, "dustTiny"),
		DUST_SMALL(null, 36, false, "dustSmall"),
		DUST(null, 144, false, "dust"),
		PLATE_DENSE(null, 0, true, "plateDense"),
		PLATE(EnumCastingType.PLATE, 144, true, "plate");

		public final int mbValue;
		public final boolean canSteel;
		public final ImmutableSet<String> prefixes;
		public final EnumCastingType castingType;

		EnumMaterialType(EnumCastingType type, int mbValue, boolean canSteel, String... prefixes)
		{
			this.castingType = type;
			this.mbValue = mbValue;
			this.canSteel = canSteel;
			this.prefixes = ImmutableSet.copyOf(prefixes);
		}
	}

	public static class OreDict
	{
		public final String name;
		public final ItemStack item;

		OreDict(String name, ItemStack item)
		{
			this.name = name;
			this.item = item;
		}

		@Override
		public String toString()
		{
			return name;
		}
	}
}