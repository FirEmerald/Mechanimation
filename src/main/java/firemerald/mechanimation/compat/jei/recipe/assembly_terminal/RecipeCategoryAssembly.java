package firemerald.mechanimation.compat.jei.recipe.assembly_terminal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import firemerald.api.core.client.Translator;
import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.api.crafting.assembly_terminal.AssemblyRecipes;
import firemerald.mechanimation.api.util.Vec2i;
import firemerald.mechanimation.compat.jei.ConfigJEI;
import firemerald.mechanimation.compat.jei.JEICompat;
import firemerald.mechanimation.compat.jei.JEICompatPlugin;
import firemerald.mechanimation.compat.jei.recipe.RecipeCategoryBase;
import firemerald.mechanimation.compat.jei.transfer.PacketAssemblyTerminalRecipeTransfer;
import firemerald.mechanimation.init.MechanimationBlocks;
import firemerald.mechanimation.inventory.container.ContainerMachine;
import firemerald.mechanimation.tileentity.machine.assembly_terminal.TileEntityAssemblyTerminalBase;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.config.ServerInfo;
import mezz.jei.startup.StackHelper;
import mezz.jei.transfer.RecipeTransferHandlerHelper;
import mezz.jei.util.Log;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class RecipeCategoryAssembly extends RecipeCategoryBase<RecipeWrapperAssembly>
{
	public static final String ID = "mechanimation.assembly";
    public static final ResourceLocation ASSEMBLY_TERMINAL_GUI_TEXTURES = new ResourceLocation(MechanimationAPI.MOD_ID, "textures/gui/jei/assembly_terminal.png");

	public static void register(IRecipeCategoryRegistration registry)
	{
		if (!ConfigJEI.INSTANCE.assemblyTerminalRecipes.val) return;
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new RecipeCategoryAssembly(guiHelper));
	}

	@SuppressWarnings("unchecked")
	public static <T extends TileEntityAssemblyTerminalBase<T>> void initialize(IModRegistry registry)
	{

		if (!ConfigJEI.INSTANCE.assemblyTerminalRecipes.val) return;
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
		registry.addRecipes(getRecipes(guiHelper), ID);
		NonNullList<ItemStack> stacks = NonNullList.create();
		MechanimationBlocks.ASSEMBLY_TERMINAL.getSubBlocks(CreativeTabs.SEARCH, stacks);
		stacks.forEach(stack -> registry.addRecipeCatalyst(stack, ID));
		StackHelper stackHelper = (StackHelper) jeiHelpers.getStackHelper();
		RecipeTransferHandlerHelper handlerHelper = (RecipeTransferHandlerHelper) jeiHelpers.recipeTransferHandlerHelper();
		JEICompatPlugin.machineRecipeHandler.addRecipeTransferHandler((ContainerMachine<T> container, IRecipeLayout recipeLayout, EntityPlayer player, boolean maxTransfer, boolean doTransfer) -> {
			if (!(container.machine instanceof TileEntityAssemblyTerminalBase)) return handlerHelper.createInternalError();
			if (!ServerInfo.isJeiOnServer())
			{
				String tooltipMessage = Translator.translate("jei.tooltip.error.recipe.transfer.no.server");
				return handlerHelper.createUserErrorWithTooltip(tooltipMessage);
			}

			IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();
			int numInputs = (int) itemStackGroup.getGuiIngredients().values().stream().filter(IGuiIngredient::isInput).count();

			int startIndex, endIndex, inputCount;
			Map<Integer, IGuiIngredient<ItemStack>> ingredientsMap = new HashMap<>();
			//if (stackHelper.containsStack(itemStackGroup.getGuiIngredients().get(0).getAllIngredients(), container.getSlot(container.index_max_hotbar + 2).getStack()) != null) //has the blueprint
			{
				startIndex = container.index_max_hotbar + 2;
				endIndex = startIndex + numInputs;
				inputCount = 0;
				for (Entry<Integer, ? extends IGuiIngredient<ItemStack>> entry : itemStackGroup.getGuiIngredients().entrySet())
				{
					Integer id = entry.getKey();
					//if (id != 0)
					{
						IGuiIngredient<ItemStack> ingredient = entry.getValue();
						if (ingredient.isInput())
						{
							if (!ingredient.getAllIngredients().isEmpty()) inputCount++;
							ingredientsMap.put(id, ingredient);
						}
					}
				}
			}
			//else
			//{
			//	startIndex = container.index_max_hotbar + 2;
			//	endIndex = container.index_max_hotbar + 3;
			//	inputCount = 1;
			//	ingredientsMap.put(0, itemStackGroup.getGuiIngredients().get(0));
			//}


			Map<Integer, ItemStack> availableItemStacks = new HashMap<>();
			int filledCraftSlotCount = 0;
			int emptySlotCount = 0;
			//do only for blueprint slot as all other slots are subject to be dumped
			{
				Slot slot = container.getSlot(startIndex);
				final ItemStack stack = slot.getStack();
				if (!stack.isEmpty())
				{
					if (!slot.canTakeStack(player))
					{
						Log.get().error("Assembly Terminal Recipe Transfer helper does not work for container. Player can't move item out of Crafting Slot number " + slot.slotNumber);
						return handlerHelper.createInternalError();
					}
					filledCraftSlotCount++;
					availableItemStacks.put(slot.slotNumber, stack.copy());
				}
			}

			for (int i = 0; i < container.index_max_hotbar; i++)
			{
				Slot slot = container.inventorySlots.get(i);
				final ItemStack stack = slot.getStack();
				if (!stack.isEmpty()) availableItemStacks.put(slot.slotNumber, stack.copy());
				else emptySlotCount++;
			}

			// check if we have enough inventory space to shuffle items around to their final locations
			if (filledCraftSlotCount - inputCount > emptySlotCount)
			{
				String message = Translator.translate("jei.tooltip.error.recipe.transfer.inventory.full");
				return handlerHelper.createUserErrorWithTooltip(message);
			}

			StackHelper.MatchingItemsResult matchingItemsResult = stackHelper.getMatchingItems(availableItemStacks, ingredientsMap);

			if (matchingItemsResult.missingItems.size() > 0)
			{
				String message = Translator.translate("jei.tooltip.error.recipe.transfer.missing");
				return handlerHelper.createUserErrorForSlots(message, matchingItemsResult.missingItems);
			}

			List<Integer> craftingSlotIndexes = new ArrayList<>(endIndex - startIndex - 1);
			for (int i = startIndex + 1; i < endIndex; i++) craftingSlotIndexes.add(i);

			List<Integer> inventorySlotIndexes = new ArrayList<>(container.index_max_hotbar);
			for (int i = 0; i < container.index_max_hotbar; i++) inventorySlotIndexes.add(i);

			if (doTransfer)
			{
				int blueprintMatch = matchingItemsResult.matchingItems.remove(0);
				int blueprintSlot = startIndex;
				Map<Integer, Integer> adjustedMatching = new HashMap<>(matchingItemsResult.matchingItems.size() - 1);
				matchingItemsResult.matchingItems.forEach((key, val) -> adjustedMatching.put(key - 1, val));
				PacketAssemblyTerminalRecipeTransfer packet = new PacketAssemblyTerminalRecipeTransfer(blueprintMatch, blueprintSlot, adjustedMatching, craftingSlotIndexes, inventorySlotIndexes, maxTransfer, true);
				JEICompat.INSTANCE.network.sendToServer(packet);
			}
			return null;
		}, ID, (Class<T>) (Object) TileEntityAssemblyTerminalBase.class);
	}

	public static List<RecipeWrapperAssembly> getRecipes(IGuiHelper guiHelper)
	{
		List<RecipeWrapperAssembly> recipes = new ArrayList<>();
		AssemblyRecipes.getBlueprints().forEach(blueprint -> {
			blueprint.getAllResults().forEach(recipe -> {
				if (recipe.getDefaultOutput() != null && !recipe.getDefaultOutput().isEmpty()) recipes.add(new RecipeWrapperAssembly(guiHelper, blueprint, recipe, ID));
			});
		});
		return recipes;
	}

	public static final int SLOTS_OFFSET_X = 18;
	public static final int SLOTS_OFFSET_Y = 0;

	public RecipeCategoryAssembly(IGuiHelper guiHelper)
	{
		this.background = guiHelper.createDrawable(ASSEMBLY_TERMINAL_GUI_TEXTURES, 0, 0, SLOTS_OFFSET_X + AssemblyRecipes.MAX_WIDTH, SLOTS_OFFSET_Y + AssemblyRecipes.MAX_HEIGHT);
		localizedName = Translator.translate("container.mechanimation.assembly_terminal");
	}

	@Override
	public String getUid()
	{
		return ID;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, RecipeWrapperAssembly recipeWrapper, IIngredients ingredients)
	{
		List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
		List<List<ItemStack>> outputs = ingredients.getOutputs(VanillaTypes.ITEM);

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		int offX = (168 - recipeWrapper.background.getWidth()) / 2;
		int offY = (130 - recipeWrapper.background.getHeight()) / 2;

		guiItemStacks.init(0, true, 0, 0);
		guiItemStacks.set(0, inputs.get(0));

		Vec2i[] inputs2 = recipeWrapper.blueprint.getInput(recipeWrapper.recipe.getDefaultBlueprint()[0], recipeWrapper.recipe.getDefaultTier());
		for (int i = 0; i < inputs2.length; i++)
		{
			guiItemStacks.init(i + 1, true, SLOTS_OFFSET_X - 1 + offX + inputs2[i].x, SLOTS_OFFSET_Y - 1 + offY + inputs2[i].y);
			guiItemStacks.set(i + 1, inputs.get(i + 1));
		}
		Vec2i outputLoc = recipeWrapper.blueprint.getOutputPosition(recipeWrapper.recipe.getDefaultBlueprint()[0], recipeWrapper.recipe.getDefaultTier());
		guiItemStacks.init(inputs2.length + 1, false, SLOTS_OFFSET_X - 1 + offX + outputLoc.x, SLOTS_OFFSET_Y - 1 + offY + outputLoc.y);
		guiItemStacks.set(inputs2.length + 1, outputs.get(0));
	}
}