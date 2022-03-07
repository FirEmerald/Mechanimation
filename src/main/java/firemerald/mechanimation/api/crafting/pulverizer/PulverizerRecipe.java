package firemerald.mechanimation.api.crafting.pulverizer;

import firemerald.craftloader.api.SizedIngredient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;

public class PulverizerRecipe implements IPulverizerRecipe
{
	public final SizedIngredient input;
	public final ItemStack output;
	public final int requiredEnergy;

	public PulverizerRecipe(SizedIngredient input, ItemStack output, int requiredEnergy)
	{
		this.input = input;
		this.output = output.copy();
		this.requiredEnergy = requiredEnergy;
	}

	@Override
	public ItemStack[] getInput()
	{
		if (input == null) return new ItemStack[0];
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
	public boolean isInputValid(ItemStack stack)
	{
		if (stack != null && !stack.isEmpty())
		{
			if (input == null) return false;
			boolean matched = false;
			SizedIngredient ingredient = this.input;
			for (ItemStack ingred : ingredient.ingredient.getMatchingStacks())
			{
				if (ItemStack.areItemsEqual(ingred, stack))
				{
					NBTTagCompound ingredTag = ingred.getTagCompound();
					NBTTagCompound inputTag = stack.getTagCompound();
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
	public int getRequiredCount(ItemStack stack)
	{
		return this.input == null ? 0 : this.input.count;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this) return true;
		else if (o.getClass() == this.getClass())
		{
			PulverizerRecipe recipe = (PulverizerRecipe) o;
			if (ItemStack.areItemsEqual(recipe.output, output) && ItemStack.areItemStackTagsEqual(recipe.output, output))
			{
				if (this.input == null ? recipe.input == null : (recipe.input != null  && this.input.equals(recipe.input)))
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