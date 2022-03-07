package firemerald.mechanimation.compat.tconstruct;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.arc_furnace.IArcFurnaceRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;

public class ArcFurnaceRecipeSmeltery implements IArcFurnaceRecipe
{
	public final MeltingRecipe recipe;

	public ArcFurnaceRecipeSmeltery(MeltingRecipe recipe)
	{
		this.recipe = recipe;
	}

	@Override
	public ItemStack[] getInput()
	{
		return recipe.input.getInputs().toArray(new ItemStack[0]);
	}

	@Override
	public Object getTrueInput()
	{
		return recipe.input;
	}

	@Override
	public FluidOrGasStack getOutput()
	{
		return FluidOrGasStack.forFluid(recipe.output);
	}

	private static final double LOG9_2 = 0.31546487678;
	@Override
	public int getTemperature()
	{
		return (int) Math.ceil((recipe.temperature - 300) * Math.pow((double) recipe.output.amount / Material.VALUE_Block, -LOG9_2)); //correct for TC scaled temperature
	}

	/*
	private static int calcTemperature(FluidStack stack)
	{
		int temp = stack.getFluid().getTemperature(stack);
		int timeAmount = stack.amount;
		int base = Material.VALUE_Block;
		int max_tmp = Math.max(0, temp - 300); // we use 0 as baseline, not 300
		double f = (double) timeAmount / (double) base;

		// we calculate 2^log9(f), which effectively gives us 2^(1 for each multiple of 9)
		// so 1 = 1, 9 = 2, 81 = 4, 1/9 = 1/2, 1/81 = 1/4 etc
		// we simplify it to f^log9(2) to make calculation simpler
		f = Math.pow(f, LOG9_2);

		return 300 + (int) (f * (double) max_tmp);

		//t = 300 + (temperature - 300) * (input.amountMatched / Material.VALUE_Block) ^ LOG9_2
		//t - 300 = (temperature - 300) * (input.amountMatched / Material.VALUE_Block) ^ LOG9_2
		//(t - 300) / ((input.amountMatched / Material.VALUE_Block) ^ LOG9_2) = (temperature - 300)
		//temperature = (recipe.temperature - 300) / ((recipe.input.amountMatched / Material.VALUE_Block) ^ LOG9_2)
		//temperature = (recipe.temperature - 300) * ((recipe.input.amountMatched / Material.VALUE_Block) ^ -LOG9_2)
	}
	*/

	@Override
	public int getVolume()
	{
		return recipe.output.amount;
	}

	@Override
	public boolean isInputValid(ItemStack stack)
	{
		if (stack != null && !stack.isEmpty())
		{
			for (ItemStack ingred : recipe.input.getInputs())
			{
				if (ItemStack.areItemsEqual(ingred, stack))
				{
					NBTTagCompound ingredTag = ingred.getTagCompound();
					NBTTagCompound inputTag = stack.getTagCompound();
					if (inputTag == null) inputTag = new NBTTagCompound();
					if (ingredTag == null || NBTUtil.areNBTEquals(ingredTag, inputTag, true))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public int getRequiredCount(ItemStack stack)
	{
		return recipe.input.amountNeeded;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this) return true;
		else if (o.getClass() == this.getClass())
		{
			ArcFurnaceRecipeSmeltery recipe = (ArcFurnaceRecipeSmeltery) o;
			if (recipe.recipe.equals(recipe.recipe)) return true;
			else return false;
		}
		else return false;
	}
}