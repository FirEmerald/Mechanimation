package firemerald.mechanimation.tileentity.machine.base.implementation.actual.vertical;

import java.util.function.Function;

import firemerald.mechanimation.tileentity.machine.base.IModeledVerticalMachine;
import firemerald.mechanimation.tileentity.machine.base.fluids.IFluidInventory;
import firemerald.mechanimation.tileentity.machine.base.implementation.actual.TileEntityFluidItemMachineBase;
import firemerald.mechanimation.tileentity.machine.base.items.IItemInventory;
import firemerald.mechanimation.util.EnumVerticalOrientation;
import firemerald.mechanimation.util.Utils;
import net.minecraft.nbt.NBTTagCompound;

public abstract class TileEntityVerticalFluidItemMachineBase<T extends TileEntityVerticalFluidItemMachineBase<T, R>, R> extends TileEntityFluidItemMachineBase<T, R> implements IModeledVerticalMachine
{
	public EnumVerticalOrientation orientation = EnumVerticalOrientation.SOUTH;

	public TileEntityVerticalFluidItemMachineBase(Function<T, IItemInventory<T>> itemInventory, Function<T, IFluidInventory<T>> fluidInventory)
	{
		super(itemInventory, fluidInventory);
	}

	@Override
	public EnumVerticalOrientation getFront()
	{
		return orientation;
	}

	@Override
	public void setFront(EnumVerticalOrientation orientation)
	{
		this.orientation = orientation;
		setNeedsUpdate();
	}

	@Override
	public void writeToShared(NBTTagCompound tag)
	{
		tag.setString("front", orientation.name());
		super.writeToShared(tag);
	}

	@Override
	public void readFromShared(NBTTagCompound tag)
	{
        orientation = Utils.getEnum(tag.getString("front"), EnumVerticalOrientation.values(), EnumVerticalOrientation.SOUTH);
		super.readFromShared(tag);
	}
}