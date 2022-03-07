package firemerald.mechanimation.compat.jei;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.util.Rectangle;
import firemerald.mechanimation.client.gui.inventory.GuiMachine;
import firemerald.mechanimation.client.gui.inventory.IGuiElements;
import firemerald.mechanimation.compat.jei.fluid.FluidOrGasStackHelper;
import firemerald.mechanimation.compat.jei.fluid.FluidOrGasStackRenderer;
import firemerald.mechanimation.compat.jei.gas.GasStackHelper;
import firemerald.mechanimation.compat.jei.gas.GasStackRenderer;
import firemerald.mechanimation.compat.jei.recipe.arc_furnace.RecipeCategoryArcFurnace;
import firemerald.mechanimation.compat.jei.recipe.assembly_terminal.RecipeCategoryAssembly;
import firemerald.mechanimation.compat.jei.recipe.casting.RecipeCategoryCasting;
import firemerald.mechanimation.compat.jei.recipe.catalytic_reformer.RecipeCategoryCatalyticReformer;
import firemerald.mechanimation.compat.jei.recipe.claus_plant.RecipeCategoryClausPlant;
import firemerald.mechanimation.compat.jei.recipe.desalter.RecipeCategoryDesalter;
import firemerald.mechanimation.compat.jei.recipe.distillation.RecipeCategoryDistillation;
import firemerald.mechanimation.compat.jei.recipe.electrolyzer.RecipeCategoryElectrolyzer;
import firemerald.mechanimation.compat.jei.recipe.fluid_reactor.RecipeCategoryFluidReactor;
import firemerald.mechanimation.compat.jei.recipe.generator.combustion.RecipeCategoryCombustionGenerator;
import firemerald.mechanimation.compat.jei.recipe.generator.stirling.RecipeCategoryStirlingGenerator;
import firemerald.mechanimation.compat.jei.recipe.hydrotreater.RecipeCategoryHydrotreater;
import firemerald.mechanimation.compat.jei.recipe.merox_treater.RecipeCategoryMeroxTreater;
import firemerald.mechanimation.compat.jei.recipe.press.RecipeCategoryPress;
import firemerald.mechanimation.compat.jei.recipe.pulverizer.RecipeCategoryPulverizer;
import firemerald.mechanimation.compat.jei.transfer.MachineRecipeTransferHandler;
import firemerald.mechanimation.compat.mekanism.CompatProviderMekanism;
import firemerald.mechanimation.init.MechanimationItems;
import firemerald.mechanimation.multipart.pipe.EnumPipeTier;
import firemerald.mechanimation.multipart.pipe.ItemPartPipe;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasRegistry;
import mekanism.api.gas.GasStack;
import mekanism.client.jei.MekanismJEI;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.ISubtypeRegistry.ISubtypeInterpreter;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.startup.StackHelper;
import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

//@JEIPlugin
public class JEICompatPlugin implements IModPlugin, IGuiElements
{
    public static final IIngredientType<FluidOrGasStack> TYPE_FLUID = () -> FluidOrGasStack.class;
    public static final IIngredientType<GasStack> TYPE_GAS = CompatProviderMekanism.INSTANCE.isPresent() ? MekanismJEI.TYPE_GAS : () -> GasStack.class;
    //public static final Map<Class<IItemMachine<?>>, IMachineRecipeTransferHandler<?>> MACHINE_RECIPE_HANDLERS = new HashMap<>();
	public static IJeiHelpers jeiHelpers;
	public static IGuiHelper guiHelper;
	public static IJeiRuntime jeiRuntime;
	public static MachineRecipeTransferHandler machineRecipeHandler;
	public static IDrawableAnimated energyMeter;
	public static IDrawableAnimated energyMeterFill;
	public static IDrawableAnimated workMeterFill;
	public static IDrawableStatic fluidMarker;
	public static IDrawableAnimated progressRight;
	public static IDrawableAnimated progressLeftHalf;
	public static IDrawableAnimated progressRightHalf;
	public static IDrawableAnimated progressBurn;

