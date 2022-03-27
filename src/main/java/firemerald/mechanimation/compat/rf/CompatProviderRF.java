package firemerald.mechanimation.compat.rf;

import firemerald.api.core.CompatProviderBase;
import firemerald.api.core.IFMLEventHandler;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;

public class CompatProviderRF extends CompatProviderBase
{
	public static final CompatProviderRF INSTANCE = new CompatProviderRF();

	public CompatProviderRF()
	{
		super(new DefaultArtifactVersion("redstoneflux", true));
	}

	@Override
	public IFMLEventHandler getEventHandler()
	{
		return null;
	}
}