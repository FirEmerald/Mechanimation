package firemerald.mechanimation.tileentity.machine.hydrotreater;

import firemerald.api.mcms.animation.IAnimation;
import firemerald.api.mcms.model.IModel;
import firemerald.mechanimation.init.MechanimationStats;
import firemerald.mechanimation.mcms.MCMSAnimation;
import firemerald.mechanimation.mcms.MCMSModel;
import firemerald.mechanimation.mcms.MCMSTexture;
import net.minecraft.stats.StatBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

public class TileEntityHydrotreaterAdvanced extends TileEntityHydrotreater<TileEntityHydrotreaterAdvanced>
{
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

	@Override
	public int getMaxEnergy()
	{
		return 20000;
	}

	@Override
	public int getMaxEnergyTransfer()
	{
		return 200;
	}

	@Override
	public int getMaxEnergyTransferItem()
	{
		return 200;
	}

	@Override
	public IModel<?, ?> getModel(float partial)
	{
		return MCMSModel.HYDROTREATER_ADVANCED.model.get();
	}

	@Override
	public ResourceLocation getTexture()
	{
		return MCMSTexture.HYDROTREATER_ADVANCED;
	}

	@Override
    public int getMaxEnergyUsagePerTick()
    {
    	return 200;
    }

	@Override
	public IAnimation getRunningAnimation()
	{
		return MCMSAnimation.HYDROTREATER_ADVANCED_RUNNING.animation.get();
	}

	@Override
	public StatBase getInteractionStat()
	{
		return MechanimationStats.ADVANCED_HYDROTREATER_INTERACTION;
	}
}