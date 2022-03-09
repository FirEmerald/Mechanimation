package firemerald.mechanimation.tileentity.machine.arc_furnace;

import javax.annotation.Nullable;

import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.api.crafting.arc_furnace.ArcFurnaceRecipes;
import firemerald.mechanimation.api.crafting.arc_furnace.IArcFurnaceRecipe;
import firemerald.mechanimation.client.gui.inventory.IGuiElements;
import firemerald.mechanimation.inventory.container.ContainerMachine;
import firemerald.mechanimation.inventory.slot.SlotMachine;
import firemerald.mechanimation.tileentity.machine.base.IModeledOrientedMachine;
import firemerald.mechanimation.tileentity.machine.base.RenderInfo;
import firemerald.mechanimation.tileentity.machine.base.energy.CommonEnergyPredicates;
import firemerald.mechanimation.tileentity.machine.base.energy.EnergyCapWrapper;
import firemerald.mechanimation.tileentity.machine.base.energy.EnergyInventoryTemplate;
import firemerald.mechanimation.tileentity.machine.base.energy.EnergyInventoryTemplateBuilder;
import firemerald.mechanimation.tileentity.machine.base.energy.IEnergyGuiMachine;
import firemerald.mechanimation.tileentity.machine.base.energy.IEnergyInventory;
import firemerald.mechanimation.tileentity.machine.base.energy.IEnergySlot;
import firemerald.mechanimation.tileentity.machine.base.fluids.CommonFluidPredicates;
import firemerald.mechanimation.tileentity.machine.base.fluids.FluidInventoryTemplate;
import firemerald.mechanimation.tileentity.machine.base.fluids.FluidInventoryTemplateBuilder;
import firemerald.mechanimation.tileentity.machine.base.items.CommonItemPredicates;
import firemerald.mechanimation.tileentity.machine.base.items.IItemSlot;
import firemerald.mechanimation.tileentity.machine.base.items.ItemInventoryTemplate;
import firemerald.mechanimation.tileentity.machine.base.items.ItemInventoryTemplateBuilder;
import firemerald.mechanimation.tileentity.machine.base.operations.CommonOperations;
import firemerald.mechanimation.tileentity.machine.base.operations.OperationsTemplate;
import firemerald.mechanimation.util.EnumFace;
import firemerald.mechanimation.util.EnumOrientation;
import firemerald.mechanimation.util.Utils;
import firemerald.mechanimation.util.flux.IFluxReceiver;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileEntityArcFurnaceBase<T extends TileEntityArcFurnaceBase<T>> extends TileEntitySteelingFurnaceBase<T> implements IModeledOrientedMachine, IEnergyGuiMachine<T>, IFluxReceiver
{
    public static final ResourceLocation ARC_FURNACE_GUI_TEXTURES = new ResourceLocation(MechanimationAPI.MOD_ID, "textures/gui/container/arc_furnace.png");
	public static final String
	ENERGY_INDEX_ENERGY = "energy";
	@SuppressWarnings("unchecked")
	public static final ItemInventoryTemplate<? extends TileEntityArcFurnaceBase<?>> ITEM_TEMPLATE = ((ItemInventoryTemplateBuilder<? extends TileEntityArcFurnaceBase<?>>) new ItemInventoryTemplateBuilder<>())
    		.addSlot(ITEM_INDEX_INPUT, machine -> 64, machine -> 64, machine -> ArcFurnaceRecipes::isCraftable, machine -> 64, CommonItemPredicates.accept(), TileEntityArcFurnaceBase::itemInputSides)
    		.addSlot(FLUID_INDEX_OUTPUT, machine -> 64, machine -> 64, CommonItemPredicates.receivesFluidFrom(machine -> machine.FLUID_INDEX_FLUID_OUTPUT), machine -> 64, CommonItemPredicates.doesNotReceiveFluidFrom(machine -> machine.FLUID_INDEX_FLUID_OUTPUT), TileEntityArcFurnaceBase::fluidOutputSides)
    		.addSlot(ENERGY_INDEX_ENERGY, machine -> 64, machine -> 64, machine -> Utils::isEnergyProvider, machine -> 64, machine -> stack -> !Utils.isEnergyProvider(stack), machine -> EnumFace.values())
    		.build();
	@SuppressWarnings("unchecked")
	public static final FluidInventoryTemplate<? extends TileEntityArcFurnaceBase<?>> FLUID_TEMPLATE = ((FluidInventoryTemplateBuilder<? extends TileEntityArcFurnaceBase<?>>) new FluidInventoryTemplateBuilder<>())
			.addSlot(FLUID_INDEX_OUTPUT, TileEntityArcFurnaceBase::getMaxFluid, machine -> 0, CommonFluidPredicates.deny(), TileEntityArcFurnaceBase::getMaxFluidTransfer, CommonFluidPredicates.accept(), TileEntityArcFurnaceBase::fluidOutputSides)
    		.build();
	@SuppressWarnings({ "unchecked"})
	public static final EnergyInventoryTemplate<? extends TileEntityArcFurnaceBase<?>> ENERGY_TEMPLATE = ((EnergyInventoryTemplateBuilder<? extends TileEntityArcFurnaceBase<?>>) new EnergyInventoryTemplateBuilder<>())
			.addSlot(ENERGY_INDEX_ENERGY, TileEntityArcFurnaceBase::getMaxEnergy, TileEntityArcFurnaceBase::getMaxEnergyTransfer, CommonEnergyPredicates.accept(), machine -> 0, CommonEnergyPredicates.deny(), machine -> EnumFace.values())
    		.build();
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static final OperationsTemplate<TileEntityArcFurnaceBase> OPERATION_TEMPLATE = new OperationsTemplate<TileEntityArcFurnaceBase>()
			.addOperation(CommonOperations.<TileEntityArcFurnaceBase>fluidToItem(machine -> machine.ITEM_INDEX_FLUID_OUTPUT, machine -> machine.FLUID_INDEX_FLUID_OUTPUT, TileEntityArcFurnaceBase::getMaxFluidTransferItem))
			.addOperation(CommonOperations.<TileEntityArcFurnaceBase>energyFromItem(machine -> machine.ITEM_INDEX_BATTERY_INPUT, machine -> machine.ENERGY_INDEX_ENERGY_INPUT, TileEntityArcFurnaceBase::getMaxEnergyTransferItem));


	public final IItemSlot
	ITEM_INDEX_BATTERY_INPUT;
	public final IEnergySlot
	ENERGY_INDEX_ENERGY_INPUT;
	@SideOnly(Side.CLIENT)
	public RenderInfo
	renderDimsEnergyInput;
	@SuppressWarnings("unchecked")
	private final IEnergyStorage[] facedEnergyHandlers = new IEnergyStorage[] {
			new EnergyCapWrapper<>((T) this, EnumFace.values()[0]),
			new EnergyCapWrapper<>((T) this, EnumFace.values()[1]),
			new EnergyCapWrapper<>((T) this, EnumFace.values()[2]),
			new EnergyCapWrapper<>((T) this, EnumFace.values()[3]),
			new EnergyCapWrapper<>((T) this, EnumFace.values()[4]),
			new EnergyCapWrapper<>((T) this, EnumFace.values()[5])
	};
	public final IEnergyInventory<T> energyInventory;
	public EnumOrientation orientation = EnumOrientation.UP_SOUTH;

	@SuppressWarnings("unchecked")
	public TileEntityArcFurnaceBase()
	{
		super((ItemInventoryTemplate<T>) ITEM_TEMPLATE, (FluidInventoryTemplate<T>) FLUID_TEMPLATE);
		this.energyInventory = ((EnergyInventoryTemplate<T>) ENERGY_TEMPLATE).build((T) this);
		ITEM_INDEX_BATTERY_INPUT = this.itemInventory.getItemSlot(ENERGY_TEMPLATE.getIndex(ENERGY_INDEX_ENERGY));
		ENERGY_INDEX_ENERGY_INPUT = this.energyInventory.getEnergySlot(ENERGY_TEMPLATE.getIndex(ENERGY_INDEX_ENERGY));
		this.operations = OPERATION_TEMPLATE.build(this);
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
		{
			renderDimsEnergyInput = new RenderInfo(8, 9, IGuiElements.ENERGY_BAR_PROGRESS);
		}
	}

	public abstract int getMaxFluid();

	public abstract int getMaxFluidTransfer();

	public abstract int getMaxFluidTransferItem();

	public abstract int getMaxEnergy();

	public abstract int getMaxEnergyTransfer();

	public abstract int getMaxEnergyTransferItem();

	public abstract EnumFace[] itemInputSides();

	public abstract EnumFace[] fluidOutputSides();

	@Override
	@SideOnly(Side.CLIENT)
	public RenderInfo getEnergyRenderInfo(int index)
	{
		return renderDimsEnergyInput;
	}

	@Override
	public IEnergyInventory<T> getEnergyInventory()
	{
		return this.energyInventory;
	}

	public boolean isBurning = false;

	@Override
	public void update()
	{
		isBurning = false;
		super.update();
	}

	@Override
	public float getFuelTemp()
	{
		return Math.min(ENERGY_INDEX_ENERGY_INPUT.getEnergyStored(), this.getMaxEnergyUsedPerTick()) * this.heatPerRF();
	}

	public abstract float heatPerRF();

	@Override
	public boolean isBurning()
	{
		return isBurning;
	}

	@Override
	public void startBurning()
	{
		if (!ENERGY_INDEX_ENERGY_INPUT.isEmpty()) this.isBurning = true;
	}

	@Override
	public void consumeFuel()
	{
		ENERGY_INDEX_ENERGY_INPUT.remove(getMaxEnergyUsedPerTick());
	}

	@Override
    public AxisAlignedBB getChamberBox()
    {
		return new AxisAlignedBB(this.pos);
    }

	@Override
	public boolean canProcessRecipeThisTick(IArcFurnaceRecipe recipe)
	{
		if (super.canProcessRecipeThisTick(recipe)) return !ENERGY_INDEX_ENERGY_INPUT.isEmpty();
		else return false;
	}

	public abstract int getMaxEnergyUsedPerTick();

    @Override
	public String getGuiID()
    {
        return "mechanimation:arc_furnace";
    }

	@Override
	public String getDefaultName()
	{
		return "container.mechanimation.arc_furnace";
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addItemSlotsToContainer(ContainerMachine<T> container)
	{
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_ITEM_INPUT.getSlot(), 56, 35));
		//TODO add battery slot!
		super.addItemSlotsToContainer(container);
	}

	@Override
    @SideOnly(Side.CLIENT)
	public ResourceLocation getGuiTexture()
	{
		return ARC_FURNACE_GUI_TEXTURES;
	}

	@Override
    @SideOnly(Side.CLIENT)
	public void renderProgressMeters(GuiContainer gui, int offX, int offY, float zLevel)
	{
		super.renderProgressMeters(gui, offX, offY, zLevel);
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
		getEnergyInventory().writeToShared(tag);
		super.writeToShared(tag);
	}

	@Override
	public void readFromShared(NBTTagCompound tag)
	{
        orientation = Utils.getEnum(tag.getString("orientation"), EnumOrientation.values(), EnumOrientation.UP_SOUTH);
		getEnergyInventory().readFromShared(tag);
		super.readFromShared(tag);
	}

	@Override
	public void readFromDisk(NBTTagCompound tag)
	{
		super.readFromDisk(tag);
		getEnergyInventory().readFromDisk(tag);
	}

	@Override
	public void writeToDisk(NBTTagCompound tag)
	{
		super.writeToDisk(tag);
		getEnergyInventory().writeToDisk(tag);
	}

    @Override
	public boolean hasCapabilityLocal(Capability<?> capability, @Nullable EnumFacing facing)
    {
    	if (capability == CapabilityEnergy.ENERGY)
    	{
    		return getEnergyInventory().getSlots(getFace(facing)).length > 0;
    	}
    	else return super.hasCapabilityLocal(capability, facing);
    }

    @Override
	@SuppressWarnings("unchecked")
	public <S> S getCapabilityLocal(Capability<S> capability, @Nullable EnumFacing facing)
    {
    	if (capability == CapabilityEnergy.ENERGY)
    	{
    		if (facing == null) return (S) this.getEnergyInventory();
    		else return (S) facedEnergyHandlers[getFace(facing).ordinal()];
    	}
    	else return super.getCapabilityLocal(capability, facing);
    }

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate)
	{
		return IEnergyGuiMachine.super.receiveEnergy(from, maxReceive, simulate);
	}

	@Override
	public int getEnergyStored(EnumFacing from)
	{
		return IEnergyGuiMachine.super.getEnergyStored(from);
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from)
	{
		return IEnergyGuiMachine.super.getMaxEnergyStored(from);
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from)
	{
		return IEnergyGuiMachine.super.canConnectEnergy(from);
	}
}