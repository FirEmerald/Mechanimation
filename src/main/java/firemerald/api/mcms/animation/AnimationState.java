package firemerald.api.mcms.animation;

import java.util.function.Supplier;

public class AnimationState
{
	public static final AnimationState[] NONE = new AnimationState[0];

	public final Supplier<IAnimation> anim;
	public float time;

	public AnimationState(Supplier<IAnimation> anim, float time)
	{
		this.anim = anim;
		this.time = time;
	}

	public AnimationState(Supplier<IAnimation> anim)
	{
		this(anim, 0);
	}
}