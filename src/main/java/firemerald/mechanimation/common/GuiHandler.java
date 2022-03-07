package firemerald.mechanimation.common;

import firemerald.mechanimation.client.gui.inventory.GuiMachine;
import firemerald.mechanimation.inventory.container.ContainerMachine;
import firemerald.mechanimation.tileentity.machine.base.IGuiMachine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{
	public static final int
	ID_MACHINE = 0;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Container getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		switch (id)
		{
		case ID_MACHINE:
		{
			TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
			return tile instanceof IGuiMachine ? new ContainerMachine(player.inventory, (IGuiMachine) tile) : null;
		}
		default: return null;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		switch (id)
		{
		case ID_MACHINE:
		{
			TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
			return tile instanceof IGuiMachine ? new GuiMachine(player.inventory, (IGuiMachine) tile) : null;
		}
		default: return null;
		}
	}
}