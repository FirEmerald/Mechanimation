package firemerald.mechanimation.compat.jei.recipe.desalter;

import java.util.ArrayList;
import java.util.List;

import firemerald.api.core.client.Translator;
import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.desalter.DesalterRecipes;
import firemerald.mechanimation.compat.jei.ConfigJEI;
import firemerald.mechanimation.compat.jei.JEICompatPlugin;
import firemerald.mechanimation.compat.jei.recipe.RecipeCategoryBase;
import firemerald.mechanimation.init.MechanimationBlocks;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class RecipeCategoryDesalter extends RecipeCategoryBase<RecipeWrapperDesalter>
{
	public static final String ID = "mechanimation.desalter";
    public static final ResourceLocation DESALTER_GUI_TEXTURES = new ResourceLocation(MechanimationAPI.MOD_ID, "textures/gui/jei/desalter.png");

	public static void register(IRecipeCategoryRegistration registry)
	{
		if (!ConfigJEI.INSTANCE.desalterRecipes.val) return;
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new RecipeCategoryDesalter(guiHelper));
	}

	public static void initialize(IModRegistry registry) {

		if (!ConfigJEI.INSTANCE.desalterRecipes.val) return;
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), ID);
		//registry.addRecipeClickArea(GuiDesalter.class, 82, 26, 12, 24, ID);
		NonNullList<ItemStack> stacks = NonNullList.create();
		MechanimationBlocks.DESALTER.getSubBlocks(CreativeTabs.SEARCH, stacks);
		stacks.forEach(stack -> registry.addRecipeCatalyst(stack, ID));
	}

	public static List<RecipeWrapperDesalter> getRecipes(IGuiHelper guiHelper)
	{
		List<RecipeWrapperDesalter> recipes = new ArrayList<>();
		DesalterRecipes.getRecipes().forEach(result -> {
			if (!result.getFluidInput().isEmpty() && result.getRequiredWater() > 0) recipes.add(new RecipeWrapperDesalter(guiHelper, result, ID));
		});
		return recipes;
	}

	public RecipeCategoryDesalter(IGuiHelper guiHelper)
	{
		this.background = guiHelper.createDrawable(DESALTER_GUI_TEXTURES, 0, 0, 118, 63);
		localizedName = Translator.translate("container.mechanimation.desalter");
	}

	@Override
	public String getUid()
	{
		return ID;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, RecipeWrapperDesalter recipeWrapper, IIngredients ingredients)
	{
		List<List<FluidOrGasStack>> inputFluids  = ingredients.getInputs (JEICompatPlugin.TYPE_FLUID);
		List<List<FluidOrGasStack>> outputFluids = ingredients.getOutputs(JEICompatPlugin.TYPE_FLUID);
		int maxVolume = 1;
		for (List<FluidOrGasStack> list : inputFluids ) for (FluidOrGasStack stack : list) if (stack != null && stack.getAmount() > maxVolume) maxVolume = stack.getAmount();
		for (List<FluidOrGasStack> list : outputFluids) for (FluidOrGasStack stack : list) if (stack != null && stack.getAmount() > maxVolume) maxVolume = stack.getAmount();

		IGuiIngredientGroup<FluidOrGasStack> guiGasStacks = recipeLayout.getIngredientsGroup(JEICompatPlugin.TYPE_FLUID);

		initFluidOrGas(guiGasStacks, inputFluids .get(0), 0, true , 28 , 18, 6, 24, maxVolume, JEICompatPlugin.fluidMarker);
		initFluidOrGas(guiGasStacks, inputFluids .get(1), 1, true , 6  , 18, 6, 24, maxVolume, JEICompatPlugin.fluidMarker);
		initFluidOrGas(guiGasStacks, outputFluids.get(0), 2, false, 84 , 18, 6, 24, maxVolume, JEICompatPlugin.fluidMarker);
		initFluidOrGas(guiGasStacks, outputFluids.get(1), 3, false, 106, 18, 6, 24, maxVolume, JEICompatPlugin.fluidMarker);
	}
}