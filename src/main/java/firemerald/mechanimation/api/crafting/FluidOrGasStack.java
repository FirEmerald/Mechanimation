package firemerald.mechanimation.api.crafting;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.IGasItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public abstract class FluidOrGasStack
{
	public abstract boolean isFluid();

	public abstract boolean isGas();

	public abstract FluidStack getFluidStack();

	public abstract GasStack getGasStack();

	public abstract String getModId();

	public abstract String getName();

	public abstract String getContentsName();

	public abstract String getUnlocalizedName();

	public abstract String getLocalizedName();

	public abstract int getAmount();

	public abstract void setAmount(int amount);

	public abstract void changeAmount(int amount);

	public boolean isFluidEqual(FluidStack other)
	{
		return this.isFluid() && this.getFluidStack().isFluidEqual(other);
	}

	public boolean isGasEqual(GasStack other)
	{
		return this.isGas() && this.getGasStack().isGasEqual(other);
	}

	public boolean isFluidEqual(FluidOrGasStack other)
	{
		return other == null ? false : other.isFluid() ? isFluidEqual(other.getFluidStack()) : isGasEqual(other.getGasStack());
	}

	public abstract FluidOrGasStack copy();

	public abstract FluidOrGasStack copy(int newAmount);

	public abstract ItemStack drainFrom(ItemStack stack, int maxAmount);

	public abstract ItemStack fillTo(ItemStack stack);

	public abstract NBTTagCompound writeToNBT(NBTTagCompound tag);

	public abstract FluidOrGasStack splitStack(int toSplit);

	@Override
	public boolean equals(Object o)
	{
		if (o == this) return true;
		if (o == null || o.getClass() != this.getClass()) return false;
		FluidOrGasStack other = (FluidOrGasStack) o;
		return other.isFluidEqual(this) && other.getAmount() == this.getAmount();
	}

	public static FluidOrGasStack readFromNBT(NBTTagCompound tag)
	{
		if (tag.hasNoTags()) return null;
		else if (tag.getBoolean("isGas"))
		{
			GasStack gas = GasStack.readFromNBT(tag);
			return gas == null ? null : new GasStackWrapper(gas);
		}
		else
		{
			FluidStack fluid = FluidStack.loadFluidStackFromNBT(tag);
			return fluid == null ? null : new FluidStackWrapper(fluid);
		}
	}

	public static NBTTagCompound writeToNBT(@Nullable FluidOrGasStack stack, NBTTagCompound tag)
	{
		if (stack != null) tag = stack.writeToNBT(tag);
		return tag;
	}

	public static FluidOrGasStack getFrom(ItemStack stack, int maxAmount, Consumer<ItemStack> setStack)
	{
		if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
		{
			IFluidHandlerItem fluidHandler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
			if (fluidHandler != null)
			{
				FluidStack drained = fluidHandler.drain(maxAmount, true);
				if (drained != null)
				{
					setStack.accept(fluidHandler.getContainer());
					return new FluidStackWrapper(drained);
				}
			}
		}
		if (stack.getItem() instanceof IGasItem)
		{
			IGasItem fluidHandler = (IGasItem) stack.getItem();
			GasStack drained = fluidHandler.removeGas(stack, maxAmount);
			if (drained != null)
			{
				setStack.accept(stack);
				return new GasStackWrapper(drained);
			}
		}
		return null;
	}

	public static FluidOrGasStack drainFrom(ItemStack stack, int maxAmount, Consumer<ItemStack> setStack, FluidOrGasStack current)
	{
		if (stack == null || stack.isEmpty()) return current;
		else if (current != null)
		{
			ItemStack res = current.drainFrom(stack, maxAmount);
			if (res != null) setStack.accept(res);
			return current.getAmount() > 0 ? current : null;
		}
		else return getFrom(stack, maxAmount, setStack);
	}

	public static FluidOrGasStack fillTo(ItemStack stack, Consumer<ItemStack> setStack, FluidOrGasStack current)
	{
		if (stack == null || stack.isEmpty()) return current;
		else if (current != null)
		{
			ItemStack res = current.fillTo(stack);
			if (res != null) setStack.accept(res);
			return current.getAmount() > 0 ? current : null;
		}
		else return null;
	}

	public static FluidOrGasStack forFluid(FluidStack stack)
	{
		return stack == null ? null : new FluidStackWrapper(stack);
	}

	public static FluidOrGasStack forFluid(Fluid fluid)
	{
		return fluid == null ? null : new FluidStackWrapper(new FluidStack(fluid, Fluid.BUCKET_VOLUME));
	}

	public static FluidOrGasStack forGas(GasStack stack)
	{
		return stack == null ? null : new GasStackWrapper(stack);
	}

	public static FluidOrGasStack forGas(Gas gas)
	{
		return gas == null ? null : new GasStackWrapper(new GasStack(gas, Fluid.BUCKET_VOLUME));
	}

	public static int getAmountStatic(@Nullable FluidOrGasStack stack)
	{
		return stack == null ? 0 : stack.getAmount();
	}

	public static FluidOrGasStack setAmountStatic(FluidOrGasStack stack, int amount)
	{
		if (amount >= 0) return null;
		else
		{
			stack.setAmount(amount);
			return stack;
		}
	}

	public static FluidOrGasStack changeAmountStatic(FluidOrGasStack stack, int amount)
	{
		if (-amount >= stack.getAmount()) return null;
		else
		{
			stack.changeAmount(amount);
			return stack;
		}
	}

	public static boolean isFluidEqualStatic(FluidOrGasStack a, FluidOrGasStack b)
	{
		return a == null || a.isFluidEqual(b);
	}

	public static boolean isFluidEqualStatic(FluidOrGasStack a, FluidStack b)
	{
		return a == null || a.isFluidEqual(b);
	}

	public static boolean isGasEqualStatic(FluidOrGasStack a, GasStack b)
	{
		return a == null || a.isGasEqual(b);
	}

	public static FluidStack getFluidStackStatic(FluidOrGasStack stack)
	{
		return stack == null ? null : stack.getFluidStack();
	}

	public static GasStack getGasStackStatic(FluidOrGasStack stack)
	{
		return stack == null ? null : stack.getGasStack();
	}

	private static class FluidStackWrapper extends FluidOrGasStack
	{
		public final FluidStack stack;

		public FluidStackWrapper(FluidStack stack)
		{
			if (stack == null) throw new IllegalStateException();
			this.stack = stack;
		}

		@Override
		public boolean isFluid()
		{
			return true;
		}

		@Override
		public boolean isGas()
		{
			return false;
		}

		@Override
		public FluidStack getFluidStack()
		{
			return stack;
		}

		@Override
		public GasStack getGasStack()
		{
			return null;
		}

		@Override
		public String getModId()
		{
			String defaultFluidName = FluidRegistry.getDefaultFluidName(stack.getFluid());
			if (defaultFluidName == null) return "";
			ResourceLocation fluidResourceName = new ResourceLocation(defaultFluidName);
			return fluidResourceName.getResourceDomain();
		}

		@Override
		public String getName()
		{
			if (stack.tag != null) return "fluid:" + stack.getFluid().getName() + ":" + stack.tag;
			return "fluid:" + stack.getFluid().getName();
		}

		@Override
		public String getUnlocalizedName()
		{
			return stack.getUnlocalizedName();
		}

		@Override
		public String getLocalizedName()
		{
			return stack.getLocalizedName();
		}

		@Override
		public int getAmount()
		{
			return stack.amount;
		}

		@Override
		public void setAmount(int amount)
		{
			stack.amount = amount;
		}

		@Override
		public void changeAmount(int amount)
		{
			stack.amount += amount;
		}

		@Override
		public FluidStackWrapper copy()
		{
			return new FluidStackWrapper(stack.copy());
		}

		@Override
		public FluidStackWrapper copy(int newAmount)
		{
			FluidStack newStack = stack.copy();
			newStack.amount = newAmount;
			return new FluidStackWrapper(newStack);
		}

		@Override
		public ItemStack drainFrom(ItemStack stack, int maxAmount)
		{
			if (this.stack.amount < maxAmount && stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
			{
				IFluidHandlerItem fluidHandler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
				if (fluidHandler != null)
				{
					FluidStack toDrain = this.stack.copy();
					toDrain.amount = maxAmount - this.stack.amount;
					toDrain = fluidHandler.drain(toDrain, true);
					if (toDrain != null)
					{
						this.stack.amount += toDrain.amount;
						return fluidHandler.getContainer();
					}
				}
			}
			return null;
		}

		@Override
		public ItemStack fillTo(ItemStack stack)
		{
			if (this.stack.amount > 0 && stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
			{
				IFluidHandlerItem fluidHandler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
				if (fluidHandler != null)
				{
					int toDrain = fluidHandler.fill(this.stack, true);
					if (toDrain > 0)
					{
						this.stack.amount -= toDrain;
						return fluidHandler.getContainer();
					}
				}
			}
			return null;
		}

		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound tag)
		{
			tag.setBoolean("isGas", false);
			return stack.writeToNBT(tag);
		}

		@Override
		public FluidStackWrapper splitStack(int toSplit)
		{
	        int i = Math.min(toSplit, stack.amount);
			FluidStack newStack = stack.copy();
			newStack.amount = i;
			stack.amount -= i;
			return new FluidStackWrapper(newStack);
		}


		@Override
		public String getContentsName()
		{
			return stack.getFluid().getName();
		}
	}

	private static class GasStackWrapper extends FluidOrGasStack
	{
		public final GasStack stack;

		public GasStackWrapper(GasStack stack)
		{
			if (stack == null || stack.getGas() == null) throw new IllegalStateException();
			this.stack = stack;
		}

		@Override
		public boolean isFluid()
		{
			return false;
		}

		@Override
		public boolean isGas()
		{
			return true;
		}

		@Override
		public FluidStack getFluidStack()
		{
			return null;
		}

		@Override
		public GasStack getGasStack()
		{
			return stack;
		}

		@Override
		public String getModId()
		{
	        return stack.getGas().getIcon().getResourceDomain();
		}

		@Override
		public String getName()
		{
			return "gas:" + stack.getGas().getName();
		}

		@Override
		public String getUnlocalizedName()
		{
			return stack.getGas().getTranslationKey();
		}

		@Override
		public String getLocalizedName()
		{
			return stack.getGas().getLocalizedName();
		}

		@Override
		public int getAmount()
		{
			return stack.amount;
		}

		@Override
		public void setAmount(int amount)
		{
			stack.amount = amount;
		}

		@Override
		public void changeAmount(int amount)
		{
			stack.amount += amount;
		}

		@Override
		public GasStackWrapper copy()
		{
			return new GasStackWrapper(stack.copy());
		}

		@Override
		public GasStackWrapper copy(int newAmount)
		{
			GasStack newStack = stack.copy();
			newStack.amount = newAmount;
			return new GasStackWrapper(newStack);
		}

		@Override
		public ItemStack drainFrom(ItemStack stack, int maxAmount)
		{
			if (this.stack.amount < maxAmount && stack.getItem() instanceof IGasItem)
			{
				IGasItem fluidHandler = (IGasItem) stack.getItem();
				if (fluidHandler.canProvideGas(stack, this.stack.getGas()))
				{
					GasStack toDrain = fluidHandler.removeGas(stack, maxAmount - this.stack.amount);
					if (toDrain != null)
					{
						this.stack.amount += toDrain.amount;
						return stack;
					}
				}
			}
			return null;
		}

		@Override
		public ItemStack fillTo(ItemStack stack)
		{
			if (this.stack.amount > 0 && stack.getItem() instanceof IGasItem)
			{
				IGasItem fluidHandler = (IGasItem) stack.getItem();
				if (fluidHandler.canReceiveGas(stack, this.stack.getGas()))
				{
					int toDrain = fluidHandler.addGas(stack, this.stack.copy());
					if (toDrain > 0)
					{
						this.stack.amount -= toDrain;
						return stack;
					}
				}
			}
			return null;
		}

		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound tag)
		{
			tag.setBoolean("isGas", true);
			return stack.write(tag);
		}

		@Override
		public GasStackWrapper splitStack(int toSplit)
		{
	        int i = Math.min(toSplit, stack.amount);
			GasStack newStack = stack.copy();
			newStack.amount = i;
			stack.amount -= i;
			return new GasStackWrapper(newStack);
		}


		@Override
		public String getContentsName()
		{
			return stack.getGas().getName();
		}
	}
}