package firemerald.mechanimation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import firemerald.api.core.CoreModMainClass;
import firemerald.api.core.IChunkLoader;
import firemerald.api.core.IFMLEventHandler;
import firemerald.api.mcms.MCMSAPI;
import firemerald.mechanimation.client.ClientProxy;
import firemerald.mechanimation.common.CommonProxy;
import firemerald.mechanimation.compat.ICompatProvider;
import firemerald.mechanimation.compat.enderio.CompatProviderEnderIO;
import firemerald.mechanimation.compat.galacticraft.core.CompatProviderGalacticraftCore;
import firemerald.mechanimation.compat.galacticraft.planets.CompatProviderGalacticraftPlanets;
import firemerald.mechanimation.compat.jei.CompatProviderJEI;
import firemerald.mechanimation.compat.mekanism.CompatProviderMekanism;
import firemerald.mechanimation.compat.rf.CompatProviderRF;
import firemerald.mechanimation.compat.tconstruct.CompatProviderTConstruct;
import firemerald.mechanimation.compat.thermalexpansion.CompatProviderThermalExpansion;
import firemerald.mechanimation.plugin.Core;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Main extends CoreModMainClass<CommonProxy> implements LoadingCallback
{
	static
	{
		MCMSAPI.init();
	}

	//TODO BC combustion engine fuels
	private static Main instance;

	public static final ICompatProvider[] COMPAT_PROVIDERS = new ICompatProvider[] {
			CompatProviderTConstruct.INSTANCE,
			CompatProviderGalacticraftCore.INSTANCE,
			CompatProviderGalacticraftPlanets.INSTANCE,
			CompatProviderThermalExpansion.INSTANCE,
			CompatProviderEnderIO.INSTANCE,
			CompatProviderRF.INSTANCE,
			CompatProviderMekanism.INSTANCE,
			CompatProviderJEI.INSTANCE
	};

    public static final Logger LOGGER = LogManager.getLogger("Mechanimation"); //has to be static to prevent a crash

	public static Main instance()
	{
		return instance;
	}

	public static Logger logger()
	{
		return LOGGER;
	}

	public static CommonProxy proxy()
	{
		return instance.proxy;
	}

	public static SimpleNetworkWrapper network()
	{
		return instance.network;
	}

	public Main()
	{
		instance = this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected CommonProxy makeClientProxy()
	{
		return new ClientProxy();
	}

	@Override
	@SideOnly(Side.SERVER)
	protected CommonProxy makeServerProxy()
	{
		return new CommonProxy();
	}

	@Override
	public ModContainer getModContainer()
	{
		return Core.getInstance();
	}

	@Override
	public LoadController getLoadController()
	{
		return Core.loadController;
	}

	@Override
    public void onPreInitialization(FMLPreInitializationEvent event)
    {
		ForgeChunkManager.setForcedChunkLoadingCallback(this, this);
		Map<String, ModContainer> mods = Loader.instance().getIndexedModList();
		for (ICompatProvider compatProvider : COMPAT_PROVIDERS)
		{
			ModContainer modContainer = mods.get(compatProvider.getModID());
			if (modContainer != null && compatProvider.isValid(modContainer))
			{
				compatProvider.setPresent();
				IFMLEventHandler handler = compatProvider.getEventHandler();
				if (handler != null) addFMLEventHandler(handler);
			}
		}
		super.onPreInitialization(event);
    }

	@Override
	public void ticketsLoaded(List<Ticket> tickets, World world)
	{
		List<Ticket> invalid = new ArrayList<>();
		tickets.forEach(ticket -> {
			switch (ticket.getType())
			{
			case ENTITY:
				invalid.add(ticket);
				break;
			case NORMAL:
				NBTTagCompound tag = ticket.getModData();
				int x = tag.getInteger("x");
				int y = tag.getInteger("y");
				int z = tag.getInteger("z");
				BlockPos pos = new BlockPos(x, y, z);
				TileEntity tile = world.getTileEntity(pos);
				if (!(tile instanceof IChunkLoader && ((IChunkLoader) tile).setTicket(ticket))) invalid.add(ticket);
				break;
			default:
				invalid.add(ticket);
			}
		});
		invalid.forEach(ForgeChunkManager::releaseTicket);
	}
}