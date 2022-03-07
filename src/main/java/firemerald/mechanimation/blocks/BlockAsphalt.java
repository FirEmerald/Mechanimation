package firemerald.mechanimation.blocks;

import firemerald.mechanimation.init.MechanimationTabs;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class BlockAsphalt extends Block
{
	public BlockAsphalt()
	{
		super(Material.ROCK, MapColor.BLACK);
		setHardness(1.5F);
		setResistance(10.0F);
		setSoundType(SoundType.STONE);
        this.setCreativeTab(MechanimationTabs.BLOCKS);
        this.setDefaultSlipperiness(0.5f);
	}
}