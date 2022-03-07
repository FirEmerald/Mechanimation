package firemerald.mechanimation.tileentity.machine.fluid_reactor;

import firemerald.api.mcms.animation.IAnimation;
import firemerald.api.mcms.model.IModel;
import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.api.crafting.CraftingUtil;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.fluid_reactor.FluidReactorRecipes;
import firemerald.mechanimation.api.crafting.fluid_reactor.IFluidReactorRecipe;
import firemerald.mechanimation.client.gui.inventory.IGuiElements;
import firemerald.mechanimation.compat.jei.ConfigJEI;
import firemerald.mechanimation.compat.jei.recipe.fluid_reactor.RecipeCategoryFluidReactor;
import firemerald.mechanimation.inventory.container.ContainerMachine;
import firemerald.mechanimation.inventory.slot.SlotMachine;
import firemerald.mechanimation.mcms.MCMSAnimation;
import firemerald.mechanimation.mcms.MCMSModel;
import firemerald.mechanimation.mcms.MCMSTexture;
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
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileEntityFluidReactorBase<T extends TileEntityFluidReactorBase<T>> extends TileEntityOrientedFluidItemMachineBase<T, IFluidReactorRecipe>
{
    public static final ResourceLocation FLUID_REACTOR_GUI_TEXTURES = new ResourceLocation(MechanimationAPI.MOD_ID, "textures/gui/container/fluid_reactor.png");
	public float progress = 0;
	public static final String
	ITEM_INDEX_INPUT = "item_input",
	FLUID_INDEX_PRIMARY_INPUT = "primary_fluid_input",
	FLUID_INDEX_SECONDARY_INPUT = "secondary_fluid_input",
	FLUID_INDEX_TERTIARY_INPUT = "tertiary_fluid_input",
	ITEM_INDEX_OUTPUT = "item_output",
	FLUID_INDEX_OUTPUT = "fluid_output";
	public static final EnumFace[]
	FACE_ITEM_INPUT = { EnumFace.TOP },
	FACE_PRIMARY_FLUID_INPUT = { EnumFace.RIGHT },
	FACE_SECONDARY_FLUID_INPUT = { EnumFace.BACK },
	FACE_TERTIARY_FLUID_INPUT = { EnumFace.FRONT },
	FACE_ITEM_OUTPUT = { EnumFace.BOTTOM },
	FACE_FLUID_OUTPUT = { EnumFace.LEFT };
    @SuppressWarnings("unchecked")
	public static final ItemInventoryTemplate<? extends TileEntityFluidReactorBase<?>> ITEM_TEMPLATE = ((ItemInventoryTemplateBuilder<? extends TileEntityFluidReactorBase<?>>) new ItemInventoryTemplateBuilder<>())
    		.addSlot(ITEM_INDEX_INPUT, machine -> 64, machine -> 64, machine -> stack -> machine.isCraftableItem(stack), machine -> 64, CommonItemPredicates.accept(), machine -> FACE_ITEM_INPUT)
    		.addSlot(FLUID_INDEX_PRIMARY_INPUT, machine -> 64, machine -> 64, CommonItemPredicates.providesFluidTo(machine -> machine.FLUID_INDEX_PRIMARY_FLUID_INPUT), machine -> 64, CommonItemPredicates.doesNotProvideFluidTo(machine -> machine.FLUID_INDEX_PRIMARY_FLUID_INPUT), machine -> FACE_PRIMARY_FLUID_INPUT)
    		.addSlot(FLUID_INDEX_SECONDARY_INPUT, machine -> 64, machine -> 64, CommonItemPredicates.providesFluidTo(machine -> machine.FLUID_INDEX_SECONDARY_FLUID_INPUT), machine -> 64, CommonItemPredicates.doesNotProvideFluidTo(machine -> machine.FLUID_INDEX_SECONDARY_FLUID_INPUT), machine -> FACE_SECONDARY_FLUID_INPUT)
    		.addSlot(FLUID_INDEX_TERTIARY_INPUT, machine -> 64, machine -> 64, CommonItemPredicates.providesFluidTo(machine -> machine.FLUID_INDEX_TERTIARY_FLUID_INPUT), machine -> 64, CommonItemPredicates.doesNotProvideFluidTo(machine -> machine.FLUID_INDEX_TERTIARY_FLUID_INPUT), machine -> FACE_TERTIARY_FLUID_INPUT)
    		.addSlot(ITEM_INDEX_OUTPUT, machine -> 64, machine -> 0, CommonItemPredicates.deny(), machine -> 64, CommonItemPredicates.accept(), machine -> FACE_ITEM_OUTPUT)
    		.addSlot(FLUID_INDEX_OUTPUT, machine -> 64, machine -> 64, CommonItemPredicates.receivesFluidFrom(machine -> machine.FLUID_INDEX_FLUID_OUTPUT), machine -> 64, CommonItemPredicates.doesNotReceiveFluidFrom(machine -> machine.FLUID_INDEX_FLUID_OUTPUT), machine -> FACE_FLUID_OUTPUT)
    		.build();
    @SuppressWarnings("unchecked")
	public static final FluidInventoryTemplate<? extends TileEntityFluidReactorBase<?>> FLUID_TEMPLATE = ((FluidInventoryTemplateBuilder<? extends TileEntityFluidReactorBase<?>>) new FluidInventoryTemplateBuilder<>())
    		.addSlot(FLUID_INDEX_PRIMARY_INPUT, machine -> machine.getMaxFluid(), machine -> machine.getMaxFluidTransfer(), machine -> stack -> machine.isCraftablePrimary(stack), machine -> machine.getMaxFluidTransfer(), CommonFluidPredicates.accept(), machine -> FACE_PRIMARY_FLUID_INPUT)
    		.addSlot(FLUID_INDEX_SECONDARY_INPUT, machine -> machine.getMaxFluid(), machine -> machine.getMaxFluidTransfer(), machine -> stack -> machine.isCraftableSecondary(stack), machine -> machine.getMaxFluidTransfer(), CommonFluidPredicates.accept(), machine -> FACE_SECONDARY_FLUID_INPUT)
    		.addSlot(FLUID_INDEX_TERTIARY_INPUT, machine -> machine.getMaxFluid(), machine -> machine.getMaxFluidTransfer(), machine -> stack -> machine.isCraftableTertiary(stack), machine -> machine.getMaxFluidTransfer(), CommonFluidPredicates.accept(), machine -> FACE_TERTIARY_FLUID_INPUT)
    		.addSlot(FLUID_INDEX_OUTPUT, machine -> machine.getMaxFluid(), machine -> 0, CommonFluidPredicates.deny(), machine -> machine.getMaxFluidTransfer(), CommonFluidPredicates.accept(), machine -> FACE_FLUID_OUTPUT)
    		.build();
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static final OperationsTemplate<TileEntityFluidReactorBase> OPERATIONS_TEMPLATE = new OperationsTemplate<TileEntityFluidReactorBase>()
    		.addOperation(CommonOperations.<TileEntityFluidReactorBase>fluidFromItem(machine -> machine.ITEM_INDEX_PRIMARY_FLUID_INPUT, machine -> machine.FLUID_INDEX_PRIMARY_FLUID_INPUT, TileEntityFluidReactorBase::getMaxFluidTransferItem))
    		.addOperation(CommonOperations.<TileEntityFluidReactorBase>fluidFromItem(machine -> machine.ITEM_INDEX_SECONDARY_FLUID_INPUT, machine -> machine.FLUID_INDEX_SECONDARY_FLUID_INPUT, TileEntityFluidReactorBase::getMaxFluidTransferItem))
    		.addOperation(CommonOperations.<TileEntityFluidReactorBase>fluidFromItem(machine -> machine.ITEM_INDEX_TERTIARY_FLUID_INPUT, machine -> machine.FLUID_INDEX_TERTIARY_FLUID_INPUT, TileEntityFluidReactorBase::getMaxFluidTransferItem))
    		.addOperation(CommonOperations.<TileEntityFluidReactorBase>fluidToItem(machine -> machine.ITEM_INDEX_FLUID_OUTPUT, machine -> machine.FLUID_INDEX_FLUID_OUTPUT, TileEntityFluidReactorBase::getMaxFluidTransferItem));

	public final IItemSlot
	ITEM_INDEX_ITEM_INPUT,
	ITEM_INDEX_PRIMARY_FLUID_INPUT,
	ITEM_INDEX_SECONDARY_FLUID_INPUT,
	ITEM_INDEX_TERTIARY_FLUID_INPUT,
	ITEM_INDEX_ITEM_OUTPUT,
	ITEM_INDEX_FLUID_OUTPUT;
	public final IFluidSlot
	FLUID_INDEX_PRIMARY_FLUID_INPUT,
	FLUID_INDEX_SECONDARY_FLUID_INPUT,
	FLUID_INDEX_TERTIARY_FLUID_INPUT,
	FLUID_INDEX_FLUID_OUTPUT;
	@SideOnly(Side.CLIENT)
	public RenderInfo
	renderDimsPrimaryFluidInput,
	renderDimsSecondaryFluidInput,
	renderDimsTertiaryFluidInput,
	renderDimsFluidOutput;

	@SuppressWarnings("unchecked")
	public TileEntityFluidReactorBase()
	{
		super(((ItemInventoryTemplate<T>) ITEM_TEMPLATE)::build, ((FluidInventoryTemplate<T>) FLUID_TEMPLATE)::build);
    	ITEM_INDEX_ITEM_INPUT = itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(ITEM_INDEX_INPUT));
    	ITEM_INDEX_PRIMARY_FLUID_INPUT = itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(FLUID_INDEX_PRIMARY_INPUT));
    	ITEM_INDEX_SECONDARY_FLUID_INPUT = itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(FLUID_INDEX_SECONDARY_INPUT));
    	ITEM_INDEX_TERTIARY_FLUID_INPUT = itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(FLUID_INDEX_TERTIARY_INPUT));
    	ITEM_INDEX_ITEM_OUTPUT = itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(ITEM_INDEX_OUTPUT));
    	ITEM_INDEX_FLUID_OUTPUT = itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(FLUID_INDEX_OUTPUT));
    	FLUID_INDEX_PRIMARY_FLUID_INPUT = fluidInventory.getFluidSlot(FLUID_TEMPLATE.getIndex(FLUID_INDEX_PRIMARY_INPUT));
    	FLUID_INDEX_SECONDARY_FLUID_INPUT = fluidInventory.getFluidSlot(FLUID_TEMPLATE.getIndex(FLUID_INDEX_SECONDARY_INPUT));
    	FLUID_INDEX_TERTIARY_FLUID_INPUT = fluidInventory.getFluidSlot(FLUID_TEMPLATE.getIndex(FLUID_INDEX_TERTIARY_INPUT));
    	FLUID_INDEX_FLUID_OUTPUT = fluidInventory.getFluidSlot(FLUID_TEMPLATE.getIndex(FLUID_INDEX_OUTPUT));
    	this.operations = OPERATIONS_TEMPLATE.build(this);
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
		{
			renderDimsPrimaryFluidInput = new RenderInfo(13, 28, IGuiElements.FLUID_BAR_DECOR);
			renderDimsSecondaryFluidInput = new RenderInfo(35, 28, IGuiElements.FLUID_BAR_DECOR);
			renderDimsTertiaryFluidInput = new RenderInfo(57, 28, IGuiElements.FLUID_BAR_DECOR);
			renderDimsFluidOutput = new RenderInfo(157, 28, IGuiElements.FLUID_BAR_DECOR);
		}
	}

	public abstract int getMaxFluid();

	public abstract int getMaxFluidTransfer();

	public abstract int getMaxFluidTransferItem();

    public ItemStack getItemOrNull()
    {
    	ItemStack stack = ITEM_INDEX_ITEM_INPUT.getStack();
    	return stack.isEmpty() ? null : stack;
    }

	public boolean isCraftableItem(ItemStack stack)
	{
		return FluidReactorRecipes.isCraftable(stack, FLUID_INDEX_PRIMARY_FLUID_INPUT.getFluid(), FLUID_INDEX_SECONDARY_FLUID_INPUT.getFluid(), FLUID_INDEX_TERTIARY_FLUID_INPUT.getFluid());
	}

	public boolean isCraftablePrimary(FluidOrGasStack stack)
	{
		return FluidReactorRecipes.isCraftable(getItemOrNull(), stack, FLUID_INDEX_SECONDARY_FLUID_INPUT.getFluid(), FLUID_INDEX_TERTIARY_FLUID_INPUT.getFluid());
	}

	public boolean isCraftableSecondary(FluidOrGasStack stack)
	{
		return FluidReactorRecipes.isCraftable(getItemOrNull(), FLUID_INDEX_PRIMARY_FLUID_INPUT.getFluid(), stack, FLUID_INDEX_TERTIARY_FLUID_INPUT.getFluid());
	}

	public boolean isCraftableTertiary(FluidOrGasStack stack)
	{
		return FluidReactorRecipes.isCraftable(getItemOrNull(), FLUID_INDEX_PRIMARY_FLUID_INPUT.getFluid(), FLUID_INDEX_SECONDARY_FLUID_INPUT.getFluid(), stack);
	}

	@Override
    public void onStoppedRunning()
    {
		progress = 0;
    }

    @Override
    public void onUsageTick(IFluidReactorRecipe recipe)
    {
    	progress += .05f;
    }

    @Override
	public IFluidReactorRecipe getValidRecipe()
    {
    	ItemStack item = ITEM_INDEX_ITEM_INPUT.getStack();
    	FluidOrGasStack fluidInput1 = FLUID_INDEX_PRIMARY_FLUID_INPUT.getFluid();
    	FluidOrGasStack fluidInput2 = FLUID_INDEX_SECONDARY_FLUID_INPUT.getFluid();
    	FluidOrGasStack fluidInput3 = FLUID_INDEX_TERTIARY_FLUID_INPUT.getFluid();
    	IFluidReactorRecipe recipe = FluidReactorRecipes.getRecipe(item, fluidInput1, fluidInput2, fluidInput3);
    	if (FluidReactorRecipes.isCraftable(recipe) && recipe.isInputValidComplete(item, fluidInput1, fluidInput2, fluidInput3))
    	{
    		if (item != null && !item.isEmpty() && recipe.getRequiredCount(item, fluidInput1, fluidInput2, fluidInput3) > item.getCount()) return null;
    		else
    		{
    			FluidOrGasStack primary, secondary, tertiary;
    			boolean matched1 = false, matched2 = false, matched3 = false;
    			if (!recipe.getFluidInputPrimary().isEmpty())
    			{
    				FluidOrGasStack first = CraftingUtil.getFluidWithSize(recipe.getFluidInputPrimary(), fluidInput1);
    				int amount1 = first == null ? Integer.MAX_VALUE : first.getAmount();
    				FluidOrGasStack second = CraftingUtil.getFluidWithSize(recipe.getFluidInputPrimary(), fluidInput2);
    				int amount2 = second == null ? Integer.MAX_VALUE : second.getAmount();
    				FluidOrGasStack third = CraftingUtil.getFluidWithSize(recipe.getFluidInputPrimary(), fluidInput3);
    				int amount3 = third == null ? Integer.MAX_VALUE : third.getAmount();
    				if (amount1 <= amount3)
    				{
    					if (amount1 <= amount2)
    					{
    						matched1 = true;
    						primary = first;
    					}
    					else
    					{
    						matched2 = true;
    						primary = second;
    					}
    				}
    				else if (amount2 <= amount3)
    				{
						matched2 = true;
						primary = second;
    				}
					else
					{
						matched3 = true;
						primary = third;
					}
    				if (primary == null) return null;
    			}
    			if (!recipe.getFluidInputSecondary().isEmpty())
    			{
    				FluidOrGasStack first = matched1 ? null : CraftingUtil.getFluidWithSize(recipe.getFluidInputSecondary(), fluidInput1);
    				int amount1 = first == null ? Integer.MAX_VALUE : first.getAmount();
    				FluidOrGasStack second = matched2 ? null : CraftingUtil.getFluidWithSize(recipe.getFluidInputSecondary(), fluidInput2);
    				int amount2 = second == null ? Integer.MAX_VALUE : second.getAmount();
    				FluidOrGasStack third = matched3 ? null : CraftingUtil.getFluidWithSize(recipe.getFluidInputSecondary(), fluidInput3);
    				int amount3 = third == null ? Integer.MAX_VALUE : third.getAmount();
    				if (amount1 <= amount3)
    				{
    					if (amount1 <= amount2)
    					{
    						matched1 = true;
    						secondary = first;
    					}
    					else
    					{
    						matched2 = true;
    						secondary = second;
    					}
    				}
    				else if (amount2 <= amount3)
    				{
						matched2 = true;
						secondary = second;
    				}
					else
					{
						matched3 = true;
						secondary = third;
					}
    				if (secondary == null) return null;
    			}
    			if (!recipe.getFluidInputTertiary().isEmpty())
    			{
    				FluidOrGasStack first = matched1 ? null : CraftingUtil.getFluidWithSize(recipe.getFluidInputTertiary(), fluidInput1);
    				int amount1 = first == null ? Integer.MAX_VALUE : first.getAmount();
    				FluidOrGasStack second = matched2 ? null : CraftingUtil.getFluidWithSize(recipe.getFluidInputTertiary(), fluidInput2);
    				int amount2 = second == null ? Integer.MAX_VALUE : second.getAmount();
    				FluidOrGasStack third = matched3 ? null : CraftingUtil.getFluidWithSize(recipe.getFluidInputTertiary(), fluidInput3);
    				int amount3 = third == null ? Integer.MAX_VALUE : third.getAmount();
    				if (amount1 <= amount3)
    				{
    					if (amount1 <= amount2)
    					{
    						matched1 = true;
    						tertiary = first;
    					}
    					else
    					{
    						matched2 = true;
    						tertiary = second;
    					}
    				}
    				else if (amount2 <= amount3)
    				{
						matched2 = true;
						tertiary = second;
    				}
					else
					{
						matched3 = true;
						tertiary = third;
					}
    				if (tertiary == null) return null;
    			}
				ItemStack inv = ITEM_INDEX_ITEM_OUTPUT.getStack();
				ItemStack out = recipe.getItemOutput();
    			if (inv != null && !inv.isEmpty() && out != null && !out.isEmpty())
    			{
    				if (!ItemStack.areItemsEqual(inv, out) || !ItemStack.areItemStackTagsEqual(inv, out) || out.getCount() + inv.getCount() > inv.getMaxStackSize()) return null;
    			}
    			FluidOrGasStack fluidOutput = FLUID_INDEX_FLUID_OUTPUT.getFluid();
    			if (fluidOutput != null)
    			{
    				FluidOrGasStack outFluid = recipe.getFluidOutput();
    				if (outFluid != null)
    				{
    					if (!outFluid.isFluidEqual(fluidOutput) || (outFluid.getAmount() + fluidOutput.getAmount()) > FLUID_INDEX_FLUID_OUTPUT.getCapacity()) return null;
    				}
    			}
    			return recipe;
    		}
    	}
    	else return null;
    }

    @Override
	public String getGuiID()
    {
        return "mechanimation:fluid_reactor";
    }

    @Override
	public int getField(int id)
    {
        switch (id)
        {
            case 0:
                return this.isRunning ? (int) (this.progress * 1000) : 0;
            case 1:
            	IFluidReactorRecipe recipe = this.getValidRecipe();
            	return recipe != null ? (int) (recipe.getRequiredSeconds() * 1000) : 1000;
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
                if (this.isRunning) this.progress = value * .001f;
        }
    } //not used

    @Override
	public int getFieldCount()
    {
        return 2;
    }

	@Override
	public IModel<?, ?> getModel(float partial)
	{
		return MCMSModel.FLUID_REACTOR_ADVANCED.model.get();
	}

	@Override
	public ResourceLocation getTexture()
	{
		return MCMSTexture.FLUID_REACTOR_ADVANCED;
	}

	@Override
	public boolean isTransparent()
	{
		return this.world != null;
	}


	@Override
	public IAnimation getRunningAnimation()
	{
		return MCMSAnimation.FLUID_REACTOR_ADVANCED_RUNNING.animation.get();
	}


	@Override
	public String getDefaultName()
	{
		return "container.mechanimation.fluid_reactor";
	}


	@Override
	public boolean canProcessRecipeThisTick(IFluidReactorRecipe recipe)
	{
		return this.progress >= recipe.getRequiredSeconds();
	}


	@Override
	public void processRecipe(IFluidReactorRecipe recipe) //TODO onFluidSlotChanged
	{
    	FluidOrGasStack fluidInput1 = FLUID_INDEX_PRIMARY_FLUID_INPUT.getFluid();
    	FluidOrGasStack fluidInput2 = FLUID_INDEX_SECONDARY_FLUID_INPUT.getFluid();
    	FluidOrGasStack fluidInput3 = FLUID_INDEX_TERTIARY_FLUID_INPUT.getFluid();
		FluidOrGasStack primary = null, secondary = null, tertiary = null;
		FluidOrGasStack firstStack = null, secondStack = null, thirdStack = null;
		boolean matched1 = false, matched2 = false, matched3 = false;
		if (!recipe.getFluidInputPrimary().isEmpty())
		{
			FluidOrGasStack first = CraftingUtil.getFluidWithSize(recipe.getFluidInputPrimary(), fluidInput1);
			int amount1 = first == null ? Integer.MAX_VALUE : first.getAmount();
			FluidOrGasStack second = CraftingUtil.getFluidWithSize(recipe.getFluidInputPrimary(), fluidInput2);
			int amount2 = second == null ? Integer.MAX_VALUE : second.getAmount();
			FluidOrGasStack third = CraftingUtil.getFluidWithSize(recipe.getFluidInputPrimary(), fluidInput3);
			int amount3 = third == null ? Integer.MAX_VALUE : third.getAmount();
			if (amount1 <= amount3)
			{
				if (amount1 <= amount2)
				{
					matched1 = true;
					primary = first;
					firstStack = fluidInput1;
				}
				else
				{
					matched2 = true;
					primary = second;
					firstStack = fluidInput2;
				}
			}
			else if (amount2 <= amount3)
			{
				matched2 = true;
				primary = second;
				firstStack = fluidInput2;
			}
			else
			{
				matched3 = true;
				primary = third;
				firstStack = fluidInput3;
			}
			if (primary == null) return;
		}
		if (!recipe.getFluidInputSecondary().isEmpty())
		{
			FluidOrGasStack first = matched1 ? null : CraftingUtil.getFluidWithSize(recipe.getFluidInputSecondary(), fluidInput1);
			int amount1 = first == null ? Integer.MAX_VALUE : first.getAmount();
			FluidOrGasStack second = matched2 ? null : CraftingUtil.getFluidWithSize(recipe.getFluidInputSecondary(), fluidInput2);
			int amount2 = second == null ? Integer.MAX_VALUE : second.getAmount();
			FluidOrGasStack third = matched3 ? null : CraftingUtil.getFluidWithSize(recipe.getFluidInputSecondary(), fluidInput3);
			int amount3 = third == null ? Integer.MAX_VALUE : third.getAmount();
			if (amount1 <= amount3)
			{
				if (amount1 <= amount2)
				{
					matched1 = true;
					secondary = first;
					secondStack = fluidInput1;
				}
				else
				{
					matched2 = true;
					secondary = second;
					secondStack = fluidInput2;
				}
			}
			else if (amount2 <= amount3)
			{
				matched2 = true;
				secondary = second;
				secondStack = fluidInput2;
			}
			else
			{
				matched3 = true;
				secondary = third;
				secondStack = fluidInput3;
			}
			if (secondary == null) return;
		}
		if (!recipe.getFluidInputTertiary().isEmpty())
		{
			FluidOrGasStack first = matched1 ? null : CraftingUtil.getFluidWithSize(recipe.getFluidInputTertiary(), fluidInput1);
			int amount1 = first == null ? Integer.MAX_VALUE : first.getAmount();
			FluidOrGasStack second = matched2 ? null : CraftingUtil.getFluidWithSize(recipe.getFluidInputTertiary(), fluidInput2);
			int amount2 = second == null ? Integer.MAX_VALUE : second.getAmount();
			FluidOrGasStack third = matched3 ? null : CraftingUtil.getFluidWithSize(recipe.getFluidInputTertiary(), fluidInput3);
			int amount3 = third == null ? Integer.MAX_VALUE : third.getAmount();
			if (amount1 <= amount3)
			{
				if (amount1 <= amount2)
				{
					matched1 = true;
					tertiary = first;
					thirdStack = fluidInput1;
				}
				else
				{
					matched2 = true;
					tertiary = second;
					thirdStack = fluidInput2;
				}
			}
			else if (amount2 <= amount3)
			{
				matched2 = true;
				tertiary = second;
				thirdStack = fluidInput2;
			}
			else
			{
				matched3 = true;
				tertiary = third;
				thirdStack = fluidInput3;
			}
			if (tertiary == null) return;
		}
		if (firstStack != null) firstStack.changeAmount(-primary.getAmount());
		if (secondStack != null) secondStack.changeAmount(-secondary.getAmount());
		if (thirdStack != null) thirdStack.changeAmount(-tertiary.getAmount());
		if (fluidInput1 != null && fluidInput1.getAmount() <= 0) fluidInput1 = null;
		if (fluidInput2 != null && fluidInput2.getAmount() <= 0) fluidInput2 = null;
		if (fluidInput3 != null && fluidInput3.getAmount() <= 0) fluidInput3 = null;

		ItemStack outputItem = recipe.getItemOutput();
		if (outputItem != null && !outputItem.isEmpty()) ITEM_INDEX_ITEM_OUTPUT.add(outputItem);
		FluidOrGasStack outputFluid = recipe.getFluidOutput();
		if (outputFluid != null) FLUID_INDEX_FLUID_OUTPUT.add(outputFluid);
		ItemStack inputItem = ITEM_INDEX_ITEM_INPUT.getStack();
		if (inputItem != null && !inputItem.isEmpty()) ITEM_INDEX_ITEM_INPUT.remove(recipe.getRequiredCount(inputItem, fluidInput1, fluidInput2, fluidInput3));
		this.progress -= recipe.getRequiredSeconds();
		FLUID_INDEX_PRIMARY_FLUID_INPUT.setFluid(fluidInput1);
		FLUID_INDEX_SECONDARY_FLUID_INPUT.setFluid(fluidInput2);
		FLUID_INDEX_TERTIARY_FLUID_INPUT.setFluid(fluidInput3);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addItemSlotsToContainer(ContainerMachine<T> container)
	{
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_ITEM_INPUT.getSlot(), 74, 55));
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_PRIMARY_FLUID_INPUT.getSlot(), 8, 55));
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_SECONDARY_FLUID_INPUT.getSlot(), 30, 55));
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_TERTIARY_FLUID_INPUT.getSlot(), 52, 55));
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_ITEM_OUTPUT.getSlot(), 130, 55));
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_FLUID_OUTPUT.getSlot(), 152, 55));
	}

	@Override
    @SideOnly(Side.CLIENT)
	public ResourceLocation getGuiTexture()
	{
		return FLUID_REACTOR_GUI_TEXTURES;
	}

	@Override
    @SideOnly(Side.CLIENT)
	public void renderProgressMeters(GuiContainer gui, int offX, int offY, float zLevel)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public RenderInfo getFluidRenderInfo(int index)
	{
		if (index == FLUID_INDEX_PRIMARY_FLUID_INPUT.getSlot()) return renderDimsPrimaryFluidInput;
		else if (index == FLUID_INDEX_SECONDARY_FLUID_INPUT.getSlot()) return renderDimsSecondaryFluidInput;
		else if (index == FLUID_INDEX_TERTIARY_FLUID_INPUT.getSlot()) return renderDimsTertiaryFluidInput;
		else if (index == FLUID_INDEX_FLUID_OUTPUT.getSlot()) return renderDimsFluidOutput;
		else return null;
	}

	@Override
    @SideOnly(Side.CLIENT)
    public String[] getRecipeTypes(int adjX, int adjY)
    {
		if (ConfigJEI.INSTANCE.fluidReactorRecipes.val && adjX >= 104 && adjX < 116 && adjY >= 28 && adjY < 52) return new String[] {RecipeCategoryFluidReactor.ID};
		else return null;
    }

	@Override
	public int getMaxFluid(int index)
	{
		IFluidSlot slot = fluidInventory.getFluidSlot(index);
		if (slot == null) return 0;
		else return slot.getCapacity();
	}
}