package firemerald.mechanimation.init;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.compat.enderio.CompatProviderEnderIO;
import firemerald.mechanimation.compat.mekanism.CompatProviderMekanism;
import firemerald.mechanimation.compat.tconstruct.CompatProviderTConstruct;
import firemerald.mechanimation.fluid.CustomFluidLiquid;
import firemerald.mechanimation.fluid.CustomFluidMetal;
import firemerald.mechanimation.fluid.CustomGas;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasRegistry;
import mekanism.api.gas.GasStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class MechanimationFluids
{
	public static final CustomFluidLiquid NITRIC_ACID = (CustomFluidLiquid) new CustomFluidLiquid("nitric_acid",
			0x20FFFFFF).setViscosity(10000);
	public static final CustomFluidLiquid SULFURIC_ACID = (CustomFluidLiquid) new CustomFluidLiquid("sulfuric_acid",
			0x40FFFF7F).setViscosity(10000);
	public static final CustomFluidLiquid GLYCEROL = (CustomFluidLiquid) new CustomFluidLiquid("glycerol",
			0x20FFFFFF).setViscosity(10000);
	public static final CustomFluidLiquid NITROGLYCERIN = (CustomFluidLiquid) new CustomFluidLiquid("nitroglycerin",
			0x40FFBF7F).setViscosity(10000);
	public static final CustomFluidLiquid LIQUID_REDSTONE = (CustomFluidLiquid) new CustomFluidLiquid("redstone", 0xFF930000).setDoesntVaporize().setTemperature(1000).setLuminosity(12).setDensity(1000).setViscosity(3000); //TODO project red
	public static final CustomFluidMetal //TODO temperature
	LIQUID_TUNGSTEN = (CustomFluidMetal) new CustomFluidMetal("tungsten", 0xFF9a9590).setDoesntVaporize().setTemperature(1000).setLuminosity(12).setDensity(1000).setViscosity(3000),
	LIQUID_TITANIUM = (CustomFluidMetal) new CustomFluidMetal("titanium", 0xFF1a1f2a).setDoesntVaporize().setTemperature(1000).setLuminosity(12).setDensity(1000).setViscosity(3000);
	public static final CustomFluidMetal  CRUDE_OIL          = (CustomFluidMetal)  new CustomFluidMetal( "crude_oil"         , 0xFF2D2D27).setDoesntVaporize()                          .setDensity(1000).setViscosity(3000);
	public static final CustomFluidMetal  DESALTED_CRUDE_OIL = (CustomFluidMetal)  new CustomFluidMetal( "desalted_crude_oil", 0xFF141400).setDoesntVaporize()                          .setDensity(1000).setViscosity(3000);
	public static final CustomFluidMetal  TAR                = (CustomFluidMetal)  new CustomFluidMetal( "tar"               , 0xFF0D0D0D).setDoesntVaporize().setTemperature(400).setDensity(1000).setViscosity(3000);
	public static final CustomFluidLiquid GASOLINE           = (CustomFluidLiquid) new CustomFluidLiquid("gasoline"          , 0x4DF4CD49).setDoesntVaporize();
	public static final CustomFluidLiquid KEROSENE           = (CustomFluidLiquid) new CustomFluidLiquid("kerosene"          , 0x307F7F7F).setDoesntVaporize();
	@Nullable
	public static Fluid brine, rocketFuel, liquidIron, liquidGold, liquidNickel, liquidCopper, liquidAluminum, liquidTin, liquidSilver, liquidSteel;
	public static final CustomGas
	UNTREATED_NAPHTHA = new CustomGas("untreated_naphtha", 0xB5B479),
	NAPHTHA           = new CustomGas("naphtha"          , 0xE2DF65),
	FUEL_GAS          = new CustomGas("fuel_gas"         , 0xAFAFAF),
	PROPANE           = new CustomGas("propane"          , 0xFFC489),
	HYDROGEN_SULFIDE  = new CustomGas("hydrogen_sulfide" , 0xFFFFDB);
	@Nullable
	public static Gas
	hydrogen,
	oxygen,
	sodium,
	chlorine;

	public static void init()
	{
		registerFluid(NITRIC_ACID);
		registerFluid(SULFURIC_ACID);
		registerFluid(GLYCEROL);
		registerFluid(NITROGLYCERIN);
		registerFluid(LIQUID_REDSTONE);
		registerFluid(LIQUID_TUNGSTEN);
		registerFluid(LIQUID_TITANIUM);
		registerFluid(CRUDE_OIL);
		registerFluid(DESALTED_CRUDE_OIL);
		registerFluid(TAR);
		registerFluid(GASOLINE);
		registerFluid(KEROSENE);
		GasRegistry.register(UNTREATED_NAPHTHA);
		GasRegistry.register(NAPHTHA);
		GasRegistry.register(FUEL_GAS);
		GasRegistry.register(PROPANE);
		GasRegistry.register(HYDROGEN_SULFIDE);
		if (!CompatProviderMekanism.INSTANCE.isPresent())
		{
			registerFluid(brine = new CustomFluidLiquid("brine", 0xDFFEEF9C)); //TODO set using compat
			GasRegistry.register(sodium   = new CustomGas("sodium"  , 0xE9FEF4)); //TODO set using compat
			GasRegistry.register(chlorine = new CustomGas("chlorine", 0xCFE800)); //TODO set using compat
			GasRegistry.register(hydrogen = new CustomGas("hydrogen", 0xFFFFFF)); //TODO set using compat
			GasRegistry.register(oxygen   = new CustomGas("oxygen"  , 0x6CE2FF)); //TODO set using compat
		}
		if (!CompatProviderEnderIO.INSTANCE.isPresent())
		{
			registerFluid(rocketFuel = new CustomFluidLiquid("rocket_fuel", 0x207F7F7F)); //TODO set using compat
		}
		if (!CompatProviderTConstruct.INSTANCE.isPresent())
		{
			registerFluid(liquidIron = new CustomFluidMetal("iron", 0xFFa81212).setTemperature(769).setLuminosity(12).setDensity(1000).setViscosity(3000));
			registerFluid(liquidGold = new CustomFluidMetal("gold", 0xFFf6d609).setTemperature(532).setLuminosity(12).setDensity(1000).setViscosity(3000));
			registerFluid(liquidNickel = new CustomFluidMetal("nickel", 0xFFffffd9).setTemperature(727).setLuminosity(12).setDensity(1000).setViscosity(3000));
			registerFluid(liquidCopper = new CustomFluidMetal("copper", 0xFFeba151).setTemperature(542).setLuminosity(12).setDensity(1000).setViscosity(3000));
			registerFluid(liquidAluminum = new CustomFluidMetal("aluminum", 0xFFeef4fb).setTemperature(330).setLuminosity(12).setDensity(1000).setViscosity(3000));
			registerFluid(liquidTin = new CustomFluidMetal("tin", 0xFFdadbd9).setTemperature(350).setLuminosity(12).setDensity(1000).setViscosity(3000));
			registerFluid(liquidSilver = new CustomFluidMetal("silver", 0xFFe6f7fe).setTemperature(480).setLuminosity(12).setDensity(1000).setViscosity(3000));
			registerFluid(liquidSteel = new CustomFluidMetal("steel", 0xFF898989).setTemperature(681).setLuminosity(12).setDensity(1000).setViscosity(3000));
		}
	}

	public static void registerFluid(Fluid fluid)
	{
		FluidRegistry.registerFluid(fluid);
		FluidRegistry.addBucketForFluid(fluid);
	}

	public static List<FluidOrGasStack> forName(String name, int size)
	{
		Fluid fluid = FluidRegistry.getFluid(name);
		Gas gas = GasRegistry.getGas(name);
		if (fluid == null)
		{
			if (gas == null) return Collections.emptyList();
			else return Collections.singletonList(FluidOrGasStack.forGas(new GasStack(gas, size)));
		}
		else
		{
			if (gas == null) return Collections.singletonList(FluidOrGasStack.forFluid(new FluidStack(fluid, size)));
			else
			{
				List<FluidOrGasStack> list = new ArrayList<>();
				list.add(FluidOrGasStack.forFluid(new FluidStack(fluid, size)));
				list.add(FluidOrGasStack.forGas(new GasStack(gas, size)));
				return list;
			}
		}
	}
}