package firemerald.mechanimation.api.crafting.generator.combustion;

import java.util.List;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;

public class CombustionGeneratorRecipe implements ICombustionGeneratorRecipe
{
	public final List<FluidOrGasStack> fuel;
	public final float speed;
	public final int ticksPerMillibucket;

	public CombustionGeneratorRecipe(List<FluidOrGasStack> fuel, float speed, int ticksPerMillibucket)
	{
		this.fuel = fuel;
		this.speed = speed;
		this.ticksPerMillibucket = ticksPerMillibucket;
	}

	@Override
	public boolean isInputValid(FluidOrGasStack fuel)
	{
		return fuel != null && this.fuel.parallelStream().anyMatch(stack -> FluidOrGasStack.isFluidEqualStatic(fuel, stack));
	}

	@Override
	public List<FluidOrGasStack> fuel()
	{
		return fuel;
	}

	@Override
	public float speed()
	{
		return speed;
	}

	@Override
	public int ticksPerMillibucket()
	{
		return ticksPerMillibucket;
	}
}