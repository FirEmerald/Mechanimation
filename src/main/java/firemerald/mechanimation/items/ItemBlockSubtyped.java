package firemerald.mechanimation.items;

import firemerald.api.core.items.IItemSubtyped;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockSubtyped<B extends Block & IItemSubtyped> extends ItemBlock implements IItemSubtyped
{
	public final B block;

	public ItemBlockSubtyped(B block)
	{
		super(block);
		this.block = block;
        this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	@Override
	public String getSubtype(int meta)
	{
		return block.getSubtype(meta);
	}

	@Override
	public int getMeta(String subtype)
	{
		return block.getMeta(subtype);
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
}