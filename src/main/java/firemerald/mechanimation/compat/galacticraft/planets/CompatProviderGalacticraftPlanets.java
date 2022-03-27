package firemerald.mechanimation.compat.galacticraft.planets;

import firemerald.api.core.CompatProviderBase;
import firemerald.api.core.IFMLEventHandler;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;

public class CompatProviderGalacticraftPlanets extends CompatProviderBase
{
	public static final CompatProviderGalacticraftPlanets INSTANCE = new CompatProviderGalacticraftPlanets();

	public CompatProviderGalacticraftPlanets()
	{
		super(new DefaultArtifactVersion("galacticraftplanets", true));
	}

	@Override
	public IFMLEventHandler getEventHandler()
	{
		return GalacticraftPlanetsCompat.INSTANCE;
	}
}