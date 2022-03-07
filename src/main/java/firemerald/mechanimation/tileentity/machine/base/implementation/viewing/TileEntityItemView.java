package firemerald.mechanimation.tileentity.machine.base.implementation.viewing;

import javax.annotation.Nullable;

import firemerald.mechanimation.tileentity.machine.base.items.IItemInventory;
import firemerald.mechanimation.tileentity.machine.base.items.IItemMachine;
import firemerald.mechanimation.tileentity.machine.base.items.ItemCapWrapper;
import firemerald.mechanimation.tileentity.machine.base.items.ItemView;
import firemerald.mechanimation.util.EnumFace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public abstract class TileEntityItemView<T extends IItemMachine<T>> extends TileEntityViewBase<T> implements IItemMachine<T>
{
	@SuppressWarnings("unchecked")
	private final IItemHandler[] facedItemHandlers = new IItemHandler[] {
			new ItemCapWrapper<>((T) this, EnumFace.values()[0]),
			new ItemCapWrapper<>((T) this, EnumFace.values()[1]),
			new ItemCapWrapper<>((T) this, EnumFace.values()[2]),
			new ItemCapWrapper<>((T) this, EnumFace.values()[3]),
			new ItemCapWrapper<>((T) this, EnumFace.values()[4]),
			new ItemCapWrapper<>((T) this, EnumFace.values()[5])
	};
	public final ItemView<T> itemView = new ItemView<>();

	@Override
	public void setNeedsUpdate() {}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player)
	{
		return false;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public int getField(int id)
	{
		return 0;
	}

	@Override
	public void setField(int id, int value) {}

	@Override
	public int getFieldCount()
	{
		return 0;
	}

	@Override
	public void clear() {}

	@Override
	public String getName()
	{
		return "fluidView";
	}

	@Override
	public boolean hasCustomName()
	{
		return false;
	}

	@Override
	public IItemInventory<T> getItemInventory()
	{
		updateViewed();
		return itemView;
	}

	@Override
    public boolean hasCapabilityLocal(Capability<?> capability, @Nullable EnumFacing facing)
    {
    	if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
    	{
    		if (facing == null) return getItemInventory().getSlots() > 0;
    		else return getItemInventory().getSlots(getFace(facing)).length > 0;
    	}
    	else return super.hasCapabilityLocal(capability, facing);
    }

	@Override
    @SuppressWarnings("unchecked")
	public <S> S getCapabilityLocal(Capability<S> capability, @Nullable EnumFacing facing)
    {
    	if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
    	{
    		if (facing == null) return (S) this.getItemInventory();
    		else return (S) facedItemHandlers[getFace(facing).ordinal()];
    	}
    	else return super.getCapabilityLocal(capability, facing);
    }

	@Override
	public void onViewChanged()
	{
		itemView.viewing = viewedMachine == null ? null : viewedMachine.getItemInventory();
	}
}