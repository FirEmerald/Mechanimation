package firemerald.mechanimation.api.crafting.press;

import net.minecraft.item.ItemStack;

public interface IPressRecipe
{
	public ItemStack[] getInput();

	public Object getTrueInput();

	public boolean isInputValid(ItemStack input); //null values are wildcards for testing if an input can be accepted

	public int getRequiredCount(ItemStack input);

	public ItemStack getOutput();

	public int getRequiredEnergy();
}
