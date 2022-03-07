package firemerald.mechanimation.util.crafting;

import java.util.List;

import firemerald.mechanimation.api.crafting.assembly_terminal.IAssemblyRecipe;
import firemerald.mechanimation.config.CommonConfig;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class CraftingTableAssemblyRecipe implements IAssemblyRecipe
{
	private static class InventoryWrapper extends InventoryCrafting
	{
		final ItemStack[] items;

		InventoryWrapper(ItemStack[] items)
		{
			super(null, 3, 3);
			this.items = items;
		}

		@Override
	    public int getSizeInventory()
	    {
	        return 9;
	    }

		@Override
	    public boolean isEmpty()
	    {
	    	for (ItemStack stack : items) if (!stack.isEmpty()) return true;
	    	return false;
	    }

		@Override
	    public ItemStack getStackInSlot(int index)
	    {
	        return index >= 0 && index < 9 ? items[index] : ItemStack.EMPTY;
	    }

		@Override
	    public ItemStack removeStackFromSlot(int index)
	    {
			if (index >= 0 && index < 9)
			{
				ItemStack stack = items[index];
				items[index] = ItemStack.EMPTY;
				return stack;
			}
			else return ItemStack.EMPTY;
	    }

		@Override
	    public ItemStack decrStackSize(int index, int count)
	    {
			if (index >= 0 && index < 9)
			{
				ItemStack stack = items[index];
				return stack.splitStack(count);
			}
			else return ItemStack.EMPTY;
	    }

		@Override
	    public void setInventorySlotContents(int index, ItemStack stack)
	    {
			if (index >= 0 && index < 9) items[index] = stack;
	    }

		@Override
	    public void clear()
	    {
			for (int i = 0; i < 9; i++) items[i] = ItemStack.EMPTY;
	    }

		@Override
	    public void fillStackedContents(RecipeItemHelper helper)
	    {
	        for (ItemStack itemstack : items) helper.accountStack(itemstack);
	    }
	}

	public final ResourceLocation recipeName;
	public final IRecipe recipe;

	public CraftingTableAssemblyRecipe(ResourceLocation recipeName, IRecipe recipe)
	{
		this.recipeName = recipeName;
		this.recipe = recipe;
	}

	@Override
	public ResourceLocation getUniqueName()
	{
		return recipeName;
	}

	@Override
	public boolean isPartialInputValid(World world, ItemStack blueprint, int tier, ItemStack... existingInputs)
	{
		ItemStack[][] inputs = this.getDefaultInput(blueprint, tier);
		ItemStack[] newItems = new ItemStack[9];
		for (int i = 0; i < 9; i++)
		{
			if (!existingInputs[i].isEmpty()) newItems[i] = existingInputs[i];
			else
			{
				ItemStack[] stacks = inputs[i];
				newItems[i] = stacks.length == 0 ? ItemStack.EMPTY : stacks[0];
			}
		}
		return recipe.matches(new InventoryWrapper(newItems), world);
	}

	@Override
	public ItemStack[] getDefaultBlueprint()
	{
		return CraftingTableBlueprint.BLUEPRINTS.getMatchingStacks();
	}

	@Override
	public int getDefaultTier()
	{
		return 0;
	}

	@Override
	public boolean isBlueprintValid(ItemStack blueprint, int tier)
	{
		return CraftingTableBlueprint.BLUEPRINTS.apply(blueprint);
	}

	@Override
	public ItemStack getDefaultOutput()
	{
		return recipe.getRecipeOutput();
	}

	@Override
	public int getDefaultUsedFlux()
	{
		return CommonConfig.INSTANCE.autoCraftingRF.val;
	}

	private static Ingredient get(List<Ingredient> list, int index)
	{
		return index < list.size() ? list.get(index) : Ingredient.EMPTY;
	}

	@Override
	public ItemStack[][] getDefaultInput(ItemStack blueprint, int tier)
	{
		ItemStack[][] array = new ItemStack[9][];
		List<Ingredient> ingreds = recipe.getIngredients();
		if (recipe.canFit(1, 1)) //center
		{
			array[0] = array[1] = array[2] = array[3] = array[5] = array[6] = array[7] = array[8] = new ItemStack[0];
			array[4] = get(ingreds, 0).getMatchingStacks();
			return array;
		}
		else if (recipe.canFit(1, 2)) //center X, top
		{
			array[0] = array[2] = array[3] = array[5] = array[6] = array[7] = array[8] = new ItemStack[0];
			array[1] = get(ingreds, 0).getMatchingStacks();
			array[4] = get(ingreds, 1).getMatchingStacks();
			return array;
		}
		else if (recipe.canFit(1, 3)) //center X
		{
			array[0] = array[2] = array[3] = array[5] = array[6] = array[8] = new ItemStack[0];
			array[1] = get(ingreds, 0).getMatchingStacks();
			array[4] = get(ingreds, 1).getMatchingStacks();
			array[7] = get(ingreds, 2).getMatchingStacks();
			return array;
		}
		else if (recipe.canFit(2, 1)) //center Y, left
		{
			array[0] = array[1] = array[2] = array[5] = array[6] = array[7] = array[8] = new ItemStack[0];
			array[3] = get(ingreds, 0).getMatchingStacks();
			array[4] = get(ingreds, 1).getMatchingStacks();
			return array;
		}
		else if (recipe.canFit(2, 2)) //top-left
		{
			array[2] = array[5] = array[6] = array[7] = array[8] = new ItemStack[0];
			array[0] = get(ingreds, 0).getMatchingStacks();
			array[1] = get(ingreds, 1).getMatchingStacks();
			array[3] = get(ingreds, 2).getMatchingStacks();
			array[4] = get(ingreds, 3).getMatchingStacks();
			return array;
		}
		else if (recipe.canFit(2, 3)) //left
		{
			array[2] = array[5] = array[8] = new ItemStack[0];
			array[0] = get(ingreds, 0).getMatchingStacks();
			array[1] = get(ingreds, 1).getMatchingStacks();
			array[3] = get(ingreds, 2).getMatchingStacks();
			array[4] = get(ingreds, 3).getMatchingStacks();
			array[6] = get(ingreds, 4).getMatchingStacks();
			array[7] = get(ingreds, 5).getMatchingStacks();
			return array;
		}
		else if (recipe.canFit(3, 1)) //center Y
		{
			array[0] = array[1] = array[2] = array[6] = array[7] = array[8] = new ItemStack[0];
			array[3] = get(ingreds, 0).getMatchingStacks();
			array[4] = get(ingreds, 1).getMatchingStacks();
			array[5] = get(ingreds, 2).getMatchingStacks();
			return array;
		}
		else
		{
			int i;
			for (i = 0; i < ingreds.size(); i++) array[i] = ingreds.get(i).getMatchingStacks();
			for (; i < 9; i++) array[i] = new ItemStack[0];
			return array;
		}
	}

	@Override
	public boolean isInputValid(World world, ItemStack blueprint, int tier, ItemStack... inputs)
	{
		return recipe.matches(new InventoryWrapper(inputs), world);
	}

	@Override
	public ItemStack process(World world, ItemStack blueprint, int tier, boolean simulate, ItemStack... items)
	{
		InventoryWrapper inv = new InventoryWrapper(items);
		ItemStack initial = recipe.getCraftingResult(inv);
		if (!simulate)
		{
			List<ItemStack> remaining = recipe.getRemainingItems(inv);
			for (int i = 0; i < 9; i++)
			{
				if (!items[i].isEmpty()) items[i].shrink(1);
				if (items[i].isEmpty()) items[i] = i < remaining.size() ? remaining.get(i) : ItemStack.EMPTY;
			}
		}
		return initial;
	}

	@Override
	public int requiredFlux(World world, ItemStack blueprint, int tier, ItemStack... inputs)
	{
		return CommonConfig.INSTANCE.autoCraftingRF.val;
	}

	@Override
	public String toString()
	{
		return "Crafting Assembly Recipe ID " + getUniqueName();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this) return true;
		else if (o == null || o.getClass() != this.getClass()) return false;
		else return ((CraftingTableAssemblyRecipe) o).recipeName.equals(this.recipeName);
	}
}