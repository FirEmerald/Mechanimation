package firemerald.mechanimation.items;

import firemerald.api.core.items.IItemSubtyped;
import firemerald.mechanimation.init.MechanimationTabs;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemExtractor extends Item implements IItemSubtyped
{
	public static final String NAME_FLUID = "fluid", NAME_ITEM = "item";
	public static final int ID_FLUID = 0, ID_ITEM = 1;

	public ItemExtractor()
	{
        this.setMaxDamage(0);
		this.setHasSubtypes(true);
		this.setCreativeTab(MechanimationTabs.PIPES);
	}

	@Override
	public String getSubtype(int meta)
	{
		switch (meta)
		{
		case ID_FLUID:
			return NAME_FLUID;
		case ID_ITEM:
			return NAME_ITEM;
		default:
			return "invalid";
		}
	}

	@Override
	public int getMeta(String subtype)
	{
		switch (subtype)
		{
		case NAME_FLUID:
			return ID_FLUID;
		case NAME_ITEM:
			return ID_ITEM;
		default:
			return Short.MAX_VALUE;
		}
	}

    @Override
	public int getMetadata(int damage)
    {
        return damage;
    }

    @Override
	public String getUnlocalizedName(ItemStack stack)
    {
        return super.getUnlocalizedName() + "." + this.getSubtype(stack.getItemDamage());
    }

	@Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (this.isInCreativeTab(tab))
        {
        	items.add(new ItemStack(this, 1, ID_ITEM));
        	items.add(new ItemStack(this, 1, ID_FLUID));
        }
    }
}