package firemerald.mechanimation.tileentity.machine.base.fluids;

import firemerald.mechanimation.tileentity.machine.base.IGuiMachine;
import firemerald.mechanimation.tileentity.machine.base.RenderInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IFluidGuiMachine<T extends IFluidGuiMachine<T>> extends IGuiMachine<T>, IFluidMachine<T>
{
	@SideOnly(Side.CLIENT)
	public RenderInfo getFluidRenderInfo(int index);
}