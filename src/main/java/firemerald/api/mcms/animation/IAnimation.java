package firemerald.api.mcms.animation;

import java.util.Collection;
import java.util.Map;

import org.joml.Matrix4d;

import firemerald.api.data.AbstractElement;
import firemerald.api.mcms.model.Bone;
import firemerald.api.mcms.model.IRigged;
import firemerald.api.mcms.model.ITransformsProvider;
import firemerald.api.mcms.util.IClonableObject;
import firemerald.api.mcms.util.ISaveable;

public interface IAnimation extends ISaveable, IClonableObject<IAnimation>
{
	/**
	 * gets the transformation matrices to apply to the given bones.
	 * By using an animation this way, an animated object need only track the frame time for it's animations.
	 *
	 * @param map a list of already computed transformations, generally from other animations
	 * @param frame the current frame time
	 * @param bones the model's bones
	 * @return a map paring bones to transformation matrices
	 */
	public Map<String, Matrix4d> getBones(Map<String, Matrix4d> map, float frame, Collection<? extends Bone<?>> bones);

	/**
	 * gets the animation's running length, or loop length for looping animations. This can be used to detect when an animation has completed.
	 * @return the animation's total length (use {@link Float#POSITIVE_INFINITY} for custom animations that have no specified length)
	 */
	public float getLength();

	/**
	 * gets the animation timestamp from the playing timestamp.
	 *
	 * @param timestamp the playing timestamp
	 * @return the animation timestamp
	 */
	public float getAnimTime(float timestamp);

	public void reverseAnimation(IRigged<?, ?> rig);

	public boolean isRelative();

	public void setRelative(boolean relative, ITransformsProvider transforms);

	public static IAnimation loadAnimation(AbstractElement el)
	{
		switch (el.getName())
		{
		case "animation":
			return new Animation(el);
		case "pose":
			return new Pose(el);
		default:
			return null;
		}
	}
}