package firemerald.mechanimation.tileentity.machine.generator;

import firemerald.mechanimation.client.gui.inventory.IGuiElements;
import firemerald.mechanimation.inventory.container.ContainerMachine;
import firemerald.mechanimation.inventory.slot.SlotMachine;
import firemerald.mechanimation.tileentity.machine.base.RenderInfo;
import firemerald.mechanimation.tileentity.machine.base.energy.EnergyInventoryTemplate;
import firemerald.mechanimation.tileentity.machine.base.energy.IEnergySlot;
import firemerald.mechanimation.tileentity.machine.base.implementation.actual.oriented.TileEntityOrientedEnergyItemMachineBase;
import firemerald.mechanimation.tileentity.machine.base.items.IItemSlot;
import firemerald.mechanimation.tileentity.machine.base.items.ItemInventoryTemplate;
import firemerald.mechanimation.util.flux.IFluxProvider;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileEntityGeneratorBase<T extends TileEntityGeneratorBase<T, R>, R> extends TileEntityOrientedEnergyItemMachineBase<T, R> implements IFluxProvider
{
	public static final String
	ENERGY_INDEX_ENERGY = "energy";

	public final IItemSlot
	ITEM_INDEX_ENERGY_OUTPUT;
	public final IEnergySlot
	ENERGY_INDEX_ENERGY_OUTPUT;
	@SideOnly(Side.CLIENT)
	public RenderInfo
	renderDimsEnergyOutput;

    public TileEntityGeneratorBase(ItemInventoryTemplate<T> itemInventory, EnergyInventoryTemplate<T> energyInventory)
    {
		super(itemInventory::build, energyInventory::build);
		ITEM_INDEX_ENERGY_OUTPUT = this.itemInventory.getItemSlot(itemInventory.getIndex(ENERGY_INDEX_ENERGY));
		ENERGY_INDEX_ENERGY_OUTPUT = this.energyInventory.getEnergySlot(energyInventory.getIndex(ENERGY_INDEX_ENERGY));
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
		{
			renderDimsEnergyOutput = new RenderInfo(8, 9, IGuiElements.ENERGY_BAR_PROGRESS);
		}
    }

    public abstract int fluxProvidedPerIteration(R recipe);

	@Override
	public boolean canProcessRecipeThisTick(R recipe)
	{
		if (this.iterations < this.getMaxIterations()) return ENERGY_INDEX_ENERGY_OUTPUT.getEnergyStored() + fluxProvidedPerIteration(recipe) <= ENERGY_INDEX_ENERGY_OUTPUT.getMaxEnergyStored();
		else return false;
	}

	@Override
	public void processRecipe(R recipe)
	{
		ENERGY_INDEX_ENERGY_OUTPUT.add(fluxProvidedPerIteration(recipe));
		actuallyRunning = true;
		iterations++;
		onProcessTick(recipe);
	}

	public abstract void onProcessTick(R recipe);

	public boolean actuallyRunning = false;
	public boolean prevRunning = false;

	@Override
	public void readFromShared(NBTTagCompound tag)
	{
		super.readFromShared(tag);
		actuallyRunning = tag.getBoolean("actuallyRunning");
	}

	@Override
	public void writeToShared(NBTTagCompound tag)
	{
		super.writeToShared(tag);
		tag.setBoolean("actuallyRunning", actuallyRunning);
	}

    @Override
	public boolean shouldTickAnimation()
    {
    	return actuallyRunning;
    }

    public int iterations = 0;

    public abstract int getMaxIterations();

    @Override
	public void preProcess()
	{
    	iterations = 0;
    	prevRunning = actuallyRunning;
    	actuallyRunning = false;
	}

    @Override
	public void postProcess()
	{
    	if (actuallyRunning != prevRunning) this.setNeedsUpdate();
	}

	@Override
    public void onStoppedRunning() {}

	@Override
	public boolean useAnimation()
	{
		return true;
	}

	@Override
	public void onUsageTick(R recipe) {}

	@SuppressWarnings("unchecked")
	@Override
	@SideOnly(Side.CLIENT)
	public void addItemSlotsToContainer(ContainerMachine<T> container)
	{
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_ENERGY_OUTPUT.getSlot(), 8, 53));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public RenderInfo getEnergyRenderInfo(int index)
	{
		return renderDimsEnergyOutput;
	}

	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate)
	{
		return super.extractEnergy(from, maxExtract, simulate);
	}

	@Override
	public int getEnergyStored(EnumFacing from)
	{
		return super.getEnergyStored(from);
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from)
	{
		return super.getMaxEnergyStored(from);
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from)
	{
		return super.canConnectEnergy(from);
	}
}