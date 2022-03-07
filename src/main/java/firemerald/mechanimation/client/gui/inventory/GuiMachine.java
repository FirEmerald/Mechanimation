package firemerald.mechanimation.client.gui.inventory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import firemerald.api.betterscreens.GuiBetterContainer;
import firemerald.api.core.client.Translator;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.client.gui.GuiUtils;
import firemerald.mechanimation.compat.jei.CompatProviderJEI;
import firemerald.mechanimation.compat.jei.JEICompatPlugin;
import firemerald.mechanimation.inventory.container.ContainerMachine;
import firemerald.mechanimation.tileentity.machine.base.IGuiMachine;
import firemerald.mechanimation.tileentity.machine.base.RenderInfo;
import firemerald.mechanimation.tileentity.machine.base.energy.IEnergyGuiMachine;
import firemerald.mechanimation.tileentity.machine.base.energy.IEnergySlot;
import firemerald.mechanimation.tileentity.machine.base.fluids.IFluidGuiMachine;
import mekanism.api.gas.GasStack;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiMachine<T extends IGuiMachine<T>> extends GuiBetterContainer implements IIngredientGui, IGuiElements
{
    private final InventoryPlayer playerInventory;
    private final T machine;
    private int displayTick = 0;
    private final Map<Integer, List<ItemStack>> previewStacks = new HashMap<>();

	public GuiMachine(InventoryPlayer playerInv, T machine)
    {
        super(new ContainerMachine<>(playerInv, machine));
        this.playerInventory = playerInv;
        this.machine = machine;
        this.xSize = machine.inventorySizeX();
        this.ySize = machine.inventorySizeY();
    	machine.setPreview(previewStacks);
    }

    @Override
    public void initGui()
    {
    	super.initGui();
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
    	machine.onGuiInit(this, i, j);
    }

    @Override
    public void updateScreen()
    {
    	super.updateScreen();
    	displayTick++;
    	machine.updatePreview(previewStacks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
    	if (CompatProviderJEI.INSTANCE.isPresent())
    	{
    		if (JEICompatPlugin.jeiRuntime != null)
    		{
                int i = (this.width - this.xSize) / 2;
                int j = (this.height - this.ySize) / 2;
                int adjX = mouseX - i;
                int adjY = mouseY - j;
            	String[] recipes = machine.getRecipeTypes(adjX, adjY);
            	if (recipes != null && recipes.length > 0)
            	{
            		JEICompatPlugin.jeiRuntime.getRecipesGui().showCategories(Arrays.asList(recipes));
            		return;
            	}
    		}
    	}
    	super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        int adjX = mouseX - i;
        int adjY = mouseY - j;
        machine.onGuiDrawn(this, i, j, adjX, adjY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void renderHoveredToolTip(int mouseX, int mouseY)
    {
    	if (CompatProviderJEI.INSTANCE.isPresent())
    	{
            int i = (this.width - this.xSize) / 2;
            int j = (this.height - this.ySize) / 2;
            int adjX = mouseX - i;
            int adjY = mouseY - j;
        	String[] recipes = machine.getRecipeTypes(adjX, adjY);
        	if (recipes != null && recipes.length > 0)
        	{
        		if (JEICompatPlugin.jeiRuntime != null)
        		{
            		this.drawHoveringText(Arrays.asList(new String[] {Translator.translate("jei.tooltip.show.recipes")}), mouseX, mouseY, fontRenderer);
            		return;
        		}
        		else
        		{
            		this.drawHoveringText(Arrays.asList(new String[] {Translator.translate("jei.tooltip.missing_runtime")}), mouseX, mouseY, fontRenderer);
            		return;
        		}
        	}
    	}
    	Object ingredient = this.getIngredient(mouseX, mouseY);
    	if (ingredient instanceof FluidOrGasStack)
    	{
    		FluidOrGasStack fluid = (FluidOrGasStack) ingredient;
    		this.drawHoveringText(Arrays.asList(new String[] {fluid.getLocalizedName(), Translator.format("gui.fluid.amount", fluid.getAmount())}), mouseX, mouseY, fontRenderer);
    	}
    	else if (ingredient instanceof FluidStack)
    	{
    		FluidStack fluid = (FluidStack) ingredient;
    		this.drawHoveringText(Arrays.asList(new String[] {fluid.getLocalizedName(), Translator.format("gui.fluid.amount", fluid.amount)}), mouseX, mouseY, fontRenderer);
    	}
    	else if (ingredient instanceof GasStack)
    	{
    		GasStack gas = (GasStack) ingredient;
    		this.drawHoveringText(Arrays.asList(new String[] {gas.getGas().getLocalizedName(), Translator.format("gui.fluid.amount", gas.amount)}), mouseX, mouseY, fontRenderer);
    	}
        if (machine instanceof IEnergyGuiMachine)
        {
            int i = (this.width - this.xSize) / 2;
            int j = (this.height - this.ySize) / 2;
            int adjX = mouseX - i;
            int adjY = mouseY - j;
            IEnergyGuiMachine<?> storage = (IEnergyGuiMachine<?>) machine;
            for (int k = 0; k < storage.getEnergyInventory().numSlots(); ++k)
        	{
        		RenderInfo dims = storage.getEnergyRenderInfo(k);
        		if (dims != null)
        		{
        			if (adjX >= dims.x && adjX < dims.x2 && adjY >= dims.y && adjY < dims.y2)
        			{
        	    		this.drawHoveringText(Arrays.asList(new String[] {Translator.format("gui.mechanimation.energy", storage.getEnergyInventory().getEnergySlot(k).getEnergyStored())}), mouseX, mouseY, fontRenderer);
        	    		return;
        			}
        		}
        	}
        }
    	super.renderHoveredToolTip(mouseX, mouseY);
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String s = this.machine.getDisplayName().getUnformattedText();
        this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
        this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    @Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(machine.getGuiTexture());
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        int adjX = mouseX - i;
        int adjY = mouseY - j;
        machine.onGuiDrawnBackground(this, i, j, adjX, adjY, partialTicks);
        if (machine instanceof IFluidGuiMachine)
        {
        	IFluidGuiMachine<?> fluids = (IFluidGuiMachine<?>) machine;
        	for (int k = 0; k < fluids.getFluidInventory().numSlots(); ++k)
        	{
        		RenderInfo dims = fluids.getFluidRenderInfo(k);
        		if (dims != null) GuiUtils.drawFluid(mc, i + dims.x , j + dims.y, fluids.getFluidInventory().getFluidSlot(k).getFluidOrGas(), dims.w, dims.h, fluids.getMaxFluid(k));
        	}
        }
        GlStateManager.color(1f, 1f, 1f, 1f);
        this.mc.getTextureManager().bindTexture(GUI_ELEMENTS_TEXTURE);
        if (machine instanceof IFluidGuiMachine)
        {
        	IFluidGuiMachine<?> fluids = (IFluidGuiMachine<?>) machine;
        	for (int k = 0; k < fluids.getFluidInventory().numSlots(); ++k)
        	{
        		RenderInfo dims = fluids.getFluidRenderInfo(k);
        		if (dims != null) dims.render.render(i + dims.x, j + dims.y, zLevel, 1);
        	}
        }
        if (machine instanceof IEnergyGuiMachine)
        {
        	IEnergyGuiMachine<?> storage = (IEnergyGuiMachine<?>) machine;
        	for (int k = 0; k < storage.getEnergyInventory().numSlots(); ++k)
        	{
        		IEnergySlot stored = storage.getEnergyInventory().getEnergySlot(k);
        		if (stored.getEnergyStored() > 0)
        		{
            		RenderInfo dims = storage.getEnergyRenderInfo(k);
            		if (dims != null)
            		{
            			double progress = ((double) stored.getEnergyStored()) / stored.getMaxEnergyStored();
            			if (progress > 1) progress = 1;
            			dims.render.render(i + dims.x, j + dims.y, zLevel, progress);
            		}
        		}
        	}
        }
        machine.renderProgressMeters(this, i, j, this.zLevel);
    }

	@Override
	public Object getIngredient(int mX, int mY)
	{
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        mX -= i;
        mY -= j;
        return machine.getIngredient(mX, mY);
	}

	@Nullable
	public ItemStack getPreviewStack(int id)
	{
		List<ItemStack> list = this.previewStacks.get(id);
		if (list == null || list.isEmpty()) return null;
		else return list.get((displayTick / 20) % list.size());
	}

    /**
     * Draws the given slot: any item in it, the slot's background, the hovered highlight, etc.
     */
	@Override
    protected void drawSlot(Slot slotIn)
    {
        int slotX = slotIn.xPos;
        int slotY = slotIn.yPos;
        ItemStack slotStack = slotIn.getStack();
        boolean isHovered = false;
        boolean preventStackRender = slotIn == this.clickedSlot && !this.draggedStack.isEmpty() && !this.isRightMouseClick;
        ItemStack heldStack = this.mc.player.inventory.getItemStack();
        String countOverride = null;

        if (slotIn == this.clickedSlot && !this.draggedStack.isEmpty() && this.isRightMouseClick && !slotStack.isEmpty())
        {
            slotStack = slotStack.copy();
            slotStack.setCount(slotStack.getCount() / 2);
        }
        else if (this.dragSplitting && this.dragSplittingSlots.contains(slotIn) && !heldStack.isEmpty())
        {
            if (this.dragSplittingSlots.size() == 1)
            {
                return;
            }

            if (Container.canAddItemToSlot(slotIn, heldStack, true) && this.inventorySlots.canDragIntoSlot(slotIn))
            {
                slotStack = heldStack.copy();
                isHovered = true;
                Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, slotStack, slotIn.getStack().isEmpty() ? 0 : slotIn.getStack().getCount());
                int k = Math.min(slotStack.getMaxStackSize(), slotIn.getItemStackLimit(slotStack));

                if (slotStack.getCount() > k)
                {
                    countOverride = TextFormatting.YELLOW.toString() + k;
                    slotStack.setCount(k);
                }
            }
            else
            {
                this.dragSplittingSlots.remove(slotIn);
                this.updateDragSplitting();
            }
        }

        this.zLevel = 100.0F;
        this.itemRender.zLevel = 100.0F;

        if (slotStack.isEmpty() && slotIn.isEnabled())
        {
            TextureAtlasSprite textureatlassprite = slotIn.getBackgroundSprite();

            if (textureatlassprite != null)
            {
                GlStateManager.disableLighting();
                this.mc.getTextureManager().bindTexture(slotIn.getBackgroundLocation());
                this.drawTexturedModalRect(slotX, slotY, textureatlassprite, 16, 16);
                GlStateManager.enableLighting();
                preventStackRender = true;
            }
        }

        if (!preventStackRender)
        {
            if (isHovered)
            {
                drawRect(slotX, slotY, slotX + 16, slotY + 16, 0x80FFFFFF);
            }

            GlStateManager.enableDepth();

            if (slotStack.isEmpty())
            {
            	if (slotIn.inventory == this.machine)
            	{
                	ItemStack previewStack = this.getPreviewStack(slotIn.getSlotIndex());
                	if (previewStack != null && !previewStack.isEmpty())
                	{
                        this.itemRender.renderItemAndEffectIntoGUI(this.mc.player, previewStack, slotX, slotY);
                        this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, previewStack, slotX, slotY, countOverride);
                        GlStateManager.disableDepth();
                        drawRect(slotX, slotY, slotX + 16, slotY + 16, 0x808b8b8b);
                        GlStateManager.enableDepth();
                	}
            	}
            }
            else
            {
                this.itemRender.renderItemAndEffectIntoGUI(this.mc.player, slotStack, slotX, slotY);
                this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, slotStack, slotX, slotY, countOverride);
            }
        }

        this.itemRender.zLevel = 0.0F;
        this.zLevel = 0.0F;
    }
}