package firemerald.mechanimation.plugin;

import firemerald.api.core.plugin.TransformerBase;
import firemerald.mechanimation.plugin.transformers.common.InterfaceStripper;

public class Transformer extends TransformerBase
{
	@Override
	public void addCommonTransformers()
	{
		transformers.put("firemerald.mechanimation.util.flux.IFluxConnection", new InterfaceStripper("redstoneflux", "cofh.redstoneflux.api.IEnergyConnection"));
		transformers.put("firemerald.mechanimation.util.flux.IFluxContainerItem", new InterfaceStripper("redstoneflux", "cofh.redstoneflux.api.IEnergyContainerItem"));
		transformers.put("firemerald.mechanimation.util.flux.IFluxHandler", new InterfaceStripper("redstoneflux", "cofh.redstoneflux.api.IEnergyHandler"));
		transformers.put("firemerald.mechanimation.util.flux.IFluxProvider", new InterfaceStripper("redstoneflux", "cofh.redstoneflux.api.IEnergyProvider"));
		transformers.put("firemerald.mechanimation.util.flux.IFluxReceiver", new InterfaceStripper("redstoneflux", "cofh.redstoneflux.api.IEnergyReceiver"));
		transformers.put("firemerald.mechanimation.util.flux.IFluxStorage", new InterfaceStripper("redstoneflux", "cofh.redstoneflux.api.IEnergyStorage"));
	}

	@Override
	public void addClientTransformers() {}
}