package firemerald.mechanimation.util.crafting;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import firemerald.craftloader.api.CraftLoaderAPI;
import firemerald.craftloader.api.CraftingUtil;
import firemerald.craftloader.api.ICraftingFactory;
import firemerald.craftloader.api.RecipeKey;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.api.crafting.casting.CastingRecipe;
import firemerald.mechanimation.api.crafting.casting.EnumCastingType;
import firemerald.mechanimation.api.crafting.casting.ICastingRecipe;
import firemerald.mechanimation.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.JsonContext;

public class CastingRecipeFactory implements ICraftingFactory<RecipeKey, Pair<EnumCastingType, ICastingRecipe>>
{
	@Override
	public Pair<EnumCastingType, ICastingRecipe> parse(RecipeKey key, JsonContext context, JsonObject obj)
	{
		EnumCastingType type = Utils.getEnum(JsonUtils.getString(obj, "type"), EnumCastingType.values(), null);
		if (type == null) throw new JsonSyntaxException("Missing type, expected to find an enum from " + Arrays.toString(EnumCastingType.values()));
		List<FluidOrGasStack> input = CraftingLoader.getFluids(obj, "input");
		ItemStack output = CraftingUtil.getResult(CraftLoaderAPI.getJsonElement(obj, "output"), context);
		int temperature = JsonUtils.getInt(obj, "temperature");
		int cooledTemperature = JsonUtils.getInt(obj, "cooled_temperature", 340);
		return Pair.of(type, new CastingRecipe(input, output, temperature - 300, cooledTemperature - 300));
	}
}
