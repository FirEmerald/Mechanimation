package firemerald.mechanimation.util.flux;

import cofh.redstoneflux.api.IEnergyConnection;
import net.minecraft.util.EnumFacing;

public interface IFluxConnection extends IEnergyConnection
{
	/**
	 * Returns TRUE if the TileEntity can connect on a given side.
	 */
	@Override
	boolean canConnectEnergy(EnumFacing from);
}