package firemerald.mechanimation.blocks;

import firemerald.mechanimation.init.MechanimationTabs;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.util.IStringSerializable;

public class BlockTankTest extends Block
{
    public static final PropertyEnum<EnumEdgeState> EDGE_UP_SOUTH = PropertyEnum.create("edge_up_south", EnumEdgeState.class);
    public static final PropertyEnum<EnumEdgeState> EDGE_UP_NORTH = PropertyEnum.create("edge_up_north", EnumEdgeState.class);
    public static final PropertyEnum<EnumEdgeState> EDGE_UP_EAST = PropertyEnum.create("edge_up_east", EnumEdgeState.class);
    public static final PropertyEnum<EnumEdgeState> EDGE_UP_WEST = PropertyEnum.create("edge_up_west", EnumEdgeState.class);
    public static final PropertyEnum<EnumEdgeState> EDGE_DOWN_SOUTH = PropertyEnum.create("edge_down_south", EnumEdgeState.class);
    public static final PropertyEnum<EnumEdgeState> EDGE_DOWN_NORTH = PropertyEnum.create("edge_down_north", EnumEdgeState.class);
    public static final PropertyEnum<EnumEdgeState> EDGE_DOWN_EAST = PropertyEnum.create("edge_down_east", EnumEdgeState.class);
    public static final PropertyEnum<EnumEdgeState> EDGE_DOWN_WEST = PropertyEnum.create("edge_down_west", EnumEdgeState.class);
    public static final PropertyEnum<EnumEdgeState> EDGE_SOUTH_EAST = PropertyEnum.create("edge_south_east", EnumEdgeState.class);
    public static final PropertyEnum<EnumEdgeState> EDGE_SOUTH_WEST = PropertyEnum.create("edge_south_west", EnumEdgeState.class);
    public static final PropertyEnum<EnumEdgeState> EDGE_NORTH_EAST = PropertyEnum.create("edge_north_east", EnumEdgeState.class);
    public static final PropertyEnum<EnumEdgeState> EDGE_NORTH_WEST = PropertyEnum.create("edge_north_west", EnumEdgeState.class);

	public BlockTankTest()
	{
		super(Material.IRON, MapColor.IRON);
		this.setDefaultState(this.blockState.getBaseState()
				.withProperty(EDGE_UP_SOUTH, EnumEdgeState.FULL)
				.withProperty(EDGE_UP_NORTH, EnumEdgeState.FULL)
				.withProperty(EDGE_UP_EAST, EnumEdgeState.FULL)
				.withProperty(EDGE_UP_WEST, EnumEdgeState.FULL)
				.withProperty(EDGE_DOWN_SOUTH, EnumEdgeState.FULL)
				.withProperty(EDGE_DOWN_NORTH, EnumEdgeState.FULL)
				.withProperty(EDGE_DOWN_EAST, EnumEdgeState.FULL)
				.withProperty(EDGE_DOWN_WEST, EnumEdgeState.FULL)
				.withProperty(EDGE_SOUTH_EAST, EnumEdgeState.FULL)
				.withProperty(EDGE_SOUTH_WEST, EnumEdgeState.FULL)
				.withProperty(EDGE_NORTH_EAST, EnumEdgeState.FULL)
				.withProperty(EDGE_NORTH_WEST, EnumEdgeState.FULL));
		setSoundType(SoundType.METAL);
		this.setCreativeTab(MechanimationTabs.BLOCKS);
	}

    @Override
	protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this,
        		EDGE_UP_SOUTH, EDGE_UP_NORTH, EDGE_UP_EAST, EDGE_UP_WEST,
        		EDGE_DOWN_SOUTH, EDGE_DOWN_NORTH, EDGE_DOWN_EAST, EDGE_DOWN_WEST,
        		EDGE_SOUTH_EAST, EDGE_SOUTH_WEST, EDGE_NORTH_EAST, EDGE_NORTH_WEST);
    }

	public static enum EnumEdgeState implements IStringSerializable
	{
		FULL("full"),
		HORIZONTAL("horizontal"),
		VERTICAL("vertical"),
		OUTSIDE("outside"),
		NONE("silver"),
		HIDDEN("hidden");

        private final String name;

        private EnumEdgeState(String name)
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
}