package firemerald.mechanimation.util.crafting;

import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.init.MechanimationItems;
import firemerald.mechanimation.items.ItemCraftingMaterial;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class RecipeBannerPatternRemoval extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe
{
	public final ItemBanner banner;
	public final int damage;

	public RecipeBannerPatternRemoval(ItemBanner banner, int damage)
	{
		this.banner = banner;
		this.damage = damage;
		this.setRegistryName(new ResourceLocation(MechanimationAPI.MOD_ID, "banner_pattern_removal/" + banner.getRegistryName().getResourceDomain() + "/" + banner.getRegistryName().getResourcePath() + "/" + damage));
	}

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
	public boolean matches(InventoryCrafting inv, World worldIn)
    {
    	boolean hasBanner = false, hasSoap = false;
        for (int i = 0; i < inv.getSizeInventory(); ++i)
        {
            ItemStack itemstack1 = inv.getStackInSlot(i);
            if (!itemstack1.isEmpty())
            {
                if (itemstack1.getItem() == banner && itemstack1.getItemDamage() == damage)
                {
                    if (hasBanner || TileEntityBanner.getPatterns(itemstack1) <= 0) return false;
                    else hasBanner = true;
                }
                else
                {
                    if (hasSoap || itemstack1.getItem() != MechanimationItems.CRAFTING_MATERIAL || itemstack1.getItemDamage() != ItemCraftingMaterial.SOAP) return false;
                    else hasSoap = true;
                }
            }
        }
        return hasBanner && hasSoap;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    @Override
	public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        for (int k = 0; k < inv.getSizeInventory(); ++k)
        {
            ItemStack itemstack1 = inv.getStackInSlot(k);
            if (!itemstack1.isEmpty())
            {
                if (itemstack1.getItem() == banner && itemstack1.getItemDamage() == damage)
                {
                    ItemStack itemstack = itemstack1.copy();
                    itemstack.setCount(1);
                    TileEntityBanner.removeBannerData(itemstack);
                    //TODO playerIn.addStat(StatList.BANNER_CLEANED);
                    return itemstack;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
	public ItemStack getRecipeOutput()
    {
        return new ItemStack(banner, 1, damage);
    }

    @Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv)
    {
        NonNullList<ItemStack> nonnulllist = NonNullList.<ItemStack>withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        for (int i = 0; i < nonnulllist.size(); ++i)
        {
            ItemStack itemstack = inv.getStackInSlot(i);
            nonnulllist.set(i, net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack));
        }
        return nonnulllist;
    }

    @Override
	public boolean isDynamic()
    {
        return true;
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    @Override
	public boolean canFit(int width, int height)
    {
        return width * height >= 2;
    }

    @Override
	public NonNullList<Ingredient> getIngredients()
    {
    	NonNullList<Ingredient> list = NonNullList.<Ingredient>create();
    	ItemStack bannerStack = new ItemStack(banner, 1, damage);
    	NBTTagCompound stackTag = new NBTTagCompound();
    	NBTTagCompound displayTag = new NBTTagCompound();
    	NBTTagList loreList = new NBTTagList();
    	bannerStack.setTagCompound(stackTag);
    	stackTag.setTag("display", displayTag);
    	displayTag.setTag("Lore", loreList);
    	loreList.appendTag(new NBTTagString("any pattern"));
    	list.add(Ingredient.fromStacks(bannerStack));
    	list.add(Ingredient.fromStacks(new ItemStack(MechanimationItems.CRAFTING_MATERIAL, 1, ItemCraftingMaterial.SOAP)));
    	return list;
    }
}