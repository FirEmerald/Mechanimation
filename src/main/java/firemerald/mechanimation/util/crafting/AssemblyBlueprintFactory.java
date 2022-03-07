package firemerald.mechanimation.util.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

import firemerald.craftloader.api.CraftLoaderAPI;
import firemerald.craftloader.api.CraftingUtil;
import firemerald.craftloader.api.ICraftingFactory;
import firemerald.craftloader.api.RecipeKey;
import firemerald.mechanimation.api.crafting.assembly_terminal.AssemblyBlueprint;
import firemerald.mechanimation.api.crafting.assembly_terminal.AssemblyRecipes;
import firemerald.mechanimation.api.crafting.assembly_terminal.IAssemblyBlueprint;
import firemerald.mechanimation.api.util.Rectangle;
import firemerald.mechanimation.api.util.Vec2i;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.JsonContext;

public class AssemblyBlueprintFactory implements ICraftingFactory<RecipeKey, IAssemblyBlueprint>
{
	@Override
	public IAssemblyBlueprint parse(RecipeKey key, JsonContext context, JsonObject obj)
	{
		Ingredient blueprint = CraftingUtil.getIngredient(CraftLoaderAPI.getJsonElement(obj, "blueprint"), context);
		ResourceLocation image = null;
		Rectangle imageBounds = null;
		if (obj.has("background"))
		{
			JsonElement el = obj.get("background");
			if (el.isJsonPrimitive())
			{
				JsonPrimitive prim = el.getAsJsonPrimitive();
				if (prim.isString())
				{
					image = new ResourceLocation(prim.getAsString());
					imageBounds = Rectangle.ofSize(0, 0, AssemblyRecipes.MAX_WIDTH, AssemblyRecipes.MAX_HEIGHT);
				}
				else throw new JsonSyntaxException("Invalid background, expected to find a string or JsonObject");
			}
			else if (el.isJsonObject())
			{
				JsonObject obj2 = el.getAsJsonObject();
				image = new ResourceLocation(JsonUtils.getString(obj2, "image"));
				int x = JsonUtils.getInt(obj2, "x", 0);
				if (x < 0) throw new JsonSyntaxException("x cannot be negative, got " + x);
				else if (x >= AssemblyRecipes.MAX_WIDTH) throw new JsonSyntaxException("x must be less than " + AssemblyRecipes.MAX_WIDTH + ", got " + x);
				int y = JsonUtils.getInt(obj2, "y", 0);
				if (y < 0) throw new JsonSyntaxException("y cannot be negative, got " + y);
				else if (y >= AssemblyRecipes.MAX_HEIGHT) throw new JsonSyntaxException("y must be less than " + AssemblyRecipes.MAX_HEIGHT + ", got " + y);
				int w = JsonUtils.getInt(obj2, "w", AssemblyRecipes.MAX_WIDTH - x);
				if (w <= 0) throw new JsonSyntaxException("w must be positive, got " + w);
				else if (w >= (AssemblyRecipes.MAX_WIDTH - x)) throw new JsonSyntaxException("w must be less than " + (AssemblyRecipes.MAX_WIDTH - x) + ", got " + w);
				int h = JsonUtils.getInt(obj2, "h", AssemblyRecipes.MAX_HEIGHT - y);
				if (h <= 0) throw new JsonSyntaxException("h must be positive, got " + h);
				else if (h >= (AssemblyRecipes.MAX_HEIGHT - y)) throw new JsonSyntaxException("h must be less than " + (AssemblyRecipes.MAX_HEIGHT - y) + ", got " + h);
				imageBounds = Rectangle.ofSize(x, y, w, h);
			}
			else throw new JsonSyntaxException("Invalid background, expected to find a string or JsonObject");
		}
		JsonArray inAr = JsonUtils.getJsonArray(obj, "input");
		if (inAr.size() == 0) throw new JsonSyntaxException("Invalid input, expected to find a JsonArray of JsonArray of 2 ints");
		Vec2i[] inputs = new Vec2i[inAr.size()];
		for (int i = 0; i < inAr.size(); i++)
		{
			JsonElement el = inAr.get(i);
			if (!el.isJsonArray()) throw new JsonSyntaxException("Invalid input, expected to find a JsonArray of JsonArray of 2 ints");
			else
			{
				JsonArray posAr = el.getAsJsonArray();
				if (posAr.size() != 2) throw new JsonSyntaxException("Invalid input, expected to find a JsonArray of JsonArray of 2 ints");
				JsonElement elx = posAr.get(0);
				if (!elx.isJsonPrimitive()) throw new JsonSyntaxException("Invalid input, expected to find a JsonArray of JsonArray of 2 ints");
				JsonPrimitive primx = elx.getAsJsonPrimitive();
				if (!primx.isNumber()) throw new JsonSyntaxException("Invalid input, expected to find a JsonArray of JsonArray of 2 ints");
				JsonElement ely = posAr.get(1);
				if (!ely.isJsonPrimitive()) throw new JsonSyntaxException("Invalid input, expected to find a JsonArray of JsonArray of 2 ints");
				JsonPrimitive primy = ely.getAsJsonPrimitive();
				if (!primy.isNumber()) throw new JsonSyntaxException("Invalid input, expected to find a JsonArray of JsonArray of 2 ints");
				inputs[i] = new Vec2i(primx.getAsInt(), primy.getAsInt());
			}
		}
		JsonArray outAr = JsonUtils.getJsonArray(obj, "output");
		if (inAr.size() != 2) throw new JsonSyntaxException("Invalid output, expected to find a JsonArray of 2 ints");
		JsonElement elx = outAr.get(0);
		if (!elx.isJsonPrimitive()) throw new JsonSyntaxException("Invalid input, expected to find a JsonArray of JsonArray of 2 ints");
		JsonPrimitive primx = elx.getAsJsonPrimitive();
		if (!primx.isNumber()) throw new JsonSyntaxException("Invalid input, expected to find a JsonArray of JsonArray of 2 ints");
		JsonElement ely = outAr.get(1);
		if (!ely.isJsonPrimitive()) throw new JsonSyntaxException("Invalid input, expected to find a JsonArray of JsonArray of 2 ints");
		JsonPrimitive primy = ely.getAsJsonPrimitive();
		if (!primy.isNumber()) throw new JsonSyntaxException("Invalid input, expected to find a JsonArray of JsonArray of 2 ints");
		Vec2i output = new Vec2i(primx.getAsInt(), primy.getAsInt());
		return new AssemblyBlueprint(key.name, blueprint, image, imageBounds, output, inputs);
	}
}
