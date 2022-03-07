package firemerald.mechanimation.api.tileentity;

import javax.annotation.Nullable;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTankInfo;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public interface IFluidTankInfo extends GasTankInfo, IFluidTankProperties
{
	IFluidTankInfo[] NONE = new IFluidTankInfo[0];

    @Nullable
    public FluidOrGasStack getFluidOrGas();

    public boolean canFill(@Nullable FluidOrGasStack type);

    public boolean canDrain(@Nullable FluidOrGasStack type);

    @Override
    @Nullable
    public default FluidStack getContents()
    {
    	return FluidOrGasStack.getFluidStackStatic(getFluidOrGas());
    }

    @Override
    public default boolean canFill()
    {
    	return canFill(null);
    }

    @Override
    public default boolean canDrain()
    {
    	return canDrain(null);
    }

    @Override
    public default boolean canFillFluidType(FluidStack fluidStack)
    {
    	return canFill(FluidOrGasStack.forFluid(fluidStack));
    }

    @Override
    public default boolean canDrainFluidType(FluidStack fluidStack)
    {
    	return canDrain(FluidOrGasStack.forFluid(fluidStack));
    }

    @Override
    public default GasStack getGas()
    {
    	return FluidOrGasStack.getGasStackStatic(getFluidOrGas());
    }

    @Override
    public default int getStored()
    {
    	return FluidOrGasStack.getAmountStatic(getFluidOrGas());
    }

    @Override
    public default int getMaxGas()
    {
    	return getCapacity();
    }
}