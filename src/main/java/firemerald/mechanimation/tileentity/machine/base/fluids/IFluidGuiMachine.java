package firemerald.mechanimation.tileentity.machine.base.fluids;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.tileentity.machine.base.IGuiMachine;
import firemerald.mechanimation.tileentity.machine.base.RenderInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IFluidGuiMachine<T extends IFluidGuiMachine<T>> extends IGuiMachine<T>, IFluidMachine<T>
{
	@SideOnly(Side.CLIENT)
	public RenderInfo getFluidRenderInfo(int index);

	@Override
    @SideOnly(Side.CLIENT)
    public default Object getIngredient(int adjX, int adjY)
    {
    	for (int i = 0; i < this.getFluidInventory().numSlots(); ++i)
    	{
    		RenderInfo renderInfo = getFluidRenderInfo(i);
    		if (renderInfo != null)
    		{
    			if (adjX >= renderInfo.x && adjX < renderInfo.x2 && adjY >= renderInfo.y && adjY < renderInfo.y2)
    			{
    				FluidOrGasStack stack = this.getFluidInventory().getFluidOrGas(i);
    				if (stack != null) return stack;
    			}
    		}
    	}
    	return null;
    }
}