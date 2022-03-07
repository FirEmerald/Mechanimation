package firemerald.mechanimation.fluid;

import firemerald.mechanimation.api.MechanimationAPI;
import mekanism.api.gas.Gas;
import net.minecraft.util.ResourceLocation;

public class CustomGas extends Gas
{
	public static final ResourceLocation DEFAULT_ICON = new ResourceLocation(MechanimationAPI.MOD_ID, "fluids/gas");

	public CustomGas(String s, int t)
	{
		this(s, DEFAULT_ICON, t);
	}

	public CustomGas(String s, ResourceLocation icon, int t)
	{
		super(s, icon);
		setTint(t);
	}
}