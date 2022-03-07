package firemerald.mechanimation.tileentity.machine.arc_furnace;

import firemerald.api.mcms.model.IModel;
import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.api.crafting.arc_furnace.ArcFurnaceRecipes;
import firemerald.mechanimation.client.gui.inventory.IGuiElements;
import firemerald.mechanimation.init.MechanimationStats;
import firemerald.mechanimation.inventory.container.ContainerMachine;
import firemerald.mechanimation.inventory.slot.SlotMachine;
import firemerald.mechanimation.mcms.MCMSModel;
import firemerald.mechanimation.mcms.MCMSTexture;
import firemerald.mechanimation.tileentity.machine.base.IVerticalMachine;
import firemerald.mechanimation.tileentity.machine.base.fluids.CommonFluidPredicates;
import firemerald.mechanimation.tileentity.machine.base.fluids.FluidInventoryTemplate;
import firemerald.mechanimation.tileentity.machine.base.fluids.FluidInventoryTemplateBuilder;
import firemerald.mechanimation.tileentity.machine.base.items.CommonItemPredicates;
import firemerald.mechanimation.tileentity.machine.base.items.IItemSlot;
import firemerald.mechanimation.tileentity.machine.base.items.ItemInventoryTemplate;
import firemerald.mechanimation.tileentity.machine.base.items.ItemInventoryTemplateBuilder;
import firemerald.mechanimation.tileentity.machine.base.operations.CommonOperations;
import firemerald.mechanimation.tileentity.machine.base.operations.OperationsTemplate;
import firemerald.mechanimation.util.EnumFace;
import firemerald.mechanimation.util.EnumVerticalOrientation;
import firemerald.mechanimation.util.Utils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatBase;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityBlastFurnace extends TileEntitySteelingFurnaceBase<TileEntityBlastFurnace> implements IVerticalMachine
{
    public static final ResourceLocation BLAST_FURNACE_GUI_TEXTURES = new ResourceLocation(MechanimationAPI.MOD_ID, "textures/gui/container/blast_furnace.png");
	public static final String
	ITEM_INDEX_FUEL = "fuel";
	public static final ItemInventoryTemplate<TileEntityBlastFurnace> ITEM_TEMPLATE = new ItemInventoryTemplateBuilder<TileEntityBlastFurnace>()
    		.addSlot(ITEM_INDEX_INPUT, machine -> 64, machine -> 64, machine -> ArcFurnaceRecipes::isCraftable, machine -> 64, CommonItemPredicates.accept(), machine -> EnumFace.NONE)
    		.addSlot(FLUID_INDEX_OUTPUT, machine -> 64, machine -> 64, CommonItemPredicates.receivesFluidFrom(machine -> machine.FLUID_INDEX_FLUID_OUTPUT), machine -> 64, CommonItemPredicates.doesNotReceiveFluidFrom(machine -> machine.FLUID_INDEX_FLUID_OUTPUT), machine -> new EnumFace[] {EnumFace.BOTTOM})
    		.addSlot(ITEM_INDEX_FUEL, machine -> 64, machine -> 64, machine -> TileEntityFurnace::isItemFuel, machine -> 64, machine -> stack -> !TileEntityFurnace.isItemFuel(stack), machine -> EnumFace.SIDES)
    		.build();
	public static final FluidInventoryTemplate<TileEntityBlastFurnace> FLUID_TEMPLATE = new FluidInventoryTemplateBuilder<TileEntityBlastFurnace>()
    		.addSlot(FLUID_INDEX_OUTPUT, machine -> 2 * Fluid.BUCKET_VOLUME, machine -> 0, CommonFluidPredicates.deny(), machine -> 200, CommonFluidPredicates.accept(), machine -> EnumFace.SIDES)
    		.build();
	public static final OperationsTemplate<TileEntityBlastFurnace> OPERATION_TEMPLATE = new OperationsTemplate<TileEntityBlastFurnace>()
			.addOperation(CommonOperations.<TileEntityBlastFurnace>fluidToItem(machine -> machine.ITEM_INDEX_FLUID_OUTPUT, machine -> machine.FLUID_INDEX_FLUID_OUTPUT, machine -> 200));


	public final IItemSlot
	ITEM_INDEX_FUEL_INPUT;
	public int fuelLeft = 0;
	public int burnTime = 0;
	public EnumVerticalOrientation orientation = EnumVerticalOrientation.SOUTH;

	public TileEntityBlastFurnace()
	{
		super(ITEM_TEMPLATE, FLUID_TEMPLATE);
		ITEM_INDEX_FUEL_INPUT = this.itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(ITEM_INDEX_FUEL));
		this.operations = OPERATION_TEMPLATE.build(this);
	}

	@Override
	public float getFuelTemp()
	{
		return 1927;
	}

	@Override
	public int getFuelSurfaceArea() //1 block = 10 units
	{
		return 100;
	}

	@Override
	public int getAmbientSurfaceArea() //1 block = 10 units
	{
		return 75;
	}

	@Override
	public int getHeatConductorVolume()
	{
		return 500;
	}

	@Override
	public boolean isBurning()
	{
		return this.fuelLeft > 0;
	}

	@Override
	public void startBurning()
	{
		ItemStack stack = ITEM_INDEX_FUEL_INPUT.getStack();
		fuelLeft = burnTime = TileEntityFurnace.getItemBurnTime(stack) / 4; //burn faster
        Item item = stack.getItem();
        stack.shrink(1);
        if (stack.isEmpty()) ITEM_INDEX_FUEL_INPUT.setStack(item.getContainerItem(stack));
        else ITEM_INDEX_FUEL_INPUT.setStack(stack);
	}

	@Override
	public void consumeFuel()
	{
		fuelLeft--;
	}

    @Override
	public String getGuiID()
    {
        return "mechanimation:blast_furnace";
    }

    @Override
	public int getField(int id)
    {
        switch (id)
        {
        case 2:
        	return this.fuelLeft;
        case 3:
        	return burnTime > 0 ? burnTime : 1;
        default:
        	return super.getField(id);
        }
    }

    @Override
	public void setField(int id, int value)
    {
        switch (id)
        {
        case 2:
            this.fuelLeft = value;
            break;
        case 3:
            this.burnTime = value;
            break;
        default:
        	super.setField(id, value);
        }
    } //not used

    @Override
	public int getFieldCount()
    {
        return 4;
    }

	@Override
	public IModel<?, ?> getModel(float partial)
	{
		return MCMSModel.BLAST_FURNACE.model.get();
	}

	@Override
	public ResourceLocation getTexture()
	{
		return MCMSTexture.BLAST_FURNACE;
	}

	@Override
	public String getDefaultName()
	{
		return "container.mechanimation.blast_furnace";
	}

	@Override
	public void addItemSlotsToContainer(ContainerMachine<TileEntityBlastFurnace> container)
	{
		container.addSlotToContainer(new SlotMachine<>(this, ITEM_INDEX_FUEL_INPUT.getSlot(), 56, 53));
		container.addSlotToContainer(new SlotMachine<>(this, ITEM_INDEX_ITEM_INPUT.getSlot(), 56, 17));
		super.addItemSlotsToContainer(container);
	}

	@Override
    @SideOnly(Side.CLIENT)
	public ResourceLocation getGuiTexture()
	{
		return BLAST_FURNACE_GUI_TEXTURES;
	}

	@Override
    @SideOnly(Side.CLIENT)
	public void renderProgressMeters(GuiContainer gui, int offX, int offY, float zLevel)
	{
		super.renderProgressMeters(gui, offX, offY, zLevel);
		IGuiElements.PROGRESS_BURN_PROGRESS.render(offX + 57 , offY + 37, zLevel, getProgress(2));
	}

	@Override
	public StatBase getInteractionStat()
	{
		return MechanimationStats.BLAST_FURNACE_INTERACTION;
	}

    @Override
	public void readFromDisk(NBTTagCompound tag)
    {
        super.readFromDisk(tag);
        this.fuelLeft = tag.getInteger("fuelLeft");
        this.burnTime = tag.getInteger("burnTime");
    }

    @Override
	public void writeToDisk(NBTTagCompound tag)
    {
    	super.writeToDisk(tag);
    	tag.setInteger("fuelLeft", fuelLeft);
        tag.setInteger("burnTime", burnTime);
    }

	@Override
	public EnumVerticalOrientation getFront()
	{
		return orientation;
	}

	@Override
	public void setFront(EnumVerticalOrientation orientation)
	{
		this.orientation = orientation;
		setNeedsUpdate();
	}

	@Override
	public void writeToShared(NBTTagCompound tag)
	{
		tag.setString("front", orientation.name());
		super.writeToShared(tag);
	}

	@Override
	public void readFromShared(NBTTagCompound tag)
	{
        orientation = Utils.getEnum(tag.getString("front"), EnumVerticalOrientation.values(), EnumVerticalOrientation.SOUTH);
		super.readFromShared(tag);
	}

	@Override
    public AxisAlignedBB getChamberBox()
    {
		return new AxisAlignedBB(this.pos).expand(0, 1, 0);
    }
}