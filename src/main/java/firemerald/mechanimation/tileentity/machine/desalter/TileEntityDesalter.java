package firemerald.mechanimation.tileentity.machine.desalter;

import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.desalter.DesalterRecipes;
import firemerald.mechanimation.api.crafting.desalter.IDesalterRecipe;
import firemerald.mechanimation.client.gui.inventory.IGuiElements;
import firemerald.mechanimation.compat.jei.ConfigJEI;
import firemerald.mechanimation.compat.jei.recipe.desalter.RecipeCategoryDesalter;
import firemerald.mechanimation.init.MechanimationFluids;
import firemerald.mechanimation.inventory.container.ContainerMachine;
import firemerald.mechanimation.inventory.slot.SlotMachine;
import firemerald.mechanimation.tileentity.machine.base.RenderInfo;
import firemerald.mechanimation.tileentity.machine.base.fluids.CommonFluidPredicates;
import firemerald.mechanimation.tileentity.machine.base.fluids.FluidInventoryTemplate;
import firemerald.mechanimation.tileentity.machine.base.fluids.FluidInventoryTemplateBuilder;
import firemerald.mechanimation.tileentity.machine.base.fluids.IFluidSlot;
import firemerald.mechanimation.tileentity.machine.base.implementation.actual.oriented.TileEntityOrientedFluidItemMachineBase;
import firemerald.mechanimation.tileentity.machine.base.items.CommonItemPredicates;
import firemerald.mechanimation.tileentity.machine.base.items.IItemSlot;
import firemerald.mechanimation.tileentity.machine.base.items.ItemInventoryTemplate;
import firemerald.mechanimation.tileentity.machine.base.items.ItemInventoryTemplateBuilder;
import firemerald.mechanimation.tileentity.machine.base.operations.CommonOperations;
import firemerald.mechanimation.tileentity.machine.base.operations.OperationsTemplate;
import firemerald.mechanimation.util.EnumFace;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileEntityDesalter<T extends TileEntityDesalter<T>> extends TileEntityOrientedFluidItemMachineBase<T, IDesalterRecipe>
{
    public static final ResourceLocation DESALTER_GUI_TEXTURES = new ResourceLocation(MechanimationAPI.MOD_ID, "textures/gui/container/desalter.png");
	public static final String
	FLUID_INDEX_INPUT = "fluid_input",
	WATER_INDEX_INPUT = "water_input",
	FLUID_INDEX_OUTPUT = "fluid_output",
	BRINE_INDEX_OUTPUT = "brine_output";
	public static final EnumFace[]
	FACE_FLUID_INPUT = { EnumFace.RIGHT },
	FACE_WATER_INPUT = { EnumFace.TOP },
	FACE_FLUID_OUTPUT = { EnumFace.LEFT },
	FACE_BRINE_OUTPUT = { EnumFace.BACK };
	@SuppressWarnings("unchecked")
	public static final ItemInventoryTemplate<? extends TileEntityDesalter<?>> ITEM_TEMPLATE = ((ItemInventoryTemplateBuilder<? extends TileEntityDesalter<?>>) new ItemInventoryTemplateBuilder<>())
    		.addSlot(FLUID_INDEX_INPUT, machine -> 64, machine -> 64, CommonItemPredicates.providesFluidTo(machine -> machine.FLUID_INDEX_FLUID_INPUT), machine -> 64, CommonItemPredicates.doesNotProvideFluidTo(machine -> machine.FLUID_INDEX_FLUID_INPUT), machine -> FACE_FLUID_INPUT)
    		.addSlot(WATER_INDEX_INPUT, machine -> 64, machine -> 64, CommonItemPredicates.providesFluidTo(machine -> machine.FLUID_INDEX_WATER_INPUT), machine -> 64, CommonItemPredicates.doesNotProvideFluidTo(machine -> machine.FLUID_INDEX_WATER_INPUT), machine -> FACE_WATER_INPUT)
    		.addSlot(FLUID_INDEX_OUTPUT, machine -> 64, machine -> 64, CommonItemPredicates.receivesFluidFrom(machine -> machine.FLUID_INDEX_FLUID_OUTPUT), machine -> 64, CommonItemPredicates.doesNotReceiveFluidFrom(machine -> machine.FLUID_INDEX_FLUID_OUTPUT), machine -> FACE_FLUID_OUTPUT)
    		.addSlot(BRINE_INDEX_OUTPUT, machine -> 64, machine -> 64, CommonItemPredicates.receivesFluidFrom(machine -> machine.FLUID_INDEX_BRINE_OUTPUT), machine -> 64, CommonItemPredicates.doesNotReceiveFluidFrom(machine -> machine.FLUID_INDEX_BRINE_OUTPUT), machine -> FACE_BRINE_OUTPUT)
    		.build();
	@SuppressWarnings("unchecked")
	public static final FluidInventoryTemplate<? extends TileEntityDesalter<?>> FLUID_TEMPLATE = ((FluidInventoryTemplateBuilder<? extends TileEntityDesalter<?>>) new FluidInventoryTemplateBuilder<>())
			.addSlot(FLUID_INDEX_INPUT, TileEntityDesalter::getMaxFluid, TileEntityDesalter::getMaxFluidTransfer, machine -> DesalterRecipes::isCraftable, TileEntityDesalter::getMaxFluidTransfer, CommonFluidPredicates.accept(), machine -> FACE_FLUID_INPUT)
			.addSlot(WATER_INDEX_INPUT, TileEntityDesalter::getMaxFluid, TileEntityDesalter::getMaxFluidTransfer, machine -> stack -> stack.isFluid() && stack.getFluidStack().getFluid() == FluidRegistry.WATER, TileEntityDesalter::getMaxFluidTransfer, CommonFluidPredicates.accept(), machine -> FACE_WATER_INPUT)
			.addSlot(FLUID_INDEX_OUTPUT, TileEntityDesalter::getMaxFluid, machine -> 0, CommonFluidPredicates.deny(), TileEntityDesalter::getMaxFluidTransfer, CommonFluidPredicates.accept(), machine -> FACE_FLUID_OUTPUT)
			.addSlot(BRINE_INDEX_OUTPUT, TileEntityDesalter::getMaxFluid, machine -> 0, CommonFluidPredicates.deny(), TileEntityDesalter::getMaxFluidTransfer, CommonFluidPredicates.accept(), machine -> FACE_BRINE_OUTPUT)
    		.build();
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static final OperationsTemplate<TileEntityDesalter> OPERATION_TEMPLATE = new OperationsTemplate<TileEntityDesalter>()
			.addOperation(CommonOperations.<TileEntityDesalter>fluidFromItem(machine -> machine.ITEM_INDEX_FLUID_INPUT, machine -> machine.FLUID_INDEX_FLUID_INPUT, TileEntityDesalter::getMaxFluidTransferItem))
			.addOperation(CommonOperations.<TileEntityDesalter>fluidFromItem(machine -> machine.ITEM_INDEX_WATER_INPUT, machine -> machine.FLUID_INDEX_WATER_INPUT, TileEntityDesalter::getMaxFluidTransferItem))
			.addOperation(CommonOperations.<TileEntityDesalter>fluidToItem(machine -> machine.ITEM_INDEX_FLUID_OUTPUT, machine -> machine.FLUID_INDEX_FLUID_OUTPUT, TileEntityDesalter::getMaxFluidTransferItem))
			.addOperation(CommonOperations.<TileEntityDesalter>fluidToItem(machine -> machine.ITEM_INDEX_BRINE_OUTPUT, machine -> machine.FLUID_INDEX_BRINE_OUTPUT, TileEntityDesalter::getMaxFluidTransferItem));

	public final IItemSlot
	ITEM_INDEX_FLUID_INPUT,
	ITEM_INDEX_WATER_INPUT,
	ITEM_INDEX_FLUID_OUTPUT,
	ITEM_INDEX_BRINE_OUTPUT;
	public final IFluidSlot
	FLUID_INDEX_FLUID_INPUT,
	FLUID_INDEX_WATER_INPUT,
	FLUID_INDEX_FLUID_OUTPUT,
	FLUID_INDEX_BRINE_OUTPUT;
	@SideOnly(Side.CLIENT)
	public RenderInfo
	renderDimsFluidInput,
	renderDimsWaterInput,
	renderDimsFluidOutput,
	renderDimsBrineOutput;

    protected int progress = 0;

    public abstract int getMaxWaterUsagePerTick();

    @SuppressWarnings("unchecked")
	public TileEntityDesalter()
    {
    	super(((ItemInventoryTemplate<T>) ITEM_TEMPLATE)::build, ((FluidInventoryTemplate<T>) FLUID_TEMPLATE)::build);
    	ITEM_INDEX_FLUID_INPUT = this.itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(FLUID_INDEX_INPUT));
    	ITEM_INDEX_WATER_INPUT = this.itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(WATER_INDEX_INPUT));
    	ITEM_INDEX_FLUID_OUTPUT = this.itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(FLUID_INDEX_OUTPUT));
    	ITEM_INDEX_BRINE_OUTPUT = this.itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(BRINE_INDEX_OUTPUT));
    	FLUID_INDEX_FLUID_INPUT = this.fluidInventory.getFluidSlot(FLUID_TEMPLATE.getIndex(FLUID_INDEX_INPUT));
    	FLUID_INDEX_WATER_INPUT = this.fluidInventory.getFluidSlot(FLUID_TEMPLATE.getIndex(WATER_INDEX_INPUT));
    	FLUID_INDEX_FLUID_OUTPUT = this.fluidInventory.getFluidSlot(FLUID_TEMPLATE.getIndex(FLUID_INDEX_OUTPUT));
    	FLUID_INDEX_BRINE_OUTPUT = this.fluidInventory.getFluidSlot(FLUID_TEMPLATE.getIndex(BRINE_INDEX_OUTPUT));
    	this.operations = OPERATION_TEMPLATE.build(this);
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
		{
			renderDimsFluidInput = new RenderInfo(57, 26, IGuiElements.FLUID_BAR_DECOR);
			renderDimsWaterInput = new RenderInfo(35, 26, IGuiElements.FLUID_BAR_DECOR);
			renderDimsFluidOutput = new RenderInfo(113, 26, IGuiElements.FLUID_BAR_DECOR);
			renderDimsBrineOutput = new RenderInfo(135, 26, IGuiElements.FLUID_BAR_DECOR);
		}
    }

	public abstract int getMaxFluid();

	public abstract int getMaxFluidTransfer();

	public abstract int getMaxFluidTransferItem();

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
	public boolean canProcessRecipeThisTick(IDesalterRecipe recipe)
	{
		return this.progress >= recipe.getRequiredWater();
	}

	@Override
    public void onStoppedRunning()
    {
		progress = 0;
    }

	@Override
	public void onUsageTick(IDesalterRecipe recipe)
	{
		this.progress += this.getMaxWaterUsagePerTick();
	}

	@Override
	public void processRecipe(IDesalterRecipe recipe) //TODO onFluidSlotChanged
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
		FluidOrGasStack outputFluid = recipe.getFluidOutput();
		FLUID_INDEX_FLUID_OUTPUT.add(outputFluid);
		FLUID_INDEX_FLUID_INPUT.remove(testStack.getAmount());
		FLUID_INDEX_WATER_INPUT.remove(recipe.getRequiredWater());
		FLUID_INDEX_BRINE_OUTPUT.add(FluidOrGasStack.forFluid(new FluidStack(MechanimationFluids.brine, recipe.getRequiredWater())));
    	this.progress -= recipe.getRequiredWater();
	}

    @Override
	public IDesalterRecipe getValidRecipe()
    {
    	FluidOrGasStack fluidInput = FLUID_INDEX_FLUID_INPUT.getFluid();
    	if (fluidInput == null) return null;
    	IDesalterRecipe recipe = DesalterRecipes.getRecipe(fluidInput);
    	if (DesalterRecipes.isCraftable(recipe))
    	{
        	int water = FluidOrGasStack.getAmountStatic(FLUID_INDEX_WATER_INPUT.getFluid());
        	int brine = FluidOrGasStack.getAmountStatic(FLUID_INDEX_BRINE_OUTPUT.getFluid());
    		if (recipe.getRequiredWater() > water || recipe.getRequiredWater() + brine > FLUID_INDEX_BRINE_OUTPUT.getCapacity()) return null;
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
        	IDesalterRecipe recipe = this.getValidRecipe();
        	return recipe != null ? recipe.getRequiredWater() : 1;
        default:
        	return 0;
        }
    }

    @Override
	public void setField(int id, int value)
    {
    	this.readFromDisk(getTileData());
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
		else if (index == FLUID_INDEX_WATER_INPUT.getSlot()) return renderDimsWaterInput;
		else if (index == FLUID_INDEX_FLUID_OUTPUT.getSlot()) return renderDimsFluidOutput;
		else if (index == FLUID_INDEX_BRINE_OUTPUT.getSlot()) return renderDimsBrineOutput;
		else return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	@SideOnly(Side.CLIENT)
	public void addItemSlotsToContainer(ContainerMachine<T> container)
	{
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_FLUID_INPUT.getSlot(), 52 , 53));
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_WATER_INPUT.getSlot(), 30 , 53));
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_FLUID_OUTPUT.getSlot(), 108, 53));
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_BRINE_OUTPUT.getSlot(), 130, 53));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getGuiTexture()
	{
		return DESALTER_GUI_TEXTURES;
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
		if (ConfigJEI.INSTANCE.desalterRecipes.val && adjX >= 81 && adjX < 94 && adjY >= 26 && adjY < 50) return new String[] {RecipeCategoryDesalter.ID};
		else return null;
	}

	@Override
	public String getGuiID()
	{
		return "mechanimation:desalter";
	}

	@Override
	public String getDefaultName()
	{
		return "container.mechanimation.desalter";
	}
}