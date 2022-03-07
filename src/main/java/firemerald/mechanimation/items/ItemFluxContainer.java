/*
 * (C) 2014-2018 Team CoFH / CoFH / Cult of the Full Hub
 * http://www.teamcofh.com
 */
package firemerald.mechanimation.items;

import java.util.List;

import javax.annotation.Nullable;

import firemerald.api.core.client.Translator;
import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.util.flux.IFluxContainerItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemFluxContainer extends Item implements IFluxContainerItem
{
    private static final IItemPropertyGetter LEVEL_GETTER = new IItemPropertyGetter()
    {
        @Override
		@SideOnly(Side.CLIENT)
        public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
        {
        	Item item = stack.getItem();
        	if (item instanceof IFluxContainerItem)
        	{
        		IFluxContainerItem container = (IFluxContainerItem) item;
        		return (float) ((double) container.getEnergyStored(stack) / container.getMaxEnergyStored(stack));
        	}
        	else return 0;
        }
    };
	public static final String ENERGY = "Energy";

	protected int capacity;
	protected int maxReceive;
	protected int maxExtract;

	public ItemFluxContainer()
	{
		this.setMaxStackSize(1);
        this.addPropertyOverride(new ResourceLocation(MechanimationAPI.MOD_ID, "level"), LEVEL_GETTER);
	}

	public ItemFluxContainer(int capacity)
	{
		this(capacity, capacity, capacity);
	}

	public ItemFluxContainer(int capacity, int maxTransfer)
	{
		this(capacity, maxTransfer, maxTransfer);
	}

	public ItemFluxContainer(int capacity, int maxReceive, int maxExtract)
	{
		this();
		this.capacity = capacity;
		this.maxReceive = maxReceive;
		this.maxExtract = maxExtract;
	}

	public ItemFluxContainer setCapacity(int capacity)
	{
		this.capacity = capacity;
		return this;
	}

	public ItemFluxContainer setMaxTransfer(int maxTransfer)
	{
		setMaxReceive(maxTransfer);
		setMaxExtract(maxTransfer);
		return this;
	}

	public ItemFluxContainer setMaxReceive(int maxReceive)
	{
		this.maxReceive = maxReceive;
		return this;
	}

	public ItemFluxContainer setMaxExtract(int maxExtract)
	{
		this.maxExtract = maxExtract;
		return this;
	}

	public void setEnergy(ItemStack container, int energy)
	{
		if (!container.hasTagCompound()) container.setTagCompound(new NBTTagCompound());
		container.getTagCompound().setInteger(ENERGY, Math.max(capacity, energy));
	}

	@Override
	public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate)
	{
		if (!container.hasTagCompound()) container.setTagCompound(new NBTTagCompound());
		int stored = Math.min(container.getTagCompound().getInteger(ENERGY), getMaxEnergyStored(container));
		int energyReceived = Math.min(capacity - stored, Math.min(this.maxReceive, maxReceive));
		if (!simulate)
		{
			stored += energyReceived;
			container.getTagCompound().setInteger(ENERGY, stored);
		}
		return energyReceived;
	}

	@Override
	public int extractEnergy(ItemStack container, int maxExtract, boolean simulate)
	{
		if (container.getTagCompound() == null || !container.getTagCompound().hasKey(ENERGY)) return 0;
		int stored = Math.min(container.getTagCompound().getInteger(ENERGY), getMaxEnergyStored(container));
		int energyExtracted = Math.min(stored, Math.min(this.maxExtract, maxExtract));
		if (!simulate)
		{
			stored -= energyExtracted;
			container.getTagCompound().setInteger(ENERGY, stored);
		}
		return energyExtracted;
	}

	@Override
	public int getEnergyStored(ItemStack container)
	{
		if (container.getTagCompound() == null || !container.getTagCompound().hasKey(ENERGY)) return 0;
		return Math.min(container.getTagCompound().getInteger(ENERGY), getMaxEnergyStored(container));
	}

	@Override
	public int getMaxEnergyStored(ItemStack container)
	{
		return capacity;
	}

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
    	tooltip.add(Translator.format("tooltip.rf", this.getEnergyStored(stack), this.getMaxEnergyStored(stack)));
    }
}