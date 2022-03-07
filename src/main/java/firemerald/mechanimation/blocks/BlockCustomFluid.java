package firemerald.mechanimation.blocks;

import firemerald.api.core.blocks.ICustomStateMapper;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCustomFluid extends BlockFluidClassic implements ICustomStateMapper
{
	public BlockCustomFluid(Fluid fluid, Material material, MapColor mapColor)
	{
		super(fluid, material, mapColor);
		disableStats();
	}
	public BlockCustomFluid(Fluid fluid, Material material)
	{
		super(fluid, material);
		disableStats();
	}

	@SuppressWarnings("deprecation")
	@Override
	public PathNodeType getAiPathNodeType(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving entity)
	{
		return this.getMaterial(state) == Material.LAVA ? PathNodeType.LAVA : PathNodeType.WATER;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IStateMapper getStateMapper()
	{
		return new StateMap.Builder().ignore(LEVEL).build();
	}
}