package net.minecraft.entity;

import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;

public class MechanimationEntityAccess
{
	public static void playHurtSound(EntityLivingBase entity, DamageSource source)
	{
		entity.playHurtSound(source);
	}

	public static void playDeathSound(EntityLivingBase entity)
	{
		SoundEvent sound = entity.getDeathSound();
        if (sound != null)
        {
        	entity.playSound(sound, entity.getSoundVolume(), entity.getSoundPitch());
        }
	}
}