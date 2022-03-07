package firemerald.mechanimation.compat.galacticraft;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.api.crafting.assembly_terminal.IAssemblyBlueprint;
import firemerald.mechanimation.api.crafting.assembly_terminal.IAssemblyRecipe;
import firemerald.mechanimation.api.util.Rectangle;
import firemerald.mechanimation.api.util.Vec2i;
import micdoodle8.mods.galacticraft.api.recipe.INasaWorkbenchRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class NasaWorkbenchAssemblyBlueprint implements IAssemblyBlueprint
{
	public final ResourceLocation name;
	public final ItemStack blueprint;
	public final int tier;
	public final ResourceLocation image;
	public final Rectangle imageBounds;
	public final int flux;
	public final Vec2i[] inputs;
	public final Vec2i output;
	public final Supplier<Stream<INasaWorkbenchRecipe>> recipes;

	public NasaWorkbenchAssemblyBlueprint(String name, ItemStack blueprint, int minimumTier, ResourceLocation image, Rectangle imageBounds, int flux, Supplier<Stream<INasaWorkbenchRecipe>> recipes, Vec2i output, Vec2i... slots)
	{
		this.name = new ResourceLocation(MechanimationAPI.MOD_ID, "compat/galacticraft/nasa_workbench/" + name);
		this.blueprint = blueprint;
		this.tier = minimumTier;
		this.image = image;
		this.imageBounds = imageBounds;
		this.flux = flux;
		this.recipes = recipes;
		this.output = output;
		this.inputs = slots;
	}

	@Override
	public ResourceLocation getUniqueName()
	{
		return name;
	}

	@Override
	public IAssemblyRecipe getRecipe(ResourceLocation name)
	{
		if (name.getResourceDomain().equals(this.name.getResourceDomain()) && name.getResourcePath().startsWith(this.name.getResourcePath() + "/")) try
		{
			int id = Integer.parseInt(name.getResourcePath().substring(this.name.getResourcePath().length() + 1));
			INasaWorkbenchRecipe recipe = recipes.get().skip(id).findFirst().orElse(null);
			return new NasaWorkbenchAssemblyRecipe(name, blueprint, tier, flux, recipe, inputs.length);
		}
		catch (NumberFormatException e) {}
		return null;
	}

	@Override
	public ItemStack[] getDefaultBlueprint()
	{
		return new ItemStack[] {blueprint};
	}

	@Override
	public int getDefaultTier()
	{
		return tier;
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

	@Override
	public void init() {}

	@Override
	public Collection<? extends IAssemblyRecipe> getAllResults()
	{
		AtomicInteger counter = new AtomicInteger();
		return recipes.get().map(recipe -> new NasaWorkbenchAssemblyRecipe(new ResourceLocation(name.getResourceDomain(), name.getResourcePath() + "/" + counter.getAndIncrement()), blueprint, tier, flux, recipe, inputs.length)).collect(Collectors.toList());
	}

	@Override
	public boolean isBlueprintValid(ItemStack blueprint, int tier)
	{
		return tier >= this.tier && ItemStack.areItemsEqual(blueprint, this.blueprint); //ignore NBT
	}

	@Override
	public Vec2i getOutputPosition(ItemStack blueprint, int tier)
	{
		return output;
	}

	@Override
	public String toString()
	{
		return "NASA Assembly Blueprint ID " + getUniqueName();
	}
}