	public static IDrawableStatic makeDrawable(IGuiHelper guiHelper, Rectangle bounds)
	{
		return guiHelper.createDrawable(GUI_ELEMENTS_TEXTURE, bounds.x1, bounds.y1, bounds.w, bounds.h);
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry)
	{
		jeiHelpers = registry.getJeiHelpers();
		guiHelper = jeiHelpers.getGuiHelper();

		energyMeter = guiHelper.createAnimatedDrawable(makeDrawable(guiHelper, ENERGY_BAR_TEX), 300, StartDirection.TOP, true);
		energyMeterFill = guiHelper.createAnimatedDrawable(makeDrawable(guiHelper, ENERGY_BAR_TEX), 300, StartDirection.BOTTOM, false);
		workMeterFill = guiHelper.createAnimatedDrawable(makeDrawable(guiHelper, WORK_BAR_TEX), 100, StartDirection.BOTTOM, false);
		fluidMarker = makeDrawable(guiHelper, FLUID_BAR_TEX);
		progressRight = guiHelper.createAnimatedDrawable(makeDrawable(guiHelper, PROGRESS_RIGHT_TEX), 100, StartDirection.LEFT, false);
		progressLeftHalf = guiHelper.createAnimatedDrawable(makeDrawable(guiHelper, PROGRESS_HALF_LEFT_TEX), 100, StartDirection.RIGHT, false);
		progressRightHalf = guiHelper.createAnimatedDrawable(makeDrawable(guiHelper, PROGRESS_HALF_RIGHT_TEX), 100, StartDirection.LEFT, false);
		progressBurn = guiHelper.createAnimatedDrawable(makeDrawable(guiHelper, PROGRESS_BURN_TEX), 100, StartDirection.TOP, true);

		RecipeCategoryPress.register(registry);
		RecipeCategoryFluidReactor.register(registry);
		RecipeCategoryElectrolyzer.register(registry);
		RecipeCategoryHydrotreater.register(registry);
		RecipeCategoryDesalter.register(registry);
		RecipeCategoryCatalyticReformer.register(registry);
		RecipeCategoryDistillation.register(registry);
		RecipeCategoryMeroxTreater.register(registry);
		RecipeCategoryClausPlant.register(registry);
		RecipeCategoryStirlingGenerator.register(registry);
		RecipeCategoryCombustionGenerator.register(registry);
		RecipeCategoryPulverizer.register(registry);
		RecipeCategoryArcFurnace.register(registry);
		RecipeCategoryCasting.register(registry);
		RecipeCategoryAssembly.register(registry);
	}

	@Override
	public void register(IModRegistry registry)
	{
		jeiHelpers = registry.getJeiHelpers();
		guiHelper = jeiHelpers.getGuiHelper();
		registry.getRecipeTransferRegistry().addUniversalRecipeTransferHandler(machineRecipeHandler = new MachineRecipeTransferHandler((StackHelper) jeiHelpers.getStackHelper(), jeiHelpers.recipeTransferHandlerHelper()));
		RecipeCategoryPress.initialize(registry);
		RecipeCategoryFluidReactor.initialize(registry);
		RecipeCategoryElectrolyzer.initialize(registry);
		RecipeCategoryHydrotreater.initialize(registry);
		RecipeCategoryDesalter.initialize(registry);
		RecipeCategoryCatalyticReformer.initialize(registry);
		RecipeCategoryDistillation.initialize(registry);
		RecipeCategoryMeroxTreater.initialize(registry);
		RecipeCategoryClausPlant.initialize(registry);
		RecipeCategoryStirlingGenerator.initialize(registry);
		RecipeCategoryCombustionGenerator.initialize(registry);
		RecipeCategoryPulverizer.initialize(registry);
		RecipeCategoryArcFurnace.initialize(registry);
		RecipeCategoryCasting.initialize(registry);
		RecipeCategoryAssembly.initialize(registry);

		registry.addAdvancedGuiHandlers(new IngredientGuiHandler<>(GuiMachine.class));

		//Descriptions.register(registry);
	}

    @Override
    public void registerIngredients(IModIngredientRegistration registry)
    {
    	if (!CompatProviderMekanism.INSTANCE.isPresent())
    	{
            List<GasStack> list = GasRegistry.getRegisteredGasses().stream().filter(Gas::isVisible).map(g -> new GasStack(g, Fluid.BUCKET_VOLUME)).collect(Collectors.toList());
            registry.register(TYPE_GAS, list, new GasStackHelper(), new GasStackRenderer());
    	}
        List<FluidOrGasStack> list = new ArrayList<>();
        FluidRegistry.getRegisteredFluids().values().forEach(fluid -> list.add(FluidOrGasStack.forFluid(new FluidStack(fluid, Fluid.BUCKET_VOLUME))));
        GasRegistry.getRegisteredGasses().stream().filter(Gas::isVisible).forEach(gas -> list.add(FluidOrGasStack.forGas(new GasStack(gas, Fluid.BUCKET_VOLUME))));
        registry.register(TYPE_FLUID, list, new FluidOrGasStackHelper(), new FluidOrGasStackRenderer());
    }

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime)
	{
		JEICompatPlugin.jeiRuntime = jeiRuntime;
	}

    @Override
    public void registerItemSubtypes(ISubtypeRegistry registry)
    {
    	ISubtypeInterpreter PIPE_TIER = (stack) -> {
    		ItemPartPipe pipe = (ItemPartPipe) stack.getItem();
    		EnumPipeTier tier = pipe.getTier(stack);
    		EnumDyeColor color = pipe.getColor(stack);
    		return color == null ? (tier.name().toLowerCase(Locale.ENGLISH)) : (tier.name().toLowerCase(Locale.ENGLISH) + "_" + color.name().toLowerCase(Locale.ENGLISH));
    	};
        registry.registerSubtypeInterpreter(MechanimationItems.ITEM_PIPE, PIPE_TIER);
        registry.registerSubtypeInterpreter(MechanimationItems.ENERGY_PIPE, PIPE_TIER);
        registry.registerSubtypeInterpreter(MechanimationItems.FLUID_PIPE, PIPE_TIER);
    }
}