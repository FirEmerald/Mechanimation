package firemerald.mechanimation.util.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import firemerald.api.core.items.IItemSubtyped;
import firemerald.craftloader.api.CraftLoaderAPI;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;

public class VariantIngredientFactory implements IIngredientFactory
{
	@Override
	public Ingredient parse(JsonContext context, JsonObject json)
	{
		JsonElement element = CraftLoaderAPI.getJsonElement(json, "item");
		String str = element.getAsString();
		Item item = Item.getByNameOrId(str);
		if (item == null) throw new JsonSyntaxException("Item not found: " + str);
		if (!(item instanceof IItemSubtyped)) throw new JsonSyntaxException("Invalid item: " + str);
		element = CraftLoaderAPI.getJsonElement(json, "subtype");
		if (element == null) throw new JsonSyntaxException("Missing subtype");
		str = element.getAsString();
		int meta = ((IItemSubtyped) item).getMeta(str);
		if (meta < 0) throw new JsonSyntaxException("Invalid subtype: " + str + " for item: " + item.getRegistryName().toString());
		return Ingredient.fromStacks(new ItemStack(item, 1, meta));
	}
}