package firemerald.api.mcms.model.effects;

import javax.annotation.Nullable;

import firemerald.api.mcms.animation.Transformation;
import firemerald.api.mcms.data.AbstractElement;
import firemerald.api.mcms.model.RenderBone;

public abstract class StagedPosedBoneEffect extends PosedBoneEffect
{
	public EffectRenderStage stage;

	public StagedPosedBoneEffect(String name, @Nullable RenderBone<?> parent, Transformation transform, EffectRenderStage stage)
	{
		super(name, parent, transform);
		this.stage = stage;
	}

	public StagedPosedBoneEffect(String name, @Nullable RenderBone<?> parent, EffectRenderStage stage)
	{
		super(name, parent);
		this.stage = stage;
	}

	public abstract EffectRenderStage getDefaultStage();

	@Override
	public void loadFromXML(AbstractElement el)
	{
		super.loadFromXML(el);
		this.stage = el.getEnum("renderStage", EffectRenderStage.values(), getDefaultStage());
	}

	@Override
	public void saveToXML(AbstractElement el)
	{
		super.saveToXML(el);
		el.setEnum("renderStage", stage);
	}

	@Override
	public void doPreRender(@Nullable Object holder, Runnable defaultTex)
	{
		if (stage == EffectRenderStage.PRE_BONE) render(holder, defaultTex);
	}

	@Override
	public void doPostRenderBone(@Nullable Object holder, Runnable defaultTex)
	{
		if (stage == EffectRenderStage.POST_BONE) render(holder, defaultTex);
	}

	@Override
	public void doPostRenderChildren(@Nullable Object holder, Runnable defaultTex)
	{
		if (stage == EffectRenderStage.POST_CHILDREN) render(holder, defaultTex);
	}

	public abstract void render(@Nullable Object holder, Runnable defaultTex);
}