package firemerald.mechanimation.blocks.machine;

import java.util.List;
import java.util.function.Supplier;

import firemerald.api.core.client.Translator;
import firemerald.mechanimation.blocks.machine.BlockGenerator.EnumVariant;
import firemerald.mechanimation.tileentity.machine.generator.TileEntityCombustionGenerator;
import firemerald.mechanimation.tileentity.machine.generator.TileEntityGeneratorBase;
import firemerald.mechanimation.tileentity.machine.generator.TileEntityStirlingGenerator;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockGenerator extends BlockMachineBase<TileEntityGeneratorBase<?, ?>, EnumVariant>
{
	public static enum EnumVariant implements IMachineVariant<TileEntityGeneratorBase<?, ?>>
	{
		STIRLING("stirling", MapColor.IRON, 5f, 10f, TileEntityStirlingGenerator::new),
		COMBUSTION("combustion", MapColor.IRON, 5f, 10f, TileEntityCombustionGenerator::new);

        private final String name;
        private final MapColor color;
        private final float hardness;
        private final float resistance;
        private final Supplier<TileEntityGeneratorBase<?, ?>> tile;

        private EnumVariant(String name, MapColor color, float hardness, Supplier<TileEntityGeneratorBase<?, ?>> tile)
        {
        	this(name, color, hardness, hardness * 5, tile);
        }

        private EnumVariant(String name, MapColor color, float hardness, float resistance, Supplier<TileEntityGeneratorBase<?, ?>> tile)
        {
            this.name = name;
            this.color = color;
            this.hardness = hardness;
            this.resistance = resistance;
            this.tile = tile;
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

		@Override
		public TileEntityGeneratorBase<?, ?> newTile()
		{
			return tile.get();
		}

		@Override
		public MapColor getColor()
		{
			return color;
		}

		@Override
		public float getHardness()
		{
			return hardness;
		}

		@Override
		public float getExplosionResistance()
		{
			return resistance;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag)
	{
		super.addInformation(stack, world, list, flag);
		list.add(Translator.translate("lore.mechanimation.generator." + getName(stack)));
	}

	@Override
	public EnumVariant getDefaultVariant()
	{
		return EnumVariant.STIRLING;
	}
}