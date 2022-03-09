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

public class ItemPartFluidPipe extends ItemPartPipe
{
	@Override
	public PartPipe newPipe(ItemStack stack, EntityPlayer player, World world, BlockPos pos, int arg4, Vector3 arg5)
	{
		return new PartFluidPipe();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag)
	{
		super.addInformation(stack, world, list, flag);
		EnumPipeTier tier = getTier(stack);
		list.add(Translator.format("lore.mechanimation.fluid_pipe"));
		list.add(Translator.format("lore.mechanimation.fluid.capacity", tier.maxFluid));
		list.add(Translator.format("lore.mechanimation.fluid.transfer", 20 * tier.maxFluidTransfer));
	}
}