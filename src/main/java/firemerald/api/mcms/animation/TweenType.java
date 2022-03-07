package firemerald.api.mcms.animation;

import firemerald.api.mcms.math.MathUtils;
import firemerald.api.mcms.util.FloatBinaryOperator;

public enum TweenType
{
	SLERP(MathUtils::smoothLerp),
	FALLIN(MathUtils::intoCurve),
	FALLOUT(MathUtils::fromCurve);

	static
	{
		SLERP.inverse = SLERP;
		FALLIN.inverse = FALLOUT;
		FALLOUT.inverse = FALLIN;
	}

	protected TweenType inverse;
	public final FloatBinaryOperator function;

	TweenType(FloatBinaryOperator function)
	{
		this.function = function;
	}

	public TweenType inverse()
	{
		return inverse;
	}

	public float apply(float factor, float smoothing)
	{
		return function.applyAsFloat(factor, smoothing);
	}
}