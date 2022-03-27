package firemerald.mechanimation.compat.galacticraft.core;

import firemerald.api.core.CompatProviderBase;
import firemerald.api.core.IFMLEventHandler;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;

public class CompatProviderGalacticraftCore extends CompatProviderBase
{
	public static final CompatProviderGalacticraftCore INSTANCE = new CompatProviderGalacticraftCore();

	public CompatProviderGalacticraftCore()
	{
		super(new DefaultArtifactVersion("galacticraftcore", true));
	}

	@Override
	public IFMLEventHandler getEventHandler()
	{
		return GalacticraftCoreCompat.INSTANCE;
	}
}