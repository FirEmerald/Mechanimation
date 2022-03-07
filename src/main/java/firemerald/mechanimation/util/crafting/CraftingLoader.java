package firemerald.mechanimation.util.crafting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import firemerald.craftloader.api.CraftLoaderAPI;
import firemerald.craftloader.api.CraftingUtil;
import firemerald.craftloader.api.SizedIngredient;
import firemerald.mechanimation.Main;
import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.arc_furnace.ArcFurnaceRecipe;
import firemerald.mechanimation.api.crafting.arc_furnace.ArcFurnaceRecipes;
import firemerald.mechanimation.api.crafting.arc_furnace.IArcFurnaceRecipe;
import firemerald.mechanimation.api.crafting.assembly_terminal.AssemblyBlueprint;
import firemerald.mechanimation.api.crafting.assembly_terminal.AssemblyRecipes;
import firemerald.mechanimation.api.crafting.assembly_terminal.IAssemblyBlueprint;
import firemerald.mechanimation.api.crafting.assembly_terminal.IAssemblyRecipe;
import firemerald.mechanimation.api.crafting.casting.CastingRecipe;
import firemerald.mechanimation.api.crafting.casting.CastingRecipes;
import firemerald.mechanimation.api.crafting.casting.EnumCastingType;
import firemerald.mechanimation.api.crafting.casting.ICastingRecipe;
import firemerald.mechanimation.api.crafting.catalytic_reformer.CatalyticReformerRecipes;
import firemerald.mechanimation.api.crafting.catalytic_reformer.ICatalyticReformerRecipe;
import firemerald.mechanimation.api.crafting.claus_plant.ClausPlantRecipes;
import firemerald.mechanimation.api.crafting.claus_plant.IClausPlantRecipe;
import firemerald.mechanimation.api.crafting.desalter.DesalterRecipes;
import firemerald.mechanimation.api.crafting.desalter.IDesalterRecipe;
import firemerald.mechanimation.api.crafting.distillation.DistillationRecipes;
import firemerald.mechanimation.api.crafting.distillation.IDistillationRecipe;
import firemerald.mechanimation.api.crafting.electrolyzer.ElectrolyzerRecipes;
import firemerald.mechanimation.api.crafting.electrolyzer.IElectrolyzerRecipe;
import firemerald.mechanimation.api.crafting.fluid_reactor.FluidReactorRecipes;
import firemerald.mechanimation.api.crafting.fluid_reactor.IFluidReactorRecipe;
import firemerald.mechanimation.api.crafting.generator.combustion.CombustionGeneratorRecipes;
import firemerald.mechanimation.api.crafting.generator.combustion.ICombustionGeneratorRecipe;
import firemerald.mechanimation.api.crafting.hydrotreater.HydrotreaterRecipes;
import firemerald.mechanimation.api.crafting.hydrotreater.IHydrotreaterRecipe;
import firemerald.mechanimation.api.crafting.merox_treater.IMeroxTreaterRecipe;
import firemerald.mechanimation.api.crafting.merox_treater.MeroxTreaterRecipes;
import firemerald.mechanimation.api.crafting.press.IPressRecipe;
import firemerald.mechanimation.api.crafting.press.PressRecipe;
import firemerald.mechanimation.api.crafting.press.PressRecipes;
import firemerald.mechanimation.api.crafting.pulverizer.IPulverizerRecipe;
import firemerald.mechanimation.api.crafting.pulverizer.PulverizerRecipe;
import firemerald.mechanimation.api.crafting.pulverizer.PulverizerRecipes;
import firemerald.mechanimation.api.util.APIUtils;
import firemerald.mechanimation.config.CommonConfig;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasRegistry;
import mekanism.api.gas.GasStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreIngredient;

