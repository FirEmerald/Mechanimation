package firemerald.mechanimation.util;

import org.joml.Matrix4d;

import firemerald.api.mcms.math.MathUtils;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;

public enum EnumOrientation
{
	UP_SOUTH(EnumFacing.UP, EnumFacing.SOUTH, new Matrix4d()),
	UP_EAST(EnumFacing.UP, EnumFacing.EAST, new Matrix4d().rotateY(90 * MathUtils.DEG_TO_RAD)),
	UP_NORTH(EnumFacing.UP, EnumFacing.NORTH, new Matrix4d().rotateY(180 * MathUtils.DEG_TO_RAD)),
	UP_WEST(EnumFacing.UP, EnumFacing.WEST, new Matrix4d().rotateY(270 * MathUtils.DEG_TO_RAD)),
	DOWN_SOUTH(EnumFacing.DOWN, EnumFacing.SOUTH, new Matrix4d().rotateZ(180 * MathUtils.DEG_TO_RAD)),
	DOWN_WEST(EnumFacing.DOWN, EnumFacing.WEST, new Matrix4d().rotateZ(180 * MathUtils.DEG_TO_RAD).rotateY(90 * MathUtils.DEG_TO_RAD)),
	DOWN_NORTH(EnumFacing.DOWN, EnumFacing.NORTH, new Matrix4d().rotateZ(180 * MathUtils.DEG_TO_RAD).rotateY(180 * MathUtils.DEG_TO_RAD)),
	DOWN_EAST(EnumFacing.DOWN, EnumFacing.EAST, new Matrix4d().rotateZ(180 * MathUtils.DEG_TO_RAD).rotateY(270 * MathUtils.DEG_TO_RAD)),
	NORTH_UP(EnumFacing.NORTH, EnumFacing.UP, new Matrix4d().rotateX(270 * MathUtils.DEG_TO_RAD)),
	NORTH_EAST(EnumFacing.NORTH, EnumFacing.EAST, new Matrix4d().rotateX(270 * MathUtils.DEG_TO_RAD).rotateY(90 * MathUtils.DEG_TO_RAD)),
	NORTH_DOWN(EnumFacing.NORTH, EnumFacing.DOWN, new Matrix4d().rotateX(270 * MathUtils.DEG_TO_RAD).rotateY(180 * MathUtils.DEG_TO_RAD)),
	NORTH_WEST(EnumFacing.NORTH, EnumFacing.WEST, new Matrix4d().rotateX(270 * MathUtils.DEG_TO_RAD).rotateY(270 * MathUtils.DEG_TO_RAD)),
	SOUTH_DOWN(EnumFacing.SOUTH, EnumFacing.DOWN, new Matrix4d().rotateX(90 * MathUtils.DEG_TO_RAD)),
	SOUTH_EAST(EnumFacing.SOUTH, EnumFacing.EAST, new Matrix4d().rotateX(90 * MathUtils.DEG_TO_RAD).rotateY(90 * MathUtils.DEG_TO_RAD)),
	SOUTH_UP(EnumFacing.SOUTH, EnumFacing.UP, new Matrix4d().rotateX(90 * MathUtils.DEG_TO_RAD).rotateY(180 * MathUtils.DEG_TO_RAD)),
	SOUTH_WEST(EnumFacing.SOUTH, EnumFacing.WEST, new Matrix4d().rotateX(90 * MathUtils.DEG_TO_RAD).rotateY(270 * MathUtils.DEG_TO_RAD)),
	EAST_SOUTH(EnumFacing.EAST, EnumFacing.SOUTH, new Matrix4d().rotateZ(-90 * MathUtils.DEG_TO_RAD)),
	EAST_DOWN(EnumFacing.EAST, EnumFacing.DOWN, new Matrix4d().rotateZ(-90 * MathUtils.DEG_TO_RAD).rotateY(90 * MathUtils.DEG_TO_RAD)),
	EAST_NORTH(EnumFacing.EAST, EnumFacing.NORTH, new Matrix4d().rotateZ(-90 * MathUtils.DEG_TO_RAD).rotateY(180 * MathUtils.DEG_TO_RAD)),
	EAST_UP(EnumFacing.EAST, EnumFacing.UP, new Matrix4d().rotateZ(-90 * MathUtils.DEG_TO_RAD).rotateY(270 * MathUtils.DEG_TO_RAD)),
	WEST_SOUTH(EnumFacing.WEST, EnumFacing.SOUTH, new Matrix4d().rotateZ(90 * MathUtils.DEG_TO_RAD)),
	WEST_UP(EnumFacing.WEST, EnumFacing.UP, new Matrix4d().rotateZ(90 * MathUtils.DEG_TO_RAD).rotateY(90 * MathUtils.DEG_TO_RAD)),
	WEST_NORTH(EnumFacing.WEST, EnumFacing.NORTH, new Matrix4d().rotateZ(90 * MathUtils.DEG_TO_RAD).rotateY(180 * MathUtils.DEG_TO_RAD)),
	WEST_DOWN(EnumFacing.WEST, EnumFacing.DOWN, new Matrix4d().rotateZ(90 * MathUtils.DEG_TO_RAD).rotateY(270 * MathUtils.DEG_TO_RAD));

	private static class StaticData
	{
		static final EnumOrientation[] TOP_AND_FRONT = new EnumOrientation[6 * 6]; //allocates 36 but only needs 24
	}

	public static EnumOrientation forFront(EnumFacing front)
	{
		switch (front)
		{
		case EAST: return UP_EAST;
		case NORTH: return UP_NORTH;
		case WEST: return UP_WEST;
		default: return UP_SOUTH;
		}
	}

	public final EnumFacing up, forward;
	public final Matrix4d transformation;
	public final EnumFace[] forFacing = new EnumFace[6];
	public final EnumFacing[] forFace = new EnumFacing[6];

	EnumOrientation(EnumFacing up, EnumFacing forward, Matrix4d transformation)
	{
		this.transformation = transformation;
		this.up = up;
		this.forward = forward;
		Vec3i rightV = forward.getDirectionVec().crossProduct(up.getDirectionVec());
		EnumFacing right = EnumFacing.getFacingFromVector(rightV.getX(),rightV.getY(), rightV.getZ());
		mapFacing(up, EnumFace.TOP);
		mapFacing(up.getOpposite(), EnumFace.BOTTOM);
		mapFacing(forward, EnumFace.FRONT);
		mapFacing(forward.getOpposite(), EnumFace.BACK);
		mapFacing(right, EnumFace.RIGHT);
		mapFacing(right.getOpposite(), EnumFace.LEFT);
		StaticData.TOP_AND_FRONT[up.getIndex() * 6 + forward.getIndex()] = this;
	}

	private void mapFacing(EnumFacing facing, EnumFace face)
	{
		forFacing[facing.getIndex()] = face;
		forFace[face.ordinal()] = facing;
	}

	public EnumFace getFace(EnumFacing facing)
	{
		return facing == null ? null : forFacing[facing.getIndex()];
	}

	public EnumFacing getFacing(EnumFace face)
	{
		return face == null ? null : forFace[face.ordinal()];
	}

	public static EnumOrientation getOrientation(EnumFacing up, EnumFacing forward, EnumOrientation _def)
	{
		if (up == forward || up == forward.getOpposite()) return _def;
		else return StaticData.TOP_AND_FRONT[up.getIndex() * 6 + forward.getIndex()];
	}
}