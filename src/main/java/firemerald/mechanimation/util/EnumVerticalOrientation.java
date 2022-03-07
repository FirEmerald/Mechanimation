package firemerald.mechanimation.util;

import org.joml.Matrix4d;

import firemerald.api.mcms.math.MathUtils;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;

public enum EnumVerticalOrientation
{
	SOUTH(EnumFacing.SOUTH, new Matrix4d()),
	EAST(EnumFacing.EAST, new Matrix4d().rotateY(90 * MathUtils.DEG_TO_RAD)),
	NORTH(EnumFacing.NORTH, new Matrix4d().rotateY(180 * MathUtils.DEG_TO_RAD)),
	WEST(EnumFacing.WEST, new Matrix4d().rotateY(270 * MathUtils.DEG_TO_RAD));

	public final EnumFacing forward;
	public final Matrix4d transformation;
	public final EnumFace[] forFacing = new EnumFace[6];
	public final EnumFacing[] forFace = new EnumFacing[6];

	EnumVerticalOrientation(EnumFacing forward, Matrix4d transformation)
	{
		this.transformation = transformation;
		this.forward = forward;
		Vec3i rightV = forward.getDirectionVec().crossProduct(EnumFacing.UP.getDirectionVec());
		EnumFacing right = EnumFacing.getFacingFromVector(rightV.getX(),rightV.getY(), rightV.getZ());
		mapFacing(EnumFacing.UP, EnumFace.TOP);
		mapFacing(EnumFacing.DOWN, EnumFace.BOTTOM);
		mapFacing(forward, EnumFace.FRONT);
		mapFacing(forward.getOpposite(), EnumFace.BACK);
		mapFacing(right, EnumFace.RIGHT);
		mapFacing(right.getOpposite(), EnumFace.LEFT);
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

	public static EnumVerticalOrientation forFront(EnumFacing facing, EnumVerticalOrientation def)
	{
		for (EnumVerticalOrientation face : EnumVerticalOrientation.values()) if (face.forward == facing) return face;
		return def;
	}
}