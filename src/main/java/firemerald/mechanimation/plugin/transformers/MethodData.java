package firemerald.mechanimation.plugin.transformers;

public class MethodData
{
	public final String name;
	public final String desc;

	public MethodData(String name, String desc)
	{
		this.name = name;
		this.desc = desc.replace('.', '/');
	}
}