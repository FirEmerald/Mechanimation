package firemerald.mechanimation.util;

import firemerald.mechanimation.api.MechanimationAPI;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;

public class Constants
{
	public static final String CR = Character.toString((char) 0x000D);
	public static final ResourceLocation LOGBOOK_STORAGE_CAP_NAME = new ResourceLocation(MechanimationAPI.MOD_ID, "logbook");
	public static final ResourceLocation PLAYER_SETTINGS_CAP_NAME = new ResourceLocation(MechanimationAPI.MOD_ID, "player_settings");

    public static final AxisAlignedBB FULL_BLOCK = new AxisAlignedBB(0, 0, 0, 1, 1, 1);
    public static final AxisAlignedBB SLAB_UP = new AxisAlignedBB(0, .5f, 0, 1, 1, 1);
    public static final AxisAlignedBB SLAB_DOWN = new AxisAlignedBB(0, 0, 0, 1, .5f, 1);
    public static final AxisAlignedBB SLAB_SOUTH = new AxisAlignedBB(0, 0, .5f, 1, 1, 1);
    public static final AxisAlignedBB SLAB_NORTH = new AxisAlignedBB(0, 0, 0, 1, 1, .5f);
    public static final AxisAlignedBB SLAB_EAST = new AxisAlignedBB(.5f, 0, 0, 1, 1, 1);
    public static final AxisAlignedBB SLAB_WEST = new AxisAlignedBB(0, 0, 0, .5f, 1, 1);
    public static final AxisAlignedBB EDGE_UP_SOUTH = new AxisAlignedBB(0, .5f, .5f, 1, 1, 1);
    public static final AxisAlignedBB EDGE_UP_NORTH = new AxisAlignedBB(0, .5f, 0, 1, 1, .5f);
    public static final AxisAlignedBB EDGE_UP_EAST = new AxisAlignedBB(.5f, .5f, 0, 1, 1, 1);
    public static final AxisAlignedBB EDGE_UP_WEST = new AxisAlignedBB(0, .5f, 0, .5f, 1, 1);
    public static final AxisAlignedBB EDGE_DOWN_SOUTH = new AxisAlignedBB(0, 0, .5f, 1, .5f, 1);
    public static final AxisAlignedBB EDGE_DOWN_NORTH = new AxisAlignedBB(0, 0, 0, 1, .5f, .5f);
    public static final AxisAlignedBB EDGE_DOWN_EAST = new AxisAlignedBB(.5f, 0, 0, 1, .5f, 1);
    public static final AxisAlignedBB EDGE_DOWN_WEST = new AxisAlignedBB(0, 0, 0, .5f, .5f, 1);
    public static final AxisAlignedBB EDGE_SOUTH_EAST = new AxisAlignedBB(.5f, 0, .5f, 1, 1, 1);
    public static final AxisAlignedBB EDGE_SOUTH_WEST = new AxisAlignedBB(0, 0, .5f, .5f, 1, 1);
    public static final AxisAlignedBB EDGE_NORTH_EAST = new AxisAlignedBB(.5f, 0, 0, 1, 1, .5f);
    public static final AxisAlignedBB EDGE_NORTH_WEST = new AxisAlignedBB(0, 0, 0, .5f, 1, .5f);
    public static final AxisAlignedBB CORNER_UP_SOUTH_EAST = new AxisAlignedBB(.5f, .5f, .5f, 1, 1, 1);
    public static final AxisAlignedBB CORNER_UP_SOUTH_WEST = new AxisAlignedBB(0, .5f, .5f, .5f, 1, 1);
    public static final AxisAlignedBB CORNER_UP_NORTH_EAST = new AxisAlignedBB(.5f, .5f, 0, 1, 1, .5f);
    public static final AxisAlignedBB CORNER_UP_NORTH_WEST = new AxisAlignedBB(0, .5f, 0, .5f, 1, .5f);
    public static final AxisAlignedBB CORNER_DOWN_SOUTH_EAST = new AxisAlignedBB(.5f, 0, .5f, 1, .5f, 1);
    public static final AxisAlignedBB CORNER_DOWN_SOUTH_WEST = new AxisAlignedBB(0, 0, .5f, .5f, .5f, 1);
    public static final AxisAlignedBB CORNER_DOWN_NORTH_EAST = new AxisAlignedBB(.5f, 0, 0, 1, .5f, .5f);
    public static final AxisAlignedBB CORNER_DOWN_NORTH_WEST = new AxisAlignedBB(0, 0, 0, .5f, .5f, .5f);
}