package firemerald.mechanimation.compat.enderio;

import firemerald.api.core.IFMLEventHandler;
import firemerald.mechanimation.compat.CompatProviderBase;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;

public class CompatProviderEnderIO extends CompatProviderBase
{
	public static final CompatProviderEnderIO INSTANCE = new CompatProviderEnderIO();

	public CompatProviderEnderIO()
	{
		super(new DefaultArtifactVersion("enderio", true));
	}

	@Override
	public IFMLEventHandler getEventHandler()
	{
		return EnderIOCompat.INSTANCE;
	}
}