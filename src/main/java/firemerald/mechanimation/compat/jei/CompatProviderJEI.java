package firemerald.mechanimation.compat.jei;

import firemerald.api.core.CompatProviderBase;
import firemerald.api.core.IFMLEventHandler;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;

public class CompatProviderJEI extends CompatProviderBase
{
	public static final CompatProviderJEI INSTANCE = new CompatProviderJEI();

	public CompatProviderJEI()
	{
		super(new DefaultArtifactVersion("jei", true));
	}

	@Override
	public IFMLEventHandler getEventHandler()
	{
		return JEICompat.INSTANCE;
	}
}