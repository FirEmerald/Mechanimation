package firemerald.mechanimation.api.crafting.electrolyzer;

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

public class ElectrolyzerRecipes implements IElectrolyzerRecipes
{
	private static final List<IElectrolyzerRecipes> PROVIDERS = new ArrayList<>();

	public static void registerRecipes(IElectrolyzerRecipes recipes)
	{
		PROVIDERS.add(recipes);
	}

	public static void unregisterRecipes(IElectrolyzerRecipes recipes)
	{
		PROVIDERS.remove(recipes);
	}

	public static IElectrolyzerRecipe getRecipe(FluidOrGasStack fluid)
	{
		return PROVIDERS.stream().map(recipes -> recipes.getResult(fluid)).filter(result -> result != null).findFirst().orElse(null);
	}

	public static List<IElectrolyzerRecipe> getRecipes()
	{
		return PROVIDERS.stream().flatMap(recipes -> recipes.getAllResults().stream()).collect(Collectors.toList());
	}

	public static boolean isCraftable(FluidOrGasStack fluid)
	{
		return isCraftable(getRecipe(fluid));
	}

	public static boolean isCraftable(IElectrolyzerRecipe result)
	{
		return result != null && result.getFluidOutputPrimary() != null && result.getFluidOutputPrimary().getAmount() > 0 && result.getRequiredFlux() > 0;
	}

	private static final ElectrolyzerRecipes INSTANCE = new ElectrolyzerRecipes();

	public static void registerRecipe(ResourceLocation name, IElectrolyzerRecipe recipe)
	{
		if (!INSTANCE.disabled.contains(name)) INSTANCE.recipes.put(name, recipe);
	}

	public static IElectrolyzerRecipe getRecipe(ResourceLocation name)
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

	public static void removeRecipe(IElectrolyzerRecipe recipe)
	{
		INSTANCE.recipes.entrySet().parallelStream().filter(entry -> entry.getValue() == recipe).map((Function<Entry<ResourceLocation, IElectrolyzerRecipe>, Object>) Entry::getKey).collect(Collectors.toList()).forEach(INSTANCE.recipes::remove);
	}

	public static void removeRecipe(ResourceLocation name, IElectrolyzerRecipe recipe)
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

	private final Map<ResourceLocation, IElectrolyzerRecipe> recipes = new LinkedHashMap<>();
	private final Set<ResourceLocation> disabled = new HashSet<>();

	private ElectrolyzerRecipes()
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
	public IElectrolyzerRecipe getResult(FluidOrGasStack fluid)
	{
		return recipes.values().stream().filter(recipe -> recipe.isInputValid(fluid)).findFirst().orElse(null);
	}

	@Override
	public Collection<IElectrolyzerRecipe> getAllResults()
	{
		return recipes.values();
	}
}