package firemerald.mechanimation.tileentity.machine.distillation_tower;

import firemerald.mechanimation.tileentity.machine.base.SidedSlots;
import firemerald.mechanimation.tileentity.machine.base.fluids.IFluidInventory;
import firemerald.mechanimation.tileentity.machine.base.fluids.IFluidSlot;
import firemerald.mechanimation.tileentity.machine.base.items.IItemInventory;
import firemerald.mechanimation.tileentity.machine.base.items.IItemSlot;
import firemerald.mechanimation.util.EnumFace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class TileEntityDistillationTowerTop<T extends TileEntityDistillationTower<T>> extends TileEntityDistillationTowerPart<T>
{
	@Override
	public BlockPos getViewedPos()
	{
		return getPos().offset(EnumFacing.DOWN, 2);
	}

	@Override
	public void onViewChanged()
	{
		super.onViewChanged();
		SidedSlots<IItemSlot> sidedItemSlots = new SidedSlots<>();
		SidedSlots<IFluidSlot> sidedFluidSlots = new SidedSlots<>();
		if (this.viewedMachine != null)
		{
			IItemInventory<T> viewedItem = viewedMachine.getItemInventory();
			sidedItemSlots.addSlot(viewedItem.getItemSlot(TileEntityDistillationTower.ITEM_TEMPLATE.getIndex(TileEntityDistillationTower.FLUID_INDEX_OUTPUT_UPPER)), EnumFace.SIDES);
			sidedItemSlots.addSlot(viewedItem.getItemSlot(TileEntityDistillationTower.ITEM_TEMPLATE.getIndex(TileEntityDistillationTower.FLUID_INDEX_OUTPUT_TOP)), EnumFace.TOP);
			IFluidInventory<T> viewedFluid = viewedMachine.getFluidInventory();
			sidedFluidSlots.addSlot(viewedFluid.getFluidSlot(TileEntityDistillationTower.FLUID_TEMPLATE.getIndex(TileEntityDistillationTower.FLUID_INDEX_OUTPUT_UPPER)), EnumFace.SIDES);
			sidedFluidSlots.addSlot(viewedFluid.getFluidSlot(TileEntityDistillationTower.FLUID_TEMPLATE.getIndex(TileEntityDistillationTower.FLUID_INDEX_OUTPUT_TOP)), EnumFace.TOP);
		}
		this.itemView.setSidedSlots(sidedItemSlots.getIndexArray());
		this.fluidView.setSidedSlots(sidedFluidSlots.getIndexArray());
	}
}