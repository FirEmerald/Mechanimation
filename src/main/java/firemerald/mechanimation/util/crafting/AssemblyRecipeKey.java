package firemerald.mechanimation.util.crafting;

import firemerald.craftloader.api.IRecipeKey;
import firemerald.mechanimation.api.crafting.assembly_terminal.AssemblyBlueprint;
import net.minecraft.util.ResourceLocation;

public class AssemblyRecipeKey implements IRecipeKey
{
	public final ResourceLocation name;
	public final AssemblyBlueprint blueprint;

	public AssemblyRecipeKey(ResourceLocation name, AssemblyBlueprint blueprint)
	{
		this.name = name;
		this.blueprint = blueprint;
	}

	@Override
	public String getModDomain()
	{
		return name.getResourceDomain();
	}

	@Override
	public String toString()
	{
		return name.toString() + " of " + blueprint.toString();
	}

	@Override
	public int hashCode()
	{
		return name.hashCode() ^ blueprint.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == null) return false;
		if (o == this) return true;
		if (o.getClass() == this.getClass()) return name.equals(((AssemblyRecipeKey) o).name) && blueprint.equals(((AssemblyRecipeKey) o).blueprint);
		return false;
	}
}
