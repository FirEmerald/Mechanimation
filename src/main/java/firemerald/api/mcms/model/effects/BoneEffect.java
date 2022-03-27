package firemerald.api.mcms.model.effects;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import javax.annotation.Nullable;

import org.joml.Matrix4d;

import firemerald.api.mcms.MCMSAPI;
import firemerald.api.mcms.animation.Transformation;
import firemerald.api.data.AbstractElement;
import firemerald.api.mcms.model.IEditableParent;
import firemerald.api.mcms.model.IModelEditable;
import firemerald.api.mcms.model.RenderBone;
import firemerald.api.mcms.util.MCMSUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public abstract class BoneEffect implements IModelEditable
{
	private static final Map<String, BiFunction<RenderBone<?>, AbstractElement, BoneEffect>> EFFECT_TYPES = new HashMap<>();

	/**
	 * Register a Bone Effect type
	 *
	 * @param name the bone effect's name - the bone effect's XML name <i>must</i> be {domain}:{path with "/"'s replaced with "_"'s} or it will not load properly!
	 * @param constructor the constructor lambda - constructs the bone effect. see the static constructor below for examples.
	 *
	 * @return if the bone effect was registered
	 */
	public static boolean registerBoneType(ResourceLocation name, BiFunction<RenderBone<?>, AbstractElement, BoneEffect> constructor)
	{
		return registerBoneType(name.toString().replace('/', '_'), constructor);
	}

	private static boolean registerBoneType(String name, BiFunction<RenderBone<?>, AbstractElement, BoneEffect> constructor)
	{
		if (EFFECT_TYPES.containsKey(name)) return false;
		else
		{
			EFFECT_TYPES.put(name, constructor);
			return true;
		}
	}

	public static BoneEffect constructIfRegistered(String name, @Nullable RenderBone<?> parent, AbstractElement element)
	{
		BiFunction<RenderBone<?>, AbstractElement, BoneEffect> constructor = EFFECT_TYPES.get(name);
		if (constructor == null)
		{
			MCMSAPI.LOGGER.warn("Unknown effect type " + name);
			return null;
		}
		else return constructor.apply(parent, element);
	}

	static
	{
		registerBoneType("item", (parent, element) -> {
			BoneEffect bone = new ItemRenderEffect(element.getString("name", "unnamed item"), parent, new Transformation(), 0);
			bone.loadFromXML(element);
			return bone;
		});
		registerBoneType("fluid", (parent, element) -> {
			BoneEffect bone = new FluidRenderEffect(element.getString("name", "unnamed fluid"), parent, new Transformation(), 0);
			bone.loadFromXML(element);
			return bone;
		});
		registerBoneType("no_lighting", (parent, element) -> {
			BoneEffect bone = new DisableLightingEffect(element.getString("name", "disable lighting"), parent);
			bone.loadFromXML(element);
			return bone;
		});
	}

	protected boolean visible = true;
	protected String name;
	protected RenderBone<?> parent;
	public final Transformation transform;

	public BoneEffect(String name, @Nullable RenderBone<?> parent)
	{
		this(name, parent, new Transformation());
	}

	public BoneEffect(String name, @Nullable RenderBone<?> parent, Transformation transform)
	{
		this.name = name;
		this.transform = transform;
		if (parent == null) this.parent = null;
		else (this.parent = parent).addEffect(this);
	}

	public Matrix4d getTransformation()
	{
		Matrix4d mat = transform.getTransformation();
		if (parent != null) parent.getTransformation().mul(mat, mat);
		return mat;
	}

	public void loadFromXML(AbstractElement el)
	{
		name = el.getString("name", "unnamed");
	}

	public void addToXML(AbstractElement addTo)
	{
		saveToXML(addTo.addChild(getXMLName()));
	}

	public void saveToXML(AbstractElement el)
	{
		el.setString("name", name);
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public void movedTo(IEditableParent oldParent, IEditableParent newParent) {}

	@Override
	public boolean isVisible()
	{
		return visible;
	}

	@Override
	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}

	@Override
	public Transformation getDefaultTransformation()
	{
		return transform;
	}

	@Override
	public Collection<? extends IModelEditable> getChildren()
	{
		return Collections.emptySet();
	}

	@Override
	public boolean hasChildren()
	{
		return false;
	}

	@Override
	public boolean canBeChild(IModelEditable candidate)
	{
		return false;
	}

	@Override
	public void addChild(IModelEditable child) {}

	@Override
	public void addChildBefore(IModelEditable child, IModelEditable position) {}

	@Override
	public void addChildAfter(IModelEditable child, IModelEditable position) {}

	@Override
	public void removeChild(IModelEditable child) {}

	@Override
	public int getChildIndex(IModelEditable child)
	{
		return -1;
	}

	@Override
	public void addChildAt(IModelEditable child, int index) {}

	public void preRender(@Nullable Object holder, Runnable defaultTex)
	{
		GlStateManager.pushMatrix();
		MCMSUtil.glMultMatrix(transform.getTransformation());
		doPreRender(holder, defaultTex);
		GlStateManager.popMatrix();
	}

	public abstract void doPreRender(@Nullable Object holder, Runnable defaultTex);

	public void postRenderBone(@Nullable Object holder, Runnable defaultTex)
	{
		GlStateManager.pushMatrix();
		MCMSUtil.glMultMatrix(transform.getTransformation());
		doPostRenderBone(holder, defaultTex);
		GlStateManager.popMatrix();
	}

	public abstract void doPostRenderBone(@Nullable Object holder, Runnable defaultTex);

	public void postRenderChildren(@Nullable Object holder, Runnable defaultTex)
	{
		GlStateManager.pushMatrix();
		MCMSUtil.glMultMatrix(transform.getTransformation());
		doPostRenderChildren(holder, defaultTex);
		GlStateManager.popMatrix();
	}

	public abstract void doPostRenderChildren(@Nullable Object holder, Runnable defaultTex);

	public abstract String getXMLName();

	public abstract void doCleanUp();

	public abstract BoneEffect cloneObject(RenderBone<?> clonedParent);
}