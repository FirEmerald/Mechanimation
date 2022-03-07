package firemerald.mechanimation.multipart;

import codechicken.multipart.TMultiPart;
import codechicken.multipart.api.IPartFactory;
import firemerald.mechanimation.multipart.pipe.PartEnergyPipe;
import firemerald.mechanimation.multipart.pipe.PartFluidPipe;
import firemerald.mechanimation.multipart.pipe.PartItemPipe;
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