package firemerald.mechanimation.tileentity.machine.arc_furnace;

import java.util.Map;

import org.joml.Matrix4d;
import org.joml.Vector3d;

import firemerald.api.core.client.Translator;
import firemerald.api.mcms.animation.IAnimation;
import firemerald.api.mcms.model.IModel;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.arc_furnace.ArcFurnaceRecipes;
import firemerald.mechanimation.api.crafting.arc_furnace.IArcFurnaceRecipe;
import firemerald.mechanimation.api.util.APIUtils;
import firemerald.mechanimation.api.util.APIUtils.BlockOperationTemp;
import firemerald.mechanimation.client.gui.inventory.GuiMachine;
import firemerald.mechanimation.client.gui.inventory.IGuiElements;
import firemerald.mechanimation.compat.jei.ConfigJEI;
import firemerald.mechanimation.compat.jei.recipe.arc_furnace.RecipeCategoryArcFurnace;
import firemerald.mechanimation.init.MechanimationFluids;
import firemerald.mechanimation.inventory.container.ContainerMachine;
import firemerald.mechanimation.inventory.slot.SlotMachine;
import firemerald.mechanimation.tileentity.machine.base.RenderInfo;
import firemerald.mechanimation.tileentity.machine.base.fluids.FluidInventoryTemplate;
import firemerald.mechanimation.tileentity.machine.base.fluids.IFluidSlot;
import firemerald.mechanimation.tileentity.machine.base.implementation.actual.TileEntityFluidItemMachineBase;
import firemerald.mechanimation.tileentity.machine.base.items.IItemSlot;
import firemerald.mechanimation.tileentity.machine.base.items.ItemInventoryTemplate;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileEntitySteelingFurnaceBase<T extends TileEntitySteelingFurnaceBase<T>> extends TileEntityFluidItemMachineBase<T, IArcFurnaceRecipe>
{
	public static final String
	ITEM_INDEX_INPUT = "item_input",
	FLUID_INDEX_OUTPUT = "fluid_output";

	public final IItemSlot
	ITEM_INDEX_ITEM_INPUT,
	ITEM_INDEX_FLUID_OUTPUT;
	public final IFluidSlot
	FLUID_INDEX_FLUID_OUTPUT;
	@SideOnly(Side.CLIENT)
	public RenderInfo
	renderDimsFluidOutput;
	public float ambientTemp = 21, temp = 21;
	public boolean initTemps = true;

	public TileEntitySteelingFurnaceBase(ItemInventoryTemplate<T> itemInventory, FluidInventoryTemplate<T> fluidInventory)
	{
		super(itemInventory::build, fluidInventory::build);
		ITEM_INDEX_ITEM_INPUT = this.itemInventory.getItemSlot(itemInventory.getIndex(ITEM_INDEX_INPUT));
		ITEM_INDEX_FLUID_OUTPUT = this.itemInventory.getItemSlot(itemInventory.getIndex(FLUID_INDEX_OUTPUT));
		FLUID_INDEX_FLUID_OUTPUT = this.fluidInventory.getFluidSlot(fluidInventory.getIndex(FLUID_INDEX_OUTPUT));
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
		{
			renderDimsFluidOutput = new RenderInfo(117, 21, IGuiElements.FLUID_BAR_DECOR);
		}
	}

	public abstract float getFuelTemp();

	public abstract int getFuelSurfaceArea();

	public abstract int getAmbientSurfaceArea();

	public abstract int getHeatConductorVolume();

    public ItemStack getItemOrNull()
    {
    	ItemStack stack = ITEM_INDEX_ITEM_INPUT.getStack();
    	return stack.isEmpty() ? null : stack;
    }

    public abstract boolean isBurning();

    public abstract void startBurning();

    public abstract void consumeFuel();

    public abstract AxisAlignedBB getChamberBox();

    @Override
    public void update()
    {
		BlockOperationTemp op = new BlockOperationTemp(getChamberBox(), 0, 3, APIUtils.getBiomeTempCelsius(world, pos));
		APIUtils.forEachBlock(world, op.getEffectiveBox(), false, op);
		ambientTemp = op.getTemp();
    	IArcFurnaceRecipe recipe = this.getValidRecipe();
    	int volume = getHeatConductorVolume();
    	if (recipe != null)
    	{
	    	ItemStack input = ITEM_INDEX_ITEM_INPUT.getStack();
    		volume += recipe.getVolume(input);
    		if (!isBurning() && recipe.getTemperature(input) >= ambientTemp) startBurning();
    	}
    	float fuelTemp = this.isBurning() ? this.getFuelTemp() : ambientTemp;
    	float deltaFuel = fuelTemp - temp;
    	float deltaAmbient = ambientTemp - temp;
    	this.temp += (deltaFuel * this.getFuelSurfaceArea() + deltaAmbient * this.getAmbientSurfaceArea()) / (volume * 100); //TODO factor
    	super.update();
    	if (isBurning())
    	{
    		consumeFuel();
    		if (world.rand.nextDouble() >= 0.75)
    		{
				IModel<?, ?> model = this.getModel(1);
				if (model != null)
				{
					Map<String, Matrix4d> pose = model.getPose(this.getAnimationStates(1));
					Matrix4d particlePose = pose.get("particle_exhaust");
					if (particlePose != null)
					{
						particlePose = this.getRootTick().mul(particlePose, particlePose);
						Vector3d particlePos = particlePose.getTranslation(new Vector3d());
						world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX() + particlePos.x + .375 + .25 * world.rand.nextDouble(), pos.getY() + particlePos.y, pos.getZ() + particlePos.z + .375 + .25 * world.rand.nextDouble(), 0, .1, 0);
					}
				}
    		}
    	}
    	//TODO particles
    }

    @Override
	public IArcFurnaceRecipe getValidRecipe()
    {
    	ItemStack item = ITEM_INDEX_ITEM_INPUT.getStack();
    	IArcFurnaceRecipe recipe = ArcFurnaceRecipes.getRecipe(item);
    	if (recipe == null) return null;
    	else if (recipe.getTemperature(item) >= Math.max(temp, Math.max(ambientTemp, this.getFuelTemp()))) return null; //cannot reach desired temperature
    	else
    	{
    		FluidOrGasStack output = getActualOutput(recipe.getOutput(item));
    		if (output.getAmount() + FLUID_INDEX_FLUID_OUTPUT.getStored() > FLUID_INDEX_FLUID_OUTPUT.getCapacity() || !FluidOrGasStack.isFluidEqualStatic(FLUID_INDEX_FLUID_OUTPUT.getFluidOrGas(), output)) return null;
    		else return recipe;
    	}
    }

    public FluidOrGasStack getActualOutput(FluidOrGasStack stack)
    {
    	//TODO is steeling?
    	if (stack.isFluid() && stack.getFluidStack().getFluid() == MechanimationFluids.liquidIron) //make steel
    	{
    		return FluidOrGasStack.forFluid(new FluidStack(MechanimationFluids.liquidSteel, stack.getAmount()));
    	}
    	else return stack;
    }

    @Override
	public String getGuiID()
    {
        return "mechanimation:blast_furnace";
    }

	@Override
	public int getMaxFluid(int index)
	{
		IFluidSlot slot = fluidInventory.getFluidSlot(index);
		if (slot == null) return 0;
		else return slot.getCapacity();
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
	public boolean canProcessRecipeThisTick(IArcFurnaceRecipe recipe)
	{
		ItemStack input = ITEM_INDEX_ITEM_INPUT.getStack();
		return recipe.getTemperature(input) <= this.temp;
	}

	@Override
	public void processRecipe(IArcFurnaceRecipe recipe)
	{
		ItemStack input = ITEM_INDEX_ITEM_INPUT.getStack();
		FluidOrGasStack outputFluid = getActualOutput(recipe.getOutput(input));
		FLUID_INDEX_FLUID_OUTPUT.add(outputFluid);
		float conductorVolume = this.getHeatConductorVolume();
		this.temp = (this.temp - this.ambientTemp) * conductorVolume / (conductorVolume + recipe.getVolume(input)) + this.ambientTemp;
		ITEM_INDEX_ITEM_INPUT.remove(recipe.getRequiredCount(input));
    	this.initTemps = true;
	}

	@Override
	public void onStoppedRunning()
	{
		super.onStoppedRunning();
    	this.initTemps = true;
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
		IGuiElements.PROGRESS_RIGHT_PROGRESS.render(offX + 80, offY + 35, zLevel, this.getProgress());
	}

    @Override
	public int getField(int id)
    {
        switch (id)
        {
        case 0:
        	return (int) this.temp;
        case 1:
        	IArcFurnaceRecipe recipe = this.getValidRecipe();
        	if (recipe != null)
        	{
        		ItemStack item = ITEM_INDEX_ITEM_INPUT.getStack();
        		return recipe.getTemperature(item);
        	}
        	else return 0;
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
        	this.temp = value;
        }
    } //not used

    @Override
	public int getFieldCount()
    {
        return 2;
    }

	@Override
	public RenderInfo getFluidRenderInfo(int index)
	{
		if (index == FLUID_INDEX_FLUID_OUTPUT.getSlot()) return renderDimsFluidOutput;
		else return null;
	}

	@Override
    @SideOnly(Side.CLIENT)
    public String[] getRecipeTypes(int adjX, int adjY)
    {
		if (ConfigJEI.INSTANCE.arcFurnaceRecipes.val && adjX >= 80 && adjX < 102 && adjY >= 35 && adjY < 50) return new String[] {RecipeCategoryArcFurnace.ID};
		else return null;
    }

	@Override
	public void onUsageTick(IArcFurnaceRecipe recipe)
	{
		if (initTemps)
		{
			ItemStack input = ITEM_INDEX_ITEM_INPUT.getStack();
			float conductorVolume = this.getHeatConductorVolume();
			float recipeVolume = recipe.getVolume(input);
			this.temp = (21 * recipeVolume + this.temp * conductorVolume) / (recipeVolume + conductorVolume);
			initTemps = false;
		}
	}

	@Override
	public void readFromShared(NBTTagCompound tag)
	{
		super.readFromShared(tag);
        this.temp = tag.getFloat("temp");
	}

	@Override
	public void writeToShared(NBTTagCompound tag)
	{
		super.writeToShared(tag);
		tag.setFloat("temp", temp);
	}

    @Override
	public void readFromDisk(NBTTagCompound tag)
    {
        super.readFromDisk(tag);
        this.ambientTemp = tag.getFloat("ambientTemp");
        this.initTemps = tag.getBoolean("initTemps");
    }

    @Override
	public void writeToDisk(NBTTagCompound tag)
    {
    	super.writeToDisk(tag);
        tag.setFloat("ambientTemp", ambientTemp);
		tag.setBoolean("initTemps", initTemps);
    }

	@SuppressWarnings("unchecked")
	@Override
	public void addItemSlotsToContainer(ContainerMachine<T> container)
	{
		container.addSlotToContainer(new SlotMachine<>((T) this, ITEM_INDEX_FLUID_OUTPUT.getSlot(), 112, 48));
	}
}