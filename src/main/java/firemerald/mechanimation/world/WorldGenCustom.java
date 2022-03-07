package firemerald.mechanimation.world;

import java.util.Random;

import firemerald.mechanimation.config.CommonConfig;
import firemerald.mechanimation.init.MechanimationBlocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.common.IWorldGenerator;

public class WorldGenCustom implements IWorldGenerator
{
	public static final WorldGenCustom INSTANCE = new WorldGenCustom();

	public WorldGenMinable nickelGen, copperGen, aluminumGen, tinGen, silverGen, tungstenGen, titaniumGen, sulfurGen;

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
	{
		generate(random, chunkX, chunkZ, world, 0);
	}

	public void generate(Random random, int chunkX, int chunkZ, World world, int retrogenLevel)
	{
		CommonConfig config = CommonConfig.INSTANCE;
        this.nickelGen = new WorldGenMinable(MechanimationBlocks.NICKEL_ORE, config.overworldNickel.getSize());
        this.copperGen = new WorldGenMinable(MechanimationBlocks.COPPER_ORE, config.overworldCopper.getSize());
        this.aluminumGen = new WorldGenMinable(MechanimationBlocks.ALUMINUM_ORE, config.overworldAluminum.getSize());
        this.tinGen = new WorldGenMinable(MechanimationBlocks.TIN_ORE, config.overworldTin.getSize());
        this.silverGen = new WorldGenMinable(MechanimationBlocks.SILVER_ORE, config.overworldSilver.getSize());
        this.tungstenGen = new WorldGenMinable(MechanimationBlocks.TUNGSTEN_ORE, config.overworldTungsten.getSize());
        this.titaniumGen = new WorldGenMinable(MechanimationBlocks.TITANIUM_ORE, config.overworldTitanium.getSize());
        this.sulfurGen = new WorldGenMinable(MechanimationBlocks.SULFUR_ORE, config.overworldSulfur.getSize());
		if (world.provider.isSurfaceWorld()) generateSurface(world, random, chunkX * 16, chunkZ * 16, retrogenLevel);
	}

	private void generateSurface(World world, Random random, int chunkX, int chunkZ, int retrogenLevel)
	{
		if (retrogenLevel < 1)
		{
			CommonConfig config = CommonConfig.INSTANCE;
			BlockPos chunkPos = new BlockPos(chunkX, 0, chunkZ);
	        net.minecraftforge.common.MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Pre(world, random, chunkPos));
	        if (nickelGen != null && config.overworldNickel.getDoesGen() && TerrainGen.generateOre(world, random, nickelGen, chunkPos, OreGenEvent.GenerateMinable.EventType.CUSTOM))
	        this.genStandardOre3(chunkPos, world, random, config.overworldNickel.getCount(), nickelGen, config.overworldNickel.getMinHeight(), config.overworldNickel.getMaxHeight());
	        if (copperGen != null && config.overworldCopper.getDoesGen() && TerrainGen.generateOre(world, random, copperGen, chunkPos, OreGenEvent.GenerateMinable.EventType.CUSTOM))
	        this.genStandardOre3(chunkPos, world, random, config.overworldCopper.getCount(), copperGen, config.overworldCopper.getMinHeight(), config.overworldCopper.getMaxHeight());
	        if (aluminumGen != null && config.overworldAluminum.getDoesGen() && TerrainGen.generateOre(world, random, aluminumGen, chunkPos, OreGenEvent.GenerateMinable.EventType.CUSTOM))
	        this.genStandardOre3(chunkPos, world, random, config.overworldAluminum.getCount(), aluminumGen, config.overworldAluminum.getMinHeight(), config.overworldAluminum.getMaxHeight());
	        if (tinGen != null && config.overworldTin.getDoesGen() && TerrainGen.generateOre(world, random, tinGen, chunkPos, OreGenEvent.GenerateMinable.EventType.CUSTOM))
	        this.genStandardOre3(chunkPos, world, random, config.overworldTin.getCount(), tinGen, config.overworldTin.getMinHeight(), config.overworldTin.getMaxHeight());
	        if (silverGen != null && config.overworldSilver.getDoesGen() && TerrainGen.generateOre(world, random, silverGen, chunkPos, OreGenEvent.GenerateMinable.EventType.CUSTOM))
	        this.genStandardOre3(chunkPos, world, random, config.overworldSilver.getCount(), silverGen, config.overworldSilver.getMinHeight(), config.overworldSilver.getMaxHeight());
	        if (tungstenGen != null && config.overworldTungsten.getDoesGen() && TerrainGen.generateOre(world, random, tungstenGen, chunkPos, OreGenEvent.GenerateMinable.EventType.CUSTOM))
	        this.genStandardOre3(chunkPos, world, random, config.overworldTungsten.getCount(), tungstenGen, config.overworldTungsten.getMinHeight(), config.overworldTungsten.getMaxHeight());
	        if (titaniumGen != null && config.overworldTitanium.getDoesGen() && TerrainGen.generateOre(world, random, titaniumGen, chunkPos, OreGenEvent.GenerateMinable.EventType.CUSTOM))
	        this.genStandardOre3(chunkPos, world, random, config.overworldTitanium.getCount(), titaniumGen, config.overworldTitanium.getMinHeight(), config.overworldTitanium.getMaxHeight());
	        if (sulfurGen != null && config.overworldSulfur.getDoesGen() && TerrainGen.generateOre(world, random, sulfurGen, chunkPos, OreGenEvent.GenerateMinable.EventType.CUSTOM))
	        this.genStandardOre3(chunkPos, world, random, config.overworldSulfur.getCount(), sulfurGen, config.overworldSulfur.getMinHeight(), config.overworldSulfur.getMaxHeight());
	        net.minecraftforge.common.MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Post(world, random, chunkPos));
		}
	}

    protected void genStandardOre3(BlockPos chunkPos, World worldIn, Random random, double clusterCount, WorldGenerator generator, int minHeight, int maxHeight)
    {
        if (maxHeight < minHeight)
        {
            int i = minHeight;
            minHeight = maxHeight;
            maxHeight = i;
        }
        else if (maxHeight == minHeight)
        {
            if (minHeight < 255)
            {
                ++maxHeight;
            }
            else
            {
                --minHeight;
            }
        }
        int blockCount = MathHelper.floor(clusterCount);
        if ((clusterCount - blockCount) < random.nextDouble()) blockCount++;
        for (int j = 0; j < blockCount; ++j)
        {
            BlockPos blockpos = chunkPos.add(random.nextInt(16), random.nextInt(maxHeight - minHeight) + minHeight, random.nextInt(16));
            generator.generate(worldIn, random, blockpos);
        }
    }
}