public class CraftingLoader
{
	public static void initLoaders()
	{
		CraftingHelper.register(new ResourceLocation(MechanimationAPI.MOD_ID, "ingredient_exists"),  (IConditionFactory) (context, json) -> {
			JsonElement ingredient = CraftLoaderAPI.getJsonElement(json, "ingredient");
			return () -> {
				try
				{
					Ingredient ing = CraftingUtil.getIngredient(ingredient, context);
					return ing != null && ing.getMatchingStacks().length > 0;
				}
				catch (JsonParseException e)
				{
					Main.LOGGER.debug("Ingredient condition failed due to error", e);
					return false;
				}
			};
		});
		CraftingHelper.register(new ResourceLocation(MechanimationAPI.MOD_ID, "fluid_exists"),  (IConditionFactory) (context, json) -> {
			String fluidName = JsonUtils.getString(json, "fluid");
			return () -> FluidRegistry.isFluidRegistered(fluidName);
		});
		CraftingHelper.register(new ResourceLocation(MechanimationAPI.MOD_ID, "gas_exists"),  (IConditionFactory) (context, json) -> {
			String gasName = JsonUtils.getString(json, "gas");
			return () -> GasRegistry.containsGas(gasName);
		});
		CraftLoaderAPI.registerLoader(new ResourceLocation(MechanimationAPI.MOD_ID, "press"), PressRecipes::initRecipes, (BiConsumer<ResourceLocation, IPressRecipe>) PressRecipes::registerRecipe, PressRecipes::disableRecipe, disabled -> {
        	Main.logger().debug("Generating automatic ore-dictionary press recipes");
        	OreDictUtil.MATERIALS.forEach((name, entries) -> {
        		OreDictUtil.OreDict plate = entries.get(OreDictUtil.EnumMaterialType.PLATE);
        		if (plate != null)
        		{
        			OreDictUtil.OreDict material = entries.get(OreDictUtil.EnumMaterialType.INGOT);
        			if (material != null) //recipe for material
        			{
        				ResourceLocation recipeName = new ResourceLocation(MechanimationAPI.MOD_ID, name + "_ingot_to_" + name + "_plate");
        				if (!disabled.contains(recipeName) && !PressRecipes.hasRecipe(recipeName))
        				{
            				Main.logger().debug("automatically adding ingot -> plate press recipe for " + name + " with name " + recipeName);
            				ItemStack copy = plate.item.copy();
            				copy.setCount(1);
            				PressRecipes.registerRecipe(recipeName, new PressRecipe(new SizedIngredient(new OreIngredient(material.name), 1), copy, 4000));
        				}
        			}
        			material = entries.get(OreDictUtil.EnumMaterialType.GEM);
        			if (material != null) //recipe for material
        			{
        				ResourceLocation recipeName = new ResourceLocation(MechanimationAPI.MOD_ID, name + "_gem_to_" + name + "_plate");
        				if (!disabled.contains(recipeName) && !PressRecipes.hasRecipe(recipeName))
        				{
            				Main.logger().debug("automatically adding gem -> plate press recipe for " + name + " with name " + recipeName);
            				ItemStack copy = plate.item.copy();
            				copy.setCount(1);
            				PressRecipes.registerRecipe(recipeName, new PressRecipe(new SizedIngredient(new OreIngredient(material.name), 1), copy, 4000));
        				}
        			}
        		}
        	});
		});
		CraftLoaderAPI.registerLoader(new ResourceLocation(MechanimationAPI.MOD_ID, "fluid_reactor"), FluidReactorRecipes::initRecipes, (BiConsumer<ResourceLocation, IFluidReactorRecipe>) FluidReactorRecipes::registerRecipe, FluidReactorRecipes::disableRecipe);
		CraftLoaderAPI.registerLoader(new ResourceLocation(MechanimationAPI.MOD_ID, "electrolyzer"), ElectrolyzerRecipes::initRecipes, (BiConsumer<ResourceLocation, IElectrolyzerRecipe>) ElectrolyzerRecipes::registerRecipe, ElectrolyzerRecipes::disableRecipe);
		CraftLoaderAPI.registerLoader(new ResourceLocation(MechanimationAPI.MOD_ID, "hydrotreater"), HydrotreaterRecipes::initRecipes, (BiConsumer<ResourceLocation, IHydrotreaterRecipe>) HydrotreaterRecipes::registerRecipe, HydrotreaterRecipes::disableRecipe);
		CraftLoaderAPI.registerLoader(new ResourceLocation(MechanimationAPI.MOD_ID, "desalter"), DesalterRecipes::initRecipes, (BiConsumer<ResourceLocation, IDesalterRecipe>) DesalterRecipes::registerRecipe, DesalterRecipes::disableRecipe);
		CraftLoaderAPI.registerLoader(new ResourceLocation(MechanimationAPI.MOD_ID, "catalytic_reformer"), CatalyticReformerRecipes::initRecipes, (BiConsumer<ResourceLocation, ICatalyticReformerRecipe>) CatalyticReformerRecipes::registerRecipe, CatalyticReformerRecipes::disableRecipe);
		CraftLoaderAPI.registerLoader(new ResourceLocation(MechanimationAPI.MOD_ID, "distillation_tower"), DistillationRecipes::initRecipes, (BiConsumer<ResourceLocation, IDistillationRecipe>) DistillationRecipes::registerRecipe, DistillationRecipes::disableRecipe);
		CraftLoaderAPI.registerLoader(new ResourceLocation(MechanimationAPI.MOD_ID, "merox_treater"), MeroxTreaterRecipes::initRecipes, (BiConsumer<ResourceLocation, IMeroxTreaterRecipe>) MeroxTreaterRecipes::registerRecipe, MeroxTreaterRecipes::disableRecipe);
		CraftLoaderAPI.registerLoader(new ResourceLocation(MechanimationAPI.MOD_ID, "claus_plant"), ClausPlantRecipes::initRecipes, (BiConsumer<ResourceLocation, IClausPlantRecipe>) ClausPlantRecipes::registerRecipe, ClausPlantRecipes::disableRecipe);
		CraftLoaderAPI.registerLoader(new ResourceLocation(MechanimationAPI.MOD_ID, "combustion_generator"), CombustionGeneratorRecipes::initRecipes, (BiConsumer<ResourceLocation, ICombustionGeneratorRecipe>) CombustionGeneratorRecipes::registerRecipe, CombustionGeneratorRecipes::disableRecipe);
		CraftLoaderAPI.registerLoader(new ResourceLocation(MechanimationAPI.MOD_ID, "pulverizer"), PulverizerRecipes::initRecipes, (BiConsumer<ResourceLocation, IPulverizerRecipe>) PulverizerRecipes::registerRecipe, PulverizerRecipes::disableRecipe, disabled -> {
        	Main.logger().debug("Generating automatic ore-dictionary pulverizer recipes");
        	OreDictUtil.MATERIALS.forEach((name, entries) -> {
    			OreDictUtil.OreDict gem = entries.get(OreDictUtil.EnumMaterialType.GEM);
    			if (gem != null)
    			{
        			OreDictUtil.OreDict ore = entries.get(OreDictUtil.EnumMaterialType.ORE);
        			if (ore != null) //recipe for ore
        			{
        				ResourceLocation recipeName = new ResourceLocation(MechanimationAPI.MOD_ID, name + "_ore_to_" + name + "_gem");
        				if (!disabled.contains(recipeName) && !PulverizerRecipes.hasRecipe(recipeName))
        				{
            				Main.logger().debug("automatically adding ore -> gem pulverizer recipe for " + name + " with name " + recipeName);
            				ItemStack copy = gem.item.copy();
            				copy.setCount(2);
            				PulverizerRecipes.registerRecipe(recipeName, new PulverizerRecipe(new SizedIngredient(new OreIngredient(ore.name), 1), copy, 4000));
        				}
        			}
    			}
        		OreDictUtil.OreDict dust = entries.get(OreDictUtil.EnumMaterialType.DUST);
        		if (dust != null)
        		{
        			if (gem == null)
        			{
            			OreDictUtil.OreDict ore = entries.get(OreDictUtil.EnumMaterialType.ORE);
            			if (ore != null) //recipe for ore
            			{
            				ResourceLocation recipeName = new ResourceLocation(MechanimationAPI.MOD_ID, name + "_ore_to_" + name + "_dust");
            				if (!disabled.contains(recipeName) && !PulverizerRecipes.hasRecipe(recipeName))
            				{
                				Main.logger().debug("automatically adding ore -> dust pulverizer recipe for " + name + " with name " + recipeName);
                				ItemStack copy = dust.item.copy();
                				copy.setCount(2);
                				PulverizerRecipes.registerRecipe(recipeName, new PulverizerRecipe(new SizedIngredient(new OreIngredient(ore.name), 1), copy, 4000));
            				}
            			}
            			OreDictUtil.OreDict ingot = entries.get(OreDictUtil.EnumMaterialType.INGOT);
            			if (ingot != null) //recipe for ingot
            			{
            				ResourceLocation recipeName = new ResourceLocation(MechanimationAPI.MOD_ID, name + "_ingot_to_" + name + "_dust");
            				if (!disabled.contains(recipeName) && !PulverizerRecipes.hasRecipe(recipeName))
            				{
                				Main.logger().debug("automatically adding ingot -> dust pulverizer recipe for " + name + " with name " + recipeName);
                				ItemStack copy = dust.item.copy();
                				copy.setCount(1);
                				PulverizerRecipes.registerRecipe(recipeName, new PulverizerRecipe(new SizedIngredient(new OreIngredient(ingot.name), 1), copy, 4000));
            				}
            			}
        			}
        			else //recipe for gem
        			{
        				ResourceLocation recipeName = new ResourceLocation(MechanimationAPI.MOD_ID, name + "_gem_to_" + name + "_dust");
        				if (!disabled.contains(recipeName) && !PulverizerRecipes.hasRecipe(recipeName))
        				{
            				Main.logger().debug("automatically adding gem -> dust pulverizer recipe for " + name + " with name " + recipeName);
            				ItemStack copy = dust.item.copy();
            				copy.setCount(1);
            				PulverizerRecipes.registerRecipe(recipeName, new PulverizerRecipe(new SizedIngredient(new OreIngredient(gem.name), 1), copy, 4000));
        				}
        			}
        		}
        	});
		});
		CraftLoaderAPI.registerLoader(new ResourceLocation(MechanimationAPI.MOD_ID, "arc_furnace"), ArcFurnaceRecipes::initRecipes, (BiConsumer<ResourceLocation, IArcFurnaceRecipe>) ArcFurnaceRecipes::registerRecipe, ArcFurnaceRecipes::disableRecipe, disabled -> {
        	Main.logger().debug("Generating automatic ore-dictionary arc furnace recipes");
        	OreDictUtil.MATERIALS.forEach((name, entries) -> {
        		Fluid fluid = getFluidFromCamelCase(name);
        		if (fluid != null)
        		{
        			for (OreDictUtil.EnumMaterialType type : OreDictUtil.EnumMaterialType.values()) if (type.mbValue > 0)
        			{
            			OreDictUtil.OreDict dict = entries.get(type);
            			if (dict != null)
            			{
            				ResourceLocation recipeName = new ResourceLocation(MechanimationAPI.MOD_ID, "metal_from_" + name + "_" + type.name().toLowerCase(Locale.ENGLISH));
            				if (!disabled.contains(recipeName))
            				{
                				Main.logger().debug("automatically adding " + type.name().toLowerCase(Locale.ENGLISH) + " arc furnace recipe for " + dict.name + " with name " + recipeName);
                				FluidStack stack = new FluidStack(fluid, type.mbValue);
                				ArcFurnaceRecipes.registerRecipe(recipeName, new ArcFurnaceRecipe(new SizedIngredient(new OreIngredient(dict.name), 1), FluidOrGasStack.forFluid(stack), fluid.getTemperature(stack) - 300));
            				}
            			}
        			}
        		}
        	});
		});
		CraftLoaderAPI.registerLoader(new ResourceLocation(MechanimationAPI.MOD_ID, "casting"), CastingRecipes::initRecipes, (ResourceLocation name, Pair<EnumCastingType, ICastingRecipe> pair) -> CastingRecipes.registerRecipe(pair.getLeft(), name, pair.getRight()), CastingRecipes::disableRecipe, disabled -> {
        	Main.logger().debug("Generating automatic ore-dictionary casting recipes");
        	OreDictUtil.MATERIALS.forEach((name, entries) -> {
        		List<Fluid> fluids = getFluidsFromCamelCase(name);
        		if (!fluids.isEmpty())
        		{
        			for (OreDictUtil.EnumMaterialType type : OreDictUtil.EnumMaterialType.values()) if (type.castingType != null)
        			{
            			OreDictUtil.OreDict dict = entries.get(type);
            			if (dict != null)
            			{
            				ResourceLocation recipeName = new ResourceLocation(MechanimationAPI.MOD_ID, name + "_" + type.name().toLowerCase(Locale.ENGLISH) + "_from_metal");
            				if (!disabled.contains(recipeName))
            				{
                				Main.logger().debug("automatically adding " + type.name().toLowerCase(Locale.ENGLISH) + " casting recipe for " + dict.name + " with name " + recipeName);
                				List<FluidOrGasStack> stacks = fluids.stream().map(fluid -> FluidOrGasStack.forFluid(new FluidStack(fluid, type.mbValue))).collect(Collectors.toList());
                				CastingRecipes.registerRecipe(type.castingType, recipeName, new CastingRecipe(stacks, dict.item.copy(), fluids.get(0).getTemperature(stacks.get(0).getFluidStack()) - 300, 40));
            				}
            			}
        			}
        		}
        	});
		});
		CraftLoaderAPI.registerLoader(new ResourceLocation(MechanimationAPI.MOD_ID, "assembly_terminal_blueprints"), AssemblyRecipes::initBlueprints, (ResourceLocation name, Pair<EnumCastingType, ICastingRecipe> pair) -> CastingRecipes.registerRecipe(pair.getLeft(), name, pair.getRight()), CastingRecipes::disableRecipe, disabled -> {
	        if (CommonConfig.INSTANCE.autoCraftingRF.val >= 0) AssemblyRecipes.registerBlueprint(new CraftingTableBlueprint());
		});
		CraftLoaderAPI.registerLoader(new ResourceLocation(MechanimationAPI.MOD_ID, "assembly_terminal_recipes"), () -> {}, (ResourceLocation name, JsonObject obj) -> {
			ResourceLocation blr = new ResourceLocation(JsonUtils.getString(obj, "blueprint"));
			IAssemblyBlueprint existing = AssemblyRecipes.getBlueprint(blr);
			if (existing == null) throw new JsonSyntaxException("Can't add or remove assembly terminal recipe " + name + " to existing non-standard assembly terminal blueprint " + blr);
			else if (!(existing instanceof AssemblyBlueprint)) throw new JsonSyntaxException("Can't add or remove assembly terminal recipe " + name + " to non-existing assembly terminal blueprint " + blr);
			return new AssemblyRecipeKey(name, (AssemblyBlueprint) existing);
		}, (AssemblyRecipeKey name, IAssemblyRecipe recipe) -> name.blueprint.registerRecipe(recipe), name -> name.blueprint.disableRecipe(name.name));
	}

