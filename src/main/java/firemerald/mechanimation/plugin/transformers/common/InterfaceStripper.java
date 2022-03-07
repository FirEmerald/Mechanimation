package firemerald.mechanimation.plugin.transformers.common;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import firemerald.mechanimation.plugin.Plugin;
import firemerald.mechanimation.plugin.transformers.MechanimationTransformer;
import firemerald.mechanimation.plugin.transformers.MethodData;
import net.minecraftforge.fml.common.Loader;

public class InterfaceStripper extends MechanimationTransformer
{
	public final String modId;
	public final String interfaceToStrip;
	public final MethodData[] methodsToStrip;

	public InterfaceStripper(String modId, String interfaceToStrip, MethodData... methodsToStrip)
	{
		super(false);
		this.modId = modId;
		this.interfaceToStrip = interfaceToStrip.replace('.', '/');
		this.methodsToStrip = methodsToStrip;
	}

	@Override
	public byte[] transform(byte[] bytes, String className)
	{
		if (Loader.isModLoaded(modId)) return bytes;
		else return super.transform(bytes, className);
	}

	@Override
	public void transform(ClassNode classNode, String className)
	{
		Plugin.logger().debug("Stripping " + className + " of interface " + interfaceToStrip + " because required mod " + modId + " was not found.");
		if (classNode.interfaces.remove(interfaceToStrip))
		{
			if (methodsToStrip.length > 0)
			{
				int size = classNode.methods.size();
				for (int i = 0; i < size; i++)
				{
					MethodNode method = classNode.methods.get(i);
					for (MethodData m : methodsToStrip) if (method.name.equals(m.name) && method.desc.equals(m.desc))
					{
						classNode.methods.remove(i);
						size--;
						i--;
						break;
					}
				}
			}
		}
		else Plugin.logger().fatal("Could not strip interface " + interfaceToStrip + " from " + className);
	}
}