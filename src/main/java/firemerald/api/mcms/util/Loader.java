package firemerald.api.mcms.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import com.google.common.collect.MapMaker;

import firemerald.api.mcms.MCMSAPI;
import firemerald.api.mcms.animation.IAnimation;
import firemerald.api.mcms.data.AbstractElement;
import firemerald.api.mcms.model.ObjData;
import firemerald.api.mcms.model.ObjModel;
import firemerald.api.mcms.model.ObjModel.Client;
import firemerald.api.mcms.model.Skeleton;
import firemerald.api.mcms.model.effects.EffectsData;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Loader //optimized to keep needed models in memory and remove unused ones. Please remember to use fields to hold the resourcelocations of items so they don't get dumped from memory all the time!
{
	public static final Loader INSTANCE = new Loader();

	public static class Reference<T>
	{
		private T val;

		public <V extends T> Reference(V val)
		{
			this.val = val;
		}

		public <V extends T> void set(V val)
		{
			this.val = val;
		}

		public T get()
		{
			return val;
		}
	}

	private final MapMaker maker = new MapMaker().weakValues();
	private final ConcurrentMap<ResourceLocation, Reference<Skeleton>> loadedSkeletons = maker.makeMap();
	private final ConcurrentMap<ResourceLocation, Reference<ObjData>> loadedOBJData = maker.makeMap();
	private final ConcurrentMap<Pair<Supplier<Skeleton>, Supplier<ObjData>>, Reference<ObjModel<?, ?>>> loadedServerModels = maker.makeMap();
	@SideOnly(Side.CLIENT)
	private ConcurrentMap<ResourceLocation, Reference<EffectsData>> loadedEffectsData;
	@SideOnly(Side.CLIENT)
	private ConcurrentMap<Triple<Supplier<Skeleton>, Supplier<ObjData>, Supplier<EffectsData>>, Reference<ObjModel.Client>> loadedClientModels;
	private final ConcurrentMap<ResourceLocation, Reference<IAnimation>> loadedAnimations = maker.makeMap();

	private Loader()
	{
		if (FMLCommonHandler.instance().getSide().isClient()) initClient();
	}

	@SideOnly(Side.CLIENT)
	public void initClient()
	{
		loadedEffectsData = maker.makeMap();
		loadedClientModels = maker.makeMap();
	}

	private Skeleton loadSkeleton(ResourceLocation location)
	{
		InputStream in = null;
		try
		{
			in = ResourceLoader.getResource(location, "mcms");
			AbstractElement el = FileUtil.readStream(in);
			return new Skeleton(el);
		}
		catch (IOException e)
		{
			MCMSAPI.LOGGER.warn("Could not load skeleton from " + location, e);
			FileUtil.closeSafe(in);
			return null;
		}
	}

	public Reference<Skeleton> getSkeleton(ResourceLocation location)
	{
		Reference<Skeleton> skel = loadedSkeletons.get(location);
		if (skel == null) loadedSkeletons.put(location, skel = new Reference<>(loadSkeleton(location))); //does not exist, load it
		return skel;
	}

	private ObjData loadObjData(ResourceLocation location)
	{
		InputStream in = null;
		try
		{
			in = ResourceLoader.getResource(location, "mcms");
			return new ObjData(in);
		}
		catch (Exception e)
		{
			MCMSAPI.LOGGER.warn("Could not load obj data from " + location, e);
			FileUtil.closeSafe(in);
			return null;
		}
	}

	public Reference<ObjData> getObjData(ResourceLocation location)
	{
		Reference<ObjData> obj = loadedOBJData.get(location);
		if (obj == null) loadedOBJData.put(location, obj = new Reference<>(loadObjData(location))); //does not exist, load it
		return obj;
	}

	public Reference<ObjModel<?, ?>> getServerObjModel(ResourceLocation skeleton, ResourceLocation objData)
	{
		return getServerObjModel(getSkeleton(skeleton)::get, getObjData(objData)::get);
	}

	public Reference<ObjModel<?, ?>> getServerObjModel(Supplier<Skeleton> skeleton, ResourceLocation objData)
	{
		return getServerObjModel(skeleton, getObjData(objData)::get);
	}

	public Reference<ObjModel<?, ?>> getServerObjModel(ResourceLocation skeleton, Supplier<ObjData> objData)
	{
		return getServerObjModel(getSkeleton(skeleton)::get, objData);
	}

	public Reference<ObjModel<?, ?>> getServerObjModel(Supplier<Skeleton> skeleton, Supplier<ObjData> objData) //TODO account for null skeleton/obj
	{
		Pair<Supplier<Skeleton>, Supplier<ObjData>> pair = Pair.of(skeleton, objData);
		Reference<ObjModel<?, ?>> obj = loadedServerModels.get(pair);
		if (obj == null) loadedServerModels.put(pair, obj = new Reference<>(new ObjModel.Server(objData.get(), skeleton.get()))); //does not exist, create it
		return obj;
	}

	private EffectsData loadEffectsData(ResourceLocation location)
	{
		InputStream in = null;
		try
		{
			in = ResourceLoader.getResource(location, "mcms");
			AbstractElement el = FileUtil.readStream(in);
			return new EffectsData(el);
		}
		catch (IOException e)
		{
			MCMSAPI.LOGGER.warn("Could not load effects data from " + location, e);
			FileUtil.closeSafe(in);
			return null;
		}
	}

	@SideOnly(Side.CLIENT)
	public Reference<EffectsData> getEffectsData(ResourceLocation location)
	{
		Reference<EffectsData> effects = loadedEffectsData.get(location);
		if (effects == null) loadedEffectsData.put(location, effects = new Reference<>(loadEffectsData(location))); //does not exist, load it
		return effects;
	}

	@SideOnly(Side.CLIENT)
	public Reference<ObjModel.Client> getClientObjModel(ResourceLocation skeleton, ResourceLocation objData)
	{
		return getClientObjModel(getSkeleton(skeleton)::get, getObjData(objData)::get, (Supplier<EffectsData>) () -> null);
	}

	@SideOnly(Side.CLIENT)
	public Reference<ObjModel.Client> getClientObjModel(Supplier<Skeleton> skeleton, ResourceLocation objData)
	{
		return getClientObjModel(skeleton, getObjData(objData)::get, (Supplier<EffectsData>) () -> null);
	}

	@SideOnly(Side.CLIENT)
	public Reference<ObjModel.Client> getClientObjModel(ResourceLocation skeleton, Supplier<ObjData> objData)
	{
		return getClientObjModel(getSkeleton(skeleton)::get, objData, (Supplier<EffectsData>) () -> null);
	}

	@SideOnly(Side.CLIENT)
	public Reference<ObjModel.Client> getClientObjModel(Supplier<Skeleton> skeleton, Supplier<ObjData> objData)
	{
		return getClientObjModel(skeleton, objData, (Supplier<EffectsData>) () -> null);
	}

	@SideOnly(Side.CLIENT)
	public Reference<ObjModel.Client> getClientObjModel(ResourceLocation skeleton, ResourceLocation objData, ResourceLocation effectsData)
	{
		return getClientObjModel(getSkeleton(skeleton)::get, getObjData(objData)::get, getEffectsData(effectsData)::get);
	}

	@SideOnly(Side.CLIENT)
	public Reference<ObjModel.Client> getClientObjModel(Supplier<Skeleton> skeleton, ResourceLocation objData, ResourceLocation effectsData)
	{
		return getClientObjModel(skeleton, getObjData(objData)::get, getEffectsData(effectsData)::get);
	}

	@SideOnly(Side.CLIENT)
	public Reference<ObjModel.Client> getClientObjModel(ResourceLocation skeleton, Supplier<ObjData> objData, ResourceLocation effectsData)
	{
		return getClientObjModel(getSkeleton(skeleton)::get, objData, getEffectsData(effectsData)::get);
	}

	@SideOnly(Side.CLIENT)
	public Reference<ObjModel.Client> getClientObjModel(Supplier<Skeleton> skeleton, Supplier<ObjData> objData, ResourceLocation effectsData)
	{
		return getClientObjModel(skeleton, objData, getEffectsData(effectsData)::get);
	}

	@SideOnly(Side.CLIENT)
	public Reference<ObjModel.Client> getClientObjModel(ResourceLocation skeleton, ResourceLocation objData, Supplier<EffectsData> effectsData)
	{
		return getClientObjModel(getSkeleton(skeleton)::get, getObjData(objData)::get, effectsData);
	}

	@SideOnly(Side.CLIENT)
	public Reference<ObjModel.Client> getClientObjModel(Supplier<Skeleton> skeleton, ResourceLocation objData, Supplier<EffectsData> effectsData)
	{
		return getClientObjModel(skeleton, getObjData(objData)::get, effectsData);
	}

	@SideOnly(Side.CLIENT)
	public Reference<ObjModel.Client> getClientObjModel(ResourceLocation skeleton, Supplier<ObjData> objData, Supplier<EffectsData> effectsData)
	{
		return getClientObjModel(getSkeleton(skeleton)::get, objData, effectsData);
	}

	@SideOnly(Side.CLIENT)
	public Reference<ObjModel.Client> getClientObjModel(Supplier<Skeleton> skeleton, Supplier<ObjData> objData, Supplier<EffectsData> effectsData) //TODO account for null skeleton/obj
	{
		Triple<Supplier<Skeleton>, Supplier<ObjData>, Supplier<EffectsData>> triple = Triple.of(skeleton, objData, effectsData);
		Reference<Client> obj = loadedClientModels.get(triple);
		if (obj == null) //does not exist, create it
		{
			obj = new Reference<>(new ObjModel.Client(objData.get(), skeleton.get(), effectsData.get()));
			loadedClientModels.put(triple, obj);
		}
		return obj;
	}

	public IAnimation loadAnimation(ResourceLocation location)
	{
		InputStream in = null;
		try
		{
			in = ResourceLoader.getResource(location, "mcms");
			AbstractElement el = FileUtil.readStream(in);
			return IAnimation.loadAnimation(el);
		}
		catch (IOException e)
		{
			MCMSAPI.LOGGER.warn("Could not load animation from " + location, e);
			FileUtil.closeSafe(in);
			return null;
		}
	}

	public Reference<IAnimation> getAnimation(ResourceLocation location)
	{
		Reference<IAnimation> anim = loadedAnimations.get(location);
		if (anim == null) loadedAnimations.put(location, anim = new Reference<>(loadAnimation(location))); //does not exist, load it
		return anim;
	}

	public void reload() //reloads all loaded skeletons, obj data, server models, effects data, client models, and animations, if possible. if an item was created not using this loader, or a model using an item not created using this loader, it cannot be reloaded and must be manually reloaded by the mod author.
	{
		this.loadedSkeletons.forEach((location, reference) -> reference.set(loadSkeleton(location)));
		this.loadedOBJData.forEach((location, reference) -> reference.set(loadObjData(location)));
		this.loadedServerModels.forEach((key, val) -> val.set(new ObjModel.Server(key.getRight().get(), key.getLeft().get())));
		if (FMLCommonHandler.instance().getSide().isClient()) reloadClient();
		this.loadedAnimations.forEach((location, reference) -> reference.set(loadAnimation(location)));
	}

	@SideOnly(Side.CLIENT)
	public void reloadClient()
	{
		this.loadedEffectsData.forEach((location, reference) -> reference.set(loadEffectsData(location)));
		this.loadedClientModels.forEach((key, val) -> val.set(new ObjModel.Client(key.getMiddle().get(), key.getLeft().get(), key.getRight().get())));
	}
}