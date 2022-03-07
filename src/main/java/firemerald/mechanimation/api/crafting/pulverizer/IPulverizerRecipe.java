package firemerald.mechanimation.api.crafting.pulverizer;

import net.minecraft.item.ItemStack;

public interface IPulverizerRecipe
{
	public ItemStack[] getInput();

	public Object getTrueInput();

	public boolean isInputValid(ItemStack stack);

	public int getRequiredCount(ItemStack stack);

	public ItemStack getOutput();

	public int getRequiredEnergy();
}
