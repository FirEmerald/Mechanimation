package firemerald.mechanimation.tileentity.machine.base.energy;

import firemerald.mechanimation.tileentity.machine.base.IGuiMachine;
import firemerald.mechanimation.tileentity.machine.base.RenderInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IEnergyGuiMachine<T extends IEnergyGuiMachine<T>> extends IGuiMachine<T>, IEnergyMachine<T>
{
	@SideOnly(Side.CLIENT)
	public RenderInfo getEnergyRenderInfo(int index);
}