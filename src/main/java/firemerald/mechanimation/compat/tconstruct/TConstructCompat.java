package firemerald.mechanimation.compat.tconstruct;

import java.util.List;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import firemerald.api.core.IFMLEventHandler;
import firemerald.mechanimation.api.crafting.arc_furnace.ArcFurnaceRecipes;
import firemerald.mechanimation.api.crafting.casting.CastingRecipes;
import firemerald.mechanimation.api.crafting.casting.EnumCastingType;
import firemerald.mechanimation.api.crafting.fluid_reactor.FluidReactorRecipes;
import firemerald.mechanimation.init.MechanimationBlocks;
import firemerald.mechanimation.init.MechanimationFluids;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.client.MaterialRenderInfo;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.smeltery.ICastingRecipe;
import slimeknights.tconstruct.shared.TinkerFluids;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

public class TConstructCompat implements IFMLEventHandler
{
	public static final TConstructCompat INSTANCE = new TConstructCompat();
	public static Logger logger = LogManager.getLogger("Mechanimation TConstruct Compat");

	@SubscribeEvent
	public void onItemRegistry(RegistryEvent.Register<Item> event)
	{
		MechanimationBlocks.liquidIron = (MechanimationFluids.liquidIron = TinkerFluids.iron).getBlock();
		MechanimationBlocks.liquidGold = (MechanimationFluids.liquidGold = TinkerFluids.gold).getBlock();
		MechanimationBlocks.liquidNickel = (MechanimationFluids.liquidNickel = TinkerFluids.nickel).getBlock();
		MechanimationBlocks.liquidCopper = (MechanimationFluids.liquidCopper = TinkerFluids.copper).getBlock();
		MechanimationBlocks.liquidAluminum = (MechanimationFluids.liquidAluminum = TinkerFluids.aluminum).getBlock();
		MechanimationBlocks.liquidTin = (MechanimationFluids.liquidTin = TinkerFluids.tin).getBlock();
		MechanimationBlocks.liquidSilver = (MechanimationFluids.liquidSilver = TinkerFluids.silver).getBlock();
		MechanimationBlocks.liquidSteel = (MechanimationFluids.liquidSteel = TinkerFluids.steel).getBlock();

		TinkerRegistry.registerMelting("dustRedstone", MechanimationFluids.LIQUID_REDSTONE, 144);
		TinkerRegistry.registerMelting("blockRedstone", MechanimationFluids.LIQUID_REDSTONE, 1296);
		TinkerRegistry.registerBasinCasting(new ItemStack(Blocks.REDSTONE_BLOCK), ItemStack.EMPTY, MechanimationFluids.LIQUID_REDSTONE, 1296);
	}

	@Override
    public void onPreInitialization(FMLPreInitializationEvent event)
    {
	    MinecraftForge.EVENT_BUS.register(this);
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("fluid", MechanimationFluids.LIQUID_REDSTONE.getName());
		tag.setString("ore", "Redstone");
		tag.setBoolean("toolforge", true);
		FMLInterModComms.sendMessage("tconstruct", "integrateSmeltery", tag);

		tag = new NBTTagCompound();
		tag.setString("fluid", MechanimationFluids.LIQUID_TUNGSTEN.getName());
		tag.setString("ore", "Tungsten");
		tag.setBoolean("toolforge", true);
		FMLInterModComms.sendMessage("tconstruct", "integrateSmeltery", tag);

		tag = new NBTTagCompound();
		tag.setString("fluid", MechanimationFluids.LIQUID_TITANIUM.getName());
		tag.setString("ore", "Titanium");
		tag.setBoolean("toolforge", true);
		FMLInterModComms.sendMessage("tconstruct", "integrateSmeltery", tag);
	}

	@Override
	public void onPostInitialization(FMLPostInitializationEvent event)
	{
		FluidReactorRecipes.registerRecipes(new FluidReactorRecipesAlloy());
		ArcFurnaceRecipes.registerRecipes(new ArcFurnaceRecipesSmeltery());
		Supplier<List<ICastingRecipe>> tableRecipes = () -> TinkerRegistry.getAllTableCastingRecipes();
		CastingRecipes.registerRecipes(EnumCastingType.INGOT, new CastingRecipesCasting(tableRecipes, TinkerSmeltery.castIngot));
		CastingRecipes.registerRecipes(EnumCastingType.GEM, new CastingRecipesCasting(tableRecipes, TinkerSmeltery.castGem));
		CastingRecipes.registerRecipes(EnumCastingType.NUGGET, new CastingRecipesCasting(tableRecipes, TinkerSmeltery.castNugget));
		CastingRecipes.registerRecipes(EnumCastingType.PLATE, new CastingRecipesCasting(tableRecipes, TinkerSmeltery.castPlate));
		CastingRecipes.registerRecipes(EnumCastingType.BLOCK, new CastingRecipesCasting(() -> TinkerRegistry.getAllBasinCastingRecipes(), ItemStack.EMPTY));
	}

	@SideOnly(Side.CLIENT)
	public static class RenderHelper
	{
		public static void addRenderInfo(Material mat, ResourceLocation icon)
		{
			mat.setRenderInfo(new MaterialRenderInfo.BlockTexture(icon));
		}
	}
}