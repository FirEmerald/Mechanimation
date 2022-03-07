package firemerald.mechanimation.util.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import firemerald.craftloader.api.CraftLoaderAPI;
import firemerald.mechanimation.multipart.pipe.EnumPipeTier;
import firemerald.mechanimation.multipart.pipe.ItemPartPipe;
import firemerald.mechanimation.util.Utils;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;

public class PipeIngredientFactory implements IIngredientFactory
{
	@Override
	public Ingredient parse(JsonContext context, JsonObject json)
	{
		JsonElement element = CraftLoaderAPI.getJsonElement(json, "item");
		String str = element.getAsString();
		Item item = Item.getByNameOrId(str);
		if (item == null) throw new JsonSyntaxException("Item not found: " + str);
		if (!(item instanceof ItemPartPipe)) throw new JsonSyntaxException("Invalid item: " + str);
		element = CraftLoaderAPI.getJsonElement(json, "tier");
		str = element.getAsString();
		EnumPipeTier tier = Utils.getEnum(str, EnumPipeTier.values(), null);
		if (tier == null) throw new JsonSyntaxException("Invalid or missing tier " + str);
		element = json.get("color");
		if (element == null) throw new JsonSyntaxException("Missing color (use \"none\" for no color or \"any\" for any color)");
		else
		{
			str = element.getAsString();
			if (str.equalsIgnoreCase("any"))
			{
				ItemStack[] stacks = new ItemStack[17];
				for (int i = 0; i <= 16; i++) stacks[i] = ((ItemPartPipe) item).setTier(new ItemStack(item, 1, i), tier);
				return Ingredient.fromStacks(stacks);
			}
			else
			{
				int meta;
				if (str.equalsIgnoreCase("none")) meta = 0;
				else
				{
					EnumDyeColor color = Utils.getEnum(str, EnumDyeColor.values(), null);
					if (color == null) throw new JsonSyntaxException("Invalid color " + str);
					meta = color.ordinal() + 1;
				}
				return Ingredient.fromStacks(((ItemPartPipe) item).setTier(new ItemStack(item, 1, meta), tier));
			}
		}
	}
}