package firemerald.mechanimation.blocks;

import firemerald.mechanimation.client.renderer.IBlockHighlight;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ICustomBlockHighlight
{
	@SideOnly(Side.CLIENT)
	public IBlockHighlight getBlockHighlight(EntityPlayer player, RayTraceResult trace);
}