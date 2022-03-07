package firemerald.mechanimation.tileentity.machine.base.energy;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import firemerald.mechanimation.util.EnumFace;
import net.minecraft.nbt.NBTTagCompound;

public class EnergyView<T extends IEnergyMachine<T>> implements IEnergyInventory<T>
{
	@Nullable
	public IEnergyInventory<T> viewing;
	protected int[] unsidedSlots = new int[0];
	protected IEnergySlot[] unsidedEnergySlots = new IEnergySlot[0];
	private int[][] sidedSlots;
	private IEnergySlot[][] sidedEnergySlots;

	public void setSidedSlots(int[][] sidedSlots)
	{
		this.sidedSlots = sidedSlots;
		List<IEnergySlot> allSlots = new ArrayList<>();
		this.sidedEnergySlots = new IEnergySlot[sidedSlots.length][];
		for (int j = 0; j < sidedSlots.length; ++j)
		{
			int[] slots = sidedSlots[j];
			IEnergySlot[] energySlots = this.sidedEnergySlots[j] = new IEnergySlot[slots.length];
			for (int i = 0; i < slots.length; ++i)
			{
				IEnergySlot slot = energySlots[i] = getEnergySlot(slots[i]);
				if (!allSlots.contains(slot)) allSlots.add(slot);
			}
		}
		unsidedSlots = new int[allSlots.size()];
		unsidedEnergySlots = new IEnergySlot[allSlots.size()];
		for (int i = 0; i < unsidedSlots.length; ++i)
		{
			unsidedSlots[i] = (unsidedEnergySlots[i] = allSlots.get(i)).getSlot();
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
	public boolean isEmpty()
	{
		return viewing == null ? true : viewing.isEmpty();
	}

	@Override
	public IEnergySlot[] getEnergySlots()
	{
		return viewing == null ? new IEnergySlot[0] : viewing.getEnergySlots();
	}

	@Override
	public IEnergySlot getEnergySlot(int slot)
	{
		return viewing == null ? null : viewing.getEnergySlot(slot);
	}

	@Override
	public int[] getSlots(EnumFace side)
	{
		return viewing == null ? new int[0] : side == null ? unsidedSlots : sidedSlots[side.ordinal()];
	}

	@Override
	public IEnergySlot[] getEnergySlots(EnumFace side)
	{
		return viewing == null ? new IEnergySlot[0] : side == null ? unsidedEnergySlots : sidedEnergySlots[side.ordinal()];
	}
}