package firemerald.mechanimation.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import cofh.redstoneflux.api.IEnergyContainerItem;
import firemerald.mechanimation.Main;
import firemerald.mechanimation.api.crafting.FluidOrGasStack;
import firemerald.mechanimation.compat.rf.CompatProviderRF;
import firemerald.mechanimation.util.flux.IFluxContainerItem;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.IGasItem;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.InvalidBlockStateException;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class Utils
{
	public static final Map<String, String> TILE_ENTITY_RENAMED = new HashMap<>();

	public static void registerChangedTileEntityID(ResourceLocation oldID, ResourceLocation newID)
	{
		TILE_ENTITY_RENAMED.put(oldID.toString(), newID.toString());
	}

	public static void fixTileEntityIDs(NBTTagCompound tag)
	{
		String newID = TILE_ENTITY_RENAMED.get(tag.getString("id"));
		if (newID != null) tag.setString("id", newID);
	}

	public static boolean isEnergyProvider(ItemStack stack)
	{
		if (stack.hasCapability(CapabilityEnergy.ENERGY, null))
		{
			return stack.getCapability(CapabilityEnergy.ENERGY, null).canExtract();
		}
		else
		{
			Item item = stack.getItem();
			if (item instanceof IFluxContainerItem)
			{
				return ((IFluxContainerItem) item).getEnergyStored(stack) > 0;
			}
			else if (CompatProviderRF.INSTANCE.isPresent()) //has RF MCMSAPI
			{
				if (item instanceof IEnergyContainerItem)
				{
					return ((IEnergyContainerItem) item).getEnergyStored(stack) > 0;
				}
				else return false;
			}
			else return false;
		}
	}

	public static boolean isEnergyReceiver(ItemStack stack)
	{
		if (stack.hasCapability(CapabilityEnergy.ENERGY, null))
		{
			return stack.getCapability(CapabilityEnergy.ENERGY, null).canReceive();
		}
		else
		{
			Item item = stack.getItem();
			if (item instanceof IFluxContainerItem)
			{
				IFluxContainerItem container = (IFluxContainerItem) item;
				return container.getEnergyStored(stack) < container.getMaxEnergyStored(stack);
			}
			else if (CompatProviderRF.INSTANCE.isPresent()) //has RF MCMSAPI
			{
				if (item instanceof IEnergyContainerItem)
				{
					IEnergyContainerItem container = (IEnergyContainerItem) item;
					return container.getEnergyStored(stack) < container.getMaxEnergyStored(stack);
				}
				else return false;
			}
			else return false;
		}
	}

	public static boolean isBattery(ItemStack stack)
	{
		if (stack.hasCapability(CapabilityEnergy.ENERGY, null)) return true;
		else
		{
			Item item = stack.getItem();
			if (item instanceof IFluxContainerItem) return true;
			else if (CompatProviderRF.INSTANCE.isPresent()) //has RF MCMSAPI
			{
				if (item instanceof IEnergyContainerItem) return true;
				else return false;
			}
			else return false;
		}
	}

	public static int getStoredEnergy(ItemStack stack)
	{
		if (stack.hasCapability(CapabilityEnergy.ENERGY, null))
		{
			return stack.getCapability(CapabilityEnergy.ENERGY, null).getEnergyStored();
		}
		else
		{
			Item item = stack.getItem();
			if (item instanceof IFluxContainerItem)
			{
				return ((IFluxContainerItem) item).getEnergyStored(stack);
			}
			else if (CompatProviderRF.INSTANCE.isPresent()) //has RF MCMSAPI
			{
				if (item instanceof IEnergyContainerItem)
				{
					return ((IEnergyContainerItem) item).getEnergyStored(stack);
				}
				else return 0;
			}
			else return 0;
		}
	}

	public static int getMaxStoredEnergy(ItemStack stack)
	{
		if (stack.hasCapability(CapabilityEnergy.ENERGY, null))
		{
			return stack.getCapability(CapabilityEnergy.ENERGY, null).getMaxEnergyStored();
		}
		else
		{
			Item item = stack.getItem();
			if (item instanceof IFluxContainerItem)
			{
				return ((IFluxContainerItem) item).getMaxEnergyStored(stack);
			}
			else if (CompatProviderRF.INSTANCE.isPresent()) //has RF MCMSAPI
			{
				if (item instanceof IEnergyContainerItem)
				{
					return ((IEnergyContainerItem) item).getMaxEnergyStored(stack);
				}
				else return 0;
			}
			else return 0;
		}
	}

	public static int tryExtract(ItemStack stack, int amount, boolean simulate)
	{
		if (stack.hasCapability(CapabilityEnergy.ENERGY, null))
		{
			return stack.getCapability(CapabilityEnergy.ENERGY, null).extractEnergy(amount, simulate);
		}
		else
		{
			Item item = stack.getItem();
			if (item instanceof IFluxContainerItem)
			{
				return ((IFluxContainerItem) item).extractEnergy(stack, amount, simulate);
			}
			else if (CompatProviderRF.INSTANCE.isPresent()) //has RF MCMSAPI
			{
				if (item instanceof IEnergyContainerItem)
				{
					return ((IEnergyContainerItem) item).extractEnergy(stack, amount, simulate);
				}
				else return 0;
			}
			else return 0;
		}
	}

	public static int tryReceive(ItemStack stack, int amount, boolean simulate)
	{
		if (stack.hasCapability(CapabilityEnergy.ENERGY, null))
		{
			return stack.getCapability(CapabilityEnergy.ENERGY, null).receiveEnergy(amount, simulate);
		}
		else
		{
			Item item = stack.getItem();
			if (item instanceof IFluxContainerItem)
			{
				return ((IFluxContainerItem) item).receiveEnergy(stack, amount, simulate);
			}
			else if (CompatProviderRF.INSTANCE.isPresent()) //has RF MCMSAPI
			{
				if (item instanceof IEnergyContainerItem)
				{
					return ((IEnergyContainerItem) item).receiveEnergy(stack, amount, simulate);
				}
				else return 0;
			}
			else return 0;
		}
	}

	public static boolean isFluidOrGasProvider(ItemStack stack, @Nullable FluidOrGasStack fluidType)
	{
		if (fluidType == null || fluidType.isFluid())
		{
			FluidStack fluid = FluidOrGasStack.getFluidStackStatic(fluidType);
			if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
			{
				IFluidTankProperties[] tanks = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).getTankProperties();
				for (IFluidTankProperties tank : tanks) if ((fluid == null ? tank.canDrain() : tank.canDrainFluidType(fluid)) && tank.getContents() != null && tank.getContents().amount > 0) return true;
			}
		}
		if (fluidType == null || fluidType.isGas())
		{
			if (stack.getItem() instanceof IGasItem)
			{
				IGasItem fluidHandler = (IGasItem) stack.getItem();
				if (fluidHandler.canProvideGas(stack, fluidType == null ? null : fluidType.getGasStack().getGas()) && fluidHandler.getGas(stack) != null && fluidHandler.getGas(stack).amount > 0) return true;
			}
		}
		return false;
	}

	public static boolean isFluidOrGasReceiver(ItemStack stack, @Nullable FluidOrGasStack fluidType)
	{
		if (fluidType == null || fluidType.isFluid())
		{
			FluidStack fluid = FluidOrGasStack.getFluidStackStatic(fluidType);
			if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
			{
				IFluidTankProperties[] tanks = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).getTankProperties();
				for (IFluidTankProperties tank : tanks) if ((fluid == null ? tank.canFill() : tank.canFillFluidType(fluid)) && (tank.getContents() == null || tank.getContents().amount < tank.getCapacity())) return true;
			}
		}
		if (fluidType == null || fluidType.isGas())
		{
			if (stack.getItem() instanceof IGasItem)
			{
				IGasItem fluidHandler = (IGasItem) stack.getItem();
				if (fluidHandler.canReceiveGas(stack, fluidType == null ? null : fluidType.getGasStack().getGas()) && (fluidHandler.getGas(stack) == null || fluidHandler.getGas(stack).amount < fluidHandler.getMaxGas(stack))) return true;
			}
		}
		return false;
	}

	public static boolean isTank(ItemStack stack)
	{
		if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null) || (stack.getItem() instanceof IGasItem)) return true;
		return false;
	}

	public static FluidOrGasStack getStoredFluid(ItemStack stack, @Nullable FluidOrGasStack fluidType)
	{
		if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
		{
			IFluidTankProperties[] tanks = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).getTankProperties();
			for (IFluidTankProperties tank : tanks) if (tank.getContents() != null && FluidOrGasStack.isFluidEqualStatic(fluidType, tank.getContents())) return FluidOrGasStack.forFluid(tank.getContents());
		}
		if (stack.getItem() instanceof IGasItem)
		{
			IGasItem fluidHandler = (IGasItem) stack.getItem();
			GasStack gas = fluidHandler.getGas(stack);
			if (gas != null && FluidOrGasStack.isGasEqualStatic(fluidType, gas)) return FluidOrGasStack.forGas(gas);
		}
		return null;
	}

	public static int getMaxStoredFluid(ItemStack stack, @Nullable FluidOrGasStack fluidType)
	{
		if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
		{
			IFluidTankProperties[] tanks = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).getTankProperties();
			for (IFluidTankProperties tank : tanks) if (tank.getCapacity() > 0 && (tank.getContents() == null || FluidOrGasStack.isFluidEqualStatic(fluidType, tank.getContents()))) return tank.getCapacity();
		}
		if (stack.getItem() instanceof IGasItem)
		{
			IGasItem fluidHandler = (IGasItem) stack.getItem();
			return fluidHandler.getMaxGas(stack);
		}
		return 0;
	}

	public static FluidOrGasStack tryDrain(ItemStack stack, int amount, boolean simulate, @Nullable FluidOrGasStack fluidType, Consumer<ItemStack> setStack)
	{
		if (fluidType == null || fluidType.isFluid())
		{
			FluidStack fluid = FluidOrGasStack.getFluidStackStatic(fluidType);
			if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
			{
				IFluidHandlerItem handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
				if (fluid != null)
				{
					fluid = fluid.copy();
					fluid.amount = amount;
				}
				FluidStack drained = fluid == null ? handler.drain(amount, !simulate) : handler.drain(fluid, !simulate);
				if (!simulate) setStack.accept(handler.getContainer());
				if (drained != null) return FluidOrGasStack.forFluid(drained);
			}
		}
		if (fluidType == null || fluidType.isGas())
		{
			if (stack.getItem() instanceof IGasItem)
			{
				IGasItem fluidHandler = (IGasItem) stack.getItem();
				if (fluidType == null || fluidType.isGasEqual(fluidHandler.getGas(stack)))
				{
					if (simulate) stack = stack.copy(); //make no changes to original stack
					return FluidOrGasStack.forGas(fluidHandler.removeGas(stack, amount));
				}
			}
		}
		return null;
	}

	public static int tryFill(ItemStack stack, FluidOrGasStack resource, boolean simulate, Consumer<ItemStack> setStack)
	{
		if (resource == null) return 0;
		if (resource.isFluid())
		{
			FluidStack fluid = FluidOrGasStack.getFluidStackStatic(resource).copy();
			if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
			{
				IFluidHandlerItem handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
				int filled = handler.fill(fluid, !simulate);
				if (!simulate) setStack.accept(handler.getContainer());
				return filled;
			}
		}
		if (resource.isGas())
		{
			GasStack gas = FluidOrGasStack.getGasStackStatic(resource).copy();
			if (stack.getItem() instanceof IGasItem)
			{
				IGasItem fluidHandler = (IGasItem) stack.getItem();
				if (resource == null || resource.isGasEqual(fluidHandler.getGas(stack)))
				{
					if (simulate) stack = stack.copy(); //make no changes to original stack
					return fluidHandler.addGas(stack, gas);
				}
			}
		}
		return 0;
	}

	public static <T extends Enum<?>> T getEnum(String name, T[] values, T _def)
	{
		for (T val : values) if (val.name().equalsIgnoreCase(name)) return val;
		try
		{
			int index = Integer.parseInt(name);
			if (index >= 0 && index < values.length) return values[index];
		}
		catch (NumberFormatException e) {}
		return _def;
	}

	public static double lerp(double v1, double v2, double amnt)
	{
		return (v1 + (v2 - v1) * amnt);
	}

	public static float lerp(float v1, float v2, float amnt)
	{
		return (v1 + (v2 - v1) * amnt);
	}

	public static IBlockState getBlockState(String full)
	{
		int index = full.indexOf('[');
		String blockName, stateString;
		if (index < 0 && index < full.length())
		{
			blockName = full;
			stateString = "";
		}
		else
		{
			blockName = full.substring(0, index);
			stateString = full.substring(index + 1, full.length() - 1);
		}
		ResourceLocation name = new ResourceLocation(blockName);
		if (Block.REGISTRY.containsKey(name))
		{
			Block block = Block.REGISTRY.getObject(name);
			if (block != Blocks.AIR && block != null)
			{
				IBlockState state = null;
				if (stateString.length() > 0) try
				{
					state = CommandBase.convertArgToBlockState(block, stateString);
				}
				catch (Exception e)
				{
					Main.LOGGER.warn("Invalid blockstate string, failed to parse: " + stateString);
				}
				if (state == null) state = block.getDefaultState();
				return state;
			}
		}
		else Main.LOGGER.warn("Invalid blockstate string, unknown block: " + blockName);
		return null;
	}

	public static BlockStateEntry getBlockStateEntry(String full)
	{
		int index = full.indexOf('[');
		String blockName, stateString;
		if (index < 0 && index < full.length())
		{
			blockName = full;
			stateString = "";
		}
		else
		{
			blockName = full.substring(0, index);
			stateString = full.substring(index + 1, full.length() - 1);
		}
		ResourceLocation name = new ResourceLocation(blockName);
		if (Block.REGISTRY.containsKey(name))
		{
			Block block = Block.REGISTRY.getObject(name);
			if (block != Blocks.AIR && block != null)
			{
				if (stateString.length() > 0) try
	            {
	                Map<IProperty<?>, Comparable<?>> map = CommandBase.getBlockStatePropertyValueMap(block, stateString);
	                IBlockState iblockstate = block.getDefaultState();
	                for (Entry < IProperty<?>, Comparable<? >> entry : map.entrySet())
	                {
	                    iblockstate = CommandBase.getBlockState(iblockstate, entry.getKey(), entry.getValue());
	                }
	                return new BlockStateEntry(iblockstate, map.keySet());
	            }
	            catch (RuntimeException | InvalidBlockStateException var6)
	            {
	            	Main.LOGGER.warn("Invalid blockstate string: " + stateString);
	            }
				else return new BlockStateEntry(block);
			}
		}
		else Main.LOGGER.warn("Invalid blockstate string, unknown block: " + blockName);
		return null;
	}

	public static int addToMax(int current, int toAdd)
	{
		return (Integer.MAX_VALUE - toAdd) >= current ? Integer.MAX_VALUE : current + toAdd;
	}

}