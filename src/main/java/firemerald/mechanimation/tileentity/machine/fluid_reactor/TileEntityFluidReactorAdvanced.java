package firemerald.mechanimation.tileentity.machine.fluid_reactor;

import firemerald.mechanimation.init.MechanimationStats;
import net.minecraft.stats.StatBase;
import net.minecraftforge.fluids.Fluid;

public class TileEntityFluidReactorAdvanced extends TileEntityFluidReactorBase<TileEntityFluidReactorAdvanced>
{
	public static final int TANK_VOLUME = 2 * Fluid.BUCKET_VOLUME;

	@Override
	public StatBase getInteractionStat()
	{
		return MechanimationStats.ADVANCED_FLUID_REACTOR_INTERACTION;
	}

	@Override
	public int getMaxFluid()
	{
		return 2 * Fluid.BUCKET_VOLUME;
	}

	@Override
	public int getMaxFluidTransfer()
	{
		return 200;
	}

	@Override
	public int getMaxFluidTransferItem()
	{
		return Fluid.BUCKET_VOLUME;
	}
}