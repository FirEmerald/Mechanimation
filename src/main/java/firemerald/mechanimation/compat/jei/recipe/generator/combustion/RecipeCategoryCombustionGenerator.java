package firemerald.mechanimation.compat.jei.recipe.generator.combustion;

import java.util.ArrayList;
import java.util.List;

import firemerald.api.core.client.Translator;
import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.generator.combustion.CombustionGeneratorRecipes;
import firemerald.mechanimation.blocks.machine.BlockGenerator.EnumVariant;
import firemerald.mechanimation.compat.jei.ConfigJEI;
import firemerald.mechanimation.compat.jei.JEICompatPlugin;
import firemerald.mechanimation.compat.jei.recipe.RecipeCategoryBase;
import firemerald.mechanimation.init.MechanimationBlocks;
import firemerald.mechanimation.init.MechanimationItems;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class RecipeCategoryCombustionGenerator extends RecipeCategoryBase<RecipeWrapperCombustionGenerator>
{
	public static final String ID = "mechanimation.combustion_generator";
    public static final ResourceLocation COMBUSTION_GENERATOR_GUI_TEXTURES = new ResourceLocation(MechanimationAPI.MOD_ID, "textures/gui/jei/combustion_generator.png");

	public static void register(IRecipeCategoryRegistration registry)
	{
		if (!ConfigJEI.INSTANCE.combustionGeneratorRecipes.val) return;
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new RecipeCategoryCombustionGenerator(guiHelper));
	}

	public static void initialize(IModRegistry registry) {

		if (!ConfigJEI.INSTANCE.combustionGeneratorRecipes.val) return;
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), ID);
		NonNullList<ItemStack> stacks = NonNullList.create();
		stacks.add(new ItemStack(MechanimationItems.GENERATOR, 1, MechanimationBlocks.GENERATOR.getMetaFromState(MechanimationBlocks.GENERATOR.getDefaultState().withProperty(MechanimationBlocks.GENERATOR.getVariantProperty(), EnumVariant.COMBUSTION))));
		stacks.forEach(stack -> registry.addRecipeCatalyst(stack, ID));
	}

	public static List<RecipeWrapperCombustionGenerator> getRecipes(IGuiHelper guiHelper)
	{
		List<RecipeWrapperCombustionGenerator> recipes = new ArrayList<>();
		CombustionGeneratorRecipes.getRecipes().forEach(result -> {
			if (!result.fuel().isEmpty() && result.speed() > 0) recipes.add(new RecipeWrapperCombustionGenerator(guiHelper, result, ID));
		});
		return recipes;
	}

	public RecipeCategoryCombustionGenerator(IGuiHelper guiHelper)
	{
		this.background = guiHelper.createDrawable(COMBUSTION_GENERATOR_GUI_TEXTURES, 0, 0, 162, 62);
		localizedName = Translator.translate("container.mechanimation.combustion_generator");
	}

	@Override
	public String getUid()
	{
		return ID;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, RecipeWrapperCombustionGenerator recipeWrapper, IIngredients ingredients)
	{
		List<List<FluidOrGasStack>> inputFluids  = ingredients.getInputs (JEICompatPlugin.TYPE_FLUID);
		int maxVolume = 1;
		for (List<FluidOrGasStack> list : inputFluids ) for (FluidOrGasStack stack : list) if (stack != null && stack.getAmount() > maxVolume) maxVolume = stack.getAmount();

		IGuiIngredientGroup<FluidOrGasStack> guiGasStacks = recipeLayout.getIngredientsGroup(JEICompatPlugin.TYPE_FLUID);

		initFluidOrGas(guiGasStacks, inputFluids.get(0), 0, true, 78, 18, 6, 24, maxVolume, JEICompatPlugin.fluidMarker);
	}
}