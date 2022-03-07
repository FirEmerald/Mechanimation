package firemerald.mechanimation.compat.mekanism;

import firemerald.api.core.IFMLEventHandler;
import firemerald.mechanimation.compat.CompatProviderBase;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;

public class CompatProviderMekanism extends CompatProviderBase
{
	public static final CompatProviderMekanism INSTANCE = new CompatProviderMekanism();

	public CompatProviderMekanism()
	{
		super(new DefaultArtifactVersion("mekanism", true));
	}

	@Override
	public IFMLEventHandler getEventHandler()
	{
		return new MekanismCompat();
	}
}