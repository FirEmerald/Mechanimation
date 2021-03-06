package firemerald.mechanimation.compat.thermalexpansion;

import firemerald.api.core.CompatProviderBase;
import firemerald.api.core.IFMLEventHandler;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;

public class CompatProviderThermalExpansion extends CompatProviderBase
{
	public static final CompatProviderThermalExpansion INSTANCE = new CompatProviderThermalExpansion();

	public CompatProviderThermalExpansion()
	{
		super(new DefaultArtifactVersion("thermalexpansion", true));
	}

	@Override
	public IFMLEventHandler getEventHandler()
	{
		return ThermalExpansionCompat.INSTANCE;
	}
}