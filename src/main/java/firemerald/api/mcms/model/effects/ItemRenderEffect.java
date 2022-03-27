package firemerald.api.mcms.model.effects;

import javax.annotation.Nullable;

import firemerald.api.mcms.animation.Transformation;
import firemerald.api.data.AbstractElement;
import firemerald.api.mcms.model.IEditableParent;
import firemerald.api.mcms.model.IRigged;
import firemerald.api.mcms.model.RenderBone;
import firemerald.api.mcms.util.IItemStackProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ItemRenderEffect extends StagedPosedBoneEffect
{
	protected int slot = 0;
	protected float scale = 1;
	protected TransformType transformType;

	public ItemRenderEffect(String name, @Nullable RenderBone<?> parent, Transformation transform, int slot)
	{
		this(name, parent, transform, EffectRenderStage.POST_BONE, slot, 1f);
	}

	public ItemRenderEffect(String name, @Nullable RenderBone<?> parent, Transformation transform, EffectRenderStage stage, int slot)
	{
		this(name, parent, transform, stage, slot, 1f);
	}

	public ItemRenderEffect(String name, @Nullable RenderBone<?> parent, Transformation transform, int slot, float scale)
	{
		this(name, parent, transform, EffectRenderStage.POST_BONE, slot, scale, TransformType.FIXED);
	}

	public ItemRenderEffect(String name, @Nullable RenderBone<?> parent, Transformation transform, EffectRenderStage stage, int slot, float scale)
	{
		this(name, parent, transform, stage, slot, scale, TransformType.FIXED);
	}

	public ItemRenderEffect(String name, @Nullable RenderBone<?> parent, Transformation transform, int slot, TransformType transformType)
	{
		this(name, parent, transform, EffectRenderStage.POST_BONE, slot, transformType);
	}

	public ItemRenderEffect(String name, @Nullable RenderBone<?> parent, Transformation transform, EffectRenderStage stage, int slot, TransformType transformType)
	{
		this(name, parent, transform, stage, slot, 1, transformType);
	}

	public ItemRenderEffect(String name, @Nullable RenderBone<?> parent, Transformation transform, int slot, float scale, TransformType transformType)
	{
		this(name, parent, transform, EffectRenderStage.POST_BONE, slot, scale, transformType);
	}

	public ItemRenderEffect(String name, @Nullable RenderBone<?> parent, Transformation transform, EffectRenderStage stage, int slot, float scale, TransformType transformType)
	{
		super(name, parent, transform, stage);
		this.slot = slot;
		this.scale = scale;
		this.transformType = transformType;
	}

	public int getSlot()
	{
		return slot;
	}

	public void setSlot(int slot)
	{
		this.slot = slot;
	}

	public float getScale()
	{
		return scale;
	}

	public void setScale(float scale)
	{
		this.scale = scale;
	}

	public TransformType getTransformType()
	{
		return transformType;
	}

	public void setTransformType(TransformType transformType)
	{
		this.transformType = transformType;
	}

	@Override
	public void render(@Nullable Object holder, Runnable defaultTexture) //TODO
	{
		ItemStack item = null;
		if (holder instanceof IItemStackProvider) item = ((IItemStackProvider) holder).getItemStack(slot);
		else if (holder instanceof IInventory) item = ((IInventory) holder).getStackInSlot(slot);
		//else if (holder instanceof EntityLivingBase) item = ((EntityLivingBase) holder). TODO entity inventory
		if (item != null && !item.isEmpty())
		{
			GlStateManager.pushMatrix();
			GlStateManager.scale(scale, scale, scale);
			Minecraft.getMinecraft().getRenderItem().renderItem(item, transformType);
			GlStateManager.popMatrix();
			defaultTexture.run();
		}
	}

	@Override
	public void loadFromXML(AbstractElement el)
	{
		super.loadFromXML(el);
		slot = el.getInt("slot", 0);
		this.scale = el.getFloat("scale", 1f);
		transformType = el.getEnum("transformType", TransformType.values(), TransformType.FIXED);
	}

	@Override
	public void saveToXML(AbstractElement el)
	{
		super.saveToXML(el);
		el.setInt("slot", slot);
		if (this.scale != 1f) el.setFloat("scale", this.scale);
		el.setEnum("transformType", transformType);
	}

	@Override
	public String getXMLName()
	{
		return "item";
	}

	@Override
	public ItemRenderEffect cloneObject(RenderBone<?> clonedParent)
	{
		return new ItemRenderEffect(this.name, clonedParent, transform, slot, scale, transformType);
	}

	@Override
	public ItemRenderEffect copy(IEditableParent newParent, IRigged<?, ?> iRigged)
	{
		if (newParent instanceof RenderBone<?>) return cloneObject((RenderBone<?>) newParent);
		else return null;
	}

	@Override
	public void doCleanUp() {}

	@Override
	public EffectRenderStage getDefaultStage()
	{
		return EffectRenderStage.POST_BONE;
	}
}