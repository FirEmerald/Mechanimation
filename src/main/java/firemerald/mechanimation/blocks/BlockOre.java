package firemerald.mechanimation.blocks;

import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import firemerald.api.core.items.IItemSubtyped;
import firemerald.mechanimation.init.MechanimationItems;
import firemerald.mechanimation.init.MechanimationTabs;
import firemerald.mechanimation.items.ItemCraftingMaterial;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockOre extends Block implements IItemSubtyped //TODO hardness/resistance
{
    public static final PropertyEnum<EnumVariant> VARIANT = PropertyEnum.<EnumVariant>create("variant", EnumVariant.class);

    public BlockOre(MapColor mapColor)
    {
        super(Material.ROCK, mapColor);
		this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, EnumVariant.NICKEL));
        setSoundType(SoundType.STONE);
        this.setTickRandomly(true);
        this.setHarvestLevel(EnumVariant.NICKEL, 2);
        this.setHarvestLevel(EnumVariant.COPPER, 1);
        this.setHarvestLevel(EnumVariant.ALUMINUM, 2);
        this.setHarvestLevel(EnumVariant.TIN, 2);
        this.setHarvestLevel(EnumVariant.SILVER, 2);
        this.setHarvestLevel(EnumVariant.TUNGSTEN, 3);
        this.setHarvestLevel(EnumVariant.TITANIUM, 4);
        this.setHarvestLevel(EnumVariant.SULFUR, 1);
        this.setCreativeTab(MechanimationTabs.BLOCKS);
    }

    public void setHarvestLevel(EnumVariant variant, int level)
    {
        this.setHarvestLevel("pickaxe", level, this.getDefaultState().withProperty(VARIANT, variant));
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return state.getValue(VARIANT).getItemDropped(Item.getItemFromBlock(this));
    }

    @Override
    public int quantityDropped(IBlockState state, int fortune, Random random)
    {
    	return state.getValue(VARIANT).getQuantityDropped(fortune, random);
    }

    @Override
	public int damageDropped(IBlockState state)
    {
    	return state.getValue(VARIANT).getDamageDropped();
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return new ItemStack(Item.getItemFromBlock(this), 1, state.getValue(VARIANT).ordinal());
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
		NICKEL("nickel", 3, 5),
		COPPER("copper", 3, 5),
		ALUMINUM("aluminum", 3, 5),
		TIN("tin", 3, 5),
		SILVER("silver", 3, 5),
		TUNGSTEN("tungsten", 3, 5),
		TITANIUM("titanium", 3, 5),
		SULFUR("sulfur", () -> MechanimationItems.CRAFTING_MATERIAL, (fortune, random) -> {
			if (fortune > 0)
			{
				int j = random.nextInt(fortune + 2);
				if (j < 1) j = 1;
				return j;
			}
			else return 1;
		}, () -> ItemCraftingMaterial.SULFUR, 3, 5);

        private final String name;
        private final float hardness;
        private final float resistance;
        private final Supplier<Item> item;
        private final BiFunction<Integer, Random, Integer> count;
        private final Supplier<Integer> damage;

        private EnumVariant(String name, float hardness)
        {
        	this(name, hardness, hardness * 5);
        }

        private EnumVariant(String name, float hardness, float resistance)
        {
        	this(name, null, null, null, hardness, resistance);
        }

        private EnumVariant(String name, Supplier<Item> item, BiFunction<Integer, Random, Integer> count, Supplier<Integer> damage, float hardness, float resistance)
        {
            this.name = name;
            this.item = item;
            this.damage = damage;
            this.count = count;
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

        public Item getItemDropped(Item blockItem)
        {
        	return item == null ? blockItem : item.get();
        }

        public int getQuantityDropped(int fortune, Random random)
        {
        	return count == null ? 1 : count.apply(fortune, random);
        }

        public int getDamageDropped()
        {
        	return damage == null ? ordinal() : damage.get();
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