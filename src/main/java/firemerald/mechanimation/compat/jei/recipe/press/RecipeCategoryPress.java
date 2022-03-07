package firemerald.mechanimation.compat.jei.recipe.press;

import java.util.List;
import java.util.stream.Collectors;

import firemerald.api.core.client.Translator;
import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.api.crafting.press.PressRecipes;
import firemerald.mechanimation.compat.jei.ConfigJEI;
import firemerald.mechanimation.compat.jei.JEICompatPlugin;
import firemerald.mechanimation.compat.jei.recipe.RecipeCategoryBase;
import firemerald.mechanimation.init.MechanimationBlocks;
import firemerald.mechanimation.tileentity.machine.press.TileEntityPressBase;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class RecipeCategoryPress extends RecipeCategoryBase<RecipeWrapperPress>
{
	public static final String ID = "mechanimation.press";
    public static final ResourceLocation PRESS_GUI_TEXTURES = new ResourceLocation(MechanimationAPI.MOD_ID, "textures/gui/jei/press.png");

	public static void register(IRecipeCategoryRegistration registry)
	{
		if (!ConfigJEI.INSTANCE.pressRecipes.val) return;
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new RecipeCategoryPress(guiHelper));
	}

	public static void initialize(IModRegistry registry) {

		if (!ConfigJEI.INSTANCE.pressRecipes.val) return;
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), ID);
		NonNullList<ItemStack> stacks = NonNullList.create();
		MechanimationBlocks.PRESS.getSubBlocks(CreativeTabs.SEARCH, stacks);
		stacks.forEach(stack -> registry.addRecipeCatalyst(stack, ID));

		JEICompatPlugin.machineRecipeHandler.addRecipeTransferHandler(ID, TileEntityPressBase.class, 2, 4);
	}

	public static List<RecipeWrapperPress> getRecipes(IGuiHelper guiHelper)
	{
		return PressRecipes.getRecipes().stream().filter(PressRecipes::isCraftable).map(recipe -> new RecipeWrapperPress(guiHelper, recipe, ID)).collect(Collectors.toList());
	}

	public RecipeCategoryPress(IGuiHelper guiHelper)
	{
		this.background = guiHelper.createDrawable(PRESS_GUI_TEXTURES, 0, 0, 130, 62);
		localizedName = Translator.translate("container.mechanimation.press");
	}

	@Override
	public String getUid()
	{
		return ID;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, RecipeWrapperPress recipeWrapper, IIngredients ingredients)
	{

		List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
		List<List<ItemStack>> outputs = ingredients.getOutputs(VanillaTypes.ITEM);

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiItemStacks.init(0, false, 108, 26);
		guiItemStacks.init(1, true, 45, 26);

		guiItemStacks.set(0, outputs.get(0));
		guiItemStacks.set(1, inputs.get(0));
	}
}