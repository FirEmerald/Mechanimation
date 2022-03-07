package firemerald.mechanimation.compat.jei.recipe.hydrotreater;

import java.util.ArrayList;
import java.util.List;

import firemerald.api.core.client.Translator;
import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.hydrotreater.HydrotreaterRecipes;
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

public class RecipeCategoryHydrotreater extends RecipeCategoryBase<RecipeWrapperHydrotreater>
{
	public static final String ID = "mechanimation.hydrotreater";
    public static final ResourceLocation HYDROTREATER_GUI_TEXTURES = new ResourceLocation(MechanimationAPI.MOD_ID, "textures/gui/jei/hydrotreater.png");

	public static void register(IRecipeCategoryRegistration registry)
	{
		if (!ConfigJEI.INSTANCE.hydrotreaterRecipes.val) return;
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new RecipeCategoryHydrotreater(guiHelper));
	}

	public static void initialize(IModRegistry registry) {

		if (!ConfigJEI.INSTANCE.hydrotreaterRecipes.val) return;
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), ID);
		//registry.addRecipeClickArea(GuiHydrotreater.class, 82, 26, 12, 24, ID);
		NonNullList<ItemStack> stacks = NonNullList.create();
		MechanimationBlocks.HYDROTREATER.getSubBlocks(CreativeTabs.SEARCH, stacks);
		stacks.forEach(stack -> registry.addRecipeCatalyst(stack, ID));
	}

	public static List<RecipeWrapperHydrotreater> getRecipes(IGuiHelper guiHelper)
	{
		List<RecipeWrapperHydrotreater> recipes = new ArrayList<>();
		HydrotreaterRecipes.getRecipes().forEach(result -> {
			if (!result.getFluidInput().isEmpty() && result.getRequiredHydrogen() > 0) recipes.add(new RecipeWrapperHydrotreater(guiHelper, result, ID));
		});
		return recipes;
	}

	public RecipeCategoryHydrotreater(IGuiHelper guiHelper)
	{
		this.background = guiHelper.createDrawable(HYDROTREATER_GUI_TEXTURES, 0, 0, 140, 63);
		localizedName = Translator.translate("container.mechanimation.hydrotreater");
	}

	@Override
	public String getUid()
	{
		return ID;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, RecipeWrapperHydrotreater recipeWrapper, IIngredients ingredients)
	{
		List<List<FluidOrGasStack>> inputFluids  = ingredients.getInputs (JEICompatPlugin.TYPE_FLUID);
		List<List<FluidOrGasStack>> outputFluids = ingredients.getOutputs(JEICompatPlugin.TYPE_FLUID);
		int maxVolume = 1;
		for (List<FluidOrGasStack> list : inputFluids ) for (FluidOrGasStack stack : list) if (stack != null && stack.getAmount() > maxVolume) maxVolume = stack.getAmount();
		for (List<FluidOrGasStack> list : outputFluids) for (FluidOrGasStack stack : list) if (stack != null && stack.getAmount() > maxVolume) maxVolume = stack.getAmount();

		IGuiIngredientGroup<FluidOrGasStack> guiGasStacks = recipeLayout.getIngredientsGroup(JEICompatPlugin.TYPE_FLUID);

		initFluidOrGas(guiGasStacks, inputFluids .get(0), 0, true , 50 , 18, 6, 24, maxVolume, JEICompatPlugin.fluidMarker);
		initFluidOrGas(guiGasStacks, inputFluids .get(1), 1, true , 28 , 18, 6, 24, maxVolume, JEICompatPlugin.fluidMarker);
		initFluidOrGas(guiGasStacks, outputFluids.get(0), 2, false, 106, 18, 6, 24, maxVolume, JEICompatPlugin.fluidMarker);
		initFluidOrGas(guiGasStacks, outputFluids.get(1), 3, false, 128, 18, 6, 24, maxVolume, JEICompatPlugin.fluidMarker);
	}
}