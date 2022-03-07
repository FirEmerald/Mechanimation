package firemerald.mechanimation.tileentity.multiblock;

import firemerald.mechanimation.util.flux.IFluxReceiver;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class TileEntityMultiblockFluxReceiver extends TileEntityMultiblock implements IFluxReceiver
{
	public IFluxReceiver getParentReceiver()
	{
		TileEntity tile = this.getParent();
		if (tile instanceof IFluxReceiver) return (IFluxReceiver) tile;
		else return null;
	}

	@Override
	public int getEnergyStored(EnumFacing from)
	{
		IFluxReceiver parent = getParentReceiver();
		return parent == null ? 0 : parent.getEnergyStored(from);
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from)
	{
		IFluxReceiver parent = getParentReceiver();
		return parent == null ? 0 : parent.getMaxEnergyStored(from);
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from)
	{
		IFluxReceiver parent = getParentReceiver();
		return parent == null ? false : parent.canConnectEnergy(from);
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate)
	{
		IFluxReceiver parent = getParentReceiver();
		return parent == null ? 0 : parent.receiveEnergy(from, maxReceive, simulate);
	}
}