	public static Fluid getFluidFromCamelCase(String name)
	{
		String snake = APIUtils.camelCaseToSnakeCase(name);
		try
		{
			return FluidRegistry.getRegisteredFluids().entrySet().stream().filter(entry -> entry.getKey().equalsIgnoreCase(name) || entry.getKey().equalsIgnoreCase(snake)).findFirst().get().getValue();
		}
		catch (NoSuchElementException e)
		{
			return null;
		}
	}

	public static List<Fluid> getFluidsFromCamelCase(String name)
	{
		String snake = APIUtils.camelCaseToSnakeCase(name);
		return FluidRegistry.getRegisteredFluids().entrySet().stream().filter(entry -> entry.getKey().equalsIgnoreCase(name) || entry.getKey().equalsIgnoreCase(snake)).map(Entry::getValue).collect(Collectors.toList());
	}

	public static List<FluidOrGasStack> getFluids(JsonObject obj, String memberName)
	{
		JsonElement el = obj.get(memberName);
		if (el.isJsonObject()) return Collections.singletonList(getFluid(el.getAsJsonObject()));
		else if (el.isJsonArray())
		{
			JsonArray array = el.getAsJsonArray();
			if (array.size() == 0) throw new JsonSyntaxException("Missing " + memberName + ", expected to find a JsonObject or JsonArray of JsonObject");
			List<FluidOrGasStack> list = new ArrayList<>();
			for (int i = 0; i < array.size(); i++)
			{
				JsonElement el2 = array.get(i);
				if (el2.isJsonObject()) try
				{
					list.add(getFluid(el2.getAsJsonObject()));
				}
				catch (JsonSyntaxException e) {} //TODO log failures
				else throw new JsonSyntaxException("Missing " + memberName + ", expected to find a JsonObject or JsonArray of JsonObject");
			}
			if (list.isEmpty()) throw new JsonSyntaxException("No valid fluids parsed from " + memberName + "!");
			return list;
		}
		else
		{
            throw new JsonSyntaxException("Missing " + memberName + ", expected to find a JsonObject or JsonArray of JsonObject");
		}
	}

