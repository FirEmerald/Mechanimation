package firemerald.mechanimation.tileentity.machine.base;

import firemerald.mechanimation.util.EnumFace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IMachine
{
	public EnumFace getFace(EnumFacing side);

	public EnumFacing getFacing(EnumFace face);

	public World getTheWorld();

	public BlockPos getThePos();

	public void setNeedsUpdate();
}