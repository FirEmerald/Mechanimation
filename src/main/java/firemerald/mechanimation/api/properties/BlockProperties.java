package firemerald.mechanimation.api.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockProperties
{
	protected static final List<PropertiesBase<Float>> THERMAL_TEMPS = new ArrayList<>();

	public static void registerThermalTemps(PropertiesBase<Float> temps)
	{
		THERMAL_TEMPS.add(temps);
		Collections.sort(THERMAL_TEMPS);
	}

	public static void unregisterThermalTemps(PropertiesBase<Float> temps)
	{
		THERMAL_TEMPS.remove(temps);
	}

	public static List<PropertiesBase<Float>> getAllThermalTemps()
	{
		return Collections.unmodifiableList(THERMAL_TEMPS);
	}

	public static Float getThermalTemp(IBlockAccess world, BlockPos pos, IBlockState blockState, TileEntity tile)
	{
		return THERMAL_TEMPS.stream().map(temps -> temps.getProperty(world, pos, blockState, tile)).filter(Objects::nonNull).findFirst().orElse(null);
	}
}