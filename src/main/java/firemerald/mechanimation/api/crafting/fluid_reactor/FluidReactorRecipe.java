package firemerald.mechanimation.api.crafting.fluid_reactor;

import java.util.List;

import javax.annotation.Nullable;

import firemerald.craftloader.api.SizedIngredient;
import firemerald.mechanimation.api.crafting.CraftingUtil;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;

public class FluidReactorRecipe implements IFluidReactorRecipe
{
	public final SizedIngredient inputItem;
	public final List<FluidOrGasStack> inputFluidPrimary, inputFluidSecondary, inputFluidTertiary;
	public final ItemStack outputItem;
	public final FluidOrGasStack outputFluid;
	public final float requiredSeconds;

	public FluidReactorRecipe(SizedIngredient inputItem, List<FluidOrGasStack> inputFluidPrimary, List<FluidOrGasStack> inputFluidSecondary, List<FluidOrGasStack> inputFluidTertiary, ItemStack outputItem, @Nullable FluidOrGasStack outputFluid, float requiredSeconds)
	{
		this.inputItem = inputItem;
		this.inputFluidPrimary = inputFluidPrimary;
		this.inputFluidSecondary = inputFluidSecondary;
		this.inputFluidTertiary = inputFluidTertiary;
		this.outputItem = outputItem == null ? ItemStack.EMPTY : outputItem.copy();
		this.outputFluid = outputFluid == null ? null : outputFluid.copy();
		this.requiredSeconds = requiredSeconds;
	}

	@Override
	public boolean isInputValid(ItemStack item, FluidOrGasStack fluid1, FluidOrGasStack fluid2, FluidOrGasStack fluid3)
	{
		if (inputItem != null && item != null) //requires item
		{
			if (!item.isEmpty())
			{
				if (inputItem == null) return false;
				boolean matched = false;
				SizedIngredient ingredient = this.inputItem;
				for (ItemStack ingred : ingredient.ingredient.getMatchingStacks())
				{
					if (ItemStack.areItemsEqual(ingred, item))
					{
						NBTTagCompound ingredTag = ingred.getTagCompound();
						NBTTagCompound inputTag = item.getTagCompound();
						if (inputTag == null) inputTag = new NBTTagCompound();
						if (ingredTag == null || NBTUtil.areNBTEquals(ingredTag, inputTag, true))
						{
							matched = true; //item match
							break;
						}
					}
				}
				if (!matched) return false;
			}
			else return false;
		}
		int irrelevantMatches = 0;
		if (fluid1 == null) irrelevantMatches++;
		if (fluid2 == null) irrelevantMatches++;
		if (fluid3 == null) irrelevantMatches++;
		boolean match1 = false, match2 = false, match3 = false;
		if (!inputFluidPrimary.isEmpty()) //requires primary fluid
		{
			if (fluid1 != null && CraftingUtil.containsFluid(inputFluidPrimary, fluid1)) match1 = true;
			else if (fluid2 != null && CraftingUtil.containsFluid(inputFluidPrimary, fluid2)) match2 = true;
			else if (fluid3 != null && CraftingUtil.containsFluid(inputFluidPrimary, fluid3)) match3 = true;
			else irrelevantMatches--;
		}
		if (!inputFluidSecondary.isEmpty()) //requires secondary fluid
		{
			if (!match1 && fluid1 != null && CraftingUtil.containsFluid(inputFluidSecondary, fluid1)) match1 = true;
			else if (!match2 && fluid2 != null && CraftingUtil.containsFluid(inputFluidSecondary, fluid2)) match2 = true;
			else if (!match3 && fluid3 != null && CraftingUtil.containsFluid(inputFluidSecondary, fluid3)) match3 = true;
			else irrelevantMatches--;
		}
		if (!inputFluidTertiary.isEmpty()) //requires tertiary fluid
		{
			if (!match1 && fluid1 != null && CraftingUtil.containsFluid(inputFluidTertiary, fluid1)) match1 = true;
			else if (!match2 && fluid2 != null && CraftingUtil.containsFluid(inputFluidTertiary, fluid2)) match2 = true;
			else if (!match3 && fluid3 != null && CraftingUtil.containsFluid(inputFluidTertiary, fluid3)) match3 = true;
			else irrelevantMatches--;
		}
		if (irrelevantMatches < 0) return false;
		else return true;
	}

