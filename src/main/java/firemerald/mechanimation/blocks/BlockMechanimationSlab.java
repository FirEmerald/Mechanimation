package firemerald.mechanimation.blocks;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import firemerald.mechanimation.client.renderer.IBlockHighlight;
import firemerald.mechanimation.init.MechanimationTabs;
import firemerald.mechanimation.util.Constants;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMechanimationSlab extends Block implements ICustomBlockHighlight
{
    public static final PropertyDirection PLACING = PropertyDirection.create("direction");

    private final Block modelBlock;
    private final IBlockState modelState;

	public BlockMechanimationSlab(IBlockState modelState)
	{
        super(modelState.getBlock().blockMaterial);
        this.setDefaultState(this.blockState.getBaseState().withProperty(PLACING, EnumFacing.DOWN));
        this.modelBlock = modelState.getBlock();
        this.modelState = modelState;
        this.setHardness(this.modelBlock.blockHardness);
        this.setResistance(this.modelBlock.blockResistance / 3.0F);
        this.setSoundType(this.modelBlock.blockSoundType);
        this.setHarvestLevel(modelBlock.getHarvestTool(modelState), modelBlock.getHarvestLevel(modelState));
        this.setLightOpacity(255);
        this.setCreativeTab(MechanimationTabs.DECORATIONS);
	}

	public IBlockState getBlock()
	{
		return modelState;
	}

    @Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        @SuppressWarnings("deprecation")
		IBlockState iblockstate = super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
        hitX -= 0.5f;
        hitY -= 0.5f;
        hitZ -= 0.5f;
        switch (facing)
        {
		case DOWN:
			if (Math.abs(hitX) <= .25f && Math.abs(hitZ) <= .25f)
			{
				iblockstate = iblockstate.withProperty(PLACING, EnumFacing.UP);
			}
			else if (hitX > hitZ)
			{
				if (hitX > -hitZ)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumFacing.EAST);
				}
				else
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumFacing.NORTH);
				}
			}
			else
			{
				if (hitZ > -hitX)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumFacing.SOUTH);
				}
				else
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumFacing.WEST);
				}
			}
			break;
		case UP:
			if (Math.abs(hitX) <= .25f && Math.abs(hitZ) <= .25f)
			{
				iblockstate = iblockstate.withProperty(PLACING, EnumFacing.DOWN);
			}
			else if (hitX > hitZ)
			{
				if (hitX > -hitZ)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumFacing.EAST);
				}
				else
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumFacing.NORTH);
				}
			}
			else
			{
				if (hitZ > -hitX)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumFacing.SOUTH);
				}
				else
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumFacing.WEST);
				}
			}
			break;
		case NORTH:
			if (Math.abs(hitX) <= .25f && Math.abs(hitY) <= .25f)
			{
				iblockstate = iblockstate.withProperty(PLACING, EnumFacing.SOUTH);
			}
			else if (hitX > hitY)
			{
				if (hitX > -hitY)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumFacing.EAST);
				}
				else
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumFacing.DOWN);
				}
			}
			else
			{
				if (hitY > -hitX)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumFacing.UP);
				}
				else
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumFacing.WEST);
				}
			}
			break;
		case SOUTH:
			if (Math.abs(hitX) <= .25f && Math.abs(hitY) <= .25f)
			{
				iblockstate = iblockstate.withProperty(PLACING, EnumFacing.NORTH);
			}
			else if (hitX > hitY)
			{
				if (hitX > -hitY)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumFacing.EAST);
				}
				else
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumFacing.DOWN);
				}
			}
			else
			{
				if (hitY > -hitX)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumFacing.UP);
				}
				else
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumFacing.WEST);
				}
			}
			break;
		case WEST:
			if (Math.abs(hitY) <= .25f && Math.abs(hitZ) <= .25f)
			{
				iblockstate = iblockstate.withProperty(PLACING, EnumFacing.EAST);
			}
			else if (hitZ > hitY)
			{
				if (hitZ > -hitY)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumFacing.SOUTH);
				}
				else
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumFacing.DOWN);
				}
			}
			else
			{
				if (hitY > -hitZ)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumFacing.UP);
				}
				else
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumFacing.NORTH);
				}
			}
			break;
		case EAST:
			if (Math.abs(hitY) <= .25f && Math.abs(hitZ) <= .25f)
			{
				iblockstate = iblockstate.withProperty(PLACING, EnumFacing.WEST);
			}
			else if (hitZ > hitY)
			{
				if (hitZ > -hitY)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumFacing.SOUTH);
				}
				else
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumFacing.DOWN);
				}
			}
			else
			{
				if (hitY > -hitZ)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumFacing.UP);
				}
				else
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumFacing.NORTH);
				}
			}
			break;
        }
        return iblockstate;
    }

	@Override
    public IBlockState getStateFromMeta(int meta)
    {
		if (meta < 0 || meta >= EnumFacing.values().length) meta = 0;
		return this.getDefaultState().withProperty(PLACING, EnumFacing.getFront(meta));
    }

	@Override
    public int getMetaFromState(IBlockState state)
    {
		return state.getValue(PLACING).getIndex();
    }

    @Override
	@SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        this.modelBlock.randomDisplayTick(stateIn, worldIn, pos, rand);
    }

    @Override
	public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn)
    {
        this.modelBlock.onBlockClicked(worldIn, pos, playerIn);
    }

    /**
     * Called after a player destroys this Block - the posiiton pos may no longer hold the state indicated.
     */
    @Override
	public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state)
    {
        this.modelBlock.onBlockDestroyedByPlayer(worldIn, pos, state);
    }

    @Override
	@SideOnly(Side.CLIENT)
    public int getPackedLightmapCoords(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return this.modelState.getPackedLightmapCoords(source, pos);
    }

    /**
     * Returns how much this block can resist explosions from the passed in entity.
     */
    @SuppressWarnings("deprecation")
	@Override
	public float getExplosionResistance(Entity exploder)
    {
        return this.modelBlock.getExplosionResistance(exploder);
    }

    /**
     * How many world ticks before ticking
     */
    @Override
	public int tickRate(World worldIn)
    {
        return this.modelBlock.tickRate(worldIn);
    }

    @Override
	public Vec3d modifyAcceleration(World worldIn, BlockPos pos, Entity entityIn, Vec3d motion)
    {
        return this.modelBlock.modifyAcceleration(worldIn, pos, entityIn, motion);
    }

    @Override
	@SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return this.modelBlock.getBlockLayer();
    }

    /**
     * Return an AABB (in world coords!) that should be highlighted when the player is targeting this Block
     */
    @Override
	@SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos)
    {
        return getBoundingBox(state).offset(pos);
    }

    public static AxisAlignedBB getBoundingBox(IBlockState state)
    {
        switch (state.getValue(PLACING))
        {
        case UP:
        	return Constants.SLAB_UP;
        case DOWN:
        	return Constants.SLAB_DOWN;
        case NORTH:
        	return Constants.SLAB_NORTH;
        case SOUTH:
        	return Constants.SLAB_SOUTH;
        case EAST:
        	return Constants.SLAB_EAST;
        case WEST:
        	return Constants.SLAB_WEST;
		default:
        	return Constants.FULL_BLOCK;
        }
    }

    /**
     * Returns if this block is collidable. Only used by fire, although stairs return that of the block that the stair
     * is made of (though nobody's going to make fire stairs, right?)
     */
    @Override
	public boolean isCollidable()
    {
        return this.modelBlock.isCollidable();
    }

    @Override
	public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid)
    {
        return this.modelBlock.canCollideCheck(state, hitIfLiquid);
    }

    /**
     * Checks if this block can be placed exactly at the given position.
     */
    @Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return this.modelBlock.canPlaceBlockAt(worldIn, pos);
    }

    /**
     * Called after the block is set in the Chunk data, but before the Tile Entity is set
     */
    @Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        this.modelState.neighborChanged(worldIn, pos, Blocks.AIR, pos);
        this.modelBlock.onBlockAdded(worldIn, pos, this.modelState);
    }

    /**
     * Called serverside after this block is replaced with another in Chunk, but before the Tile Entity is updated
     */
    @Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        this.modelBlock.breakBlock(worldIn, pos, this.modelState);
    }

    /**
     * Called when the given entity walks on this Block
     */
    @Override
	public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn)
    {
        this.modelBlock.onEntityWalk(worldIn, pos, entityIn);
    }

    @Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        this.modelBlock.updateTick(worldIn, pos, state, rand);
    }

    /**
     * Called when the block is right clicked by a player.
     */
    @Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        return this.modelBlock.onBlockActivated(worldIn, pos, this.modelState, playerIn, hand, EnumFacing.DOWN, 0.0F, 0.0F, 0.0F);
    }

    /**
     * Called when this Block is destroyed by an Explosion
     */
    @Override
	public void onBlockDestroyedByExplosion(World worldIn, BlockPos pos, Explosion explosionIn)
    {
        this.modelBlock.onBlockDestroyedByExplosion(worldIn, pos, explosionIn);
    }

    /**
     * Get the MapColor for this Block and the given BlockState
     */
    @SuppressWarnings("deprecation")
	@Override
	public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return this.modelBlock.getMapColor(this.modelState, worldIn, pos);
    }

	@Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PLACING);
    }

	@Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

	@Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState)
    {
        if (!isActualState && (worldIn.getWorldType() != WorldType.DEBUG_ALL_BLOCK_STATES))
        {
            state = this.getActualState(state, worldIn, pos);
        }

        addCollisionBoxToList(pos, entityBox, collidingBoxes, getBoundingBox(state));
    }

    @Override
	@Nullable
    public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end)
    {
        List<RayTraceResult> list = Lists.<RayTraceResult>newArrayList();

        list.add(this.rayTrace(pos, start, end, getBoundingBox(blockState)));

        RayTraceResult raytraceresult1 = null;
        double d1 = 0.0D;

        for (RayTraceResult raytraceresult : list)
        {
            if (raytraceresult != null)
            {
                double d0 = raytraceresult.hitVec.squareDistanceTo(end);

                if (d0 > d1)
                {
                    raytraceresult1 = raytraceresult;
                    d1 = d0;
                }
            }
        }

        return raytraceresult1;
    }

    /**
     * Get the geometry of the queried face at the given position and state. This is used to decide whether things like
     * buttons are allowed to be placed on the face, or how glass panes connect to the face, among other things.
     * <p>
     * Common values are {@code SOLID}, which is the default, and {@code UNDEFINED}, which represents something that
     * does not fit the other descriptions and will generally cause other things not to connect to the face.
     *
     * @return an approximation of the form of the given face
     */
    @SuppressWarnings("deprecation")
	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        state = this.getActualState(state, worldIn, pos);
        if (state.getValue(PLACING) == face) return modelState.getBlockFaceShape(worldIn, pos, face);
        else return BlockFaceShape.UNDEFINED;
    }

    /**
     * Determines if the block is solid enough on the top side to support other blocks, like redstone components.
     */
    @SuppressWarnings("deprecation")
	@Override
	public boolean isTopSolid(IBlockState state)
    {
    	return state.getValue(PLACING) == EnumFacing.UP && modelState.isTopSolid();
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    @Override
	public IBlockState withRotation(IBlockState state, Rotation rot)
    {
    	if (rot == Rotation.NONE) return state;
    	EnumFacing place = state.getValue(PLACING);
    	if (place.getAxis() == Axis.Y) return state;
    	switch (place)
    	{
		case WEST:
			switch (rot)
			{
			case CLOCKWISE_180:
				return state.withProperty(PLACING, EnumFacing.EAST);
			case CLOCKWISE_90:
				return state.withProperty(PLACING, EnumFacing.NORTH);
			case COUNTERCLOCKWISE_90:
				return state.withProperty(PLACING, EnumFacing.SOUTH);
			default:
		    	return state;
			}
		case EAST:
			switch (rot)
			{
			case CLOCKWISE_180:
				return state.withProperty(PLACING, EnumFacing.WEST);
			case CLOCKWISE_90:
				return state.withProperty(PLACING, EnumFacing.SOUTH);
			case COUNTERCLOCKWISE_90:
				return state.withProperty(PLACING, EnumFacing.NORTH);
			default:
		    	return state;
			}
		case NORTH:
			switch (rot)
			{
			case CLOCKWISE_180:
				return state.withProperty(PLACING, EnumFacing.SOUTH);
			case CLOCKWISE_90:
				return state.withProperty(PLACING, EnumFacing.EAST);
			case COUNTERCLOCKWISE_90:
				return state.withProperty(PLACING, EnumFacing.WEST);
			default:
		    	return state;
			}
		case SOUTH:
			switch (rot)
			{
			case CLOCKWISE_180:
				return state.withProperty(PLACING, EnumFacing.NORTH);
			case CLOCKWISE_90:
				return state.withProperty(PLACING, EnumFacing.WEST);
			case COUNTERCLOCKWISE_90:
				return state.withProperty(PLACING, EnumFacing.EAST);
			default:
		    	return state;
			}
		default:
	    	return state;
    	}
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    @Override
	public IBlockState withMirror(IBlockState state, Mirror mirror)
    {
    	switch (mirror)
    	{
    	case LEFT_RIGHT:
    	{
        	switch (state.getValue(PLACING))
        	{
        	case NORTH:
        		return state.withProperty(PLACING, EnumFacing.SOUTH);
        	case SOUTH:
        		return state.withProperty(PLACING, EnumFacing.NORTH);
    		default:
    	    	return state;
        	}
    	}
    	case FRONT_BACK:
    	{
        	switch (state.getValue(PLACING))
        	{
        	case WEST:
        		return state.withProperty(PLACING, EnumFacing.EAST);
        	case EAST:
        		return state.withProperty(PLACING, EnumFacing.WEST);
    		default:
    	    	return state;
        	}
    	}
		default:
	    	return state;
    	}
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face)
    {
        if (net.minecraftforge.common.ForgeModContainer.disableStairSlabCulling) return super.doesSideBlockRendering(state, world, pos, face);
        if (state.isOpaqueCube()) return true;
        return face == state.getValue(PLACING) && modelState.doesSideBlockRendering(world, pos, face);
    }

	@Override
	@SideOnly(Side.CLIENT)
	public IBlockHighlight getBlockHighlight(EntityPlayer player, RayTraceResult trace)
	{
		return IBlockHighlight.SLAB;
	}
}