package firemerald.mechanimation.blocks.machine;

import java.util.List;
import java.util.function.Supplier;

import firemerald.api.core.client.Translator;
import firemerald.mechanimation.blocks.machine.BlockElectrolyzer.EnumVariant;
import firemerald.mechanimation.tileentity.machine.electrolyzer.TileEntityElectrolyzer;
import firemerald.mechanimation.tileentity.machine.electrolyzer.TileEntityElectrolyzerAdvanced;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockElectrolyzer extends BlockMachineBase<TileEntityElectrolyzer<?>, EnumVariant>
{
	public static enum EnumVariant implements IMachineVariant<TileEntityElectrolyzer<?>>
	{
		ADVANCED("advanced", MapColor.IRON, 5f, 10f, TileEntityElectrolyzerAdvanced::new);

        private final String name;
        private final MapColor color;
        private final float hardness;
        private final float resistance;
        private final Supplier<TileEntityElectrolyzer<?>> tile;

        private EnumVariant(String name, MapColor color, float hardness, Supplier<TileEntityElectrolyzer<?>> tile)
        {
        	this(name, color, hardness, hardness * 5, tile);
        }

        private EnumVariant(String name, MapColor color, float hardness, float resistance, Supplier<TileEntityElectrolyzer<?>> tile)
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
		public TileEntityElectrolyzer<?> newTile()
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
		list.add(Translator.translate("lore.mechanimation.electrolyzer"));
	}

	@Override
	public EnumVariant getDefaultVariant()
	{
		return EnumVariant.ADVANCED;
	}
}