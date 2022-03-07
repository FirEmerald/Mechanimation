package firemerald.mechanimation.util;

public enum EnumFace
{
	TOP,
	BOTTOM,
	FRONT,
	BACK,
	LEFT,
	RIGHT;

	public static final EnumFace[] NONE = {};
	public static final EnumFace[] SIDES = {FRONT, BACK, LEFT, RIGHT};
	public static final EnumFace[] SIDES_AND_TOP = {FRONT, BACK, LEFT, RIGHT, TOP};
	public static final EnumFace[] SIDES_AND_BOTTOM = {FRONT, BACK, LEFT, RIGHT, BOTTOM};
}