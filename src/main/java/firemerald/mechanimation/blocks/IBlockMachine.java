package firemerald.mechanimation.blocks;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IBlockMachine
{
	@SideOnly(Side.CLIENT)
	public TileEntity getToRender(ItemStack stack);

	public String getName(ItemStack stack);
}