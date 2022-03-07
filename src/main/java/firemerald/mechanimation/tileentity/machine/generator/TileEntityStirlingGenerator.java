package firemerald.mechanimation.tileentity.machine.generator;

import firemerald.api.mcms.animation.IAnimation;
import firemerald.api.mcms.model.IModel;
import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.client.gui.inventory.IGuiElements;
import firemerald.mechanimation.compat.jei.ConfigJEI;
import firemerald.mechanimation.compat.jei.recipe.generator.stirling.RecipeCategoryStirlingGenerator;
import firemerald.mechanimation.init.MechanimationStats;
import firemerald.mechanimation.inventory.container.ContainerMachine;
import firemerald.mechanimation.inventory.slot.SlotMachine;
import firemerald.mechanimation.mcms.MCMSAnimation;
import firemerald.mechanimation.mcms.MCMSModel;
import firemerald.mechanimation.mcms.MCMSTexture;
import firemerald.mechanimation.tileentity.machine.base.energy.CommonEnergyPredicates;
import firemerald.mechanimation.tileentity.machine.base.energy.EnergyInventoryTemplate;
import firemerald.mechanimation.tileentity.machine.base.energy.EnergyInventoryTemplateBuilder;
import firemerald.mechanimation.tileentity.machine.base.items.IItemSlot;
import firemerald.mechanimation.tileentity.machine.base.items.ItemInventoryTemplate;
import firemerald.mechanimation.tileentity.machine.base.items.ItemInventoryTemplateBuilder;
import firemerald.mechanimation.tileentity.machine.base.operations.CommonOperations;
import firemerald.mechanimation.tileentity.machine.base.operations.OperationsTemplate;
import firemerald.mechanimation.util.EnumFace;
import firemerald.mechanimation.util.Utils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatBase;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityStirlingGenerator extends TileEntityGeneratorBase<TileEntityStirlingGenerator, TileEntityStirlingGenerator>
{
	public static final int FLUX_PER_BURN_TICK = 12; //TODO
	public static final int BURN_TICKS_PER_TICK = 5; //TODO
    public static final ResourceLocation STIRLING_GENERATOR_GUI_TEXTURES = new ResourceLocation(MechanimationAPI.MOD_ID, "textures/gui/container/stirling_generator.png");
	public static final String
	ITEM_INDEX_FUEL = "fuel";
	public static final ItemInventoryTemplate<TileEntityStirlingGenerator> ITEM_TEMPLATE = new ItemInventoryTemplateBuilder<TileEntityStirlingGenerator>()
    		.addSlot(ENERGY_INDEX_ENERGY, machine -> 64, machine -> 64, machine -> Utils::isEnergyReceiver, machine -> 64, machine -> stack -> !Utils.isEnergyReceiver(stack), machine -> EnumFace.values())
    		.addSlot(ITEM_INDEX_FUEL, machine -> 64, machine -> 64, machine -> TileEntityFurnace::isItemFuel, machine -> 64, machine -> stack -> !TileEntityFurnace.isItemFuel(stack), machine -> EnumFace.values())
    		.build();
	public static final EnergyInventoryTemplate<TileEntityStirlingGenerator> ENERGY_TEMPLATE = new EnergyInventoryTemplateBuilder<TileEntityStirlingGenerator>()
			.addSlot(ENERGY_INDEX_ENERGY, machine -> 5000, machine -> 0, CommonEnergyPredicates.deny(), machine -> 1000, CommonEnergyPredicates.accept(), machine -> EnumFace.values())
    		.build();
	public static final OperationsTemplate<TileEntityStirlingGenerator> OPERATION_TEMPLATE = new OperationsTemplate<TileEntityStirlingGenerator>()
			.addOperation(CommonOperations.<TileEntityStirlingGenerator>energyToItem(machine -> machine.ITEM_INDEX_ENERGY_OUTPUT, machine -> machine.ENERGY_INDEX_ENERGY_OUTPUT, machine -> 1000))
			.addOperation(CommonOperations.<TileEntityStirlingGenerator>provideEnergy(machine -> machine.ENERGY_INDEX_ENERGY_OUTPUT, machine -> 200, machine -> EnumFace.values()));
	//TODO provide energy


	public final IItemSlot
	ITEM_INDEX_FUEL_INPUT;

    public TileEntityStirlingGenerator()
    {
    	super(ITEM_TEMPLATE, ENERGY_TEMPLATE);
    	ITEM_INDEX_FUEL_INPUT = this.itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(ITEM_INDEX_FUEL));
    	this.operations = OPERATION_TEMPLATE.build(this);
    }

	@Override
	public int getMaxIterations()
	{
		return BURN_TICKS_PER_TICK;
	}

    @Override
	public int getSizeInventory()
    {
        return 2;
    }

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getGuiTexture()
	{
		return STIRLING_GENERATOR_GUI_TEXTURES;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderProgressMeters(GuiContainer gui, int offX, int offY, float zLevel)
	{
		IGuiElements.PROGRESS_BURN_PROGRESS.render(offX + 81 , offY + 37, zLevel, this.getProgress());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String[] getRecipeTypes(int adjX, int adjY)
	{
		if (ConfigJEI.INSTANCE.stirlingGeneratorRecipes.val && adjX >= 81 && adjX < 94 && adjY >= 37 && adjY < 50) return new String[] {RecipeCategoryStirlingGenerator.ID};
		else return null;
	}

	@Override
	public IModel<?, ?> getModel(float partial)
	{
		return MCMSModel.STIRLING_GENERATOR.model.get();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getTexture()
	{
		return MCMSTexture.STIRLING_GENERATOR;
	}

	@Override
	public String getGuiID()
	{
		return "mechanimation:stirling_generator";
	}

	@Override
	public IAnimation getRunningAnimation()
	{
		return MCMSAnimation.STIRLING_GENERATOR_RUNNING.animation.get();
	}

	@Override
	public String getDefaultName()
	{
		return "container.mechanimation.stirling_generator";
	}

	@Override
	public int fluxProvidedPerIteration(TileEntityStirlingGenerator tile)
	{
		return FLUX_PER_BURN_TICK;
	}

    @Override
	public int getField(int id)
    {
        switch (id)
        {
        case 0:
        	return this.fuelLeft;
        case 1:
        	return burnTime > 0 ? burnTime : 1;
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
            this.fuelLeft = value;
            break;
        case 1:
            this.burnTime = value;
            break;
        }
    }

    @Override
	public int getFieldCount()
    {
        return 2;
    }

	@Override
	public TileEntityStirlingGenerator getValidRecipe()
	{
		return fuelLeft > 0 || TileEntityFurnace.isItemFuel(ITEM_INDEX_FUEL_INPUT.getStack()) ? this : null;
	}

	public int fuelLeft = 0;
	public int burnTime = 0;

	@Override
	public void onProcessTick(TileEntityStirlingGenerator recipe)
	{
		if (this.fuelLeft <= 0) //start burning item
		{
			ItemStack stack = ITEM_INDEX_FUEL_INPUT.getStack();
			fuelLeft = burnTime = TileEntityFurnace.getItemBurnTime(stack);
            Item item = stack.getItem();
            stack.shrink(1);
            if (stack.isEmpty()) stack = item.getContainerItem(stack);
            ITEM_INDEX_FUEL_INPUT.setStack(stack);
		}
		fuelLeft--;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addItemSlotsToContainer(ContainerMachine<TileEntityStirlingGenerator> container)
	{
		super.addItemSlotsToContainer(container);
		container.addSlotToContainer(new SlotMachine<>(this, ITEM_INDEX_FUEL_INPUT.getSlot(), 80, 53));
	}

    @Override
	public void readFromDisk(NBTTagCompound compound)
    {
        super.readFromDisk(compound);
        this.fuelLeft = compound.getInteger("fuelLeft");
        this.burnTime = compound.getInteger("burnTime");
    }

    @Override
	public void writeToDisk(NBTTagCompound compound)
    {
        super.writeToDisk(compound);
        compound.setInteger("fuelLeft", fuelLeft);
        compound.setInteger("burnTime", burnTime);
    }

	@Override
	public StatBase getInteractionStat()
	{
		return MechanimationStats.STIRLING_GENERATOR_INTERACTION;
	}
}