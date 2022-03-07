package firemerald.mechanimation.api.crafting.assembly_terminal;

import firemerald.mechanimation.api.crafting.IRecipes;
import net.minecraft.item.ItemStack;

public interface IAssemblyBlueprints extends IRecipes<IAssemblyBlueprint>
{
	public IAssemblyBlueprint getResult(ItemStack blueprint, int tier);

	@Override
	public default void init()
	{
		getAllResults().forEach(IRecipes::init);
	}
}