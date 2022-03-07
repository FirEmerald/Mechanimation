package firemerald.api.mcms.util;

import firemerald.api.mcms.model.IRaytraceTarget;

public class RaytraceResult
{
	public final IRaytraceTarget hit;
	public final double m;

	public RaytraceResult(IRaytraceTarget hit, double m)
	{
		this.hit = hit;
		this.m = m;
	}
}