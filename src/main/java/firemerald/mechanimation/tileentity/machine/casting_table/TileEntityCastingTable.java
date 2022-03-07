package firemerald.mechanimation.tileentity.machine.casting_table;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import firemerald.api.core.client.Translator;
import firemerald.api.mcms.animation.IAnimation;
import firemerald.api.mcms.util.IItemStackProvider;
import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.casting.CastingRecipes;
import firemerald.mechanimation.api.crafting.casting.EnumCastingType;
import firemerald.mechanimation.api.crafting.casting.ICastingRecipe;
import firemerald.mechanimation.api.util.APIUtils;
import firemerald.mechanimation.api.util.APIUtils.BlockOperationTemp;
import firemerald.mechanimation.client.gui.inventory.GuiMachine;
import firemerald.mechanimation.client.gui.inventory.IGuiElements;
import firemerald.mechanimation.compat.jei.ConfigJEI;
import firemerald.mechanimation.compat.jei.recipe.casting.RecipeCategoryCasting;
import firemerald.mechanimation.inventory.container.ContainerMachine;
import firemerald.mechanimation.inventory.slot.SlotMachine;
import firemerald.mechanimation.tileentity.machine.base.RenderInfo;
import firemerald.mechanimation.tileentity.machine.base.fluids.CommonFluidPredicates;
import firemerald.mechanimation.tileentity.machine.base.fluids.FluidInventoryTemplate;
import firemerald.mechanimation.tileentity.machine.base.fluids.FluidInventoryTemplateBuilder;
import firemerald.mechanimation.tileentity.machine.base.fluids.FluidSlot;
import firemerald.mechanimation.tileentity.machine.base.fluids.FluidSlotInfo;
import firemerald.mechanimation.tileentity.machine.base.fluids.IFluidSlot;
import firemerald.mechanimation.tileentity.machine.base.implementation.actual.oriented.TileEntityOrientedFluidItemMachineBase;
import firemerald.mechanimation.tileentity.machine.base.items.CommonItemPredicates;
import firemerald.mechanimation.tileentity.machine.base.items.IItemSlot;
import firemerald.mechanimation.tileentity.machine.base.items.ItemInventoryTemplate;
import firemerald.mechanimation.tileentity.machine.base.items.ItemInventoryTemplateBuilder;
import firemerald.mechanimation.tileentity.machine.base.operations.CommonOperations;
import firemerald.mechanimation.tileentity.machine.base.operations.OperationsTemplate;
import firemerald.mechanimation.util.EnumFace;
import firemerald.mechanimation.util.Utils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileEntityCastingTable<T extends TileEntityCastingTable<T>> extends TileEntityOrientedFluidItemMachineBase<T, ICastingRecipe> implements IItemStackProvider
{
	public static class InputFluidSlotInfo<T extends TileEntityCastingTable<T>> extends FluidSlotInfo<T>
	{
		public InputFluidSlotInfo(String name, Integer slot, ToIntFunction<? super T> maxCount, ToIntFunction<? super T> maxInsert, Function<? super T, Predicate<FluidOrGasStack>> canInsert, ToIntFunction<? super T> maxExtract, Function<? super T, Predicate<FluidOrGasStack>> canExtract, Function<T, EnumFace[]> sides)
		{
			super(name, slot, maxCount, maxInsert, canInsert, maxExtract, canExtract, sides);
		}

		@Override
		public IFluidSlot makeSlot(T machine)
		{
			return new InputFluidSlot<>(machine, name, slot, maxCount.applyAsInt(machine), maxInsert.applyAsInt(machine), canInsert.apply(machine), maxExtract.applyAsInt(machine), canExtract.apply(machine));
		}
	}

	public static class InputFluidSlot<T extends TileEntityCastingTable<T>> extends FluidSlot<T>
	{
		public InputFluidSlot(T machine, String name, int slot, int capacity, int maxInsert, Predicate<FluidOrGasStack> canInsert, int maxExtract, Predicate<FluidOrGasStack> canExtract)
		{
			super(machine, name, slot, capacity, maxInsert, canInsert, maxExtract, canExtract);
		}

		@Override
		public FluidOrGasStack insertFluid(FluidOrGasStack stack, boolean simulate, @Nullable EnumFace side)
		{
			int capacity = this.fluid == null ? machine.getRequiredFluid(stack) : this.capacity; //manual capacity on fluid first load
			if (!this.canInsert(stack, side) || FluidOrGasStack.getAmountStatic(fluid) >= capacity) return stack;
			int canInsert = Math.min(maxInsert, Math.min(stack.getAmount(), capacity - FluidOrGasStack.getAmountStatic(fluid))); //min(maxReceive, toInsert, available)
			if (canInsert <= 0) return stack;
			else
			{
				FluidOrGasStack remainder = stack.copy();
				remainder.setAmount(remainder.getAmount() - canInsert);
				if (!simulate)
				{
					int count;
					if (fluid == null)
					{
						fluid = stack.copy();
						count = 0;
					}
					else count = fluid.getAmount();
					fluid.setAmount(count + canInsert);
					float fluidTemp;
					ICastingRecipe recipe = CastingRecipes.getRecipe(machine.getType(), fluid);
					fluidTemp = recipe == null ? fluid.isFluid() ? fluid.getFluidStack().getFluid().getTemperature(fluid.getFluidStack()) - 273 : 21 : recipe.getTemperature(fluid); //assume gas temp is 21
					machine.temp = (machine.temp * count + fluidTemp * canInsert) / (count + canInsert);
					setFluid(fluid);
				}
				return remainder;
			}
		}
	}

    public static final ResourceLocation CASTING_TABLE_GUI_TEXTURES = new ResourceLocation(MechanimationAPI.MOD_ID, "textures/gui/container/casting_table.png");
	public static final String
	FLUID_INDEX_INPUT = "fluid_input",
	ITEM_INDEX_OUTPUT = "item_output";
	@SuppressWarnings("unchecked")
	public static final ItemInventoryTemplate<? extends TileEntityCastingTable<?>> ITEM_TEMPLATE = ((ItemInventoryTemplateBuilder<? extends TileEntityCastingTable<?>>) new ItemInventoryTemplateBuilder<>())
    		.addSlot(FLUID_INDEX_INPUT, machine -> 64, machine -> 64, CommonItemPredicates.providesFluidTo(machine -> machine.FLUID_INDEX_FLUID_INPUT), machine -> 64, CommonItemPredicates.doesNotProvideFluidTo(machine -> machine.FLUID_INDEX_FLUID_INPUT), TileEntityCastingTable::getInputSides)
    		.addSlot(ITEM_INDEX_OUTPUT, machine -> 1, machine -> 0, CommonItemPredicates.deny(), machine -> 1, CommonItemPredicates.accept(), TileEntityCastingTable::getOutputSides)
    		.build();
	@SuppressWarnings("unchecked")
	public static final FluidInventoryTemplate<? extends TileEntityCastingTable<?>> FLUID_TEMPLATE = ((FluidInventoryTemplateBuilder<? extends TileEntityCastingTable<?>>) new FluidInventoryTemplateBuilder<>())
			.addSlot(FLUID_INDEX_INPUT, (name, slot) -> new InputFluidSlotInfo<>(name, slot, TileEntityCastingTable::getMaxFluid, TileEntityCastingTable::getMaxFluidTransfer, machine -> machine::isFluidValid, TileEntityCastingTable::getMaxFluidTransfer, CommonFluidPredicates.accept(), TileEntityCastingTable::getInputSides))
    		.build();
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static final OperationsTemplate<TileEntityCastingTable> OPERATION_TEMPLATE = new OperationsTemplate<TileEntityCastingTable>()
			.addOperation(CommonOperations.<TileEntityCastingTable>fluidFromItem(machine -> machine.ITEM_INDEX_FLUID_INPUT, machine -> machine.FLUID_INDEX_FLUID_INPUT, TileEntityCastingTable::getMaxFluidTransferItem));

	public final IItemSlot
	ITEM_INDEX_FLUID_INPUT,
	ITEM_INDEX_ITEM_OUTPUT;
	public final IFluidSlot
	FLUID_INDEX_FLUID_INPUT;
	@SideOnly(Side.CLIENT)
	public RenderInfo
	renderDimsFluidInput;
	public float temp = 21;
	private float ambientTemp;
	public final EnumCastingType[] validTypes;
	private EnumCastingType type;

	@Override
	public ResourceLocation getGuiTexture()
	{
		return CASTING_TABLE_GUI_TEXTURES;
	}

	@SuppressWarnings("unchecked")
	public TileEntityCastingTable(EnumCastingType[] validTypes)
	{
    	super(((ItemInventoryTemplate<T>) ITEM_TEMPLATE)::build, ((FluidInventoryTemplate<T>) FLUID_TEMPLATE)::build);
		this.validTypes = validTypes;
		this.type = validTypes[0];
    	ITEM_INDEX_FLUID_INPUT = this.itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(FLUID_INDEX_INPUT));
    	ITEM_INDEX_ITEM_OUTPUT = this.itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(ITEM_INDEX_OUTPUT));
    	FLUID_INDEX_FLUID_INPUT = this.fluidInventory.getFluidSlot(FLUID_TEMPLATE.getIndex(FLUID_INDEX_INPUT));
    	this.operations = OPERATION_TEMPLATE.build(this);
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
		{
			renderDimsFluidInput = new RenderInfo(59, 21, IGuiElements.FLUID_BAR_DECOR);
		}
	}

	public abstract int getMaxFluid();

	public abstract int getMaxFluidTransfer();

	public abstract int getMaxFluidTransferItem();

	public abstract EnumFace[] getInputSides();

	public abstract EnumFace[] getOutputSides();

	@Override
	public ItemStack getItemStack(int index)
	{
		if (index == 0) return ITEM_INDEX_ITEM_OUTPUT.getStack();
		else return this.getStackInSlot(index - 1);
	}

	public EnumCastingType getType()
	{
		return type;
	}

	public void setType(EnumCastingType type)
	{
		this.type = type;
		updateInputTank(FLUID_INDEX_FLUID_INPUT.getFluid()); //update tank capacity
	}

	public int getRequiredFluid(FluidOrGasStack stack)
	{
		ICastingRecipe recipe = CastingRecipes.getRecipe(this.type, stack);
		if (recipe == null) return 0;
		else
		{
    		int targetAmount = FluidOrGasStack.getAmountStatic(recipe.getInput().stream().filter(st -> st.isFluidEqual(stack)).collect(Collectors.minBy((a, b) -> a.getAmount() - b.getAmount())).orElse(null)); //smallest matching stack
    		if (targetAmount > getMaxFluid()) return 0; //cannot fit
    		else return targetAmount; //set to required fluid
		}
	}

	public void updateInputTank(FluidOrGasStack stack)
	{
		ICastingRecipe recipe = CastingRecipes.getRecipe(this.type, stack);
		if (recipe == null) FLUID_INDEX_FLUID_INPUT.setCapacity(getMaxFluid());
		else
		{
    		int targetAmount = FluidOrGasStack.getAmountStatic(recipe.getInput().stream().filter(st -> st.isFluidEqual(stack)).collect(Collectors.minBy((a, b) -> a.getAmount() - b.getAmount())).orElse(null)); //smallest matching stack
    		if (targetAmount > getMaxFluid()) FLUID_INDEX_FLUID_INPUT.setCapacity(getMaxFluid()); //cannot fit
    		else FLUID_INDEX_FLUID_INPUT.setCapacity(targetAmount); //set to required fluid
		}
	}

	@Override
	public void onFluidSlotChanged(int slot, FluidOrGasStack newStack)
	{
		if (slot == FLUID_INDEX_FLUID_INPUT.getSlot()) updateInputTank(newStack);
		super.onFluidSlotChanged(slot, newStack);
	}

	public boolean isFluidValid(FluidOrGasStack stack)
	{
		ItemStack output = ITEM_INDEX_ITEM_OUTPUT.getStack();
		if (output != null && !output.isEmpty()) return false;
		FluidOrGasStack stored = FLUID_INDEX_FLUID_INPUT.getFluid();
		if (stored == null || stored.getAmount() <= 0) //if empty, check recipe
		{
			ICastingRecipe recipe = CastingRecipes.getRecipe(type, stack);
			if (recipe != null)
			{
	    		FluidOrGasStack target = recipe.getInput().stream().filter(st -> st.isFluidEqual(stack)).collect(Collectors.minBy((a, b) -> a.getAmount() - b.getAmount())).get(); //smallest matching stack
	    		return target.getAmount() <= FLUID_INDEX_FLUID_INPUT.getCapacity();
			}
			else return false;
		}
		else if (stack.isFluidEqual(stored)) //if already contains this fluid then go ahead
		{
			return true;
		}
		else return false;
	}

	public abstract int getAmbientSurfaceArea();

    public abstract AxisAlignedBB getChamberBox();

    @Override
    public void update()
    {
		BlockOperationTemp op = new BlockOperationTemp(getChamberBox(), 0, 3, APIUtils.getBiomeTempCelsius(world, pos));
		APIUtils.forEachBlock(world, op.getEffectiveBox(), false, op);
		ambientTemp = op.getTemp();
    	float deltaAmbient = ambientTemp - temp;
    	ICastingRecipe recipe = this.getValidRecipe();
    	int volume = 100; //TODO volume
    	if (recipe != null) volume += recipe.getTemperature(FLUID_INDEX_FLUID_INPUT.getFluid());
    	this.temp += deltaAmbient * this.getAmbientSurfaceArea() / (volume * 10); //TODO factor
    	super.update();
    }

    @Override
	public ICastingRecipe getValidRecipe()
    {
    	ItemStack item = ITEM_INDEX_ITEM_OUTPUT.getStack();
    	if (item != null && !item.isEmpty()) return null;
    	FluidOrGasStack fluid = FLUID_INDEX_FLUID_INPUT.getFluid();
    	if (fluid == null || fluid.getAmount() <= 0) return null;
    	ICastingRecipe recipe = CastingRecipes.getRecipe(type, fluid);
    	if (recipe == null) return null;
    	else if (recipe.getCooledTemperature(fluid) <= ambientTemp) return null; //cannot reach desired temperature
    	else return recipe;
    }

    @Override
	public String getGuiID()
    {
        return "mechanimation:casting_table";
    }

	@Override
	public boolean isTransparent()
	{
		return this.world != null;
	}

	@Override
	public IAnimation getRunningAnimation()
	{
		return null;
	}

	@Override
	public boolean canProcessRecipeThisTick(ICastingRecipe recipe)
	{
		return recipe.getCooledTemperature() >= this.temp;
	}

	@Override
	public void processRecipe(ICastingRecipe recipe)
	{
    	FluidOrGasStack fluidInput = FLUID_INDEX_FLUID_INPUT.getFluid();
		FluidOrGasStack target = recipe.getInput().stream().filter(stack -> stack.isFluidEqual(fluidInput)).collect(Collectors.minBy((a, b) -> a.getAmount() - b.getAmount())).orElse(null); //smallest matching stack
		if (target != null)
		{
			FLUID_INDEX_FLUID_INPUT.remove(target.getAmount());
			ITEM_INDEX_ITEM_OUTPUT.add(recipe.getOutput(target));
		}
    	this.temp = this.ambientTemp;
	}

	@Override
	public void onStoppedRunning()
	{
		super.onStoppedRunning();
	}

	@Override
    @SideOnly(Side.CLIENT)
    public void onGuiDrawn(GuiMachine<T> gui, int startX, int startY, int adjMouseX, int adjMouseY, float partialTicks)
	{
		//TODO render temp
		gui.mc.fontRenderer.drawString(Translator.format("gui.mechanimation.temp", (int) temp), startX + 74, startY + 53, 0xFF000000);
	}

	@Override
    @SideOnly(Side.CLIENT)
	public void renderProgressMeters(GuiContainer gui, int offX, int offY, float zLevel)
	{
		double progress = getField(0) / 100.0;
		IGuiElements.PROGRESS_RIGHT_PROGRESS.render(offX + 80, offY + 35, zLevel, progress);
	}

    @Override
	public int getField(int id)
    {
        switch (id)
        {
        case 0:
        	ICastingRecipe recipe = this.getValidRecipe();
        	if (recipe != null)
        	{
        		FluidOrGasStack stack = FLUID_INDEX_FLUID_INPUT.getFluid();
        		float heated = recipe.getTemperature(stack);
        		return (int) (100 * (temp - heated) / (recipe.getCooledTemperature(stack) - heated));
        	}
        	return 0;
        case 1:
        	return 100;
        default:
        	return 0;
        }
    }

    @Override
	public void setField(int id, int value)
    {
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
		else return null;
	}

	@Override
    @SideOnly(Side.CLIENT)
    public String[] getRecipeTypes(int adjX, int adjY)
    {
		if (ConfigJEI.INSTANCE.castingRecipes.val && adjX >= 80 && adjX < 102 && adjY >= 35 && adjY < 50) return new String[] {RecipeCategoryCasting.ID};
		else return null;
    }

	@Override
	public void writeToShared(NBTTagCompound tag)
	{
		super.writeToShared(tag);
		tag.setFloat("temp", temp);
		tag.setString("type", type.name());
	}

	@Override
	public void readFromShared(NBTTagCompound tag)
	{
		super.readFromShared(tag);
        this.temp = tag.getFloat("temp");
        this.type = Utils.getEnum(tag.getString("type"), validTypes, validTypes[0]);
	}

	@Override
	public void writeToUpdate(NBTTagCompound tag)
	{
		super.writeToUpdate(tag);
		tag.setInteger("maxFluid", FLUID_INDEX_FLUID_INPUT.getCapacity());
	}

	@Override
	public void readFromUpdate(NBTTagCompound tag)
	{
		super.readFromUpdate(tag);
		FLUID_INDEX_FLUID_INPUT.setCapacity(tag.getInteger("maxFluid"));
	}

    @Override
	public void readFromDisk(NBTTagCompound tag)
    {
        super.readFromDisk(tag);
        this.ambientTemp = tag.getFloat("ambientTemp");
		FluidOrGasStack inputStorage = FLUID_INDEX_FLUID_INPUT.getFluid();
		if (inputStorage != null && inputStorage.getAmount() > 0) updateInputTank(inputStorage); //update tank capacity
		else FLUID_INDEX_FLUID_INPUT.setCapacity(getMaxFluid());
    }

    @Override
	public void writeToDisk(NBTTagCompound tag)
    {
    	super.writeToDisk(tag);
        tag.setFloat("ambientTemp", ambientTemp);
    }

	@SuppressWarnings("unchecked")
	@Override
	public void addItemSlotsToContainer(ContainerMachine<T> container)
	{
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_FLUID_INPUT.getSlot(), 54, 48));
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_ITEM_OUTPUT.getSlot(), 110, 35));
	}

	@Override
	public String getDefaultName()
	{
		return "container.mechanimation.casting_table";
	}

	@Override
	public void onUsageTick(ICastingRecipe recipe) {}
}