package firemerald.mechanimation.fluid;

import firemerald.mechanimation.api.MechanimationAPI;
import net.minecraft.util.ResourceLocation;

public class CustomFluidLiquid extends CustomFluid
{
	private static final ResourceLocation STILL = new ResourceLocation(MechanimationAPI.MOD_ID, "fluids/liquid_still");
	private static final ResourceLocation FLOWING = new ResourceLocation(MechanimationAPI.MOD_ID, "fluids/liquid_flowing");

    public CustomFluidLiquid(String fluidName)
    {
        super(fluidName, STILL, FLOWING);
    }

    public CustomFluidLiquid(String fluidName, int mapColor)
    {
    	super(fluidName, STILL, FLOWING, mapColor);
    }
}