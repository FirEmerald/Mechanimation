package firemerald.mechanimation.tileentity.machine.base.implementation.actual.oriented;

import java.util.function.Function;

import firemerald.mechanimation.tileentity.machine.base.IModeledOrientedMachine;
import firemerald.mechanimation.tileentity.machine.base.energy.IEnergyInventory;
import firemerald.mechanimation.tileentity.machine.base.fluids.IFluidInventory;
import firemerald.mechanimation.tileentity.machine.base.implementation.actual.TileEntityEnergyFluidItemMachineBase;
import firemerald.mechanimation.tileentity.machine.base.items.IItemInventory;
import firemerald.mechanimation.util.EnumOrientation;
import firemerald.mechanimation.util.Utils;
import net.minecraft.nbt.NBTTagCompound;

public abstract class TileEntityOrientedEnergyFluidItemMachineBase<T extends TileEntityOrientedEnergyFluidItemMachineBase<T, R>, R> extends TileEntityEnergyFluidItemMachineBase<T, R> implements IModeledOrientedMachine
{
	public EnumOrientation orientation = EnumOrientation.UP_SOUTH;

	public TileEntityOrientedEnergyFluidItemMachineBase(Function<T, IItemInventory<T>> itemInventory, Function<T, IFluidInventory<T>> fluidInventory, Function<T, IEnergyInventory<T>> energyInventory)
	{
		super(itemInventory, fluidInventory, energyInventory);
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