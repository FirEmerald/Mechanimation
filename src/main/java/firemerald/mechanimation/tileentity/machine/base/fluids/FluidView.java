package firemerald.mechanimation.tileentity.machine.base.fluids;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.util.EnumFace;
import net.minecraft.nbt.NBTTagCompound;

public class FluidView<T extends IFluidMachine<T>> implements IFluidInventory<T>
{
	@Nullable
	public IFluidInventory<T> viewing;
	protected int[] unsidedSlots = new int[0];
	protected IFluidSlot[] unsidedFluidSlots = new IFluidSlot[0];
	public int[][] sidedSlots = new int[EnumFace.values().length][0];
	private IFluidSlot[][] sidedFluidSlots = new IFluidSlot[EnumFace.values().length][0];

	public void setSidedSlots(int[][] sidedSlots)
	{
		this.sidedSlots = sidedSlots;
		List<IFluidSlot> allSlots = new ArrayList<>();
		this.sidedFluidSlots = new IFluidSlot[sidedSlots.length][];
		for (int j = 0; j < sidedSlots.length; ++j)
		{
			int[] slots = sidedSlots[j];
			IFluidSlot[] fluidSlots = this.sidedFluidSlots[j] = new IFluidSlot[slots.length];
			for (int i = 0; i < slots.length; ++i)
			{
				IFluidSlot slot = fluidSlots[i] = getFluidSlot(slots[i]);
				if (!allSlots.contains(slot)) allSlots.add(slot);
			}
		}
		unsidedSlots = new int[allSlots.size()];
		unsidedFluidSlots = new IFluidSlot[allSlots.size()];
		for (int i = 0; i < unsidedSlots.length; ++i)
		{
			unsidedSlots[i] = (unsidedFluidSlots[i] = allSlots.get(i)).getSlot();
		}
	}

	@Override
	public void writeToShared(NBTTagCompound tag) {}

	@Override
	public void readFromShared(NBTTagCompound tag) {}

	@Override
	public void writeToDisk(NBTTagCompound tag) {}

	@Override
	public void readFromDisk(NBTTagCompound tag) {}

	@Override
	public int numSlots()
	{
		return viewing == null ? 0 : viewing.numSlots();
	}

	@Override
	public IFluidSlot[] getFluidSlots()
	{
		return viewing == null ? new IFluidSlot[0] : viewing.getFluidSlots();
	}

	@Override
	public IFluidSlot getFluidSlot(int slot)
	{
		return viewing == null ? null : viewing.getFluidSlot(slot);
	}

	@Override
	public int[] getSlots(EnumFace side)
	{
		return viewing == null ? new int[0] : side == null ? unsidedSlots : sidedSlots[side.ordinal()];
	}

	@Override
	public IFluidSlot[] getFluidSlots(EnumFace side)
	{
		return viewing == null ? new IFluidSlot[0] : side == null ? unsidedFluidSlots : sidedFluidSlots[side.ordinal()];
	}

	@Override
	public boolean isEmpty()
	{
		return viewing == null ? true : viewing.isEmpty();
	}

	@Override
	public FluidOrGasStack getFluidOrGas(int slot)
	{
		return viewing == null ? null : viewing.getFluidOrGas(slot);
	}
}