package tsteelworks.worldgen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenLimestone extends WorldGenerator
{
    /** Stores ID for WorldGenSand */
    private int limestoneID;
    private int limestoneMeta;

    /** The maximum radius used when generating a patch of blocks. */
    private int radius;

    public WorldGenLimestone(int area, int blockID, int blockMeta)
    {
        this.limestoneID = blockID;
        this.limestoneMeta = blockMeta;
        this.radius = area;
    }
    
    public boolean generate(World world, Random random, int xCoord, int yCoord, int zCoord)
    {
        if(!world.getChunkProvider().chunkExists(xCoord >> 4, zCoord >> 4 ))
        {
            return false;
        }
        else
        {
            if (world.getBlockMaterial(xCoord, yCoord, zCoord) != Material.water)
            {
                return false;
            }
        }
        float f = random.nextFloat() * (float)Math.PI;
        double d0 = (double)((float)(xCoord + 16) + MathHelper.sin(f) * (float)this.radius / 16.0F);
        double d1 = (double)((float)(xCoord + 16) - MathHelper.sin(f) * (float)this.radius / 16.0F);
        double d2 = (double)((float)(zCoord + 16) + MathHelper.cos(f) * (float)this.radius / 16.0F);
        double d3 = (double)((float)(zCoord + 16) - MathHelper.cos(f) * (float)this.radius / 16.0F);
        double d4 = (double)(yCoord + random.nextInt(3) - 2);
        double d5 = (double)(yCoord + random.nextInt(3) - 2);

        for (int l = 0; l <= this.radius; ++l)
        {
            double d6 = d0 + (d1 - d0) * (double)l / (double)this.radius;
            double d7 = d4 + (d5 - d4) * (double)l / (double)this.radius;
            double d8 = d2 + (d3 - d2) * (double)l / (double)this.radius;
            double d9 = random.nextDouble() * (double)this.radius / 16.0D;
            double d10 = (double)(MathHelper.sin((float)l * (float)Math.PI / (float)this.radius) + 1.0F) * d9 + 1.0D;
            double d11 = (double)(MathHelper.sin((float)l * (float)Math.PI / (float)this.radius) + 1.0F) * d9 + 1.0D;
            int i1 = MathHelper.floor_double(d6 - d10 / 2.0D);
            int j1 = MathHelper.floor_double(d7 - d11 / 2.0D);
            int k1 = MathHelper.floor_double(d8 - d10 / 2.0D);
            int l1 = MathHelper.floor_double(d6 + d10 / 2.0D);
            int i2 = MathHelper.floor_double(d7 + d11 / 2.0D);
            int j2 = MathHelper.floor_double(d8 + d10 / 2.0D);

            for (int x = i1; x <= l1; ++x)
            {
                double d12 = ((double)x + 0.5D - d6) / (d10 / 2.0D);

                if (d12 * d12 < 1.0D)
                {
                    for (int y = j1; y <= i2; ++y)
                    {
                        double d13 = ((double)y + 0.5D - d7) / (d11 / 2.0D);

                        if (d12 * d12 + d13 * d13 < 1.0D)
                        {
                            for (int z = k1; z <= j2; ++z)
                            {
                                double d14 = ((double)z + 0.5D - d8) / (d10 / 2.0D);
                                if(world.getChunkProvider().chunkExists(x >> 4, z >> 4))
                                {
                                    Block block = Block.blocksList[world.getBlockId(x, y, z)];
                                    if (d12 * d12 + d13 * d13 + d14 * d14 < 1.0D && (block != null && block.blockID ==  Block.stone.blockID))
                                    {
                                        world.setBlock(x, y, z, this.limestoneID, limestoneMeta, 2);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}
