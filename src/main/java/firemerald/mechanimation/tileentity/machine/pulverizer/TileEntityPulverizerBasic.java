package firemerald.mechanimation.tileentity.machine.pulverizer;

import firemerald.api.mcms.animation.IAnimation;
import firemerald.api.mcms.model.IModel;
import firemerald.mechanimation.init.MechanimationStats;
import firemerald.mechanimation.mcms.MCMSAnimation;
import firemerald.mechanimation.mcms.MCMSModel;
import firemerald.mechanimation.mcms.MCMSTexture;
import firemerald.mechanimation.util.EnumFace;
import net.minecraft.stats.StatBase;
import net.minecraft.util.ResourceLocation;

public class TileEntityPulverizerBasic extends TileEntityPulverizerBase<TileEntityPulverizerBasic>
{
	@Override
	public int getMaxEnergy()
	{
		return 10000;
	}

	@Override
	public int getMaxEnergyTransfer()
	{
		return 100;
	}

	@Override
	public int getMaxEnergyTransferItem()
	{
		return 100;
	}

	@Override
	public EnumFace[] getInputFaces()
	{
		return new EnumFace[] {EnumFace.TOP, EnumFace.FRONT};
	}

	@Override
	public EnumFace[] getOutputFaces()
	{
		return new EnumFace[] {EnumFace.BOTTOM, EnumFace.BACK};
	}

	@Override
    public int getPulverizeTime()
    {
    	return 100;
    }

	@Override
	public int getSoundTimestamp()
	{
		return 60;
	}

	@Override
    public int getMaxEnergyPerTick()
    {
    	return 100;
    }

	@Override
	public IModel<?, ?> getModel(float partial)
	{
		return MCMSModel.PULVERIZER_BASIC.model.get();
	}

	@Override
	public ResourceLocation getTexture()
	{
		return MCMSTexture.PULVERIZER_BASIC;
	}

	@Override
	public IAnimation getRunningAnimation()
	{
		return MCMSAnimation.PULVERIZER_BASIC_RUNNING.animation.get();
	}

	@Override
	public StatBase getInteractionStat()
	{
		return MechanimationStats.BASIC_PULVERIZER_INTERACTION;
	}
}