package firemerald.mechanimation.api.util;

public class Rectangle
{
	public static Rectangle ofSize(int x1, int y1, int w, int h)
	{
		return new Rectangle(x1, y1, x1 + w, y1 + h, w, h);
	}

	public static Rectangle ofPoints(int x1, int y1, int x2, int y2)
	{
		return new Rectangle(x1, y1, x2, y2, x2 - x1, y2 - y1);
	}

	public final int x1, y1, x2, y2, w, h;

    private Rectangle(int x1, int y1, int x2, int y2, int w, int h)
    {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.w = w;
        this.h = h;
    }

    @Override
    public int hashCode()
    {
    	return ((x1 & 0xFF) << 24) | ((y1 & 0xFF) << 16) | ((x2 & 0xFF) << 8) | (y2 & 0xFF);
    }

    @Override
    public boolean equals(Object o)
    {
    	if (o == null) return false;
    	else if (o == this) return true;
    	else if (o.getClass() != this.getClass()) return false;
    	else
    	{
    		Rectangle r = (Rectangle) o;
    		return r.x1 == x1 && r.y1 == y1 && r.x2 == x2 && r.y2 == y2;
    	}
    }
}