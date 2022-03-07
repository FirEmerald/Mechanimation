package firemerald.mechanimation.tileentity.machine.arc_furnace;

import firemerald.api.mcms.animation.IAnimation;
import firemerald.api.mcms.model.IModel;
import firemerald.mechanimation.init.MechanimationStats;
import firemerald.mechanimation.mcms.MCMSAnimation;
import firemerald.mechanimation.mcms.MCMSModel;
import firemerald.mechanimation.mcms.MCMSTexture;
import firemerald.mechanimation.util.EnumFace;
import net.minecraft.stats.StatBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

public class TileEntityArcFurnaceAdvanced extends TileEntityArcFurnaceBase<TileEntityArcFurnaceAdvanced>
{
	@Override
	public IModel<?, ?> getModel(float partial)
	{
		return MCMSModel.ARC_FURNACE_ADVANCED.model.get();
	}

	@Override
	public ResourceLocation getTexture()
	{
		return MCMSTexture.ARC_FURNACE_ADVANCED;
	}

	@Override
	public boolean useAnimation()
	{
		return true;
	}

	@Override
	public boolean shouldTickAnimation()
	{
		return this.isBurning ? this.runTime < MCMSAnimation.ARC_FURNACE_ADVANCED_RUNNING.animation.get().getLength() * 20 : this.runTime > 0;
	}

	@Override
	public IAnimation getRunningAnimation()
	{
		return MCMSAnimation.ARC_FURNACE_ADVANCED_RUNNING.animation.get();
	}

	@Override
    public float getAnimationSpeed()
    {
    	return this.isBurning ? 1 : -1;
    }

	@Override
	public StatBase getInteractionStat()
	{
		return MechanimationStats.ADVANCED_CATALYTIC_REFORMER_INTERACTION;
	}

	@Override
	public int getMaxEnergyUsedPerTick()
	{
		return 200;
	}

	@Override
	public float heatPerRF()
	{
		return 25;
	}

	@Override
	public int getFuelSurfaceArea()
	{
		return 64;
	}

	@Override
	public int getAmbientSurfaceArea()
	{
		return 256;
	}

	@Override
	public int getHeatConductorVolume()
	{
		return 500;
	}

	@Override
	public int getMaxFluid()
	{
		return 4 * Fluid.BUCKET_VOLUME;
	}

	@Override
	public int getMaxFluidTransfer()
	{
		return 400;
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
	public EnumFace[] itemInputSides()
	{
		return new EnumFace[] {EnumFace.TOP};
	}

	@Override
	public EnumFace[] fluidOutputSides()
	{
		return new EnumFace[] {EnumFace.BACK, EnumFace.LEFT, EnumFace.RIGHT};
	}
}