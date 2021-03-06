package firemerald.mechanimation.compat.forgemultipart;

import firemerald.api.core.CompatProviderBase;
import firemerald.api.core.IFMLEventHandler;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;

public class CompatProviderForgeMultipart extends CompatProviderBase
{
	public static final CompatProviderForgeMultipart INSTANCE = new CompatProviderForgeMultipart();

	public CompatProviderForgeMultipart()
	{
		super(new DefaultArtifactVersion("forgemultipartcbe", true));
	}

	@Override
	public IFMLEventHandler getEventHandler()
	{
		return ForgeMultipartCompat.INSTANCE;
	}
}