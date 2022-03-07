package firemerald.mechanimation.tileentity.machine.distillation_tower;

import firemerald.mechanimation.tileentity.machine.base.SidedSlots;
import firemerald.mechanimation.tileentity.machine.base.energy.IEnergyInventory;
import firemerald.mechanimation.tileentity.machine.base.energy.IEnergySlot;
import firemerald.mechanimation.tileentity.machine.base.implementation.viewing.vertical.TileEntityVerticalEnergyFluidItemView;
import firemerald.mechanimation.util.EnumFace;

public abstract class TileEntityDistillationTowerPart<T extends TileEntityDistillationTower<T>>  extends TileEntityVerticalEnergyFluidItemView<T>
{
	@Override
	public Class<? super T> getViewClass()
	{
		return TileEntityDistillationTower.class;
	}

	@Override
	public void onViewChanged()
	{
		super.onViewChanged();
		SidedSlots<IEnergySlot> sidedEnergySlots = new SidedSlots<>();
		if (this.viewedMachine != null)
		{
			IEnergyInventory<T> viewedEnergy = viewedMachine.getEnergyInventory();
			sidedEnergySlots.addSlot(viewedEnergy.getEnergySlot(TileEntityDistillationTower.ENERGY_TEMPLATE.getIndex(TileEntityDistillationTower.ENERGY_INDEX_ENERGY)), EnumFace.values());
		}
		this.energyView.setSidedSlots(sidedEnergySlots.getIndexArray());
	}
}