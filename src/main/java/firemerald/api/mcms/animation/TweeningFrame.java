package firemerald.api.mcms.animation;

import firemerald.api.mcms.data.AbstractElement;

public class TweeningFrame
{
	public final Transformation transformation;
	public TweenType tweening = TweenType.SLERP;
	public float factor = 1;

	public TweeningFrame(AbstractElement el)
	{
		transformation = new Transformation();
		load(el);
	}

	public TweeningFrame(TweeningFrame from)
	{
		this.transformation = from.transformation.copy();
		this.tweening = from.tweening;
		this.factor = from.factor;
	}

	public TweeningFrame(Transformation transformation, TweenType tweening, float smoothing)
	{
		this.transformation = transformation;
		this.tweening = tweening;
		this.factor = smoothing;
	}

	public TweeningFrame(Transformation transformation, TweenType tweening)
	{
		this.transformation = transformation;
		this.tweening = tweening;
	}

	public TweeningFrame(Transformation transformation, float smoothing)
	{
		this.transformation = transformation;
		this.factor = smoothing;
	}

	public TweeningFrame(Transformation transformation)
	{
		this.transformation = transformation;
	}

	public void load(AbstractElement el)
	{
		transformation.load(el);
		tweening = el.getEnum("tweening", TweenType.values(), TweenType.SLERP);
		factor = el.getFloat("factor", 1);
	}

	public void save(AbstractElement el)
	{
		transformation.save(el);
		if (tweening != TweenType.SLERP) el.setEnum("tweening", tweening);
		if (factor != 1) el.setFloat("factor", factor);
	}

	public float apply(float v)
	{
		return tweening.apply(v, factor);
	}

	@Override
	public String toString()
	{
		return transformation.toString() + " tween type: " + tweening.name() + " factor: " + factor;
	}
}