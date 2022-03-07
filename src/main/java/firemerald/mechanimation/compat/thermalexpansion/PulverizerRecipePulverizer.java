package firemerald.mechanimation.compat.thermalexpansion;

import java.util.ArrayList;
import java.util.List;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager.PulverizerRecipe;
import firemerald.mechanimation.api.crafting.pulverizer.IPulverizerRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraftforge.oredict.OreDictionary;

public class PulverizerRecipePulverizer implements IPulverizerRecipe
{
	public final PulverizerRecipe recipe;

	public PulverizerRecipePulverizer(PulverizerRecipe recipe)
	{
		this.recipe = recipe;
	}

	@Override
	public ItemStack[] getInput()
	{
		int oreID = PulverizerManager.convertInput(recipe.getInput()).oreID;
		List<ItemStack> recipeInputs = new ArrayList<>();
		if (oreID != -1) for (ItemStack ore : OreDictionary.getOres(ItemHelper.oreProxy.getOreName(oreID), false)) recipeInputs.add(ItemHelper.cloneStack(ore, recipe.getInput().getCount()));
		else recipeInputs.add(recipe.getInput());
		return recipeInputs.toArray(new ItemStack[recipeInputs.size()]);
	}

	@Override
	public Object getTrueInput()
	{
		int oreID = PulverizerManager.convertInput(recipe.getInput()).oreID;
		if (oreID != -1) return ItemHelper.oreProxy.getOreName(oreID);
		else return recipe.getInput();
	}

	@Override
	public ItemStack getOutput()
	{
		return recipe.getPrimaryOutput();
	}

	@Override
	public int getRequiredEnergy()
	{
		return recipe.getEnergy();
	}

	@Override
	public boolean isInputValid(ItemStack stack)
	{
		if (this.getInput().length == 0) return false;
		if (stack == null) return true;
		for (ItemStack ingred : this.getInput())
		{
			if (ItemStack.areItemsEqual(ingred, stack))
			{
				NBTTagCompound ingredTag = ingred.getTagCompound();
				NBTTagCompound inputTag = stack.getTagCompound();
				if (inputTag == null) inputTag = new NBTTagCompound();
				if (ingredTag == null || NBTUtil.areNBTEquals(ingredTag, inputTag, true)) return true; //item match
			}
		}
		return false;
	}

	@Override
	public int getRequiredCount(ItemStack stack)
	{
		return recipe.getInput().getCount();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this) return true;
		else if (o.getClass() == this.getClass())
		{
			PulverizerRecipePulverizer recipe = (PulverizerRecipePulverizer) o;
			return recipe.recipe == this.recipe;
		}
		else return false;
	}
}