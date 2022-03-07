package firemerald.mechanimation.tileentity.machine.distillation_tower;

import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.distillation.DistillationRecipes;
import firemerald.mechanimation.api.crafting.distillation.IDistillationRecipe;
import firemerald.mechanimation.client.gui.inventory.IGuiElements;
import firemerald.mechanimation.compat.jei.ConfigJEI;
import firemerald.mechanimation.compat.jei.recipe.distillation.RecipeCategoryDistillation;
import firemerald.mechanimation.inventory.container.ContainerMachine;
import firemerald.mechanimation.inventory.slot.SlotMachine;
import firemerald.mechanimation.tileentity.machine.base.RenderInfo;
import firemerald.mechanimation.tileentity.machine.base.energy.CommonEnergyPredicates;
import firemerald.mechanimation.tileentity.machine.base.energy.EnergyInventoryTemplate;
import firemerald.mechanimation.tileentity.machine.base.energy.EnergyInventoryTemplateBuilder;
import firemerald.mechanimation.tileentity.machine.base.energy.IEnergySlot;
import firemerald.mechanimation.tileentity.machine.base.fluids.CommonFluidPredicates;
import firemerald.mechanimation.tileentity.machine.base.fluids.FluidInventoryTemplate;
import firemerald.mechanimation.tileentity.machine.base.fluids.FluidInventoryTemplateBuilder;
import firemerald.mechanimation.tileentity.machine.base.fluids.IFluidSlot;
import firemerald.mechanimation.tileentity.machine.base.implementation.actual.vertical.TileEntityVerticalEnergyFluidItemMachineBase;
import firemerald.mechanimation.tileentity.machine.base.items.CommonItemPredicates;
import firemerald.mechanimation.tileentity.machine.base.items.IItemSlot;
import firemerald.mechanimation.tileentity.machine.base.items.ItemInventoryTemplate;
import firemerald.mechanimation.tileentity.machine.base.items.ItemInventoryTemplateBuilder;
import firemerald.mechanimation.tileentity.machine.base.operations.CommonOperations;
import firemerald.mechanimation.tileentity.machine.base.operations.OperationsTemplate;
import firemerald.mechanimation.util.EnumFace;
import firemerald.mechanimation.util.Utils;
import firemerald.mechanimation.util.flux.IFluxReceiver;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileEntityDistillationTower<T extends TileEntityDistillationTower<T>> extends TileEntityVerticalEnergyFluidItemMachineBase<T, IDistillationRecipe> implements IFluxReceiver
{
    public static final ResourceLocation DISTILLATION_TOWER_GUI_TEXTURES = new ResourceLocation(MechanimationAPI.MOD_ID, "textures/gui/container/distillation_tower.png");
    public static final String
	ENERGY_INDEX_ENERGY = "energy",
	FLUID_INDEX_INPUT = "fluid_input",
	FLUID_INDEX_OUTPUT_TOP = "fluid_output_top",
	FLUID_INDEX_OUTPUT_UPPER = "fluid_output_upper",
	FLUID_INDEX_OUTPUT_MIDDLE = "fluid_output_middle",
	FLUID_INDEX_OUTPUT_BOTTOM = "fluid_output_bottom";
	@SuppressWarnings("unchecked")
	public static final ItemInventoryTemplate<? extends TileEntityDistillationTower<?>> ITEM_TEMPLATE = ((ItemInventoryTemplateBuilder<? extends TileEntityDistillationTower<?>>) new ItemInventoryTemplateBuilder<>())
    		.addSlot(ENERGY_INDEX_ENERGY, machine -> 64, machine -> 64, machine -> Utils::isEnergyProvider, machine -> 64, machine -> stack -> !Utils.isEnergyProvider(stack), machine -> EnumFace.values())
    		.addSlot(FLUID_INDEX_INPUT, machine -> 64, machine -> 64, CommonItemPredicates.providesFluidTo(machine -> machine.FLUID_INDEX_FLUID_INPUT), machine -> 64, CommonItemPredicates.doesNotProvideFluidTo(machine -> machine.FLUID_INDEX_FLUID_INPUT), machine -> EnumFace.SIDES)
    		.addSlot(FLUID_INDEX_OUTPUT_TOP, machine -> 64, machine -> 64, CommonItemPredicates.receivesFluidFrom(machine -> machine.FLUID_INDEX_FLUID_OUTPUT_TOP), machine -> 64, CommonItemPredicates.doesNotReceiveFluidFrom(machine -> machine.FLUID_INDEX_FLUID_OUTPUT_TOP), machine -> EnumFace.NONE)
    		.addSlot(FLUID_INDEX_OUTPUT_UPPER, machine -> 64, machine -> 64, CommonItemPredicates.receivesFluidFrom(machine -> machine.FLUID_INDEX_FLUID_OUTPUT_UPPER), machine -> 64, CommonItemPredicates.doesNotReceiveFluidFrom(machine -> machine.FLUID_INDEX_FLUID_OUTPUT_UPPER), machine -> EnumFace.NONE)
    		.addSlot(FLUID_INDEX_OUTPUT_MIDDLE, machine -> 64, machine -> 64, CommonItemPredicates.receivesFluidFrom(machine -> machine.FLUID_INDEX_FLUID_OUTPUT_MIDDLE), machine -> 64, CommonItemPredicates.doesNotReceiveFluidFrom(machine -> machine.FLUID_INDEX_FLUID_OUTPUT_MIDDLE), machine -> EnumFace.NONE)
    		.addSlot(FLUID_INDEX_OUTPUT_BOTTOM, machine -> 64, machine -> 64, CommonItemPredicates.receivesFluidFrom(machine -> machine.FLUID_INDEX_FLUID_OUTPUT_BOTTOM), machine -> 64, CommonItemPredicates.doesNotReceiveFluidFrom(machine -> machine.FLUID_INDEX_FLUID_OUTPUT_BOTTOM), machine -> new EnumFace[] { EnumFace.BOTTOM })
    		.build();
	@SuppressWarnings("unchecked")
	public static final FluidInventoryTemplate<? extends TileEntityDistillationTower<?>> FLUID_TEMPLATE = ((FluidInventoryTemplateBuilder<? extends TileEntityDistillationTower<?>>) new FluidInventoryTemplateBuilder<>())
			.addSlot(FLUID_INDEX_INPUT, TileEntityDistillationTower::getMaxFluid, TileEntityDistillationTower::getMaxFluidTransfer, machine -> DistillationRecipes::isCraftable, TileEntityDistillationTower::getMaxFluidTransfer, CommonFluidPredicates.accept(), machine -> EnumFace.SIDES)
			.addSlot(FLUID_INDEX_OUTPUT_TOP, TileEntityDistillationTower::getMaxFluid, machine -> 0, CommonFluidPredicates.deny(), TileEntityDistillationTower::getMaxFluidTransfer, CommonFluidPredicates.accept(), machine -> EnumFace.NONE)
			.addSlot(FLUID_INDEX_OUTPUT_UPPER, TileEntityDistillationTower::getMaxFluid, machine -> 0, CommonFluidPredicates.deny(), TileEntityDistillationTower::getMaxFluidTransfer, CommonFluidPredicates.accept(), machine -> EnumFace.NONE)
			.addSlot(FLUID_INDEX_OUTPUT_MIDDLE, TileEntityDistillationTower::getMaxFluid, machine -> 0, CommonFluidPredicates.deny(), TileEntityDistillationTower::getMaxFluidTransfer, CommonFluidPredicates.accept(), machine -> EnumFace.NONE)
			.addSlot(FLUID_INDEX_OUTPUT_BOTTOM, TileEntityDistillationTower::getMaxFluid, machine -> 0, CommonFluidPredicates.deny(), TileEntityDistillationTower::getMaxFluidTransfer, CommonFluidPredicates.accept(), machine -> new EnumFace[] { EnumFace.BOTTOM })
    		.build();
	@SuppressWarnings({ "unchecked"})
	public static final EnergyInventoryTemplate<? extends TileEntityDistillationTower<?>> ENERGY_TEMPLATE = ((EnergyInventoryTemplateBuilder<? extends TileEntityDistillationTower<?>>) new EnergyInventoryTemplateBuilder<>())
			.addSlot(ENERGY_INDEX_ENERGY, TileEntityDistillationTower::getMaxEnergy, TileEntityDistillationTower::getMaxEnergyTransfer, CommonEnergyPredicates.accept(), machine -> 0, CommonEnergyPredicates.deny(), machine -> EnumFace.values())
    		.build();
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static final OperationsTemplate<TileEntityDistillationTower> OPERATION_TEMPLATE = new OperationsTemplate<TileEntityDistillationTower>()
			.addOperation(CommonOperations.<TileEntityDistillationTower>energyFromItem(machine -> machine.ITEM_INDEX_ENERGY_INPUT, machine -> machine.ENERGY_INDEX_ENERGY_INPUT, TileEntityDistillationTower::getMaxEnergyTransferItem))
			.addOperation(CommonOperations.<TileEntityDistillationTower>fluidFromItem(machine -> machine.ITEM_INDEX_FLUID_INPUT, machine -> machine.FLUID_INDEX_FLUID_INPUT, TileEntityDistillationTower::getMaxFluidTransferItem))
			.addOperation(CommonOperations.<TileEntityDistillationTower>fluidToItem(machine -> machine.ITEM_INDEX_FLUID_OUTPUT_TOP, machine -> machine.FLUID_INDEX_FLUID_OUTPUT_TOP, TileEntityDistillationTower::getMaxFluidTransferItem))
			.addOperation(CommonOperations.<TileEntityDistillationTower>fluidToItem(machine -> machine.ITEM_INDEX_FLUID_OUTPUT_UPPER, machine -> machine.FLUID_INDEX_FLUID_OUTPUT_UPPER, TileEntityDistillationTower::getMaxFluidTransferItem))
			.addOperation(CommonOperations.<TileEntityDistillationTower>fluidToItem(machine -> machine.ITEM_INDEX_FLUID_OUTPUT_MIDDLE, machine -> machine.FLUID_INDEX_FLUID_OUTPUT_MIDDLE, TileEntityDistillationTower::getMaxFluidTransferItem))
			.addOperation(CommonOperations.<TileEntityDistillationTower>fluidToItem(machine -> machine.ITEM_INDEX_FLUID_OUTPUT_BOTTOM, machine -> machine.FLUID_INDEX_FLUID_OUTPUT_BOTTOM, TileEntityDistillationTower::getMaxFluidTransferItem));

	public final IItemSlot
	ITEM_INDEX_ENERGY_INPUT,
	ITEM_INDEX_FLUID_INPUT,
	ITEM_INDEX_FLUID_OUTPUT_TOP,
	ITEM_INDEX_FLUID_OUTPUT_UPPER,
	ITEM_INDEX_FLUID_OUTPUT_MIDDLE,
	ITEM_INDEX_FLUID_OUTPUT_BOTTOM;
	public final IFluidSlot
	FLUID_INDEX_FLUID_INPUT,
	FLUID_INDEX_FLUID_OUTPUT_TOP,
	FLUID_INDEX_FLUID_OUTPUT_UPPER,
	FLUID_INDEX_FLUID_OUTPUT_MIDDLE,
	FLUID_INDEX_FLUID_OUTPUT_BOTTOM;
	public final IEnergySlot
	ENERGY_INDEX_ENERGY_INPUT;
	@SideOnly(Side.CLIENT)
	public RenderInfo
	renderDimsEnergyInput,
	renderDimsFluidInput,
	renderDimsFluidOutputTop,
	renderDimsFluidOutputUpper,
	renderDimsFluidOutputMiddle,
	renderDimsFluidOutputBottom;

    protected int progress = 0;

    public abstract int getMaxEnergyUsagePerTick();

    @SuppressWarnings("unchecked")
	public TileEntityDistillationTower()
    {
    	super(((ItemInventoryTemplate<T>) ITEM_TEMPLATE)::build, ((FluidInventoryTemplate<T>) FLUID_TEMPLATE)::build, ((EnergyInventoryTemplate<T>) ENERGY_TEMPLATE)::build);
    	ITEM_INDEX_ENERGY_INPUT = this.itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(ENERGY_INDEX_ENERGY));
    	ITEM_INDEX_FLUID_INPUT = this.itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(FLUID_INDEX_INPUT));
    	ITEM_INDEX_FLUID_OUTPUT_TOP = this.itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(FLUID_INDEX_OUTPUT_TOP));
    	ITEM_INDEX_FLUID_OUTPUT_UPPER = this.itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(FLUID_INDEX_OUTPUT_UPPER));
    	ITEM_INDEX_FLUID_OUTPUT_MIDDLE = this.itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(FLUID_INDEX_OUTPUT_MIDDLE));
    	ITEM_INDEX_FLUID_OUTPUT_BOTTOM = this.itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(FLUID_INDEX_OUTPUT_BOTTOM));
    	FLUID_INDEX_FLUID_INPUT = this.fluidInventory.getFluidSlot(FLUID_TEMPLATE.getIndex(FLUID_INDEX_INPUT));
    	FLUID_INDEX_FLUID_OUTPUT_TOP = this.fluidInventory.getFluidSlot(FLUID_TEMPLATE.getIndex(FLUID_INDEX_OUTPUT_TOP));
    	FLUID_INDEX_FLUID_OUTPUT_UPPER = this.fluidInventory.getFluidSlot(FLUID_TEMPLATE.getIndex(FLUID_INDEX_OUTPUT_UPPER));
    	FLUID_INDEX_FLUID_OUTPUT_MIDDLE = this.fluidInventory.getFluidSlot(FLUID_TEMPLATE.getIndex(FLUID_INDEX_OUTPUT_MIDDLE));
    	FLUID_INDEX_FLUID_OUTPUT_BOTTOM = this.fluidInventory.getFluidSlot(FLUID_TEMPLATE.getIndex(FLUID_INDEX_OUTPUT_BOTTOM));
    	ENERGY_INDEX_ENERGY_INPUT = this.energyInventory.getEnergySlot(ENERGY_TEMPLATE.getIndex(ENERGY_INDEX_ENERGY));
    	this.operations = OPERATION_TEMPLATE.build(this);
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
		{
			renderDimsEnergyInput = new RenderInfo(8, 9, IGuiElements.ENERGY_BAR_PROGRESS);
			renderDimsFluidInput = new RenderInfo(35, 26, IGuiElements.FLUID_BAR_DECOR);
			renderDimsFluidOutputTop = new RenderInfo(157, 26, IGuiElements.FLUID_BAR_DECOR);
			renderDimsFluidOutputUpper = new RenderInfo(135, 26, IGuiElements.FLUID_BAR_DECOR);
			renderDimsFluidOutputMiddle = new RenderInfo(113, 26, IGuiElements.FLUID_BAR_DECOR);
			renderDimsFluidOutputBottom = new RenderInfo(91, 26, IGuiElements.FLUID_BAR_DECOR);
		}
    }

	public abstract int getMaxFluid();

	public abstract int getMaxFluidTransfer();

	public abstract int getMaxFluidTransferItem();

	public abstract int getMaxEnergy();

	public abstract int getMaxEnergyTransfer();

	public abstract int getMaxEnergyTransferItem();

    @Override
	public void readFromDisk(NBTTagCompound compound)
    {
        super.readFromDisk(compound);
        this.progress = compound.getInteger("progress");
    }

    @Override
	public void writeToDisk(NBTTagCompound compound)
    {
        super.writeToDisk(compound);
        compound.setInteger("progress", progress);
    }

    @Override
	public IDistillationRecipe getValidRecipe()
    {
    	FluidOrGasStack fluidInput = FLUID_INDEX_FLUID_INPUT.getFluid();
    	if (fluidInput == null ) return null;
    	IDistillationRecipe recipe = DistillationRecipes.getRecipe(fluidInput);
    	if (DistillationRecipes.isCraftable(recipe))
    	{
    		int energyStored = ENERGY_INDEX_ENERGY_INPUT.getEnergyStored();
    		if (recipe.getRequiredFlux() > energyStored) return null;
    		FluidOrGasStack testStack = null;
    		for (FluidOrGasStack fluid : recipe.getFluidInput())
    		{
    			if (fluid.isFluidEqual(fluidInput) && fluid.getAmount() <= fluidInput.getAmount())
    			{
    				testStack = fluid;
    				break;
    			}
    		}
    		if (testStack == null) return null;
    		FluidOrGasStack outputFluidTop = recipe.getFluidOutputTop();
    		FluidOrGasStack outputFluidUpper = recipe.getFluidOutputUpper();
    		FluidOrGasStack outputFluidMiddle = recipe.getFluidOutputMiddle();
    		FluidOrGasStack outputFluidBottom = recipe.getFluidOutputBottom();
    		if (outputFluidTop != null)
    		{
    	    	FluidOrGasStack fluidOutputTop = FLUID_INDEX_FLUID_OUTPUT_TOP.getFluid();
    			if (fluidOutputTop != null)
    			{
    				if (!fluidOutputTop.isFluidEqual(outputFluidTop) || fluidOutputTop.getAmount() + outputFluidTop.getAmount() > FLUID_INDEX_FLUID_OUTPUT_TOP.getCapacity()) return null;
    			}
    			else
    			{
    				if (outputFluidTop.getAmount() > FLUID_INDEX_FLUID_OUTPUT_TOP.getCapacity()) return null;
    			}
    		}
    		if (outputFluidUpper != null)
    		{
    	    	FluidOrGasStack fluidOutputUpper = FLUID_INDEX_FLUID_OUTPUT_UPPER.getFluid();
    			if (fluidOutputUpper != null)
    			{
    				if (!fluidOutputUpper.isFluidEqual(outputFluidUpper) || fluidOutputUpper.getAmount() + outputFluidUpper.getAmount() > FLUID_INDEX_FLUID_OUTPUT_UPPER.getCapacity()) return null;
    			}
    			else
    			{
    				if (outputFluidUpper.getAmount() > FLUID_INDEX_FLUID_OUTPUT_UPPER.getCapacity()) return null;
    			}
    		}
    		if (outputFluidMiddle != null)
    		{
    	    	FluidOrGasStack fluidOutputMiddle = FLUID_INDEX_FLUID_OUTPUT_MIDDLE.getFluid();
    			if (fluidOutputMiddle != null)
    			{
    				if (!fluidOutputMiddle.isFluidEqual(outputFluidMiddle) || fluidOutputMiddle.getAmount() + outputFluidMiddle.getAmount() > FLUID_INDEX_FLUID_OUTPUT_MIDDLE.getCapacity()) return null;
    			}
    			else
    			{
    				if (outputFluidMiddle.getAmount() > FLUID_INDEX_FLUID_OUTPUT_MIDDLE.getCapacity()) return null;
    			}
    		}
    		if (outputFluidBottom != null)
    		{
    	    	FluidOrGasStack fluidOutputBottom = FLUID_INDEX_FLUID_OUTPUT_BOTTOM.getFluid();
    			if (fluidOutputBottom != null)
    			{
    				if (!fluidOutputBottom.isFluidEqual(outputFluidBottom) || fluidOutputBottom.getAmount() + outputFluidBottom.getAmount() > FLUID_INDEX_FLUID_OUTPUT_BOTTOM.getCapacity()) return null;
    			}
    			else
    			{
    				if (outputFluidBottom.getAmount() > FLUID_INDEX_FLUID_OUTPUT_BOTTOM.getCapacity()) return null;
    			}
    		}
    		return recipe;
    	}
    	else return null;
    }

	@Override
	public void processRecipe(IDistillationRecipe recipe) //TODO onFluidSlotChanged
	{
		FluidOrGasStack testStack = null;
    	FluidOrGasStack fluidInput = FLUID_INDEX_FLUID_INPUT.getFluid();
		for (FluidOrGasStack fluid : recipe.getFluidInput())
		{
			if (fluid.isFluidEqual(fluidInput) && fluid.getAmount() <= fluidInput.getAmount())
			{
				testStack = fluid;
				break;
			}
		}
		if (testStack == null) return;
		FluidOrGasStack outputFluidTop = recipe.getFluidOutputTop();
		FluidOrGasStack outputFluidUpper = recipe.getFluidOutputUpper();
		FluidOrGasStack outputFluidMiddle = recipe.getFluidOutputMiddle();
		FluidOrGasStack outputFluidBottom = recipe.getFluidOutputBottom();
		if (outputFluidTop != null) FLUID_INDEX_FLUID_OUTPUT_TOP.add(outputFluidTop);
		if (outputFluidUpper != null) FLUID_INDEX_FLUID_OUTPUT_UPPER.add(outputFluidUpper);
		if (outputFluidMiddle != null) FLUID_INDEX_FLUID_OUTPUT_MIDDLE.add(outputFluidMiddle);
		if (outputFluidBottom != null) FLUID_INDEX_FLUID_OUTPUT_BOTTOM.add(outputFluidBottom);
		FLUID_INDEX_FLUID_INPUT.remove(testStack.getAmount());
		ENERGY_INDEX_ENERGY_INPUT.remove(recipe.getRequiredFlux());
	}


    @Override
	public String getGuiID()
    {
        return "mechanimation:distillation_tower";
    }

    @Override
	public int getField(int id)
    {
        switch (id)
        {
        case 0:
        	return this.isRunning ? this.progress : 0;
        case 1:
        	IDistillationRecipe recipe = this.getValidRecipe();
        	return recipe != null ? recipe.getRequiredFlux() : 1;
        default:
        	return 0;
        }
    }

    @Override
	public void setField(int id, int value)
    {
        switch (id)
        {
            case 0:
                this.progress = value;
                break;
        }
    } //not used

    @Override
	public int getFieldCount()
    {
        return 2;
    }

	@Override
	@SideOnly(Side.CLIENT)
	public RenderInfo getFluidRenderInfo(int index)
	{
		if (index == FLUID_INDEX_FLUID_INPUT.getSlot()) return renderDimsFluidInput;
		else if (index == FLUID_INDEX_FLUID_OUTPUT_TOP.getSlot()) return renderDimsFluidOutputTop;
		else if (index == FLUID_INDEX_FLUID_OUTPUT_UPPER.getSlot()) return renderDimsFluidOutputUpper;
		else if (index == FLUID_INDEX_FLUID_OUTPUT_MIDDLE.getSlot()) return renderDimsFluidOutputMiddle;
		else if (index == FLUID_INDEX_FLUID_OUTPUT_BOTTOM.getSlot()) return renderDimsFluidOutputBottom;
		else return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public RenderInfo getEnergyRenderInfo(int index)
	{
		return index == ENERGY_INDEX_ENERGY_INPUT.getSlot() ? renderDimsEnergyInput : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addItemSlotsToContainer(ContainerMachine<T> container)
	{
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_ENERGY_INPUT.getSlot(), 8, 53));
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_FLUID_INPUT.getSlot(), 30, 53));
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_FLUID_OUTPUT_TOP.getSlot(), 152, 53));
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_FLUID_OUTPUT_UPPER.getSlot(), 130, 53));
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_FLUID_OUTPUT_MIDDLE.getSlot(), 108, 53));
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_FLUID_OUTPUT_BOTTOM.getSlot(), 86, 53));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getGuiTexture()
	{
		return DISTILLATION_TOWER_GUI_TEXTURES;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderProgressMeters(GuiContainer gui, int offX, int offY, float zLevel) {
		// TODO Auto-generated method stub

	}

	@Override
	@SideOnly(Side.CLIENT)
	public String[] getRecipeTypes(int adjX, int adjY)
	{
		if (ConfigJEI.INSTANCE.distillationRecipes.val && adjY >= 26 && adjY < 72 && adjX >= 60 && adjX < 72) return new String[] {RecipeCategoryDistillation.ID};
		else return null;
	}

	@Override
	public String getDefaultName()
	{
		return "container.mechanimation.distillation_tower";
	}

	@Override
	public void onStoppedRunning()
	{
		progress = 0;
	}

	@Override
	public boolean canProcessRecipeThisTick(IDistillationRecipe recipe)
	{
		return this.progress >= recipe.getRequiredFlux();
	}

	@Override
	public void onUsageTick(IDistillationRecipe recipe)
	{
		progress += this.getMaxEnergyUsagePerTick();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		return new AxisAlignedBB(pos.getX() - 1, pos.getY() -1, pos.getZ() - 1, pos.getX() + 2, pos.getY() + 4, pos.getZ() + 2);
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate)
	{
		return super.receiveEnergy(from, maxReceive, simulate);
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