package firemerald.mechanimation.tileentity.machine.base.fluids;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.tileentity.machine.base.SidedSlots;
import firemerald.mechanimation.util.EnumFace;
import net.minecraft.nbt.NBTTagCompound;

public class FluidInventory<T extends IFluidMachine<T>> implements IFluidInventory<T>
{
	private final IFluidSlot[] slots;
	private final int[] slotIds;
	private final int[][] sidedSlots;
	private final IFluidSlot[][] sidedSlotInstances;

	public FluidInventory(T machine, FluidSlotInfo<T>[] slots)
	{
		this.slots = new IFluidSlot[slots.length];
		this.slotIds = new int[slots.length];
		SidedSlots<IFluidSlot> sidedSlots = new SidedSlots<>();
		for (int i = 0; i < slots.length; ++i)
		{
			FluidSlotInfo<T> info = slots[i];
			IFluidSlot slot = this.slots[i] = info.makeSlot(machine);
			sidedSlots.addSlot(slot, info.sides.apply(machine));
			slotIds[i] = slot.getSlot();
		}
		this.sidedSlots = sidedSlots.getIndexArray();
		this.sidedSlotInstances = sidedSlots.getSlotArray(IFluidSlot[][]::new, IFluidSlot[]::new);
	}

	@Override
	public int numSlots()
	{
		return slots.length;
	}

	@Override
	public IFluidSlot[] getFluidSlots()
	{
		return slots;
	}

	@Override
	public int[] getSlots(EnumFace side)
	{
		return side == null ? slotIds : sidedSlots[side.ordinal()];
	}

	@Override
	public IFluidSlot[] getFluidSlots(EnumFace side)
	{
		return sidedSlotInstances[side.ordinal()];
	}

	@Override
	public boolean isEmpty()
	{
		for (IFluidSlot slot : slots) if (!slot.isEmpty()) return false;
		return true;
	}

	@Override
	public FluidOrGasStack getFluidOrGas(int slot)
	{
		if (slot < 0 || slot >= slots.length) return null;
		else return slots[slot].getFluid();
	}

	@Override
	public IFluidSlot getFluidSlot(int slot)
	{
		if (slot < 0 || slot >= slots.length) return null;
		else return slots[slot];
	}

	@Override
	public void writeToDisk(NBTTagCompound tag)
	{
		NBTTagCompound storage = tag.getCompoundTag("fluidStorage");
		for (IFluidSlot slot : slots) slot.writeToDisk(storage);
		tag.setTag("fluidStorage", storage);
	}

	@Override
	public void readFromDisk(NBTTagCompound tag)
	{
		NBTTagCompound storage = tag.getCompoundTag("fluidStorage");
		for (IFluidSlot slot : slots) slot.readFromDisk(storage);
	}

	@Override
	public void writeToShared(NBTTagCompound tag)
	{
		NBTTagCompound storage = tag.getCompoundTag("fluidStorage");
		for (IFluidSlot slot : slots) slot.writeToShared(storage);
		tag.setTag("fluidStorage", storage);
	}

	@Override
	public void readFromShared(NBTTagCompound tag)
	{
		NBTTagCompound storage = tag.getCompoundTag("fluidStorage");
		for (IFluidSlot slot : slots) slot.readFromShared(storage);
	}
}