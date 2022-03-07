package firemerald.mechanimation.fluid;

import net.minecraft.block.material.Material;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class CustomFluid extends Fluid
{
    protected SoundEvent emptySound = SoundEvents.ITEM_BUCKET_EMPTY;
    protected SoundEvent fillSound = SoundEvents.ITEM_BUCKET_FILL;
    protected boolean isInfinite = false;
    protected boolean doesVaporize = true;

    public CustomFluid(String fluidName, ResourceLocation still, ResourceLocation flowing)
    {
        super(fluidName, still, flowing);
    }

    public CustomFluid(String fluidName, ResourceLocation still, ResourceLocation flowing, int mapColor)
    {
        this(fluidName, still, flowing);
        setColor(mapColor);
    }

    @Override
    public CustomFluid setEmptySound(SoundEvent parSound)
    {
        emptySound = parSound;
        return this;
    }

    @Override
    public SoundEvent getEmptySound()
    {
        return emptySound;
    }

    @Override
    public CustomFluid setFillSound(SoundEvent parSound)
    {
        fillSound = parSound;
        return this;
    }

    @Override
    public SoundEvent getFillSound()
    {
        return fillSound;
    }

    @Override
    public boolean doesVaporize(FluidStack fluidStack)
    {
        if (!doesVaporize || block == null) return false;
        return block.getDefaultState().getMaterial() == Material.WATER;
    }

    public CustomFluid setDoesntVaporize()
    {
    	this.doesVaporize = false;
    	return this;
    }

    public CustomFluid setInfinite()
    {
    	this.isInfinite = true;
    	return this;
    }

    public CustomFluid setFinite()
    {
    	this.isInfinite = true;
    	return this;
    }

    public boolean isInfinite()
    {
    	return isInfinite;
    }


}