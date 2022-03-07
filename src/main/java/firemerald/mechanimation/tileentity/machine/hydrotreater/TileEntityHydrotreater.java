package firemerald.mechanimation.tileentity.machine.hydrotreater;

import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.hydrotreater.HydrotreaterRecipes;
import firemerald.mechanimation.api.crafting.hydrotreater.IHydrotreaterRecipe;
import firemerald.mechanimation.client.gui.inventory.IGuiElements;
import firemerald.mechanimation.compat.jei.ConfigJEI;
import firemerald.mechanimation.compat.jei.recipe.hydrotreater.RecipeCategoryHydrotreater;
import firemerald.mechanimation.init.MechanimationFluids;
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
import mekanism.api.gas.GasStack;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileEntityHydrotreater<T extends TileEntityHydrotreater<T>> extends TileEntityOrientedEnergyFluidItemMachineBase<T, IHydrotreaterRecipe> implements IFluxReceiver
{
    public static final ResourceLocation HYDROTREATER_GUI_TEXTURES = new ResourceLocation(MechanimationAPI.MOD_ID, "textures/gui/container/hydrotreater.png");
	public static final String
	ENERGY_INDEX_ENERGY = "energy",
	FLUID_INDEX_INPUT = "fluid_input",
	HYDROGEN_INDEX_INPUT = "hydrogen_input",
	FLUID_INDEX_OUTPUT = "fluid_output",
	HYDROGEN_SULFIDE_INDEX_OUTPUT = "hydrogen_sulfide_output";
	public static final EnumFace[]
	FACE_FLUID_INPUT = { EnumFace.RIGHT },
	FACE_HYDROGEN_INPUT = { EnumFace.BACK },
	FACE_FLUID_OUTPUT = { EnumFace.LEFT },
	FACE_HYDROGEN_SULFIDE_OUTPUT = { EnumFace.TOP };
	@SuppressWarnings("unchecked")
	public static final ItemInventoryTemplate<? extends TileEntityHydrotreater<?>> ITEM_TEMPLATE = ((ItemInventoryTemplateBuilder<? extends TileEntityHydrotreater<?>>) new ItemInventoryTemplateBuilder<>())
    		.addSlot(ENERGY_INDEX_ENERGY, machine -> 64, machine -> 64, machine -> Utils::isEnergyProvider, machine -> 64, machine -> stack -> !Utils.isEnergyProvider(stack), machine -> EnumFace.values())
    		.addSlot(FLUID_INDEX_INPUT, machine -> 64, machine -> 64, CommonItemPredicates.providesFluidTo(machine -> machine.FLUID_INDEX_FLUID_INPUT), machine -> 64, CommonItemPredicates.doesNotProvideFluidTo(machine -> machine.FLUID_INDEX_FLUID_INPUT), machine -> FACE_FLUID_INPUT)
    		.addSlot(HYDROGEN_INDEX_INPUT, machine -> 64, machine -> 64, CommonItemPredicates.providesFluidTo(machine -> machine.FLUID_INDEX_HYDROGEN_INPUT), machine -> 64, CommonItemPredicates.doesNotProvideFluidTo(machine -> machine.FLUID_INDEX_HYDROGEN_INPUT), machine -> FACE_HYDROGEN_INPUT)
    		.addSlot(FLUID_INDEX_OUTPUT, machine -> 64, machine -> 64, CommonItemPredicates.receivesFluidFrom(machine -> machine.FLUID_INDEX_FLUID_OUTPUT), machine -> 64, CommonItemPredicates.doesNotReceiveFluidFrom(machine -> machine.FLUID_INDEX_FLUID_OUTPUT), machine -> FACE_FLUID_OUTPUT)
    		.addSlot(HYDROGEN_SULFIDE_INDEX_OUTPUT, machine -> 64, machine -> 64, CommonItemPredicates.receivesFluidFrom(machine -> machine.FLUID_INDEX_HYDROGEN_SULFIDE_OUTPUT), machine -> 64, CommonItemPredicates.doesNotReceiveFluidFrom(machine -> machine.FLUID_INDEX_HYDROGEN_SULFIDE_OUTPUT), machine -> FACE_HYDROGEN_SULFIDE_OUTPUT)
    		.build();
	@SuppressWarnings("unchecked")
	public static final FluidInventoryTemplate<? extends TileEntityHydrotreater<?>> FLUID_TEMPLATE = ((FluidInventoryTemplateBuilder<? extends TileEntityHydrotreater<?>>) new FluidInventoryTemplateBuilder<>())
			.addSlot(FLUID_INDEX_INPUT, TileEntityHydrotreater::getMaxFluid, TileEntityHydrotreater::getMaxFluidTransfer, machine -> HydrotreaterRecipes::isCraftable, TileEntityHydrotreater::getMaxFluidTransfer, CommonFluidPredicates.accept(), machine -> FACE_FLUID_INPUT)
			.addSlot(HYDROGEN_INDEX_INPUT, TileEntityHydrotreater::getMaxFluid, TileEntityHydrotreater::getMaxFluidTransfer, machine -> stack -> stack.getContentsName().equals("hydrogen"), TileEntityHydrotreater::getMaxFluidTransfer, CommonFluidPredicates.accept(), machine -> FACE_HYDROGEN_INPUT)
			.addSlot(FLUID_INDEX_OUTPUT, TileEntityHydrotreater::getMaxFluid, machine -> 0, CommonFluidPredicates.deny(), TileEntityHydrotreater::getMaxFluidTransfer, CommonFluidPredicates.accept(), machine -> FACE_FLUID_OUTPUT)
			.addSlot(HYDROGEN_SULFIDE_INDEX_OUTPUT, TileEntityHydrotreater::getMaxFluid, machine -> 0, CommonFluidPredicates.deny(), TileEntityHydrotreater::getMaxFluidTransfer, CommonFluidPredicates.accept(), machine -> FACE_HYDROGEN_SULFIDE_OUTPUT)
    		.build();
	@SuppressWarnings({ "unchecked"})
	public static final EnergyInventoryTemplate<? extends TileEntityHydrotreater<?>> ENERGY_TEMPLATE = ((EnergyInventoryTemplateBuilder<? extends TileEntityHydrotreater<?>>) new EnergyInventoryTemplateBuilder<>())
			.addSlot(ENERGY_INDEX_ENERGY, TileEntityHydrotreater::getMaxEnergy, TileEntityHydrotreater::getMaxEnergyTransfer, CommonEnergyPredicates.accept(), machine -> 0, CommonEnergyPredicates.deny(), machine -> EnumFace.values())
    		.build();
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static final OperationsTemplate<TileEntityHydrotreater> OPERATION_TEMPLATE = new OperationsTemplate<TileEntityHydrotreater>()
			.addOperation(CommonOperations.<TileEntityHydrotreater>energyFromItem(machine -> machine.ITEM_INDEX_ENERGY_INPUT, machine -> machine.ENERGY_INDEX_ENERGY_INPUT, TileEntityHydrotreater::getMaxEnergyTransferItem))
			.addOperation(CommonOperations.<TileEntityHydrotreater>fluidFromItem(machine -> machine.ITEM_INDEX_FLUID_INPUT, machine -> machine.FLUID_INDEX_FLUID_INPUT, TileEntityHydrotreater::getMaxFluidTransferItem))
			.addOperation(CommonOperations.<TileEntityHydrotreater>fluidFromItem(machine -> machine.ITEM_INDEX_HYDROGEN_INPUT, machine -> machine.FLUID_INDEX_HYDROGEN_INPUT, TileEntityHydrotreater::getMaxFluidTransferItem))
			.addOperation(CommonOperations.<TileEntityHydrotreater>fluidToItem(machine -> machine.ITEM_INDEX_FLUID_OUTPUT, machine -> machine.FLUID_INDEX_FLUID_OUTPUT, TileEntityHydrotreater::getMaxFluidTransferItem))
			.addOperation(CommonOperations.<TileEntityHydrotreater>fluidToItem(machine -> machine.ITEM_INDEX_HYDROGEN_SULFIDE_OUTPUT, machine -> machine.FLUID_INDEX_HYDROGEN_SULFIDE_OUTPUT, TileEntityHydrotreater::getMaxFluidTransferItem));

	public final IItemSlot
	ITEM_INDEX_ENERGY_INPUT,
	ITEM_INDEX_FLUID_INPUT,
	ITEM_INDEX_HYDROGEN_INPUT,
	ITEM_INDEX_FLUID_OUTPUT,
	ITEM_INDEX_HYDROGEN_SULFIDE_OUTPUT;
	public final IFluidSlot
	FLUID_INDEX_FLUID_INPUT,
	FLUID_INDEX_HYDROGEN_INPUT,
	FLUID_INDEX_FLUID_OUTPUT,
	FLUID_INDEX_HYDROGEN_SULFIDE_OUTPUT;
	public final IEnergySlot
	ENERGY_INDEX_ENERGY_INPUT;
	@SideOnly(Side.CLIENT)
	public RenderInfo
	renderDimsEnergyInput,
	renderDimsFluidInput,
	renderDimsHydrogenInput,
	renderDimsFluidOutput,
	renderDimsHydrogenSulfideOutput;

    protected int progress = 0;

    public abstract int getMaxEnergyUsagePerTick();

    @SuppressWarnings("unchecked")
	public TileEntityHydrotreater()
    {
    	super(((ItemInventoryTemplate<T>) ITEM_TEMPLATE)::build, ((FluidInventoryTemplate<T>) FLUID_TEMPLATE)::build, ((EnergyInventoryTemplate<T>) ENERGY_TEMPLATE)::build);
    	ITEM_INDEX_ENERGY_INPUT = this.itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(ENERGY_INDEX_ENERGY));
    	ITEM_INDEX_FLUID_INPUT = this.itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(FLUID_INDEX_INPUT));
    	ITEM_INDEX_HYDROGEN_INPUT = this.itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(HYDROGEN_INDEX_INPUT));
    	ITEM_INDEX_FLUID_OUTPUT = this.itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(FLUID_INDEX_OUTPUT));
    	ITEM_INDEX_HYDROGEN_SULFIDE_OUTPUT = this.itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(HYDROGEN_SULFIDE_INDEX_OUTPUT));
    	FLUID_INDEX_FLUID_INPUT = this.fluidInventory.getFluidSlot(FLUID_TEMPLATE.getIndex(FLUID_INDEX_INPUT));
    	FLUID_INDEX_HYDROGEN_INPUT = this.fluidInventory.getFluidSlot(FLUID_TEMPLATE.getIndex(HYDROGEN_INDEX_INPUT));
    	FLUID_INDEX_FLUID_OUTPUT = this.fluidInventory.getFluidSlot(FLUID_TEMPLATE.getIndex(FLUID_INDEX_OUTPUT));
    	FLUID_INDEX_HYDROGEN_SULFIDE_OUTPUT = this.fluidInventory.getFluidSlot(FLUID_TEMPLATE.getIndex(HYDROGEN_SULFIDE_INDEX_OUTPUT));
    	ENERGY_INDEX_ENERGY_INPUT = this.energyInventory.getEnergySlot(ENERGY_TEMPLATE.getIndex(ENERGY_INDEX_ENERGY));
    	this.operations = OPERATION_TEMPLATE.build(this);
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
		{
			renderDimsEnergyInput = new RenderInfo(8, 9, IGuiElements.ENERGY_BAR_PROGRESS);
			renderDimsFluidInput = new RenderInfo(57, 26, IGuiElements.FLUID_BAR_DECOR);
			renderDimsHydrogenInput = new RenderInfo(35, 26, IGuiElements.FLUID_BAR_DECOR);
			renderDimsFluidOutput = new RenderInfo(113, 26, IGuiElements.FLUID_BAR_DECOR);
			renderDimsHydrogenSulfideOutput = new RenderInfo(135, 26, IGuiElements.FLUID_BAR_DECOR);
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

    public static final int FLUX_PER_HYDROGEN = 100;

	@Override
	public boolean canProcessRecipeThisTick(IHydrotreaterRecipe recipe)
	{
		return this.progress >= recipe.getRequiredHydrogen() * FLUX_PER_HYDROGEN;
	}

	@Override
    public void onStoppedRunning()
    {
		progress = 0;
    }

	@Override
	public void onUsageTick(IHydrotreaterRecipe recipe)
	{
		this.progress += this.getMaxEnergyUsagePerTick();
	}

	@Override
	public void processRecipe(IHydrotreaterRecipe recipe) //TODO onFluidSlotChanged
	{
		FluidOrGasStack testStack = null;
		FluidOrGasStack has = FLUID_INDEX_FLUID_INPUT.getFluid();
		for (FluidOrGasStack fluid : recipe.getFluidInput())
		{
			if (fluid.isFluidEqual(has) && fluid.getAmount() <= has.getAmount())
			{
				testStack = fluid;
				break;
			}
		}
		if (testStack == null) return;
		FluidOrGasStack outputFluid = recipe.getFluidOutput();
		FLUID_INDEX_FLUID_OUTPUT.add(outputFluid);
		FLUID_INDEX_FLUID_INPUT.remove(testStack.getAmount());
		FLUID_INDEX_HYDROGEN_INPUT.remove(recipe.getRequiredHydrogen());
		FLUID_INDEX_HYDROGEN_SULFIDE_OUTPUT.add(FluidOrGasStack.forGas(new GasStack(MechanimationFluids.HYDROGEN_SULFIDE, recipe.getRequiredHydrogen())));
    	this.progress -= recipe.getRequiredHydrogen() * FLUX_PER_HYDROGEN;
    	ENERGY_INDEX_ENERGY_INPUT.remove(recipe.getRequiredHydrogen() * FLUX_PER_HYDROGEN);
	}

    @Override
	public IHydrotreaterRecipe getValidRecipe()
    {
    	FluidOrGasStack fluidInput = FLUID_INDEX_FLUID_INPUT.getFluid();
    	if (fluidInput == null) return null;
    	IHydrotreaterRecipe recipe = HydrotreaterRecipes.getRecipe(fluidInput);
    	if (HydrotreaterRecipes.isCraftable(recipe))
    	{
        	int hydrogen = FluidOrGasStack.getAmountStatic(FLUID_INDEX_HYDROGEN_INPUT.getFluid());
        	int hydrogenSulfide = FluidOrGasStack.getAmountStatic(FLUID_INDEX_HYDROGEN_SULFIDE_OUTPUT.getFluid());
    		if (recipe.getRequiredHydrogen() > hydrogen || recipe.getRequiredHydrogen() + hydrogenSulfide > FLUID_INDEX_HYDROGEN_SULFIDE_OUTPUT.getCapacity() || recipe.getRequiredHydrogen() * FLUX_PER_HYDROGEN > ENERGY_INDEX_ENERGY_INPUT.getEnergyStored()) return null;
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
    		FluidOrGasStack outputFluid = recipe.getFluidOutput();
        	FluidOrGasStack fluidOutput = FLUID_INDEX_FLUID_OUTPUT.getFluid();
			if (outputFluid.getAmount() > FLUID_INDEX_FLUID_OUTPUT.getCapacity() || (fluidOutput != null && (!fluidOutput.isFluidEqual(outputFluid) || outputFluid.getAmount() > (FLUID_INDEX_FLUID_OUTPUT.getCapacity() - fluidOutput.getAmount())))) return null;
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
        	IHydrotreaterRecipe recipe = this.getValidRecipe();
        	return recipe != null ? recipe.getRequiredHydrogen() : 1;
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
		else if (index == FLUID_INDEX_HYDROGEN_INPUT.getSlot()) return renderDimsHydrogenInput;
		else if (index == FLUID_INDEX_FLUID_OUTPUT.getSlot()) return renderDimsFluidOutput;
		else if (index == FLUID_INDEX_HYDROGEN_SULFIDE_OUTPUT.getSlot()) return renderDimsHydrogenSulfideOutput;
		else return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	@SideOnly(Side.CLIENT)
	public void addItemSlotsToContainer(ContainerMachine<T> container)
	{
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_ENERGY_INPUT.getSlot(), 8 , 53));
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_FLUID_INPUT.getSlot(), 52 , 53));
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_HYDROGEN_INPUT.getSlot(), 30 , 53));
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_FLUID_OUTPUT.getSlot(), 108, 53));
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_HYDROGEN_SULFIDE_OUTPUT.getSlot(), 130, 53));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getGuiTexture()
	{
		return HYDROTREATER_GUI_TEXTURES;
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
		if (ConfigJEI.INSTANCE.hydrotreaterRecipes.val && adjX >= 81 && adjX < 94 && adjY >= 26 && adjY < 50) return new String[] {RecipeCategoryHydrotreater.ID};
		else return null;
	}

	@Override
	public String getGuiID()
	{
		return "mechanimation:hydrotreater";
	}

	@Override
	public String getDefaultName()
	{
		return "container.mechanimation.hydrotreater";
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