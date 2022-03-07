package firemerald.mechanimation.api.crafting.assembly_terminal;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import firemerald.mechanimation.api.crafting.IRecipes;
import firemerald.mechanimation.api.util.Rectangle;
import firemerald.mechanimation.api.util.Vec2i;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public interface IAssemblyBlueprint extends IRecipes<IAssemblyRecipe>, IAssemblyBase
{
	public IAssemblyRecipe getRecipe(ResourceLocation name);

	public default Collection<? extends IAssemblyRecipe> getAllResults(ItemStack blueprint, int tier)
	{
		return isBlueprintValid(blueprint, tier) ? getAllResults() : Collections.emptyList();
	}

	public default List<ResourceLocation> getAllResultNames(ItemStack blueprint, int tier) //should maintain a consistent order. May only change when recipes are reloaded or absolutely needed.
	{
		return isBlueprintValid(blueprint, tier) ? getAllResults().stream().map(IAssemblyBase::getUniqueName).collect(Collectors.toList()) : Collections.emptyList();
	}

	@Override
	public boolean isBlueprintValid(ItemStack blueprint, int tier);

	public default IAssemblyRecipe getResult(World world, ItemStack blueprint, int tier, ItemStack[] inputs)
	{
		return isBlueprintValid(blueprint, tier) ? getAllResults(blueprint, tier).parallelStream().filter(recipe -> recipe.isInputValid(world, blueprint, tier, inputs)).findFirst().orElse(null) : null;
	}

	public default List<IAssemblyRecipe> getValidResults(World world, ItemStack blueprint, int tier, ItemStack[] inputs)
	{
		return isBlueprintValid(blueprint, tier) ? getAllResults(blueprint, tier).parallelStream().filter(recipe -> recipe.isPartialInputValid(world, blueprint, tier, inputs)).collect(Collectors.toList()) : Collections.emptyList();
	}

	public default IAssemblyRecipe getResult(ItemStack blueprint, int tier, ResourceLocation uniqueName)
	{
		return isBlueprintValid(blueprint, tier) ? getAllResults(blueprint, tier).parallelStream().filter(recipe -> recipe.getUniqueName().equals(uniqueName)).findFirst().orElse(null) : null;
	}

	@Override
	public default boolean isPartialInputValid(World world, ItemStack blueprint, int tier, ItemStack... existingInputs)
	{
		return getAllResults(blueprint, tier).stream().anyMatch(recipe -> recipe.isPartialInputValid(world, blueprint, tier, existingInputs));
	}

	public int getMaxUsedInputs();

	public Vec2i[] getInput(ItemStack blueprint, int tier);

	public Vec2i getOutputPosition(ItemStack blueprint, int tier);

	public ResourceLocation blueprintImage(ItemStack blueprint, int tier);

	public Rectangle getBlueprintImageBounds(ItemStack blueprint, int tier);
}