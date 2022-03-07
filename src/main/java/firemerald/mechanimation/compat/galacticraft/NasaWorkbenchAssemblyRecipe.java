package firemerald.mechanimation.compat.galacticraft;

import firemerald.mechanimation.api.crafting.assembly_terminal.IAssemblyRecipe;
import micdoodle8.mods.galacticraft.api.recipe.INasaWorkbenchRecipe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class NasaWorkbenchAssemblyRecipe implements IAssemblyRecipe
{
	public final ResourceLocation name;
	public final INasaWorkbenchRecipe recipe;
	public final ItemStack blueprint;
	public final int tier;
	public final int flux;
	public final int numInputs;

	public static class DummyInventory implements IInventory
	{
		public final ItemStack[] items;

		public DummyInventory(ItemStack... items)
		{
			this.items = items;
		}

		@Override
		public String getName()
		{
			return "none";
		}

		@Override
		public boolean hasCustomName()
		{
			return false;
		}

		@Override
		public ITextComponent getDisplayName()
		{
			return new TextComponentString("none");
		}

		@Override
		public int getSizeInventory()
		{
			return items.length + 1;
		}

		@Override
		public boolean isEmpty()
		{
			for (ItemStack stack : items) if (!stack.isEmpty()) return false;
			return true;
		}

		@Override
		public ItemStack getStackInSlot(int index)
		{
			return index > 0 && index <= items.length ? items[index - 1] : ItemStack.EMPTY;
		}

		@Override
		public ItemStack decrStackSize(int index, int count)
		{
			return ItemStack.EMPTY;
		}

		@Override
		public ItemStack removeStackFromSlot(int index)
		{
			return ItemStack.EMPTY;
		}

		@Override
		public void setInventorySlotContents(int index, ItemStack stack) {}

		@Override
		public int getInventoryStackLimit()
		{
			return 64;
		}

		@Override
		public void markDirty() {}

		@Override
		public boolean isUsableByPlayer(EntityPlayer player)
		{
			return true;
		}

		@Override
		public void openInventory(EntityPlayer player) {}

		@Override
		public void closeInventory(EntityPlayer player) {}

		@Override
		public boolean isItemValidForSlot(int index, ItemStack stack)
		{
			return false;
		}

		@Override
		public int getField(int id)
		{
			return 0;
		}

		@Override
		public void setField(int id, int value) {}

		@Override
		public int getFieldCount()
		{
			return 0;
		}

		@Override
		public void clear() {}
	}

	public NasaWorkbenchAssemblyRecipe(ResourceLocation name, ItemStack blueprint, int minimumTier, int flux, INasaWorkbenchRecipe recipe, int numInputs)
	{
		this.name = name;
		this.recipe = recipe;
		this.blueprint = blueprint;
		this.tier = minimumTier;
		this.flux = flux;
		this.numInputs = numInputs;
	}

	@Override
	public boolean isInputValid(World world, ItemStack blueprint, int tier, ItemStack... inputs)
	{
		if (!ItemStack.areItemsEqual(blueprint, this.blueprint) || tier < this.tier) return false;
		DummyInventory inv = new DummyInventory(inputs);
		return recipe.matches(inv);
	}

	@Override
	public ItemStack process(World world, ItemStack blueprint, int tier, boolean simulate, ItemStack... items)
	{
		if (!simulate)
			for (ItemStack item : items)
				if (!item.isEmpty()) item.shrink(1);
		return recipe.getRecipeOutput();
	}

	@Override
	public ItemStack[] getDefaultBlueprint()
	{
		return new ItemStack[] {blueprint};
	}

	@Override
	public boolean isBlueprintValid(ItemStack blueprint, int tier)
	{
		return tier >= this.tier && ItemStack.areItemsEqual(blueprint, this.blueprint);
	}

	@Override
	public int getDefaultTier()
	{
		return tier;
	}

	@Override
	public ItemStack getDefaultOutput()
	{
		return recipe.getRecipeOutput();
	}

	@Override
	public int getDefaultUsedFlux()
	{
		return flux;
	}

	@Override
	public ItemStack[][] getDefaultInput(ItemStack blueprint, int tier)
	{
		ItemStack[][] input = new ItemStack[numInputs][];
		for (int i = 0; i < input.length; i++)
		{
			ItemStack stack = recipe.getRecipeInput().get(i + 1);
			if (stack != null) input[i] = new ItemStack[] {stack};
			else input[i] = new ItemStack[0];
		}
		return input;
	}

	@Override
	public int requiredFlux(World world, ItemStack blueprint, int tier, ItemStack... inputs)
	{
		return flux;
	}

	@Override
	public ResourceLocation getUniqueName()
	{
		return name;
	}

	@Override
	public boolean isPartialInputValid(World world, ItemStack blueprint, int tier, ItemStack... existingInputs)
	{
		for (int i = 0; i < numInputs; i++)
		{
			ItemStack stack = existingInputs[i];
			if (!stack.isEmpty())
			{
				ItemStack target = recipe.getRecipeInput().get(i + 1);
				if (target == null || target.isEmpty() || !(target.getItemDamage() == OreDictionary.WILDCARD_VALUE ? ItemStack.areItemsEqualIgnoreDurability(stack, target) : ItemStack.areItemsEqual(stack, target))) return false;
			}
		}
		return true;
	}

	@Override
	public String toString()
	{
		return "NASA Assembly Recipe ID " + name;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this) return true;
		else if (o == null || o.getClass() != this.getClass()) return false;
		else return ((NasaWorkbenchAssemblyRecipe) o).name.equals(this.name);
	}
}
