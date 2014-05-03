package tsteelworks.worldgen;

import java.util.Random;

import tconstruct.common.TContent;
import tconstruct.util.config.PHConstruct;
import tsteelworks.common.TSContent;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import cpw.mods.fml.common.IWorldGenerator;

public class TSBaseWorldGenerator implements IWorldGenerator
{
    WorldGenLimestone limestone;
    
    public TSBaseWorldGenerator()
    {
        limestone = new WorldGenLimestone(32, TSContent.limestoneBlock.blockID, 0);
    }
    
    @Override
    public void generate (Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
    {
        if (world.provider.terrainType != WorldType.FLAT)
        {
            if (world.provider.dimensionId == 0)
                generateSurface(random, chunkX * 16, chunkZ * 16, world);
        }
    }
    
    void generateSurface (Random random, int xChunk, int zChunk, World world)
    {
        String biomeName = world.getWorldChunkManager().getBiomeGenAt(xChunk, zChunk).biomeName;
        generateLimestone(random, xChunk, zChunk, world);
    }
    
    
    //TODO: Generate in underground ponds
    // This currently generates under oceans, rivers, etc, around the same places you find sandstone
    void generateLimestone (Random random, int xChunk, int zChunk, World world)
    {
        int xPos, yPos, zPos;
        int maxamt = 32;
        int miny = 12;
        int maxy = 64;
        for (int q = 0; q <= maxamt; q++)
        {
            xPos = xChunk + random.nextInt(16);
            yPos = miny + random.nextInt(maxy - miny);
            zPos = zChunk + random.nextInt(16);
            limestone.generate(world, random, xPos, yPos, zPos);
        }
    }
}
