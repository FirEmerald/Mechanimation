package firemerald.api.mcms.util;

import java.util.Collection;

import firemerald.api.mcms.model.IRigged;

public class MiscUtil
{
	public static String getNewBoneName(String name, IRigged<?, ?> model)
	{
		if (!model.isNameUsed(name)) return name;
		int i = 2;
		if (!name.endsWith("(copy)"))
		{
			boolean flag = true;
			int pos = name.lastIndexOf(" (");
			if (pos > 0)
			{
				int pos2 = name.lastIndexOf(')');
				if (pos2 > pos)
				{
					String val = name.substring(pos + 2, pos2);
					try
					{
						i = Integer.parseInt(val) + 1;
						name = name.substring(0, pos);
						flag = false;
					}
					catch (NumberFormatException e) {}
				}
			}
			if (flag)
			{
				name = name + " (copy)";
				if (!model.isNameUsed(name)) return name;
			}
		}
		String orig = name;
		while (model.isNameUsed(name = (orig + " (" + Integer.toString(i) + ")"))) i++;
		return name;
	}

	public static String ensureUnique(String name, Collection<String> collection)
	{
		if (collection.contains(name))
		{
			String orig = name;
			int i = 2;
			while (collection.contains(name = orig + " (" + i++ + ")")) {}
		}
		return name;
	}
}