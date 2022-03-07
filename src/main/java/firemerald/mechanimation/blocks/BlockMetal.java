package firemerald.mechanimation.blocks;

import javax.annotation.Nullable;

import firemerald.api.core.items.IItemSubtyped;
import firemerald.mechanimation.init.MechanimationTabs;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMetal extends Block implements IItemSubtyped //TODO hardness/resistance
{
    public static final PropertyEnum<EnumVariant> VARIANT = PropertyEnum.<EnumVariant>create("variant", EnumVariant.class);

	public BlockMetal()
	{
		super(Material.IRON, MapColor.IRON);
		this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, EnumVariant.NICKEL));
        this.setHarvestLevel(EnumVariant.NICKEL, 2);
        this.setHarvestLevel(EnumVariant.COPPER, 2);
        this.setHarvestLevel(EnumVariant.ALUMINUM, 2);
        this.setHarvestLevel(EnumVariant.TIN, 2);
        this.setHarvestLevel(EnumVariant.SILVER, 2);
        this.setHarvestLevel(EnumVariant.TUNGSTEN, 3);
        this.setHarvestLevel(EnumVariant.TITANIUM, 4);
		setSoundType(SoundType.METAL);
		this.setCreativeTab(MechanimationTabs.BLOCKS);
	}

    public void setHarvestLevel(EnumVariant variant, int level)
    {
        this.setHarvestLevel("pickaxe", level, this.getDefaultState().withProperty(VARIANT, variant));
    }

    @Override
	public int damageDropped(IBlockState state)
    {
    	//System.out.println(state);
    	return state.getValue(VARIANT).ordinal();
    }

	@Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items)
    {
		for (int i = 0; i < EnumVariant.values().length; i++) items.add(new ItemStack(this, 1, i));
    }

    @Override
	protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, VARIANT);
    }

	@Override
    public IBlockState getStateFromMeta(int meta)
    {
		if (meta < 0 || meta >= EnumVariant.values().length) meta = 0;
		return this.getDefaultState().withProperty(VARIANT, EnumVariant.values()[meta]);
    }

	@Override
    public int getMetaFromState(IBlockState state)
    {
		return state.getValue(VARIANT).ordinal();
    }

	@Override
	public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		return state.getValue(VARIANT).color;
	}

	@Override
	public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos)
	{
		return blockState.getValue(VARIANT).hardness;
	}

	@Override
	public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion)
	{
		return world.getBlockState(pos).getValue(VARIANT).resistance;
	}

	public static enum EnumVariant implements IStringSerializable
	{
		NICKEL("nickel", MapColor.IRON, 5, 10),
		COPPER("copper", MapColor.IRON, 5, 10),
		ALUMINUM("aluminum", MapColor.IRON, 5, 10),
		TIN("tin", MapColor.IRON, 5, 10),
		SILVER("silver", MapColor.IRON, 5, 10),
		TUNGSTEN("tungsten", MapColor.IRON, 5, 10),
		TITANIUM("titanium", MapColor.IRON, 5, 10);

        private final String name;
        private final MapColor color;
        private final float hardness;
        private final float resistance;

        private EnumVariant(String name, MapColor color, float hardness)
        {
        	this(name, color, hardness, hardness * 5);
        }

        private EnumVariant(String name, MapColor color, float hardness, float resistance)
        {
            this.name = name;
            this.color = color;
            this.hardness = hardness;
            this.resistance = resistance;
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
	public String getSubtype(int meta)
	{
		if (meta < 0 || meta >= EnumVariant.values().length) meta = 0;
		return EnumVariant.values()[meta].getName();
	}

	@Override
	public int getMeta(String subtype)
	{
		for (EnumVariant variant : EnumVariant.values()) if (variant.getName().equals(subtype)) return variant.ordinal();
		return -1;
	}
}