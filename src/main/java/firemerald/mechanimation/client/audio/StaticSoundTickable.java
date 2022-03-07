package firemerald.mechanimation.client.audio;

import net.minecraft.client.audio.ITickableSound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class StaticSoundTickable extends StaticSound implements ITickableSound
{
	protected boolean donePlaying = false;

	public StaticSoundTickable(SoundEvent soundIn, SoundCategory categoryIn)
    {
        super(soundIn, categoryIn);
    }

	public StaticSoundTickable(ResourceLocation soundId, SoundCategory categoryIn)
	{
		super(soundId, categoryIn);
	}

	@Override
	public void update() {}

	@Override
	public boolean isDonePlaying()
	{
		return donePlaying;
	}
}