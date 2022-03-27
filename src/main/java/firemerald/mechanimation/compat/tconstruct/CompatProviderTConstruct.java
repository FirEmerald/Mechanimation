package firemerald.mechanimation.compat.tconstruct;

import firemerald.api.core.CompatProviderBase;
import firemerald.api.core.IFMLEventHandler;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;

public class CompatProviderTConstruct extends CompatProviderBase
{
	public static final CompatProviderTConstruct INSTANCE = new CompatProviderTConstruct();

	public CompatProviderTConstruct()
	{
		super(new DefaultArtifactVersion("tconstruct", true));
	}

	@Override
	public IFMLEventHandler getEventHandler()
	{
		return TConstructCompat.INSTANCE;
	}
}