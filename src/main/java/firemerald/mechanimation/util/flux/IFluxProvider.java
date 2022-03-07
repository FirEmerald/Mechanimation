package firemerald.mechanimation.util.flux;

import cofh.redstoneflux.api.IEnergyProvider;
import net.minecraft.util.EnumFacing;

public interface IFluxProvider extends IFluxHandler, IEnergyProvider
{
	/**
	 * Remove energy from an IEnergyProvider, internal distribution is left entirely to the IEnergyProvider.
	 *
	 * @param from       Orientation the energy is extracted from.
	 * @param maxExtract Maximum amount of energy to extract.
	 * @param simulate   If TRUE, the extraction will only be simulated.
	 * @return Amount of energy that was (or would have been, if simulated) extracted.
	 */
	@Override
	int extractEnergy(EnumFacing from, int maxExtract, boolean simulate);
}