package firemerald.mechanimation.compat.jei;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.client.gui.inventory.IIngredientGui;
import mezz.jei.api.gui.IAdvancedGuiHandler;
import net.minecraft.client.gui.inventory.GuiContainer;

public class IngredientGuiHandler<T extends GuiContainer & IIngredientGui> implements IAdvancedGuiHandler<T>
{
	private Class<T> clazz;

	public IngredientGuiHandler(Class<T> clazz)
	{
		this.clazz = clazz;
	}

	@Nonnull
	@Override
	public Class<T> getGuiContainerClass()
	{
		return clazz;
	}

	@Nullable
	@Override
	public Object getIngredientUnderMouse(T guiContainer, int mouseX, int mouseY)
	{
		Object ingred = guiContainer.getIngredient(mouseX, mouseY);
		if (ingred instanceof FluidOrGasStack)
		{
			FluidOrGasStack stack = (FluidOrGasStack) ingred;
			return stack.isFluid() ? stack.getFluidStack() : stack.getGasStack();
		}
		else return ingred;
	}
}