package firemerald.mechanimation.compat.jei.recipe.claus_plant;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.Streams;

import firemerald.api.core.client.Translator;
import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.claus_plant.ClausPlantRecipes;
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

public class RecipeCategoryClausPlant extends RecipeCategoryBase<RecipeWrapperClausPlant>
{
	public static final String ID = "mechanimation.claus_plant";
    public static final ResourceLocation CLAUS_PLANT_GUI_TEXTURES = new ResourceLocation(MechanimationAPI.MOD_ID, "textures/gui/jei/claus_plant.png");

	public static void register(IRecipeCategoryRegistration registry)
	{
		if (!ConfigJEI.INSTANCE.clausPlantRecipes.val) return;
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new RecipeCategoryClausPlant(guiHelper));
	}

	public static void initialize(IModRegistry registry) {

		if (!ConfigJEI.INSTANCE.clausPlantRecipes.val) return;
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), ID);
		NonNullList<ItemStack> stacks = NonNullList.create();
		MechanimationBlocks.CLAUS_PLANT.getSubBlocks(CreativeTabs.SEARCH, stacks);
		stacks.forEach(stack -> registry.addRecipeCatalyst(stack, ID));
	}

	public static List<RecipeWrapperClausPlant> getRecipes(IGuiHelper guiHelper)
	{
		List<RecipeWrapperClausPlant> recipes = new ArrayList<>();
		ClausPlantRecipes.getRecipes().forEach(result -> {
			if (!result.getFluidInput().isEmpty() && result.getRequiredOxygen() > 0) recipes.add(new RecipeWrapperClausPlant(guiHelper, result, ID));
		});
		return recipes;
	}

	public RecipeCategoryClausPlant(IGuiHelper guiHelper)
	{
		this.background = guiHelper.createDrawable(CLAUS_PLANT_GUI_TEXTURES, 0, 0, 140, 63);
		localizedName = Translator.translate("container.mechanimation.claus_plant");
	}

	@Override
	public String getUid()
	{
		return ID;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, RecipeWrapperClausPlant recipeWrapper, IIngredients ingredients)
	{
		List<List<FluidOrGasStack>> inputFluids  = ingredients.getInputs (JEICompatPlugin.TYPE_FLUID);
		List<List<FluidOrGasStack>> outputFluids = ingredients.getOutputs(JEICompatPlugin.TYPE_FLUID);
		int maxVolume = Streams.concat(inputFluids.stream(), outputFluids.stream()).flatMap(List::stream).filter(Objects::nonNull).mapToInt(FluidOrGasStack::getAmount).max().orElse(1);
		if (maxVolume < 1) maxVolume = 1;

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiIngredientGroup<FluidOrGasStack> guiGasStacks = recipeLayout.getIngredientsGroup(JEICompatPlugin.TYPE_FLUID);

		initFluidOrGas(guiGasStacks, inputFluids .get(0), 0, true , 50 , 18, 6, 24, maxVolume, JEICompatPlugin.fluidMarker);
		initFluidOrGas(guiGasStacks, inputFluids .get(1), 1, true , 28 , 18, 6, 24, maxVolume, JEICompatPlugin.fluidMarker);
		guiItemStacks.init(2, false, 100, 44);
		initFluidOrGas(guiGasStacks, outputFluids.get(0), 3, false, 128, 18, 6, 24, maxVolume, JEICompatPlugin.fluidMarker);

		guiItemStacks.set(2, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
	}
}