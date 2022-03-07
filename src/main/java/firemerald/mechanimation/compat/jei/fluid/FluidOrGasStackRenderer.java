package firemerald.mechanimation.compat.jei.fluid;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import firemerald.api.core.client.Translator;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.client.gui.GuiUtils;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.Fluid;

public class FluidOrGasStackRenderer implements IIngredientRenderer<FluidOrGasStack> {

    private static final int TEX_WIDTH = 16;
    private static final int TEX_HEIGHT = 16;

    private final int capacityMb;
    private final TooltipMode tooltipMode;
    private final int width;
    private final int height;
    @Nullable
    private final IDrawable overlay;

    public FluidOrGasStackRenderer()
    {
        this(Fluid.BUCKET_VOLUME, TooltipMode.ITEM_LIST, TEX_WIDTH, TEX_HEIGHT, null);
    }

    public FluidOrGasStackRenderer(int capacityMb, boolean showCapacity, int width, int height, @Nullable IDrawable overlay)
    {
        this(capacityMb, showCapacity ? TooltipMode.SHOW_AMOUNT_AND_CAPACITY : TooltipMode.SHOW_AMOUNT, width, height, overlay);
    }

    public FluidOrGasStackRenderer(int capacityMb, TooltipMode tooltipMode, int width, int height, @Nullable IDrawable overlay)
    {
        this.capacityMb = capacityMb;
        this.tooltipMode = tooltipMode;
        this.width = width;
        this.height = height;
        this.overlay = overlay;
    }

    @Override
    public void render(Minecraft minecraft, final int xPosition, final int yPosition, @Nullable FluidOrGasStack fluidStack)
    {
		GlStateManager.enableBlend();
    	GlStateManager.color(1f, 1f, 1f, 1f);
    	GuiUtils.drawFluid(minecraft, xPosition, yPosition, fluidStack, width, height, capacityMb);
    	GlStateManager.color(1f, 1f, 1f, 1f);
    	if (overlay != null)
    	{
        	GlStateManager.pushMatrix();
        	GlStateManager.translate(0, 0, 101);
        	overlay.draw(minecraft, xPosition, yPosition);
        	GlStateManager.popMatrix();
    	}
    }

    @Override
    public List<String> getTooltip(Minecraft minecraft, FluidOrGasStack gasStack, ITooltipFlag tooltipFlag)
    {
        List<String> tooltip = new ArrayList<>();
        String gasName = gasStack.getLocalizedName();
        tooltip.add(gasName);
        if (tooltipMode == TooltipMode.SHOW_AMOUNT_AND_CAPACITY)
        {
            String amount = Translator.format("jei.tooltip.liquid.amount.with.capacity", gasStack.getAmount(), capacityMb);
            tooltip.add(TextFormatting.GRAY + amount);
        }
        else if (tooltipMode == TooltipMode.SHOW_AMOUNT)
        {
            String amount = Translator.format("jei.tooltip.liquid.amount", gasStack.getAmount());
            tooltip.add(TextFormatting.GRAY + amount);
        }
        return tooltip;
    }

    @Override
    public FontRenderer getFontRenderer(Minecraft minecraft, FluidOrGasStack gasStack)
    {
        return minecraft.fontRenderer;
    }

    enum TooltipMode
    {
        SHOW_AMOUNT,
        SHOW_AMOUNT_AND_CAPACITY,
        ITEM_LIST
    }
}