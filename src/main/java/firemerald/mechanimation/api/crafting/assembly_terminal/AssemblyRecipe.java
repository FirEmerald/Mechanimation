package firemerald.mechanimation.api.crafting.assembly_terminal;

import firemerald.craftloader.api.SizedIngredient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class AssemblyRecipe implements IAssemblyRecipe
{
	public final ResourceLocation name;
	public final Ingredient blueprint;
	public final int minimumTier;
	public final int flux;
	public final SizedIngredient[] inputs;
	public final ItemStack output;

	public AssemblyRecipe(ResourceLocation name, Ingredient blueprint, int minimumTier, int flux, ItemStack output, SizedIngredient... input)
	{
		this.name = name;
		this.blueprint = blueprint;
		this.minimumTier = minimumTier;
		this.flux = flux;
		this.inputs = input;
		this.output = output.copy();
	}

	@Override
	public ItemStack[] getDefaultBlueprint()
	{
		return blueprint.getMatchingStacks();
	}

	@Override
	public boolean isBlueprintValid(ItemStack blueprint, int tier)
	{
		return tier >= minimumTier && this.blueprint.apply(blueprint);
	}

	@Override
	public int getDefaultTier()
	{
		return minimumTier;
	}

	@Override
	public ItemStack getDefaultOutput()
	{
		return output;
	}

	@Override
	public int getDefaultUsedFlux()
	{
		return flux;
	}

	@Override
	public boolean isInputValid(World world, ItemStack blueprint, int tier, ItemStack... inputs)
	{
		if (!this.blueprint.apply(blueprint) || tier < this.minimumTier || inputs.length != this.inputs.length) return false;
		for (int i = 0; i < this.inputs.length; i++) if (!this.inputs[i].ingredient.apply(inputs[i])) return false;
		return true;
	}

	@Override
	public ItemStack[][] getDefaultInput(ItemStack blueprint, int tier)
	{
		ItemStack[][] def = new ItemStack[inputs.length][];
		for (int i = 0; i < inputs.length; i++)
		{
			def[i] = inputs[i].ingredient.getMatchingStacks();
			for (int j = 0; j < def[i].length; j++) def[i][j].setCount(inputs[i].count);
		}
		return def;
	}

	@Override
	public ItemStack process(World world, ItemStack blueprint, int tier, boolean simulate, ItemStack... items)
	{
		if (!simulate) for (int i = 0; i < items.length; i++)
		{
			SizedIngredient ingred = this.inputs[i];
			if (ingred != null)
			{
				int cur = items[i].getCount();
				if (cur > ingred.count) items[i].setCount(cur - ingred.count);
				else items[i] = ForgeHooks.getContainerItem(items[i]);
			}
		}
		return this.output;
	}

	@Override
	public int requiredFlux(World world, ItemStack blueprint, int tier, ItemStack... inputs)
	{
		return getDefaultUsedFlux();
	}

	@Override
	public ResourceLocation getUniqueName()
	{
		return name;
	}

	@Override
	public boolean isPartialInputValid(World world, ItemStack blueprint, int tier, ItemStack... existingInputs)
	{
		for (int i = 0; i < inputs.length; i++)
		{
			ItemStack stack = existingInputs[i];
			if (!stack.isEmpty() && !inputs[i].ingredient.apply(stack)) return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return "Assembly Recipe ID " + getUniqueName();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this) return true;
		else if (o == null || o.getClass() != this.getClass()) return false;
		else return ((AssemblyRecipe) o).name.equals(this.name);
	}
}