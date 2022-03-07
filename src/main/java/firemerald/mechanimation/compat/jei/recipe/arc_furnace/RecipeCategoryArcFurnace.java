package firemerald.mechanimation.compat.jei.recipe.arc_furnace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import firemerald.api.core.client.Translator;
import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.arc_furnace.ArcFurnaceRecipes;
import firemerald.mechanimation.api.crafting.arc_furnace.IArcFurnaceRecipe;
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

public class RecipeCategoryArcFurnace extends RecipeCategoryBase<RecipeWrapperArcFurnace>
{
	public static final String ID = "mechanimation.arc_furnace";
    public static final ResourceLocation ARC_FURNACE_GUI_TEXTURES = new ResourceLocation(MechanimationAPI.MOD_ID, "textures/gui/jei/arc_furnace.png");

	public static void register(IRecipeCategoryRegistration registry)
	{
		if (!ConfigJEI.INSTANCE.arcFurnaceRecipes.val) return;
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new RecipeCategoryArcFurnace(guiHelper));
	}

	public static void initialize(IModRegistry registry)
	{
		if (!ConfigJEI.INSTANCE.arcFurnaceRecipes.val) return;
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), ID);
		NonNullList<ItemStack> stacks = NonNullList.create();
		MechanimationBlocks.BLAST_FURNACE.getSubBlocks(CreativeTabs.SEARCH, stacks);
		MechanimationBlocks.ARC_FURNACE.getSubBlocks(CreativeTabs.SEARCH, stacks);
		stacks.forEach(stack -> registry.addRecipeCatalyst(stack, ID));
	}

	public static List<RecipeWrapperArcFurnace> getRecipes(IGuiHelper guiHelper)
	{
		return ArcFurnaceRecipes.getRecipes().stream().flatMap(recipe -> getWrappers(guiHelper, recipe)).collect(Collectors.toList());
	}

	private static Stream<RecipeWrapperArcFurnace> getWrappers(IGuiHelper guiHelper, IArcFurnaceRecipe recipe)
	{
		if (!recipe.isToolMeltingRecipe()) return Collections.singletonList(new RecipeWrapperArcFurnace(guiHelper, recipe, ID)).stream();
		else
		{
			List<RecipeWrapperArcFurnace> list = new ArrayList<>();
			for (ItemStack input : recipe.getInput())
			{
				for (int i = 0; i < 4; i++)
				{
					final ItemStack stack = input.copy();
					stack.setItemDamage(stack.getMaxDamage() * i / 4);
					list.add(new RecipeWrapperArcFurnace(guiHelper, new IArcFurnaceRecipe() {
						@Override
						public ItemStack[] getInput()
						{
							return new ItemStack[] {stack};
						}

						@Override
						public Object getTrueInput()
						{
							return stack;
						}

						@Override
						public boolean isInputValid(ItemStack in)
						{
							return recipe.isInputValid(in);
						}

						@Override
						public int getRequiredCount(ItemStack in)
						{
							return recipe.getRequiredCount(in);
						}

						@Override
						public FluidOrGasStack getOutput()
						{
							return recipe.getOutput(stack);
						}

						@Override
						public int getTemperature()
						{
							return recipe.getTemperature(stack);
						}

						@Override
						public int getVolume()
						{
							return recipe.getVolume(stack);
						}

						@Override
						public boolean isToolMeltingRecipe()
						{
							return true;
						}
					}, ID));
				}
			}
			return list.stream();
		}
	}

	public RecipeCategoryArcFurnace(IGuiHelper guiHelper)
	{
		this.background = guiHelper.createDrawable(ARC_FURNACE_GUI_TEXTURES, 0, 0, 74, 45);
		localizedName = Translator.translate("container.mechanimation.arc_furnace");
	}

	@Override
	public String getUid()
	{
		return ID;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, RecipeWrapperArcFurnace recipeWrapper, IIngredients ingredients)
	{
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(0, true, 0, 14);
		guiItemStacks.set(ingredients);
		IGuiIngredientGroup<FluidOrGasStack> guiFluidStacks = recipeLayout.getIngredientsGroup(JEICompatPlugin.TYPE_FLUID);
		int maxVolume = 1;
		List<List<FluidOrGasStack>> outputFluids = ingredients.getOutputs(JEICompatPlugin.TYPE_FLUID);
		for (List<FluidOrGasStack> list : outputFluids) for (FluidOrGasStack stack : list) if (stack != null && stack.getAmount() > maxVolume) maxVolume = stack.getAmount();
		this.initFluidOrGas(guiFluidStacks, outputFluids.get(0), 0, false, 62, 1, 6, 24, maxVolume, JEICompatPlugin.fluidMarker);
	}
}