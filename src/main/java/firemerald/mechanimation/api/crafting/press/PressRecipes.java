package firemerald.mechanimation.api.crafting.press;

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

public class PressRecipes implements IPressRecipes
{
	private static final List<IPressRecipes> PROVIDERS = new ArrayList<>();

	public static void registerRecipes(IPressRecipes recipes)
	{
		PROVIDERS.add(recipes);
	}

	public static void unregisterRecipes(IPressRecipes recipes)
	{
		PROVIDERS.remove(recipes);
	}

	public static IPressRecipe getRecipe(ItemStack input)
	{
		return PROVIDERS.stream().map(recipes -> recipes.getResult(input)).filter(result -> result != null).findFirst().orElse(null);
	}

	public static List<IPressRecipe> getRecipes()
	{
		return PROVIDERS.stream().flatMap(recipes -> recipes.getAllResults().stream()).collect(Collectors.toList());
	}

	public static boolean isCraftable(ItemStack input)
	{
		return getRecipes().parallelStream().anyMatch(recipe -> recipe.isInputValid(input));
	}

	public static boolean isCraftable(IPressRecipe result)
	{
		return result != null && result.getOutput() != null && !result.getOutput().isEmpty();
	}

	private static final PressRecipes INSTANCE = new PressRecipes();

	public static void registerRecipe(ResourceLocation name, IPressRecipe recipe)
	{
		if (!INSTANCE.disabled.contains(name)) INSTANCE.recipes.put(name, recipe);
	}

	public static IPressRecipe getRecipe(ResourceLocation name)
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

	public static void removeRecipe(IPressRecipe recipe)
	{
		INSTANCE.recipes.entrySet().parallelStream().filter(entry -> entry.getValue() == recipe).map((Function<Entry<ResourceLocation, IPressRecipe>, Object>) Entry::getKey).collect(Collectors.toList()).forEach(INSTANCE.recipes::remove);
	}

	public static void removeRecipe(ResourceLocation name, IPressRecipe recipe)
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

	private final Map<ResourceLocation, IPressRecipe> recipes = new LinkedHashMap<>();
	private final Set<ResourceLocation> disabled = new HashSet<>();

	private PressRecipes()
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
	public IPressRecipe getResult(ItemStack input)
	{
		return recipes.values().stream().filter(recipe -> recipe.isInputValid(input)).findFirst().orElse(null);
	}

	@Override
	public Collection<IPressRecipe> getAllResults()
	{
		return recipes.values();
	}
}