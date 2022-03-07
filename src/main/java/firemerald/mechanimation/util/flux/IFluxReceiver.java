package firemerald.mechanimation.util.flux;

import cofh.redstoneflux.api.IEnergyReceiver;
import net.minecraft.util.EnumFacing;

public interface IFluxReceiver extends IFluxHandler, IEnergyReceiver
{
	/**
	 * Add energy to an IEnergyReceiver, internal distribution is left entirely to the IEnergyReceiver.
	 *
	 * @param from       Orientation the energy is received from.
	 * @param maxReceive Maximum amount of energy to receive.
	 * @param simulate   If TRUE, the charge will only be simulated.
	 * @return Amount of energy that was (or would have been, if simulated) received.
	 */
	@Override
	int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate);
}