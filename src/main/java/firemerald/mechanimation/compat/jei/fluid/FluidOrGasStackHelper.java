package firemerald.mechanimation.compat.jei.fluid;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.compat.jei.gas.GasStackHelper;
import firemerald.mechanimation.compat.mekanism.CompatProviderMekanism;
import mekanism.api.gas.GasStack;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.plugins.vanilla.ingredients.fluid.FluidStackHelper;
import net.minecraftforge.fluids.FluidStack;

public class FluidOrGasStackHelper implements IIngredientHelper<FluidOrGasStack>
{
	public final IIngredientHelper<FluidStack> fluidStackHelper;
	public final IIngredientHelper<GasStack> gasStackHelper;

	public FluidOrGasStackHelper()
	{
		fluidStackHelper = new FluidStackHelper();
		gasStackHelper = CompatProviderMekanism.INSTANCE.isPresent() ? new mekanism.client.jei.GasStackHelper() : new GasStackHelper();
	}

    @Override
    public List<FluidOrGasStack> expandSubtypes(List<FluidOrGasStack> contained)
    {
    	List<FluidStack> fluids = contained.stream().filter(stack -> stack != null && stack.isFluid()).map(FluidOrGasStack::getFluidStackStatic).collect(Collectors.toList());
    	List<GasStack> gasses = contained.stream().filter(stack -> stack != null && stack.isGas()).map(FluidOrGasStack::getGasStackStatic).collect(Collectors.toList());
    	fluids = fluidStackHelper.expandSubtypes(fluids);
    	gasses = gasStackHelper.expandSubtypes(gasses);
    	contained = Collections.synchronizedList(new ArrayList<>());
    	fluids.parallelStream().map(FluidOrGasStack::forFluid).forEach(contained::add);
    	gasses.parallelStream().map(FluidOrGasStack::forGas).forEach(contained::add);
        return contained;
    }

    @Override
    @Nullable
    public FluidOrGasStack getMatch(Iterable<FluidOrGasStack> ingredients, FluidOrGasStack toMatch)
    {
        for (FluidOrGasStack stack : ingredients) if (toMatch.isFluidEqual(toMatch)) return stack;
        return null;
    }

    @Override
    public String getDisplayName(FluidOrGasStack ingredient)
    {
    	return ingredient.isFluid() ? fluidStackHelper.getDisplayName(ingredient.getFluidStack()) : gasStackHelper.getDisplayName(ingredient.getGasStack());
    }

    @Override
    public String getUniqueId(FluidOrGasStack ingredient)
    {
    	return ingredient.isFluid() ? fluidStackHelper.getUniqueId(ingredient.getFluidStack()) : gasStackHelper.getUniqueId(ingredient.getGasStack());
    }

    @Override
    public String getWildcardId(FluidOrGasStack ingredient)
    {
    	return ingredient.isFluid() ? fluidStackHelper.getWildcardId(ingredient.getFluidStack()) : gasStackHelper.getWildcardId(ingredient.getGasStack());
    }

    @Override
    public String getModId(FluidOrGasStack ingredient)
    {
    	return ingredient.isFluid() ? fluidStackHelper.getModId(ingredient.getFluidStack()) : gasStackHelper.getModId(ingredient.getGasStack());
    }

    @Override
    public Iterable<Color> getColors(FluidOrGasStack ingredient)
    {
    	return ingredient.isFluid() ? fluidStackHelper.getColors(ingredient.getFluidStack()) : gasStackHelper.getColors(ingredient.getGasStack());
    }

    @Override
    public String getResourceId(FluidOrGasStack ingredient)
    {
    	return ingredient.isFluid() ? fluidStackHelper.getResourceId(ingredient.getFluidStack()) : gasStackHelper.getResourceId(ingredient.getGasStack());
    }

    @Override
    public FluidOrGasStack copyIngredient(FluidOrGasStack ingredient)
    {
        return ingredient.copy();
    }

    @Override
    public String getErrorInfo(@Nullable FluidOrGasStack ingredient)
    {
        ToStringHelper toStringHelper = MoreObjects.toStringHelper(FluidOrGasStack.class);
        if (ingredient == null) toStringHelper.add("Name", "null");
        else
        {
        	toStringHelper.add("Name", ingredient.getLocalizedName());
        	toStringHelper.add("Is Fluid", ingredient.isFluid());
        	toStringHelper.add("Is Gas", ingredient.isGas());
        	toStringHelper.add("Amount", ingredient.getAmount());
        }
        return toStringHelper.toString();
    }
}