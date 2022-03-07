package firemerald.mechanimation.blocks.machine;

import java.util.List;
import java.util.function.Supplier;

import firemerald.api.core.client.Translator;
import firemerald.mechanimation.blocks.machine.BlockMeroxTreater.EnumVariant;
import firemerald.mechanimation.tileentity.machine.merox_treater.TileEntityMeroxTreater;
import firemerald.mechanimation.tileentity.machine.merox_treater.TileEntityMeroxTreaterAdvanced;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMeroxTreater extends BlockMachineBase<TileEntityMeroxTreater<?>, EnumVariant>
{
	public static enum EnumVariant implements IMachineVariant<TileEntityMeroxTreater<?>>
	{
		ADVANCED("advanced", MapColor.IRON, 5f, 10f, TileEntityMeroxTreaterAdvanced::new);

        private final String name;
        private final MapColor color;
        private final float hardness;
        private final float resistance;
        private final Supplier<TileEntityMeroxTreater<?>> tile;

        private EnumVariant(String name, MapColor color, float hardness, Supplier<TileEntityMeroxTreater<?>> tile)
        {
        	this(name, color, hardness, hardness * 5, tile);
        }

        private EnumVariant(String name, MapColor color, float hardness, float resistance, Supplier<TileEntityMeroxTreater<?>> tile)
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
		public TileEntityMeroxTreater<?> newTile()
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
		list.add(Translator.translate("lore.mechanimation.merox_treater"));
	}

	@Override
	public EnumVariant getDefaultVariant()
	{
		return EnumVariant.ADVANCED;
	}
}