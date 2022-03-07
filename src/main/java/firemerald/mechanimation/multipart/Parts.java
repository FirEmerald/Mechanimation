package firemerald.mechanimation.multipart;

import codechicken.multipart.PartMap;

public class Parts
{
	public static final PartMap CENTER = PartMap.CENTER;
	public static final PartMap WEST = PartMap.WEST;
	public static final PartMap EAST = PartMap.EAST;
	public static final PartMap DOWN = PartMap.BOTTOM;
	public static final PartMap UP = PartMap.TOP;
	public static final PartMap NORTH = PartMap.NORTH;
	public static final PartMap SOUTH = PartMap.SOUTH;
	public static final PartMap DOWN_NORTH = PartMap.EDGE_XNN;
	public static final PartMap UP_NORTH = PartMap.EDGE_XPN;
	public static final PartMap DOWN_SOUTH = PartMap.EDGE_XNP;
	public static final PartMap UP_SOUTH = PartMap.EDGE_XPP;
	public static final PartMap WEST_NORTH = PartMap.EDGE_NYN;
	public static final PartMap EAST_NORTH = PartMap.EDGE_PYN;
	public static final PartMap WEST_SOUTH = PartMap.EDGE_NYP;
	public static final PartMap EAST_SOUTH = PartMap.EDGE_PYP;
	public static final PartMap WEST_DOWN = PartMap.EDGE_NNZ;
	public static final PartMap EAST_DOWN = PartMap.EDGE_PNZ;
	public static final PartMap WEST_UP = PartMap.EDGE_NPZ;
	public static final PartMap EAST_UP = PartMap.EDGE_PPZ;
	public static final PartMap WEST_DOWN_NORTH = PartMap.CORNER_NNN;
	public static final PartMap EAST_DOWN_NORTH = PartMap.CORNER_PNN;
	public static final PartMap WEST_UP_NORTH = PartMap.CORNER_NPN;
	public static final PartMap EAST_UP_NORTH = PartMap.CORNER_PPN;
	public static final PartMap WEST_DOWN_SOUTH = PartMap.CORNER_NNP;
	public static final PartMap EAST_DOWN_SOUTH = PartMap.CORNER_PNP;
	public static final PartMap WEST_UP_SOUTH = PartMap.CORNER_NPP;
	public static final PartMap EAST_UP_SOUTH = PartMap.CORNER_PPP;

}