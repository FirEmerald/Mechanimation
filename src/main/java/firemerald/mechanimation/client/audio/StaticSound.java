package firemerald.mechanimation.client.audio;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class StaticSound implements ISound
{
    protected Sound sound;
    @Nullable
    private SoundEventAccessor soundEvent;
    public SoundCategory category;
    public ResourceLocation positionedSoundLocation;
    public float volume;
    public float pitch;
    public boolean repeat;
    /** The number of ticks between repeating the sound */
    public int repeatDelay;
    public final EntityPlayer player;

    public StaticSound(SoundEvent soundIn, SoundCategory categoryIn)
    {
        this(soundIn.getSoundName(), categoryIn);
    }

    public StaticSound(ResourceLocation soundId, SoundCategory categoryIn)
    {
        this.volume = 1.0F;
        this.pitch = 1.0F;
        this.positionedSoundLocation = soundId;
        this.category = categoryIn;
        this.player = Minecraft.getMinecraft().player;
    }

    @Override
	public ResourceLocation getSoundLocation()
    {
        return this.positionedSoundLocation;
    }

    @Override
	public SoundEventAccessor createAccessor(SoundHandler handler)
    {
        this.soundEvent = handler.getAccessor(this.positionedSoundLocation);

        if (this.soundEvent == null)
        {
            this.sound = SoundHandler.MISSING_SOUND;
        }
        else
        {
            this.sound = this.soundEvent.cloneEntry();
        }

        return this.soundEvent;
    }

    @Override
	public Sound getSound()
    {
        return this.sound;
    }

    @Override
	public SoundCategory getCategory()
    {
        return this.category;
    }

    @Override
	public boolean canRepeat()
    {
        return this.repeat;
    }

    @Override
	public int getRepeatDelay()
    {
        return this.repeatDelay;
    }

    @Override
	public float getVolume()
    {
        return this.volume * this.sound.getVolume();
    }

    @Override
	public float getPitch()
    {
        return this.pitch * this.sound.getPitch();
    }

    @Override
	public float getXPosF()
    {
        return (float) player.posX;
    }

    @Override
	public float getYPosF()
    {
        return (float) player.posY;
    }

    @Override
	public float getZPosF()
    {
        return (float) player.posZ;
    }

    @Override
	public ISound.AttenuationType getAttenuationType()
    {
        return ISound.AttenuationType.NONE;
    }
}