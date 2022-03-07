package firemerald.mechanimation.util.flux;

import cofh.redstoneflux.api.IEnergyHandler;
import net.minecraft.util.EnumFacing;

public interface IFluxHandler extends IFluxConnection, IEnergyHandler
{
	/**
	 * Returns the amount of energy currently stored.
	 */
	@Override
	int getEnergyStored(EnumFacing from);

	/**
	 * Returns the maximum amount of energy that can be stored.
	 */
	@Override
	int getMaxEnergyStored(EnumFacing from);
}