package tsteelworks.blocks;

import java.util.Random;

import tsteelworks.common.TSContent;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CementFluidBlock extends TSBaseFluid
{
    int tick;
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
        tick++;
        
        int quantaRemaining = quantaPerBlock - world.getBlockMetadata(x, y, z);
        int expQuanta = -101;

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
            checkForHarden(world, x, y, z);
            
            return;
        }

        if (isSourceBlock(world, x, y, z) || !isFlowingVertically(world, x, y, z))
        {
            if (world.getBlockId(x, y - densityDir, z) == blockID)
            {
                flowMeta = 1;
            }
            boolean flowTo[] = getOptimalFlowDirections(world, x, y, z);

            if (flowTo[0])
            {
                flowIntoBlock(world, x - 1, y, z,     flowMeta);
//                checkForHarden(world, x - 1, y, z);
            }
            if (flowTo[1]) 
            {
                flowIntoBlock(world, x + 1, y, z,     flowMeta);
//                checkForHarden(world, x + 1, y, z);
            }
            if (flowTo[2])
            {
                flowIntoBlock(world, x,     y, z - 1, flowMeta);
//                checkForHarden(world, x, y, z - 1);
            }
            if (flowTo[3]) 
            {
                flowIntoBlock(world, x,     y, z + 1, flowMeta);
//                checkForHarden(world, x, y, z + 1);
            }
            
//            checkForHarden(world, x, y, z);
        }
        if ((tick % 30) == 0)
        {
//            checkForHarden(world, x, y, z);
        }
        if ((tick % 60) == 0)
            tick = 0;
        
    }
    
    @Override
    protected void flowIntoBlock(World world, int x, int y, int z, int meta)
    {
        if (meta < 0) return;
        if (displaceIfPossible(world, x, y, z))
        {
            world.setBlock(x, y, z, this.blockID, meta, 3);
//            checkForHarden(world, x, y, z);
        }
    }
    
    /**
     * Forces coment to check to see if it is colliding with air, and then decide what it should harden to.
     */
    private void checkForHarden(World par1World, int par2, int par3, int par4)
    {
        if (par1World.getBlockId(par2, par3, par4) == this.blockID)
        {
//            if (this.blockMaterial == Material.lava)
//            {
                boolean flag = false;

                if (flag || par1World.getBlockMaterial(par2, par3, par4 - 1) == Material.air)
                {
                    flag = true;
                }

                if (flag || par1World.getBlockMaterial(par2, par3, par4 + 1) == Material.air)
                {
                    flag = true;
                }

                if (flag || par1World.getBlockMaterial(par2 - 1, par3, par4) == Material.air)
                {
                    flag = true;
                }

                if (flag || par1World.getBlockMaterial(par2 + 1, par3, par4) == Material.air)
                {
                    flag = true;
                }

                if (flag || par1World.getBlockMaterial(par2, par3 + 1, par4) == Material.air)
                {
                    flag = true;
                }

                if (flag)
                {
//                    int l = par1World.getBlockMetadata(par2, par3, par4);
//
//                    if (l == 0)
//                    {
                    par1World.setBlock(par2, par3, par4, TSContent.cementBlock.blockID);
//                    }

                    this.triggerCementMixEffects(par1World, par2, par3, par4);
                }
//            }
        }
    }

    /**
     * Creates fizzing sound and smoke. Used when lava flows over block or mixes with water.
     */
    protected void triggerCementMixEffects(World par1World, int par2, int par3, int par4)
    {
        par1World.playSoundEffect((double)((float)par2 + 0.5F), (double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F), "random.fizz", 0.5F, 2.6F + (par1World.rand.nextFloat() - par1World.rand.nextFloat()) * 0.8F);

        for (int l = 0; l < 8; ++l)
        {
            par1World.spawnParticle("largesmoke", (double)par2 + Math.random(), (double)par3 + 1.2D, (double)par4 + Math.random(), 0.0D, 0.0D, 0.0D);
        }
    }
}
