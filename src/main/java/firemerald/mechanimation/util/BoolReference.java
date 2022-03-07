package firemerald.mechanimation.util;

public class BoolReference
{
	public boolean val;

	public BoolReference() {}

	public BoolReference(boolean val)
	{
		this.val = val;
	}

	public void or(boolean val)
	{
		this.val |= val;
	}

	public void and(boolean val)
	{
		this.val &= val;
	}

	public void xor(boolean val)
	{
		this.val ^= val;
	}

	public void negate()
	{
		this.val = !val;
	}
}