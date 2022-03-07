package firemerald.mechanimation.tileentity.machine.pulverizer;

import firemerald.api.mcms.util.IItemStackProvider;
import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.api.crafting.pulverizer.IPulverizerRecipe;
import firemerald.mechanimation.api.crafting.pulverizer.PulverizerRecipes;
import firemerald.mechanimation.client.gui.inventory.IGuiElements;
import firemerald.mechanimation.compat.jei.ConfigJEI;
import firemerald.mechanimation.compat.jei.recipe.pulverizer.RecipeCategoryPulverizer;
import firemerald.mechanimation.inventory.container.ContainerMachine;
import firemerald.mechanimation.inventory.slot.SlotMachine;
import firemerald.mechanimation.tileentity.machine.base.RenderInfo;
import firemerald.mechanimation.tileentity.machine.base.energy.CommonEnergyPredicates;
import firemerald.mechanimation.tileentity.machine.base.energy.EnergyInventoryTemplate;
import firemerald.mechanimation.tileentity.machine.base.energy.EnergyInventoryTemplateBuilder;
import firemerald.mechanimation.tileentity.machine.base.energy.IEnergySlot;
import firemerald.mechanimation.tileentity.machine.base.implementation.actual.oriented.TileEntityOrientedEnergyItemMachineBase;
import firemerald.mechanimation.tileentity.machine.base.items.CommonItemPredicates;
import firemerald.mechanimation.tileentity.machine.base.items.IItemSlot;
import firemerald.mechanimation.tileentity.machine.base.items.ItemInventoryTemplate;
import firemerald.mechanimation.tileentity.machine.base.items.ItemInventoryTemplateBuilder;
import firemerald.mechanimation.tileentity.machine.base.operations.CommonOperations;
import firemerald.mechanimation.tileentity.machine.base.operations.OperationsTemplate;
import firemerald.mechanimation.util.EnumFace;
import firemerald.mechanimation.util.flux.IFluxReceiver;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileEntityPulverizerBase<T extends TileEntityPulverizerBase<T>> extends TileEntityOrientedEnergyItemMachineBase<T, IPulverizerRecipe> implements IFluxReceiver, IItemStackProvider
{
    public static final ResourceLocation PULVERIZER_GUI_TEXTURES = new ResourceLocation(MechanimationAPI.MOD_ID, "textures/gui/container/pulverizer.png");
	public static final String
	ENERGY_INDEX_ENERGY = "energy",
	ITEM_INDEX_INPUT = "input",
	ITEM_INDEX_OUTPUT = "output";
    @SuppressWarnings("unchecked")
	public static final ItemInventoryTemplate<? extends TileEntityPulverizerBase<?>> ITEM_TEMPLATE = ((ItemInventoryTemplateBuilder<? extends TileEntityPulverizerBase<?>>) new ItemInventoryTemplateBuilder<>())
    		.addSlot(ENERGY_INDEX_ENERGY, machine -> 64, machine -> 64, CommonItemPredicates.providesEnergy(), machine -> 64, CommonItemPredicates.doesNotProvideEnergy(), machine -> EnumFace.values())
    		.addSlot(ITEM_INDEX_INPUT, machine -> 64, machine -> 64, CommonItemPredicates.of(PulverizerRecipes::isCraftable), machine -> 64, CommonItemPredicates.of(stack -> !PulverizerRecipes.isCraftable(stack)), TileEntityPulverizerBase::getInputFaces)
    		.addSlot(ITEM_INDEX_OUTPUT, machine -> 64, machine -> 0, CommonItemPredicates.deny(), machine -> 64, CommonItemPredicates.accept(), TileEntityPulverizerBase::getOutputFaces)
    		.build();
    @SuppressWarnings("unchecked")
	public static final EnergyInventoryTemplate<? extends TileEntityPulverizerBase<?>> ENERGY_TEMPLATE = ((EnergyInventoryTemplateBuilder<? extends TileEntityPulverizerBase<?>>) new EnergyInventoryTemplateBuilder<>())
    		.addSlot(ENERGY_INDEX_ENERGY, machine -> machine.getMaxEnergy(), machine -> machine.getMaxEnergyTransfer(), CommonEnergyPredicates.accept(), machine -> 0, CommonEnergyPredicates.deny(), machine -> EnumFace.values())
    		.build();
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static final OperationsTemplate<TileEntityPulverizerBase> OPERATIONS_TEMPLATE = new OperationsTemplate<TileEntityPulverizerBase>()
    		.addOperation(CommonOperations.<TileEntityPulverizerBase>energyFromItem(machine -> machine.ITEM_INDEX_ENERGY_INPUT, machine -> machine.ENERGY_INDEX_ENERGY_INPUT, TileEntityPulverizerBase::getMaxEnergyTransferItem));

	public final IItemSlot
	ITEM_INDEX_ENERGY_INPUT,
	ITEM_INDEX_ITEM_INPUT,
	ITEM_INDEX_ITEM_OUTPUT;
	public final IEnergySlot
	ENERGY_INDEX_ENERGY_INPUT;
	@SideOnly(Side.CLIENT)
	public RenderInfo
	renderDimsEnergyInput;

	@SuppressWarnings("unchecked")
    public TileEntityPulverizerBase()
    {
    	super(((ItemInventoryTemplate<T>) ITEM_TEMPLATE)::build, ((EnergyInventoryTemplate<T>) ENERGY_TEMPLATE)::build);
    	ITEM_INDEX_ENERGY_INPUT = itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(ENERGY_INDEX_ENERGY));
    	ITEM_INDEX_ITEM_INPUT = itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(ITEM_INDEX_INPUT));
    	ITEM_INDEX_ITEM_OUTPUT = itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(ITEM_INDEX_OUTPUT));
    	ENERGY_INDEX_ENERGY_INPUT = energyInventory.getEnergySlot(ENERGY_TEMPLATE.getIndex(ENERGY_INDEX_ENERGY));
    	this.operations = OPERATIONS_TEMPLATE.build(this);
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
		{
			renderDimsEnergyInput = new RenderInfo(8, 9, IGuiElements.ENERGY_BAR_PROGRESS);
		}
    }

	public abstract int getMaxEnergy();

	public abstract int getMaxEnergyTransfer();

	public abstract int getMaxEnergyTransferItem();

	public abstract EnumFace[] getInputFaces();

	public abstract EnumFace[] getOutputFaces();

    public abstract int getPulverizeTime();

    public abstract int getSoundTimestamp();

    public abstract int getMaxEnergyPerTick();

	@Override
	public ItemStack getItemStack(int index)
	{
		if (index == 0)
		{
			if (ITEM_INDEX_ITEM_INPUT.isEmpty()) return ITEM_INDEX_ITEM_OUTPUT.getStack();
			else return ITEM_INDEX_ITEM_INPUT.getStack();
		}
		else if (index <= this.getSizeInventory())
		{
			return getStackInSlot(index - 1);
		}
		else return ItemStack.EMPTY;
	}

	@Override
	public boolean useAnimation()
	{
		return true;
	}

    @Override
	public IPulverizerRecipe getValidRecipe()
    {
    	ItemStack stack = ITEM_INDEX_ITEM_INPUT.getStack();
    	if (stack.isEmpty()) return null;
    	IPulverizerRecipe result = PulverizerRecipes.getRecipe(stack);
    	if (PulverizerRecipes.isCraftable(result))
    	{
    		int needed = result.getRequiredCount(stack);
    		if (needed > stack.getCount()) return null;
    		ItemStack out = ITEM_INDEX_ITEM_OUTPUT.getStack();
    		ItemStack res = result.getOutput();
    		if (!out.isEmpty())
    		{
        		if (!ItemStack.areItemsEqual(res, out) || !ItemStack.areItemStackTagsEqual(res, out)) return null;
    		}
    		if (res.getCount() + out.getCount() > this.getInventoryStackLimit()) return null;
    		return result;
    	}
    	else return null;
    }

    private int getEnergyUsage(IPulverizerRecipe recipe)
    {
    	if (recipe != null)
    	{
        	int pulverizeTime = this.getPulverizeTime();
        	int prevEnergyUsed = MathHelper.ceil(recipe.getRequiredEnergy() * (runTime - 1) / pulverizeTime);
        	int newEnergyUsed = MathHelper.ceil(recipe.getRequiredEnergy() * (runTime + 0) / pulverizeTime);
        	return newEnergyUsed - prevEnergyUsed; //correct for non-integer energy used per tick
    	}
    	else return 0;
    }

    @Override
	public String getGuiID()
    {
        return "mechanimation:pulverizer";
    }

    @Override
	public int getField(int id)
    {
        switch (id)
        {
            case 0:
                return this.isRunning ? (int) this.runTime : 0;
            case 1:
            	return this.getPulverizeTime();
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
            	this.runTime = value;
        }
    }

    @Override
	public int getFieldCount()
    {
        return 2;
    }

	@Override
	public RenderInfo getEnergyRenderInfo(int index)
	{
		return this.renderDimsEnergyInput;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addItemSlotsToContainer(ContainerMachine<T> container)
	{
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_ENERGY_INPUT.getSlot(), 8, 53));
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_ITEM_INPUT.getSlot(), 53, 35));
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_ITEM_OUTPUT.getSlot(), 116, 35));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getGuiTexture()
	{
		return PULVERIZER_GUI_TEXTURES;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderProgressMeters(GuiContainer gui, int offX, int offY, float zLevel)
	{
		IGuiElements.PROGRESS_RIGHT_PROGRESS.render(offX + 80, offY + 35, zLevel, this.getProgress());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String[] getRecipeTypes(int adjX, int adjY)
	{
		if (ConfigJEI.INSTANCE.pulverizerRecipes.val && adjX >= 80 && adjX < 102 && adjY >= 35 && adjY < 51) return new String[] {RecipeCategoryPulverizer.ID};
		else return null;
	}

	@Override
	public String getDefaultName()
	{
		return "container.mechanimation.pulverizer";
	}

	@Override
	public void onStoppedRunning() {}

	@Override
	public boolean canProcessRecipeThisTick(IPulverizerRecipe recipe)
	{
		if (ranThisTick) return false;
		else
		{
			int energy = ENERGY_INDEX_ENERGY_INPUT.getEnergyStored();
			return energy >= this.getEnergyUsage(recipe);
		}
	}

    @Override
	public void preProcess()
	{
    	ranThisTick = false;
    	prevRunning = actuallyRunning;
    	actuallyRunning = false;
	}

    @Override
	public void postProcess()
	{
    	if (actuallyRunning != prevRunning) this.setNeedsUpdate();
	}

	public boolean ranThisTick = false;

	@Override
	public void processRecipe(IPulverizerRecipe recipe)
	{
		ranThisTick = true;
		ENERGY_INDEX_ENERGY_INPUT.remove(this.getEnergyUsage(recipe));
		actuallyRunning = true;
		if (this.runTime >= this.getPulverizeTime())
		{
			ITEM_INDEX_ITEM_OUTPUT.add(recipe.getOutput());
	    	ItemStack stack = ITEM_INDEX_ITEM_INPUT.getStack();
	        int used = recipe.getRequiredCount(stack);
	        if (used > 0) ITEM_INDEX_ITEM_INPUT.remove(used);
	        this.runTime -= this.getPulverizeTime();
		}
	}
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

	@Override
	public void onUsageTick(IPulverizerRecipe recipe) {}

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