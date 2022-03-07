package firemerald.mechanimation.api.crafting.catalytic_reformer;

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

public class CatalyticReformerRecipes implements ICatalyticReformerRecipes
{
	private static final List<ICatalyticReformerRecipes> PROVIDERS = new ArrayList<>();

	public static void registerRecipes(ICatalyticReformerRecipes recipes)
	{
		PROVIDERS.add(recipes);
	}

	public static void unregisterRecipes(ICatalyticReformerRecipes recipes)
	{
		PROVIDERS.remove(recipes);
	}

	public static ICatalyticReformerRecipe getRecipe(FluidOrGasStack fluid)
	{
		return PROVIDERS.stream().map(recipes -> recipes.getResult(fluid)).filter(result -> result != null).findFirst().orElse(null);
	}

	public static List<ICatalyticReformerRecipe> getRecipes()
	{
		return PROVIDERS.stream().flatMap(recipes -> recipes.getAllResults().stream()).collect(Collectors.toList());
	}

	public static boolean isCraftable(FluidOrGasStack fluid)
	{
		return isCraftable(getRecipe(fluid));
	}

	public static boolean isCraftable(ICatalyticReformerRecipe result)
	{
		return result != null && result.getFluidOutputPrimary() != null && result.getFluidOutputPrimary().getAmount() > 0 && result.getRequiredFlux() > 0;
	}

	private static final CatalyticReformerRecipes INSTANCE = new CatalyticReformerRecipes();

	public static void registerRecipe(ResourceLocation name, ICatalyticReformerRecipe recipe)
	{
		if (!INSTANCE.disabled.contains(name)) INSTANCE.recipes.put(name, recipe);
	}

	public static ICatalyticReformerRecipe getRecipe(ResourceLocation name)
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

	public static void removeRecipe(ICatalyticReformerRecipe recipe)
	{
		INSTANCE.recipes.entrySet().parallelStream().filter(entry -> entry.getValue() == recipe).map((Function<Entry<ResourceLocation, ICatalyticReformerRecipe>, Object>) Entry::getKey).collect(Collectors.toList()).forEach(INSTANCE.recipes::remove);
	}

	public static void removeRecipe(ResourceLocation name, ICatalyticReformerRecipe recipe)
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

	private final Map<ResourceLocation, ICatalyticReformerRecipe> recipes = new LinkedHashMap<>();
	private final Set<ResourceLocation> disabled = new HashSet<>();

	private CatalyticReformerRecipes()
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
	public ICatalyticReformerRecipe getResult(FluidOrGasStack fluid)
	{
		return recipes.values().stream().filter(recipe -> recipe.isInputValid(fluid)).findFirst().orElse(null);
	}

	@Override
	public Collection<ICatalyticReformerRecipe> getAllResults()
	{
		return recipes.values();
	}
}