package firemerald.mechanimation.api.crafting.generator.combustion;

import java.util.ArrayList;
import java.util.Collection;
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

public class CombustionGeneratorRecipes implements ICombustionGeneratorRecipes
{
	private static final List<ICombustionGeneratorRecipes> PROVIDERS = new ArrayList<>();

	public static void registerRecipes(ICombustionGeneratorRecipes recipes)
	{
		PROVIDERS.add(recipes);
	}

	public static void unregisterRecipes(ICombustionGeneratorRecipes recipes)
	{
		PROVIDERS.remove(recipes);
	}

	public static ICombustionGeneratorRecipe getRecipe(FluidOrGasStack fuel)
	{
		return PROVIDERS.stream().map(recipes -> recipes.getResult(fuel)).filter(result -> result != null).findFirst().orElse(null);
	}

	public static List<ICombustionGeneratorRecipe> getRecipes()
	{
		return PROVIDERS.stream().flatMap(recipes -> recipes.getAllResults().stream()).collect(Collectors.toList());
	}

	public static boolean isCraftable(FluidOrGasStack fuel)
	{
		return isCraftable(getRecipe(fuel));
	}

	public static boolean isCraftable(ICombustionGeneratorRecipe result)
	{
		return result != null && result.speed() > 0;
	}

	private static final CombustionGeneratorRecipes INSTANCE = new CombustionGeneratorRecipes();

	public static void registerRecipe(ResourceLocation name, ICombustionGeneratorRecipe recipe)
	{
		if (!INSTANCE.disabled.contains(name)) INSTANCE.recipes.put(name, recipe);
	}

	public static ICombustionGeneratorRecipe getRecipe(ResourceLocation name)
	{
		return INSTANCE.recipes.get(name);
	}

	public static boolean hasRecipe(ResourceLocation name)
	{
		return INSTANCE.recipes.containsKey(name);
	}

	public static void removeRecipe(ResourceLocation name)
	{
		INSTANCE.recipes.remove(name);
	}

	public static void removeRecipe(ICombustionGeneratorRecipe recipe)
	{
		INSTANCE.recipes.entrySet().parallelStream().filter(entry -> entry.getValue() == recipe).map((Function<Entry<ResourceLocation, ICombustionGeneratorRecipe>, Object>) Entry::getKey).collect(Collectors.toList()).forEach(INSTANCE.recipes::remove);
	}

	public static void removeRecipe(ResourceLocation name, ICombustionGeneratorRecipe recipe)
	{
		if (INSTANCE.recipes.get(name) == recipe) INSTANCE.recipes.remove(name);
	}

	public static void disableRecipe(ResourceLocation name)
	{
		removeRecipe(name);
		INSTANCE.disabled.add(name);
	}

	public static void initRecipes()
	{
		PROVIDERS.forEach(IRecipes::init);
	}

	private final Map<ResourceLocation, ICombustionGeneratorRecipe> recipes = new LinkedHashMap<>();
	private final Set<ResourceLocation> disabled = new HashSet<>();

	private CombustionGeneratorRecipes()
	{
		registerRecipes(this);
	}

	@Override
	public void init()
	{
		recipes.clear();
		disabled.clear();
	}

	@Override
	public ICombustionGeneratorRecipe getResult(FluidOrGasStack fuel)
	{
		return recipes.values().stream().filter(recipe -> recipe.isInputValid(fuel)).findFirst().orElse(null);
	}

	@Override
	public Collection<? extends ICombustionGeneratorRecipe> getAllResults()
	{
		return recipes.values();
	}
}