package firemerald.mechanimation.api.crafting.generator.combustion;

import java.util.List;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;

public interface ICombustionGeneratorRecipe
{
	public List<FluidOrGasStack> fuel(); //stack amount ignored

	public boolean isInputValid(FluidOrGasStack fuel);

	public float speed(); //rotations per second

	public int ticksPerMillibucket(); //ticks per millibucket of fuel
}