package firemerald.mechanimation.mcms;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import firemerald.api.mcms.animation.Transformation;
import firemerald.api.mcms.data.AbstractElement;
import firemerald.api.mcms.model.IEditableParent;
import firemerald.api.mcms.model.IRigged;
import firemerald.api.mcms.model.RenderBone;
import firemerald.api.mcms.model.effects.EffectRenderStage;
import firemerald.api.mcms.model.effects.StagedPosedBoneEffect;
import firemerald.api.mcms.util.IModeledEntity;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.client.gui.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class FluidOrGasRenderEffect extends StagedPosedBoneEffect
{
	protected int index = 0;
	protected float sizeX = 1, sizeY = 1, sizeZ = 1, margin = 0.00390625f;

	public FluidOrGasRenderEffect(String name, @Nullable RenderBone<?> parent, Transformation transform, int index)
	{
		this(name, parent, transform, EffectRenderStage.POST_BONE, index);
	}

	public FluidOrGasRenderEffect(String name, @Nullable RenderBone<?> parent, Transformation transform, EffectRenderStage stage, int index)
	{
		this(name, parent, transform, index, 1f, 1f, 1f, 0.00390625f);
	}

	public FluidOrGasRenderEffect(String name, @Nullable RenderBone<?> parent, Transformation transform, int index, float sizeX, float sizeY, float sizeZ, float margin)
	{
		this(name, parent, transform, EffectRenderStage.POST_BONE, index, sizeX, sizeY, sizeZ, margin);
	}

	public FluidOrGasRenderEffect(String name, @Nullable RenderBone<?> parent, Transformation transform, EffectRenderStage stage, int index, float sizeX, float sizeY, float sizeZ, float margin)
	{
		super(name, parent, transform, stage);
		this.index = index;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		this.margin = margin;
	}

	public void setSizeX(float sizeX)
	{
		this.sizeX = sizeX;
	}

	public float getSizeX()
	{
		return sizeX;
	}

	public void setSizeY(float sizeY)
	{
		this.sizeY = sizeY;
	}

	public float getSizeY()
	{
		return sizeY;
	}

	public void setSizeZ(float sizeZ)
	{
		this.sizeZ = sizeZ;
	}

	public float getSizeZ()
	{
		return sizeZ;
	}

	public void setMargin(float margin)
	{
		this.margin = margin;
	}

	public float getMargin()
	{
		return margin;
	}

	public void setIndex(int index)
	{
		this.index = index;
	}

	public int getIndex()
	{
		return index;
	}

	@Override
	public void render(@Nullable Object holder, Runnable defaultTexture)
	{
		if (holder instanceof IFluidOrGasStackProvider)
		{
			FluidOrGasStack stack = ((IFluidOrGasStackProvider) holder).getFluidStack(index);
			if (stack != null && stack.getAmount() > 0)
			{
				boolean transparent = holder instanceof IModeledEntity && ((IModeledEntity) holder).isTransparent();
				if (!transparent)
				{
					GlStateManager.enableBlend();
					GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
				}
				GuiUtils.setColorFrom(stack);
				TextureAtlasSprite sprite = GuiUtils.getSprite(Minecraft.getMinecraft(), stack);
				Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				Tessellator t = Tessellator.getInstance();
				BufferBuilder b = t.getBuffer();
				b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
				float u0 = sprite.getMinU(), u2 = sprite.getMaxU();
				float v0 = sprite.getMinV(), v2 = sprite.getMaxV();
				GlStateManager.translate(margin, margin, margin);
				float sizeX = this.sizeX - 2 * margin;
				float sizeY = (this.sizeY - 2 * margin) * stack.getAmount() / ((IFluidOrGasStackProvider) holder).getMaxFluid(index);
				float sizeZ = this.sizeZ - 2 * margin;
				{
					float x = 0;
					while (x < sizeX)
					{
						float nX = Math.min(sizeX, x + 1);
						float u = sprite.getInterpolatedU((nX - x) * 16);
						float y = 0;
						while (y < sizeY)
						{
							float nY = Math.min(sizeY, y + 1);
							float v = sprite.getInterpolatedV((nY - y) * 16);
							b.pos(x , y , 0).tex(u0, v0).normal(0, 0, -1).endVertex();
							b.pos(x , nY, 0).tex(u0, v ).normal(0, 0, -1).endVertex();
							b.pos(nX, nY, 0).tex(u , v ).normal(0, 0, -1).endVertex();
							b.pos(nX, y , 0).tex(u , v0).normal(0, 0, -1).endVertex();
							y = nY;
						}
						x = nX;
					}
				}
				{
					float z = sizeZ;
					while (z > 0)
					{
						float nZ = Math.max(0, z - 1);
						float u = sprite.getInterpolatedU((1 + nZ - z) * 16);
						float y = 0;
						while (y < sizeY)
						{
							float nY = Math.min(sizeY, y + 1);
							float v = sprite.getInterpolatedV((nY - y) * 16);
							b.pos(0, y , z ).tex(u , v0).normal(-1, 0, 0).endVertex();
							b.pos(0, nY, z ).tex(u , v ).normal(-1, 0, 0).endVertex();
							b.pos(0, nY, nZ).tex(u2, v ).normal(-1, 0, 0).endVertex();
							b.pos(0, y , nZ).tex(u2, v0).normal(-1, 0, 0).endVertex();
							y = nY;
						}
						z = nZ;
					}
				}
				{
					float x = sizeX;
					while (x > 0)
					{
						float nX = Math.max(0, x - 1);
						float u = sprite.getInterpolatedU((1 + nX - x) * 16);
						float y = 0;
						while (y < sizeY)
						{
							float nY = Math.min(sizeY, y + 1);
							float v = sprite.getInterpolatedV((nY - y) * 16);
							b.pos(x , y , sizeZ).tex(u , v0).normal(0, 0, 1).endVertex();
							b.pos(x , nY, sizeZ).tex(u , v ).normal(0, 0, 1).endVertex();
							b.pos(nX, nY, sizeZ).tex(u2, v ).normal(0, 0, 1).endVertex();
							b.pos(nX, y , sizeZ).tex(u2, v0).normal(0, 0, 1).endVertex();
							y = nY;
						}
						x = nX;
					}
				}
				{
					float z = 0;
					while (z < sizeZ)
					{
						float nZ = Math.min(sizeZ, z + 1);
						float u = sprite.getInterpolatedU((nZ - z) * 16);
						float y = 0;
						while (y < sizeY)
						{
							float nY = Math.min(sizeY, y + 1);
							float v = sprite.getInterpolatedV((nY - y) * 16);
							b.pos(sizeX, y , z ).tex(u0, v0).normal(1, 0, 0).endVertex();
							b.pos(sizeX, nY, z ).tex(u0, v ).normal(1, 0, 0).endVertex();
							b.pos(sizeX, nY, nZ).tex(u , v ).normal(1, 0, 0).endVertex();
							b.pos(sizeX, y , nZ).tex(u , v0).normal(1, 0, 0).endVertex();
							y = nY;
						}
						z = nZ;
					}
				}
				{
					float x = 0;
					while (x < sizeX)
					{
						float nX = Math.min(sizeX, x + 1);
						float u = sprite.getInterpolatedU((nX - x) * 16);
						float z = 0;
						while (z < sizeZ)
						{
							float nZ = Math.min(sizeZ, z + 1);
							float v = sprite.getInterpolatedV((nZ - z) * 16);
							b.pos(x , sizeY, z ).tex(u0, v0).normal(0, 1, 0).endVertex();
							b.pos(x , sizeY, nZ).tex(u0, v ).normal(0, 1, 0).endVertex();
							b.pos(nX, sizeY, nZ).tex(u , v ).normal(0, 1, 0).endVertex();
							b.pos(nX, sizeY, z ).tex(u , v0).normal(0, 1, 0).endVertex();
							z = nZ;
						}
						x = nX;
					}
				}
				{
					float x = 0;
					while (x < sizeX)
					{
						float nX = Math.min(sizeX, x + 1);
						float u = sprite.getInterpolatedU((nX - x) * 16);
						float z = sizeZ;
						while (z > 0)
						{
							float nZ = Math.max(0, z - 1);
							float v = sprite.getInterpolatedV((1 + nZ - z) * 16);
							b.pos(x , 0, z ).tex(u0, v ).normal(0, -1, 0).endVertex();
							b.pos(x , 0, nZ).tex(u0, v2).normal(0, -1, 0).endVertex();
							b.pos(nX, 0, nZ).tex(u , v2).normal(0, -1, 0).endVertex();
							b.pos(nX, 0, z ).tex(u , v ).normal(0, -1, 0).endVertex();
							z = nZ;
						}
						x = nX;
					}
				}
				t.draw();
				GlStateManager.color(1f, 1f, 1f, 1f);
				defaultTexture.run();
				if (!transparent) GlStateManager.disableBlend();
			}
		}
	}

	@Override
	public void loadFromXML(AbstractElement el)
	{
		super.loadFromXML(el);
		index = el.getInt("index", 0);
		this.sizeX = el.getFloat("sizeX", 1f);
		this.sizeY = el.getFloat("sizeY", 1f);
		this.sizeZ = el.getFloat("sizeZ", 1f);
		this.margin = el.getFloat("margin", 0f);
	}

	@Override
	public void saveToXML(AbstractElement el)
	{
		super.saveToXML(el);
		el.setInt("index", index);
		el.setFloat("sizeX", this.sizeX);
		el.setFloat("sizeY", this.sizeY);
		el.setFloat("sizeZ", this.sizeZ);
		el.setFloat("margin", this.margin);
	}

	@Override
	public String getXMLName()
	{
		return "fluid";
	}

	@Override
	public FluidOrGasRenderEffect cloneObject(RenderBone<?> clonedParent)
	{
		return new FluidOrGasRenderEffect(this.name, clonedParent, transform.copy(), index, sizeX, sizeY, sizeZ, margin);
	}

	@Override
	public FluidOrGasRenderEffect copy(IEditableParent newParent, IRigged<?, ?> iRigged)
	{
		if (newParent instanceof RenderBone<?>) return cloneObject((RenderBone<?>) newParent);
		else return null;
	}

	@Override
	public EffectRenderStage getDefaultStage()
	{
		return EffectRenderStage.POST_BONE;
	}

	@Override
	public void doCleanUp() {}
}