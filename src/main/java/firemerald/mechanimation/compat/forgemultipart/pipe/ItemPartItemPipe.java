package firemerald.mechanimation.compat.forgemultipart.pipe;

import java.util.List;

import codechicken.lib.vec.Vector3;
import firemerald.api.core.client.Translator;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPartItemPipe extends ItemPartPipe
{
	@Override
	public PartPipe newPipe(ItemStack stack, EntityPlayer player, World world, BlockPos pos, int arg4, Vector3 arg5)
	{
		return new PartItemPipe();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag)
	{
		super.addInformation(stack, world, list, flag);
		EnumPipeTier tier = getTier(stack);
		list.add(Translator.translate("lore.mechanimation.item_pipe"));
		list.add(Translator.format("lore.mechanimation.items.items_per_second", tier.maxItemExtract * 20, .05f / tier.maxItemExtract));
	}
}