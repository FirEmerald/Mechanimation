package firemerald.mechanimation.tileentity.machine.assembly_terminal;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import com.google.common.base.Objects;

import firemerald.api.betterscreens.components.Button;
import firemerald.mechanimation.Main;
import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.api.crafting.assembly_terminal.AssemblyRecipes;
import firemerald.mechanimation.api.crafting.assembly_terminal.IAssemblyBlueprint;
import firemerald.mechanimation.api.crafting.assembly_terminal.IAssemblyPredicate;
import firemerald.mechanimation.api.crafting.assembly_terminal.IAssemblyRecipe;
import firemerald.mechanimation.api.util.Rectangle;
import firemerald.mechanimation.api.util.Vec2i;
import firemerald.mechanimation.client.gui.inventory.GuiMachine;
import firemerald.mechanimation.client.gui.inventory.IGuiElements;
import firemerald.mechanimation.compat.jei.ConfigJEI;
import firemerald.mechanimation.compat.jei.recipe.assembly_terminal.RecipeCategoryAssembly;
import firemerald.mechanimation.inventory.container.ContainerMachine;
import firemerald.mechanimation.inventory.slot.SlotAssemblyBlueprint;
import firemerald.mechanimation.inventory.slot.SlotMachine;
import firemerald.mechanimation.networking.server.MachineGUIPacket;
import firemerald.mechanimation.tileentity.machine.base.RenderInfo;
import firemerald.mechanimation.tileentity.machine.base.energy.CommonEnergyPredicates;
import firemerald.mechanimation.tileentity.machine.base.energy.EnergyInventoryTemplate;
import firemerald.mechanimation.tileentity.machine.base.energy.EnergyInventoryTemplateBuilder;
import firemerald.mechanimation.tileentity.machine.base.energy.IEnergySlot;
import firemerald.mechanimation.tileentity.machine.base.implementation.actual.oriented.TileEntityOrientedEnergyItemMachineBase;
import firemerald.mechanimation.tileentity.machine.base.items.CommonItemPredicates;
import firemerald.mechanimation.tileentity.machine.base.items.IItemSlot;
import firemerald.mechanimation.tileentity.machine.base.items.ItemInventory;
import firemerald.mechanimation.tileentity.machine.base.items.ItemInventoryTemplate;
import firemerald.mechanimation.tileentity.machine.base.items.ItemInventoryTemplateBuilder;
import firemerald.mechanimation.tileentity.machine.base.items.ItemSlot;
import firemerald.mechanimation.tileentity.machine.base.items.ItemSlotInfo;
import firemerald.mechanimation.tileentity.machine.base.operations.CommonOperations;
import firemerald.mechanimation.tileentity.machine.base.operations.OperationsTemplate;
import firemerald.mechanimation.util.EnumFace;
import firemerald.mechanimation.util.flux.IFluxReceiver;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileEntityAssemblyTerminalBase<T extends TileEntityAssemblyTerminalBase<T>> extends TileEntityOrientedEnergyItemMachineBase<T, IAssemblyRecipe> implements IFluxReceiver
{
	public static class AssemblyTableInventory<T extends TileEntityAssemblyTerminalBase<T>> extends ItemInventory<T>
	{
		public IItemSlot[] additionalSlots = new ItemSlot[0];
		public List<EnumFace> faces = Collections.emptyList();

		public AssemblyTableInventory(T machine, ItemSlotInfo<T>[] slots)
		{
			super(machine, slots);
		}

		public void setAdditionalSlots(T machine, int numAdditionalSlots, EnumFace... faces)
		{
			this.faces = Arrays.asList(faces);
			this.additionalSlots = new IItemSlot[numAdditionalSlots];
			for (int i = 0; i < numAdditionalSlots; ++i)
			{
				final int j = i;
				additionalSlots[i] = new ItemSlot<>(machine, "slot_" + i, i + slots.length, 64, 64, stack -> machine.getAssembly().isPartialInputValid(machine.world, machine.ITEM_INDEX_BLUEPRINT_INPUT.getStack(), machine.getTier(), machine.getInputsWithTest(j, stack)), 64, stack -> true);
			}
		}

		@Override
		public int getSlots()
		{
			return super.getSlots() + additionalSlots.length;
		}

		@Override
		public boolean isEmpty()
		{
			for (IItemSlot slot : additionalSlots) if (!slot.isEmpty()) return false;
			return super.isEmpty();
		}

		@Override
		public void clear()
		{
			for (IItemSlot slot : additionalSlots) slot.clear();
			super.clear();
		}

		public void writeAdditionalToDisk(NBTTagCompound tag)
		{
			NBTTagCompound storage = tag.getCompoundTag("itemStorage");
			for (IItemSlot slot : additionalSlots) slot.writeToDisk(storage);
			tag.setTag("itemStorage", storage);
		}

		public void readAdditionalFromDisk(NBTTagCompound tag)
		{
			NBTTagCompound storage = tag.getCompoundTag("itemStorage");
			for (IItemSlot slot : additionalSlots) slot.readFromDisk(storage);
			tag.setTag("itemStorage", storage);
		}

		public void writeAdditionalToShared(NBTTagCompound tag)
		{
			NBTTagCompound storage = tag.getCompoundTag("itemStorage");
			for (IItemSlot slot : additionalSlots) slot.writeToShared(storage);
			tag.setTag("itemStorage", storage);
		}

		public void readAdditionalFromShared(NBTTagCompound tag)
		{
			NBTTagCompound storage = tag.getCompoundTag("itemStorage");
			for (IItemSlot slot : additionalSlots) slot.readFromShared(storage);
			tag.setTag("itemStorage", storage);
		}

		@Override
		public IItemSlot getItemSlot(int slot)
		{
			if (slot >= slots.length && slot < slots.length + additionalSlots.length) return additionalSlots[slot - slots.length];
			else return super.getItemSlot(slot);
		}

		@Override
		public IItemSlot getItemSlot(int index, EnumFace face)
		{
			if (face == null)
			{
				if (index >= slots.length && index < slots.length + additionalSlots.length) return additionalSlots[index - slots.length];
				else return super.getItemSlot(index, face);
			}
			if (faces.contains(face))
			{
				int[] slots = sidedSlots[face.ordinal()];
				if (index >= slots.length && index < slots.length + additionalSlots.length) return additionalSlots[index - slots.length];
				else return super.getItemSlot(index, face);
			}
			else return super.getItemSlot(index, face);
		}

		@Override
		public int[] getSlots(EnumFace face)
		{
			if (face == null)
			{
				int[] slots = this.slotIds;
				int[] newSlots = new int[slots.length + additionalSlots.length];
				System.arraycopy(slots, 0, newSlots, 0, slots.length);
				int ind = slots.length;
				for (int i = 0; i < newSlots.length; ++i) newSlots[ind++] = ind;
				return newSlots;
			}
			else if (faces.contains(face))
			{
				int[] slots = super.getSlots(face);
				int[] newSlots = new int[slots.length + additionalSlots.length];
				System.arraycopy(slots, 0, newSlots, 0, slots.length);
				int ind = slots.length;
				for (int i = 0; i < newSlots.length; ++i) newSlots[ind++] = ind;
				return newSlots;
			}
			else return super.getSlots(face);
		}

	}

    public static final ResourceLocation ASSEMBLY_TERMINAL_GUI_TEXTURES = new ResourceLocation(MechanimationAPI.MOD_ID, "textures/gui/container/assembly_terminal.png");
	public static final int GUI_OFFSET_X = 4, GUI_OFFSET_Y = 15, SLOT_OFFSET_X = GUI_OFFSET_X + 19, SLOT_OFFSET_Y = GUI_OFFSET_Y;

	//TODO only auto-craft if a recipe has been selected?
    public static final String
	ENERGY_INDEX_ENERGY = "energy",
	ITEM_INDEX_BLUEPRINT = "blueprint",
	ITEM_INDEX_OUTPUT = "output";
	@SuppressWarnings("unchecked")
	public static final ItemInventoryTemplate<? extends TileEntityAssemblyTerminalBase<?>> ITEM_TEMPLATE = ((ItemInventoryTemplateBuilder<? extends TileEntityAssemblyTerminalBase<?>>) new ItemInventoryTemplateBuilder<>())
    		.addSlot(ENERGY_INDEX_ENERGY, machine -> 64, machine -> 64, CommonItemPredicates.providesEnergy(), machine -> 64, CommonItemPredicates.doesNotProvideEnergy(), machine -> EnumFace.values())
    		.addSlot(ITEM_INDEX_BLUEPRINT, machine -> 64, machine -> 64, machine -> stack -> AssemblyRecipes.isCraftable(stack, machine.getTier()), machine -> 64, CommonItemPredicates.accept(), machine -> new EnumFace[] {EnumFace.TOP})
    		.addSlot(ITEM_INDEX_OUTPUT, machine -> 64, machine -> 0, CommonItemPredicates.deny(), machine -> 64, CommonItemPredicates.accept(), machine -> new EnumFace[] {EnumFace.BOTTOM})
    		.build();
    @SuppressWarnings("unchecked")
	public static final EnergyInventoryTemplate<? extends TileEntityAssemblyTerminalBase<?>> ENERGY_TEMPLATE = ((EnergyInventoryTemplateBuilder<? extends TileEntityAssemblyTerminalBase<?>>) new EnergyInventoryTemplateBuilder<>())
    		.addSlot(ENERGY_INDEX_ENERGY, machine -> machine.getMaxEnergy(), machine -> machine.getMaxEnergyTransfer(), CommonEnergyPredicates.accept(), machine -> 0, CommonEnergyPredicates.deny(), machine -> EnumFace.values())
    		.build();
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static final OperationsTemplate<TileEntityAssemblyTerminalBase> OPERATIONS_TEMPLATE = new OperationsTemplate<TileEntityAssemblyTerminalBase>()
    		.addOperation(CommonOperations.<TileEntityAssemblyTerminalBase>energyFromItem(machine -> machine.ITEM_INDEX_ENERGY_INPUT, machine -> machine.ENERGY_INDEX_ENERGY_INPUT, TileEntityAssemblyTerminalBase::getMaxEnergyTransferItem));

	private IAssemblyBlueprint cachedBlueprint = null;
	private IAssemblyRecipe selectedRecipe = null; //TODO allow selection of recipe
	private IAssemblyRecipe cachedRecipe = null;
	public final IItemSlot
	ITEM_INDEX_ENERGY_INPUT,
	ITEM_INDEX_BLUEPRINT_INPUT,
	ITEM_INDEX_ITEM_OUTPUT;
	public final IEnergySlot
	ENERGY_INDEX_ENERGY_INPUT;
	@SideOnly(Side.CLIENT)
	public RenderInfo
	renderDimsEnergyInput;
    protected int progress = 0;
    public final AssemblyTableInventory<T> assemblyInventory;
    private boolean needsMultipartUpdate = true;
    public boolean doCrafting = false;

	@SuppressWarnings("unchecked")
	public TileEntityAssemblyTerminalBase()
	{
    	super(machine -> ((ItemInventoryTemplate<T>) ITEM_TEMPLATE).build(slots -> new AssemblyTableInventory<>(machine, slots)), ((EnergyInventoryTemplate<T>) ENERGY_TEMPLATE)::build);
    	this.assemblyInventory = (AssemblyTableInventory<T>) this.itemInventory;
    	ITEM_INDEX_ENERGY_INPUT = itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(ENERGY_INDEX_ENERGY));
    	ITEM_INDEX_BLUEPRINT_INPUT = itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(ITEM_INDEX_BLUEPRINT));
    	ITEM_INDEX_ITEM_OUTPUT = itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(ITEM_INDEX_OUTPUT));
    	ENERGY_INDEX_ENERGY_INPUT = energyInventory.getEnergySlot(ENERGY_TEMPLATE.getIndex(ENERGY_INDEX_ENERGY));
    	this.operations = OPERATIONS_TEMPLATE.build(this);
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
		{
			renderDimsEnergyInput = new RenderInfo(GUI_OFFSET_X + 1, GUI_OFFSET_Y + 73, IGuiElements.ENERGY_BAR_PROGRESS);
		}
	}

	public abstract int getMaxEnergy();

	public abstract int getMaxEnergyTransfer();

	public abstract int getMaxEnergyTransferItem();

	public IAssemblyBlueprint getCachedBlueprint()
	{
		return cachedBlueprint;
	}

	public abstract int getTier();

    public abstract int getMaxEnergyPerTick();

	@Override
	public void onInventorySlotChanged(int index, ItemStack stack)
	{
		super.onInventorySlotChanged(index, stack);
		if (getWorld() != null)
		{
			if (index == ITEM_INDEX_BLUEPRINT_INPUT.getSlot())
			{
				World world = getWorld();
				BlockPos pos = getPos();
				double x = pos.getX();
				double y = pos.getY();
				double z = pos.getZ();
				onBlueprintUpdate(stack, world.isRemote ? toDrop -> {} : toDrop -> InventoryHelper.spawnItemStack(world, x, y, z, toDrop));
			}
	    	if (FMLCommonHandler.instance().getEffectiveSide().isClient()) updateGuiRecipeSelectors();
		}
	}

	@SideOnly(Side.CLIENT)
	public void updateGuiRecipeSelectors()
	{
		if (isGuiInit)
		{
        	prev_recipe.enabled = next_recipe.enabled = this.cachedBlueprint != null && !cachedBlueprint.getValidResults(Minecraft.getMinecraft().world, ITEM_INDEX_BLUEPRINT_INPUT.getStack(), getTier(), this.getInputs()).isEmpty();
		}
	}

	public void onBlueprintUpdate(ItemStack stack, Consumer<ItemStack> drop)
	{
		ResourceLocation oldBlueprint = cachedBlueprint == null ? null : cachedBlueprint.getUniqueName();
		cachedBlueprint = AssemblyRecipes.getBlueprint(stack, getTier());
		ResourceLocation newBlueprint = cachedBlueprint == null ? null : cachedBlueprint.getUniqueName();
		if (!Objects.equal(oldBlueprint, newBlueprint))
		{
			setCachedRecipe(null);
			setSelectedRecipe(null);
			validateBlueprint(stack, drop);
		}
		this.markDirty();
	}

	public ItemStack[] getInputsWithTest(int index, ItemStack toTest)
	{
		ItemStack[] inputs = new ItemStack[assemblyInventory.additionalSlots.length];
		for (int i = 0; i < inputs.length; i++) inputs[i] = i == index ? toTest : assemblyInventory.additionalSlots[i].getStack();
		return inputs;
	}

	public ItemStack[] getInputs()
	{
		return getInputsWithTest(-1, null);
	}

	public void setInputs(ItemStack... inputs)
	{
		for (int i = 0; i < assemblyInventory.additionalSlots.length; i++) assemblyInventory.additionalSlots[i].setStack(inputs[i]);
	}

	@Nonnull
	public IAssemblyPredicate getAssembly()
	{
		return selectedRecipe == null ? cachedBlueprint == null ? (world, blueprint, tier, existingInputs) -> false : cachedBlueprint : selectedRecipe;
	}

	@SuppressWarnings("unchecked")
	public void validateBlueprint(ItemStack blueprint, Consumer<ItemStack> dropFunc)
	{
		ItemStack[] previouslyHeld = new ItemStack[assemblyInventory.additionalSlots.length];
		for (int i = 0; i < previouslyHeld.length; i++) previouslyHeld[i] = assemblyInventory.additionalSlots[i].getStack();
		if (cachedBlueprint != null)
		{
			int lastNumInputs = cachedBlueprint.getMaxUsedInputs();
			assemblyInventory.setAdditionalSlots((T) this, cachedBlueprint.getMaxUsedInputs(), EnumFace.SIDES);
			ItemStack[] newlyHeld = new ItemStack[lastNumInputs];
			Arrays.fill(newlyHeld, ItemStack.EMPTY);
			for (int i = 0; i < lastNumInputs; i++) if (i < previouslyHeld.length && getAssembly().isPartialInputValid(world, blueprint, getTier(), newlyHeld))
			{
				newlyHeld[i] = previouslyHeld[i];
				previouslyHeld[i] = ItemStack.EMPTY;
				assemblyInventory.additionalSlots[i].setStack(newlyHeld[i]);
			}
		}
		else assemblyInventory.setAdditionalSlots((T) this, 0, EnumFace.NONE);
		for (ItemStack stack : previouslyHeld) if (!stack.isEmpty()) dropFunc.accept(stack);
		if (this.hasWorld()) updateMultipart();
		else needsMultipartUpdate = true;
		//TODO revalidate viewers
	}

	public void validateInputs(Consumer<ItemStack> dropFunc)
	{
		if (cachedBlueprint != null)
		{
			for (IItemSlot slot : assemblyInventory.additionalSlots) {
				ItemStack has = slot.getStack();
				if (!has.isEmpty() && !slot.canInsert(has, null))
				{
					slot.setStack(ItemStack.EMPTY);
					dropFunc.accept(has);
				}
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public RenderInfo getEnergyRenderInfo(int index)
	{
		return renderDimsEnergyInput;
	}

    @Override
	public int getField(int id)
    {
        switch (id)
        {
            case 0:
                return this.progress;
            case 1:
            	IAssemblyRecipe recipe = this.getValidRecipe();
            	return recipe == null ? 1 : recipe.requiredFlux(getWorld(), ITEM_INDEX_BLUEPRINT_INPUT.getStack(), getTier(), getInputs());
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
	public ResourceLocation getGuiTexture()
	{
		return ASSEMBLY_TERMINAL_GUI_TEXTURES;
	}

	@Override
	public String getGuiID()
	{
		return "mechanimation:assembly_terminal";
	}

	@Override
	public String getDefaultName()
	{
		return "container.mechanimation.assembly_terminal";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderProgressMeters(GuiContainer gui, int offX, int offY, float zLevel)
	{
		IGuiElements.WORK_BAR_PROGRESS.render(offX + GUI_OFFSET_X + 1, offY + GUI_OFFSET_Y + 27, zLevel, this.getProgress());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String[] getRecipeTypes(int adjX, int adjY)
	{
		if (ConfigJEI.INSTANCE.assemblyTerminalRecipes.val && adjX >= GUI_OFFSET_X + 0 && adjX < GUI_OFFSET_X + 18 && adjY >= GUI_OFFSET_Y + 20 && adjY < GUI_OFFSET_Y + 68) return new String[] {RecipeCategoryAssembly.ID};
		else return new String[0];
	}

	@Override
    @SideOnly(Side.CLIENT)
    public void onGuiDrawnBackground(GuiMachine<T> gui, int startX, int startY, int adjMouseX, int adjMouseY, float partialTicks)
    {
		if (cachedBlueprint != null)
		{
			ItemStack blueprint = ITEM_INDEX_BLUEPRINT_INPUT.getStack();
			gui.mc.getTextureManager().bindTexture(cachedBlueprint.blueprintImage(blueprint, getTier()));
			Rectangle bounds = cachedBlueprint.getBlueprintImageBounds(blueprint, getTier());
			gui.drawTexturedModalRect(startX + SLOT_OFFSET_X + (AssemblyRecipes.MAX_WIDTH - bounds.w) / 2, startY + SLOT_OFFSET_Y + (AssemblyRecipes.MAX_HEIGHT - bounds.h) / 2, bounds.x1, bounds.y1, bounds.w, bounds.h);
		}
    }

	@Override
	public void setPreview(Map<Integer, List<ItemStack>> previewMap)
	{
		updatePreview(previewMap);
	}

	@Override
	public void updatePreview(Map<Integer, List<ItemStack>> previewMap) //TODO improve
	{
		previewMap.clear();
		ItemStack blueprint = ITEM_INDEX_BLUEPRINT_INPUT.getStack();
		ItemStack output = null;
		if (this.selectedRecipe != null)
		{
			output = selectedRecipe.process(getWorld(), blueprint, getTier(), true, this.getInputs());
			if (output == null || output.isEmpty()) output = selectedRecipe.getDefaultOutput();
			ItemStack[][] inputs = this.selectedRecipe.getDefaultInput(blueprint, getTier());
			for (int i = 0; i < inputs.length; i++) previewMap.put(i + 3, Arrays.asList(inputs[i]));
		}
		else if (this.cachedRecipe != null)
		{
			output = cachedRecipe.process(getWorld(), blueprint, getTier(), true, this.getInputs());
			if (output == null || output.isEmpty()) output = cachedRecipe.getDefaultOutput();
		}
		if (output != null && !output.isEmpty()) previewMap.put(2, Collections.singletonList(output));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addItemSlotsToContainer(ContainerMachine<T> container)
	{
		Slot output;
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_ENERGY_INPUT.getSlot(), GUI_OFFSET_X + 1, GUI_OFFSET_Y + 117));
		container.addSlotToContainer(output = new SlotMachine<>((T) this, ITEM_INDEX_ITEM_OUTPUT.getSlot(), GUI_OFFSET_X, GUI_OFFSET_Y + 42));
		container.addSlotToContainer(new SlotAssemblyBlueprint<>((T) this, container, output, ITEM_INDEX_BLUEPRINT_INPUT.getSlot(), GUI_OFFSET_X + 1, GUI_OFFSET_Y + 1));
		if (this.cachedBlueprint != null)
		{
			ItemStack blueprint = ITEM_INDEX_BLUEPRINT_INPUT.getStack();
			Rectangle bounds = cachedBlueprint.getBlueprintImageBounds(blueprint, getTier());
			int offX = SLOT_OFFSET_X + (AssemblyRecipes.MAX_WIDTH - bounds.w) / 2;
			int offY = SLOT_OFFSET_Y + (AssemblyRecipes.MAX_HEIGHT - bounds.h) / 2;
			Vec2i[] slots = this.cachedBlueprint.getInput(blueprint, getTier());
			for (int i = 0; i < slots.length; ++i) //add new slots
			{
				container.addSlotToContainer(new SlotMachine<>((T) this, assemblyInventory.additionalSlots[i].getSlot(), slots[i].x + offX, slots[i].y + offY));
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void updateItemSlotsInContainer(ContainerMachine<T> container, Slot outputSlot) //called when blueprint item is changed in GUI
	{
		int index = container.index_max_hotbar + 3; //we don't need to update slots for blueprint, output, and energy
		int containerLength = container.index_max_machine - index;
		if (this.cachedBlueprint != null) //update
		{
			ItemStack blueprint = ITEM_INDEX_BLUEPRINT_INPUT.getStack();
			Rectangle bounds = cachedBlueprint.getBlueprintImageBounds(blueprint, getTier());
			int offX = SLOT_OFFSET_X + (AssemblyRecipes.MAX_WIDTH - bounds.w) / 2;
			int offY = SLOT_OFFSET_Y + (AssemblyRecipes.MAX_HEIGHT - bounds.h) / 2;
			Vec2i[] slots = this.cachedBlueprint.getInput(blueprint, getTier());
			int i;
			for (i = 0; i < containerLength; i++) //update or remove existing slots
			{
				if (i < slots.length) //update slot
				{
					Slot slot = container.getSlot(index);
					slot.xPos = slots[i].x + offX;
					slot.yPos = slots[i].y + offY;
					index++;
				}
				else //remove slot
				{
					container.inventorySlots.remove(index);
					container.inventoryItemStacks.remove(index); //TODO drop item?
				}
			}
			for (i = 0; i < slots.length; ++i) //add new slots
			{
				container.addSlotToContainer(new SlotMachine<>((T) this, assemblyInventory.additionalSlots[i].getSlot(), slots[i].x + offX, slots[i].y + offY));
			}
			Vec2i outputLoc = cachedBlueprint.getOutputPosition(blueprint, getTier());
			outputSlot.xPos = offX + outputLoc.x;
			outputSlot.yPos = offY + outputLoc.y;
		}
		else //remove all inputs
		{
			outputSlot.xPos = SLOT_OFFSET_X + 1;
			outputSlot.yPos = SLOT_OFFSET_Y + 1;
			for (int i = 0; i < containerLength; i++) //remove slot
			{
				container.inventorySlots.remove(index);
				container.inventoryItemStacks.remove(index); //TODO drop item?
			}
		}
		container.index_max_machine = container.inventorySlots.size();
	}

	@Override
	public IAssemblyRecipe getValidRecipe()
	{
		IAssemblyRecipe result;
		if (cachedBlueprint == null) result = null;
		else if (selectedRecipe != null) result = selectedRecipe.isInputValid(getWorld(), ITEM_INDEX_BLUEPRINT_INPUT.getStack(), getTier(), getInputs()) ? selectedRecipe : null;
		else result = cachedBlueprint.getResult(getWorld(), ITEM_INDEX_BLUEPRINT_INPUT.getStack(), getTier(), getInputs());
		if (result == null)
		{
			if (cachedRecipe != null)
			{
				progress = 0;
				setCachedRecipe(null);
			}
		}
		else if (cachedRecipe == null || !result.getUniqueName().equals(cachedRecipe.getUniqueName()))
		{
			progress = 0;
			setCachedRecipe(result);
		}
		return result;
	}

	@Override
	public void onUsageTick(IAssemblyRecipe recipe)
	{
		if (!doCrafting) return;
		int reqFlux = recipe.requiredFlux(getWorld(), ITEM_INDEX_BLUEPRINT_INPUT.getStack(), getTier(), getInputs());
		if (reqFlux > 0)
		{
			int toPull = Math.min(Math.min(this.getMaxEnergyPerTick(), reqFlux - progress), ENERGY_INDEX_ENERGY_INPUT.getEnergyStored());
			if (toPull > 0)
			{
				this.progress += toPull;
				ENERGY_INDEX_ENERGY_INPUT.remove(toPull);
			}
		}
	}

	@Override
	public boolean canProcessRecipeThisTick(IAssemblyRecipe recipe)
	{
		if (!doCrafting) return false;
		int reqFlux = recipe.requiredFlux(getWorld(), ITEM_INDEX_BLUEPRINT_INPUT.getStack(), getTier(), getInputs());
		if (reqFlux <= progress)
		{
			ItemStack output = recipe.process(getWorld(), ITEM_INDEX_BLUEPRINT_INPUT.getStack(), getTier(), true, this.getInputs());
			if (output.isEmpty()) return false;
			else
			{
				ItemStack curOut = ITEM_INDEX_ITEM_OUTPUT.getStack();
				return (curOut.getCount() + output.getCount()) <= Math.min(ITEM_INDEX_ITEM_OUTPUT.getStackLimit(), output.getMaxStackSize()) && (curOut.isEmpty() || (ItemStack.areItemsEqual(output, curOut) && ItemStack.areItemStackTagsEqual(output, curOut)));
			}
		}
		else return false;
	}

	public void setDoCrafting(boolean doCrafting)
	{
		this.doCrafting = doCrafting;
		if (world != null && world.isRemote && isGuiInit)
		{
			startCrafting.displayString = doCrafting ? "stop" : "start";
			if (doCrafting) prev_recipe.enabled = next_recipe.enabled = false;
			else prev_recipe.enabled = next_recipe.enabled = true;
		}
		this.setNeedsUpdate();
	}

	public void setCachedRecipe(IAssemblyRecipe cachedRecipe)
	{
		this.cachedRecipe = cachedRecipe;
		if (world != null && world.isRemote && isGuiInit)
		{
			if (cachedRecipe == null && selectedRecipe == null) startCrafting.enabled = false;
			else startCrafting.enabled = true;
		}
	}

	public void setSelectedRecipe(IAssemblyRecipe selectedRecipe)
	{
		this.selectedRecipe = selectedRecipe;
		if (selectedRecipe == null && doCrafting) setDoCrafting(false);
		if (world != null && world.isRemote && startCrafting != null)
		{
			if (cachedRecipe == null && selectedRecipe == null) startCrafting.enabled = false;
			else startCrafting.enabled = true;
		}
	}

	@Override
	public void processRecipe(IAssemblyRecipe recipe)
	{
		ItemStack[] inputs = this.getInputs();
		ITEM_INDEX_ITEM_OUTPUT.add(recipe.process(getWorld(), ITEM_INDEX_BLUEPRINT_INPUT.getStack(), getTier(), false, inputs));
		this.setInputs(inputs);
		progress = 0;
		if (selectedRecipe == null)
		{
			setDoCrafting(false);
			setCachedRecipe(null);
		}
	}

    @Override
	public void writeToShared(NBTTagCompound tag)
	{
    	//if (FMLCommonHandler.instance().getEffectiveSide().isServer()) Thread.dumpStack();
    	tag.setInteger("progress", progress);
    	tag.setBoolean("doCrafting", doCrafting);
    	tag.setInteger("numInputs", assemblyInventory.additionalSlots.length);
    	super.writeToShared(tag);
    	if (selectedRecipe != null) tag.setString("selectedRecipe", selectedRecipe.getUniqueName().toString());
    	assemblyInventory.writeAdditionalToShared(tag);
	}

    @SuppressWarnings("unchecked")
	@Override
	public void readFromShared(NBTTagCompound tag)
	{
    	//if (!FMLCommonHandler.instance().getEffectiveSide().isServer()) Thread.dumpStack();
    	boolean isCrafting = this.doCrafting;
    	IAssemblyRecipe cachedRecipe = this.cachedRecipe;
		this.assemblyInventory.setAdditionalSlots((T) this, 0, EnumFace.NONE); //remove added slots
		this.assemblyInventory.setAdditionalSlots((T) this, tag.getInteger("numInputs"), EnumFace.SIDES); //add old slots
        super.readFromShared(tag); //read after updating inventory size
		this.setDoCrafting(tag.getBoolean("doCrafting"));
        ItemStack blueprint = ITEM_INDEX_BLUEPRINT_INPUT.getStack();
		cachedBlueprint = AssemblyRecipes.getBlueprint(blueprint, getTier());
		setSelectedRecipe(null);
		setCachedRecipe(null);
		validateBlueprint(blueprint, toDrop -> {}); //TODO what to do with items
    	if (cachedBlueprint != null && tag.hasKey("selectedRecipe", 8))
    	{
    		setSelectedRecipe(cachedBlueprint.getRecipe(new ResourceLocation(tag.getString("selectedRecipe"))));
    	}
    	this.setDoCrafting(isCrafting);
		this.cachedRecipe = cachedRecipe;
		progress = tag.getInteger("progress");
		//this.assemblyInventory.setAdditionalSlots((T) this, lastNumInputs, EnumFace.SIDES); //remove added slots
    	assemblyInventory.readAdditionalFromShared(tag);
    	if (FMLCommonHandler.instance().getEffectiveSide().isClient())
    	{
    		updateGuiRecipeSelectors();
    	}
    	else this.setNeedsUpdate(); //make the client receive this
	}

    @Override
	public void writeToDisk(NBTTagCompound tag)
	{
    	super.writeToDisk(tag);
    	assemblyInventory.writeAdditionalToDisk(tag);
	}

	@Override
	public void readFromDisk(NBTTagCompound tag)
	{
    	super.readFromDisk(tag);
    	assemblyInventory.readAdditionalFromDisk(tag);
	}

    @Override
	public int inventorySizeX()
	{
		return 188;
	}

    @Override
	public int inventorySizeY()
	{
		return 244;
	}

	@SideOnly(Side.CLIENT)
    private boolean isGuiInit;
	@SideOnly(Side.CLIENT)
    private Button prev_recipe, next_recipe, startCrafting;

    @Override
	@SideOnly(Side.CLIENT)
    public void onGuiInit(GuiMachine<T> gui, int startX, int startY)
	{
    	final int offX = 4, sizeX = 28, offY = 4, sizeY = 10;
    	final int x1 = startX + offX, x2 = x1 + sizeX, x4 = startX + inventorySizeX() - offX, x3 = x4 - sizeX;
    	final int y1 = startY + offY, y2 = y1 + sizeY;
    	if (isGuiInit)
    	{
        	gui.removeElement(prev_recipe);
        	gui.removeElement(next_recipe);
        	gui.removeElement(startCrafting);
        	prev_recipe.setSize(x1, y1, x2, y2);
        	next_recipe.setSize(x3, y1, x4, y2);
        	startCrafting.setSize(startX + inventorySizeX() - 38, startY + 148, startX + inventorySizeX() - 4, startY + 160);
    	}
    	else
    	{
    		//TODO can we set this from JEI somehow?
    		prev_recipe = new Button(x1, y1, sizeX, sizeY, "<--", () -> {
    			if (cachedBlueprint != null)
    			{
    				List<IAssemblyRecipe> validRecipes = cachedBlueprint.getValidResults(Minecraft.getMinecraft().world, ITEM_INDEX_BLUEPRINT_INPUT.getStack(), getTier(), this.getInputs());
    				if (selectedRecipe == null)
    				{
    					if (!validRecipes.isEmpty()) setSelectedRecipe(validRecipes.get(validRecipes.size() - 1));
    				}
    				else
    				{
    					int index = validRecipes.indexOf(selectedRecipe);
    					if (index < 1) setSelectedRecipe(null);
    					else setSelectedRecipe(validRecipes.get(index - 1));
    				}
    				byte id;
    				ByteBuf buf = Unpooled.buffer();
    				if (selectedRecipe == null) id = 0;
    				else
    				{
    					id = 1;
    					ByteBufUtils.writeUTF8String(buf, selectedRecipe.getUniqueName().toString());
    				}
    				Main.network().sendToServer(new MachineGUIPacket(id, buf));
    			}
    		});
    		next_recipe = new Button(x3, y1, sizeX, sizeY, "-->", () -> {
    			if (cachedBlueprint != null)
    			{
    				List<IAssemblyRecipe> validRecipes = cachedBlueprint.getValidResults(Minecraft.getMinecraft().world, ITEM_INDEX_BLUEPRINT_INPUT.getStack(), getTier(), this.getInputs());
    				if (selectedRecipe == null)
    				{
    					if (!validRecipes.isEmpty()) setSelectedRecipe(validRecipes.get(0));
    				}
    				else
    				{
    					int index = validRecipes.indexOf(selectedRecipe);
    					if (index < 0 || index >= validRecipes.size() - 1) setSelectedRecipe(null);
    					else setSelectedRecipe(validRecipes.get(index + 1));
    				}
    				byte id;
    				ByteBuf buf = Unpooled.buffer();
    				if (selectedRecipe == null) id = 0;
    				else
    				{
    					id = 1;
    					ByteBufUtils.writeUTF8String(buf, selectedRecipe.getUniqueName().toString());
    				}
    				Main.network().sendToServer(new MachineGUIPacket(id, buf));
    			}
    		});
    		startCrafting = new Button(startX + inventorySizeX() - 38, startY + 148, 34, 12, "craft", () -> {
				Main.network().sendToServer(new MachineGUIPacket(doCrafting ? (byte) 2 : (byte) 3, Unpooled.buffer()));
    			setDoCrafting(!doCrafting);
    		});
    		prev_recipe.enabled = next_recipe.enabled = this.cachedBlueprint != null && !cachedBlueprint.getValidResults(Minecraft.getMinecraft().world, ITEM_INDEX_BLUEPRINT_INPUT.getStack(), getTier(), this.getInputs()).isEmpty();
			if (cachedRecipe == null && selectedRecipe == null) startCrafting.enabled = false;
			else startCrafting.enabled = true;
    		isGuiInit = true;
    	}
    	gui.addElement(prev_recipe);
    	gui.addElement(next_recipe);
    	gui.addElement(startCrafting);
	}

    @Override
	public void onGuiPacket(byte id, ByteBuf data)
	{
    	switch (id)
    	{
    	case 0:
    		if (cachedBlueprint != null)
    		{
    			setSelectedRecipe(null);
        		this.setNeedsUpdate();
    		}
    		break;
    	case 1:
    		if (cachedBlueprint != null)
    		{
        		ResourceLocation recipeName = new ResourceLocation(ByteBufUtils.readUTF8String(data));
        		setSelectedRecipe(cachedBlueprint.getResult(ITEM_INDEX_BLUEPRINT_INPUT.getStack(), getTier(), recipeName));
    			World world = getWorld();
    			BlockPos pos = getPos();
    			double x = pos.getX();
    			double y = pos.getY();
    			double z = pos.getZ();
        		this.validateInputs(stack -> InventoryHelper.spawnItemStack(world, x, y, z, stack));
        		this.setNeedsUpdate();
    		}
    		break;
    	case 2:
    		this.setDoCrafting(false);
    		break;
    	case 3:
    		this.setDoCrafting(true);
    		break;
    	}
	}

	@Override
	public void update()
	{
		if (this.hasWorld())
		{
			if (needsMultipartUpdate) updateMultipart();
		}
		super.update();
	}

	@SuppressWarnings("unchecked")
	public void updateMultipart()
	{
		EnumFacing back = this.getOrientation().getFacing(EnumFace.BACK);
		EnumFacing left = this.getOrientation().getFacing(EnumFace.LEFT);
		BlockPos backPos = getPos().offset(back);
		TileEntity backTile = getWorld().getTileEntity(backPos);
		if (backTile instanceof TileEntityAssemblyTerminalPart) ((TileEntityAssemblyTerminalPart<T>) backTile).onViewChanged();
		BlockPos leftPos = getPos().offset(left);
		TileEntity leftTile = getWorld().getTileEntity(leftPos);
		if (leftTile instanceof TileEntityAssemblyTerminalPart) ((TileEntityAssemblyTerminalPart<T>) leftTile).onViewChanged();
		BlockPos backLeftPos = backPos.offset(left);
		TileEntity backLeftTile = getWorld().getTileEntity(backLeftPos);
		if (backLeftTile instanceof TileEntityAssemblyTerminalPart) ((TileEntityAssemblyTerminalPart<T>) backLeftTile).onViewChanged();
		needsMultipartUpdate = false;
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