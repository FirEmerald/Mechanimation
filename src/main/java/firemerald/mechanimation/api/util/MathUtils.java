package firemerald.mechanimation.api.util;

import net.minecraft.util.math.MathHelper;

public class MathUtils
{
	public static final double DEG_TO_RAD = Math.PI / 180;
	public static final double RAD_TO_DEG = 180 / Math.PI;
	public static final double HALF_PI = Math.PI * .5;
	public static final double TAU = 2 * Math.PI;
	public static final float DEG_TO_RAD_F = (float) DEG_TO_RAD;
	public static final float RAD_TO_DEG_F = (float) RAD_TO_DEG;
	public static final float HALF_PI_F = (float) (Math.PI * .5);
	public static final float PI_F = (float) Math.PI;
	public static final float TAU_F = (float) TAU;
	public static final float VR = .299f;
	public static final float VG = .587f;
	public static final float VB = .114f;
	public static final int FLOAT_0_BITS = Float.floatToRawIntBits(0);
	public static final long DOUBLE_0_BITS = Double.doubleToRawLongBits(0);

	public static boolean isZero(float val)
	{
		return Float.floatToRawIntBits(val) == FLOAT_0_BITS;
	}

	public static boolean isZero(double val)
	{
		return Double.doubleToRawLongBits(val) == DOUBLE_0_BITS;
	}

	public static float tan(float a)
	{
		return (float) Math.tan(a);
	}

	public static double sec(double a)
	{
		double cos = Math.cos(a);
		return cos == 0 ? Double.NaN : 1 / cos;
	}

	public static float sec(float a)
	{
		float cos = MathHelper.cos(a);
		return cos == 0 ? Float.NaN : 1 / cos;
	}

	public static double csc(double a)
	{
		double sin = Math.sin(a);
		return sin == 0 ? Double.NaN : 1 / sin;
	}

	public static float csc(float a)
	{
		float sin = MathHelper.sin(a);
		return sin == 0 ? Float.NaN : 1 / sin;
	}

	public static double cot(double a)
	{
		return Math.tan(HALF_PI - a);
	}

	public static float cot(float a)
	{
		return tan(HALF_PI_F - a);
	}

	public static float acos(float a)
	{
		return (float) Math.acos(a);
	}

	public static float asin(float a)
	{
		return (float) Math.asin(a);
	}

	public static float atan(float a)
	{
		return (float) Math.atan(a);
	}

	public static double asec(double a)
	{
		return a == 0 ? Double.NaN : Math.acos(1 / a);
	}

	public static float asec(float a)
	{
		return a == 0 ? Float.NaN : acos(1 / a);
	}

	public static double acsc(double a)
	{
		return a == 0 ? Double.NaN : Math.asin(1 / a);
	}

	public static float acsc(float a)
	{
		return a == 0 ? Float.NaN : asin(1 / a);
	}

	public static double acot(double a)
	{
		return (a >= 0 ? HALF_PI : -HALF_PI) - Math.atan(a);
	}

	public static float acot(float a)
	{
		return (a >= 0 ? HALF_PI_F : -HALF_PI_F) - atan(a);
	}

    /**
     * Long version of ceil()
     */
    public static long lceil(double value)
    {
        long i = (long)value;
        return value > i ? i + 1L : i;
    }

}