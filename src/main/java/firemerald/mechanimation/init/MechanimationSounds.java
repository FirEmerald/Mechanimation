package firemerald.mechanimation.init;

import firemerald.mechanimation.api.MechanimationAPI;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.IForgeRegistry;

public enum MechanimationSounds
{
	LOGBOOK_OPEN("logbook.open"),
	LOGBOOK_CLOSE("logbook.close");

	public final SoundEvent sound;

	private MechanimationSounds(String name)
	{
		ResourceLocation loc = new ResourceLocation(MechanimationAPI.MOD_ID, name);
		sound = new SoundEvent(loc).setRegistryName(loc);
	}

	public static void register(IForgeRegistry<SoundEvent> registry)
	{
		for (MechanimationSounds sound : values()) registry.register(sound.sound);
	}
}