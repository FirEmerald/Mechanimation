package firemerald.mechanimation.compat.jei.recipe.casting;

import java.util.ArrayList;
import java.util.List;

import firemerald.api.core.client.Translator;
import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.casting.CastingRecipes;
import firemerald.mechanimation.api.crafting.casting.EnumCastingType;
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
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class RecipeCategoryCasting extends RecipeCategoryBase<RecipeWrapperCasting>
{
	public static final String ID = "mechanimation.casting_table";
    public static final ResourceLocation CASTING_GUI_TEXTURES = new ResourceLocation(MechanimationAPI.MOD_ID, "textures/gui/jei/casting_table.png");

	public static void register(IRecipeCategoryRegistration registry)
	{
		if (!ConfigJEI.INSTANCE.castingRecipes.val) return;
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new RecipeCategoryCasting(guiHelper));
	}

	public static void initialize(IModRegistry registry)
	{
		if (!ConfigJEI.INSTANCE.castingRecipes.val) return;
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), ID);
		NonNullList<ItemStack> stacks = NonNullList.create();
		MechanimationBlocks.CASTING_TABLE.getSubBlocks(CreativeTabs.SEARCH, stacks);
		stacks.forEach(stack -> registry.addRecipeCatalyst(stack, ID));
	}

	public static List<RecipeWrapperCasting> getRecipes(IGuiHelper guiHelper)
	{
		List<RecipeWrapperCasting> list = new ArrayList<>();
		for (EnumCastingType type : EnumCastingType.values()) CastingRecipes.getRecipes(type).stream().map(recipe -> new RecipeWrapperCasting(guiHelper, type, recipe, ID)).forEach(list::add);
		return list;
	}

	public RecipeCategoryCasting(IGuiHelper guiHelper)
	{
		this.background = guiHelper.createDrawable(CASTING_GUI_TEXTURES, 0, 0, 74, 57);
		localizedName = Translator.translate("container.mechanimation.casting_table");
	}

	@Override
	public String getUid()
	{
		return ID;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, RecipeWrapperCasting recipeWrapper, IIngredients ingredients)
	{
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(0, false, 56, 14);
		guiItemStacks.set(ingredients);
		IGuiIngredientGroup<FluidOrGasStack> guiFluidStacks = recipeLayout.getIngredientsGroup(JEICompatPlugin.TYPE_FLUID);
		int maxVolume = 1;
		List<List<FluidOrGasStack>> inputFluids = ingredients.getInputs(JEICompatPlugin.TYPE_FLUID);
		for (List<FluidOrGasStack> list : inputFluids) for (FluidOrGasStack stack : list) if (stack != null && stack.getAmount() > maxVolume) maxVolume = stack.getAmount();
		this.initFluidOrGas(guiFluidStacks, inputFluids.get(0), 0, false, 6, 1, 6, 24, maxVolume, JEICompatPlugin.fluidMarker);
	}
}