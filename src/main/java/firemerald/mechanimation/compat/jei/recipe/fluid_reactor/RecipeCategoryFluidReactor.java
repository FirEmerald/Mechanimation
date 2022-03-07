package firemerald.mechanimation.compat.jei.recipe.fluid_reactor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import firemerald.api.core.client.Translator;
import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.fluid_reactor.FluidReactorRecipes;
import firemerald.mechanimation.compat.jei.ConfigJEI;
import firemerald.mechanimation.compat.jei.JEICompatPlugin;
import firemerald.mechanimation.compat.jei.recipe.RecipeCategoryBase;
import firemerald.mechanimation.init.MechanimationBlocks;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class RecipeCategoryFluidReactor extends RecipeCategoryBase<RecipeWrapperFluidReactor>
{
	public static final String ID = "mechanimation.fluid_reactor";
    public static final ResourceLocation FLUID_REACTOR_GUI_TEXTURES = new ResourceLocation(MechanimationAPI.MOD_ID, "textures/gui/jei/fluid_reactor.png");

	public static void register(IRecipeCategoryRegistration registry)
	{
		if (!ConfigJEI.INSTANCE.fluidReactorRecipes.val) return;
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new RecipeCategoryFluidReactor(guiHelper));
	}

	public static void initialize(IModRegistry registry) {

		if (!ConfigJEI.INSTANCE.fluidReactorRecipes.val) return;
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), ID);
		//registry.addRecipeClickArea(GuiFluidReactor.class, 104, 28, 12, 24, ID);
		NonNullList<ItemStack> stacks = NonNullList.create();
		MechanimationBlocks.FLUID_REACTOR.getSubBlocks(CreativeTabs.SEARCH, stacks);
		stacks.forEach(stack -> registry.addRecipeCatalyst(stack, ID));
	}

	public static List<RecipeWrapperFluidReactor> getRecipes(IGuiHelper guiHelper)
	{
		List<RecipeWrapperFluidReactor> recipes = new ArrayList<>();
		FluidReactorRecipes.getRecipes().forEach(result -> {
			boolean hasInput = false;
			ItemStack[] input = result.getItemInput();
			if (input != null && input.length > 0)
			{
				for (ItemStack stack : input) if (!stack.isEmpty())
				{
					hasInput = true;
					break;
				}
			}
			if (!hasInput)
			{
				if (!result.getFluidInputPrimary().isEmpty() || !result.getFluidInputSecondary().isEmpty() || !result.getFluidInputTertiary().isEmpty()) hasInput = true;
			}
			if (hasInput) recipes.add(new RecipeWrapperFluidReactor(guiHelper, result, ID));
		});
		return recipes;
	}

	public RecipeCategoryFluidReactor(IGuiHelper guiHelper)
	{
		this.background = guiHelper.createDrawable(FLUID_REACTOR_GUI_TEXTURES, 0, 0, 162, 58);
		localizedName = Translator.translate("container.mechanimation.fluid_reactor");
	}

	@Override
	public String getUid()
	{
		return ID;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, RecipeWrapperFluidReactor recipeWrapper, IIngredients ingredients)
	{
		List<List<ItemStack>> inputItems = ingredients.getInputs(VanillaTypes.ITEM);
		List<List<FluidOrGasStack>> inputFluids = ingredients.getInputs(JEICompatPlugin.TYPE_FLUID);
		List<List<ItemStack>> outputItems = ingredients.getOutputs(VanillaTypes.ITEM);
		List<List<FluidOrGasStack>> outputFluids = ingredients.getOutputs(JEICompatPlugin.TYPE_FLUID);
		int maxVolume = 1;
		for (List<FluidOrGasStack> list : inputFluids) for (FluidOrGasStack stack : list) if (stack != null && stack.getAmount() > maxVolume) maxVolume = stack.getAmount();
		for (List<FluidOrGasStack> list : outputFluids) for (FluidOrGasStack stack : list) if (stack != null && stack.getAmount() > maxVolume) maxVolume = stack.getAmount();

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiIngredientGroup<FluidOrGasStack> guiFluidStacks = recipeLayout.getIngredientsGroup(JEICompatPlugin.TYPE_FLUID);


		guiItemStacks.init(0, true, 66, 40);
		initFluidOrGas(guiFluidStacks, inputFluids.get(0) , 1, true , 6  , 14, 6, 24, maxVolume, JEICompatPlugin.fluidMarker);
		initFluidOrGas(guiFluidStacks, inputFluids.get(1) , 2, true , 28 , 14, 6, 24, maxVolume, JEICompatPlugin.fluidMarker);
		initFluidOrGas(guiFluidStacks, inputFluids.get(2) , 3, true , 50 , 14, 6, 24, maxVolume, JEICompatPlugin.fluidMarker);
		guiItemStacks.init(4, false, 122, 40);
		initFluidOrGas(guiFluidStacks, outputFluids.get(0), 5, false, 150, 14, 6, 24, maxVolume, JEICompatPlugin.fluidMarker);

		guiItemStacks.set(0, inputItems.size() > 0 ? inputItems.get(0) : Collections.emptyList());
		guiItemStacks.set(4, outputItems.size() > 0 ? outputItems.get(0) : Collections.emptyList());
	}
}