package firemerald.mechanimation.fluid;

import firemerald.mechanimation.api.MechanimationAPI;
import net.minecraft.util.ResourceLocation;

public class CustomFluidMetal extends CustomFluid
{
	private static final ResourceLocation STILL = new ResourceLocation(MechanimationAPI.MOD_ID, "fluids/metal_still");
	private static final ResourceLocation FLOWING = new ResourceLocation(MechanimationAPI.MOD_ID, "fluids/metal_flowing");

    public CustomFluidMetal(String fluidName)
    {
        super(fluidName, STILL, FLOWING);
    }

    public CustomFluidMetal(String fluidName, int mapColor)
    {
    	super(fluidName, STILL, FLOWING, mapColor);
    }
}