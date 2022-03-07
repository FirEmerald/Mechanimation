package firemerald.mechanimation.api.crafting.pulverizer;

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

import firemerald.mechanimation.api.crafting.IRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class PulverizerRecipes implements IPulverizerRecipes
{
	private static final List<IPulverizerRecipes> PROVIDERS = new ArrayList<>();

	public static void registerRecipes(IPulverizerRecipes recipes)
	{
		PROVIDERS.add(recipes);
	}

	public static void unregisterRecipes(IPulverizerRecipes recipes)
	{
		PROVIDERS.remove(recipes);
	}

	public static IPulverizerRecipe getRecipe(ItemStack stack)
	{
		return PROVIDERS.stream().map(recipes -> recipes.getResult(stack)).filter(result -> result != null).findFirst().orElse(null);
	}

	public static List<IPulverizerRecipe> getRecipes()
	{
		return PROVIDERS.stream().flatMap(recipes -> recipes.getAllResults().stream()).collect(Collectors.toList());
	}

	public static boolean isCraftable(ItemStack stack)
	{
		return getRecipes().parallelStream().anyMatch(recipe -> recipe.isInputValid(stack));
	}

	public static boolean isCraftable(IPulverizerRecipe result)
	{
		return result != null && result.getOutput() != null && !result.getOutput().isEmpty();
	}

	private static final PulverizerRecipes INSTANCE = new PulverizerRecipes();

	public static void registerRecipe(ResourceLocation name, IPulverizerRecipe recipe)
	{
		if (!INSTANCE.disabled.contains(name)) INSTANCE.recipes.put(name, recipe);
	}

	public static IPulverizerRecipe getRecipe(ResourceLocation name)
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

	public static void removeRecipe(IPulverizerRecipe recipe)
	{
		INSTANCE.recipes.entrySet().parallelStream().filter(entry -> entry.getValue() == recipe).map((Function<Entry<ResourceLocation, IPulverizerRecipe>, Object>) Entry::getKey).collect(Collectors.toList()).forEach(INSTANCE.recipes::remove);
	}

	public static void removeRecipe(ResourceLocation name, IPulverizerRecipe recipe)
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

	private final Map<ResourceLocation, IPulverizerRecipe> recipes = new LinkedHashMap<>();
	private final Set<ResourceLocation> disabled = new HashSet<>();

	private PulverizerRecipes()
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
	public IPulverizerRecipe getResult(ItemStack stack)
	{
		return recipes.values().stream().filter(recipe -> recipe.isInputValid(stack)).findFirst().orElse(null);
	}

	@Override
	public Collection<IPulverizerRecipe> getAllResults()
	{
		return recipes.values();
	}
}