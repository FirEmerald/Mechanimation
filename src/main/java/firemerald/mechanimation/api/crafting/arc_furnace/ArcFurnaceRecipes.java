package firemerald.mechanimation.api.crafting.arc_furnace;

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

public class ArcFurnaceRecipes implements IArcFurnaceRecipes
{
	private static final List<IArcFurnaceRecipes> PROVIDERS = new ArrayList<>();

	public static void registerRecipes(IArcFurnaceRecipes recipes)
	{
		PROVIDERS.add(recipes);
	}

	public static void unregisterRecipes(IArcFurnaceRecipes recipes)
	{
		PROVIDERS.remove(recipes);
	}

	public static IArcFurnaceRecipe getRecipe(ItemStack stack)
	{
		IArcFurnaceRecipe recipe = null;
		for (IArcFurnaceRecipes recipes : PROVIDERS)
		{
			IArcFurnaceRecipe res = recipes.getResult(stack);
			if (res != null)
			{
				if (res.isToolMeltingRecipe()) return res;
				else recipe = res;
			}
		}
		return recipe;
	}

	public static List<IArcFurnaceRecipe> getRecipes()
	{
		return PROVIDERS.stream().flatMap(recipes -> recipes.getAllResults().stream()).collect(Collectors.toList());
	}

	public static boolean isCraftable(ItemStack stack)
	{
		return getRecipes().parallelStream().anyMatch(recipe -> recipe.isInputValid(stack));
	}

	public static boolean isCraftable(IArcFurnaceRecipe result)
	{
		return result != null && result.getOutput() != null;
	}

	private static final ArcFurnaceRecipes INSTANCE = new ArcFurnaceRecipes();

	public static void registerRecipe(ResourceLocation name, IArcFurnaceRecipe recipe)
	{
		if (!INSTANCE.disabled.contains(name)) INSTANCE.recipes.put(name, recipe);
	}

	public static IArcFurnaceRecipe getRecipe(ResourceLocation name)
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

	public static void removeRecipe(IArcFurnaceRecipe recipe)
	{
		INSTANCE.recipes.entrySet().parallelStream().filter(entry -> entry.getValue() == recipe).map((Function<Entry<ResourceLocation, IArcFurnaceRecipe>, Object>) Entry::getKey).collect(Collectors.toList()).forEach(INSTANCE.recipes::remove);
	}

	public static void removeRecipe(ResourceLocation name, IArcFurnaceRecipe recipe)
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

	private final Map<ResourceLocation, IArcFurnaceRecipe> recipes = new LinkedHashMap<>();
	private final Set<ResourceLocation> disabled = new HashSet<>();

	private ArcFurnaceRecipes()
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
	public IArcFurnaceRecipe getResult(ItemStack stack)
	{
		IArcFurnaceRecipe recipe = null;
		for (IArcFurnaceRecipe res : recipes.values())
		{
			if (res.isInputValid(stack))
			{
				if (res.isToolMeltingRecipe()) return res;
				else recipe = res;
			}
		}
		return recipe;
	}

	@Override
	public Collection<IArcFurnaceRecipe> getAllResults()
	{
		return recipes.values();
	}
}