package firemerald.mechanimation.tileentity.machine.arc_furnace;

import firemerald.mechanimation.tileentity.machine.base.SidedSlots;
import firemerald.mechanimation.tileentity.machine.base.implementation.viewing.vertical.TileEntityVerticalItemView;
import firemerald.mechanimation.tileentity.machine.base.items.IItemInventory;
import firemerald.mechanimation.tileentity.machine.base.items.IItemSlot;
import firemerald.mechanimation.util.EnumFace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class TileEntityBlastFurnaceTop extends TileEntityVerticalItemView<TileEntityBlastFurnace>
{
	@Override
	public BlockPos getViewedPos()
	{
		return getPos().offset(EnumFacing.DOWN, 1);
	}

	@Override
	public Class<TileEntityBlastFurnace> getViewClass()
	{
		return TileEntityBlastFurnace.class;
	}

	@Override
	public void onViewChanged()
	{
		super.onViewChanged();
		SidedSlots<IItemSlot> sidedSlots = new SidedSlots<>();
		if (this.viewedMachine != null)
		{
			IItemInventory<TileEntityBlastFurnace> viewed = viewedMachine.getItemInventory();
			sidedSlots.addSlot(viewed.getItemSlot(TileEntityBlastFurnace.ITEM_TEMPLATE.getIndex(TileEntitySteelingFurnaceBase.ITEM_INDEX_INPUT)), EnumFace.SIDES_AND_TOP);
		}
		this.itemView.setSidedSlots(sidedSlots.getIndexArray());
	}
}