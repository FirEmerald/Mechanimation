package firemerald.mechanimation.util.crafting;

import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.init.MechanimationItems;
import firemerald.mechanimation.items.ItemCraftingMaterial;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class RecipeArmorDyeRemoval extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe
{
	public final ItemArmor armor;

	public RecipeArmorDyeRemoval(ItemArmor armor)
	{
		this.armor = armor;
		this.setRegistryName(new ResourceLocation(MechanimationAPI.MOD_ID, "armor_dye_removal/" + armor.getRegistryName().getResourceDomain() + "/" + armor.getRegistryName().getResourcePath()));
	}

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
	public boolean matches(InventoryCrafting inv, World worldIn)
    {
    	boolean hasArmor = false, hasSoap = false;
        for (int i = 0; i < inv.getSizeInventory(); ++i)
        {
            ItemStack itemstack1 = inv.getStackInSlot(i);
            if (!itemstack1.isEmpty())
            {
                if (itemstack1.getItem() == armor)
                {
                    if (hasArmor) return false;
                    else hasArmor = true;
                }
                else
                {
                    if (hasSoap || itemstack1.getItem() != MechanimationItems.CRAFTING_MATERIAL || itemstack1.getItemDamage() != ItemCraftingMaterial.SOAP) return false;
                    else hasSoap = true;
                }
            }
        }
        return hasArmor && hasSoap;
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
                if (itemstack1.getItem() == armor)
                {
                    ItemStack itemstack = itemstack1.copy();
                    itemstack.setCount(1);
                    armor.removeColor(itemstack);
                    //TODO playerIn.addStat(StatList.ARMOR_CLEANED);
                    return itemstack;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
	public ItemStack getRecipeOutput()
    {
        return new ItemStack(armor);
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
    	ItemStack armorStack = new ItemStack(armor);
    	NBTTagCompound stackTag = new NBTTagCompound();
    	NBTTagCompound displayTag = new NBTTagCompound();
    	NBTTagList loreList = new NBTTagList();
    	armorStack.setTagCompound(stackTag);
    	stackTag.setTag("display", displayTag);
    	displayTag.setTag("Lore", loreList);
    	loreList.appendTag(new NBTTagString("any dyed color"));
    	list.add(Ingredient.fromStacks(armorStack));
    	list.add(Ingredient.fromStacks(new ItemStack(MechanimationItems.CRAFTING_MATERIAL, 1, ItemCraftingMaterial.SOAP)));
    	return list;
    }
}