package firemerald.mechanimation.compat.jei.recipe;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.compat.jei.fluid.FluidOrGasStackRenderer;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;

public abstract class RecipeCategoryBase<T extends IRecipeWrapper> implements IRecipeCategory<T>
{
	protected IDrawableStatic background;
	protected IDrawableStatic icon;
	protected String localizedName;

	@Nonnull
	@Override
	public String getTitle()
	{
		return localizedName;
	}

	@Override
	public String getModName()
	{
		return MechanimationAPI.MOD_ID;
	}

	@Nonnull
	@Override
	public IDrawable getBackground()
	{
		return background;
	}

	@Nullable
	@Override
	public IDrawable getIcon()
	{
		return icon;
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY)
	{
		return Collections.emptyList();
	}

    protected void initFluidOrGas(IGuiIngredientGroup<FluidOrGasStack> fluidGroup, List<FluidOrGasStack> fluids, int slot, boolean input, int x, int y, int width, int height, int maxAmount, IDrawable overlay)
    {
    	fluidGroup.init(slot, input, new FluidOrGasStackRenderer(maxAmount, false, width, height, overlay), x, y, width, height, 0, 0);
    	fluidGroup.set (slot, fluids);
    }
}