	@Override
	public boolean isInputValidComplete(ItemStack item, FluidOrGasStack fluid1, FluidOrGasStack fluid2, FluidOrGasStack fluid3)
	{
		if (inputItem != null) //requires item
		{
			if (!item.isEmpty())
			{
				boolean matched = false;
				SizedIngredient ingredient = this.inputItem;
				for (ItemStack ingred : ingredient.ingredient.getMatchingStacks())
				{
					if (ItemStack.areItemsEqual(ingred, item))
					{
						NBTTagCompound ingredTag = ingred.getTagCompound();
						NBTTagCompound inputTag = item.getTagCompound();
						if (inputTag == null) inputTag = new NBTTagCompound();
						if (ingredTag == null || NBTUtil.areNBTEquals(ingredTag, inputTag, true))
						{
							matched = true; //item match
							break;
						}
					}
				}
				if (!matched) return false;
			}
			else return false;
		}
		boolean match1 = false, match2 = false, match3 = false;
		if (!inputFluidPrimary.isEmpty()) //requires primary fluid
		{
			if (fluid1 != null && CraftingUtil.containsFluid(inputFluidPrimary, fluid1)) match1 = true;
			else if (fluid2 != null && CraftingUtil.containsFluid(inputFluidPrimary, fluid2)) match2 = true;
			else if (fluid3 != null && CraftingUtil.containsFluid(inputFluidPrimary, fluid3)) match3 = true;
			else return false;
		}
		if (!inputFluidSecondary.isEmpty()) //requires secondary fluid
		{
			if (!match1 && fluid1 != null && CraftingUtil.containsFluid(inputFluidSecondary, fluid1)) match1 = true;
			else if (!match2 && fluid2 != null && CraftingUtil.containsFluid(inputFluidSecondary, fluid2)) match2 = true;
			else if (!match3 && fluid3 != null && CraftingUtil.containsFluid(inputFluidSecondary, fluid3)) match3 = true;
			else return false;
		}
		if (!inputFluidTertiary.isEmpty()) //requires tertiary fluid
		{
			if (!match1 && fluid1 != null && CraftingUtil.containsFluid(inputFluidTertiary, fluid1)) match1 = true;
			else if (!match2 && fluid2 != null && CraftingUtil.containsFluid(inputFluidTertiary, fluid2)) match2 = true;
			else if (!match3 && fluid3 != null && CraftingUtil.containsFluid(inputFluidTertiary, fluid3)) match3 = true;
			else return false;
		}
		return true;
	}

	@Override
	public int getRequiredCount(ItemStack item, FluidOrGasStack fluid1, FluidOrGasStack fluid2, FluidOrGasStack fluid3)
	{
		return inputItem != null && isInputValidComplete(item, fluid1, fluid2, fluid3) ? inputItem.count : 0;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this) return true;
		else if (o.getClass() == this.getClass())
		{
			FluidReactorRecipe recipe = (FluidReactorRecipe) o;
			if (ItemStack.areItemsEqual(recipe.outputItem, outputItem) && ItemStack.areItemStackTagsEqual(recipe.outputItem, outputItem))
			{
				if (recipe.inputItem == null ? this.inputItem == null : recipe.inputItem.equals(inputItem))
				{
					if (recipe.outputFluid == null ? this.outputFluid == null : (this.outputFluid.isFluidEqual(recipe.outputFluid) && this.outputFluid.getAmount() == recipe.outputFluid.getAmount()))
					{
						boolean match1 = false, match2 = false, match3 = false;
						if (this.inputFluidPrimary.equals(recipe.inputFluidPrimary)) match1 = true;
						else if (this.inputFluidSecondary.equals(recipe.inputFluidPrimary)) match2 = true;
						else if (this.inputFluidTertiary.equals(recipe.inputFluidPrimary)) match3 = true;
						else return false;
						if (!match1 && this.inputFluidPrimary.equals(recipe.inputFluidSecondary)) match1 = true;
						else if (!match2 && this.inputFluidSecondary.equals(recipe.inputFluidSecondary)) match2 = true;
						else if (!match3 && this.inputFluidTertiary.equals(recipe.inputFluidSecondary)) match3 = true;
						else return false;
						if (!match1 && this.inputFluidPrimary.equals(recipe.inputFluidTertiary)) match1 = true;
						else if (!match2 && this.inputFluidSecondary.equals(recipe.inputFluidTertiary)) match2 = true;
						else if (!match3 && this.inputFluidTertiary.equals(recipe.inputFluidTertiary)) match3 = true;
						else return false;
						return true;
					}
					else return false;
				}
				else return false;
			}
			else return false;
		}
		else return false;
	}

	@Override
	public ItemStack[] getItemInput()
	{
		if (inputItem == null) return new ItemStack[0];
		ItemStack[] inputs = this.inputItem.ingredient.getMatchingStacks();
		ItemStack[] input = new ItemStack[inputs.length];
		for (int j = 0; j < inputs.length; j++) (input[j] = inputs[j].copy()).setCount(this.inputItem.count);
		return input;
	}

	@Override
	public Object getTrueItemInput()
	{
		return this.inputItem;
	}

	@Override
	public List<FluidOrGasStack> getFluidInputPrimary()
	{
		return this.inputFluidPrimary;
	}

	@Override
	public List<FluidOrGasStack> getFluidInputSecondary()
	{
		return this.inputFluidSecondary;
	}

	@Override
	public List<FluidOrGasStack> getFluidInputTertiary()
	{
		return this.inputFluidTertiary;
	}

	@Override
	public ItemStack getItemOutput()
	{
		return this.outputItem;
	}

	@Override
	public FluidOrGasStack getFluidOutput()
	{
		return this.outputFluid;
	}

	@Override
	public float getRequiredSeconds()
	{
		return this.requiredSeconds;
	}
}