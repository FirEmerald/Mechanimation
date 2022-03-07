/*
 * (C) 2014-2018 Team CoFH / CoFH / Cult of the Full Hub
 * http://www.teamcofh.com
 */
package firemerald.mechanimation.util.flux;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;

/**
 * This class provides a simple way to wrap an IEnergyContainerItem to allow for capability support. It will seamlessly allow for Forge Energy to be supported in addition to RF.
 *
 * @author King Lemming
 */
public class FluxContainerItemWrapper implements ICapabilityProvider
{
	final ItemStack stack;
	final IFluxContainerItem container;

	final boolean canExtract;
	final boolean canReceive;

	final net.minecraftforge.energy.IEnergyStorage energyCap;

	public FluxContainerItemWrapper(ItemStack stackIn, IFluxContainerItem containerIn, boolean extractIn, boolean receiveIn)
	{
		this.stack = stackIn;
		this.container = containerIn;
		this.canExtract = extractIn;
		this.canReceive = receiveIn;
		this.energyCap = new net.minecraftforge.energy.IEnergyStorage()
		{
			@Override
			public int receiveEnergy(int maxReceive, boolean simulate)
			{
				return container.receiveEnergy(stack, maxReceive, simulate);
			}

			@Override
			public int extractEnergy(int maxExtract, boolean simulate)
			{
				return container.extractEnergy(stack, maxExtract, simulate);
			}

			@Override
			public int getEnergyStored()
			{
				return container.getEnergyStored(stack);
			}

			@Override
			public int getMaxEnergyStored()
			{
				return container.getMaxEnergyStored(stack);
			}

			@Override
			public boolean canExtract()
			{
				return canExtract;
			}

			@Override
			public boolean canReceive()
			{
				return canReceive;
			}
		};
	}

	public FluxContainerItemWrapper(ItemStack stackIn, IFluxContainerItem containerIn)
	{
		this(stackIn, containerIn, true, true);
	}

	/* ICapabilityProvider */
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing from)
	{
		return capability == CapabilityEnergy.ENERGY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, final EnumFacing from)
	{
		if (!hasCapability(capability, from)) return null;
		return CapabilityEnergy.ENERGY.cast(energyCap);
	}

}
