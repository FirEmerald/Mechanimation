package firemerald.mechanimation.compat.thermalexpansion;

import java.util.ArrayList;
import java.util.List;

import cofh.thermalexpansion.util.managers.machine.CompactorManager;
import cofh.thermalexpansion.util.managers.machine.CompactorManager.CompactorRecipe;
import cofh.thermalexpansion.util.managers.machine.CompactorManager.Mode;
import cofh.thermalexpansion.util.managers.machine.CrucibleManager;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager.PulverizerRecipe;
import firemerald.api.core.IFMLEventHandler;
import firemerald.craftloader.api.CraftingReloadEvent;
import firemerald.mechanimation.api.crafting.press.PressRecipes;
import firemerald.mechanimation.api.crafting.pulverizer.PulverizerRecipes;
import firemerald.mechanimation.blocks.BlockMetal;
import firemerald.mechanimation.blocks.BlockOre;
import firemerald.mechanimation.init.MechanimationBlocks;
import firemerald.mechanimation.init.MechanimationFluids;
import firemerald.mechanimation.init.MechanimationItems;
import firemerald.mechanimation.items.ItemCraftingMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ThermalExpansionCompat implements IFMLEventHandler
{
	public static final ThermalExpansionCompat INSTANCE = new ThermalExpansionCompat();

	@Override
	public void onInitialization(FMLInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void onPostInitialization(FMLPostInitializationEvent event)
	{
		CrucibleManager.addRecipe(2000 , new ItemStack(MechanimationItems.CRAFTING_MATERIAL, 1, ItemCraftingMaterial.TUNGSTEN_DUST), new FluidStack(MechanimationFluids.LIQUID_TUNGSTEN, 144));
		CrucibleManager.addRecipe(500  , new ItemStack(MechanimationItems.CRAFTING_MATERIAL, 1, ItemCraftingMaterial.TUNGSTEN_NUGGET), new FluidStack(MechanimationFluids.LIQUID_TUNGSTEN, 16));
		CrucibleManager.addRecipe(4000 , new ItemStack(MechanimationItems.CRAFTING_MATERIAL, 1, ItemCraftingMaterial.TUNGSTEN_INGOT), new FluidStack(MechanimationFluids.LIQUID_TUNGSTEN, 144));
		CrucibleManager.addRecipe(32000, new ItemStack(MechanimationItems.METAL_ALLOY_BLOCK, 1, BlockMetal.EnumVariant.TUNGSTEN.ordinal()), new FluidStack(MechanimationFluids.LIQUID_TUNGSTEN, 1296));

		CrucibleManager.addRecipe(2000 , new ItemStack(MechanimationItems.CRAFTING_MATERIAL, 1, ItemCraftingMaterial.TITANIUM_DUST), new FluidStack(MechanimationFluids.LIQUID_TITANIUM, 144));
		CrucibleManager.addRecipe(500  , new ItemStack(MechanimationItems.CRAFTING_MATERIAL, 1, ItemCraftingMaterial.TITANIUM_NUGGET), new FluidStack(MechanimationFluids.LIQUID_TITANIUM, 16));
		CrucibleManager.addRecipe(4000 , new ItemStack(MechanimationItems.CRAFTING_MATERIAL, 1, ItemCraftingMaterial.TITANIUM_INGOT), new FluidStack(MechanimationFluids.LIQUID_TITANIUM, 144));
		CrucibleManager.addRecipe(32000, new ItemStack(MechanimationItems.METAL_ALLOY_BLOCK, 1, BlockMetal.EnumVariant.TITANIUM.ordinal()), new FluidStack(MechanimationFluids.LIQUID_TITANIUM, 1296));

		PulverizerManager.addRecipe(4000, new ItemStack(MechanimationBlocks.ORE, 1, BlockOre.EnumVariant.TUNGSTEN.ordinal()), new ItemStack(MechanimationItems.CRAFTING_MATERIAL, 2, ItemCraftingMaterial.TUNGSTEN_DUST));
		PulverizerManager.addRecipe(4000, new ItemStack(MechanimationBlocks.ORE, 1, BlockOre.EnumVariant.TITANIUM.ordinal()), new ItemStack(MechanimationItems.CRAFTING_MATERIAL, 2, ItemCraftingMaterial.TITANIUM_DUST));

		PulverizerManager.addRecipe(2000, new ItemStack(MechanimationItems.CRAFTING_MATERIAL, 1, ItemCraftingMaterial.TUNGSTEN_INGOT), new ItemStack(MechanimationItems.CRAFTING_MATERIAL, 1, ItemCraftingMaterial.TUNGSTEN_DUST));
		PulverizerManager.addRecipe(2000, new ItemStack(MechanimationItems.CRAFTING_MATERIAL, 1, ItemCraftingMaterial.TITANIUM_INGOT), new ItemStack(MechanimationItems.CRAFTING_MATERIAL, 1, ItemCraftingMaterial.TITANIUM_DUST));
		PulverizerManager.addRecipe(2000, new ItemStack(MechanimationItems.CRAFTING_MATERIAL, 1, ItemCraftingMaterial.STEEL_INGOT), new ItemStack(MechanimationItems.CRAFTING_MATERIAL, 1, ItemCraftingMaterial.STEEL_DUST));

		//ItemStack emptyBottle = new ItemStack(Items.GLASS_BOTTLE);
        /*
		TransposerManager.addFillRecipe(400, emptyBottle, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER), new FluidStack(FluidRegistry.WATER, MechanimationItems.FILLED_BOTTLE.getCapacity()), true);
		for (Fluid fluid : FluidRegistry.getRegisteredFluids().values()) if (MechanimationItems.FILLED_BOTTLE.isFluidApplicable(fluid))
        {
    		TransposerManager.addFillRecipe(400, emptyBottle, MechanimationItems.FILLED_BOTTLE.getForFluid(fluid), new FluidStack(fluid, MechanimationItems.FILLED_BOTTLE.getCapacity()), true);
        }
        */
		//TODO centrifugal seperator

        PressRecipes.registerRecipes(new PressRecipesCompactor());
        PulverizerRecipes.registerRecipes(new PulverizerRecipesPulverizer());
	}

	protected final List<CompactorRecipe> addedCompactor = new ArrayList<>();
	protected final List<PulverizerRecipe> addedPulverizer = new ArrayList<>();

	@SubscribeEvent
	public void onCraftingReloadPre(CraftingReloadEvent.Pre event)
	{
		//remove all added press recipes
		addedCompactor.forEach(recipe -> {
			if (recipe != null && CompactorManager.getRecipe(recipe.getInput(), Mode.PLATE) == recipe) //prevent removal of replaced recipes
			{
				CompactorManager.removeRecipe(recipe.getInput(), Mode.PLATE);
			}
		});
		addedCompactor.clear();
		addedPulverizer.forEach(recipe -> {
			if (recipe != null && PulverizerManager.getRecipe(recipe.getInput()) == recipe) //prevent removal of replaced recipes
			{
				PulverizerManager.removeRecipe(recipe.getInput());
			}
		});
		addedPulverizer.clear();
	}

	@SubscribeEvent
	public void onCraftingReloadPost(CraftingReloadEvent.Pre event)
	{
		PressRecipes.getRecipes().forEach(recipe -> {
			ItemStack[] inputs = recipe.getInput();
			if (inputs.length > 0)
			{
				for (ItemStack input : inputs)
				{
					if (input.getCount() == 1 && CompactorManager.getRecipe(input, Mode.PLATE) == null) //prevent replacing existing recipes
					{
						addedCompactor.add(CompactorManager.addRecipe(recipe.getRequiredEnergy(), input, recipe.getOutput(), Mode.PLATE));
					}
				}
			}
		});
		PulverizerRecipes.getRecipes().forEach(recipe -> {
			ItemStack[] inputs = recipe.getInput();
			if (inputs.length > 0)
			{
				for (ItemStack input : inputs)
				{
					if (input.getCount() == 1 && PulverizerManager.getRecipe(input) == null) //prevent replacing existing recipes
					{
						addedPulverizer.add(PulverizerManager.addRecipe(recipe.getRequiredEnergy(), input, recipe.getOutput()));
					}
				}
			}
		});
	}
}