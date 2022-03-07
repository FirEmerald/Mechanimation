package firemerald.mechanimation.tileentity.machine.base.implementation.viewing;

import javax.annotation.Nullable;

import firemerald.mechanimation.tileentity.machine.base.energy.EnergyCapWrapper;
import firemerald.mechanimation.tileentity.machine.base.energy.EnergyView;
import firemerald.mechanimation.tileentity.machine.base.energy.IEnergyInventory;
import firemerald.mechanimation.tileentity.machine.base.energy.IEnergyMachine;
import firemerald.mechanimation.tileentity.machine.base.fluids.IFluidMachine;
import firemerald.mechanimation.util.EnumFace;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public abstract class TileEntityEnergyFluidView<T extends IEnergyMachine<T> & IFluidMachine<T>> extends TileEntityFluidView<T> implements IEnergyMachine<T>
{
	@SuppressWarnings("unchecked")
	private final IEnergyStorage[] facedEnergyHandlers = new IEnergyStorage[] {
			new EnergyCapWrapper<>((T) this, EnumFace.values()[0]),
			new EnergyCapWrapper<>((T) this, EnumFace.values()[1]),
			new EnergyCapWrapper<>((T) this, EnumFace.values()[2]),
			new EnergyCapWrapper<>((T) this, EnumFace.values()[3]),
			new EnergyCapWrapper<>((T) this, EnumFace.values()[4]),
			new EnergyCapWrapper<>((T) this, EnumFace.values()[5])
	};
	public final EnergyView<T> energyView = new EnergyView<>();

	@Override
	public IEnergyInventory<T> getEnergyInventory()
	{
		updateViewed();
		return energyView;
	}

    @Override
    public boolean hasCapabilityLocal(Capability<?> capability, @Nullable EnumFacing facing)
    {
    	if (capability == CapabilityEnergy.ENERGY)
    	{
    		return getEnergyInventory().getSlots(getFace(facing)).length > 0;
    	}
    	else return super.hasCapabilityLocal(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
	public <S> S getCapabilityLocal(Capability<S> capability, @Nullable EnumFacing facing)
    {
    	if (capability == CapabilityEnergy.ENERGY)
    	{
    		if (facing == null) return (S) this.getEnergyInventory();
    		else return (S) facedEnergyHandlers[getFace(facing).ordinal()];
    	}
    	else return super.getCapabilityLocal(capability, facing);
    }

	@Override
	public void onViewChanged()
	{
		super.onViewChanged();
		energyView.viewing = viewedMachine == null ? null : viewedMachine.getEnergyInventory();
	}
}