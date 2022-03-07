package firemerald.mechanimation.tileentity.machine.press;

import firemerald.api.mcms.animation.IAnimation;
import firemerald.api.mcms.model.IModel;
import firemerald.mechanimation.init.MechanimationStats;
import firemerald.mechanimation.mcms.MCMSAnimation;
import firemerald.mechanimation.mcms.MCMSModel;
import firemerald.mechanimation.mcms.MCMSTexture;
import firemerald.mechanimation.util.EnumFace;
import net.minecraft.stats.StatBase;
import net.minecraft.util.ResourceLocation;

public class TileEntityPressBasic extends TileEntityPressBase<TileEntityPressBasic>
{
	@Override
    public int getPressTime()
    {
    	return 240;
    }

	@Override
	public int getSoundTimestamp()
	{
		return 120;
	}

	@Override
    public int getOpenTime()
    {
    	return 30;
    }

	@Override
    public int getCloseTime()
    {
    	return 30;
    }

	@Override
    public int getMaxEnergyPerTick()
    {
    	return 100;
    }

	@Override
	public IModel<?, ?> getModel(float partial)
	{
		return MCMSModel.PRESS_BASIC.model.get();
	}

	@Override
	public ResourceLocation getTexture()
	{
		return MCMSTexture.PRESS_BASIC;
	}

	@Override
	public IAnimation getClosingAnimation()
	{
		return MCMSAnimation.PRESS_BASIC_CLOSING.animation.get();
	}

	@Override
	public IAnimation getRunAnimation()
	{
		return MCMSAnimation.PRESS_BASIC_RUNNING.animation.get();
	}

	@Override
	public IAnimation getOpeningAnimation()
	{
		return MCMSAnimation.PRESS_BASIC_OPENING.animation.get();
	}

	@Override
	public StatBase getInteractionStat()
	{
		return MechanimationStats.BASIC_PRESS_INTERACTION;
	}

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
}