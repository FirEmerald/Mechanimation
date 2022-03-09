package firemerald.mechanimation.compat.forgemultipart;

import firemerald.mechanimation.compat.forgemultipart.pipe.EnumPipeTier;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class MultipartTabs
{
	public static final CreativeTabs PIPES = new CreativeTabs("mechanimation.pipes")
	{
		@Override
		public ItemStack getTabIconItem()
		{
			return MultipartItems.ITEM_PIPE.setTier(new ItemStack(MultipartItems.ITEM_PIPE), EnumPipeTier.STONE);
		}
	};

	public static void init() {}
}
