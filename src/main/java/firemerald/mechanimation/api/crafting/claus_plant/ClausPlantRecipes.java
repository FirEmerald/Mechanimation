package firemerald.mechanimation.api.crafting.claus_plant;

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

public class ClausPlantRecipes implements IClausPlantRecipes
{
	private static final List<IClausPlantRecipes> PROVIDERS = new ArrayList<>();

	public static void registerRecipes(IClausPlantRecipes recipes)
	{
		PROVIDERS.add(recipes);
	}

	public static void unregisterRecipes(IClausPlantRecipes recipes)
	{
		PROVIDERS.remove(recipes);
	}

	public static IClausPlantRecipe getRecipe(FluidOrGasStack fluid)
	{
		return PROVIDERS.stream().map(recipes -> recipes.getResult(fluid)).filter(result -> result != null).findFirst().orElse(null);
	}

	public static List<IClausPlantRecipe> getRecipes()
	{
		return PROVIDERS.stream().flatMap(recipes -> recipes.getAllResults().stream()).collect(Collectors.toList());
	}

	public static boolean isCraftable(FluidOrGasStack fluid)
	{
		return isCraftable(getRecipe(fluid));
	}

	public static boolean isCraftable(IClausPlantRecipe result)
	{
		return result != null && result.getItemOutput() != null && !result.getItemOutput().isEmpty() && result.getRequiredOxygen() > 0;
	}

	private static final ClausPlantRecipes INSTANCE = new ClausPlantRecipes();

	public static void registerRecipe(ResourceLocation name, IClausPlantRecipe recipe)
	{
		if (!INSTANCE.disabled.contains(name)) INSTANCE.recipes.put(name, recipe);
	}

	public static IClausPlantRecipe getRecipe(ResourceLocation name)
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

	public static void removeRecipe(IClausPlantRecipe recipe)
	{
		INSTANCE.recipes.entrySet().parallelStream().filter(entry -> entry.getValue() == recipe).map((Function<Entry<ResourceLocation, IClausPlantRecipe>, Object>) Entry::getKey).collect(Collectors.toList()).forEach(INSTANCE.recipes::remove);
	}

	public static void removeRecipe(ResourceLocation name, IClausPlantRecipe recipe)
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

	private final Map<ResourceLocation, IClausPlantRecipe> recipes = new LinkedHashMap<>();
	private final Set<ResourceLocation> disabled = new HashSet<>();

	private ClausPlantRecipes()
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
	public IClausPlantRecipe getResult(FluidOrGasStack fluid)
	{
		return recipes.values().stream().filter(recipe -> recipe.isInputValid(fluid)).findFirst().orElse(null);
	}

	@Override
	public Collection<IClausPlantRecipe> getAllResults()
	{
		return recipes.values();
	}
}