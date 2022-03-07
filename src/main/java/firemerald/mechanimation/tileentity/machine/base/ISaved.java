package firemerald.mechanimation.tileentity.machine.base;

import net.minecraft.nbt.NBTTagCompound;

public interface ISaved
{
	public void writeToShared(NBTTagCompound tag);

	public void readFromShared(NBTTagCompound tag);

	public void writeToDisk(NBTTagCompound tag);

	public void readFromDisk(NBTTagCompound tag);
}