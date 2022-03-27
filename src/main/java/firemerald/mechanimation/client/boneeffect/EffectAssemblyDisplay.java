package firemerald.mechanimation.client.boneeffect;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import firemerald.api.mcms.animation.Transformation;
import firemerald.api.data.AbstractElement;
import firemerald.api.mcms.model.IEditableParent;
import firemerald.api.mcms.model.IModelEditable;
import firemerald.api.mcms.model.IRigged;
import firemerald.api.mcms.model.RenderBone;
import firemerald.api.mcms.model.effects.BoneEffect;
import firemerald.api.mcms.model.effects.EffectRenderStage;
import firemerald.api.mcms.model.effects.StagedPosedBoneEffect;
import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.api.crafting.assembly_terminal.IAssemblyBlueprint;
import firemerald.mechanimation.api.util.Rectangle;
import firemerald.mechanimation.api.util.Vec2i;
import firemerald.mechanimation.tileentity.machine.assembly_terminal.TileEntityAssemblyTerminalBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;

public class EffectAssemblyDisplay extends StagedPosedBoneEffect
{
	protected float scale = 1;

	public EffectAssemblyDisplay(String name, @Nullable RenderBone<?> parent, Transformation transform, EffectRenderStage stage)
	{
		this(name, parent, transform, stage, 1);
	}

	public EffectAssemblyDisplay(String name, @Nullable RenderBone<?> parent, Transformation transform, EffectRenderStage stage, float scale)
	{
		super(name, parent, transform, stage);
		this.scale = scale;
	}

	public EffectAssemblyDisplay(String name, @Nullable RenderBone<?> parent, EffectRenderStage stage)
	{
		this(name, parent, stage, 1);
	}

	public EffectAssemblyDisplay(String name, @Nullable RenderBone<?> parent, EffectRenderStage stage, float scale)
	{
		super(name, parent, stage);
		this.scale = scale;
	}

	@Override
	public IModelEditable copy(IEditableParent newParent, IRigged<?, ?> iRigged)
	{
		if (newParent instanceof RenderBone<?>) return cloneObject((RenderBone<?>) newParent);
		else return null;
	}

	@Override
	public EffectRenderStage getDefaultStage()
	{
		return EffectRenderStage.POST_CHILDREN;
	}

	@Override
	public void render(Object holder, Runnable defaultTex)
	{
		if (holder instanceof TileEntityAssemblyTerminalBase)
		{
			TileEntityAssemblyTerminalBase<?> tile = (TileEntityAssemblyTerminalBase<?>) holder;
			IAssemblyBlueprint blueprint = tile.getCachedBlueprint();
			if (blueprint != null)
			{
				GlStateManager.pushMatrix();
				GlStateManager.scale(scale, scale, scale);
				ItemStack blueprintStack = tile.ITEM_INDEX_BLUEPRINT_INPUT.getStack();
				Minecraft.getMinecraft().getTextureManager().bindTexture(blueprint.blueprintImage(blueprintStack, tile.getTier()));
				Rectangle bounds = blueprint.getBlueprintImageBounds(blueprintStack, tile.getTier());
				GlStateManager.translate(bounds.w * -.5, 0, bounds.h * -.5);
				Tessellator t = Tessellator.getInstance();
				BufferBuilder b = t.getBuffer();
				b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
				b.pos(0, 0, 0)              .tex(bounds.x1 / 256f, bounds.y1 / 256f).normal(0, 1, 0).endVertex();
				b.pos(0, 0, bounds.h)       .tex(bounds.x1 / 256f, bounds.y2 / 256f).normal(0, 1, 0).endVertex();
				b.pos(bounds.w, 0, bounds.h).tex(bounds.x2 / 256f, bounds.y2 / 256f).normal(0, 1, 0).endVertex();
				b.pos(bounds.w, 0, 0)       .tex(bounds.x2 / 256f, bounds.y1 / 256f).normal(0, 1, 0).endVertex();
				t.draw();
				Vec2i[] inputs = blueprint.getInput(blueprintStack, tile.getTier());
				int numInputs = Math.min(tile.assemblyInventory.additionalSlots.length, inputs.length);
				for (int i = 0; i < numInputs; i++)
				{
					ItemStack stack = tile.assemblyInventory.additionalSlots[i].getStack();
					if (!stack.isEmpty())
					{
						GlStateManager.pushMatrix();
						GlStateManager.translate(inputs[i].x + 8, 0, inputs[i].y + 8);
						GlStateManager.scale(16, 16, 16);
						GlStateManager.rotate(-90, 1, 0, 0);
						GlStateManager.rotate(180, 0, 1, 0);
						Minecraft.getMinecraft().getRenderItem().renderItem(stack, TransformType.FIXED);
						GlStateManager.popMatrix();
					}
				}
				ItemStack output = tile.ITEM_INDEX_ITEM_OUTPUT.getStack();
				if (!output.isEmpty())
				{
					Vec2i outputPos = blueprint.getOutputPosition(blueprintStack, tile.getTier());
					GlStateManager.pushMatrix();
					GlStateManager.translate(outputPos.x + 8, 0, outputPos.y + 8);
					GlStateManager.scale(16, 16, 16);
					GlStateManager.rotate(-90, 1, 0, 0);
					GlStateManager.rotate(180, 0, 1, 0);
					Minecraft.getMinecraft().getRenderItem().renderItem(output, TransformType.FIXED);
					GlStateManager.popMatrix();
				}
				GlStateManager.popMatrix();
				defaultTex.run();
			}
		}
	}

	@Override
	public void loadFromXML(AbstractElement el)
	{
		super.loadFromXML(el);
		this.scale = el.getFloat("scale", 1f);
	}

	@Override
	public void saveToXML(AbstractElement el)
	{
		super.saveToXML(el);
		if (this.scale != 1f) el.setFloat("scale", this.scale);
	}

	@Override
	public String getXMLName()
	{
		return MechanimationAPI.MOD_ID + ":assembly_terminal";
	}

	@Override
	public void doCleanUp() {}

	@Override
	public BoneEffect cloneObject(RenderBone<?> clonedParent)
	{
		return new EffectAssemblyDisplay(this.name, clonedParent, this.transform, this.stage, this.scale);
	}
}
