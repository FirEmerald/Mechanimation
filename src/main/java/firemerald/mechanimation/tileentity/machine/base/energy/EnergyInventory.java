package firemerald.mechanimation.tileentity.machine.base.energy;

import firemerald.mechanimation.tileentity.machine.base.SidedSlots;
import firemerald.mechanimation.util.EnumFace;
import net.minecraft.nbt.NBTTagCompound;

public class EnergyInventory<T extends IEnergyMachine<T>> implements IEnergyInventory<T>
{
	private final IEnergySlot[] slots;
	private final int[] slotIds;
	private final int[][] sidedSlots;
	private final IEnergySlot[][] sidedSlotInstances;

	public EnergyInventory(T machine, EnergySlotInfo<T>[] slots)
	{
		this.slots = new IEnergySlot[slots.length];
		this.slotIds = new int[slots.length];
		SidedSlots<IEnergySlot> sidedSlots = new SidedSlots<>();
		for (int i = 0; i < slots.length; ++i)
		{
			EnergySlotInfo<T> info = slots[i];
			IEnergySlot slot = this.slots[i] = info.makeSlot(machine);
			sidedSlots.addSlot(slot, info.sides.apply(machine));
			slotIds[i] = slot.getSlot();
		}
		this.sidedSlots = sidedSlots.getIndexArray();
		this.sidedSlotInstances = sidedSlots.getSlotArray(IEnergySlot[][]::new, IEnergySlot[]::new);
	}

	@Override
	public int numSlots()
	{
		return slots.length;
	}

	@Override
	public IEnergySlot[] getEnergySlots()
	{
		return slots;
	}

	@Override
	public IEnergySlot getEnergySlot(int slot)
	{
		if (slot < 0 || slot >= slots.length) return null;
		else return slots[slot];
	}

	@Override
	public int[] getSlots(EnumFace side)
	{
		return side == null ? slotIds : sidedSlots[side.ordinal()];
	}

	@Override
	public IEnergySlot[] getEnergySlots(EnumFace side)
	{
		return sidedSlotInstances[side.ordinal()];
	}

	@Override
	public boolean isEmpty()
	{
		for (IEnergySlot slot : slots) if (!slot.isEmpty()) return false;
		return true;
	}

	@Override
	public void writeToDisk(NBTTagCompound tag)
	{
		NBTTagCompound storage = tag.getCompoundTag("energyStorage");
		for (IEnergySlot slot : slots) slot.writeToDisk(storage);
		tag.setTag("energyStorage", storage);
	}

	@Override
	public void readFromDisk(NBTTagCompound tag)
	{
		NBTTagCompound storage = tag.getCompoundTag("energyStorage");
		for (IEnergySlot slot : slots) slot.readFromDisk(storage);
	}

	@Override
	public void writeToShared(NBTTagCompound tag)
	{
		NBTTagCompound storage = tag.getCompoundTag("energyStorage");
		for (IEnergySlot slot : slots) slot.writeToShared(storage);
		tag.setTag("energyStorage", storage);
	}

	@Override
	public void readFromShared(NBTTagCompound tag)
	{
		NBTTagCompound storage = tag.getCompoundTag("energyStorage");
		for (IEnergySlot slot : slots) slot.readFromShared(storage);
	}
}