package firemerald.mechanimation.compat.forgemultipart;

import codechicken.multipart.TMultiPart;
import codechicken.multipart.api.IPartFactory;
import firemerald.mechanimation.compat.forgemultipart.pipe.PartEnergyPipe;
import firemerald.mechanimation.compat.forgemultipart.pipe.PartFluidPipe;
import firemerald.mechanimation.compat.forgemultipart.pipe.PartItemPipe;
import net.minecraft.util.ResourceLocation;

public class MechanimationPartFactory implements IPartFactory
{
	@Override
	public TMultiPart createPart(ResourceLocation type, boolean var2)
	{
		if (type.equals(PartItemPipe.TYPE)) return new PartItemPipe();
		else if (type.equals(PartEnergyPipe.TYPE)) return new PartEnergyPipe();
		else if (type.equals(PartFluidPipe.TYPE)) return new PartFluidPipe();
		else return null;
	}
}