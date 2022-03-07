package firemerald.mechanimation.blocks;

import java.util.Random;

import firemerald.mechanimation.init.MechanimationTabs;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockReinforcedGlass extends BlockGlass
{
	public BlockReinforcedGlass()
	{
		super(Material.GLASS, false);
		setHardness(5.0F);
		setResistance(10.0F);
		setSoundType(SoundType.GLASS);
		this.setHarvestLevel("pickaxe", 1);
		this.setCreativeTab(MechanimationTabs.BLOCKS);
	}

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    @Override
	public int quantityDropped(Random random)
    {
        return 1;
    }
}