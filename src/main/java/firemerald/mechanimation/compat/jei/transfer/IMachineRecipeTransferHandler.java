package firemerald.mechanimation.compat.jei.transfer;

import firemerald.mechanimation.inventory.container.ContainerMachine;
import firemerald.mechanimation.tileentity.machine.base.IGuiMachine;
import firemerald.mechanimation.tileentity.machine.base.items.IItemMachine;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import net.minecraft.entity.player.EntityPlayer;

@FunctionalInterface
public interface IMachineRecipeTransferHandler<T extends IItemMachine<T> & IGuiMachine<T>>
{
	public IRecipeTransferError transferRecipe(ContainerMachine<T> container, IRecipeLayout recipeLayout, EntityPlayer player, boolean maxTransfer, boolean doTransfer);
}