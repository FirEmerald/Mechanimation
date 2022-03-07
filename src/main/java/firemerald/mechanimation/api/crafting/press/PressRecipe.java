package firemerald.mechanimation.api.crafting.press;

import firemerald.craftloader.api.SizedIngredient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;

public class PressRecipe implements IPressRecipe
{
	public final SizedIngredient input;
	public final ItemStack output;
	public final int requiredEnergy;

	public PressRecipe(SizedIngredient input, ItemStack output, int requiredEnergy)
	{
		this.input = input;
		this.output = output.copy();
		this.requiredEnergy = requiredEnergy;
	}

	@Override
	public ItemStack[] getInput()
	{
		ItemStack[] inputs = this.input.ingredient.getMatchingStacks();
		ItemStack[] input = new ItemStack[inputs.length];
		for (int j = 0; j < inputs.length; j++) (input[j] = inputs[j].copy()).setCount(this.input.count);
		return input;
	}

	@Override
	public Object getTrueInput()
	{
		return this.input;
	}

	@Override
	public ItemStack getOutput()
	{
		return output;
	}

	@Override
	public int getRequiredEnergy()
	{
		return requiredEnergy;
	}

	@Override
	public boolean isInputValid(ItemStack primary)
	{
		if (primary != null)
		{
			boolean matched = false;
			SizedIngredient ingredient = this.input;
			for (ItemStack ingred : ingredient.ingredient.getMatchingStacks())
			{
				if (ItemStack.areItemsEqual(ingred, primary))
				{
					NBTTagCompound ingredTag = ingred.getTagCompound();
					NBTTagCompound inputTag = primary.getTagCompound();
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
		return true;
	}

	@Override
	public int getRequiredCount(ItemStack primary)
	{
		return this.input.count;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this) return true;
		else if (o.getClass() == this.getClass())
		{
			PressRecipe recipe = (PressRecipe) o;
			if (ItemStack.areItemsEqual(recipe.output, output) && ItemStack.areItemStackTagsEqual(recipe.output, output))
			{
				if (this.input.equals(recipe.input))
				{
					return true;
				}
				else return false;
			}
			else return false;
		}
		else return false;
	}
}