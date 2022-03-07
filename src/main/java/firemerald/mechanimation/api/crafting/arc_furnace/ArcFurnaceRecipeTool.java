package firemerald.mechanimation.api.crafting.arc_furnace;

import firemerald.craftloader.api.SizedIngredient;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class ArcFurnaceRecipeTool extends ArcFurnaceRecipe
{
	public ArcFurnaceRecipeTool(SizedIngredient input, FluidOrGasStack output, int temperature)
	{
		super(input, output, temperature);
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
	public FluidOrGasStack getOutput(ItemStack stack)
	{
		FluidOrGasStack copy = output.copy();
		copy.setAmount(getVolume(stack));
		return copy;
	}

	private static float getDurability(ItemStack stack)
	{
		return (float) stack.getItemDamage() / (float) stack.getMaxDamage();
	}

	@Override
	public int getVolume(ItemStack stack)
	{
		return output.getAmount() - MathHelper.ceil(output.getAmount() * getDurability(stack));
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
				if (!ingred.isEmpty() && stack.getItem() == ingred.getItem()) return true;
			}
		}
		return false;
	}

	@Override
	public boolean isToolMeltingRecipe()
	{
		return true;
	}
}