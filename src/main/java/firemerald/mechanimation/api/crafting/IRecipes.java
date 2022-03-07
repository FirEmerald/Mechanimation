package firemerald.mechanimation.api.crafting;

import java.util.Collection;

public interface IRecipes<R>
{
	public void init();

	public Collection<? extends R> getAllResults();
}