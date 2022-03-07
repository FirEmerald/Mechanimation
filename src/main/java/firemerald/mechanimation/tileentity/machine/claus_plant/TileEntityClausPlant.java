package firemerald.mechanimation.tileentity.machine.claus_plant;

import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.claus_plant.ClausPlantRecipes;
import firemerald.mechanimation.api.crafting.claus_plant.IClausPlantRecipe;
import firemerald.mechanimation.client.gui.inventory.IGuiElements;
import firemerald.mechanimation.compat.jei.ConfigJEI;
import firemerald.mechanimation.compat.jei.recipe.claus_plant.RecipeCategoryClausPlant;
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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileEntityClausPlant<T extends TileEntityClausPlant<T>> extends TileEntityOrientedEnergyFluidItemMachineBase<T, IClausPlantRecipe> implements IFluxReceiver
{
    public static final ResourceLocation CLAUS_PLANT_GUI_TEXTURES = new ResourceLocation(MechanimationAPI.MOD_ID, "textures/gui/container/claus_plant.png");
	public static final String
	ENERGY_INDEX_ENERGY = "energy",
	FLUID_INDEX_INPUT = "fluid_input",
	OXYGEN_INDEX_INPUT = "oxygen_input",
	ITEM_INDEX_OUTPUT = "item_output",
	WATER_INDEX_OUTPUT = "water_output";
	public static final EnumFace[]
	FACE_FLUID_INPUT = { EnumFace.RIGHT },
	FACE_OXYGEN_INPUT = { EnumFace.BACK },
	FACE_ITEM_OUTPUT = { EnumFace.BOTTOM },
	FACE_WATER_OUTPUT = { EnumFace.LEFT };
	@SuppressWarnings("unchecked")
	public static final ItemInventoryTemplate<? extends TileEntityClausPlant<?>> ITEM_TEMPLATE = ((ItemInventoryTemplateBuilder<? extends TileEntityClausPlant<?>>) new ItemInventoryTemplateBuilder<>())
    		.addSlot(ENERGY_INDEX_ENERGY, machine -> 64, machine -> 64, machine -> Utils::isEnergyProvider, machine -> 64, machine -> stack -> !Utils.isEnergyProvider(stack), machine -> EnumFace.values())
    		.addSlot(FLUID_INDEX_INPUT, machine -> 64, machine -> 64, CommonItemPredicates.providesFluidTo(machine -> machine.FLUID_INDEX_FLUID_INPUT), machine -> 64, CommonItemPredicates.doesNotProvideFluidTo(machine -> machine.FLUID_INDEX_FLUID_INPUT), machine -> FACE_FLUID_INPUT)
    		.addSlot(OXYGEN_INDEX_INPUT, machine -> 64, machine -> 64, CommonItemPredicates.providesFluidTo(machine -> machine.FLUID_INDEX_OXYGEN_INPUT), machine -> 64, CommonItemPredicates.doesNotProvideFluidTo(machine -> machine.FLUID_INDEX_OXYGEN_INPUT), machine -> FACE_OXYGEN_INPUT)
    		.addSlot(ITEM_INDEX_OUTPUT, machine -> 64, machine -> 0, CommonItemPredicates.deny(), machine -> 64, CommonItemPredicates.accept(), machine -> FACE_ITEM_OUTPUT)
    		.addSlot(WATER_INDEX_OUTPUT, machine -> 64, machine -> 64, CommonItemPredicates.receivesFluidFrom(machine -> machine.FLUID_INDEX_WATER_OUTPUT), machine -> 64, CommonItemPredicates.doesNotReceiveFluidFrom(machine -> machine.FLUID_INDEX_WATER_OUTPUT), machine -> FACE_WATER_OUTPUT)
    		.build();
	@SuppressWarnings("unchecked")
	public static final FluidInventoryTemplate<? extends TileEntityClausPlant<?>> FLUID_TEMPLATE = ((FluidInventoryTemplateBuilder<? extends TileEntityClausPlant<?>>) new FluidInventoryTemplateBuilder<>())
			.addSlot(FLUID_INDEX_INPUT, TileEntityClausPlant::getMaxFluid, TileEntityClausPlant::getMaxFluidTransfer, machine -> ClausPlantRecipes::isCraftable, TileEntityClausPlant::getMaxFluidTransfer, CommonFluidPredicates.accept(), machine -> FACE_FLUID_INPUT)
			.addSlot(OXYGEN_INDEX_INPUT, TileEntityClausPlant::getMaxFluid, TileEntityClausPlant::getMaxFluidTransfer, machine -> stack -> stack.getContentsName().equals("oxygen"), TileEntityClausPlant::getMaxFluidTransfer, CommonFluidPredicates.accept(), machine -> FACE_OXYGEN_INPUT)
			.addSlot(WATER_INDEX_OUTPUT, TileEntityClausPlant::getMaxFluid, machine -> 0, CommonFluidPredicates.deny(), TileEntityClausPlant::getMaxFluidTransfer, CommonFluidPredicates.accept(), machine -> FACE_WATER_OUTPUT)
    		.build();
	@SuppressWarnings({ "unchecked"})
	public static final EnergyInventoryTemplate<? extends TileEntityClausPlant<?>> ENERGY_TEMPLATE = ((EnergyInventoryTemplateBuilder<? extends TileEntityClausPlant<?>>) new EnergyInventoryTemplateBuilder<>())
			.addSlot(ENERGY_INDEX_ENERGY, TileEntityClausPlant::getMaxEnergy, TileEntityClausPlant::getMaxEnergyTransfer, CommonEnergyPredicates.accept(), machine -> 0, CommonEnergyPredicates.deny(), machine -> EnumFace.values())
    		.build();
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static final OperationsTemplate<TileEntityClausPlant> OPERATION_TEMPLATE = new OperationsTemplate<TileEntityClausPlant>()
			.addOperation(CommonOperations.<TileEntityClausPlant>energyFromItem(machine -> machine.ITEM_INDEX_ENERGY_INPUT, machine -> machine.ENERGY_INDEX_ENERGY_INPUT, TileEntityClausPlant::getMaxEnergyTransferItem))
			.addOperation(CommonOperations.<TileEntityClausPlant>fluidFromItem(machine -> machine.ITEM_INDEX_FLUID_INPUT, machine -> machine.FLUID_INDEX_FLUID_INPUT, TileEntityClausPlant::getMaxFluidTransferItem))
			.addOperation(CommonOperations.<TileEntityClausPlant>fluidFromItem(machine -> machine.ITEM_INDEX_OXYGEN_INPUT, machine -> machine.FLUID_INDEX_OXYGEN_INPUT, TileEntityClausPlant::getMaxFluidTransferItem))
			.addOperation(CommonOperations.<TileEntityClausPlant>fluidToItem(machine -> machine.ITEM_INDEX_WATER_OUTPUT, machine -> machine.FLUID_INDEX_WATER_OUTPUT, TileEntityClausPlant::getMaxFluidTransferItem));

	public final IItemSlot
	ITEM_INDEX_ENERGY_INPUT,
	ITEM_INDEX_FLUID_INPUT,
	ITEM_INDEX_OXYGEN_INPUT,
	ITEM_INDEX_ITEM_OUTPUT,
	ITEM_INDEX_WATER_OUTPUT;
	public final IFluidSlot
	FLUID_INDEX_FLUID_INPUT,
	FLUID_INDEX_OXYGEN_INPUT,
	FLUID_INDEX_WATER_OUTPUT;
	public final IEnergySlot
	ENERGY_INDEX_ENERGY_INPUT;
	@SideOnly(Side.CLIENT)
	public RenderInfo
	renderDimsEnergyInput,
	renderDimsFluidInput,
	renderDimsOxygenInput,
	renderDimsWaterOutput;

    protected int progress = 0;

    public abstract int getMaxEnergyUsagePerTick();

    @SuppressWarnings("unchecked")
	public TileEntityClausPlant()
    {
    	super(((ItemInventoryTemplate<T>) ITEM_TEMPLATE)::build, ((FluidInventoryTemplate<T>) FLUID_TEMPLATE)::build, ((EnergyInventoryTemplate<T>) ENERGY_TEMPLATE)::build);
    	ITEM_INDEX_ENERGY_INPUT = this.itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(ENERGY_INDEX_ENERGY));
    	ITEM_INDEX_FLUID_INPUT = this.itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(FLUID_INDEX_INPUT));
    	ITEM_INDEX_OXYGEN_INPUT = this.itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(OXYGEN_INDEX_INPUT));
    	ITEM_INDEX_ITEM_OUTPUT = this.itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(ITEM_INDEX_OUTPUT));
    	ITEM_INDEX_WATER_OUTPUT = this.itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(WATER_INDEX_OUTPUT));
    	FLUID_INDEX_FLUID_INPUT = this.fluidInventory.getFluidSlot(FLUID_TEMPLATE.getIndex(FLUID_INDEX_INPUT));
    	FLUID_INDEX_OXYGEN_INPUT = this.fluidInventory.getFluidSlot(FLUID_TEMPLATE.getIndex(OXYGEN_INDEX_INPUT));
    	FLUID_INDEX_WATER_OUTPUT = this.fluidInventory.getFluidSlot(FLUID_TEMPLATE.getIndex(WATER_INDEX_OUTPUT));
    	ENERGY_INDEX_ENERGY_INPUT = this.energyInventory.getEnergySlot(ENERGY_TEMPLATE.getIndex(ENERGY_INDEX_ENERGY));
    	this.operations = OPERATION_TEMPLATE.build(this);
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
		{
			renderDimsEnergyInput = new RenderInfo(8, 9, IGuiElements.ENERGY_BAR_PROGRESS);
			renderDimsFluidInput = new RenderInfo(57, 26, IGuiElements.FLUID_BAR_DECOR);
			renderDimsOxygenInput = new RenderInfo(35, 26, IGuiElements.FLUID_BAR_DECOR);
			renderDimsWaterOutput = new RenderInfo(135, 26, IGuiElements.FLUID_BAR_DECOR);
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

    public static final int FLUX_PER_OXYGEN = 100;

	@Override
	public boolean canProcessRecipeThisTick(IClausPlantRecipe recipe)
	{
		return this.progress >= recipe.getRequiredOxygen() * FLUX_PER_OXYGEN;
	}

	@Override
    public void onStoppedRunning()
    {
		progress = 0;
    }

	@Override
	public void onUsageTick(IClausPlantRecipe recipe)
	{
		this.progress += this.getMaxEnergyUsagePerTick();
	}

	@Override
	public void processRecipe(IClausPlantRecipe recipe)
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
		ItemStack outputItem = recipe.getItemOutput();
		if (outputItem != null && !outputItem.isEmpty()) ITEM_INDEX_ITEM_OUTPUT.add(outputItem);
		FLUID_INDEX_FLUID_INPUT.remove(testStack.getAmount());
		FLUID_INDEX_OXYGEN_INPUT.remove(recipe.getRequiredOxygen());
		FLUID_INDEX_WATER_OUTPUT.add(FluidOrGasStack.forFluid(new FluidStack(FluidRegistry.WATER, recipe.getRequiredOxygen())));
    	this.progress -= recipe.getRequiredOxygen() * FLUX_PER_OXYGEN;
    	ENERGY_INDEX_ENERGY_INPUT.remove(recipe.getRequiredOxygen() * FLUX_PER_OXYGEN);
	}

    @Override
	public IClausPlantRecipe getValidRecipe()
    {
    	FluidOrGasStack fluidInput = FLUID_INDEX_FLUID_INPUT.getFluid();
    	if (fluidInput == null) return null;
    	IClausPlantRecipe recipe = ClausPlantRecipes.getRecipe(fluidInput);
    	if (ClausPlantRecipes.isCraftable(recipe))
    	{
        	int oxygen = FluidOrGasStack.getAmountStatic(FLUID_INDEX_OXYGEN_INPUT.getFluid());
        	int water = FluidOrGasStack.getAmountStatic(FLUID_INDEX_WATER_OUTPUT.getFluid());
    		if (recipe.getRequiredOxygen() > oxygen || (recipe.getRequiredOxygen() * 2) + water > FLUID_INDEX_WATER_OUTPUT.getCapacity() || recipe.getRequiredOxygen() * FLUX_PER_OXYGEN > ENERGY_INDEX_ENERGY_INPUT.getEnergyStored()) return null;
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
			ItemStack inv = ITEM_INDEX_ITEM_OUTPUT.getStack();
			ItemStack out = recipe.getItemOutput();
			if (inv != null && !inv.isEmpty() && out != null && !out.isEmpty())
			{
				if (!ItemStack.areItemsEqual(inv, out) || !ItemStack.areItemStackTagsEqual(inv, out) || out.getCount() + inv.getCount() > inv.getMaxStackSize()) return null;
			}
    		return recipe;
    	}
    	else return null;
    }

    @Override
	public int getField(int id)
    {
        switch (id)
        {
        case 0:
        	return this.isRunning ? this.progress : 0;
        case 1:
        	IClausPlantRecipe recipe = this.getValidRecipe();
        	return recipe != null ? recipe.getRequiredOxygen() : 1;
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
	@SideOnly(Side.CLIENT)
	public RenderInfo getFluidRenderInfo(int index)
	{
		if (index == FLUID_INDEX_FLUID_INPUT.getSlot()) return renderDimsFluidInput;
		else if (index == FLUID_INDEX_OXYGEN_INPUT.getSlot()) return renderDimsOxygenInput;
		else if (index == FLUID_INDEX_WATER_OUTPUT.getSlot()) return renderDimsWaterOutput;
		else return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	@SideOnly(Side.CLIENT)
	public void addItemSlotsToContainer(ContainerMachine<T> container)
	{
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_ENERGY_INPUT.getSlot(), 8 , 53));
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_FLUID_INPUT.getSlot(), 52 , 53));
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_OXYGEN_INPUT.getSlot(), 30 , 53));
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_ITEM_OUTPUT.getSlot(), 108, 53));
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_WATER_OUTPUT.getSlot(), 130, 53));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getGuiTexture()
	{
		return CLAUS_PLANT_GUI_TEXTURES;
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
		if (ConfigJEI.INSTANCE.clausPlantRecipes.val && adjX >= 81 && adjX < 94 && adjY >= 26 && adjY < 50) return new String[] {RecipeCategoryClausPlant.ID};
		else return null;
	}

	@Override
	public String getGuiID()
	{
		return "mechanimation:claus_plant";
	}

	@Override
	public String getDefaultName()
	{
		return "container.mechanimation.claus_plant";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public RenderInfo getEnergyRenderInfo(int index)
	{
		return renderDimsEnergyInput;
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