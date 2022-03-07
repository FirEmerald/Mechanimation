package firemerald.mechanimation.api.crafting.assembly_terminal;

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

public class AssemblyRecipes implements IAssemblyBlueprints
{
	private static final List<IAssemblyBlueprints> PROVIDERS = new ArrayList<>();
	public static final int MAX_WIDTH = 160, MAX_HEIGHT = 134;

	public static void registerBlueprints(IAssemblyBlueprints recipes)
	{
		PROVIDERS.add(recipes);
	}

	public static void unregisterBlueprints(IAssemblyBlueprints recipes)
	{
		PROVIDERS.remove(recipes);
	}

	public static IAssemblyBlueprint getBlueprint(ItemStack blueprint, int tier)
	{
		return PROVIDERS.stream().map(recipes -> recipes.getResult(blueprint, tier)).filter(result -> result != null).findFirst().orElse(null);
	}

	public static List<IAssemblyBlueprint> getBlueprints()
	{
		return PROVIDERS.stream().flatMap(recipes -> recipes.getAllResults().stream()).collect(Collectors.toList());
	}

	public static boolean isCraftable(ItemStack blueprint, int tier)
	{
		return getBlueprints().parallelStream().anyMatch(recipe -> recipe.isBlueprintValid(blueprint, tier));
	}

	public static boolean isCraftable(IAssemblyRecipe result)
	{
		return result != null && result.getDefaultOutput() != null && !result.getDefaultOutput().isEmpty();
	}

	private static final AssemblyRecipes INSTANCE = new AssemblyRecipes();

	public static void registerBlueprint(IAssemblyBlueprint recipe)
	{
		ResourceLocation name = recipe.getUniqueName();
		if (!INSTANCE.disabled.contains(name)) INSTANCE.blueprints.put(name, recipe);
	}

	public static IAssemblyBlueprint getBlueprint(ResourceLocation name)
	{
		return INSTANCE.blueprints.get(name);
	}

	public static boolean hasBlueprint(ResourceLocation name)
	{
		return INSTANCE.blueprints.containsKey(name);
	}

	public static void removeBlueprint(ResourceLocation name)
	{
		INSTANCE.blueprints.remove(name);
	}

	public static void removeBlueprint(IAssemblyBlueprint recipe)
	{
		INSTANCE.blueprints.entrySet().parallelStream().filter(entry -> entry.getValue() == recipe).map((Function<Entry<ResourceLocation, IAssemblyBlueprint>, Object>) Entry::getKey).collect(Collectors.toList()).forEach(INSTANCE.blueprints::remove);
	}

	public static void removeBlueprint(ResourceLocation name, IAssemblyBlueprint recipe)
	{
		if (INSTANCE.blueprints.get(name) == recipe) INSTANCE.blueprints.remove(name);
	}

	public static void disableBlueprint(ResourceLocation name)
	{
		removeBlueprint(name);
		INSTANCE.disabled.add(name);
	}

	public static void initBlueprints()
	{
		PROVIDERS.forEach(IRecipes::init);
	}

	private final Map<ResourceLocation, IAssemblyBlueprint> blueprints = new LinkedHashMap<>();
	private final Set<ResourceLocation> disabled = new HashSet<>();

	private AssemblyRecipes()
	{
		registerBlueprints(this);
	}

	@Override
	public void init()
	{
		blueprints.clear();
		disabled.clear();
	}

	@Override
	public IAssemblyBlueprint getResult(ItemStack blueprint, int tier)
	{
		return blueprints.values().stream().filter(recipe -> recipe.isBlueprintValid(blueprint, tier)).findFirst().orElse(null);
	}

	@Override
	public Collection<IAssemblyBlueprint> getAllResults()
	{
		return blueprints.values();
	}
}