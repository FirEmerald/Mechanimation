package firemerald.mechanimation.tileentity.machine.desalter;

import firemerald.api.mcms.animation.IAnimation;
import firemerald.api.mcms.model.IModel;
import firemerald.mechanimation.init.MechanimationStats;
import firemerald.mechanimation.mcms.MCMSModel;
import firemerald.mechanimation.mcms.MCMSTexture;
import net.minecraft.stats.StatBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

public class TileEntityDesalterAdvanced extends TileEntityDesalter<TileEntityDesalterAdvanced>
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
	public IModel<?, ?> getModel(float partial)
	{
		return MCMSModel.DESALTER_ADVANCED.model.get();
	}

	@Override
	public ResourceLocation getTexture()
	{
		return MCMSTexture.DESALTER_ADVANCED;
	}

	@Override
    public int getMaxWaterUsagePerTick()
    {
    	return 100;
    }

	@Override
	public IAnimation getRunningAnimation()
	{
		return null;
	}

	@Override
	public StatBase getInteractionStat()
	{
		return MechanimationStats.ADVANCED_DESALTER_INTERACTION;
	}
}