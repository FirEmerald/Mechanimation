package firemerald.api.data.attributes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import firemerald.api.data.BinaryFormat;
import firemerald.api.data.FileUtil;

public class AttributeDouble implements IAttribute
{
	public final double val;

	public AttributeDouble(double val)
	{
		this.val = val;
	}

	@Override
	public int getID()
	{
		return ID_DOUBLE;
	}

	@Override
	public String getString()
	{
		return Double.toString(val);
	}

	@Override
	public boolean getBoolean()
	{
		return val > 0;
	}

	@Override
	public byte getByte()
	{
		return (byte) val;
	}

	@Override
	public short getShort()
	{
		return (short) val;
	}

	@Override
	public int getInt()
	{
		return (int) val;
	}

	@Override
	public long getLong()
	{
		return (long) val;
	}

	@Override
	public float getFloat()
	{
		return (float) val;
	}

	@Override
	public double getDouble()
	{
		return val;
	}

	@Override
	public <T extends Enum<?>> T getEnum(T[] values) throws Exception
	{
		throw new Exception("Value is a double, not an enum");
	}

	@Override
	public void write(OutputStream out, BinaryFormat format) throws IOException
	{
		FileUtil.writeDouble(out, val);
	}

	public static AttributeDouble read(InputStream in) throws IOException
	{
		return new AttributeDouble(FileUtil.readDouble(in));
	}

	@Override
	public JsonElement makeElement()
	{
		return new JsonPrimitive(val);
	}
}