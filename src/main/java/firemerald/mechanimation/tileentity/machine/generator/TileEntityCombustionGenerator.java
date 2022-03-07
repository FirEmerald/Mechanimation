package firemerald.mechanimation.tileentity.machine.generator;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import firemerald.api.mcms.animation.AnimationState;
import firemerald.api.mcms.animation.IAnimation;
import firemerald.api.mcms.model.IModel;
import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.api.capabilities.Capabilities;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.generator.combustion.CombustionGeneratorRecipes;
import firemerald.mechanimation.api.crafting.generator.combustion.ICombustionGeneratorRecipe;
import firemerald.mechanimation.client.gui.inventory.IGuiElements;
import firemerald.mechanimation.init.MechanimationStats;
import firemerald.mechanimation.inventory.container.ContainerMachine;
import firemerald.mechanimation.inventory.slot.SlotMachine;
import firemerald.mechanimation.mcms.MCMSAnimation;
import firemerald.mechanimation.mcms.MCMSModel;
import firemerald.mechanimation.mcms.MCMSTexture;
import firemerald.mechanimation.tileentity.machine.base.RenderInfo;
import firemerald.mechanimation.tileentity.machine.base.energy.CommonEnergyPredicates;
import firemerald.mechanimation.tileentity.machine.base.energy.EnergyInventoryTemplate;
import firemerald.mechanimation.tileentity.machine.base.energy.EnergyInventoryTemplateBuilder;
import firemerald.mechanimation.tileentity.machine.base.fluids.CommonFluidPredicates;
import firemerald.mechanimation.tileentity.machine.base.fluids.FluidCapWrapper;
import firemerald.mechanimation.tileentity.machine.base.fluids.FluidInventoryTemplate;
import firemerald.mechanimation.tileentity.machine.base.fluids.FluidInventoryTemplateBuilder;
import firemerald.mechanimation.tileentity.machine.base.fluids.IFluidGuiMachine;
import firemerald.mechanimation.tileentity.machine.base.fluids.IFluidInventory;
import firemerald.mechanimation.tileentity.machine.base.fluids.IFluidSlot;
import firemerald.mechanimation.tileentity.machine.base.items.CommonItemPredicates;
import firemerald.mechanimation.tileentity.machine.base.items.IItemSlot;
import firemerald.mechanimation.tileentity.machine.base.items.ItemInventoryTemplate;
import firemerald.mechanimation.tileentity.machine.base.items.ItemInventoryTemplateBuilder;
import firemerald.mechanimation.tileentity.machine.base.operations.CommonOperations;
import firemerald.mechanimation.tileentity.machine.base.operations.OperationsTemplate;
import firemerald.mechanimation.util.EnumFace;
import firemerald.mechanimation.util.Utils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityCombustionGenerator extends TileEntityGeneratorBase<TileEntityCombustionGenerator, ICombustionGeneratorRecipe> implements IFluidGuiMachine<TileEntityCombustionGenerator>, ICombustionGeneratorRecipe
{
	public static final int FLUX_PER_ROTATION = 3200;
    public static final ResourceLocation COMBUSTION_GENERATOR_GUI_TEXTURES = new ResourceLocation(MechanimationAPI.MOD_ID, "textures/gui/container/combustion_generator.png");
	public static final String
	ITEM_INDEX_FUEL = "fuel";
	public static final ItemInventoryTemplate<TileEntityCombustionGenerator> ITEM_TEMPLATE = new ItemInventoryTemplateBuilder<TileEntityCombustionGenerator>()
    		.addSlot(ENERGY_INDEX_ENERGY, machine -> 64, machine -> 64, machine -> Utils::isEnergyReceiver, machine -> 64, machine -> stack -> !Utils.isEnergyReceiver(stack), machine -> EnumFace.values())
    		.addSlot(ITEM_INDEX_FUEL, machine -> 64, machine -> 64, CommonItemPredicates.providesFluidTo(machine -> machine.FLUID_INDEX_FUEL_INPUT), machine -> 64, CommonItemPredicates.doesNotProvideFluidTo(machine -> machine.FLUID_INDEX_FUEL_INPUT), machine -> EnumFace.values())
    		.build();
	public static final FluidInventoryTemplate<TileEntityCombustionGenerator> FLUID_TEMPLATE = new FluidInventoryTemplateBuilder<TileEntityCombustionGenerator>()
			.addSlot(ITEM_INDEX_FUEL, machine -> 5 * Fluid.BUCKET_VOLUME, machine -> 200, machine -> CombustionGeneratorRecipes::isCraftable, machine -> 200, CommonFluidPredicates.accept(), machine -> EnumFace.values())
    		.build();
	public static final EnergyInventoryTemplate<TileEntityCombustionGenerator> ENERGY_TEMPLATE = new EnergyInventoryTemplateBuilder<TileEntityCombustionGenerator>()
			.addSlot(ENERGY_INDEX_ENERGY, machine -> 10000, machine -> 0, CommonEnergyPredicates.deny(), machine -> 2000, CommonEnergyPredicates.accept(), machine -> EnumFace.values())
    		.build();
	public static final OperationsTemplate<TileEntityCombustionGenerator> OPERATION_TEMPLATE = new OperationsTemplate<TileEntityCombustionGenerator>()
			.addOperation(CommonOperations.<TileEntityCombustionGenerator>energyToItem(machine -> machine.ITEM_INDEX_ENERGY_OUTPUT, machine -> machine.ENERGY_INDEX_ENERGY_OUTPUT, machine -> 2000))
			.addOperation(CommonOperations.<TileEntityCombustionGenerator>fluidFromItem(machine -> machine.ITEM_INDEX_FUEL_INPUT, machine -> machine.FLUID_INDEX_FUEL_INPUT, machine -> Fluid.BUCKET_VOLUME))
			.addOperation(CommonOperations.<TileEntityCombustionGenerator>provideEnergy(machine -> machine.ENERGY_INDEX_ENERGY_OUTPUT, machine -> 200, machine -> EnumFace.values()));

	public final IItemSlot
	ITEM_INDEX_FUEL_INPUT;
	public final IFluidSlot
	FLUID_INDEX_FUEL_INPUT;
	@SideOnly(Side.CLIENT)
	public RenderInfo
	renderDimsFluidFuel;
	private final IFluidHandler[] facedFluidHandlers = new IFluidHandler[] {
			new FluidCapWrapper<>(this, EnumFace.values()[0]),
			new FluidCapWrapper<>(this, EnumFace.values()[1]),
			new FluidCapWrapper<>(this, EnumFace.values()[2]),
			new FluidCapWrapper<>(this, EnumFace.values()[3]),
			new FluidCapWrapper<>(this, EnumFace.values()[4]),
			new FluidCapWrapper<>(this, EnumFace.values()[5])
	};
	public final IFluidInventory<TileEntityCombustionGenerator> fluidInventory;
	public int fuelLeft = 0;
	public float speed = 0;
	public float targetSpeed = 0;

    public TileEntityCombustionGenerator()
    {
    	super(ITEM_TEMPLATE, ENERGY_TEMPLATE);
    	fluidInventory = FLUID_TEMPLATE.build(this);
    	ITEM_INDEX_FUEL_INPUT = this.itemInventory.getItemSlot(ITEM_TEMPLATE.getIndex(ITEM_INDEX_FUEL));
    	FLUID_INDEX_FUEL_INPUT = this.fluidInventory.getFluidSlot(FLUID_TEMPLATE.getIndex(ITEM_INDEX_FUEL));
		this.operations = OPERATION_TEMPLATE.build(this);
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
		{
			renderDimsFluidFuel = new RenderInfo(85, 26, IGuiElements.FLUID_BAR_DECOR);
		}
    }

    @Override
	public float getAnimationSpeed()
    {
    	return this.speed;
    }

	@Override
	public void readFromShared(NBTTagCompound tag)
	{
		super.readFromShared(tag);
		getFluidInventory().readFromShared(tag);
        this.speed = tag.getFloat("speed");
        this.targetSpeed = tag.getFloat("targetSpeed");
	}

	@Override
	public void writeToShared(NBTTagCompound tag)
	{
		super.writeToShared(tag);
		getFluidInventory().writeToShared(tag);
		tag.setFloat("speed", speed);
		tag.setFloat("targetSpeed", targetSpeed);
	}

	@Override
	public int getMaxIterations()
	{
		return 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getGuiTexture()
	{
		return COMBUSTION_GENERATOR_GUI_TEXTURES;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderProgressMeters(GuiContainer gui, int offX, int offY, float zLevel) {}

	@Override
	@SideOnly(Side.CLIENT)
	public String[] getRecipeTypes(int adjX, int adjY)
	{
		//if (ConfigJEI.INSTANCE.combustionGeneratorRecipes.val && adjX >= 81 && adjX < 94 && adjY >= 37 && adjY < 50) return new String[] {RecipeCategoryStirlingGenerator.ID}; TODO
		return null;
	}

	@Override
	public IModel<?, ?> getModel(float partial)
	{
		return MCMSModel.COMBUSTION_GENERATOR.model.get();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getTexture()
	{
		return MCMSTexture.COMBUSTION_GENERATOR;
	}

	@Override
	public String getGuiID()
	{
		return "mechanimation:combustion_generator";
	}

	@Override
	public IAnimation getRunningAnimation()
	{
		return MCMSAnimation.COMBUSTION_GENERATOR_RUNNING.animation.get();
	}

	@Override
	public AnimationState[] getAnimationStates(float partial)
	{
		running[0].time = (shouldTickAnimation() ? (runTime + partial * getAnimationSpeed()) : runTime) * 0.05f;
		return running;
	}

	@Override
	public String getDefaultName()
	{
		return "container.mechanimation.combustion_generator";
	}

	@Override
	public int fluxProvidedPerIteration(ICombustionGeneratorRecipe tile)
	{
		return 0;
	}

	@Override
	public ICombustionGeneratorRecipe getValidRecipe()
	{
		if (this.fuelLeft > 0) return this;
		else
		{
			ICombustionGeneratorRecipe recipe = CombustionGeneratorRecipes.getRecipe(FLUID_INDEX_FUEL_INPUT.getFluid());
			return recipe == null ? null : recipe;
		}
	}

	@Override
	public boolean shouldTickAnimation()
	{
		return speed > 0;
	}

	@Override
	public void update()
	{
		super.update();
		int energy = ENERGY_INDEX_ENERGY_OUTPUT.getEnergyStored();
		int maxEnergy = ENERGY_INDEX_ENERGY_OUTPUT.getMaxEnergyStored();
		int provided = MathHelper.floor(speed * 0.05f * FLUX_PER_ROTATION);
		if (this.speed > 0 && energy < maxEnergy)
		{
			ENERGY_INDEX_ENERGY_OUTPUT.setEnergy(energy = Math.min(energy + provided, maxEnergy));
		}
		if (this.targetSpeed > 0 && energy >= maxEnergy)
		{
			this.targetSpeed = 0;
			this.setNeedsUpdate();
		}
		this.speed = Utils.lerp(speed, targetSpeed, 0.01f); //TODO
	}

	@Override
	public void onProcessTick(ICombustionGeneratorRecipe recipe) //TODO onFluidSlotChanged
	{
		if (recipe != this) //start burning item
		{
			this.targetSpeed = recipe.speed();
			fuelLeft = recipe.ticksPerMillibucket();
			FLUID_INDEX_FUEL_INPUT.remove(1);
			fuelLeft--;
            this.setNeedsUpdate();
		}
		else if (fuelLeft > 0)
		{
			fuelLeft--;
		}
		else if (targetSpeed > 0)
		{
			this.targetSpeed = 0;
            this.setNeedsUpdate();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addItemSlotsToContainer(ContainerMachine<TileEntityCombustionGenerator> container)
	{
		super.addItemSlotsToContainer(container);
		container.addSlotToContainer(new SlotMachine<>(this, ITEM_INDEX_FUEL_INPUT.getSlot(), 80, 53));
	}

	@Override
	public void readFromDisk(NBTTagCompound tag)
	{
		super.readFromDisk(tag);
		getFluidInventory().readFromDisk(tag);
        this.fuelLeft = tag.getInteger("fuelLeft");
	}

	@Override
	public void writeToDisk(NBTTagCompound tag)
	{
		super.writeToDisk(tag);
		getFluidInventory().writeToDisk(tag);
        tag.setInteger("fuelLeft", fuelLeft);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public RenderInfo getFluidRenderInfo(int index)
	{
		return renderDimsFluidFuel;
	}

	@Override
	public int getField(int id)
	{
		return 0;
	}

	@Override
	public void setField(int id, int value) {}

	@Override
	public int getFieldCount()
	{
		return 0;
	}

	@Override
    public void onStoppedRunning()
	{
		this.targetSpeed = 0;
	}

	@Override
	public StatBase getInteractionStat()
	{
		return MechanimationStats.COMBUSTION_GENERATOR_INTERACTION;
	}

	@Override
	public List<FluidOrGasStack> fuel()
	{
		return Collections.emptyList();
	}

	@Override
	public boolean isInputValid(FluidOrGasStack fuel)
	{
		return false;
	}

	@Override
	public float speed()
	{
		return this.targetSpeed;
	}

	@Override
	public int ticksPerMillibucket()
	{
		return 1;
	}

	@Override
	public IFluidInventory<TileEntityCombustionGenerator> getFluidInventory()
	{
		return fluidInventory;
	}

    @Override
    public boolean hasCapabilityLocal(Capability<?> capability, @Nullable EnumFacing facing)
    {
    	if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || capability == Capabilities.gasHandler)
    	{
    		return getFluidInventory().getSlots(getFace(facing)).length > 0;
    	}
    	else return super.hasCapabilityLocal(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
	public <S> S getCapabilityLocal(Capability<S> capability, @Nullable EnumFacing facing)
    {
    	if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
    	{
    		if (facing == null) return (S) this.getFluidInventory();
    		else return (S) facedFluidHandlers[getFace(facing).ordinal()];
    	}
    	else if (capability == Capabilities.gasHandler) return (S) this;
    	else return super.getCapabilityLocal(capability, facing);
    }
}