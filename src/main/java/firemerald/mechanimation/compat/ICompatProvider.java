package firemerald.mechanimation.compat;

import javax.annotation.Nullable;

import firemerald.api.core.IFMLEventHandler;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;

public interface ICompatProvider
{
	public ArtifactVersion getVersion();

	default public String getModID()
	{
		return getVersion().getLabel();
	}

	public @Nullable IFMLEventHandler getEventHandler();

	default public boolean isValid(ModContainer modContainer)
	{
		return getVersion().containsVersion(modContainer.getProcessedVersion());
	}

	public boolean isPresent();

	public void setPresent();
}