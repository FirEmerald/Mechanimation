package firemerald.mechanimation.util.crafting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.api.crafting.assembly_terminal.IAssemblyBlueprint;
import firemerald.mechanimation.api.crafting.assembly_terminal.IAssemblyRecipe;
import firemerald.mechanimation.api.util.Rectangle;
import firemerald.mechanimation.api.util.Vec2i;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreIngredient;

public class CraftingTableBlueprint implements IAssemblyBlueprint
{
	public static final Ingredient BLUEPRINTS = new OreIngredient("workbench");
	public static final ResourceLocation ID = new ResourceLocation(MechanimationAPI.MOD_ID, "builtin/crafting_table");
	public static final ResourceLocation GUI_TEX = new ResourceLocation("minecraft", "textures/gui/container/crafting_table.png");
	public static final Rectangle IMAGE_BOUNDS = Rectangle.ofSize(29, 16, 116, 54);
	public static final Vec2i[] INPUTS = new Vec2i[] {
			new Vec2i(1, 1), new Vec2i(19, 1), new Vec2i(37, 1),
			new Vec2i(1, 19), new Vec2i(19, 19), new Vec2i(37, 19),
			new Vec2i(1, 37), new Vec2i(19, 37), new Vec2i(37, 37)
	};
	public static final Vec2i OUTPUT = new Vec2i(95, 19);

	@Override
	public void init() {}

	@Override
	public Collection<? extends IAssemblyRecipe> getAllResults()
	{
		List<CraftingTableAssemblyRecipe> recipes = new ArrayList<>();
		CraftingManager.REGISTRY.forEach(recipe -> {
			if (recipe.canFit(3, 3)) recipes.add(new CraftingTableAssemblyRecipe(CraftingManager.REGISTRY.getNameForObject(recipe), recipe));
		});
		return recipes;
	}

	@Override
	public ResourceLocation getUniqueName()
	{
		return ID;
	}

	@Override
	public ItemStack[] getDefaultBlueprint()
	{
		return BLUEPRINTS.getMatchingStacks();
	}

	@Override
	public int getDefaultTier()
	{
		return 0;
	}

	@Override
	public IAssemblyRecipe getRecipe(ResourceLocation name)
	{
		IRecipe recipe = CraftingManager.REGISTRY.getObject(name);
		return recipe == null ? null : new CraftingTableAssemblyRecipe(name, recipe);
	}

	@Override
	public int getMaxUsedInputs()
	{
		return 9;
	}

	@Override
	public Vec2i[] getInput(ItemStack blueprint, int tier)
	{
		return INPUTS;
	}

	@Override
	public ResourceLocation blueprintImage(ItemStack blueprint, int tier)
	{
		return GUI_TEX;
	}

	@Override
	public Rectangle getBlueprintImageBounds(ItemStack blueprint, int tier)
	{
		return IMAGE_BOUNDS;
	}

	@Override
	public boolean isBlueprintValid(ItemStack blueprint, int tier)
	{
		return BLUEPRINTS.test(blueprint);
	}

	@Override
	public Vec2i getOutputPosition(ItemStack blueprint, int tier)
	{
		return OUTPUT;
	}

	@Override
	public String toString()
	{
		return "Crafting Assembly Blueprint ID " + getUniqueName();
	}
}