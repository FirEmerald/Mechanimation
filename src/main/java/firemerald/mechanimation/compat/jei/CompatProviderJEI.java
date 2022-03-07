package firemerald.mechanimation.compat.jei;

import firemerald.api.core.IFMLEventHandler;
import firemerald.mechanimation.compat.CompatProviderBase;
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