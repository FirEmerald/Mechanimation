package firemerald.mechanimation.plugin.transformers.common;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;

import firemerald.mechanimation.plugin.Plugin;
import firemerald.mechanimation.plugin.transformers.MechanimationTransformer;
import net.minecraftforge.fml.common.Loader;

public class SuperclassTransformer extends MechanimationTransformer
{
	public final String modId;
	public final String newSuperClass;

	public SuperclassTransformer(String modId, String newSuperClass)
	{
		super(false);
		this.modId = modId;
		this.newSuperClass = newSuperClass.replace('.', '/');
	}

	@Override
	public byte[] transform(byte[] bytes, String className)
	{
		if (!Loader.isModLoaded(modId)) return bytes;
		else return super.transform(bytes, className);
	}

	@Override
	public void transform(ClassNode classNode, String className)
	{
		Plugin.logger().debug("Changing superclass of " + className + " from " + classNode.superName + " to " + newSuperClass + " because mod " + modId + " was found.");
		String prevSuper = classNode.superName;
		classNode.superName = newSuperClass;
		classNode.methods.forEach(m -> {
			for (int i = 0; i < m.instructions.size(); i++)
			{
				AbstractInsnNode node = m.instructions.get(i);
				if (node instanceof MethodInsnNode)
				{
					MethodInsnNode mNode = (MethodInsnNode) node;
					if (mNode.getOpcode() == INVOKESPECIAL && mNode.owner.equals(prevSuper)) mNode.owner = newSuperClass; //replace super calls
				}
			}
		});
	}
}