package firemerald.mechanimation.api.crafting.fluid_reactor;

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
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class FluidReactorRecipes implements IFluidReactorRecipes
{
	private static final List<IFluidReactorRecipes> PROVIDERS = new ArrayList<>();

	public static void registerRecipes(IFluidReactorRecipes recipes)
	{
		PROVIDERS.add(recipes);
	}

	public static void unregisterRecipes(IFluidReactorRecipes recipes)
	{
		PROVIDERS.remove(recipes);
	}

	public static IFluidReactorRecipe getRecipe(ItemStack item, FluidOrGasStack fluid1, FluidOrGasStack fluid2, FluidOrGasStack fluid3)
	{
		return PROVIDERS.stream().map(recipes -> recipes.getResult(item, fluid1, fluid2, fluid3)).filter(result -> result != null).findFirst().orElse(null);
	}

	public static List<IFluidReactorRecipe> getRecipes()
	{
		return PROVIDERS.stream().flatMap(recipes -> recipes.getAllResults().stream()).collect(Collectors.toList());
	}

	public static boolean isCraftable(ItemStack item, FluidOrGasStack fluid1, FluidOrGasStack fluid2, FluidOrGasStack fluid3)
	{
		return PROVIDERS.stream().anyMatch(recipes -> recipes.isInputValid(item, fluid1, fluid2, fluid3));
	}

	public static boolean isCraftable(IFluidReactorRecipe result)
	{
		return result != null && ((result.getItemOutput() != null && !result.getItemOutput().isEmpty()) || (result.getFluidOutput() != null && result.getFluidOutput().getAmount() > 0));
	}

	private static final FluidReactorRecipes INSTANCE = new FluidReactorRecipes();

	public static void registerRecipe(ResourceLocation name, IFluidReactorRecipe recipe)
	{
		if (!INSTANCE.disabled.contains(name)) INSTANCE.recipes.put(name, recipe);
	}

	public static IFluidReactorRecipe getRecipe(ResourceLocation name)
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

	public static void removeRecipe(IFluidReactorRecipe recipe)
	{
		INSTANCE.recipes.entrySet().parallelStream().filter(entry -> entry.getValue() == recipe).map((Function<Entry<ResourceLocation, IFluidReactorRecipe>, Object>) Entry::getKey).collect(Collectors.toList()).forEach(INSTANCE.recipes::remove);
	}

	public static void removeRecipe(ResourceLocation name, IFluidReactorRecipe recipe)
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

	private final Map<ResourceLocation, IFluidReactorRecipe> recipes = new LinkedHashMap<>();
	private final Set<ResourceLocation> disabled = new HashSet<>();

	private FluidReactorRecipes()
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
	public IFluidReactorRecipe getResult(ItemStack item, FluidOrGasStack fluid1, FluidOrGasStack fluid2, FluidOrGasStack fluid3)
	{
		return recipes.values().stream().filter(recipe -> recipe.isInputValidComplete(item, fluid1, fluid2, fluid3)).findFirst().orElse(null);
	}

	@Override
	public boolean isInputValid(ItemStack item, FluidOrGasStack fluid1, FluidOrGasStack fluid2, FluidOrGasStack fluid3)
	{
		return recipes.values().stream().anyMatch(recipe -> recipe.isInputValid(item, fluid1, fluid2, fluid3));
	}

	@Override
	public Collection<IFluidReactorRecipe> getAllResults()
	{
		return recipes.values();
	}
}