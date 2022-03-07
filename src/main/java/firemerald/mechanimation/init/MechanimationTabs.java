package firemerald.mechanimation.init;

import firemerald.mechanimation.blocks.BlockOre;
import firemerald.mechanimation.items.ItemCraftingMaterial;
import firemerald.mechanimation.multipart.pipe.EnumPipeTier;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class MechanimationTabs
{
	public static final CreativeTabs MATERIALS = new CreativeTabs("mechanimation.materials")
	{
		@Override
		public ItemStack getTabIconItem()
		{
			return new ItemStack(MechanimationItems.CRAFTING_MATERIAL, 1, ItemCraftingMaterial.TITANIUM_INGOT);
		}
	};

	public static final CreativeTabs COMBAT = new CreativeTabs("mechanimation.combat")
	{
		@Override
		public ItemStack getTabIconItem()
		{
			return new ItemStack(MechanimationItems.CARBON_FIBER_CHESTPLATE, 1, 0);
		}
	};

	public static final CreativeTabs BLOCKS = new CreativeTabs("mechanimation.blocks")
	{
		@Override
		public ItemStack getTabIconItem()
		{
			return new ItemStack(MechanimationItems.ORE, 1, BlockOre.EnumVariant.TITANIUM.ordinal());
		}
	};

	public static final CreativeTabs DECORATIONS = new CreativeTabs("mechanimation.decorations")
	{
		@Override
		public ItemStack getTabIconItem()
		{
			return new ItemStack(MechanimationItems.TITANIUM_STAIRS, 1, 0);
		}
	};

	public static final CreativeTabs MACHINES = new CreativeTabs("mechanimation.machines")
	{
		@Override
		public ItemStack getTabIconItem()
		{
			return new ItemStack(MechanimationItems.PRESS, 1, 0);
		}
	};

	public static final CreativeTabs PIPES = new CreativeTabs("mechanimation.pipes")
	{
		@Override
		public ItemStack getTabIconItem()
		{
			return MechanimationItems.ITEM_PIPE.setTier(new ItemStack(MechanimationItems.ITEM_PIPE), EnumPipeTier.STONE);
		}
	};

	public static void init() {}
}