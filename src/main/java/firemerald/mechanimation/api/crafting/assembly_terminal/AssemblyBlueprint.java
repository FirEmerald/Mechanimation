package firemerald.mechanimation.api.crafting.assembly_terminal;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import firemerald.mechanimation.api.util.Rectangle;
import firemerald.mechanimation.api.util.Vec2i;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public class AssemblyBlueprint implements IAssemblyBlueprint
{
	public final ResourceLocation name;
	public final Map<ResourceLocation, IAssemblyRecipe> recipes = new LinkedHashMap<>();
	public final Set<ResourceLocation> disabled = new HashSet<>();
	public final Ingredient blueprint;
	public final ResourceLocation image;
	public final Rectangle imageBounds;
	public final Vec2i outputLoc;
	public final Vec2i[] inputs;

	public AssemblyBlueprint(ResourceLocation name, Ingredient blueprint, ResourceLocation image, Rectangle imageBounds, Vec2i outputLoc, Vec2i... inputs)
	{
		this.name = name;
		this.blueprint = blueprint;
		this.image = image;
		this.imageBounds = imageBounds;
		this.outputLoc = outputLoc;
		this.inputs = inputs;
	}

	@Override
	public void init()
	{
		recipes.clear();
		disabled.clear();
	}

	@Override
	public Collection<IAssemblyRecipe> getAllResults()
	{
		return recipes.values();
	}

	@Override
	public ItemStack[] getDefaultBlueprint()
	{
		return blueprint.getMatchingStacks();
	}

	@Override
	public int getDefaultTier()
	{
		return recipes.values().parallelStream().mapToInt(IAssemblyRecipe::getDefaultTier).min().orElse(0);
	}

	@Override
	public int getMaxUsedInputs()
	{
		return inputs.length;
	}

	@Override
	public Vec2i[] getInput(ItemStack blueprint, int tier)
	{
		return inputs;
	}

	@Override
	public ResourceLocation blueprintImage(ItemStack blueprint, int tier)
	{
		return image;
	}

	@Override
	public Rectangle getBlueprintImageBounds(ItemStack blueprint, int tier)
	{
		return imageBounds;
	}

	public void registerRecipe(IAssemblyRecipe recipe)
	{
		ResourceLocation name = recipe.getUniqueName();
		if (!disabled.contains(name)) recipes.put(name, recipe);
	}

	@Override
	public IAssemblyRecipe getRecipe(ResourceLocation name)
	{
		return recipes.get(name);
	}

	public boolean hasRecipe(ResourceLocation name)
	{
		return recipes.containsKey(name);
	}

	public void removeRecipe(ResourceLocation name)
	{
		recipes.remove(name);
	}

	public void removeRecipe(IAssemblyRecipe recipe)
	{
		recipes.entrySet().parallelStream().filter(entry -> entry.getValue() == recipe).map((Function<Entry<ResourceLocation, IAssemblyRecipe>, Object>) Entry::getKey).collect(Collectors.toList()).forEach(recipes::remove);
	}

	public void removeRecipe(ResourceLocation name, IAssemblyRecipe recipe)
	{
		if (recipes.get(name) == recipe) recipes.remove(name);
	}

	public void disableRecipe(ResourceLocation name)
	{
		removeRecipe(name);
		disabled.add(name);
	}

	@Override
	public ResourceLocation getUniqueName()
	{
		return name;
	}

	@Override
	public boolean isBlueprintValid(ItemStack blueprint, int tier)
	{
		return tier >= this.getDefaultTier() && this.blueprint.test(blueprint);
	}

	@Override
	public Vec2i getOutputPosition(ItemStack blueprint, int tier)
	{
		return outputLoc;
	}

	@Override
	public String toString()
	{
		return "Assembly Blueprint ID " + getUniqueName();
	}
}