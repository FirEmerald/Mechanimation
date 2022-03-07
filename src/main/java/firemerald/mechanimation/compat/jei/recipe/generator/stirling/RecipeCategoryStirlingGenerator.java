package firemerald.mechanimation.compat.jei.recipe.generator.stirling;

import java.util.ArrayList;
import java.util.List;

import firemerald.api.core.client.Translator;
import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.blocks.machine.BlockGenerator.EnumVariant;
import firemerald.mechanimation.compat.jei.ConfigJEI;
import firemerald.mechanimation.compat.jei.recipe.RecipeCategoryBase;
import firemerald.mechanimation.init.MechanimationBlocks;
import firemerald.mechanimation.init.MechanimationItems;
import it.unimi.dsi.fastutil.ints.Int2BooleanArrayMap;
import it.unimi.dsi.fastutil.ints.Int2BooleanMap;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

public class RecipeCategoryStirlingGenerator extends RecipeCategoryBase<RecipeWrapperStirlingGenerator>
{
	public static final String ID = "mechanimation.stirling_generator";
    public static final ResourceLocation STIRLING_GENERATOR_GUI_TEXTURES = new ResourceLocation(MechanimationAPI.MOD_ID, "textures/gui/jei/stirling_generator.png");

	public static void register(IRecipeCategoryRegistration registry)
	{
		if (!ConfigJEI.INSTANCE.stirlingGeneratorRecipes.val) return;
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new RecipeCategoryStirlingGenerator(guiHelper));
	}

	public static void initialize(IModRegistry registry)
	{
		if (!ConfigJEI.INSTANCE.stirlingGeneratorRecipes.val) return;

		registry.addRecipes(getRecipes(registry), ID);
		NonNullList<ItemStack> stacks = NonNullList.create();
		stacks.add(new ItemStack(MechanimationItems.GENERATOR, 1, MechanimationBlocks.GENERATOR.getMetaFromState(MechanimationBlocks.GENERATOR.getDefaultState().withProperty(MechanimationBlocks.GENERATOR.getVariantProperty(), EnumVariant.STIRLING))));
		stacks.forEach(stack -> registry.addRecipeCatalyst(stack, ID));
	}

	public static List<RecipeWrapperStirlingGenerator> getRecipes(IModRegistry registry)
	{
		IJeiHelpers helpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = helpers.getGuiHelper();
		IStackHelper stackHelper = helpers.getStackHelper();
		List<ItemStack> fuelStacks = registry.getIngredientRegistry().getFuels();
		Int2BooleanMap oreIdsHaveRecipe = new Int2BooleanArrayMap();
		List<RecipeWrapperStirlingGenerator> fuelRecipes = new ArrayList<>(fuelStacks.size());
		for (ItemStack fuelStack : fuelStacks)
		{
			int burnTime = TileEntityFurnace.getItemBurnTime(fuelStack);
			List<ItemStack> subtypes = stackHelper.getSubtypes(fuelStack);
			List<ItemStack> fuels = new ArrayList<>();
			for (ItemStack subtype : subtypes) if (TileEntityFurnace.getItemBurnTime(subtype) == burnTime) fuels.add(subtype);
			if (fuels.isEmpty()) fuels.add(fuelStack);
			if (fuels.size() <= 1)
			{
				int[] oreIDs = OreDictionary.getOreIDs(fuelStack);
				boolean hasOreRecipe = false;
				for (int oreId : oreIDs)
				{
					if (!oreIdsHaveRecipe.containsKey(oreId))
					{
						String oreName = OreDictionary.getOreName(oreId);
						List<ItemStack> ores = stackHelper.getAllSubtypes(OreDictionary.getOres(oreName));
						if (ores.size() > 1 && ores.stream().allMatch(itemStack -> TileEntityFurnace.getItemBurnTime(itemStack) == burnTime))
						{
							oreIdsHaveRecipe.put(oreId, true);
							fuelRecipes.add(new RecipeWrapperStirlingGenerator(guiHelper, ores, burnTime, ID));
						}
						else oreIdsHaveRecipe.put(oreId, false);
					}
					hasOreRecipe |= oreIdsHaveRecipe.get(oreId);
				}
				if (!hasOreRecipe) fuelRecipes.add(new RecipeWrapperStirlingGenerator(guiHelper, fuels, burnTime, ID));
			}
			else fuelRecipes.add(new RecipeWrapperStirlingGenerator(guiHelper, fuels, burnTime, ID));
		}
		return fuelRecipes;
	}

	public RecipeCategoryStirlingGenerator(IGuiHelper guiHelper)
	{
		this.background = guiHelper.createDrawable(STIRLING_GENERATOR_GUI_TEXTURES, 0, 0, 162, 62);
		localizedName = Translator.translate("container.mechanimation.stirling_generator");
	}

	@Override
	public String getUid()
	{
		return ID;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, RecipeWrapperStirlingGenerator recipeWrapper, IIngredients ingredients)
	{
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(0, true, 72, 44);
		guiItemStacks.set(ingredients);
	}
}