	public static FluidOrGasStack getFluid(JsonObject obj, String memberName)
	{
		return getFluid(JsonUtils.getJsonObject(obj, memberName));
	}

	public static FluidOrGasStack getFluidOptional(JsonObject obj, String memberName)
	{
		return obj.has(memberName) ? getFluid(JsonUtils.getJsonObject(obj, memberName)) : null;
	}

	public static FluidOrGasStack getFluid(JsonObject obj)
	{
		String fluidName = JsonUtils.getString(obj, "fluid");
		int amount = JsonUtils.getInt(obj, "amount");
		if (amount < 0) throw new JsonSyntaxException("amount cannot be negative, got " + amount);
		if (JsonUtils.getBoolean(obj, "isGas", false))
		{
			Gas gas = GasRegistry.getGas(fluidName);
			if (gas != null) return FluidOrGasStack.forGas(new GasStack(gas, amount));
			else throw new JsonSyntaxException("Could not add load fluid definition - unknown gas name: " + fluidName);
		}
		else
		{
			Fluid fluid = FluidRegistry.getFluid(fluidName);
			if (fluid != null) return FluidOrGasStack.forFluid(new FluidStack(fluid, amount));
			else throw new JsonSyntaxException("Could not add load fluid definition - unknown fluid name: " + fluidName);
		}
	}
}