package firemerald.api.mcms.util;

public interface ISelfTyped<T extends ISelfTyped<T>>
{
	@SuppressWarnings("unchecked")
	public default T self()
	{
		return (T) this;
	}
}