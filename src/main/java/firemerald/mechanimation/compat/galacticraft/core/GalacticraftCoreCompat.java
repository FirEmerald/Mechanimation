package firemerald.mechanimation.compat.galacticraft.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Joiner;

import firemerald.api.core.IFMLEventHandler;
import firemerald.craftloader.api.CraftingReloadEvent;
import firemerald.mechanimation.api.crafting.assembly_terminal.AssemblyRecipes;
import firemerald.mechanimation.api.util.Rectangle;
import firemerald.mechanimation.api.util.Vec2i;
import firemerald.mechanimation.compat.galacticraft.ConfigGalacticraft;
import firemerald.mechanimation.compat.galacticraft.NasaWorkbenchAssemblyBlueprint;
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.GCBlocks;
import micdoodle8.mods.galacticraft.core.GCItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryModifiable;

public class GalacticraftCoreCompat implements IFMLEventHandler
{
	public static final GalacticraftCoreCompat INSTANCE = new GalacticraftCoreCompat();
	public static final Logger LOGGER = LogManager.getLogger("Mechanimation Galacticraft Core Compat");

	@Override
	public void onPreInitialization(FMLPreInitializationEvent event)
	{
    	MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void onInitialization(FMLInitializationEvent event)
	{
		ConfigGalacticraft.INSTANCE.loadConfig();
		//MechanimationFluids.CONST_HYDROGEN.add(FluidOrGasStack.forFluid(GCFluids.fluidHydrogenGas));
		//MechanimationFluids.CONST_OXYGEN.add(FluidOrGasStack.forFluid(GCFluids.fluidOxygenGas));
	}

	private static final ResourceLocation TIER_1_ROCKET_TEXTURE = new ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/rocketbench.png");
    public static final Supplier<ItemStack> TIER_1_ROCKET_BLUEPRINT = () -> new ItemStack(GCBlocks.nasaWorkbench);
    private static final ResourceLocation MOON_BUGGY_TEXTURE = new ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/buggybench.png");
    public static final Supplier<ItemStack> MOON_BUGGY_BLUEPRINT = () -> new ItemStack(GCItems.schematic, 1, 0);

	@Override
	public void onPostInitialization(FMLPostInitializationEvent event)
	{
		/*
		addGCCompressorRecipe(new ItemStack(MechanimationItems.CRAFTING_MATERIAL, 1, ItemCraftingMaterial.MIRROR), "g", "m", 'g', "paneGlass", 'm', "plateIron", "plateSilver", "plateAluminum");
		addGCCompressorRecipe(new ItemStack(MechanimationItems.CRAFTING_MATERIAL, 1, ItemCraftingMaterial.IRON_PLATE), "i", 'i', "ingotIron");
		addGCCompressorRecipe(new ItemStack(MechanimationItems.CRAFTING_MATERIAL, 1, ItemCraftingMaterial.GOLD_PLATE), "i", 'i', "ingotGold");
		addGCCompressorRecipe(new ItemStack(MechanimationItems.CRAFTING_MATERIAL, 1, ItemCraftingMaterial.NICKEL_PLATE), "i", 'i', "ingotNickel");
		addGCCompressorRecipe(new ItemStack(MechanimationItems.CRAFTING_MATERIAL, 1, ItemCraftingMaterial.COPPER_PLATE), "i", 'i', "ingotCopper");
		addGCCompressorRecipe(new ItemStack(MechanimationItems.CRAFTING_MATERIAL, 1, ItemCraftingMaterial.ALUMINUM_PLATE), "i", 'i', "ingotAluminum");
		addGCCompressorRecipe(new ItemStack(MechanimationItems.CRAFTING_MATERIAL, 1, ItemCraftingMaterial.TIN_PLATE), "i", 'i', "ingotTin");
		addGCCompressorRecipe(new ItemStack(MechanimationItems.CRAFTING_MATERIAL, 1, ItemCraftingMaterial.SILVER_PLATE), "i", 'i', "ingotSilver");
		addGCCompressorRecipe(new ItemStack(MechanimationItems.CRAFTING_MATERIAL, 1, ItemCraftingMaterial.TUNGSTEN_PLATE), "i", 'i', "ingotTungsten");
		addGCCompressorRecipe(new ItemStack(MechanimationItems.CRAFTING_MATERIAL, 1, ItemCraftingMaterial.TITANIUM_PLATE), "i", 'i', "ingotTitanium");
		addGCCompressorRecipe(new ItemStack(MechanimationItems.CRAFTING_MATERIAL, 1, ItemCraftingMaterial.RADION_PLATE), "i", 'i', "ingotRadion");
		addGCCompressorRecipe(new ItemStack(MechanimationItems.CRAFTING_MATERIAL, 1, ItemCraftingMaterial.BENDEZIUM_PLATE), "i", 'i', "ingotBendezium");
		addGCCompressorRecipe(new ItemStack(MechanimationItems.CRAFTING_MATERIAL, 1, ItemCraftingMaterial.DENZIUM_PLATE), "i", 'i', "ingotDenzium");
		addGCCompressorRecipe(new ItemStack(MechanimationItems.CRAFTING_MATERIAL, 1, ItemCraftingMaterial.MALDIUM_PLATE), "i", 'i', "ingotMaldium");
		addGCCompressorRecipe(new ItemStack(MechanimationItems.CRAFTING_MATERIAL, 1, ItemCraftingMaterial.STEEL_PLATE), "i", 'i', "ingotSteel");
		addGCCompressorRecipe(new ItemStack(MechanimationItems.CRAFTING_MATERIAL, 1, ItemCraftingMaterial.TALLORIC_PLATE), "i", 'i', "ingotTalloric");
		addGCCompressorRecipe(new ItemStack(MechanimationItems.CRAFTING_MATERIAL, 1, ItemCraftingMaterial.CORDITE_PLATE), "i", 'i', "ingotCordite");
		addGCCompressorRecipe(new ItemStack(MechanimationItems.CRAFTING_MATERIAL, 1, ItemCraftingMaterial.CHOZODIAN_PLATE), "i", 'i', "ingotChozodian");
		addGCCompressorRecipe(new ItemStack(MechanimationItems.CRAFTING_MATERIAL, 1, ItemCraftingMaterial.RED_CHOZODIAN_PLATE), "i", 'i', "ingotChozodianRed");
		addGCCompressorRecipe(new ItemStack(MechanimationItems.CRAFTING_MATERIAL, 1, ItemCraftingMaterial.ORANGE_CHOZODIAN_PLATE), "i", 'i', "ingotChozodianOrange");
		addGCCompressorRecipe(new ItemStack(MechanimationItems.CRAFTING_MATERIAL, 1, ItemCraftingMaterial.PURPLE_CHOZODIAN_PLATE), "i", 'i', "ingotChozodianPurple");
		*/
	}

	public static final Joiner COMMA_JOINER = Joiner.on(", ");

	/*
	public void addGCCompressorRecipe(ItemStack output, Object... recipe)
	{
		String[] table;
		Set<Character> characters = new HashSet<>();
		int i = 0;
		if (recipe[0] instanceof String[])
		{
			table = (String[]) recipe[0];
			i = 1;
		}
		else
		{
			List<String> list = new ArrayList<>();
			while (recipe[i] instanceof String)
			{
				list.add((String) recipe[i]);
				i++;
			}
			table = list.toArray(new String[list.size()]);
		}
		int width = -1;
		for (String row : table)
		{
			if (width < 0) width = row.length();
			else if (width != row.length())
			{
				LOGGER.warn("Invalid compressor recipe: mismatched recipe table row sizes");
				return;
			}
			for (int j = 0; j < width; j++)
			{
				Character c = Character.valueOf(row.charAt(j));
				if (c.charValue() != ' ' && !characters.contains(c)) characters.add(c);
			}
		}
		if (characters.isEmpty())
		{
			LOGGER.warn("Invalid compressor recipe: recipe is empty");
			return;
		}
		Map<Character, List<ItemStack>> ingredients = new HashMap<>();
		Character c = null;
		List<ItemStack> list = null;
		while (i < recipe.length)
		{
			if (recipe[i] instanceof Character)
			{
				c = (Character) recipe[i];
				if (ingredients.containsKey(c))
				{
					LOGGER.warn("Invalid compressor recipe: duplicate ingredient key: " + recipe[i].toString());
					return;
				}
				else if (!characters.contains(c))
				{
					LOGGER.warn("Invalid compressor recipe: invalid ingredient key: " + recipe[i].toString());
					return;
				}
				else
				{
					ingredients.put(c, list = new ArrayList<>());
					characters.remove(c);
				}
			}
			else if (c == null)
			{
				LOGGER.warn("Invalid compressor recipe: expected a character, got an instance of " + recipe[i].getClass().toString());
				return;
			}
			else
			{
				if (recipe[i] instanceof ItemStack) list.add((ItemStack) recipe[i]);
				else if (recipe[i] instanceof Item) list.add(new ItemStack((Item) recipe[i], 1, 32767));
				else if (recipe[i] instanceof Block) list.add(new ItemStack((Block) recipe[i], 1, 32767));
				else if (recipe[i] instanceof String) list.addAll(OreDictionary.getOres((String) recipe[i]));
				else if (recipe[i] instanceof String[])
				{
					String[] oreNames = (String[]) recipe[i];
					for (String oreName : oreNames) list.addAll(OreDictionary.getOres(oreName));
				}
				else
				{
					LOGGER.warn("Invalid compressor recipe: expected an ItemStack, Item, Block, String, or String Array, got an instance of " + recipe[i].getClass().toString());
					return;
				}
			}
			i++;
		}
		if (!characters.isEmpty())
		{
			LOGGER.warn("Invalid compressor recipe: missing ingredient keys " + COMMA_JOINER.join(characters));
			return;
		}
		char chr = (char) (' ' + 1);
		String[] newTable = new String[table.length];
		Map<Character, List<ItemStack>> newIngredients = new HashMap<>();
		for (int j = 0; j < table.length; j++)
		{
			String row = table[j];
			char[] newRow = new char[row.length()];
			for (int k = 0; k < row.length(); k++)
			{
				char rc = row.charAt(k);
				if (rc == ' ') newRow[k] = rc;
				else
				{
					newIngredients.put(chr, ingredients.get(rc));
					newRow[k] = chr;
					chr++;
				}
			}
			newTable[j] = String.valueOf(newRow);
		}
		Object[] args = new Object[newTable.length + newIngredients.size() * 2];
		System.arraycopy(newTable, 0, args, 0, table.length);
		i = newTable.length;
		for (Character ch : newIngredients.keySet())
		{
			args[i] = ch;
			i += 2;
		}
		processCompressorRecipe(output, args, newIngredients, newTable.length);
	}

	private void processCompressorRecipe(ItemStack output, Object[] args, Map<Character, List<ItemStack>> ingredients, int i)
	{
		List<ItemStack> values = ingredients.get(args[i]);
		i++;
		for (ItemStack value : values)
		{
			args[i] = value;
			if (i + 1 == args.length) CompressorRecipes.addRecipe(output, args);
			else processCompressorRecipe(output, args, ingredients, i + 1);
		}
	}
	*/

	@SubscribeEvent(priority = EventPriority.LOWEST) //lowest priority so we can remove recipes
	public void onRecipeRegistry(RegistryEvent.Register<IRecipe> event)
	{
		if (ConfigGalacticraft.INSTANCE.disableNasaWorkbench.val)
		{
			IForgeRegistry<IRecipe> registry = event.getRegistry();
			if (registry instanceof IForgeRegistryModifiable) //remove all crafting recipes for the nasa workbench
			{
				IForgeRegistryModifiable<IRecipe> modifiable = (IForgeRegistryModifiable<IRecipe>) registry;
				List<IRecipe> toRemove = new ArrayList<>();
				modifiable.forEach(recipe -> {
					if (recipe.getRecipeOutput().getItem() == Item.getItemFromBlock(GCBlocks.nasaWorkbench)) toRemove.add(recipe);
				});
				toRemove.forEach(recipe -> modifiable.remove(recipe.getRegistryName()));
				LOGGER.info("Removed " + toRemove.size() + " NASA workbench recipes.");
			}
			else
			{
				LOGGER.warn("Crafting table recipe registry is not an instance of IForgeRegistryModifiable, could not disable NASA workbench recipe");
			}
		}
	}

	@SubscribeEvent
	public void onCraftingReload(CraftingReloadEvent.Post event)
	{
		if (ConfigGalacticraft.INSTANCE.tier1RocketAssembly.val)
		{
			AssemblyRecipes.registerBlueprint(new NasaWorkbenchAssemblyBlueprint("tier_1_rocket", TIER_1_ROCKET_BLUEPRINT.get(), 0, TIER_1_ROCKET_TEXTURE, Rectangle.ofSize(20, 10, 147, 116), ConfigGalacticraft.INSTANCE.tier1RocketAssemblyFlux.val, () -> GalacticraftRegistry.getRocketT1Recipes().stream(), new Vec2i(122, 86),
				new Vec2i(28, 9),
				new Vec2i(19, 27),
				new Vec2i(19, 45),
				new Vec2i(19, 63),
				new Vec2i(19, 81),
				new Vec2i(37, 27),
				new Vec2i(37, 45),
				new Vec2i(37, 63),
				new Vec2i(37, 81),
				new Vec2i(1, 81),
				new Vec2i(1, 99),
				new Vec2i(28, 99),
				new Vec2i(55, 81),
				new Vec2i(55, 99),
				new Vec2i(73, 2),
				new Vec2i(99, 2),
				new Vec2i(125, 2)
			));
		}
		if (ConfigGalacticraft.INSTANCE.moonBuggyAssembly.val)
		{
			AssemblyRecipes.registerBlueprint(new NasaWorkbenchAssemblyBlueprint("moon_buggy", MOON_BUGGY_BLUEPRINT.get(), 0, MOON_BUGGY_TEXTURE, Rectangle.ofSize(20, 10, 147, 121), ConfigGalacticraft.INSTANCE.moonBuggyAssemblyFlux.val, () -> GalacticraftRegistry.getBuggyBenchRecipes().stream(), new Vec2i(122, 96),
				new Vec2i(19, 31),
				new Vec2i(19, 49),
				new Vec2i(19, 67),
				new Vec2i(19, 85),
				new Vec2i(37, 31),
				new Vec2i(37, 49),
				new Vec2i(37, 67),
				new Vec2i(37, 85),
				new Vec2i(55, 31),
				new Vec2i(55, 49),
				new Vec2i(55, 67),
				new Vec2i(55, 85),
				new Vec2i(1, 31),
				new Vec2i(1, 85),
				new Vec2i(73, 31),
				new Vec2i(73, 85),
				new Vec2i(73, 2),
				new Vec2i(99, 2),
				new Vec2i(125, 2)
			));
		}
	}
}