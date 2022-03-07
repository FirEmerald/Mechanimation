package firemerald.mechanimation.blocks;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import firemerald.mechanimation.client.renderer.IBlockHighlight;
import firemerald.mechanimation.init.MechanimationTabs;
import firemerald.mechanimation.util.Constants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
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

public class BlockMechanimationStairs extends Block implements ICustomBlockHighlight
{
    public static final PropertyEnum<EnumPlacing> PLACING = PropertyEnum.<EnumPlacing>create("placing", EnumPlacing.class);
    public static final PropertyEnum<EnumShape> SHAPE = PropertyEnum.<EnumShape>create("shape", EnumShape.class);

    private final Block modelBlock;
    private final IBlockState modelState;

	public BlockMechanimationStairs(IBlockState modelState)
	{
        super(modelState.getBlock().blockMaterial);
        this.setDefaultState(this.blockState.getBaseState().withProperty(PLACING, EnumPlacing.UP_WEST).withProperty(SHAPE, EnumShape.STRAIGHT));
        this.modelBlock = modelState.getBlock();
        this.modelState = modelState;
        this.setHardness(this.modelBlock.blockHardness);
        this.setResistance(this.modelBlock.blockResistance / 3.0F);
        this.setSoundType(this.modelBlock.blockSoundType);
        this.setHarvestLevel(modelBlock.getHarvestTool(modelState), modelBlock.getHarvestLevel(modelState));
        this.setLightOpacity(255);
        this.setCreativeTab(MechanimationTabs.DECORATIONS);
	}

