package firemerald.mechanimation.properties;

import java.util.Map;

import firemerald.mechanimation.api.properties.PropertiesBase;
import firemerald.mechanimation.config.CommonConfig;
import firemerald.mechanimation.util.BlockStateEntry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class ThermalTempsConfig extends PropertiesBase<Float>
{
	public static final ThermalTempsConfig INSTANCE = new ThermalTempsConfig();

    final BlockStateEntry tester = new BlockStateEntry();

	public ThermalTempsConfig()
	{
		super(0);
	}

	@Override
	public synchronized Float getProperty(IBlockAccess world, BlockPos pos, IBlockState blockState, TileEntity tile)
	{
		tester.set(blockState);
		Map<BlockStateEntry, Float> map = CommonConfig.INSTANCE.thermalBlocks.val.get(blockState.getBlock());
		return map == null ? null : map.get(tester);
	}
}