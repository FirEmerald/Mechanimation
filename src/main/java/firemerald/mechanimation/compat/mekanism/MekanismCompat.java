package firemerald.mechanimation.compat.mekanism;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import firemerald.api.core.IFMLEventHandler;
import firemerald.craftloader.api.CraftingReloadEvent;
import firemerald.mechanimation.api.crafting.electrolyzer.ElectrolyzerRecipes;
import firemerald.mechanimation.init.MechanimationFluids;
import firemerald.mechanimation.util.crafting.OreDictUtil;
import firemerald.mechanimation.util.crafting.OreDictUtil.OreDict;
import mekanism.common.MekanismFluids;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

public class MekanismCompat implements IFMLEventHandler
{
	public static Logger logger = LogManager.getLogger("Mechanimation Mekanism Compat");

	@Override
    public void onPreInitialization(FMLPreInitializationEvent event)
    {
	    MinecraftForge.EVENT_BUS.register(this);
    }

	@Override
	public void onPostInitialization(FMLPostInitializationEvent event)
	{
		MechanimationFluids.brine = MekanismFluids.Brine.getFluid();
		MechanimationFluids.hydrogen = MekanismFluids.Hydrogen;
		MechanimationFluids.oxygen = MekanismFluids.Oxygen;
		MechanimationFluids.sodium = MekanismFluids.Sodium;
		MechanimationFluids.chlorine = MekanismFluids.Chlorine;

		ElectrolyzerRecipes.registerRecipes(new MekanismElectrolyzerRecipes());

		//TODO mekanism electrolyzer from electrolyzer
		//TODO chemical infuser to/from fluid reactor
		//TODO chemical washer to/from fluid reactor
	}

	@SubscribeEvent
	public void onCraftingReload(CraftingReloadEvent.Post event)
	{
		logger.debug("Generating automatic ore-dictionary combiner, enrichment chamber, and crusher recipes");
    	OreDictUtil.MATERIALS.forEach((name, entries) -> {
			OreDict ingot = entries.get(OreDictUtil.EnumMaterialType.INGOT);
			OreDict gem = entries.get(OreDictUtil.EnumMaterialType.GEM);
	        OreDict dust = entries.get(OreDictUtil.EnumMaterialType.DUST);
	        OreDict ore = entries.get(OreDictUtil.EnumMaterialType.ORE);
			if (gem != null)
			{
		        if (ore != null)
		        {
		        	for (ItemStack oreStack : OreDictionary.getOres(ore.name)) RecipeHandler.addEnrichmentChamberRecipe(StackUtils.size(oreStack, 1), StackUtils.size(gem.item, 2));
		        }
		        if (dust != null)
		        {
		        	for (ItemStack dustStack : OreDictionary.getOres(dust.name)) RecipeHandler.addEnrichmentChamberRecipe(StackUtils.size(dustStack, 1), StackUtils.size(gem.item, 1));
		        	for (ItemStack gemStack : OreDictionary.getOres(gem.name)) RecipeHandler.addCrusherRecipe(StackUtils.size(gemStack, 1), StackUtils.size(dust.item, 1));
		        }
			}
			else if (ingot != null)
			{
		        if (dust != null) for (ItemStack dustStack : OreDictionary.getOres(dust.name)) RecipeHandler.addEnrichmentChamberRecipe(StackUtils.size(dustStack, 1), StackUtils.size(ingot.item, 1));
			}
			if (ingot != null)
			{
		        if (dust != null) for (ItemStack ingotStack : OreDictionary.getOres(ingot.name)) RecipeHandler.addCrusherRecipe(StackUtils.size(ingotStack, 1), StackUtils.size(dust.item, 1));
			}
    	});
	}
}