package firemerald.mechanimation.compat.jei.recipe;

import mezz.jei.api.recipe.IRecipeWrapper;

public abstract class RecipeWrapperBase implements IRecipeWrapper
{
	public final String uId;

	public RecipeWrapperBase(String uId)
	{
		this.uId = uId;
	}

	public String getUid()
	{
		return uId;
	}
}
