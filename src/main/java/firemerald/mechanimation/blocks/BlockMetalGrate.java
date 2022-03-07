package firemerald.mechanimation.blocks;

import firemerald.mechanimation.init.MechanimationTabs;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class BlockMetalGrate extends Block
{
	public BlockMetalGrate()
	{
		super(Material.IRON, MapColor.IRON);
		setHardness(5.0F);
		setResistance(10.0F);
		this.setHarvestLevel("pickaxe", 1);
		setSoundType(SoundType.METAL);
		this.setCreativeTab(MechanimationTabs.DECORATIONS);
	}
}