package firemerald.mechanimation.tileentity.machine.electrolyzer;

import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.electrolyzer.ElectrolyzerRecipes;
import firemerald.mechanimation.api.crafting.electrolyzer.IElectrolyzerRecipe;
import firemerald.mechanimation.client.gui.inventory.IGuiElements;
import firemerald.mechanimation.compat.jei.ConfigJEI;
import firemerald.mechanimation.compat.jei.recipe.electrolyzer.RecipeCategoryElectrolyzer;
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
import firemerald.mechanimation.tileentity.machine.base.implementation.actual.oriented.TileEntityOrientedEnergyFluidItemMachineBase;
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
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileEntityElectrolyzer<T extends TileEntityElectrolyzer<T>> extends TileEntityOrientedEnergyFluidItemMachineBase<T, IElectrolyzerRecipe> implements IFluxReceiver
{
    public static final ResourceLocation ELECTROLYZER_GUI_TEXTURES = new ResourceLocation(MechanimationAPI.MOD_ID, "textures/gui/container/electrolyzer.png");
	public static final String
	ENERGY_INDEX_ENERGY = "energy",
	FLUID_INDEX_INPUT = "fluid_input",
	FLUID_INDEX_PRIMARY_OUTPUT = "primary_fluid_output",
	FLUID_INDEX_SECONDARY_OUTPUT = "secondary_fluid_output";
	public static final EnumFace[]
	FACE_FLUID_INPUT = { EnumFace.BACK },
	FACE_PRIMARY_FLUID_OUTPUT = { EnumFace.RIGHT },
	FACE_SECONDARY_FLUID_OUTPUT = { EnumFace.LEFT };
	@SuppressWarnings("unchecked")
	public static final ItemInventoryTemplate<? extends TileEntityElectrolyzer<?>> ITEM_TEMPLATE = ((ItemInventoryTemplateBuilder<? extends TileEntityElectrolyzer<?>>) new ItemInventoryTemplateBuilder<>())
    		.addSlot(ENERGY_INDEX_ENERGY, machine -> 64, machine -> 64, machine -> Utils::isEnergyProvider, machine -> 64, machine -> stack -> !Utils.isEnergyProvider(stack), machine -> EnumFace.values())
    		.addSlot(FLUID_INDEX_INPUT, machine -> 64, machine -> 64, CommonItemPredicates.providesFluidTo(machine -> machine.FLUID_INDEX_FLUID_INPUT), machine -> 64, CommonItemPredicates.doesNotProvideFluidTo(machine -> machine.FLUID_INDEX_FLUID_INPUT), machine -> FACE_FLUID_INPUT)
    		.addSlot(FLUID_INDEX_PRIMARY_OUTPUT, machine -> 64, machine -> 64, CommonItemPredicates.receivesFluidFrom(machine -> machine.FLUID_INDEX_PRIMARY_FLUID_OUTPUT), machine -> 64, CommonItemPredicates.doesNotReceiveFluidFrom(machine -> machine.FLUID_INDEX_PRIMARY_FLUID_OUTPUT), machine -> FACE_PRIMARY_FLUID_OUTPUT)
    		.addSlot(FLUID_INDEX_SECONDARY_OUTPUT, machine -> 64, machine -> 64, CommonItemPredicates.receivesFluidFrom(machine -> machine.FLUID_INDEX_SECONDARY_FLUID_OUTPUT), machine -> 64, CommonItemPredicates.doesNotReceiveFluidFrom(machine -> machine.FLUID_INDEX_SECONDARY_FLUID_OUTPUT), machine -> FACE_SECONDARY_FLUID_OUTPUT)
    		.build();
	@SuppressWarnings("unchecked")
	public static final FluidInventoryTemplate<? extends TileEntityElectrolyzer<?>> FLUID_TEMPLATE = ((FluidInventoryTemplateBuilder<? extends TileEntityElectrolyzer<?>>) new FluidInventoryTemplateBuilder<>())
			.addSlot(FLUID_INDEX_INPUT, TileEntityElectrolyzer::getMaxFluid, TileEntityElectrolyzer::getMaxFluidTransfer, machine -> ElectrolyzerRecipes::isCraftable, TileEntityElectrolyzer::getMaxFluidTransfer, CommonFluidPredicates.accept(), machine -> FACE_FLUID_INPUT)
			.addSlot(FLUID_INDEX_PRIMARY_OUTPUT, TileEntityElectrolyzer::getMaxFluid, machine -> 0, CommonFluidPredicates.deny(), TileEntityElectrolyzer::getMaxFluidTransfer, CommonFluidPredicates.accept(), machine -> FACE_PRIMARY_FLUID_OUTPUT)
			.addSlot(FLUID_INDEX_SECONDARY_OUTPUT, TileEntityElectrolyzer::getMaxFluid, machine -> 0, CommonFluidPredicates.deny(), TileEntityElectrolyzer::getMaxFluidTransfer, CommonFluidPredicates.accept(), machine -> FACE_SECONDARY_FLUID_OUTPUT)
    		.build();
	@SuppressWarnings({ "unchecked"})
	public static final EnergyInventoryTemplate<? extends TileEntityElectrolyzer<?>> ENERGY_TEMPLATE = ((EnergyInventoryTemplateBuilder<? extends TileEntityElectrolyzer<?>>) new EnergyInventoryTemplateBuilder<>())
			.addSlot(ENERGY_INDEX_ENERGY, TileEntityElectrolyzer::getMaxEnergy, TileEntityElectrolyzer::getMaxEnergyTransfer, CommonEnergyPredicates.accept(), machine -> 0, CommonEnergyPredicates.deny(), machine -> EnumFace.values())
    		.build();
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static final OperationsTemplate<TileEntityElectrolyzer> OPERATION_TEMPLATE = new OperationsTemplate<TileEntityElectrolyzer>()
			.addOperation(CommonOperations.<TileEntityElectrolyzer>energyFromItem(machine -> machine.ITEM_INDEX_ENERGY_INPUT, machine -> machine.ENERGY_INDEX_ENERGY_INPUT, TileEntityElectrolyzer::getMaxEnergyTransferItem))
			.addOperation(CommonOperations.<TileEntityElectrolyzer>fluidFromItem(machine -> machine.ITEM_INDEX_FLUID_INPUT, machine -> machine.FLUID_INDEX_FLUID_INPUT, TileEntityElectrolyzer::getMaxFluidTransferItem))
			.addOperation(CommonOperations.<TileEntityElectrolyzer>fluidToItem(machine -> machine.ITEM_INDEX_PRIMARY_FLUID_OUTPUT, machine -> machine.FLUID_INDEX_PRIMARY_FLUID_OUTPUT, TileEntityElectrolyzer::getMaxFluidTransferItem))
			.addOperation(CommonOperations.<TileEntityElectrolyzer>fluidToItem(machine -> machine.ITEM_INDEX_SECONDARY_FLUID_OUTPUT, machine -> machine.FLUID_INDEX_SECONDARY_FLUID_OUTPUT, TileEntityElectrolyzer::getMaxFluidTransferItem));

	public final IItemSlot
	ITEM_INDEX_ENERGY_INPUT,
	ITEM_INDEX_FLUID_INPUT,
	ITEM_INDEX_PRIMARY_FLUID_OUTPUT,
	ITEM_INDEX_SECONDARY_FLUID_OUTPUT;
	public final IFluidSlot
	FLUID_INDEX_FLUID_INPUT,
	FLUID_INDEX_PRIMARY_FLUID_OUTPUT,
	FLUID_INDEX_SECONDARY_FLUID_OUTPUT;
	public final IEnergySlot
	ENERGY_INDEX_ENERGY_INPUT;
	@SideOnly(Side.CLIENT)
	public RenderInfo
	renderDimsEnergyInput,
	renderDimsFluidInput,
	renderDimsPrimaryFluidOutput,
	renderDimsSecondaryFluidOutput;

    protected int progress = 0;

    public abstract int getMaxEnergyUsagePerTick();

    @SuppressWarnings("unchecked")
	public TileEntityElectrolyzer()
    {
    	super(((ItemInventoryTemplate<T>) ITEM_TEMPLATE)::build, ((FluidInventoryTemplate<T>) FLUID_TEMPLATE)::build, ((EnergyInventoryTemplate<T>) ENERGY_TEMPLATE)::build);
    	ITEM_INDEX_ENERGY_INPUT = this.itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(ENERGY_INDEX_ENERGY));
    	ITEM_INDEX_FLUID_INPUT = this.itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(FLUID_INDEX_INPUT));
    	ITEM_INDEX_PRIMARY_FLUID_OUTPUT = this.itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(FLUID_INDEX_PRIMARY_OUTPUT));
    	ITEM_INDEX_SECONDARY_FLUID_OUTPUT = this.itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(FLUID_INDEX_SECONDARY_OUTPUT));
    	FLUID_INDEX_FLUID_INPUT = this.fluidInventory.getFluidSlot(FLUID_TEMPLATE.getIndex(FLUID_INDEX_INPUT));
    	FLUID_INDEX_PRIMARY_FLUID_OUTPUT = this.fluidInventory.getFluidSlot(FLUID_TEMPLATE.getIndex(FLUID_INDEX_PRIMARY_OUTPUT));
    	FLUID_INDEX_SECONDARY_FLUID_OUTPUT = this.fluidInventory.getFluidSlot(FLUID_TEMPLATE.getIndex(FLUID_INDEX_SECONDARY_OUTPUT));
    	ENERGY_INDEX_ENERGY_INPUT = this.energyInventory.getEnergySlot(ENERGY_TEMPLATE.getIndex(ENERGY_INDEX_ENERGY));
    	this.operations = OPERATION_TEMPLATE.build(this);
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
		{
			renderDimsEnergyInput = new RenderInfo(8, 9, IGuiElements.ENERGY_BAR_PROGRESS);
			renderDimsFluidInput = new RenderInfo(85, 26, IGuiElements.FLUID_BAR_DECOR);
			renderDimsPrimaryFluidOutput = new RenderInfo(59, 26, IGuiElements.FLUID_BAR_DECOR);
			renderDimsSecondaryFluidOutput = new RenderInfo(111, 26, IGuiElements.FLUID_BAR_DECOR);
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
	public boolean canProcessRecipeThisTick(IElectrolyzerRecipe recipe)
	{
		return recipe.getRequiredFlux() <= this.progress;
	}

	@Override
    public void onStoppedRunning()
    {
		progress = 0;
    }

	@Override
	public void onUsageTick(IElectrolyzerRecipe recipe)
	{
		progress += this.getMaxEnergyUsagePerTick();
	}

    @Override
	public IElectrolyzerRecipe getValidRecipe()
    {
    	FluidOrGasStack fluidInput = FLUID_INDEX_FLUID_INPUT.getFluid();
    	if (fluidInput == null) return null;
    	IElectrolyzerRecipe recipe = ElectrolyzerRecipes.getRecipe(fluidInput);
    	if (ElectrolyzerRecipes.isCraftable(recipe))
    	{
    		if (recipe.getRequiredFlux() > ENERGY_INDEX_ENERGY_INPUT.getEnergyStored()) return null;
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
    		FluidOrGasStack outputFluidPrimary = recipe.getFluidOutputPrimary();
    		FluidOrGasStack outputFluidSecondary = recipe.getFluidOutputSecondary();
    		boolean isPrimary;
        	FluidOrGasStack fluidOutputPrimary = FLUID_INDEX_PRIMARY_FLUID_OUTPUT.getFluid();
        	FluidOrGasStack fluidOutputSecondary = FLUID_INDEX_SECONDARY_FLUID_OUTPUT.getFluid();
    		if (fluidOutputPrimary == null)
    		{
    			if (fluidOutputSecondary == null || outputFluidSecondary == null || !outputFluidSecondary.isFluidEqual(outputFluidPrimary)) isPrimary = true; //TODO should fill second tank if first empty?
    			else isPrimary = false;
    		}
    		else if (fluidOutputSecondary != null && fluidOutputSecondary.isFluidEqual(outputFluidPrimary)) isPrimary = false;
    		else isPrimary = true;
    		if (isPrimary)
    		{
    			if (outputFluidPrimary.getAmount() > FLUID_INDEX_PRIMARY_FLUID_OUTPUT.getCapacity() || (fluidOutputPrimary != null && (!fluidOutputPrimary.isFluidEqual(outputFluidPrimary) || outputFluidPrimary.getAmount() > (FLUID_INDEX_PRIMARY_FLUID_OUTPUT.getCapacity() - fluidOutputPrimary.getAmount())))) return null;
    			if (outputFluidSecondary != null && (outputFluidSecondary.getAmount() > FLUID_INDEX_SECONDARY_FLUID_OUTPUT.getCapacity() || (fluidOutputSecondary != null && (!fluidOutputSecondary.isFluidEqual(outputFluidSecondary) || outputFluidSecondary.getAmount() > (FLUID_INDEX_SECONDARY_FLUID_OUTPUT.getCapacity() - fluidOutputSecondary.getAmount()))))) return null;
    		}
    		else
    		{
    			if (outputFluidPrimary.getAmount() > FLUID_INDEX_SECONDARY_FLUID_OUTPUT.getCapacity() || (fluidOutputSecondary != null && (!fluidOutputSecondary.isFluidEqual(outputFluidPrimary) || outputFluidPrimary.getAmount() > (FLUID_INDEX_SECONDARY_FLUID_OUTPUT.getCapacity() - fluidOutputSecondary.getAmount())))) return null;
    			if (outputFluidSecondary != null && (outputFluidSecondary.getAmount() > FLUID_INDEX_PRIMARY_FLUID_OUTPUT.getCapacity() || (fluidOutputPrimary != null && (!fluidOutputPrimary.isFluidEqual(outputFluidSecondary) || outputFluidSecondary.getAmount() > (FLUID_INDEX_PRIMARY_FLUID_OUTPUT.getCapacity() - fluidOutputPrimary.getAmount()))))) return null;
    		}
    		return recipe;
    	}
    	else return null;
    }

	@Override
	public void processRecipe(IElectrolyzerRecipe recipe) //TODO onFluidSlotChanged
	{
		FluidOrGasStack fluidInput = FLUID_INDEX_FLUID_INPUT.getFluid();
		FluidOrGasStack testStack = null;
		for (FluidOrGasStack fluid : recipe.getFluidInput())
		{
			if (fluid.isFluidEqual(fluidInput) && fluid.getAmount() <= fluidInput.getAmount())
			{
				testStack = fluid;
				break;
			}
		}
		if (testStack == null) return;
		FluidOrGasStack outputFluidPrimary = recipe.getFluidOutputPrimary();
		FluidOrGasStack outputFluidSecondary = recipe.getFluidOutputSecondary();
		boolean isPrimary;
		if (FLUID_INDEX_PRIMARY_FLUID_OUTPUT.getFluid() == null)
		{
			if (FLUID_INDEX_SECONDARY_FLUID_OUTPUT.getFluid() == null || outputFluidSecondary == null || !outputFluidSecondary.isFluidEqual(outputFluidPrimary)) isPrimary = true; //TODO should fill second tank if first empty?
			else isPrimary = false;
		}
		else if (FLUID_INDEX_SECONDARY_FLUID_OUTPUT.getFluid() != null && FLUID_INDEX_SECONDARY_FLUID_OUTPUT.getFluid().isFluidEqual(outputFluidPrimary)) isPrimary = false;
		else isPrimary = true;
		if (isPrimary)
		{
			FLUID_INDEX_PRIMARY_FLUID_OUTPUT.add(outputFluidPrimary);
			if (outputFluidSecondary != null) FLUID_INDEX_SECONDARY_FLUID_OUTPUT.add(outputFluidSecondary);
		}
		else
		{
			FLUID_INDEX_SECONDARY_FLUID_OUTPUT.add(outputFluidPrimary);
			if (outputFluidSecondary != null) FLUID_INDEX_PRIMARY_FLUID_OUTPUT.add(outputFluidSecondary);
		}
		FLUID_INDEX_FLUID_INPUT.remove(testStack.getAmount());
		ENERGY_INDEX_ENERGY_INPUT.remove(recipe.getRequiredFlux());
		this.progress -= recipe.getRequiredFlux();
	}

    @Override
	public String getGuiID()
    {
        return "mechanimation:electrolyzer";
    }

    @Override
	public int getField(int id)
    {
        switch (id)
        {
        case 0:
        	return this.isRunning ? this.progress : 0;
        case 1:
        	IElectrolyzerRecipe recipe = this.getValidRecipe();
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
        }
    } //not used

    @Override
	public int getFieldCount()
    {
        return 2;
    }

	@Override
	public RenderInfo getFluidRenderInfo(int index)
	{
		if (index == FLUID_INDEX_FLUID_INPUT.getSlot()) return renderDimsFluidInput;
		else if (index == FLUID_INDEX_PRIMARY_FLUID_OUTPUT.getSlot()) return renderDimsPrimaryFluidOutput;
		else if (index == FLUID_INDEX_SECONDARY_FLUID_OUTPUT.getSlot()) return renderDimsSecondaryFluidOutput;
		else return null;
	}

	@Override
	public RenderInfo getEnergyRenderInfo(int index)
	{
		return index == ENERGY_INDEX_ENERGY_INPUT.getSlot() ? renderDimsEnergyInput : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addItemSlotsToContainer(ContainerMachine<T> container)
	{
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_ENERGY_INPUT.getSlot(), 8, 53));
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_FLUID_INPUT.getSlot(), 80, 53));
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_PRIMARY_FLUID_OUTPUT.getSlot(), 54, 53));
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_SECONDARY_FLUID_OUTPUT.getSlot(), 106, 53));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getGuiTexture()
	{
		return ELECTROLYZER_GUI_TEXTURES;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderProgressMeters(GuiContainer gui, int offX, int offY, float zLevel)
	{
		double progress = this.getProgress();
		IGuiElements.PROGRESS_HALF_LEFT_PROGRESS.render(offX + 68, offY + 25, zLevel, progress);
		IGuiElements.PROGRESS_HALF_RIGHT_PROGRESS.render(offX + 94, offY + 25, zLevel, progress);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String[] getRecipeTypes(int adjX, int adjY)
	{
		if (ConfigJEI.INSTANCE.electrolyzerRecipes.val && adjY >= 25 && adjY < 31 && ((adjX >= 94 && adjX < 108) || (adjX >= 68 && adjX < 82))) return new String[] {RecipeCategoryElectrolyzer.ID};
		else return null;
	}

	@Override
	public String getDefaultName()
	{
		return "container.mechanimation.electrolyzer";
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