package firemerald.mechanimation.compat.jei.recipe.catalytic_reformer;

import java.util.ArrayList;
import java.util.List;

import firemerald.api.core.client.Translator;
import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.catalytic_reformer.CatalyticReformerRecipes;
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

public class RecipeCategoryCatalyticReformer extends RecipeCategoryBase<RecipeWrapperCatalyticReformer>
{
	public static final String ID = "mechanimation.catalytic_reformer";
    public static final ResourceLocation CATALYTIC_REFORMER_GUI_TEXTURES = new ResourceLocation(MechanimationAPI.MOD_ID, "textures/gui/jei/catalytic_reformer.png");

	public static void register(IRecipeCategoryRegistration registry)
	{
		if (!ConfigJEI.INSTANCE.catalyticReformerRecipes.val) return;
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new RecipeCategoryCatalyticReformer(guiHelper));
	}

	public static void initialize(IModRegistry registry) {

		if (!ConfigJEI.INSTANCE.catalyticReformerRecipes.val) return;
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), ID);
		//registry.addRecipeClickArea(GuiCatalyticReformer.class, 68, 25, 14, 6, ID);
		//registry.addRecipeClickArea(GuiCatalyticReformer.class, 94, 25, 14, 6, ID);
		NonNullList<ItemStack> stacks = NonNullList.create();
		MechanimationBlocks.CATALYTIC_REFORMER.getSubBlocks(CreativeTabs.SEARCH, stacks);
		stacks.forEach(stack -> registry.addRecipeCatalyst(stack, ID));
	}

	public static List<RecipeWrapperCatalyticReformer> getRecipes(IGuiHelper guiHelper)
	{
		List<RecipeWrapperCatalyticReformer> recipes = new ArrayList<>();
		CatalyticReformerRecipes.getRecipes().forEach(result -> {
			if (!result.getFluidInput().isEmpty() && result.getRequiredFlux() > 0) recipes.add(new RecipeWrapperCatalyticReformer(guiHelper, result, ID));
		});
		return recipes;
	}

	public RecipeCategoryCatalyticReformer(IGuiHelper guiHelper)
	{
		this.background = guiHelper.createDrawable(CATALYTIC_REFORMER_GUI_TEXTURES, 0, 0, 96, 63);
		localizedName = Translator.translate("container.mechanimation.catalytic_reformer");
	}

	@Override
	public String getUid()
	{
		return ID;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, RecipeWrapperCatalyticReformer recipeWrapper, IIngredients ingredients)
	{
		List<List<FluidOrGasStack>> inputFluids  = ingredients.getInputs (JEICompatPlugin.TYPE_FLUID);
		List<List<FluidOrGasStack>> outputFluids = ingredients.getOutputs(JEICompatPlugin.TYPE_FLUID);
		int maxVolume = 1;
		for (List<FluidOrGasStack> list : inputFluids ) for (FluidOrGasStack stack : list) if (stack != null && stack.getAmount() > maxVolume) maxVolume = stack.getAmount();
		for (List<FluidOrGasStack> list : outputFluids) for (FluidOrGasStack stack : list) if (stack != null && stack.getAmount() > maxVolume) maxVolume = stack.getAmount();

		IGuiIngredientGroup<FluidOrGasStack> guiGasStacks = recipeLayout.getIngredientsGroup(JEICompatPlugin.TYPE_FLUID);

		initFluidOrGas(guiGasStacks, inputFluids .get(0), 0, true , 58, 18, 6, 24, maxVolume, JEICompatPlugin.fluidMarker);
		initFluidOrGas(guiGasStacks, outputFluids.get(0), 1, false, 32, 18, 6, 24, maxVolume, JEICompatPlugin.fluidMarker);
		initFluidOrGas(guiGasStacks, outputFluids.get(1), 2, false, 84, 18, 6, 24, maxVolume, JEICompatPlugin.fluidMarker);
	}
}