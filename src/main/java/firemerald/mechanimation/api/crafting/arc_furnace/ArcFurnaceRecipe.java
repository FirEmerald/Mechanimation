package firemerald.mechanimation.api.crafting.arc_furnace;

import firemerald.craftloader.api.SizedIngredient;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;

public class ArcFurnaceRecipe implements IArcFurnaceRecipe
{
	public final SizedIngredient input;
	public final FluidOrGasStack output;
	public final int temperature;

	public ArcFurnaceRecipe(SizedIngredient input, FluidOrGasStack output, int temperature)
	{
		this.input = input;
		this.output = output.copy();
		this.temperature = temperature;
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
	public FluidOrGasStack getOutput()
	{
		return output;
	}

	@Override
	public FluidOrGasStack getOutput(ItemStack stack)
	{
		return output;
	}

	@Override
	public int getTemperature()
	{
		return temperature;
	}

	@Override
	public int getVolume()
	{
		return output.getAmount();
	}

	@Override
	public boolean isInputValid(ItemStack stack)
	{
		if (stack != null && !stack.isEmpty())
		{
			if (input == null) return false;
			SizedIngredient ingredient = this.input;
			for (ItemStack ingred : ingredient.ingredient.getMatchingStacks())
			{
				if (ItemStack.areItemsEqual(ingred, stack))
				{
					NBTTagCompound ingredTag = ingred.getTagCompound();
					NBTTagCompound inputTag = stack.getTagCompound();
					if (inputTag == null) inputTag = new NBTTagCompound();
					if (ingredTag == null || NBTUtil.areNBTEquals(ingredTag, inputTag, true)) return true;
				}
			}
		}
		return false;
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
			ArcFurnaceRecipe recipe = (ArcFurnaceRecipe) o;
			if (FluidOrGasStack.isFluidEqualStatic(recipe.output, output))
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