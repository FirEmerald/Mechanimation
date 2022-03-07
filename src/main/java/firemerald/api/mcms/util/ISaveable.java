package firemerald.api.mcms.util;

import firemerald.api.mcms.data.AbstractElement;

public interface ISaveable
{
	public String getElementName();

	public void save(AbstractElement el);

	public void load(AbstractElement el);
}