package firemerald.mechanimation.api.util;

public class Vec2i
{
    /** The x component of this vector. */
    public final int x;
    /** The y component of this vector. */
    public final int y;

    public Vec2i(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    @Override
    public int hashCode()
    {
    	return ((x & 0xFFFF) << 16) | (y & 0xFFFF);
    }

    @Override
    public boolean equals(Object o)
    {
    	if (o == null) return false;
    	else if (o == this) return true;
    	else if (o.getClass() != this.getClass()) return false;
    	else
    	{
    		Vec2i v = (Vec2i) o;
    		return v.x == x && v.y == y;
    	}
    }
}