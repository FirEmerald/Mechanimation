package firemerald.mechanimation.tileentity.machine.press;

import firemerald.api.mcms.animation.AnimationState;
import firemerald.api.mcms.animation.IAnimation;
import firemerald.api.mcms.util.IItemStackProvider;
import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.api.crafting.press.IPressRecipe;
import firemerald.mechanimation.api.crafting.press.PressRecipes;
import firemerald.mechanimation.client.gui.inventory.IGuiElements;
import firemerald.mechanimation.compat.jei.ConfigJEI;
import firemerald.mechanimation.compat.jei.recipe.press.RecipeCategoryPress;
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
import firemerald.mechanimation.util.Utils;
import firemerald.mechanimation.util.flux.IFluxReceiver;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileEntityPressBase<T extends TileEntityPressBase<T>> extends TileEntityOrientedEnergyItemMachineBase<T, IPressRecipe> implements IFluxReceiver, IItemStackProvider
{
    public static final ResourceLocation PRESS_GUI_TEXTURES = new ResourceLocation(MechanimationAPI.MOD_ID, "textures/gui/container/press.png");
    public static final String
	ENERGY_INDEX_ENERGY = "energy",
	ITEM_INDEX_INPUT = "input_primary",
	ITEM_INDEX_OUTPUT = "output";
    @SuppressWarnings("unchecked")
	public static final ItemInventoryTemplate<? extends TileEntityPressBase<?>> ITEM_TEMPLATE = ((ItemInventoryTemplateBuilder<? extends TileEntityPressBase<?>>) new ItemInventoryTemplateBuilder<>())
    		.addSlot(ENERGY_INDEX_ENERGY, machine -> 64, machine -> 64, CommonItemPredicates.providesEnergy(), machine -> 64, CommonItemPredicates.doesNotProvideEnergy(), machine -> EnumFace.values())
    		.addSlot(ITEM_INDEX_INPUT, machine -> 64, machine -> 64, CommonItemPredicates.of(PressRecipes::isCraftable), machine -> 64, CommonItemPredicates.of(stack -> !PressRecipes.isCraftable(stack)), TileEntityPressBase::getInputFaces)
    		.addSlot(ITEM_INDEX_OUTPUT, machine -> 64, machine -> 0, CommonItemPredicates.deny(), machine -> 64, CommonItemPredicates.accept(), TileEntityPressBase::getOutputFaces)
    		.build();
    @SuppressWarnings("unchecked")
	public static final EnergyInventoryTemplate<? extends TileEntityPressBase<?>> ENERGY_TEMPLATE = ((EnergyInventoryTemplateBuilder<? extends TileEntityPressBase<?>>) new EnergyInventoryTemplateBuilder<>())
    		.addSlot(ENERGY_INDEX_ENERGY, machine -> machine.getMaxEnergy(), machine -> machine.getMaxEnergyTransfer(), CommonEnergyPredicates.accept(), machine -> 0, CommonEnergyPredicates.deny(), machine -> EnumFace.values())
    		.build();
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static final OperationsTemplate<TileEntityPressBase> OPERATIONS_TEMPLATE = new OperationsTemplate<TileEntityPressBase>()
    		.addOperation(CommonOperations.<TileEntityPressBase>energyFromItem(machine -> machine.ITEM_INDEX_ENERGY_INPUT, machine -> machine.ENERGY_INDEX_ENERGY_INPUT, TileEntityPressBase::getMaxEnergyTransferItem));

	public final IItemSlot
	ITEM_INDEX_ENERGY_INPUT,
	ITEM_INDEX_ITEM_INPUT,
	ITEM_INDEX_ITEM_OUTPUT;
	public final IEnergySlot
	ENERGY_INDEX_ENERGY_INPUT;
	@SideOnly(Side.CLIENT)
	public RenderInfo
	renderDimsEnergyInput;

	public static enum Mode
	{
		OPEN,
		CLOSING,
		RUNNING,
		STALLED,
		OPENING;

		public static Mode getMode(int ind)
		{
			if (ind < 0 || ind >= values().length) return OPEN;
			else return values()[ind];
		}
	}

    protected Mode mode = Mode.OPENING;
    public static final AnimationState[] NO_ANIMATION = new AnimationState[0];

	@SuppressWarnings("unchecked")
	public TileEntityPressBase()
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

	@Override
	public AnimationState[] getAnimationStates(float partial)
	{
		if (mode == Mode.OPEN) return NO_ANIMATION;
		else
		{
			if (mode == Mode.STALLED) running[0].time = runTime * .05f;
			else running[0].time = (runTime + partial) * .05f;
			return running;
		}
	}

    public abstract int getPressTime();

    public abstract int getSoundTimestamp();

    public abstract int getOpenTime();

    public abstract int getCloseTime();

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
	public void readFromShared(NBTTagCompound tag)
	{
		super.readFromShared(tag);
        this.setMode(Utils.getEnum(tag.getString("mode"), Mode.values(), Mode.OPEN), tag.getFloat("modeTime"));
	}

	@Override
	public void writeToShared(NBTTagCompound tag)
	{
		super.writeToShared(tag);
		tag.setString("mode", mode.name());
        tag.setFloat("modeTime", runTime);
	}

    /**
     * Like the old updateEntity(), except more generic.
     */
    @Override
	public void update()
    {
    	if (this.world.isRemote)
    	{
    		switch (mode)
    		{
    		case CLOSING:
    			if (runTime < getCloseTime()) runTime++;
    			break;
    		case RUNNING:
    			runTime++;
    			break;
    		case OPENING:
    			if (runTime < getOpenTime()) runTime++;
    			break;
    		default:
    		}
    	}
    	else
		{
			operations.forEach(Runnable::run);
        	if (mode != Mode.OPEN)
        	{
        		switch (mode)
        		{
        		case CLOSING:
        			if (runTime >= getCloseTime())
        			{
            			if (getValidRecipe() == null) setMode(Mode.OPENING);
            			else setMode(Mode.RUNNING);
        			}
        			else
        			{
        				runTime++;
    					this.markDirty();
        			}
        			break;
        		case RUNNING:
        		case STALLED:
        			IPressRecipe recipe = getValidRecipe();
        			if (recipe == null) setMode(Mode.OPENING);
        			else
        			{
        				int extract = getEnergyUsage();
        				if (extract <= ENERGY_INDEX_ENERGY_INPUT.getEnergyStored())
        				{
        					ENERGY_INDEX_ENERGY_INPUT.remove(extract);
        					if (runTime >= getPressTime())
        					{
        						processRecipe(recipe);
        						runTime = 0; //no need to send a client-side update yet
        					}
        	    			else
        	    			{
        	    				runTime++;
        	    				if (runTime == this.getSoundTimestamp())
        	    				{
        	    			        world.playSound(null, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1, 1);
        	    				}
        	    			}
    						this.markDirty();
        					if (this.mode == Mode.STALLED) setMode(Mode.RUNNING, runTime);
        				}
        				else if (mode == Mode.RUNNING) setMode(Mode.STALLED, runTime);
        			}
        			break;
        		case OPENING:
        			if (runTime >= getOpenTime())
        			{
        				if (getValidRecipe() != null) setMode(Mode.CLOSING);
        				else setMode(Mode.OPEN);
        			}
        			else
        			{
        				runTime++;
    					this.markDirty();
        			}
        			break;
        		default:
        		}
        	}
        	else if (getValidRecipe() != null) setMode(Mode.CLOSING);
		}
    	if (needsUpdate)
    	{
    		needsUpdate = false;
    		if (!world.isRemote)
    		{
    			SPacketUpdateTileEntity p = this.getUpdatePacket();
    			for (EntityPlayer player : world.playerEntities) if (player instanceof EntityPlayerMP) if (player.getDistanceSq(this.pos) < 4096.0D) ((EntityPlayerMP) player).connection.sendPacket(p);
    		}
    	}
    }

    public void setMode(Mode mode)
    {
    	setMode(mode, 0);
    }

    public void setMode(Mode mode, float time)
    {
    	this.mode = mode;
    	this.runTime = time;
		setNeedsUpdate();
    }

    @Override
	public IPressRecipe getValidRecipe()
    {
    	ItemStack primary = ITEM_INDEX_ITEM_INPUT.getStack();
    	IPressRecipe result = PressRecipes.getRecipe(primary);
    	if (PressRecipes.isCraftable(result))
    	{
    		int needed = result.getRequiredCount(primary);
    		if (needed > primary.getCount()) return null;
    		ItemStack out = ITEM_INDEX_ITEM_OUTPUT.getStack();
    		if (result.getOutput().getCount() + out.getCount() > this.getInventoryStackLimit()) return null;
    		return result;
    	}
    	else return null;
    }

    private int getEnergyUsage()
    {
    	IPressRecipe result = getValidRecipe();
    	if (result != null)
    	{
        	int pressTime = this.getPressTime();
        	int prevEnergyUsed = (result.getRequiredEnergy() * ((int) runTime - 1)) / pressTime;
        	int newEnergyUsed = (result.getRequiredEnergy() * (int) runTime) / pressTime;
        	return newEnergyUsed - prevEnergyUsed; //correct for non-integer energy used per tick
    	}
    	else return 0;
    }

    @Override
	public String getGuiID()
    {
        return "mechanimation:press";
    }

    @Override
	public int getField(int id)
    {
        switch (id)
        {
            case 0:
                return mode == Mode.RUNNING || mode == Mode.STALLED ? (int) this.runTime : 0;
            case 1:
            	return this.getPressTime();
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
                if (mode == Mode.RUNNING || mode == Mode.STALLED) this.runTime = value;
        }
    }

    @Override
	public int getFieldCount()
    {
        return 2;
    }

	public ItemStack removeManually(ItemStack to, int maxStackSize)
	{
		if (mode == Mode.OPEN)
		{
			ItemStack from = ITEM_INDEX_ITEM_OUTPUT.getStack();
			if (!from.isEmpty())
			{
				boolean flag = false;
				if (to.isEmpty())
				{
					to = from.copy();
					from.setCount(0);
					ITEM_INDEX_ITEM_OUTPUT.setStack(from);
					flag = true;
				}
				else if (ItemStack.areItemsEqual(to, from))
				{
					int change = Math.min(Math.min(to.getMaxStackSize(), maxStackSize) - to.getCount(), from.getCount());
					if (change > 0)
					{
						to.grow(change);
						from.shrink(change);
						flag = true;
					}
				}
				if (flag)
				{
					setNeedsUpdate();
					return to;
				}
			}
		}
		return null;
	}

	public ItemStack insertManually(ItemStack from)
	{
		if (mode == Mode.OPEN)
		{
			if (ITEM_INDEX_ITEM_INPUT.canInsert(from, null))
			{
				ItemStack to = ITEM_INDEX_ITEM_INPUT.getStack();
				if (!from.isEmpty())
				{
					boolean flag = false;
					if (to.isEmpty())
					{
						to = from.copy();
						from.setCount(0);
						ITEM_INDEX_ITEM_INPUT.setStack(to);
						flag = true;
					}
					else if (ItemStack.areItemsEqual(to, from))
					{
						int change = Math.min(Math.min(to.getMaxStackSize(), this.getInventoryStackLimit()) - to.getCount(), from.getCount());
						if (change > 0)
						{
							to.grow(change);
							from.shrink(change);
							flag = true;
						}
					}
					if (flag)
					{
						setNeedsUpdate();
						return from;
					}
				}
			}
		}
		return null;
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
		return PRESS_GUI_TEXTURES;
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
		if (ConfigJEI.INSTANCE.pressRecipes.val && adjX >= 80 && adjX < 102 && adjY >= 35 && adjY < 51) return new String[] {RecipeCategoryPress.ID};
		else return null;
	}

	@Override
	public IAnimation getRunningAnimation()
    {
    	switch (mode)
    	{
    	case CLOSING:
    		return getClosingAnimation();
    	case RUNNING:
    	case STALLED:
    		return getRunAnimation();
    	case OPENING:
    		return getOpeningAnimation();
    	default:
    		return null;
    	}
    }

	public abstract IAnimation getClosingAnimation();

	public abstract IAnimation getRunAnimation();

	public abstract IAnimation getOpeningAnimation();

	@Override
	public String getDefaultName()
	{
		return "container.mechanimation.press";
	}

	@Override
	public void onStoppedRunning() {}

	@Override
	public boolean canProcessRecipeThisTick(IPressRecipe recipe)
	{
		return false;
	}

	@Override
	public void processRecipe(IPressRecipe recipe)
	{
		ITEM_INDEX_ITEM_OUTPUT.add(recipe.getOutput());
    	ItemStack primary = ITEM_INDEX_ITEM_INPUT.getStack();
        int used = recipe.getRequiredCount(primary);
        if (used > 0) ITEM_INDEX_ITEM_INPUT.remove(used);
	}

	@Override
	public void onUsageTick(IPressRecipe recipe) {}

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