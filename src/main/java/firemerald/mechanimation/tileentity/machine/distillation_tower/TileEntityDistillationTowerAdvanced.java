package firemerald.mechanimation.tileentity.machine.distillation_tower;

import firemerald.api.mcms.animation.IAnimation;
import firemerald.api.mcms.model.IModel;
import firemerald.mechanimation.init.MechanimationStats;
import firemerald.mechanimation.mcms.MCMSModel;
import firemerald.mechanimation.mcms.MCMSTexture;
import net.minecraft.stats.StatBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

public class TileEntityDistillationTowerAdvanced extends TileEntityDistillationTower<TileEntityDistillationTowerAdvanced>
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
		return MCMSModel.DISTILLATION_TOWER_ADVANCED.model.get();
	}

	@Override
	public ResourceLocation getTexture()
	{
		return MCMSTexture.DISTILLATION_TOWER_ADVANCED;
	}

	@Override
	public int getMaxEnergyUsagePerTick()
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
		return MechanimationStats.ADVANCED_DISTILLATION_TOWER_INTERACTION;
	}
}