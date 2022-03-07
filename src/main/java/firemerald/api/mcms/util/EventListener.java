package firemerald.api.mcms.util;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.common.eventbus.Subscribe;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EventListener
{
	public static final EventListener INSTANCE = new EventListener();
	@SideOnly(Side.CLIENT)
	private Queue<Runnable> pendingOperations;

	private EventListener()
	{
		if (FMLCommonHandler.instance().getSide().isClient()) pendingOperations = new ConcurrentLinkedQueue<>();
	}

	public void enqueueAction(Runnable action)
	{
		pendingOperations.add(action);
	}

	@Subscribe
	public void onServerStarted(FMLServerStartedEvent event)
	{
		if (FMLCommonHandler.instance().getSide().isClient()) pendingOperations.add(Loader.INSTANCE::reload);
		else Loader.INSTANCE.reload();
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	@SideOnly(Side.CLIENT)
	public void onTick(TickEvent.ClientTickEvent event)
	{
		while (!pendingOperations.isEmpty()) pendingOperations.poll().run();
	}
}