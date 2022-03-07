package firemerald.mechanimation.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;

public class BlockStateEntry
{
	public Block block;
	public final Map<IProperty<?>, Comparable<?>> properties;
	public int hash;

	public BlockStateEntry()
	{
		properties = new LinkedHashMap<>();
	}

	public BlockStateEntry(Block block)
	{
		this.block = block;
		this.properties = new LinkedHashMap<>();
	}

	public BlockStateEntry(IBlockState blockState)
	{
		this.block = blockState.getBlock();
		this.properties = new LinkedHashMap<>(blockState.getProperties());
	}

	public BlockStateEntry(IBlockState blockState, IProperty<?>... properties)
	{
		this(blockState, Arrays.asList(properties));
	}

	public BlockStateEntry(IBlockState blockState, Collection<IProperty<?>> properties)
	{
		this.properties = new LinkedHashMap<>();
		set(blockState, properties);
	}

	public void set(IBlockState blockState)
	{
		this.block = blockState.getBlock();
		properties.clear();
		properties.putAll(blockState.getProperties());
	}

	public void set(IBlockState blockState, Collection<IProperty<?>> properties)
	{
		this.block = blockState.getBlock();
		this.properties.clear();
		for (IProperty<?> property : blockState.getPropertyKeys()) if (properties.contains(property)) this.properties.put(property, blockState.getValue(property));
	}

	public void setProperty(IProperty<?> property, Comparable<?> value)
	{
		properties.put(property, value);
	}

	@Override
	public int hashCode()
	{
		return block.getUnlocalizedName().hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this) return true;
		if ((o == null) || (o.getClass() != this.getClass())) return false;
		BlockStateEntry otherEntry = (BlockStateEntry) o;
		if (otherEntry.block != this.block) return false;
		Map<IProperty<?>, Comparable<?>> otherMap = ((BlockStateEntry) o).properties;
		if (otherMap == properties) return true;
		for (Map.Entry<IProperty<?>, Comparable<?>> entry : properties.entrySet())
		{
			if (!otherMap.containsKey(entry.getKey())) continue;
			else
			{
				Comparable<?> val = otherMap.get(entry.getKey());
				Comparable<?> val2 = entry.getValue();
				if (val == null ? val2 != null : (val2 == null || !val.equals(val2))) return false;
			}
		}
		return true;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(block.getRegistryName().toString());
		if (properties.size() > 0)
		{
			builder.append('[');
			boolean isFirst = true;
			for (Entry<IProperty<?>, Comparable<?>> entry : properties.entrySet())
			{
				if (isFirst) isFirst = false;
				else builder.append(',');
				parseToString(builder, entry.getKey(), entry.getValue());
			}
			builder.append(']');
		}
		return builder.toString();
	}

	@SuppressWarnings("unchecked")
	private <T extends Comparable<T>, P extends IProperty<T>> void parseToString(StringBuilder builder, IProperty<?> prop, Comparable<?> comp)
	{
		builder.append(prop.getName());
		builder.append('=');
		builder.append(((P) prop).getName((T) comp));
	}
}