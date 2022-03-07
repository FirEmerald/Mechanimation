package firemerald.mechanimation.client.gui;

import javax.annotation.Nullable;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiUtils
{
	private static final int TEX_WIDTH = 16;
	private static final int TEX_HEIGHT = 16;
	private static final int MIN_FLUID_HEIGHT = 1; // ensure tiny amounts of fluid are still visible

	public static void drawFluid(Minecraft minecraft, final int xPosition, final int yPosition, @Nullable FluidOrGasStack fluidStack, int width, int height, int maxVolume)
	{
		if (fluidStack != null)
		{
			if (fluidStack.isFluid()) drawFluid(minecraft, xPosition, yPosition, fluidStack.getFluidStack(), width, height, maxVolume);
			else if (fluidStack.isGas()) drawGas(minecraft, xPosition, yPosition, fluidStack.getGasStack(), width, height, maxVolume);
		}
	}

	private static void drawFluid(Minecraft minecraft, final int xPosition, final int yPosition, @Nullable FluidStack fluidStack, int width, int height, int maxVolume)
	{
		if (fluidStack == null) return;
		Fluid fluid = fluidStack.getFluid();
		if (fluid == null) return;
		TextureAtlasSprite fluidStillSprite = getStillFluidSprite(minecraft, fluid);
		int fluidColor = fluid.getColor(fluidStack);
		int scaledAmount = (fluidStack.amount * height) / maxVolume;
		if (fluidStack.amount > 0 && scaledAmount < MIN_FLUID_HEIGHT) scaledAmount = MIN_FLUID_HEIGHT;
		if (scaledAmount > height) scaledAmount = height;
		drawTiledSprite(minecraft, xPosition, yPosition, width, height, fluidColor, scaledAmount, fluidStillSprite);
	}

    private static void drawGas(Minecraft minecraft, final int xPosition, final int yPosition, @Nullable GasStack gasStack, int width, int height, int maxVolume)
    {
        Gas gas = gasStack == null ? null : gasStack.getGas();
        if (gas == null) return;
        int scaledAmount = (gasStack.amount * height) / maxVolume;
        if (gasStack.amount > 0 && scaledAmount < MIN_FLUID_HEIGHT) scaledAmount = MIN_FLUID_HEIGHT;
        if (scaledAmount > height) scaledAmount = height;
        drawTiledSprite(minecraft, xPosition, yPosition, width, height, gas, scaledAmount, getStillGasSprite(minecraft, gas));
    }

    private static void drawTiledSprite(Minecraft minecraft, final int xPosition, final int yPosition, final int tiledWidth, final int tiledHeight, Gas gas, int scaledAmount,
          TextureAtlasSprite sprite) {
        minecraft.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        color(gas);

        final int xTileCount = tiledWidth / TEX_WIDTH;
        final int xRemainder = tiledWidth - (xTileCount * TEX_WIDTH);
        final int yTileCount = scaledAmount / TEX_HEIGHT;
        final int yRemainder = scaledAmount - (yTileCount * TEX_HEIGHT);
        final int yStart = yPosition + tiledHeight;

        for (int xTile = 0; xTile <= xTileCount; xTile++) {
            int width = (xTile == xTileCount) ? xRemainder : TEX_WIDTH;
            if (width > 0) {
                int x = xPosition + (xTile * TEX_WIDTH);
                int maskRight = TEX_WIDTH - width;
                for (int yTile = 0; yTile <= yTileCount; yTile++) {
                    int height = (yTile == yTileCount) ? yRemainder : TEX_HEIGHT;
                    if (height > 0) {
                        int y = yStart - ((yTile + 1) * TEX_HEIGHT);
                        int maskTop = TEX_HEIGHT - height;
                        drawTextureWithMasking(x, y, sprite, maskTop, maskRight, 100);
                    }
                }
            }
        }
        resetColor();
    }

    public static void color(@Nullable Gas gas) {
        if (gas != null) {
            int color = gas.getTint();
            GlStateManager.color(getRed(color), getGreen(color), getBlue(color));
        }
    }

    //Color
    public static void resetColor() {
        GlStateManager.color(1, 1, 1, 1);
    }

    private static float getRed(int color) {
        return (color >> 16 & 0xFF) / 255.0F;
    }

    private static float getGreen(int color) {
        return (color >> 8 & 0xFF) / 255.0F;
    }

    private static float getBlue(int color) {
        return (color & 0xFF) / 255.0F;
    }

	public static TextureAtlasSprite getSprite(Minecraft minecraft, FluidOrGasStack stack)
	{
		return stack == null ? minecraft.getTextureMapBlocks().getMissingSprite() :
			stack.isFluid() ? getStillFluidSprite(minecraft, stack.getFluidStack().getFluid()) :
			stack.isGas() ? getStillGasSprite(minecraft, stack.getGasStack().getGas()) :
				minecraft.getTextureMapBlocks().getMissingSprite();
	}

	public static void setColorFrom(FluidOrGasStack stack)
	{
		if (stack != null)
		{
			if (stack.isFluid()) setGLColorFromInt(stack.getFluidStack().getFluid().getColor(stack.getFluidStack()));
			else if (stack.isGas()) setGLColorFromInt(0xFF000000 | stack.getGasStack().getGas().getTint());
		}
	}

	private static TextureAtlasSprite getStillFluidSprite(Minecraft minecraft, Fluid fluid)
	{
		TextureMap textureMapBlocks = minecraft.getTextureMapBlocks();
		ResourceLocation fluidStill = fluid.getStill();
		TextureAtlasSprite fluidStillSprite = null;
		if (fluidStill != null) fluidStillSprite = textureMapBlocks.getTextureExtry(fluidStill.toString());
		if (fluidStillSprite == null) fluidStillSprite = textureMapBlocks.getMissingSprite();
		return fluidStillSprite;
	}

    private static TextureAtlasSprite getStillGasSprite(Minecraft minecraft, Gas gas)
    {
        TextureMap textureMapBlocks = minecraft.getTextureMapBlocks();
        ResourceLocation gasStill = gas.getIcon();
        TextureAtlasSprite gasStillSprite = null;
        if (gasStill != null) gasStillSprite = textureMapBlocks.getTextureExtry(gasStill.toString());
        if (gasStillSprite == null) gasStillSprite = textureMapBlocks.getMissingSprite();
        return gasStillSprite;
    }

	private static void drawTiledSprite(Minecraft minecraft, final int xPosition, final int yPosition, final int tiledWidth, final int tiledHeight, int color, int scaledAmount, TextureAtlasSprite sprite)
	{
		minecraft.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		setGLColorFromInt(color);

		final int xTileCount = tiledWidth / TEX_WIDTH;
		final int xRemainder = tiledWidth - (xTileCount * TEX_WIDTH);
		final int yTileCount = scaledAmount / TEX_HEIGHT;
		final int yRemainder = scaledAmount - (yTileCount * TEX_HEIGHT);

		final int yStart = yPosition + tiledHeight;

		for (int xTile = 0; xTile <= xTileCount; xTile++) {
			for (int yTile = 0; yTile <= yTileCount; yTile++) {
				int width = (xTile == xTileCount) ? xRemainder : TEX_WIDTH;
				int height = (yTile == yTileCount) ? yRemainder : TEX_HEIGHT;
				int x = xPosition + (xTile * TEX_WIDTH);
				int y = yStart - ((yTile + 1) * TEX_HEIGHT);
				if (width > 0 && height > 0) {
					int maskTop = TEX_HEIGHT - height;
					int maskRight = TEX_WIDTH - width;

					drawTextureWithMasking(x, y, sprite, maskTop, maskRight, 100);
				}
			}
		}
	}

	private static void drawTextureWithMasking(double xCoord, double yCoord, TextureAtlasSprite textureSprite, int maskTop, int maskRight, double zLevel) {
		double uMin = textureSprite.getMinU();
		double uMax = textureSprite.getMaxU();
		double vMin = textureSprite.getMinV();
		double vMax = textureSprite.getMaxV();
		uMax = uMax - (maskRight / 16.0 * (uMax - uMin));
		vMax = vMax - (maskTop / 16.0 * (vMax - vMin));

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferBuilder.pos(xCoord, yCoord + 16, zLevel).tex(uMin, vMax).endVertex();
		bufferBuilder.pos(xCoord + 16 - maskRight, yCoord + 16, zLevel).tex(uMax, vMax).endVertex();
		bufferBuilder.pos(xCoord + 16 - maskRight, yCoord + maskTop, zLevel).tex(uMax, vMin).endVertex();
		bufferBuilder.pos(xCoord, yCoord + maskTop, zLevel).tex(uMin, vMin).endVertex();
		tessellator.draw();
	}

	@SideOnly(Side.CLIENT)
	private static void setGLColorFromInt(int color)
	{
		float alpha = (color >> 24 & 0xFF) / 255.0F;
		float red = (color >> 16 & 0xFF) / 255.0F;
		float green = (color >> 8 & 0xFF) / 255.0F;
		float blue = (color & 0xFF) / 255.0F;
		GlStateManager.color(red, green, blue, alpha);
	}
}