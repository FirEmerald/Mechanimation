package firemerald.mechanimation.properties;

import firemerald.mechanimation.api.properties.PropertiesBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class ThermalTempsFluid extends PropertiesBase<Float>
{
	public static final ThermalTempsFluid INSTANCE = new ThermalTempsFluid();

	public ThermalTempsFluid()
	{
		super(0);
	}

	@Override
	public Float getProperty(IBlockAccess world, BlockPos pos, IBlockState blockState, TileEntity tile)
	{
		Fluid fluid = FluidRegistry.lookupFluidForBlock(blockState.getBlock());
		if (fluid != null) return Float.valueOf(fluid.getTemperature() - 273);
		else return null;
	}
}