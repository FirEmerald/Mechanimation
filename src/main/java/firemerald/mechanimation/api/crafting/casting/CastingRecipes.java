package firemerald.mechanimation.api.crafting.casting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.IRecipes;
import net.minecraft.util.ResourceLocation;

public class CastingRecipes implements ICastingRecipes
{
	private static final Map<EnumCastingType, List<ICastingRecipes>> PROVIDERS = new HashMap<>();

	static
	{
		for (EnumCastingType type : EnumCastingType.values()) PROVIDERS.put(type, new ArrayList<>());
	}

	public static void registerRecipes(EnumCastingType type, ICastingRecipes recipes)
	{
		PROVIDERS.get(type).add(recipes);
	}

	public static void unregisterRecipes(EnumCastingType type, ICastingRecipes recipes)
	{
		PROVIDERS.get(type).remove(recipes);
	}

	public static ICastingRecipe getRecipe(EnumCastingType type, FluidOrGasStack stack)
	{
		return PROVIDERS.get(type).stream().map(recipes -> recipes.getResult(stack)).filter(result -> result != null).findFirst().orElse(null);
	}

	public static List<ICastingRecipe> getRecipes(EnumCastingType type)
	{
		return PROVIDERS.get(type).stream().flatMap(recipes -> recipes.getAllResults().stream()).collect(Collectors.toList());
	}

	public static boolean isCraftable(EnumCastingType type, FluidOrGasStack stack)
	{
		return getRecipes(type).parallelStream().anyMatch(recipe -> recipe.isInputValid(stack));
	}

	public static boolean isCraftable(ICastingRecipe result)
	{
		return result != null && result.getOutput() != null && !result.getOutput().isEmpty();
	}

	private static final Map<EnumCastingType, CastingRecipes> INSTANCES = new HashMap<>();

	static
	{
		for (EnumCastingType type : EnumCastingType.values()) INSTANCES.put(type, new CastingRecipes(type));
	}

	public static void registerRecipe(EnumCastingType type, ResourceLocation name, ICastingRecipe recipe)
	{
		CastingRecipes instance = INSTANCES.get(type);
		if (!instance.disabled.contains(name)) instance.recipes.put(name, recipe);
	}

	public static ICastingRecipe getRecipe(EnumCastingType type, ResourceLocation name)
	{
		return INSTANCES.get(type).recipes.get(name);
	}

	public static boolean hasRecipe(EnumCastingType type, ResourceLocation name)
	{
		return INSTANCES.get(type).recipes.containsKey(name);
	}

	public static void removeRecipe(EnumCastingType type, ResourceLocation name)
	{
		INSTANCES.get(type).recipes.remove(name);
	}

	public static void removeRecipe(EnumCastingType type, ICastingRecipe recipe)
	{
		Map<ResourceLocation, ICastingRecipe> recipes = INSTANCES.get(type).recipes;
		recipes.entrySet().parallelStream().filter(entry -> entry.getValue() == recipe).map((Function<Entry<ResourceLocation, ICastingRecipe>, Object>) Entry::getKey).collect(Collectors.toList()).forEach(recipes::remove);
	}

	public static void removeRecipe(EnumCastingType type, ResourceLocation name, ICastingRecipe recipe)
	{
		Map<ResourceLocation, ICastingRecipe> recipes = INSTANCES.get(type).recipes;
		if (recipes.get(name) == recipe) recipes.remove(name);
	}

	public static void disableRecipe(ResourceLocation name)
	{
		INSTANCES.values().forEach(instance -> {
			instance.recipes.remove(name);
			instance.disabled.add(name);
		});
	}

	public static void initRecipes()
	{
		PROVIDERS.values().stream().flatMap(List::stream).forEach(IRecipes::init);
	}

	private final Map<ResourceLocation, ICastingRecipe> recipes = new LinkedHashMap<>();
	private final Set<ResourceLocation> disabled = new HashSet<>();

	private CastingRecipes(EnumCastingType type)
	{
		registerRecipes(type, this);
	}

	@Override
	public void init()
	{
		recipes.clear();
		disabled.clear();
	}

	@Override
	public ICastingRecipe getResult(FluidOrGasStack stack)
	{
		return recipes.values().stream().filter(recipe -> recipe.isInputValid(stack)).findFirst().orElse(null);
	}

	@Override
	public Collection<ICastingRecipe> getAllResults()
	{
		return recipes.values();
	}
}