package firemerald.mechanimation.plugin.transformers;

import org.apache.logging.log4j.Logger;

import firemerald.api.core.plugin.StandardTransformer;
import firemerald.mechanimation.plugin.Plugin;

public abstract class MechanimationTransformer extends StandardTransformer
{
	public MechanimationTransformer(boolean computeFrames)
	{
		super(computeFrames);
	}

	public MechanimationTransformer(boolean computeFrames, boolean compute_maxs)
	{
		super(computeFrames, compute_maxs);
	}

	public MechanimationTransformer(boolean skipFrames, boolean computeFrames, boolean compute_maxs)
	{
		super(computeFrames, computeFrames, compute_maxs);
	}

	@Override
	public Logger logger()
	{
		return Plugin.logger();
	}
}