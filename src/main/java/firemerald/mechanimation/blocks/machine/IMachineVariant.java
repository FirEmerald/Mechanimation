package firemerald.mechanimation.blocks.machine;

import firemerald.mechanimation.tileentity.machine.base.IMachine;
import net.minecraft.block.material.MapColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;

public interface IMachineVariant<T extends TileEntity & IMachine> extends IStringSerializable
{
	public T newTile();

	public MapColor getColor();

	public float getHardness();

	public float getExplosionResistance();
}