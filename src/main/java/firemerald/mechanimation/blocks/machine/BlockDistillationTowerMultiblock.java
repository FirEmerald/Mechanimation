package firemerald.mechanimation.blocks.machine;

import java.util.List;

import javax.annotation.Nullable;

import firemerald.mechanimation.tileentity.machine.distillation_tower.TileEntityDistillationTowerMiddle;
import firemerald.mechanimation.tileentity.machine.distillation_tower.TileEntityDistillationTowerTop;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockDistillationTowerMultiblock<T extends BlockDistillationTower> extends BlockContainer
{
    public static final PropertyBool IS_TOP = PropertyBool.create("is_top");
    public final T parent;

	public BlockDistillationTowerMultiblock(T parent)
	{
		super(parent.blockMaterial, MapColor.AIR);
		this.parent = parent;
	}

	@Override
    public IBlockState getStateFromMeta(int meta)
    {
		return this.getDefaultState().withProperty(IS_TOP, (meta & 1) != 0);
    }

	@Override
    public int getMetaFromState(IBlockState state)
    {
		return state.getValue(IS_TOP) ? 1 : 0;
    }

	@Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, IS_TOP);
    }

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return (meta & 1) == 0 ? new TileEntityDistillationTowerMiddle<>() : new TileEntityDistillationTowerTop<>();
	}

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
    	BlockPos parentPos = getParentPos(worldIn, pos, state);
    	return parentPos == null ? BlockFaceShape.UNDEFINED : parent.getBlockFaceShape(worldIn, worldIn.getBlockState(parentPos), parentPos, face, pos);
    }

    /**
     * Called after a player destroys this Block - the posiiton pos may no longer hold the state indicated.
     */
    @Override
	public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state)
    {
    	BlockPos parentPos = getParentPos(worldIn, pos, state);
    	if (parentPos != null) parent.onBlockDestroyedByPlayer(worldIn, parentPos, state);
    	else super.onBlockDestroyedByPlayer(worldIn, pos, state);
    }

    /**
     * Returns how much this block can resist explosions from the passed in entity.
     */
    @SuppressWarnings("deprecation")
	@Override
	public float getExplosionResistance(Entity exploder)
    {
        return this.parent.getExplosionResistance(exploder);
    }

    /**
     * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
     * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
     */
    @Override
	public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.INVISIBLE;
    }

    /**
     * Return an AABB (in world coords!) that should be highlighted when the player is targeting this Block
     */
    @SuppressWarnings("deprecation")
	@Override
	@SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos)
    {
    	BlockPos parentPos = getParentPos(worldIn, pos, state);
    	if (parentPos != null) return worldIn.getBlockState(parentPos).getSelectedBoundingBox(worldIn, parentPos);
    	else return super.getSelectedBoundingBox(state, worldIn, pos);
    }

    public BlockPos getParentPos(IBlockAccess world, BlockPos pos, IBlockState state)
    {
    	return pos.add(0, state.getValue(IS_TOP) ? -2 : -1, 0);
    }

    /**
     * Returns if this block is collidable. Only used by fire, although stairs return that of the block that the stair
     * is made of (though nobody's going to make fire stairs, right?)
     */
    @Override
	public boolean isCollidable()
    {
        return this.parent.isCollidable();
    }

    @Override
	public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid)
    {
        return this.parent.canCollideCheck(state, hitIfLiquid);
    }

    /**
     * Called serverside after this block is replaced with another in Chunk, but before the Tile Entity is updated
     */
    @Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
    	BlockPos parentPos = this.getParentPos(worldIn, pos, state);
    	if (parentPos != null) worldIn.setBlockToAir(parentPos);
    }

    /**
     * Called when this Block is to be destroyed by an Explosion
     */
    @Override
    public void onBlockExploded(World world, BlockPos pos, Explosion explosion)
    {
    	IBlockState state = world.getBlockState(pos);
    	BlockPos parentPos = getParentPos(world, pos, state);
    	if (parentPos != null) parent.onBlockDestroyedByExplosion(world, parentPos, explosion);
    	else super.onBlockExploded(world, pos, explosion);
    }

    @Override
	public boolean isOpaqueCube(IBlockState state)
    {
    	return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos)
    {
    	return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState)
    {
    	BlockPos parentPos = getParentPos(worldIn, pos, state);
    	if (parentPos != null) worldIn.getBlockState(parentPos).addCollisionBoxToList(worldIn, parentPos, entityBox, collidingBoxes, entityIn, isActualState);
    	else super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);
    }

    @SuppressWarnings("deprecation")
	@Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
    	BlockPos parentPos = getParentPos(worldIn, pos, state);
    	if (parentPos != null)
    	{
    		IBlockState parentState = worldIn.getBlockState(parentPos);
    		return parentState.getBlock().getItem(worldIn, parentPos, parentState);
    	}
    	else return super.getItem(worldIn, pos, state);
    }

    @SuppressWarnings("deprecation")
	@Override
	@Nullable
    public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end)
    {
    	BlockPos parentPos = getParentPos(worldIn, pos, blockState);
    	if (parentPos != null)
    	{
    		RayTraceResult result = worldIn.getBlockState(parentPos).collisionRayTrace(worldIn, parentPos, start, end);
    		if (result == null || result.typeOfHit != Type.BLOCK) return result;
    		else
    		{
    			RayTraceResult res = new RayTraceResult(result.hitVec, result.sideHit, pos);
    			res.entityHit = result.entityHit;
    			res.hitInfo = result.hitInfo;
    			res.subHit = result.subHit;
    			return res;
    		}
    	}
    	else return super.collisionRayTrace(blockState, worldIn, pos, start, end);
    }

    public boolean isPowered(World world, BlockPos pos, IBlockState blockState)
    {
        if (world.isBlockPowered(pos)) return true;
    	{
    		BlockPos newPos = pos.offset(EnumFacing.UP);
    		IBlockState newState = world.getBlockState(newPos);
    		if (newState.getBlock() instanceof BlockDistillationTowerMultiblock) //facing this
    		{
    			if (((BlockDistillationTowerMultiblock<?>) newState.getBlock()).isPowered(world, newPos, newState)) return true;
    		}
    	}
    	return false;
    }

    @Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
    	BlockPos parent = this.getParentPos(worldIn, pos, state);
    	if (parent != null) worldIn.getBlockState(parent).neighborChanged(worldIn, parent, blockIn, fromPos);
    }

    @Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
    	BlockPos parent = this.getParentPos(worldIn, pos, state);
    	if (parent != null) return worldIn.getBlockState(parent).getBlock().onBlockActivated(worldIn, parent, worldIn.getBlockState(parent), playerIn, hand, facing, hitX, hitY, hitZ);
    	else return false;
	}
}