    @Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        @SuppressWarnings("deprecation")
		IBlockState iblockstate = super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(SHAPE, EnumShape.STRAIGHT);
        hitX -= 0.5f;
        hitY -= 0.5f;
        hitZ -= 0.5f;
        switch (facing)
        {
		case DOWN:
			if (hitX < -.25f)
			{
				if (hitZ < -.25f)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.SOUTH_EAST);
					break;
				}
				else if (hitZ > .25f)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.NORTH_EAST);
					break;
				}
			}
			else if (hitX > .25f)
			{
				if (hitZ < -.25f)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.SOUTH_WEST);
					break;
				}
				else if (hitZ > .25f)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.NORTH_WEST);
					break;
				}
			}
			if (hitX > hitZ)
			{
				if (hitX > -hitZ)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.DOWN_WEST);
				}
				else
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.DOWN_SOUTH);
				}
			}
			else
			{
				if (hitZ > -hitX)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.DOWN_NORTH);
				}
				else
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.DOWN_EAST);
				}
			}
			break;
		case UP:
			if (hitX < -.25f)
			{
				if (hitZ < -.25f)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.SOUTH_EAST);
					break;
				}
				else if (hitZ > .25f)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.NORTH_EAST);
					break;
				}
			}
			else if (hitX > .25f)
			{
				if (hitZ < -.25f)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.SOUTH_WEST);
					break;
				}
				else if (hitZ > .25f)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.NORTH_WEST);
					break;
				}
			}
			if (hitX > hitZ)
			{
				if (hitX > -hitZ)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.UP_WEST);
				}
				else
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.UP_SOUTH);
				}
			}
			else
			{
				if (hitZ > -hitX)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.UP_NORTH);
				}
				else
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.UP_EAST);
				}
			}
			break;
		case NORTH:
			if (hitX < -.25f)
			{
				if (hitY < -.25f)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.UP_EAST);
					break;
				}
				else if (hitY > .25f)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.DOWN_EAST);
					break;
				}
			}
			else if (hitX > .25f)
			{
				if (hitY < -.25f)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.UP_WEST);
					break;
				}
				else if (hitY > .25f)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.DOWN_WEST);
					break;
				}
			}
			if (hitX > hitY)
			{
				if (hitX > -hitY)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.NORTH_WEST);
				}
				else
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.UP_NORTH);
				}
			}
			else
			{
				if (hitY > -hitX)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.DOWN_NORTH);
				}
				else
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.NORTH_EAST);
				}
			}
			break;
		case SOUTH:
			if (hitX < -.25f)
			{
				if (hitY < -.25f)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.UP_EAST);
					break;
				}
				else if (hitY > .25f)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.DOWN_EAST);
					break;
				}
			}
			else if (hitX > .25f)
			{
				if (hitY < -.25f)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.UP_WEST);
					break;
				}
				else if (hitY > .25f)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.DOWN_WEST);
					break;
				}
			}
			if (hitX > hitY)
			{
				if (hitX > -hitY)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.SOUTH_WEST);
				}
				else
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.UP_SOUTH);
				}
			}
			else
			{
				if (hitY > -hitX)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.DOWN_SOUTH);
				}
				else
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.SOUTH_EAST);
				}
			}
			break;
		case WEST:
			if (hitY < -.25f)
			{
				if (hitZ < -.25f)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.UP_SOUTH);
					break;
				}
				else if (hitZ > .25f)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.UP_NORTH);
					break;
				}
			}
			else if (hitY > .25f)
			{
				if (hitZ < -.25f)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.DOWN_SOUTH);
					break;
				}
				else if (hitZ > .25f)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.DOWN_NORTH);
					break;
				}
			}
			if (hitZ > hitY)
			{
				if (hitZ > -hitY)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.NORTH_WEST);
				}
				else
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.UP_WEST);
				}
			}
			else
			{
				if (hitY > -hitZ)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.DOWN_WEST);
				}
				else
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.SOUTH_WEST);
				}
			}
			break;
		case EAST:
			if (hitY < -.25f)
			{
				if (hitZ < -.25f)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.UP_SOUTH);
					break;
				}
				else if (hitZ > .25f)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.UP_NORTH);
					break;
				}
			}
			else if (hitY > .25f)
			{
				if (hitZ < -.25f)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.DOWN_SOUTH);
					break;
				}
				else if (hitZ > .25f)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.DOWN_NORTH);
					break;
				}
			}
			if (hitZ > hitY)
			{
				if (hitZ > -hitY)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.NORTH_EAST);
				}
				else
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.UP_EAST);
				}
			}
			else
			{
				if (hitY > -hitZ)
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.DOWN_EAST);
				}
				else
				{
					iblockstate = iblockstate.withProperty(PLACING, EnumPlacing.SOUTH_EAST);
				}
			}
			break;
        }
        return iblockstate;
    }

	@Override
    public IBlockState getStateFromMeta(int meta)
    {
		if (meta < 0 || meta >= EnumPlacing.values().length) meta = 0;
		return this.getDefaultState().withProperty(PLACING, EnumPlacing.values()[meta]);
    }

	@Override
    public int getMetaFromState(IBlockState state)
    {
		return state.getValue(PLACING).ordinal();
    }

	@Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
		switch (state.getValue(PLACING))
		{
		case UP_WEST:
		{
			BlockPos southPos = pos.south();
			IBlockState southState = world.getBlockState(southPos);
			EnumPlacing southPlacing = getPlacing(state, pos, southState, southPos, world);
			if (southPlacing != EnumPlacing.UP_WEST)
			{
				BlockPos eastPos = pos.east();
				IBlockState eastState = world.getBlockState(eastPos);
				EnumPlacing eastPlacing = getPlacing(state, pos, eastState, eastPos, world);
				BlockPos downPos = pos.down();
				IBlockState downState = world.getBlockState(downPos);
				EnumPlacing downPlacing = getPlacing(state, pos, downState, downPos, world);
				if (eastPlacing == EnumPlacing.UP_SOUTH)
				{
					if (downPlacing == EnumPlacing.SOUTH_WEST)
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_RIGHT);
					}
					else
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_HORIZONTAL_RIGHT);
					}
				}
				else if (downPlacing == EnumPlacing.SOUTH_WEST)
				{
			    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_VERTICAL_RIGHT);
				}
			}
			BlockPos northPos = pos.north();
			IBlockState northState = world.getBlockState(northPos);
			EnumPlacing northPlacing = getPlacing(state, pos, northState, northPos, world);
			if (northPlacing != EnumPlacing.UP_WEST)
			{
				BlockPos eastPos = pos.east();
				IBlockState eastState = world.getBlockState(eastPos);
				EnumPlacing eastPlacing = getPlacing(state, pos, eastState, eastPos, world);
				BlockPos downPos = pos.down();
				IBlockState downState = world.getBlockState(downPos);
				EnumPlacing downPlacing = getPlacing(state, pos, downState, downPos, world);
				if (eastPlacing == EnumPlacing.UP_NORTH)
				{
					if (downPlacing == EnumPlacing.NORTH_WEST)
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_LEFT);
					}
					else
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_HORIZONTAL_LEFT);
					}
				}
				else if (downPlacing == EnumPlacing.NORTH_WEST)
				{
			    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_VERTICAL_LEFT);
				}
			}
			if (southPlacing != EnumPlacing.UP_WEST)
			{
				BlockPos westPos = pos.west();
				IBlockState westState = world.getBlockState(westPos);
				EnumPlacing westPlacing = getPlacing(state, pos, westState, westPos, world);
				if  (westPlacing == EnumPlacing.UP_NORTH)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_RIGHT);
				}
				BlockPos upPos = pos.up();
				IBlockState upState = world.getBlockState(upPos);
				EnumPlacing upPlacing = getPlacing(state, pos, upState, upPos, world);
				if  (upPlacing == EnumPlacing.NORTH_WEST)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_RIGHT);
				}
			}
			if (northPlacing != EnumPlacing.UP_WEST)
			{
				BlockPos westPos = pos.west();
				IBlockState westState = world.getBlockState(westPos);
				EnumPlacing westPlacing = getPlacing(state, pos, westState, westPos, world);
				if  (westPlacing == EnumPlacing.UP_SOUTH)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_LEFT);
				}
				BlockPos upPos = pos.up();
				IBlockState upState = world.getBlockState(upPos);
				EnumPlacing upPlacing = getPlacing(state, pos, upState, upPos, world);
				if  (upPlacing == EnumPlacing.SOUTH_WEST)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_LEFT);
				}
			}
		}
		break;
		case UP_EAST:
		{
			BlockPos northPos = pos.north();
			IBlockState northState = world.getBlockState(northPos);
			EnumPlacing northPlacing = getPlacing(state, pos, northState, northPos, world);
			if (northPlacing != EnumPlacing.UP_EAST)
			{
				BlockPos westPos = pos.west();
				IBlockState westState = world.getBlockState(westPos);
				EnumPlacing westPlacing = getPlacing(state, pos, westState, westPos, world);
				BlockPos downPos = pos.down();
				IBlockState downState = world.getBlockState(downPos);
				EnumPlacing downPlacing = getPlacing(state, pos, downState, downPos, world);
				if (westPlacing == EnumPlacing.UP_NORTH)
				{
					if (downPlacing == EnumPlacing.NORTH_EAST)
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_RIGHT);
					}
					else
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_HORIZONTAL_RIGHT);
					}
				}
				else if (downPlacing == EnumPlacing.NORTH_EAST)
				{
			    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_VERTICAL_RIGHT);
				}
			}
			BlockPos southPos = pos.south();
			IBlockState southState = world.getBlockState(southPos);
			EnumPlacing southPlacing = getPlacing(state, pos, southState, southPos, world);
			if (southPlacing != EnumPlacing.UP_EAST)
			{
				BlockPos westPos = pos.west();
				IBlockState westState = world.getBlockState(westPos);
				EnumPlacing westPlacing = getPlacing(state, pos, westState, westPos, world);
				BlockPos downPos = pos.down();
				IBlockState downState = world.getBlockState(downPos);
				EnumPlacing downPlacing = getPlacing(state, pos, downState, downPos, world);
				if (westPlacing == EnumPlacing.UP_SOUTH)
				{
					if (downPlacing == EnumPlacing.SOUTH_EAST)
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_LEFT);
					}
					else
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_HORIZONTAL_LEFT);
					}
				}
				else if (downPlacing == EnumPlacing.SOUTH_EAST)
				{
			    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_VERTICAL_LEFT);
				}
			}
			if (northPlacing != EnumPlacing.UP_EAST)
			{
				BlockPos eastPos = pos.east();
				IBlockState eastState = world.getBlockState(eastPos);
				EnumPlacing eastPlacing = getPlacing(state, pos, eastState, eastPos, world);
				if  (eastPlacing == EnumPlacing.UP_SOUTH)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_RIGHT);
				}
				BlockPos upPos = pos.up();
				IBlockState upState = world.getBlockState(upPos);
				EnumPlacing upPlacing = getPlacing(state, pos, upState, upPos, world);
				if  (upPlacing == EnumPlacing.SOUTH_EAST)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_RIGHT);
				}
			}
			if (southPlacing != EnumPlacing.UP_EAST)
			{
				BlockPos eastPos = pos.east();
				IBlockState eastState = world.getBlockState(eastPos);
				EnumPlacing eastPlacing = getPlacing(state, pos, eastState, eastPos, world);
				if  (eastPlacing == EnumPlacing.UP_NORTH)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_LEFT);
				}
				BlockPos upPos = pos.up();
				IBlockState upState = world.getBlockState(upPos);
				EnumPlacing upPlacing = getPlacing(state, pos, upState, upPos, world);
				if  (upPlacing == EnumPlacing.NORTH_EAST)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_LEFT);
				}
			}
		}
		break;
		case UP_NORTH:
		{
			BlockPos westPos = pos.west();
			IBlockState westState = world.getBlockState(westPos);
			EnumPlacing westPlacing = getPlacing(state, pos, westState, westPos, world);
			if (westPlacing != EnumPlacing.UP_NORTH)
			{
				BlockPos southPos = pos.south();
				IBlockState southState = world.getBlockState(southPos);
				EnumPlacing southPlacing = getPlacing(state, pos, southState, southPos, world);
				BlockPos downPos = pos.down();
				IBlockState downState = world.getBlockState(downPos);
				EnumPlacing downPlacing = getPlacing(state, pos, downState, downPos, world);
				if (southPlacing == EnumPlacing.UP_WEST)
				{
					if (downPlacing == EnumPlacing.NORTH_WEST)
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_RIGHT);
					}
					else
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_HORIZONTAL_RIGHT);
					}
				}
				else if (downPlacing == EnumPlacing.NORTH_WEST)
				{
			    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_VERTICAL_RIGHT);
				}
			}
			BlockPos eastPos = pos.east();
			IBlockState eastState = world.getBlockState(eastPos);
			EnumPlacing eastPlacing = getPlacing(state, pos, eastState, eastPos, world);
			if (eastPlacing != EnumPlacing.UP_NORTH)
			{
				BlockPos southPos = pos.south();
				IBlockState southState = world.getBlockState(southPos);
				EnumPlacing southPlacing = getPlacing(state, pos, southState, southPos, world);
				BlockPos downPos = pos.down();
				IBlockState downState = world.getBlockState(downPos);
				EnumPlacing downPlacing = getPlacing(state, pos, downState, downPos, world);
				if (southPlacing == EnumPlacing.UP_EAST)
				{
					if (downPlacing == EnumPlacing.NORTH_EAST)
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_LEFT);
					}
					else
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_HORIZONTAL_LEFT);
					}
				}
				else if (downPlacing == EnumPlacing.NORTH_EAST)
				{
			    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_VERTICAL_LEFT);
				}
			}
			if (westPlacing != EnumPlacing.UP_NORTH)
			{
				BlockPos northPos = pos.north();
				IBlockState northState = world.getBlockState(northPos);
				EnumPlacing northPlacing = getPlacing(state, pos, northState, northPos, world);
				if  (northPlacing == EnumPlacing.UP_EAST)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_RIGHT);
				}
				BlockPos upPos = pos.up();
				IBlockState upState = world.getBlockState(upPos);
				EnumPlacing upPlacing = getPlacing(state, pos, upState, upPos, world);
				if  (upPlacing == EnumPlacing.NORTH_EAST)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_RIGHT);
				}
			}
			if (eastPlacing != EnumPlacing.UP_NORTH)
			{
				BlockPos northPos = pos.north();
				IBlockState northState = world.getBlockState(northPos);
				EnumPlacing northPlacing = getPlacing(state, pos, northState, northPos, world);
				if  (northPlacing == EnumPlacing.UP_WEST)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_LEFT);
				}
				BlockPos upPos = pos.up();
				IBlockState upState = world.getBlockState(upPos);
				EnumPlacing upPlacing = getPlacing(state, pos, upState, upPos, world);
				if  (upPlacing == EnumPlacing.NORTH_WEST)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_LEFT);
				}
			}
		}
		break;
		case UP_SOUTH:
		{
			BlockPos eastPos = pos.east();
			IBlockState eastState = world.getBlockState(eastPos);
			EnumPlacing eastPlacing = getPlacing(state, pos, eastState, eastPos, world);
			if (eastPlacing != EnumPlacing.UP_SOUTH)
			{
				BlockPos northPos = pos.north();
				IBlockState northState = world.getBlockState(northPos);
				EnumPlacing northPlacing = getPlacing(state, pos, northState, northPos, world);
				BlockPos downPos = pos.down();
				IBlockState downState = world.getBlockState(downPos);
				EnumPlacing downPlacing = getPlacing(state, pos, downState, downPos, world);
				if (northPlacing == EnumPlacing.UP_EAST)
				{
					if (downPlacing == EnumPlacing.SOUTH_EAST)
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_RIGHT);
					}
					else
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_HORIZONTAL_RIGHT);
					}
				}
				else if (downPlacing == EnumPlacing.SOUTH_EAST)
				{
			    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_VERTICAL_RIGHT);
				}
			}
			BlockPos westPos = pos.west();
			IBlockState westState = world.getBlockState(westPos);
			EnumPlacing westPlacing = getPlacing(state, pos, westState, westPos, world);
			if (westPlacing != EnumPlacing.UP_SOUTH)
			{
				BlockPos northPos = pos.north();
				IBlockState northState = world.getBlockState(northPos);
				EnumPlacing northPlacing = getPlacing(state, pos, northState, northPos, world);
				BlockPos downPos = pos.down();
				IBlockState downState = world.getBlockState(downPos);
				EnumPlacing downPlacing = getPlacing(state, pos, downState, downPos, world);
				if (northPlacing == EnumPlacing.UP_WEST)
				{
					if (downPlacing == EnumPlacing.SOUTH_WEST)
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_LEFT);
					}
					else
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_HORIZONTAL_LEFT);
					}
				}
				else if (downPlacing == EnumPlacing.SOUTH_WEST)
				{
			    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_VERTICAL_LEFT);
				}
			}
			if (eastPlacing != EnumPlacing.UP_SOUTH)
			{
				BlockPos southPos = pos.south();
				IBlockState southState = world.getBlockState(southPos);
				EnumPlacing southPlacing = getPlacing(state, pos, southState, southPos, world);
				if  (southPlacing == EnumPlacing.UP_WEST)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_RIGHT);
				}
				BlockPos upPos = pos.up();
				IBlockState upState = world.getBlockState(upPos);
				EnumPlacing upPlacing = getPlacing(state, pos, upState, upPos, world);
				if  (upPlacing == EnumPlacing.SOUTH_WEST)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_RIGHT);
				}
			}
			if (westPlacing != EnumPlacing.UP_SOUTH)
			{
				BlockPos southPos = pos.south();
				IBlockState southState = world.getBlockState(southPos);
				EnumPlacing southPlacing = getPlacing(state, pos, southState, southPos, world);
				if  (southPlacing == EnumPlacing.UP_EAST)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_LEFT);
				}
				BlockPos upPos = pos.up();
				IBlockState upState = world.getBlockState(upPos);
				EnumPlacing upPlacing = getPlacing(state, pos, upState, upPos, world);
				if  (upPlacing == EnumPlacing.SOUTH_EAST)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_LEFT);
				}
			}
		}
		break;
		case DOWN_WEST:
		{
			BlockPos northPos = pos.north();
			IBlockState northState = world.getBlockState(northPos);
			EnumPlacing northPlacing = getPlacing(state, pos, northState, northPos, world);
			if (northPlacing != EnumPlacing.DOWN_WEST)
			{
				BlockPos eastPos = pos.east();
				IBlockState eastState = world.getBlockState(eastPos);
				EnumPlacing eastPlacing = getPlacing(state, pos, eastState, eastPos, world);
				BlockPos upPos = pos.up();
				IBlockState upState = world.getBlockState(upPos);
				EnumPlacing upPlacing = getPlacing(state, pos, upState, upPos, world);
				if (eastPlacing == EnumPlacing.DOWN_NORTH)
				{
					if (upPlacing == EnumPlacing.NORTH_WEST)
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_RIGHT);
					}
					else
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_HORIZONTAL_RIGHT);
					}
				}
				else if (upPlacing == EnumPlacing.NORTH_WEST)
				{
			    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_VERTICAL_RIGHT);
				}
			}
			BlockPos southPos = pos.south();
			IBlockState southState = world.getBlockState(southPos);
			EnumPlacing southPlacing = getPlacing(state, pos, southState, southPos, world);
			if (southPlacing != EnumPlacing.DOWN_WEST)
			{
				BlockPos eastPos = pos.east();
				IBlockState eastState = world.getBlockState(eastPos);
				EnumPlacing eastPlacing = getPlacing(state, pos, eastState, eastPos, world);
				BlockPos upPos = pos.up();
				IBlockState upState = world.getBlockState(upPos);
				EnumPlacing upPlacing = getPlacing(state, pos, upState, upPos, world);
				if (eastPlacing == EnumPlacing.DOWN_SOUTH)
				{
					if (upPlacing == EnumPlacing.SOUTH_WEST)
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_LEFT);
					}
					else
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_HORIZONTAL_LEFT);
					}
				}
				else if (upPlacing == EnumPlacing.SOUTH_WEST)
				{
			    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_VERTICAL_LEFT);
				}
			}
			if (northPlacing != EnumPlacing.DOWN_WEST)
			{
				BlockPos westPos = pos.west();
				IBlockState westState = world.getBlockState(westPos);
				EnumPlacing westPlacing = getPlacing(state, pos, westState, westPos, world);
				if  (westPlacing == EnumPlacing.DOWN_SOUTH)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_RIGHT);
				}
				BlockPos downPos = pos.down();
				IBlockState downState = world.getBlockState(downPos);
				EnumPlacing downPlacing = getPlacing(state, pos, downState, downPos, world);
				if  (downPlacing == EnumPlacing.SOUTH_WEST)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_RIGHT);
				}
			}
			if (southPlacing != EnumPlacing.DOWN_WEST)
			{
				BlockPos westPos = pos.west();
				IBlockState westState = world.getBlockState(westPos);
				EnumPlacing westPlacing = getPlacing(state, pos, westState, westPos, world);
				if  (westPlacing == EnumPlacing.DOWN_NORTH)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_LEFT);
				}
				BlockPos downPos = pos.down();
				IBlockState downState = world.getBlockState(downPos);
				EnumPlacing downPlacing = getPlacing(state, pos, downState, downPos, world);
				if  (downPlacing == EnumPlacing.NORTH_WEST)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_LEFT);
				}
			}
		}
		break;
		case DOWN_EAST:
		{
			BlockPos southPos = pos.south();
			IBlockState southState = world.getBlockState(southPos);
			EnumPlacing southPlacing = getPlacing(state, pos, southState, southPos, world);
			if (southPlacing != EnumPlacing.DOWN_EAST)
			{
				BlockPos westPos = pos.west();
				IBlockState westState = world.getBlockState(westPos);
				EnumPlacing westPlacing = getPlacing(state, pos, westState, westPos, world);
				BlockPos upPos = pos.up();
				IBlockState upState = world.getBlockState(upPos);
				EnumPlacing upPlacing = getPlacing(state, pos, upState, upPos, world);
				if (westPlacing == EnumPlacing.DOWN_SOUTH)
				{
					if (upPlacing == EnumPlacing.SOUTH_EAST)
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_RIGHT);
					}
					else
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_HORIZONTAL_RIGHT);
					}
				}
				else if (upPlacing == EnumPlacing.SOUTH_EAST)
				{
			    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_VERTICAL_RIGHT);
				}
			}
			BlockPos northPos = pos.north();
			IBlockState northState = world.getBlockState(northPos);
			EnumPlacing northPlacing = getPlacing(state, pos, northState, northPos, world);
			if (northPlacing != EnumPlacing.DOWN_EAST)
			{
				BlockPos westPos = pos.west();
				IBlockState westState = world.getBlockState(westPos);
				EnumPlacing westPlacing = getPlacing(state, pos, westState, westPos, world);
				BlockPos upPos = pos.up();
				IBlockState upState = world.getBlockState(upPos);
				EnumPlacing upPlacing = getPlacing(state, pos, upState, upPos, world);
				if (westPlacing == EnumPlacing.DOWN_NORTH)
				{
					if (upPlacing == EnumPlacing.NORTH_EAST)
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_LEFT);
					}
					else
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_HORIZONTAL_LEFT);
					}
				}
				else if (upPlacing == EnumPlacing.NORTH_EAST)
				{
			    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_VERTICAL_LEFT);
				}
			}
			if (southPlacing != EnumPlacing.DOWN_EAST)
			{
				BlockPos eastPos = pos.east();
				IBlockState eastState = world.getBlockState(eastPos);
				EnumPlacing eastPlacing = getPlacing(state, pos, eastState, eastPos, world);
				if  (eastPlacing == EnumPlacing.DOWN_NORTH)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_RIGHT);
				}
				BlockPos downPos = pos.down();
				IBlockState downState = world.getBlockState(downPos);
				EnumPlacing downPlacing = getPlacing(state, pos, downState, downPos, world);
				if  (downPlacing == EnumPlacing.NORTH_EAST)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_RIGHT);
				}
			}
			if (northPlacing != EnumPlacing.DOWN_EAST)
			{
				BlockPos eastPos = pos.east();
				IBlockState eastState = world.getBlockState(eastPos);
				EnumPlacing eastPlacing = getPlacing(state, pos, eastState, eastPos, world);
				if  (eastPlacing == EnumPlacing.DOWN_SOUTH)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_LEFT);
				}
				BlockPos downPos = pos.down();
				IBlockState downState = world.getBlockState(downPos);
				EnumPlacing downPlacing = getPlacing(state, pos, downState, downPos, world);
				if  (downPlacing == EnumPlacing.SOUTH_EAST)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_LEFT);
				}
			}
		}
		break;
		case DOWN_NORTH:
		{
			BlockPos eastPos = pos.east();
			IBlockState eastState = world.getBlockState(eastPos);
			EnumPlacing eastPlacing = getPlacing(state, pos, eastState, eastPos, world);
			if (eastPlacing != EnumPlacing.DOWN_NORTH)
			{
				BlockPos southPos = pos.south();
				IBlockState southState = world.getBlockState(southPos);
				EnumPlacing southPlacing = getPlacing(state, pos, southState, southPos, world);
				BlockPos upPos = pos.up();
				IBlockState upState = world.getBlockState(upPos);
				EnumPlacing upPlacing = getPlacing(state, pos, upState, upPos, world);
				if (southPlacing == EnumPlacing.DOWN_EAST)
				{
					if (upPlacing == EnumPlacing.NORTH_EAST)
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_RIGHT);
					}
					else
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_HORIZONTAL_RIGHT);
					}
				}
				else if (upPlacing == EnumPlacing.NORTH_EAST)
				{
			    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_VERTICAL_RIGHT);
				}
			}
			BlockPos westPos = pos.west();
			IBlockState westState = world.getBlockState(westPos);
			EnumPlacing westPlacing = getPlacing(state, pos, westState, westPos, world);
			if (westPlacing != EnumPlacing.DOWN_NORTH)
			{
				BlockPos southPos = pos.south();
				IBlockState southState = world.getBlockState(southPos);
				EnumPlacing southPlacing = getPlacing(state, pos, southState, southPos, world);
				BlockPos upPos = pos.up();
				IBlockState upState = world.getBlockState(upPos);
				EnumPlacing upPlacing = getPlacing(state, pos, upState, upPos, world);
				if (southPlacing == EnumPlacing.DOWN_WEST)
				{
					if (upPlacing == EnumPlacing.NORTH_WEST)
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_LEFT);
					}
					else
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_HORIZONTAL_LEFT);
					}
				}
				else if (upPlacing == EnumPlacing.NORTH_WEST)
				{
			    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_VERTICAL_LEFT);
				}
			}
			if (eastPlacing != EnumPlacing.DOWN_NORTH)
			{
				BlockPos northPos = pos.north();
				IBlockState northState = world.getBlockState(northPos);
				EnumPlacing northPlacing = getPlacing(state, pos, northState, northPos, world);
				if  (northPlacing == EnumPlacing.DOWN_WEST)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_RIGHT);
				}
				BlockPos downPos = pos.down();
				IBlockState downState = world.getBlockState(downPos);
				EnumPlacing downPlacing = getPlacing(state, pos, downState, downPos, world);
				if  (downPlacing == EnumPlacing.NORTH_WEST)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_RIGHT);
				}
			}
			if (westPlacing != EnumPlacing.DOWN_NORTH)
			{
				BlockPos northPos = pos.north();
				IBlockState northState = world.getBlockState(northPos);
				EnumPlacing northPlacing = getPlacing(state, pos, northState, northPos, world);
				if  (northPlacing == EnumPlacing.DOWN_EAST)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_LEFT);
				}
				BlockPos downPos = pos.down();
				IBlockState downState = world.getBlockState(downPos);
				EnumPlacing downPlacing = getPlacing(state, pos, downState, downPos, world);
				if  (downPlacing == EnumPlacing.NORTH_EAST)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_LEFT);
				}
			}
		}
		break;
		case DOWN_SOUTH:
		{
			BlockPos westPos = pos.west();
			IBlockState westState = world.getBlockState(westPos);
			EnumPlacing westPlacing = getPlacing(state, pos, westState, westPos, world);
			if (westPlacing != EnumPlacing.DOWN_SOUTH)
			{
				BlockPos northPos = pos.north();
				IBlockState northState = world.getBlockState(northPos);
				EnumPlacing northPlacing = getPlacing(state, pos, northState, northPos, world);
				BlockPos upPos = pos.up();
				IBlockState upState = world.getBlockState(upPos);
				EnumPlacing upPlacing = getPlacing(state, pos, upState, upPos, world);
				if (northPlacing == EnumPlacing.DOWN_WEST)
				{
					if (upPlacing == EnumPlacing.SOUTH_WEST)
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_RIGHT);
					}
					else
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_HORIZONTAL_RIGHT);
					}
				}
				else if (upPlacing == EnumPlacing.SOUTH_WEST)
				{
			    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_VERTICAL_RIGHT);
				}
			}
			BlockPos eastPos = pos.east();
			IBlockState eastState = world.getBlockState(eastPos);
			EnumPlacing eastPlacing = getPlacing(state, pos, eastState, eastPos, world);
			if (eastPlacing != EnumPlacing.DOWN_SOUTH)
			{
				BlockPos northPos = pos.north();
				IBlockState northState = world.getBlockState(northPos);
				EnumPlacing northPlacing = getPlacing(state, pos, northState, northPos, world);
				BlockPos upPos = pos.up();
				IBlockState upState = world.getBlockState(upPos);
				EnumPlacing upPlacing = getPlacing(state, pos, upState, upPos, world);
				if (northPlacing == EnumPlacing.DOWN_EAST)
				{
					if (upPlacing == EnumPlacing.SOUTH_EAST)
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_LEFT);
					}
					else
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_HORIZONTAL_LEFT);
					}
				}
				else if (upPlacing == EnumPlacing.SOUTH_EAST)
				{
			    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_VERTICAL_LEFT);
				}
			}
			if (westPlacing != EnumPlacing.DOWN_SOUTH)
			{
				BlockPos southPos = pos.south();
				IBlockState southState = world.getBlockState(southPos);
				EnumPlacing southPlacing = getPlacing(state, pos, southState, southPos, world);
				if  (southPlacing == EnumPlacing.DOWN_EAST)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_RIGHT);
				}
				BlockPos downPos = pos.down();
				IBlockState downState = world.getBlockState(downPos);
				EnumPlacing downPlacing = getPlacing(state, pos, downState, downPos, world);
				if  (downPlacing == EnumPlacing.SOUTH_EAST)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_RIGHT);
				}
			}
			if (eastPlacing != EnumPlacing.DOWN_SOUTH)
			{
				BlockPos southPos = pos.south();
				IBlockState southState = world.getBlockState(southPos);
				EnumPlacing southPlacing = getPlacing(state, pos, southState, southPos, world);
				if  (southPlacing == EnumPlacing.DOWN_WEST)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_LEFT);
				}
				BlockPos downPos = pos.down();
				IBlockState downState = world.getBlockState(downPos);
				EnumPlacing downPlacing = getPlacing(state, pos, downState, downPos, world);
				if  (downPlacing == EnumPlacing.SOUTH_WEST)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_LEFT);
				}
			}
		}
		break;
		case NORTH_WEST:
		{
			BlockPos upPos = pos.up();
			IBlockState upState = world.getBlockState(upPos);
			EnumPlacing upPlacing = getPlacing(state, pos, upState, upPos, world);
			if (upPlacing != EnumPlacing.NORTH_WEST)
			{
				BlockPos eastPos = pos.east();
				IBlockState eastState = world.getBlockState(eastPos);
				EnumPlacing eastPlacing = getPlacing(state, pos, eastState, eastPos, world);
				BlockPos southPos = pos.south();
				IBlockState southState = world.getBlockState(southPos);
				EnumPlacing southPlacing = getPlacing(state, pos, southState, southPos, world);
				if (eastPlacing == EnumPlacing.UP_NORTH)
				{
					if (southPlacing == EnumPlacing.UP_WEST)
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_RIGHT);
					}
					else
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_HORIZONTAL_RIGHT);
					}
				}
				else if (southPlacing == EnumPlacing.UP_WEST)
				{
			    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_VERTICAL_RIGHT);
				}
			}
			BlockPos downPos = pos.down();
			IBlockState downState = world.getBlockState(downPos);
			EnumPlacing downPlacing = getPlacing(state, pos, downState, downPos, world);
			if (downPlacing != EnumPlacing.NORTH_WEST)
			{
				BlockPos eastPos = pos.east();
				IBlockState eastState = world.getBlockState(eastPos);
				EnumPlacing eastPlacing = getPlacing(state, pos, eastState, eastPos, world);
				BlockPos southPos = pos.south();
				IBlockState southState = world.getBlockState(southPos);
				EnumPlacing southPlacing = getPlacing(state, pos, southState, southPos, world);
				if (eastPlacing == EnumPlacing.DOWN_NORTH)
				{
					if (southPlacing == EnumPlacing.DOWN_WEST)
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_LEFT);
					}
					else
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_HORIZONTAL_LEFT);
					}
				}
				else if (southPlacing == EnumPlacing.DOWN_WEST)
				{
			    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_VERTICAL_LEFT);
				}
			}
			if (upPlacing != EnumPlacing.NORTH_WEST)
			{
				BlockPos westPos = pos.west();
				IBlockState westState = world.getBlockState(westPos);
				EnumPlacing westPlacing = getPlacing(state, pos, westState, westPos, world);
				if  (westPlacing == EnumPlacing.DOWN_NORTH)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_RIGHT);
				}
				BlockPos northPos = pos.north();
				IBlockState northState = world.getBlockState(northPos);
				EnumPlacing northPlacing = getPlacing(state, pos, northState, northPos, world);
				if  (northPlacing == EnumPlacing.DOWN_WEST)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_RIGHT);
				}
			}
			if (downPlacing != EnumPlacing.NORTH_WEST)
			{
				BlockPos westPos = pos.west();
				IBlockState westState = world.getBlockState(westPos);
				EnumPlacing westPlacing = getPlacing(state, pos, westState, westPos, world);
				if  (westPlacing == EnumPlacing.UP_NORTH)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_LEFT);
				}
				BlockPos northPos = pos.north();
				IBlockState northState = world.getBlockState(northPos);
				EnumPlacing northPlacing = getPlacing(state, pos, northState, northPos, world);
				if  (northPlacing == EnumPlacing.UP_WEST)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_LEFT);
				}
			}
		}
		break;
		case NORTH_EAST:
		{
			BlockPos upPos = pos.up();
			IBlockState upState = world.getBlockState(upPos);
			EnumPlacing upPlacing = getPlacing(state, pos, upState, upPos, world);
			if (upPlacing != EnumPlacing.NORTH_EAST)
			{
				BlockPos southPos = pos.south();
				IBlockState southState = world.getBlockState(southPos);
				EnumPlacing southPlacing = getPlacing(state, pos, southState, southPos, world);
				BlockPos westPos = pos.west();
				IBlockState westState = world.getBlockState(westPos);
				EnumPlacing westPlacing = getPlacing(state, pos, westState, westPos, world);
				if (southPlacing == EnumPlacing.UP_EAST)
				{
					if (westPlacing == EnumPlacing.UP_NORTH)
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_RIGHT);
					}
					else
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_HORIZONTAL_RIGHT);
					}
				}
				else if (westPlacing == EnumPlacing.UP_NORTH)
				{
			    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_VERTICAL_RIGHT);
				}
			}
			BlockPos downPos = pos.down();
			IBlockState downState = world.getBlockState(downPos);
			EnumPlacing downPlacing = getPlacing(state, pos, downState, downPos, world);
			if (downPlacing != EnumPlacing.NORTH_EAST)
			{
				BlockPos southPos = pos.south();
				IBlockState southState = world.getBlockState(southPos);
				EnumPlacing southPlacing = getPlacing(state, pos, southState, southPos, world);
				BlockPos westPos = pos.west();
				IBlockState westState = world.getBlockState(westPos);
				EnumPlacing westPlacing = getPlacing(state, pos, westState, westPos, world);
				if (southPlacing == EnumPlacing.DOWN_EAST)
				{
					if (westPlacing == EnumPlacing.DOWN_NORTH)
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_LEFT);
					}
					else
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_HORIZONTAL_LEFT);
					}
				}
				else if (westPlacing == EnumPlacing.DOWN_NORTH)
				{
			    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_VERTICAL_LEFT);
				}
			}
			if (upPlacing != EnumPlacing.NORTH_EAST)
			{
				BlockPos northPos = pos.north();
				IBlockState northState = world.getBlockState(northPos);
				EnumPlacing northPlacing = getPlacing(state, pos, northState, northPos, world);
				if  (northPlacing == EnumPlacing.DOWN_EAST)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_RIGHT);
				}
				BlockPos eastPos = pos.east();
				IBlockState eastState = world.getBlockState(eastPos);
				EnumPlacing eastPlacing = getPlacing(state, pos, eastState, eastPos, world);
				if  (eastPlacing == EnumPlacing.DOWN_NORTH)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_RIGHT);
				}
			}
			if (downPlacing != EnumPlacing.NORTH_EAST)
			{
				BlockPos northPos = pos.north();
				IBlockState northState = world.getBlockState(northPos);
				EnumPlacing northPlacing = getPlacing(state, pos, northState, northPos, world);
				if  (northPlacing == EnumPlacing.UP_EAST)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_LEFT);
				}
				BlockPos eastPos = pos.east();
				IBlockState eastState = world.getBlockState(eastPos);
				EnumPlacing eastPlacing = getPlacing(state, pos, eastState, eastPos, world);
				if  (eastPlacing == EnumPlacing.UP_NORTH)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_LEFT);
				}
			}
		}
		break;
		case SOUTH_WEST:
		{
			BlockPos upPos = pos.up();
			IBlockState upState = world.getBlockState(upPos);
			EnumPlacing upPlacing = getPlacing(state, pos, upState, upPos, world);
			if (upPlacing != EnumPlacing.SOUTH_WEST)
			{
				BlockPos northPos = pos.north();
				IBlockState northState = world.getBlockState(northPos);
				EnumPlacing northPlacing = getPlacing(state, pos, northState, northPos, world);
				BlockPos eastPos = pos.east();
				IBlockState eastState = world.getBlockState(eastPos);
				EnumPlacing eastPlacing = getPlacing(state, pos, eastState, eastPos, world);
				if (northPlacing == EnumPlacing.UP_WEST)
				{
					if (eastPlacing == EnumPlacing.UP_SOUTH)
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_RIGHT);
					}
					else
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_HORIZONTAL_RIGHT);
					}
				}
				else if (eastPlacing == EnumPlacing.UP_SOUTH)
				{
			    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_VERTICAL_RIGHT);
				}
			}
			BlockPos downPos = pos.down();
			IBlockState downState = world.getBlockState(downPos);
			EnumPlacing downPlacing = getPlacing(state, pos, downState, downPos, world);
			if (downPlacing != EnumPlacing.SOUTH_WEST)
			{
				BlockPos northPos = pos.north();
				IBlockState northState = world.getBlockState(northPos);
				EnumPlacing northPlacing = getPlacing(state, pos, northState, northPos, world);
				BlockPos eastPos = pos.east();
				IBlockState eastState = world.getBlockState(eastPos);
				EnumPlacing eastPlacing = getPlacing(state, pos, eastState, eastPos, world);
				if (northPlacing == EnumPlacing.DOWN_WEST)
				{
					if (eastPlacing == EnumPlacing.DOWN_SOUTH)
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_LEFT);
					}
					else
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_HORIZONTAL_LEFT);
					}
				}
				else if (eastPlacing == EnumPlacing.DOWN_SOUTH)
				{
			    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_VERTICAL_LEFT);
				}
			}
			if (upPlacing != EnumPlacing.SOUTH_WEST)
			{
				BlockPos southPos = pos.south();
				IBlockState southState = world.getBlockState(southPos);
				EnumPlacing southPlacing = getPlacing(state, pos, southState, southPos, world);
				if  (southPlacing == EnumPlacing.DOWN_WEST)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_RIGHT);
				}
				BlockPos westPos = pos.west();
				IBlockState westState = world.getBlockState(westPos);
				EnumPlacing westPlacing = getPlacing(state, pos, westState, westPos, world);
				if  (westPlacing == EnumPlacing.DOWN_SOUTH)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_RIGHT);
				}
			}
			if (downPlacing != EnumPlacing.SOUTH_WEST)
			{
				BlockPos southPos = pos.south();
				IBlockState southState = world.getBlockState(southPos);
				EnumPlacing southPlacing = getPlacing(state, pos, southState, southPos, world);
				if  (southPlacing == EnumPlacing.UP_WEST)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_LEFT);
				}
				BlockPos westPos = pos.west();
				IBlockState westState = world.getBlockState(westPos);
				EnumPlacing westPlacing = getPlacing(state, pos, westState, westPos, world);
				if  (westPlacing == EnumPlacing.UP_SOUTH)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_LEFT);
				}
			}
		}
			break;
		case SOUTH_EAST:
		{
			BlockPos upPos = pos.up();
			IBlockState upState = world.getBlockState(upPos);
			EnumPlacing upPlacing = getPlacing(state, pos, upState, upPos, world);
			if (upPlacing != EnumPlacing.SOUTH_EAST)
			{
				BlockPos westPos = pos.west();
				IBlockState westState = world.getBlockState(westPos);
				EnumPlacing westPlacing = getPlacing(state, pos, westState, westPos, world);
				BlockPos northPos = pos.north();
				IBlockState northState = world.getBlockState(northPos);
				EnumPlacing northPlacing = getPlacing(state, pos, northState, northPos, world);
				if (westPlacing == EnumPlacing.UP_SOUTH)
				{
					if (northPlacing == EnumPlacing.UP_EAST)
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_RIGHT);
					}
					else
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_HORIZONTAL_RIGHT);
					}
				}
				else if (northPlacing == EnumPlacing.UP_EAST)
				{
			    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_VERTICAL_RIGHT);
				}
			}
			BlockPos downPos = pos.down();
			IBlockState downState = world.getBlockState(downPos);
			EnumPlacing downPlacing = getPlacing(state, pos, downState, downPos, world);
			if (downPlacing != EnumPlacing.SOUTH_EAST)
			{
				BlockPos westPos = pos.west();
				IBlockState westState = world.getBlockState(westPos);
				EnumPlacing westPlacing = getPlacing(state, pos, westState, westPos, world);
				BlockPos northPos = pos.north();
				IBlockState northState = world.getBlockState(northPos);
				EnumPlacing northPlacing = getPlacing(state, pos, northState, northPos, world);
				if (westPlacing == EnumPlacing.DOWN_SOUTH)
				{
					if (northPlacing == EnumPlacing.DOWN_EAST)
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_LEFT);
					}
					else
					{
				    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_HORIZONTAL_LEFT);
					}
				}
				else if (northPlacing == EnumPlacing.DOWN_EAST)
				{
			    	return state.withProperty(SHAPE, EnumShape.OUTSIDE_VERTICAL_LEFT);
				}
			}
			if (upPlacing != EnumPlacing.SOUTH_EAST)
			{
				BlockPos eastPos = pos.east();
				IBlockState eastState = world.getBlockState(eastPos);
				EnumPlacing eastPlacing = getPlacing(state, pos, eastState, eastPos, world);
				if  (eastPlacing == EnumPlacing.DOWN_SOUTH)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_RIGHT);
				}
				BlockPos southPos = pos.south();
				IBlockState southState = world.getBlockState(southPos);
				EnumPlacing southPlacing = getPlacing(state, pos, southState, southPos, world);
				if  (southPlacing == EnumPlacing.DOWN_EAST)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_RIGHT);
				}
			}
			if (downPlacing != EnumPlacing.SOUTH_EAST)
			{
				BlockPos eastPos = pos.east();
				IBlockState eastState = world.getBlockState(eastPos);
				EnumPlacing eastPlacing = getPlacing(state, pos, eastState, eastPos, world);
				if  (eastPlacing == EnumPlacing.UP_SOUTH)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_LEFT);
				}
				BlockPos southPos = pos.south();
				IBlockState southState = world.getBlockState(southPos);
				EnumPlacing southPlacing = getPlacing(state, pos, southState, southPos, world);
				if  (southPlacing == EnumPlacing.UP_EAST)
				{
			    	return state.withProperty(SHAPE, EnumShape.INSIDE_LEFT);
				}
			}
		}
		break;
		default:
			break;
		}
    	return state.withProperty(SHAPE, EnumShape.STRAIGHT);
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
        return this.modelState.getSelectedBoundingBox(worldIn, pos);
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

	public EnumPlacing getPlacing(IBlockState state, BlockPos pos, IBlockState otherState, BlockPos otherPos, IBlockAccess world)
	{
		Block otherBlock = otherState.getBlock();
		if (otherBlock instanceof BlockMechanimationStairs) return (doBothConnect(this, state, pos, (BlockMechanimationStairs) otherBlock, otherState, otherPos, world)) ? otherState.getValue(PLACING) : null;
		else if (otherBlock instanceof BlockStairs)
		{
			if (canConnectVanilla(state, pos, (BlockStairs) otherBlock, otherState, otherPos, world))
			{
				if (otherState.getValue(BlockStairs.HALF) == BlockStairs.EnumHalf.BOTTOM)
				{
					switch (otherState.getValue(BlockStairs.FACING))
					{
					case EAST:
						return EnumPlacing.UP_WEST;
					case NORTH:
						return EnumPlacing.UP_SOUTH;
					case SOUTH:
						return EnumPlacing.UP_NORTH;
					case WEST:
						return EnumPlacing.UP_EAST;
					default:
						return null;
					}
				}
				else
				{
					switch (otherState.getValue(BlockStairs.FACING))
					{
					case EAST:
						return EnumPlacing.DOWN_WEST;
					case NORTH:
						return EnumPlacing.DOWN_SOUTH;
					case SOUTH:
						return EnumPlacing.DOWN_NORTH;
					case WEST:
						return EnumPlacing.DOWN_EAST;
					default:
						return null;
					}
				}
			}
			else return null;
		}
		else return null;
	}

	public static boolean doBothConnect(BlockMechanimationStairs blockA, IBlockState blockStateA, BlockPos blockPosA, BlockMechanimationStairs blockB, IBlockState blockStateB, BlockPos blockPosB, IBlockAccess world)
	{
		return blockA.canConnectMechanimation(blockStateA, blockPosA, blockB, blockStateB, blockPosB, world) && blockB.canConnectMechanimation(blockStateB, blockPosB, blockA, blockStateA, blockPosA, world);
	}

	public boolean canConnectVanilla(IBlockState blockState, BlockPos blockPos, BlockStairs other, IBlockState otherBlockStateB, BlockPos otherBlockPos, IBlockAccess world)
	{
		return true;
	}

	public boolean canConnectMechanimation(IBlockState blockState, BlockPos blockPos, BlockMechanimationStairs other, IBlockState otherBlockStateB, BlockPos otherBlockPos, IBlockAccess world)
	{
		return true;
	}

	@Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PLACING, SHAPE);
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

    @Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState)
    {
        if (!isActualState && (worldIn.getWorldType() != WorldType.DEBUG_ALL_BLOCK_STATES))
        {
            state = this.getActualState(state, worldIn, pos);
        }

        for (AxisAlignedBB axisalignedbb : getCollisionBoxList(state))
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, axisalignedbb);
        }
    }

    @Override
	@Nullable
    public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end)
    {
        List<RayTraceResult> list = Lists.<RayTraceResult>newArrayList();

        for (AxisAlignedBB axisalignedbb : getCollisionBoxList(this.getActualState(blockState, worldIn, pos)))
        {
            list.add(this.rayTrace(pos, start, end, axisalignedbb));
        }

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

    private static List<AxisAlignedBB> getCollisionBoxList(IBlockState state)
    {
        List<AxisAlignedBB> list = Lists.<AxisAlignedBB>newArrayList();
        switch (state.getValue(PLACING))
        {
		case UP_WEST:
		{
			switch (state.getValue(SHAPE))
			{
			case STRAIGHT:
				list.add(Constants.SLAB_DOWN);
				list.add(Constants.EDGE_UP_EAST);
				break;
			case INSIDE_RIGHT:
				list.add(Constants.SLAB_DOWN);
				list.add(Constants.EDGE_UP_EAST);
				list.add(Constants.CORNER_UP_SOUTH_WEST);
				break;
			case OUTSIDE_RIGHT:
				list.add(Constants.EDGE_DOWN_EAST);
				list.add(Constants.CORNER_DOWN_NORTH_WEST);
				list.add(Constants.CORNER_UP_NORTH_EAST);
				break;
			case OUTSIDE_HORIZONTAL_RIGHT:
				list.add(Constants.SLAB_DOWN);
				list.add(Constants.CORNER_UP_NORTH_EAST);
				break;
			case OUTSIDE_VERTICAL_RIGHT:
				list.add(Constants.SLAB_EAST);
				list.add(Constants.CORNER_DOWN_NORTH_WEST);
				break;
			case INSIDE_LEFT:
				list.add(Constants.SLAB_DOWN);
				list.add(Constants.EDGE_UP_EAST);
				list.add(Constants.CORNER_UP_NORTH_WEST);
				break;
			case OUTSIDE_LEFT:
				list.add(Constants.EDGE_DOWN_EAST);
				list.add(Constants.CORNER_DOWN_SOUTH_WEST);
				list.add(Constants.CORNER_UP_SOUTH_EAST);
				break;
			case OUTSIDE_HORIZONTAL_LEFT:
				list.add(Constants.SLAB_DOWN);
				list.add(Constants.CORNER_UP_SOUTH_EAST);
				break;
			case OUTSIDE_VERTICAL_LEFT:
				list.add(Constants.SLAB_EAST);
				list.add(Constants.CORNER_DOWN_SOUTH_WEST);
				break;
			default:
				list.add(Constants.FULL_BLOCK);
				break;
			}
		}
		break;
		case UP_EAST:
		{
			switch (state.getValue(SHAPE))
			{
			case STRAIGHT:
				list.add(Constants.SLAB_DOWN);
				list.add(Constants.EDGE_UP_WEST);
				break;
			case INSIDE_RIGHT:
				list.add(Constants.SLAB_DOWN);
				list.add(Constants.EDGE_UP_WEST);
				list.add(Constants.CORNER_UP_NORTH_EAST);
				break;
			case OUTSIDE_RIGHT:
				list.add(Constants.EDGE_DOWN_WEST);
				list.add(Constants.CORNER_DOWN_SOUTH_EAST);
				list.add(Constants.CORNER_UP_SOUTH_WEST);
				break;
			case OUTSIDE_HORIZONTAL_RIGHT:
				list.add(Constants.SLAB_DOWN);
				list.add(Constants.CORNER_UP_SOUTH_WEST);
				break;
			case OUTSIDE_VERTICAL_RIGHT:
				list.add(Constants.SLAB_WEST);
				list.add(Constants.CORNER_DOWN_SOUTH_EAST);
				break;
			case INSIDE_LEFT:
				list.add(Constants.SLAB_DOWN);
				list.add(Constants.EDGE_UP_WEST);
				list.add(Constants.CORNER_UP_SOUTH_EAST);
				break;
			case OUTSIDE_LEFT:
				list.add(Constants.EDGE_DOWN_WEST);
				list.add(Constants.CORNER_DOWN_NORTH_EAST);
				list.add(Constants.CORNER_UP_NORTH_WEST);
				break;
			case OUTSIDE_HORIZONTAL_LEFT:
				list.add(Constants.SLAB_DOWN);
				list.add(Constants.CORNER_UP_NORTH_WEST);
				break;
			case OUTSIDE_VERTICAL_LEFT:
				list.add(Constants.SLAB_WEST);
				list.add(Constants.CORNER_DOWN_NORTH_EAST);
				break;
			default:
				list.add(Constants.FULL_BLOCK);
				break;
			}
		}
		break;
		case UP_NORTH:
		{
			switch (state.getValue(SHAPE))
			{
			case STRAIGHT:
				list.add(Constants.SLAB_DOWN);
				list.add(Constants.EDGE_UP_SOUTH);
				break;
			case INSIDE_RIGHT:
				list.add(Constants.SLAB_DOWN);
				list.add(Constants.EDGE_UP_SOUTH);
				list.add(Constants.CORNER_UP_NORTH_WEST);
				break;
			case OUTSIDE_RIGHT:
				list.add(Constants.EDGE_DOWN_SOUTH);
				list.add(Constants.CORNER_DOWN_NORTH_EAST);
				list.add(Constants.CORNER_UP_SOUTH_EAST);
				break;
			case OUTSIDE_HORIZONTAL_RIGHT:
				list.add(Constants.SLAB_DOWN);
				list.add(Constants.CORNER_UP_SOUTH_EAST);
				break;
			case OUTSIDE_VERTICAL_RIGHT:
				list.add(Constants.SLAB_SOUTH);
				list.add(Constants.CORNER_DOWN_NORTH_EAST);
				break;
			case INSIDE_LEFT:
				list.add(Constants.SLAB_DOWN);
				list.add(Constants.EDGE_UP_SOUTH);
				list.add(Constants.CORNER_UP_NORTH_EAST);
				break;
			case OUTSIDE_LEFT:
				list.add(Constants.EDGE_DOWN_SOUTH);
				list.add(Constants.CORNER_DOWN_NORTH_WEST);
				list.add(Constants.CORNER_UP_SOUTH_WEST);
				break;
			case OUTSIDE_HORIZONTAL_LEFT:
				list.add(Constants.SLAB_DOWN);
				list.add(Constants.CORNER_UP_SOUTH_WEST);
				break;
			case OUTSIDE_VERTICAL_LEFT:
				list.add(Constants.SLAB_SOUTH);
				list.add(Constants.CORNER_DOWN_NORTH_WEST);
				break;
			default:
				list.add(Constants.FULL_BLOCK);
				break;
			}
		}
		break;
		case UP_SOUTH:
		{
			switch (state.getValue(SHAPE))
			{
			case STRAIGHT:
				list.add(Constants.SLAB_DOWN);
				list.add(Constants.EDGE_UP_NORTH);
				break;
			case INSIDE_RIGHT:
				list.add(Constants.SLAB_DOWN);
				list.add(Constants.EDGE_UP_NORTH);
				list.add(Constants.CORNER_UP_SOUTH_EAST);
				break;
			case OUTSIDE_RIGHT:
				list.add(Constants.EDGE_DOWN_NORTH);
				list.add(Constants.CORNER_DOWN_SOUTH_WEST);
				list.add(Constants.CORNER_UP_NORTH_WEST);
				break;
			case OUTSIDE_HORIZONTAL_RIGHT:
				list.add(Constants.SLAB_DOWN);
				list.add(Constants.CORNER_UP_NORTH_WEST);
				break;
			case OUTSIDE_VERTICAL_RIGHT:
				list.add(Constants.SLAB_NORTH);
				list.add(Constants.CORNER_DOWN_SOUTH_WEST);
				break;
			case INSIDE_LEFT:
				list.add(Constants.SLAB_DOWN);
				list.add(Constants.EDGE_UP_NORTH);
				list.add(Constants.CORNER_UP_SOUTH_WEST);
				break;
			case OUTSIDE_LEFT:
				list.add(Constants.EDGE_DOWN_NORTH);
				list.add(Constants.CORNER_DOWN_SOUTH_EAST);
				list.add(Constants.CORNER_UP_NORTH_EAST);
				break;
			case OUTSIDE_HORIZONTAL_LEFT:
				list.add(Constants.SLAB_DOWN);
				list.add(Constants.CORNER_UP_NORTH_EAST);
				break;
			case OUTSIDE_VERTICAL_LEFT:
				list.add(Constants.SLAB_NORTH);
				list.add(Constants.CORNER_DOWN_SOUTH_EAST);
				break;
			default:
				list.add(Constants.FULL_BLOCK);
				break;
			}
		}
		break;
		case DOWN_WEST:
		{
			switch (state.getValue(SHAPE))
			{
			case STRAIGHT:
				list.add(Constants.SLAB_UP);
				list.add(Constants.EDGE_DOWN_EAST);
				break;
			case INSIDE_RIGHT:
				list.add(Constants.SLAB_UP);
				list.add(Constants.EDGE_DOWN_EAST);
				list.add(Constants.CORNER_DOWN_NORTH_WEST);
				break;
			case OUTSIDE_RIGHT:
				list.add(Constants.EDGE_UP_EAST);
				list.add(Constants.CORNER_UP_SOUTH_WEST);
				list.add(Constants.CORNER_DOWN_SOUTH_EAST);
				break;
			case OUTSIDE_HORIZONTAL_RIGHT:
				list.add(Constants.SLAB_UP);
				list.add(Constants.CORNER_DOWN_SOUTH_EAST);
				break;
			case OUTSIDE_VERTICAL_RIGHT:
				list.add(Constants.SLAB_EAST);
				list.add(Constants.CORNER_UP_SOUTH_WEST);
				break;
			case INSIDE_LEFT:
				list.add(Constants.SLAB_UP);
				list.add(Constants.EDGE_DOWN_EAST);
				list.add(Constants.CORNER_DOWN_SOUTH_WEST);
				break;
			case OUTSIDE_LEFT:
				list.add(Constants.EDGE_UP_EAST);
				list.add(Constants.CORNER_UP_NORTH_WEST);
				list.add(Constants.CORNER_DOWN_NORTH_EAST);
				break;
			case OUTSIDE_HORIZONTAL_LEFT:
				list.add(Constants.SLAB_UP);
				list.add(Constants.CORNER_DOWN_NORTH_EAST);
				break;
			case OUTSIDE_VERTICAL_LEFT:
				list.add(Constants.SLAB_EAST);
				list.add(Constants.CORNER_UP_NORTH_WEST);
				break;
			default:
				list.add(Constants.FULL_BLOCK);
				break;
			}
		}
		break;
		case DOWN_EAST:
		{
			switch (state.getValue(SHAPE))
			{
			case STRAIGHT:
				list.add(Constants.SLAB_UP);
				list.add(Constants.EDGE_DOWN_WEST);
				break;
			case INSIDE_RIGHT:
				list.add(Constants.SLAB_UP);
				list.add(Constants.EDGE_DOWN_WEST);
				list.add(Constants.CORNER_DOWN_SOUTH_EAST);
				break;
			case OUTSIDE_RIGHT:
				list.add(Constants.EDGE_UP_WEST);
				list.add(Constants.CORNER_UP_NORTH_EAST);
				list.add(Constants.CORNER_DOWN_NORTH_WEST);
				break;
			case OUTSIDE_HORIZONTAL_RIGHT:
				list.add(Constants.SLAB_UP);
				list.add(Constants.CORNER_DOWN_NORTH_WEST);
				break;
			case OUTSIDE_VERTICAL_RIGHT:
				list.add(Constants.SLAB_WEST);
				list.add(Constants.CORNER_UP_NORTH_EAST);
				break;
			case INSIDE_LEFT:
				list.add(Constants.SLAB_UP);
				list.add(Constants.EDGE_DOWN_WEST);
				list.add(Constants.CORNER_DOWN_NORTH_EAST);
				break;
			case OUTSIDE_LEFT:
				list.add(Constants.EDGE_UP_WEST);
				list.add(Constants.CORNER_UP_SOUTH_EAST);
				list.add(Constants.CORNER_DOWN_SOUTH_WEST);
				break;
			case OUTSIDE_HORIZONTAL_LEFT:
				list.add(Constants.SLAB_UP);
				list.add(Constants.CORNER_DOWN_SOUTH_WEST);
				break;
			case OUTSIDE_VERTICAL_LEFT:
				list.add(Constants.SLAB_EAST);
				list.add(Constants.CORNER_UP_SOUTH_EAST);
				break;
			default:
				list.add(Constants.FULL_BLOCK);
				break;
			}
		}
		break;
		case DOWN_NORTH:
		{
			switch (state.getValue(SHAPE))
			{
			case STRAIGHT:
				list.add(Constants.SLAB_UP);
				list.add(Constants.EDGE_DOWN_SOUTH);
				break;
			case INSIDE_RIGHT:
				list.add(Constants.SLAB_UP);
				list.add(Constants.EDGE_DOWN_SOUTH);
				list.add(Constants.CORNER_DOWN_NORTH_EAST);
				break;
			case OUTSIDE_RIGHT:
				list.add(Constants.EDGE_UP_WEST);
				list.add(Constants.CORNER_UP_SOUTH_EAST);
				list.add(Constants.CORNER_DOWN_SOUTH_WEST);
				break;
			case OUTSIDE_HORIZONTAL_RIGHT:
				list.add(Constants.SLAB_UP);
				list.add(Constants.CORNER_DOWN_SOUTH_WEST);
				break;
			case OUTSIDE_VERTICAL_RIGHT:
				list.add(Constants.SLAB_SOUTH);
				list.add(Constants.CORNER_UP_NORTH_WEST);
				break;
			case INSIDE_LEFT:
				list.add(Constants.SLAB_UP);
				list.add(Constants.EDGE_DOWN_SOUTH);
				list.add(Constants.CORNER_DOWN_NORTH_WEST);
				break;
			case OUTSIDE_LEFT:
				list.add(Constants.EDGE_UP_EAST);
				list.add(Constants.CORNER_UP_SOUTH_WEST);
				list.add(Constants.CORNER_DOWN_SOUTH_EAST);
				break;
			case OUTSIDE_HORIZONTAL_LEFT:
				list.add(Constants.SLAB_UP);
				list.add(Constants.CORNER_DOWN_SOUTH_EAST);
				break;
			case OUTSIDE_VERTICAL_LEFT:
				list.add(Constants.SLAB_SOUTH);
				list.add(Constants.CORNER_UP_NORTH_EAST);
				break;
			default:
				list.add(Constants.FULL_BLOCK);
				break;
			}
		}
		break;
		case DOWN_SOUTH:
		{
			switch (state.getValue(SHAPE))
			{
			case STRAIGHT:
				list.add(Constants.SLAB_UP);
				list.add(Constants.EDGE_DOWN_NORTH);
				break;
			case INSIDE_RIGHT:
				list.add(Constants.SLAB_UP);
				list.add(Constants.EDGE_DOWN_NORTH);
				list.add(Constants.CORNER_DOWN_SOUTH_WEST);
				break;
			case OUTSIDE_RIGHT:
				list.add(Constants.EDGE_UP_EAST);
				list.add(Constants.CORNER_UP_NORTH_WEST);
				list.add(Constants.CORNER_DOWN_NORTH_EAST);
				break;
			case OUTSIDE_HORIZONTAL_RIGHT:
				list.add(Constants.SLAB_UP);
				list.add(Constants.CORNER_DOWN_NORTH_EAST);
				break;
			case OUTSIDE_VERTICAL_RIGHT:
				list.add(Constants.SLAB_NORTH);
				list.add(Constants.CORNER_UP_SOUTH_EAST);
				break;
			case INSIDE_LEFT:
				list.add(Constants.SLAB_UP);
				list.add(Constants.EDGE_DOWN_NORTH);
				list.add(Constants.CORNER_DOWN_SOUTH_EAST);
				break;
			case OUTSIDE_LEFT:
				list.add(Constants.EDGE_UP_WEST);
				list.add(Constants.CORNER_UP_NORTH_EAST);
				list.add(Constants.CORNER_DOWN_NORTH_WEST);
				break;
			case OUTSIDE_HORIZONTAL_LEFT:
				list.add(Constants.SLAB_UP);
				list.add(Constants.CORNER_DOWN_NORTH_WEST);
				break;
			case OUTSIDE_VERTICAL_LEFT:
				list.add(Constants.SLAB_NORTH);
				list.add(Constants.CORNER_UP_SOUTH_WEST);
				break;
			default:
				list.add(Constants.FULL_BLOCK);
				break;
			}
		}
		break;
		case NORTH_WEST:
		{
			switch (state.getValue(SHAPE))
			{
			case STRAIGHT:
				list.add(Constants.SLAB_EAST);
				list.add(Constants.EDGE_SOUTH_WEST);
				break;
			case INSIDE_RIGHT:
				list.add(Constants.SLAB_EAST);
				list.add(Constants.EDGE_SOUTH_WEST);
				list.add(Constants.CORNER_UP_NORTH_WEST);
				break;
			case OUTSIDE_RIGHT:
				list.add(Constants.EDGE_DOWN_EAST);
				list.add(Constants.CORNER_UP_SOUTH_EAST);
				list.add(Constants.CORNER_DOWN_SOUTH_WEST);
				break;
			case OUTSIDE_HORIZONTAL_RIGHT:
				list.add(Constants.SLAB_SOUTH);
				list.add(Constants.CORNER_DOWN_NORTH_EAST);
				break;
			case OUTSIDE_VERTICAL_RIGHT:
				list.add(Constants.SLAB_EAST);
				list.add(Constants.CORNER_DOWN_SOUTH_WEST);
				break;
			case INSIDE_LEFT:
				list.add(Constants.SLAB_EAST);
				list.add(Constants.EDGE_SOUTH_WEST);
				list.add(Constants.CORNER_DOWN_NORTH_WEST);
				break;
			case OUTSIDE_LEFT:
				list.add(Constants.EDGE_UP_EAST);
				list.add(Constants.CORNER_DOWN_SOUTH_EAST);
				list.add(Constants.CORNER_UP_SOUTH_WEST);
				break;
			case OUTSIDE_HORIZONTAL_LEFT:
				list.add(Constants.SLAB_SOUTH);
				list.add(Constants.CORNER_UP_NORTH_EAST);
				break;
			case OUTSIDE_VERTICAL_LEFT:
				list.add(Constants.SLAB_EAST);
				list.add(Constants.CORNER_UP_SOUTH_WEST);
				break;
			default:
				list.add(Constants.FULL_BLOCK);
				break;
			}
		}
		break;
		case NORTH_EAST:
		{
			switch (state.getValue(SHAPE))
			{
			case STRAIGHT:
				list.add(Constants.SLAB_WEST);
				list.add(Constants.EDGE_SOUTH_EAST);
				break;
			case INSIDE_RIGHT:
				list.add(Constants.SLAB_WEST);
				list.add(Constants.EDGE_SOUTH_EAST);
				list.add(Constants.CORNER_UP_NORTH_EAST);
				break;
			case OUTSIDE_RIGHT:
				list.add(Constants.EDGE_DOWN_WEST);
				list.add(Constants.CORNER_UP_SOUTH_WEST);
				list.add(Constants.CORNER_DOWN_SOUTH_EAST);
				break;
			case OUTSIDE_HORIZONTAL_RIGHT:
				list.add(Constants.SLAB_WEST);
				list.add(Constants.CORNER_DOWN_SOUTH_EAST);
				break;
			case OUTSIDE_VERTICAL_RIGHT:
				list.add(Constants.SLAB_SOUTH);
				list.add(Constants.CORNER_DOWN_NORTH_WEST);
				break;
			case INSIDE_LEFT:
				list.add(Constants.SLAB_WEST);
				list.add(Constants.EDGE_SOUTH_EAST);
				list.add(Constants.CORNER_DOWN_NORTH_EAST);
				break;
			case OUTSIDE_LEFT:
				list.add(Constants.EDGE_UP_WEST);
				list.add(Constants.CORNER_DOWN_SOUTH_WEST);
				list.add(Constants.CORNER_UP_SOUTH_EAST);
				break;
			case OUTSIDE_HORIZONTAL_LEFT:
				list.add(Constants.SLAB_WEST);
				list.add(Constants.CORNER_UP_SOUTH_EAST);
				break;
			case OUTSIDE_VERTICAL_LEFT:
				list.add(Constants.SLAB_SOUTH);
				list.add(Constants.CORNER_UP_NORTH_WEST);
				break;
			default:
				list.add(Constants.FULL_BLOCK);
				break;
			}
		}
		break;
		case SOUTH_WEST:
		{
			switch (state.getValue(SHAPE))
			{
			case STRAIGHT:
				list.add(Constants.SLAB_EAST);
				list.add(Constants.EDGE_NORTH_WEST);
				break;
			case INSIDE_RIGHT:
				list.add(Constants.SLAB_EAST);
				list.add(Constants.EDGE_NORTH_WEST);
				list.add(Constants.CORNER_UP_SOUTH_WEST);
				break;
			case OUTSIDE_RIGHT:
				list.add(Constants.EDGE_DOWN_EAST);
				list.add(Constants.CORNER_UP_NORTH_EAST);
				list.add(Constants.CORNER_DOWN_NORTH_WEST);
				break;
			case OUTSIDE_HORIZONTAL_RIGHT:
				list.add(Constants.SLAB_EAST);
				list.add(Constants.CORNER_DOWN_NORTH_WEST);
				break;
			case OUTSIDE_VERTICAL_RIGHT:
				list.add(Constants.SLAB_NORTH);
				list.add(Constants.CORNER_DOWN_SOUTH_EAST);
				break;
			case INSIDE_LEFT:
				list.add(Constants.SLAB_EAST);
				list.add(Constants.EDGE_NORTH_WEST);
				list.add(Constants.CORNER_DOWN_SOUTH_WEST);
				break;
			case OUTSIDE_LEFT:
				list.add(Constants.EDGE_UP_EAST);
				list.add(Constants.CORNER_DOWN_NORTH_EAST);
				list.add(Constants.CORNER_UP_NORTH_WEST);
				break;
			case OUTSIDE_HORIZONTAL_LEFT:
				list.add(Constants.SLAB_EAST);
				list.add(Constants.CORNER_UP_NORTH_WEST);
				break;
			case OUTSIDE_VERTICAL_LEFT:
				list.add(Constants.SLAB_NORTH);
				list.add(Constants.CORNER_UP_SOUTH_EAST);
				break;
			default:
				list.add(Constants.FULL_BLOCK);
				break;
			}
		}
		break;
		case SOUTH_EAST:
		{
			switch (state.getValue(SHAPE))
			{
			case STRAIGHT:
				list.add(Constants.SLAB_WEST);
				list.add(Constants.EDGE_NORTH_EAST);
				break;
			case INSIDE_RIGHT:
				list.add(Constants.SLAB_WEST);
				list.add(Constants.EDGE_NORTH_EAST);
				list.add(Constants.CORNER_UP_SOUTH_EAST);
				break;
			case OUTSIDE_RIGHT:
				list.add(Constants.EDGE_DOWN_WEST);
				list.add(Constants.CORNER_UP_NORTH_WEST);
				list.add(Constants.CORNER_DOWN_NORTH_EAST);
				break;
			case OUTSIDE_HORIZONTAL_RIGHT:
				list.add(Constants.SLAB_NORTH);
				list.add(Constants.CORNER_DOWN_SOUTH_WEST);
				break;
			case OUTSIDE_VERTICAL_RIGHT:
				list.add(Constants.SLAB_WEST);
				list.add(Constants.CORNER_DOWN_NORTH_EAST);
				break;
			case INSIDE_LEFT:
				list.add(Constants.SLAB_WEST);
				list.add(Constants.EDGE_NORTH_EAST);
				list.add(Constants.CORNER_DOWN_SOUTH_EAST);
				break;
			case OUTSIDE_LEFT:
				list.add(Constants.EDGE_UP_WEST);
				list.add(Constants.CORNER_DOWN_NORTH_WEST);
				list.add(Constants.CORNER_UP_NORTH_EAST);
				break;
			case OUTSIDE_HORIZONTAL_LEFT:
				list.add(Constants.SLAB_NORTH);
				list.add(Constants.CORNER_UP_SOUTH_WEST);
				break;
			case OUTSIDE_VERTICAL_LEFT:
				list.add(Constants.SLAB_WEST);
				list.add(Constants.CORNER_UP_NORTH_EAST);
				break;
			default:
				list.add(Constants.FULL_BLOCK);
				break;
			}
		}
		break;
		default:
			list.add(Constants.FULL_BLOCK);
			break;

        }
        return list;
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
    @Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        state = this.getActualState(state, worldIn, pos);
        if (isSideSolid(state, face)) return modelState.getBlockFaceShape(worldIn, pos, face);
        else return BlockFaceShape.UNDEFINED;
    }

    /**
     * Determines if the block is solid enough on the top side to support other blocks, like redstone components.
     */
    @SuppressWarnings("deprecation")
	@Override
	public boolean isTopSolid(IBlockState state)
    {
    	return isSideSolid(state, EnumFacing.UP) && modelState.isTopSolid();
    }

    @SuppressWarnings("incomplete-switch")
	public boolean isSideSolid(IBlockState state, EnumFacing side)
    {
    	EnumShape shape = state.getValue(SHAPE);
    	if (shape == EnumShape.OUTSIDE_RIGHT || shape == EnumShape.OUTSIDE_LEFT) return false;
        switch (state.getValue(PLACING))
        {
        case UP_WEST:
        {
        	switch (shape)
        	{
        	case STRAIGHT:
        		return (side == EnumFacing.DOWN || side == EnumFacing.EAST);
        	case INSIDE_RIGHT:
        		return (side == EnumFacing.DOWN || side == EnumFacing.EAST || side == EnumFacing.SOUTH);
        	case INSIDE_LEFT:
        		return (side == EnumFacing.DOWN || side == EnumFacing.EAST || side == EnumFacing.NORTH);
        	case OUTSIDE_HORIZONTAL_RIGHT:
        	case OUTSIDE_HORIZONTAL_LEFT:
        		return (side == EnumFacing.DOWN);
        	case OUTSIDE_VERTICAL_RIGHT:
        	case OUTSIDE_VERTICAL_LEFT:
        		return (side == EnumFacing.EAST);
        	}
        }
        break;
        case UP_EAST:
        {
        	switch (shape)
        	{
        	case STRAIGHT:
        		return (side == EnumFacing.DOWN || side == EnumFacing.WEST);
        	case INSIDE_RIGHT:
        		return (side == EnumFacing.DOWN || side == EnumFacing.WEST || side == EnumFacing.NORTH);
        	case INSIDE_LEFT:
        		return (side == EnumFacing.DOWN || side == EnumFacing.WEST || side == EnumFacing.SOUTH);
        	case OUTSIDE_HORIZONTAL_RIGHT:
        	case OUTSIDE_HORIZONTAL_LEFT:
        		return (side == EnumFacing.DOWN);
        	case OUTSIDE_VERTICAL_RIGHT:
        	case OUTSIDE_VERTICAL_LEFT:
        		return (side == EnumFacing.WEST);
        	}
        }
        break;
        case UP_NORTH:
        {
        	switch (shape)
        	{
        	case STRAIGHT:
        		return (side == EnumFacing.DOWN || side == EnumFacing.SOUTH);
        	case INSIDE_RIGHT:
        		return (side == EnumFacing.DOWN || side == EnumFacing.SOUTH || side == EnumFacing.WEST);
        	case INSIDE_LEFT:
        		return (side == EnumFacing.DOWN || side == EnumFacing.SOUTH || side == EnumFacing.EAST);
        	case OUTSIDE_HORIZONTAL_RIGHT:
        	case OUTSIDE_HORIZONTAL_LEFT:
        		return (side == EnumFacing.DOWN);
        	case OUTSIDE_VERTICAL_RIGHT:
        	case OUTSIDE_VERTICAL_LEFT:
        		return (side == EnumFacing.SOUTH);
        	}
        }
        break;
        case UP_SOUTH:
        {
        	switch (shape)
        	{
        	case STRAIGHT:
        		return (side == EnumFacing.DOWN || side == EnumFacing.NORTH);
        	case INSIDE_RIGHT:
        		return (side == EnumFacing.DOWN || side == EnumFacing.NORTH || side == EnumFacing.EAST);
        	case INSIDE_LEFT:
        		return (side == EnumFacing.DOWN || side == EnumFacing.NORTH || side == EnumFacing.WEST);
        	case OUTSIDE_HORIZONTAL_RIGHT:
        	case OUTSIDE_HORIZONTAL_LEFT:
        		return (side == EnumFacing.DOWN);
        	case OUTSIDE_VERTICAL_RIGHT:
        	case OUTSIDE_VERTICAL_LEFT:
        		return (side == EnumFacing.NORTH);
        	}
        }
        break;
        case DOWN_WEST:
        {
        	switch (shape)
        	{
        	case STRAIGHT:
        		return (side == EnumFacing.UP || side == EnumFacing.EAST);
        	case INSIDE_RIGHT:
        		return (side == EnumFacing.UP || side == EnumFacing.EAST || side == EnumFacing.NORTH);
        	case INSIDE_LEFT:
        		return (side == EnumFacing.UP || side == EnumFacing.EAST || side == EnumFacing.SOUTH);
        	case OUTSIDE_HORIZONTAL_RIGHT:
        	case OUTSIDE_HORIZONTAL_LEFT:
        		return (side == EnumFacing.UP);
        	case OUTSIDE_VERTICAL_RIGHT:
        	case OUTSIDE_VERTICAL_LEFT:
        		return (side == EnumFacing.EAST);
        	}
        }
        break;
        case DOWN_EAST:
        {
        	switch (shape)
        	{
        	case STRAIGHT:
        		return (side == EnumFacing.UP || side == EnumFacing.WEST);
        	case INSIDE_RIGHT:
        		return (side == EnumFacing.UP || side == EnumFacing.WEST || side == EnumFacing.SOUTH);
        	case INSIDE_LEFT:
        		return (side == EnumFacing.UP || side == EnumFacing.WEST || side == EnumFacing.NORTH);
        	case OUTSIDE_HORIZONTAL_RIGHT:
        	case OUTSIDE_HORIZONTAL_LEFT:
        		return (side == EnumFacing.UP);
        	case OUTSIDE_VERTICAL_RIGHT:
        	case OUTSIDE_VERTICAL_LEFT:
        		return (side == EnumFacing.WEST);
        	}
        }
        break;
        case DOWN_NORTH:
        {
        	switch (shape)
        	{
        	case STRAIGHT:
        		return (side == EnumFacing.UP || side == EnumFacing.SOUTH);
        	case INSIDE_RIGHT:
        		return (side == EnumFacing.UP || side == EnumFacing.SOUTH || side == EnumFacing.EAST);
        	case INSIDE_LEFT:
        		return (side == EnumFacing.UP || side == EnumFacing.SOUTH || side == EnumFacing.WEST);
        	case OUTSIDE_HORIZONTAL_RIGHT:
        	case OUTSIDE_HORIZONTAL_LEFT:
        		return (side == EnumFacing.UP);
        	case OUTSIDE_VERTICAL_RIGHT:
        	case OUTSIDE_VERTICAL_LEFT:
        		return (side == EnumFacing.SOUTH);
        	}
        }
        break;
        case DOWN_SOUTH:
        {
        	switch (shape)
        	{
        	case STRAIGHT:
        		return (side == EnumFacing.UP || side == EnumFacing.NORTH);
        	case INSIDE_RIGHT:
        		return (side == EnumFacing.UP || side == EnumFacing.NORTH || side == EnumFacing.WEST);
        	case INSIDE_LEFT:
        		return (side == EnumFacing.UP || side == EnumFacing.NORTH || side == EnumFacing.EAST);
        	case OUTSIDE_HORIZONTAL_RIGHT:
        	case OUTSIDE_HORIZONTAL_LEFT:
        		return (side == EnumFacing.UP);
        	case OUTSIDE_VERTICAL_RIGHT:
        	case OUTSIDE_VERTICAL_LEFT:
        		return (side == EnumFacing.NORTH);
        	}
        }
        break;
        case NORTH_WEST:
        {
        	switch (shape)
        	{
        	case STRAIGHT:
        		return (side == EnumFacing.SOUTH || side == EnumFacing.EAST);
        	case INSIDE_RIGHT:
        		return (side == EnumFacing.SOUTH || side == EnumFacing.EAST || side == EnumFacing.UP);
        	case INSIDE_LEFT:
        		return (side == EnumFacing.SOUTH || side == EnumFacing.EAST || side == EnumFacing.DOWN);
        	case OUTSIDE_HORIZONTAL_RIGHT:
        	case OUTSIDE_HORIZONTAL_LEFT:
        		return (side == EnumFacing.SOUTH);
        	case OUTSIDE_VERTICAL_RIGHT:
        	case OUTSIDE_VERTICAL_LEFT:
        		return (side == EnumFacing.EAST);
        	}
        }
        break;
        case NORTH_EAST:
        {
        	switch (shape)
        	{
        	case STRAIGHT:
        		return (side == EnumFacing.SOUTH || side == EnumFacing.WEST);
        	case INSIDE_RIGHT:
        		return (side == EnumFacing.SOUTH || side == EnumFacing.WEST || side == EnumFacing.UP);
        	case INSIDE_LEFT:
        		return (side == EnumFacing.SOUTH || side == EnumFacing.WEST || side == EnumFacing.DOWN);
        	case OUTSIDE_HORIZONTAL_RIGHT:
        	case OUTSIDE_HORIZONTAL_LEFT:
        		return (side == EnumFacing.WEST);
        	case OUTSIDE_VERTICAL_RIGHT:
        	case OUTSIDE_VERTICAL_LEFT:
        		return (side == EnumFacing.SOUTH);
        	}
        }
        break;
        case SOUTH_WEST:
        {
        	switch (shape)
        	{
        	case STRAIGHT:
        		return (side == EnumFacing.NORTH || side == EnumFacing.EAST);
        	case INSIDE_RIGHT:
        		return (side == EnumFacing.NORTH || side == EnumFacing.EAST || side == EnumFacing.UP);
        	case INSIDE_LEFT:
        		return (side == EnumFacing.NORTH || side == EnumFacing.EAST || side == EnumFacing.DOWN);
        	case OUTSIDE_HORIZONTAL_RIGHT:
        	case OUTSIDE_HORIZONTAL_LEFT:
        		return (side == EnumFacing.EAST);
        	case OUTSIDE_VERTICAL_RIGHT:
        	case OUTSIDE_VERTICAL_LEFT:
        		return (side == EnumFacing.NORTH);
        	}
        }
        break;
        case SOUTH_EAST:
        {
        	switch (shape)
        	{
        	case STRAIGHT:
        		return (side == EnumFacing.NORTH || side == EnumFacing.WEST);
        	case INSIDE_RIGHT:
        		return (side == EnumFacing.NORTH || side == EnumFacing.WEST || side == EnumFacing.UP);
        	case INSIDE_LEFT:
        		return (side == EnumFacing.NORTH || side == EnumFacing.WEST || side == EnumFacing.DOWN);
        	case OUTSIDE_HORIZONTAL_RIGHT:
        	case OUTSIDE_HORIZONTAL_LEFT:
        		return (side == EnumFacing.NORTH);
        	case OUTSIDE_VERTICAL_RIGHT:
        	case OUTSIDE_VERTICAL_LEFT:
        		return (side == EnumFacing.WEST);
        	}
        }
        break;
        }
        return false;
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    @Override
	public IBlockState withRotation(IBlockState state, Rotation rot)
    {
    	if (rot == Rotation.NONE) return state;
    	switch (state.getValue(PLACING))
    	{
		case UP_WEST:
			switch (rot)
			{
			case CLOCKWISE_180:
				return state.withProperty(PLACING, EnumPlacing.UP_EAST);
			case CLOCKWISE_90:
				return state.withProperty(PLACING, EnumPlacing.UP_NORTH);
			case COUNTERCLOCKWISE_90:
				return state.withProperty(PLACING, EnumPlacing.UP_SOUTH);
			default:
		    	return state;
			}
		case UP_EAST:
			switch (rot)
			{
			case CLOCKWISE_180:
				return state.withProperty(PLACING, EnumPlacing.UP_WEST);
			case CLOCKWISE_90:
				return state.withProperty(PLACING, EnumPlacing.UP_SOUTH);
			case COUNTERCLOCKWISE_90:
				return state.withProperty(PLACING, EnumPlacing.UP_NORTH);
			default:
		    	return state;
			}
		case UP_NORTH:
			switch (rot)
			{
			case CLOCKWISE_180:
				return state.withProperty(PLACING, EnumPlacing.UP_SOUTH);
			case CLOCKWISE_90:
				return state.withProperty(PLACING, EnumPlacing.UP_EAST);
			case COUNTERCLOCKWISE_90:
				return state.withProperty(PLACING, EnumPlacing.UP_WEST);
			default:
		    	return state;
			}
		case UP_SOUTH:
			switch (rot)
			{
			case CLOCKWISE_180:
				return state.withProperty(PLACING, EnumPlacing.UP_NORTH);
			case CLOCKWISE_90:
				return state.withProperty(PLACING, EnumPlacing.UP_WEST);
			case COUNTERCLOCKWISE_90:
				return state.withProperty(PLACING, EnumPlacing.UP_EAST);
			default:
		    	return state;
			}
		case DOWN_WEST:
			switch (rot)
			{
			case CLOCKWISE_180:
				return state.withProperty(PLACING, EnumPlacing.DOWN_EAST);
			case CLOCKWISE_90:
				return state.withProperty(PLACING, EnumPlacing.DOWN_NORTH);
			case COUNTERCLOCKWISE_90:
				return state.withProperty(PLACING, EnumPlacing.DOWN_SOUTH);
			default:
				return state;
			}
		case DOWN_EAST:
			switch (rot)
			{
			case CLOCKWISE_180:
				return state.withProperty(PLACING, EnumPlacing.DOWN_WEST);
			case CLOCKWISE_90:
				return state.withProperty(PLACING, EnumPlacing.DOWN_SOUTH);
			case COUNTERCLOCKWISE_90:
				return state.withProperty(PLACING, EnumPlacing.DOWN_NORTH);
			default:
				return state;
			}
		case DOWN_NORTH:
			switch (rot)
			{
			case CLOCKWISE_180:
				return state.withProperty(PLACING, EnumPlacing.DOWN_SOUTH);
			case CLOCKWISE_90:
				return state.withProperty(PLACING, EnumPlacing.DOWN_EAST);
			case COUNTERCLOCKWISE_90:
				return state.withProperty(PLACING, EnumPlacing.DOWN_WEST);
			default:
				return state;
			}
		case DOWN_SOUTH:
			switch (rot)
			{
			case CLOCKWISE_180:
				return state.withProperty(PLACING, EnumPlacing.DOWN_NORTH);
			case CLOCKWISE_90:
				return state.withProperty(PLACING, EnumPlacing.DOWN_WEST);
			case COUNTERCLOCKWISE_90:
				return state.withProperty(PLACING, EnumPlacing.DOWN_EAST);
			default:
				return state;
			}
		case NORTH_WEST:
			switch (rot)
			{
			case CLOCKWISE_180:
				return state.withProperty(PLACING, EnumPlacing.SOUTH_EAST);
			case CLOCKWISE_90:
				return state.withProperty(PLACING, EnumPlacing.NORTH_EAST);
			case COUNTERCLOCKWISE_90:
				return state.withProperty(PLACING, EnumPlacing.SOUTH_WEST);
			default:
				return state;
			}
		case NORTH_EAST:
			switch (rot)
			{
			case CLOCKWISE_180:
				return state.withProperty(PLACING, EnumPlacing.SOUTH_WEST);
			case CLOCKWISE_90:
				return state.withProperty(PLACING, EnumPlacing.SOUTH_EAST);
			case COUNTERCLOCKWISE_90:
				return state.withProperty(PLACING, EnumPlacing.NORTH_WEST);
			default:
				return state;
			}
		case SOUTH_WEST:
			switch (rot)
			{
			case CLOCKWISE_180:
				return state.withProperty(PLACING, EnumPlacing.NORTH_EAST);
			case CLOCKWISE_90:
				return state.withProperty(PLACING, EnumPlacing.NORTH_WEST);
			case COUNTERCLOCKWISE_90:
				return state.withProperty(PLACING, EnumPlacing.SOUTH_EAST);
			default:
				return state;
			}
		case SOUTH_EAST:
			switch (rot)
			{
			case CLOCKWISE_180:
				return state.withProperty(PLACING, EnumPlacing.NORTH_WEST);
			case CLOCKWISE_90:
				return state.withProperty(PLACING, EnumPlacing.SOUTH_WEST);
			case COUNTERCLOCKWISE_90:
				return state.withProperty(PLACING, EnumPlacing.NORTH_EAST);
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
        	case UP_WEST:
        		return state.withProperty(PLACING, EnumPlacing.UP_EAST);
    		case UP_EAST:
        		return state.withProperty(PLACING, EnumPlacing.UP_WEST);
    		case DOWN_WEST:
        		return state.withProperty(PLACING, EnumPlacing.DOWN_EAST);
    		case DOWN_EAST:
        		return state.withProperty(PLACING, EnumPlacing.DOWN_WEST);
    		case NORTH_WEST:
        		return state.withProperty(PLACING, EnumPlacing.NORTH_EAST);
    		case NORTH_EAST:
        		return state.withProperty(PLACING, EnumPlacing.NORTH_WEST);
    		case SOUTH_WEST:
        		return state.withProperty(PLACING, EnumPlacing.SOUTH_EAST);
    		case SOUTH_EAST:
        		return state.withProperty(PLACING, EnumPlacing.SOUTH_WEST);
    		default:
    	    	return state;
        	}
    	}
    	case FRONT_BACK:
    	{
        	switch (state.getValue(PLACING))
        	{
    		case UP_NORTH:
        		return state.withProperty(PLACING, EnumPlacing.UP_SOUTH);
    		case UP_SOUTH:
        		return state.withProperty(PLACING, EnumPlacing.UP_NORTH);
    		case DOWN_NORTH:
        		return state.withProperty(PLACING, EnumPlacing.DOWN_SOUTH);
    		case DOWN_SOUTH:
        		return state.withProperty(PLACING, EnumPlacing.DOWN_NORTH);
    		case NORTH_WEST:
        		return state.withProperty(PLACING, EnumPlacing.SOUTH_WEST);
    		case NORTH_EAST:
        		return state.withProperty(PLACING, EnumPlacing.SOUTH_EAST);
    		case SOUTH_WEST:
        		return state.withProperty(PLACING, EnumPlacing.NORTH_WEST);
    		case SOUTH_EAST:
        		return state.withProperty(PLACING, EnumPlacing.NORTH_EAST);
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
        state = this.getActualState(state, world, pos);
        return isSideSolid(state, face) && modelState.doesSideBlockRendering(world, pos, face);
    }

    public static enum EnumPlacing implements IStringSerializable
    {
        UP_WEST("up_west"),
        UP_EAST("up_east"),
        UP_NORTH("up_north"),
        UP_SOUTH("up_south"),
        DOWN_WEST("down_west"),
        DOWN_EAST("down_east"),
        DOWN_NORTH("down_north"),
        DOWN_SOUTH("down_south"),
        NORTH_WEST("north_west"),
        NORTH_EAST("north_east"),
        SOUTH_WEST("south_west"),
        SOUTH_EAST("south_east");


        private final String name;

        private EnumPlacing(String name)
        {
            this.name = name;
        }

        @Override
		public String toString()
        {
            return this.name;
        }

        @Override
		public String getName()
        {
            return this.name;
        }
    }

    public static enum EnumShape implements IStringSerializable
    {
    	//right is also up, left is also down
        STRAIGHT("straight"), //normal shape (flat on 2 planes)
        INSIDE_RIGHT("inside_right"), //flat on all planes
        OUTSIDE_RIGHT("outside_right"), //no flat
        OUTSIDE_HORIZONTAL_RIGHT("outside_horizontal_right"), //flat on XZ plane or flat on right plane
        OUTSIDE_VERTICAL_RIGHT("outside_vertical_right"), //other flat
        INSIDE_LEFT("inside_left"), //flat on all planes
        OUTSIDE_LEFT("outside_left"), //no flat
        OUTSIDE_HORIZONTAL_LEFT("outside_horizontal_left"), //flat on XZ plane or flat on right plane
        OUTSIDE_VERTICAL_LEFT("outside_vertical_left"); //other flat

        private final String name;

        private EnumShape(String name)
        {
            this.name = name;
        }

        @Override
		public String toString()
        {
            return this.name;
        }

        @Override
		public String getName()
        {
            return this.name;
        }
    }

	@Override
	@SideOnly(Side.CLIENT)
	public IBlockHighlight getBlockHighlight(EntityPlayer player, RayTraceResult trace)
	{
		return IBlockHighlight.STAIRS;
	}
}