package firemerald.mechanimation.compat.jei.transfer;

import java.util.Map.Entry;

import firemerald.mechanimation.inventory.container.ContainerMachine;
import firemerald.mechanimation.tileentity.machine.base.IGuiMachine;
import firemerald.mechanimation.tileentity.machine.base.items.IItemMachine;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.collect.Table;
import mezz.jei.startup.StackHelper;
import mezz.jei.util.ErrorUtil;
import net.minecraft.entity.player.EntityPlayer;

@SuppressWarnings("rawtypes")
public class MachineRecipeTransferHandler implements IRecipeTransferHandler<ContainerMachine>
{
	private final Table<String, Class<?>, IMachineRecipeTransferHandler<?>> recipeTransferHandlers = Table.hashBasedTable();
	private final StackHelper stackHelper;
	private final IRecipeTransferHandlerHelper handlerHelper;

	public MachineRecipeTransferHandler(StackHelper stackHelper, IRecipeTransferHandlerHelper handlerHelper)
	{
		this.stackHelper = stackHelper;
		this.handlerHelper = handlerHelper;
	}

	@Override
	public Class<ContainerMachine> getContainerClass()
	{
		return ContainerMachine.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IRecipeTransferError transferRecipe(ContainerMachine container, IRecipeLayout recipeLayout, EntityPlayer player, boolean maxTransfer, boolean doTransfer)
	{
		Entry<Class<?>, IMachineRecipeTransferHandler<?>> handlerEntry = recipeTransferHandlers.getRow(recipeLayout.getRecipeCategory().getUid()).entrySet().stream().filter(entry -> entry.getKey().isAssignableFrom(container.machine.getClass())).findFirst().orElse(null);
		if (handlerEntry == null) return handlerHelper.createInternalError();
		else return handlerEntry.getValue().transferRecipe(container, recipeLayout, player, maxTransfer, doTransfer);
	}

	public <T extends IItemMachine<T> & IGuiMachine<T>> void addRecipeTransferHandler(String recipeCategoryUid, Class<?> machineClass, int recipeStartOffset, int recipeEndOffset)
	{
		ErrorUtil.checkNotNull(machineClass, "machineClass");
		ErrorUtil.checkNotNull(recipeCategoryUid, "recipeCategoryUid");
		IMachineRecipeTransferInfo<T> recipeTransferHelper = new BasicMachineRecipeTransferInfo<>(recipeStartOffset, recipeEndOffset);
		addRecipeTransferHandler(recipeTransferHelper, recipeCategoryUid, machineClass);
	}

	public <T extends IItemMachine<T> & IGuiMachine<T>> void addRecipeTransferHandler(IMachineRecipeTransferInfo<T> recipeTransferInfo, String recipeCategoryUid, Class<?> machineClass)
	{
		ErrorUtil.checkNotNull(machineClass, "machineClass");
		ErrorUtil.checkNotNull(recipeCategoryUid, "recipeCategoryUid");
		ErrorUtil.checkNotNull(recipeTransferInfo, "recipeTransferInfo");
		IMachineRecipeTransferHandler recipeTransferHandler = new BasicMachineRecipeTransferHandler<>(stackHelper, handlerHelper, recipeTransferInfo);
		addRecipeTransferHandler(recipeTransferHandler, recipeCategoryUid, machineClass);
	}

	public void addRecipeTransferHandler(IMachineRecipeTransferHandler<?> recipeTransferHandler, String recipeCategoryUid, Class<?> machineClass)
	{
		ErrorUtil.checkNotNull(machineClass, "machineClass");
		ErrorUtil.checkNotNull(recipeCategoryUid, "recipeCategoryUid");
		ErrorUtil.checkNotNull(recipeTransferHandler, "recipeTransferHandler");
		this.recipeTransferHandlers.put(recipeCategoryUid, machineClass, recipeTransferHandler);
	}
}
