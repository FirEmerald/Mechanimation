package firemerald.mechanimation.tileentity.machine.base.implementation.actual.vertical;

import java.util.function.Function;

import firemerald.mechanimation.tileentity.machine.base.IModeledVerticalMachine;
import firemerald.mechanimation.tileentity.machine.base.energy.IEnergyInventory;
import firemerald.mechanimation.tileentity.machine.base.fluids.IFluidInventory;
import firemerald.mechanimation.tileentity.machine.base.implementation.actual.TileEntityEnergyFluidItemMachineBase;
import firemerald.mechanimation.tileentity.machine.base.items.IItemInventory;
import firemerald.mechanimation.util.EnumVerticalOrientation;
import firemerald.mechanimation.util.Utils;
import net.minecraft.nbt.NBTTagCompound;

public abstract class TileEntityVerticalEnergyFluidItemMachineBase<T extends TileEntityVerticalEnergyFluidItemMachineBase<T, R>, R> extends TileEntityEnergyFluidItemMachineBase<T, R> implements IModeledVerticalMachine
{
	public EnumVerticalOrientation orientation = EnumVerticalOrientation.SOUTH;

	public TileEntityVerticalEnergyFluidItemMachineBase(Function<T, IItemInventory<T>> itemInventory, Function<T, IFluidInventory<T>> fluidInventory, Function<T, IEnergyInventory<T>> energyInventory)
	{
		super(itemInventory, fluidInventory, energyInventory);
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