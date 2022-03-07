package firemerald.mechanimation.tileentity.machine.base.implementation.actual.oriented;

import java.util.function.Function;

import firemerald.mechanimation.tileentity.machine.base.IModeledOrientedMachine;
import firemerald.mechanimation.tileentity.machine.base.implementation.actual.TileEntityItemMachineBase;
import firemerald.mechanimation.tileentity.machine.base.items.IItemInventory;
import firemerald.mechanimation.util.EnumOrientation;
import firemerald.mechanimation.util.Utils;
import net.minecraft.nbt.NBTTagCompound;

public abstract class TileEntityOrientedItemMachineBase<T extends TileEntityOrientedItemMachineBase<T, R>, R> extends TileEntityItemMachineBase<T, R> implements IModeledOrientedMachine
{
	public EnumOrientation orientation = EnumOrientation.UP_SOUTH;

	public TileEntityOrientedItemMachineBase(Function<T, IItemInventory<T>> itemInventory)
	{
		super(itemInventory);
	}

	@Override
	public EnumOrientation getOrientation()
	{
		return orientation;
	}

	@Override
	public void setOrientation(EnumOrientation orientation)
	{
		this.orientation = orientation;
		setNeedsUpdate();
	}

	@Override
	public void writeToShared(NBTTagCompound tag)
	{
		tag.setString("orientation", orientation.name());
		super.writeToShared(tag);
	}

	@Override
	public void readFromShared(NBTTagCompound tag)
	{
        orientation = Utils.getEnum(tag.getString("orientation"), EnumOrientation.values(), EnumOrientation.UP_SOUTH);
		super.readFromShared(tag);
	}
}