package firemerald.mechanimation.tileentity.machine.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;

import firemerald.mechanimation.util.EnumFace;

public class SidedSlots<T extends ISlot>
{
	private final Map<EnumFace, List<T>> sidedSlots = new HashMap<>();

	public SidedSlots()
	{
		for (EnumFace face : EnumFace.values()) sidedSlots.put(face, new ArrayList<>());
	}

	public void addSlot(T slot, EnumFace... faces)
	{
		for (EnumFace face : faces) sidedSlots.get(face).add(slot);
	}

	public T[][] getSlotArray(IntFunction<T[][]> makeBigArray, IntFunction<T[]> makeSmallArray)
	{
		T[][] array = makeBigArray.apply(EnumFace.values().length);
		for (EnumFace face : EnumFace.values())
		{
			List<T> list = sidedSlots.get(face);
			array[face.ordinal()] = list.toArray(makeSmallArray.apply(list.size()));
		}
		return array;
	}

	public int[][] getIndexArray()
	{
		int[][] array = new int[EnumFace.values().length][];
		for (EnumFace face : EnumFace.values())
		{
			List<T> list = sidedSlots.get(face);
			int[] array2  = array[face.ordinal()] = new int[list.size()];
			for (int i = 0; i < array2.length; ++i) array2[i] = list.get(i).getSlot();
		}
		return array;
	}
}