package firemerald.mechanimation.blocks.machine;

import java.util.List;
import java.util.function.Supplier;

import firemerald.api.core.client.Translator;
import firemerald.mechanimation.blocks.machine.BlockFluidReactor.EnumVariant;
import firemerald.mechanimation.tileentity.machine.fluid_reactor.TileEntityFluidReactorAdvanced;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFluidReactor extends BlockMachineBase<TileEntityFluidReactorAdvanced, EnumVariant>
{
	public static enum EnumVariant implements IMachineVariant<TileEntityFluidReactorAdvanced>
	{
		ADVANCED("advanced", MapColor.IRON, 5f, 10f, TileEntityFluidReactorAdvanced::new);

        private final String name;
        private final MapColor color;
        private final float hardness;
        private final float resistance;
        private final Supplier<TileEntityFluidReactorAdvanced> tile;

        private EnumVariant(String name, MapColor color, float hardness, Supplier<TileEntityFluidReactorAdvanced> tile)
        {
        	this(name, color, hardness, hardness * 5, tile);
        }

        private EnumVariant(String name, MapColor color, float hardness, float resistance, Supplier<TileEntityFluidReactorAdvanced> tile)
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
		public TileEntityFluidReactorAdvanced newTile()
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
		list.add(Translator.translate("lore.mechanimation.fluid_reactor"));
	}

	@Override
	public EnumVariant getDefaultVariant()
	{
		return EnumVariant.ADVANCED;
	}
}