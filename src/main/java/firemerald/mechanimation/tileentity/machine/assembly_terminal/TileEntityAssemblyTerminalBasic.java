package firemerald.mechanimation.tileentity.machine.assembly_terminal;

import firemerald.api.mcms.animation.IAnimation;
import firemerald.api.mcms.model.IModel;
import firemerald.mechanimation.init.MechanimationStats;
import firemerald.mechanimation.mcms.MCMSModel;
import firemerald.mechanimation.mcms.MCMSTexture;
import net.minecraft.stats.StatBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityAssemblyTerminalBasic extends TileEntityAssemblyTerminalBase<TileEntityAssemblyTerminalBasic>
{
	@Override
	public int getMaxEnergy()
	{
		return 1000;
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
	@SideOnly(Side.CLIENT)
	public IModel<?, ?> getModel(float partial)
	{
		return MCMSModel.ASSEMBLY_TABLE_BASIC.model.get();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getTexture()
	{
		return MCMSTexture.ASSEMBLY_TABLE_BASIC;
	}

	@Override
	public int getTier()
	{
		return 0;
	}

	@Override
	public IAnimation getRunningAnimation()
	{
		return null;
	}

	@Override
	public StatBase getInteractionStat()
	{
		return MechanimationStats.BASIC_ASSEMBLY_TERMINAL_INTERACTION;
	}

	@Override
	public int getMaxEnergyPerTick()
	{
		return 100;
	}
}