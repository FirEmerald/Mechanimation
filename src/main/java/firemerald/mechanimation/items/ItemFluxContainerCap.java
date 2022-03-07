package firemerald.mechanimation.items;

import firemerald.mechanimation.util.flux.FluxContainerItemWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class ItemFluxContainerCap extends ItemFluxContainer
{
	public ItemFluxContainerCap()
	{
		super();
	}

	public ItemFluxContainerCap(int capacity)
	{
		super(capacity);
	}

	public ItemFluxContainerCap(int capacity, int maxTransfer)
	{
		super(capacity, maxTransfer);
	}

	public ItemFluxContainerCap(int capacity, int maxReceive, int maxExtract)
	{
		super(capacity, maxReceive, maxExtract);
	}

	/* CAPABILITIES */
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt)
	{
		return new FluxContainerItemWrapper(stack, this);
	}
}