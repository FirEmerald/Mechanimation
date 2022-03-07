package firemerald.mechanimation.items;

import firemerald.mechanimation.blocks.BlockMechanimationSlab;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMechanimationSlab extends ItemBlock
{
	public final BlockMechanimationSlab slab;

	public ItemMechanimationSlab(BlockMechanimationSlab block)
	{
		super(block);
		this.slab = block;
	}

    /**
     * Called when a Block is right-clicked with this Item
     */
    @Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        ItemStack itemstack = player.getHeldItem(hand);
        if (!itemstack.isEmpty() && player.canPlayerEdit(pos.offset(facing), facing, itemstack))
        {
        	if (willStack(worldIn, pos, facing, player, itemstack, hitX, hitY, hitZ))
        	{
                AxisAlignedBB axisalignedbb = slab.getBlock().getCollisionBoundingBox(worldIn, pos);
                if (axisalignedbb != Block.NULL_AABB && worldIn.checkNoEntityCollision(axisalignedbb.offset(pos)) && worldIn.setBlockState(pos, slab.getBlock(), 11))
                {
                    SoundType soundtype = this.slab.getSoundType(slab.getBlock(), worldIn, pos, player);
                    worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                    itemstack.shrink(1);
                    if (player instanceof EntityPlayerMP)
                    {
                        CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos, itemstack);
                    }
                    return EnumActionResult.SUCCESS;
                }
        	}
        }
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
	@SideOnly(Side.CLIENT)
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack)
    {
        return willStack(worldIn, pos, side, player, stack) || super.canPlaceBlockOnSide(worldIn, pos, side, player, stack);
    }

    public boolean willStack(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack)
    {
    	if (player.isSneaking()) return false;
        IBlockState iblockstate = worldIn.getBlockState(pos);
    	return iblockstate.getBlock() == this.slab && side == iblockstate.getValue(BlockMechanimationSlab.PLACING).getOpposite();
    }

    public boolean willStack(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack, float hitX, float hitY, float hitZ)
    {
    	if (willStack(worldIn, pos, side, player, stack))
    	{
    		switch (side.getAxis())
    		{
    		case X:
    			return hitY >= .25f && hitZ >= .25f && hitY <= .75f && hitZ <= .75;
    		case Y:
    			return hitX >= .25f && hitZ >= .25f && hitX <= .75f && hitZ <= .75;
    		case Z:
    			return hitX >= .25f && hitY >= .25f && hitX <= .75f && hitY <= .75;
    		default:
    			return false;
    		}
    	}
    	else return false;
    }
}