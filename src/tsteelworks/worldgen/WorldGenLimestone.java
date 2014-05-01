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

//    public boolean generate(World world, Random rand, int x, int y, int z)
//    {
//        if (world.getBlockMaterial(x, y, z) != Material.water)
//        {
//            return false;
//        }
//        else
//        {
//            int radial = rand.nextInt(this.radius - 2) + 2;
//            byte height = 8;
//
//            for (int xCheck = x - radial; xCheck <= x + radial; ++xCheck)
//            {
//                for (int zCheck = z - radial; zCheck <= z + radial; ++zCheck)
//                {
//                    int xSquare = xCheck - x;
//                    int zSquare = zCheck - z;
//
//                    if (xSquare * xSquare + zSquare * zSquare <= radial * radial)
//                    {
//                        for (int yCheck = y - height; yCheck <= y + height; ++yCheck)
//                        {
//                            int searchBlockID = world.getBlockId(xCheck, yCheck, zCheck);
//                            if (searchBlockID == Block.stone.blockID)
//                            {
//                                world.setBlock(xCheck, yCheck, zCheck, this.limestoneID, 0, 2);
//                            }
//                        }
//                    }
//                }
//            }
//
//            return true;
//        }
//    }
    
    public boolean generate(World world, Random random, int startX, int startY, int startZ)
    {
        if (world.getBlockMaterial(startX, startY, startZ) != Material.water)
        {
            return false;
        }
        float f = random.nextFloat() * (float)Math.PI;
        double d0 = (double)((float)(startX + 16) + MathHelper.sin(f) * (float)this.radius / 8.0F);
        double d1 = (double)((float)(startX + 16) - MathHelper.sin(f) * (float)this.radius / 8.0F);
        double d2 = (double)((float)(startZ + 16) + MathHelper.cos(f) * (float)this.radius / 8.0F);
        double d3 = (double)((float)(startZ + 16) - MathHelper.cos(f) * (float)this.radius / 8.0F);
        double d4 = (double)(startY + random.nextInt(3) - 2);
        double d5 = (double)(startY + random.nextInt(3) - 2);

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

            for (int k2 = i1; k2 <= l1; ++k2)
            {
                double d12 = ((double)k2 + 0.5D - d6) / (d10 / 2.0D);

                if (d12 * d12 < 1.0D)
                {
                    for (int l2 = j1; l2 <= i2; ++l2)
                    {
                        double d13 = ((double)l2 + 0.5D - d7) / (d11 / 2.0D);

                        if (d12 * d12 + d13 * d13 < 1.0D)
                        {
                            for (int i3 = k1; i3 <= j2; ++i3)
                            {
                                double d14 = ((double)i3 + 0.5D - d8) / (d10 / 2.0D);

                                Block block = Block.blocksList[world.getBlockId(k2, l2, i3)];
                                if (d12 * d12 + d13 * d13 + d14 * d14 < 1.0D && (block != null && block.isGenMineableReplaceable(world, k2, l2, i3, Block.stone.blockID)))
                                {
                                    world.setBlock(k2, l2, i3, this.limestoneID, limestoneMeta, 2);
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
