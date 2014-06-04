package tsteelworks.blocks;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import tconstruct.library.util.CoordTuple;
import tsteelworks.common.TSContent;
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
    public void onBlockAdded(World world, int x, int y, int z)
    {
        world.scheduleBlockUpdate(x, y, z, blockID, tickRate);
        origin = new CoordTuple(x, y, z);
    }
    
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int blockId)
    {
        world.scheduleBlockUpdate(x, y, z, blockID, tickRate);
    }
    
    @Override
    public void updateTick(World world, int x, int y, int z, Random rand)
    {
        int quantaRemaining = quantaPerBlock - world.getBlockMetadata(x, y, z);
        int expQuanta = -101;
        // check adjacent block levels if non-source
        if (quantaRemaining < quantaPerBlock)
        {
            int y2 = y - densityDir;

            if (world.getBlockId(x,     y2, z    ) == blockID ||
                world.getBlockId(x, y2 + 1, z    ) == blockID ||
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
                
                maxQuanta = getLargerQuanta(world, x,     y + 1, z, maxQuanta);

                expQuanta = maxQuanta - 1;
            }

            // decay calculation
            if (expQuanta != quantaRemaining)
            {
                quantaRemaining = expQuanta;

                if (expQuanta <= 0)
                {
                    //world.setBlockToAir(x, y, z);
                    checkForHarden(world, x, y, z);
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
            checkForHarden(world, x, y, z);
//            traceToSource(world, x, y, z);
            return;
        }

        // Flow outward if possible
        int flowMeta = quantaPerBlock - quantaRemaining + 1;
        
        if (flowMeta >= quantaPerBlock)
        {
            checkForHarden(world, x, y, z);
            traceToSource(world, x, y, z, expQuanta);
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
            world.setBlock(x, y, z, this.blockID, meta, 3);
    }
    
    /**
     * Forces coment to check to see if it is colliding with air, and then decide what it should harden to.
     */
    private void checkForHarden(World world, int x, int y, int z)
    {
        if (world.getBlockId(x, y, z) == this.blockID || (world.getBlockId(x, y, z) == TSContent.cementBlock.blockID))
        {
            boolean harden = false;
            if (!this.isFlowingVertically(world, x, y, z)) 
            {
                if (harden || validHardenCoords(world, x, y, z - 1)); harden = true;
                if (harden || validHardenCoords(world, x, y, z + 1)); harden = true;
                if (harden || validHardenCoords(world, x - 1, y, z)); harden = true;
                if (harden || validHardenCoords(world, x - 1, y, z)); harden = true;
                if (harden || validHardenCoords(world, x + 1, y, z + 1)); harden = true;
                if (harden || validHardenCoords(world, x + 1, y, z - 1)); harden = true;
                if (harden || validHardenCoords(world, x - 1, y, z - 1)); harden = true;
                if (harden || validHardenCoords(world, x + 1, y, z + 1)); harden = true;
            }
            if (harden || validHardenCoords(world, x, y - 1, z)); harden = true;
            if (harden || validHardenCoords(world, x, y + 1, z)); harden = true;
            if (harden)
            {
                world.setBlock(x, y, z, TSContent.cementBlock.blockID);
//                this.triggerCementMixEffects(world, x, y, z);
                if (this.isFlowingVertically(world, x, y, z)) 
                    onNeighborBlockChange(world, x, y, z, blockID);
            }
        }
    }
    
    public void traceToSource(World world, int x, int y, int z, int amount)
    {
        for (int xScan = 0; xScan < amount; xScan++)
            for (int yScan = 0; yScan < amount; yScan++)
                for (int zScan = 0; zScan < amount; zScan++)
                {
                    if (world.getBlockId(x - xScan, y - yScan, z - zScan) == blockID && (world.getBlockMetadata(x - xScan, y - yScan, z - zScan) != 3))// || world.getBlockMetadata(x - xScan, y - yScan, z - zScan) == 1))
                    {
                        world.setBlock(x - xScan, y - yScan, z - zScan, TSContent.cementBlock.blockID);
                        break;
                    }
                }
    }
    
    /* IFluidBlock */
/*    @Override
    public FluidStack drain(World world, int x, int y, int z, boolean doDrain)
    {
        if (!isSourceBlock(world, x, y, z))
        {
            return null;
        }

        if (doDrain)
        {
//            world.setBlockToAir(x, y, z);
            checkForHarden(world, x, y, z);
        }

        return stack.copy();
    }*/
    
    public boolean validHardenCoords(World world, int x, int y, int z)
    {
        return (world.getBlockMaterial(x, y, z) == Material.air ||  world.getBlockId(x, y, z) == TSContent.cementBlock.blockID);
    }

//    public boolean isSourceBlock(IBlockAccess world, int x, int y, int z)
//    {
//        return (world.getBlockId(x, y, z) == blockID && world.getBlockMetadata(x, y, z) == 0);//|| (world.getBlockId(x, y, z) == TSContent.cementBlock.blockID));
//    }
    
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
