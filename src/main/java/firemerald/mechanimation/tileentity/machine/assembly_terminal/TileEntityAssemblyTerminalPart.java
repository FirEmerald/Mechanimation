package firemerald.mechanimation.tileentity.machine.assembly_terminal;

import firemerald.mechanimation.blocks.machine.BlockAssemblyTerminalMultiblock;
import firemerald.mechanimation.tileentity.machine.assembly_terminal.TileEntityAssemblyTerminalBase.AssemblyTableInventory;
import firemerald.mechanimation.tileentity.machine.base.SidedSlots;
import firemerald.mechanimation.tileentity.machine.base.energy.IEnergyInventory;
import firemerald.mechanimation.tileentity.machine.base.energy.IEnergySlot;
import firemerald.mechanimation.tileentity.machine.base.implementation.viewing.oriented.TileEntityOrientedEnergyItemView;
import firemerald.mechanimation.tileentity.machine.base.items.IItemSlot;
import firemerald.mechanimation.util.EnumFace;
import firemerald.mechanimation.util.flux.IFluxReceiver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class TileEntityAssemblyTerminalPart<T extends TileEntityAssemblyTerminalBase<T>> extends TileEntityOrientedEnergyItemView<T> implements IFluxReceiver
{
	@Override
	public BlockPos getViewedPos()
	{
		IBlockState state = getWorld().getBlockState(getPos());
		if (state.getBlock() instanceof BlockAssemblyTerminalMultiblock)
		{
			BlockPos pos = ((BlockAssemblyTerminalMultiblock<?>) state.getBlock()).getParentPos(getWorld(), getPos(), state);
			return pos == null ? getPos() : pos;
		}
		else return getPos();
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from)
	{
		return super.canConnectEnergy(from);
	}

	@Override
	public void onViewChanged()
	{
		super.onViewChanged();
		SidedSlots<IItemSlot> sidedItemSlots = new SidedSlots<>();
		SidedSlots<IEnergySlot> sidedEnergySlots = new SidedSlots<>();
		if (this.viewedMachine != null)
		{
			AssemblyTableInventory<T> viewedItems = viewedMachine.assemblyInventory;
			sidedItemSlots.addSlot(viewedItems.getItemSlot(TileEntityAssemblyTerminalBase.ITEM_TEMPLATE.getIndex(TileEntityAssemblyTerminalBase.ENERGY_INDEX_ENERGY)), EnumFace.values());
			for (IItemSlot slot : viewedItems.additionalSlots) sidedItemSlots.addSlot(slot, EnumFace.SIDES);
			IEnergyInventory<T> viewedEnergy = viewedMachine.getEnergyInventory();
			sidedEnergySlots.addSlot(viewedEnergy.getEnergySlot(TileEntityAssemblyTerminalBase.ENERGY_TEMPLATE.getIndex(TileEntityAssemblyTerminalBase.ENERGY_INDEX_ENERGY)), EnumFace.values());
		}
		this.itemView.setSidedSlots(sidedItemSlots.getIndexArray());
		this.energyView.setSidedSlots(sidedEnergySlots.getIndexArray());
	}

	@Override
	public Class<? super T> getViewClass()
	{
		return TileEntityAssemblyTerminalBase.class;
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate)
	{
		return super.receiveEnergy(from, maxReceive, simulate);
	}

	@Override
	public int getEnergyStored(EnumFacing from)
	{
		return super.getEnergyStored(from);
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from)
	{
		return super.getMaxEnergyStored(from);
	}
}