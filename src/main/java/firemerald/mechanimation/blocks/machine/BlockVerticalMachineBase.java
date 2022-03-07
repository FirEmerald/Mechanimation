package firemerald.mechanimation.blocks.machine;

import firemerald.mechanimation.client.renderer.IBlockHighlight;
import firemerald.mechanimation.tileentity.machine.base.implementation.actual.TileEntityMachineBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockVerticalMachineBase<T extends TileEntityMachineBase<?, ?>, E extends Enum<E> & IMachineVariant<T>> extends BlockMachineBase<T, E>
{
	@Override
	@SideOnly(Side.CLIENT)
	public IBlockHighlight getBlockHighlight(EntityPlayer player, RayTraceResult trace)
	{
		return IBlockHighlight.MACHINE_VERTICAL;
	}
}