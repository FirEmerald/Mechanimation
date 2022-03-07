package firemerald.mechanimation.multipart;

import java.util.List;

import codechicken.multipart.JItemMultiPart;
import firemerald.api.core.client.Translator;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ItemPart extends JItemMultiPart
{
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag)
	{
		list.add(Translator.translate("lore.mechanimation.fmp"));
	}
}
