package firemerald.mechanimation.tileentity.machine.base.implementation.actual;

import java.util.function.Function;

import javax.annotation.Nullable;

import firemerald.mechanimation.tileentity.machine.base.items.IItemInventory;
import firemerald.mechanimation.tileentity.machine.base.items.IItemMachine;
import firemerald.mechanimation.tileentity.machine.base.items.ItemCapWrapper;
import firemerald.mechanimation.util.EnumFace;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public abstract class TileEntityItemMachineBase<T extends TileEntityItemMachineBase<T, R>, R> extends TileEntityMachineBase<T, R> implements IItemMachine<T>
{
	@SuppressWarnings("unchecked")
	private final IItemHandler[] facedItemHandlers = new IItemHandler[] {
			new ItemCapWrapper<>((T) this, EnumFace.values()[0]),
			new ItemCapWrapper<>((T) this, EnumFace.values()[1]),
			new ItemCapWrapper<>((T) this, EnumFace.values()[2]),
			new ItemCapWrapper<>((T) this, EnumFace.values()[3]),
			new ItemCapWrapper<>((T) this, EnumFace.values()[4]),
			new ItemCapWrapper<>((T) this, EnumFace.values()[5])
	};
	protected final IItemInventory<T> itemInventory;

	@SuppressWarnings("unchecked")
	public TileEntityItemMachineBase(Function<T, IItemInventory<T>> itemInventory)
	{
		this.itemInventory = itemInventory.apply((T) this);
	}

	@Override
	public IItemInventory<T> getItemInventory()
	{
		return itemInventory;
	}

	@Override
	public void writeToShared(NBTTagCompound tag)
	{
		super.writeToShared(tag);
		getItemInventory().writeToShared(tag);
	}

	@Override
	public void readFromShared(NBTTagCompound tag)
	{
		super.readFromShared(tag);
		getItemInventory().readFromShared(tag);
	}

	@Override
	public void readFromDisk(NBTTagCompound tag)
	{
		super.readFromDisk(tag);
		getItemInventory().readFromDisk(tag);
	}

	@Override
	public void writeToDisk(NBTTagCompound tag)
	{
		super.writeToDisk(tag);
		getItemInventory().writeToDisk(tag);
	}

	@Override
	public void clear()
	{
		super.clear();
		getItemInventory().clear();
	}

	@Override
    public boolean hasCapabilityLocal(Capability<?> capability, @Nullable EnumFacing facing)
    {
    	if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
    	{
    		if (facing == null) return getItemInventory().getSlots() > 0;
    		else return getItemInventory().getSlots(getFace(facing)).length > 0;
    	}
    	else return super.hasCapabilityLocal(capability, facing);
    }

	@Override
    @SuppressWarnings("unchecked")
	public <S> S getCapabilityLocal(Capability<S> capability, @Nullable EnumFacing facing)
    {
    	if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
    	{
    		if (facing == null) return (S) this.getItemInventory();
    		else return (S) facedItemHandlers[getFace(facing).ordinal()];
    	}
    	else return super.getCapabilityLocal(capability, facing);
    }
}