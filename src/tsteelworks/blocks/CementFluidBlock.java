package tsteelworks.blocks;

import java.util.Random;

import tconstruct.library.util.CoordTuple;
import tsteelworks.TSteelworks;
import tsteelworks.common.TSContent;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CementFluidBlock extends TSBaseFluid
{
    public CoordTuple origin;
    
    public CementFluidBlock(int id, Fluid fluid, Material material, String texture)
    {
        super(id, fluid, material, texture);
    }
    
    @SideOnly(Side.CLIENT)
    public void setRenderColorByMeta ()
    {
        
    }
    
    @Override
    public void updateTick(World world, int x, int y, int z, Random rand)
    {
        int quantaRemaining = quantaPerBlock - world.getBlockMetadata(x, y, z);
        int expQuanta = -101;
        if (quantaRemaining == quantaPerBlock - world.getBlockMetadata(x, y, z))
            origin = new CoordTuple(x, y, z);
        // check adjacent block levels if non-source
        if (quantaRemaining < quantaPerBlock)
        {
            int y2 = y - densityDir;

            if (world.getBlockId(x,     y2, z    ) == blockID ||
                world.getBlockId(x - 1, y2, z    ) == blockID ||
                world.getBlockId(x + 1, y2, z    ) == blockID ||
                world.getBlockId(x,     y2, z - 1) == blockID ||
                world.getBlockId(x,     y2, z + 1) == blockID)
            {
                expQuanta = quantaPerBlock - 1;
            }
            else
            {
                int maxQuanta = -100;
                maxQuanta = getLargerQuanta(world, x - 1, y, z,     maxQuanta);
                maxQuanta = getLargerQuanta(world, x + 1, y, z,     maxQuanta);
                maxQuanta = getLargerQuanta(world, x,     y, z - 1, maxQuanta);
                maxQuanta = getLargerQuanta(world, x,     y, z + 1, maxQuanta);

                expQuanta = maxQuanta - 1;
            }

            // decay calculation
            if (expQuanta != quantaRemaining)
            {
                quantaRemaining = expQuanta;

                if (expQuanta <= 0)
                {
                    world.setBlockToAir(x, y, z);
                    //checkForHarden(world, x, y, z);
                }
                else
                {
                    world.setBlockMetadataWithNotify(x, y, z, quantaPerBlock - expQuanta, 3);
                    world.scheduleBlockUpdate(x, y, z, blockID, tickRate);
                    world.notifyBlocksOfNeighborChange(x, y, z, blockID);
                }
            }
        }
        // This is a "source" block, set meta to zero, and send a server only update
        else if (quantaRemaining >= quantaPerBlock)
        {
            world.setBlockMetadataWithNotify(x, y, z, 0, 2);
        }

        // Flow vertically if possible
        if (canDisplace(world, x, y + densityDir, z))
        {
            flowIntoBlock(world, x, y + densityDir, z, 1);
            return;
        }

        // Flow outward if possible
        int flowMeta = quantaPerBlock - quantaRemaining + 1;
        
        if (flowMeta >= quantaPerBlock)
        {
//            for (int index = 0; index < pool.length - 1; index++)
//            {
//                if (pool[index].equals(null)) break;
//                checkForHarden(world, pool[index].x, pool[index].y, pool[index].z);
//            }
            if (!isSourceBlock(world, x, y, z))
            {
                checkForHarden(world, x + 1, y, z);
                checkForHarden(world, x - 1, y, z);

                checkForHarden(world, x, y, z + 1);
                checkForHarden(world, x, y, z - 1);
                
                checkForHarden(world, x + 1, y, z + 1);
                checkForHarden(world, x - 1, y, z - 1);
                
                checkForHarden(world, x + 1, y, z - 1);
                checkForHarden(world, x - 1, y, z + 1);
                
                checkForHarden(world, x, y, z);
            }
            if (isSourceBlock(world, x, y, z))
            {
                checkForHarden(world, x + 1, y, z);
                checkForHarden(world, x - 1, y, z);

                checkForHarden(world, x, y, z + 1);
                checkForHarden(world, x, y, z - 1);
                
                checkForHarden(world, x + 1, y, z + 1);
                checkForHarden(world, x - 1, y, z - 1);
                
                checkForHarden(world, x + 1, y, z - 1);
                checkForHarden(world, x - 1, y, z + 1);
                
                checkForHarden(world, x, y, z);
            }
        }
        
        if (flowMeta >= quantaPerBlock)
        {
            return;
        }

        if (isSourceBlock(world, x, y, z) || !isFlowingVertically(world, x, y, z))
        {
            if (world.getBlockId(x, y - densityDir, z) == blockID)
            {
                flowMeta = 1;
            }
            boolean flowTo[] = getOptimalFlowDirections(world, x, y, z);

            if (flowTo[0]) flowIntoBlock(world, x - 1, y, z,     flowMeta);
            if (flowTo[1]) flowIntoBlock(world, x + 1, y, z,     flowMeta);
            if (flowTo[2]) flowIntoBlock(world, x,     y, z - 1, flowMeta);
            if (flowTo[3]) flowIntoBlock(world, x,     y, z + 1, flowMeta);
        }
    }
    
    @Override
    protected void flowIntoBlock(World world, int x, int y, int z, int meta)
    {
        if (meta < 0) return;
        if (displaceIfPossible(world, x, y, z))
        {
            world.setBlock(x, y, z, this.blockID, meta, 3);
//            pool[0] = new CoordTuple(x, y, z);
            
//            pool[pool.length - 1] = new CoordTuple(x, y, z);
        }
//        for (int i = 0; i < pool.length - 1; i++)
//            TSteelworks.loginfo(pool[i].toString());
    }
    
    /**
     * Forces coment to check to see if it is colliding with air, and then decide what it should harden to.
     */
    private void checkForHarden(World world, int x, int y, int z)
    {
        if (world.getBlockId(x, y, z) == this.blockID)
        {
            boolean harden = false;
            if (harden || world.getBlockMaterial(x, y, z - 1) == Material.air || world.getBlockId(x, y, z - 1) == this.blockID)
            {
                harden = true;
            }
            if (harden || world.getBlockMaterial(x, y, z + 1) == Material.air || world.getBlockId(x, y, z + 1) == this.blockID)
            {
                harden = true;
            }
            if (harden || world.getBlockMaterial(x - 1, y, z) == Material.air || world.getBlockId(x - 1, y, z) == this.blockID)
            {
                harden = true;
            }
            if (harden || world.getBlockMaterial(x + 1, y, z) == Material.air || world.getBlockId(x + 1, y, z) == this.blockID)
            {
                harden = true;
            }
            if (harden || world.getBlockMaterial(x, y + 1, z) == Material.air || world.getBlockId(x, y + 1, z) == this.blockID)
            {
                harden = true;
            }
            if (harden || world.getBlockMaterial(x, y - 1, z) == Material.air || world.getBlockId(x, y - 1, z) == this.blockID)
            {
                harden = true;
            }
            if (harden)
            {
                world.setBlock(x, y, z, TSContent.cementBlock.blockID);
                this.triggerCementMixEffects(world, x, y, z);
            }
        }
    }

    /**
     * Creates fizzing sound and smoke. Used when lava flows over block or mixes with water.
     */
    protected void triggerCementMixEffects(World world, int x, int y, int z)
    {
        world.playSoundEffect((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

//        for (int l = 0; l < 8; ++l)
//        {
//            world.spawnParticle("largesmoke", (double)x + Math.random(), (double)y + 1.2D, (double)z + Math.random(), 0.0D, 0.0D, 0.0D);
//        }
    }
}
