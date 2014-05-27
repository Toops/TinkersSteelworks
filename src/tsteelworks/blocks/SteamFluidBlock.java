package tsteelworks.blocks;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import tsteelworks.lib.Repo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SteamFluidBlock extends BlockFluidClassic
{
    // TODO: Make this gaseous
    public Icon stillIcon;
    public Icon flowIcon;
    boolean alpha = true;
    protected int quantaPerBlock = 1;

    public SteamFluidBlock(int id, Fluid fluid, Material material)
    {
        super(id, fluid, material);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons (IconRegister iconRegister)
    {
        stillIcon = iconRegister.registerIcon(Repo.textureDir + "liquid_steam");
        flowIcon = iconRegister.registerIcon(Repo.textureDir + "liquid_steam_flow");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon (int side, int meta)
    {
        if (side == 0 || side == 1)
            return stillIcon;
        return flowIcon;
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
        if (canDisplace(world, x, y - densityDir, z))
        {
            flowIntoBlock(world, x, y - densityDir, z, 1);
            return;
        }

        // Flow outward if possible
        int flowMeta = quantaPerBlock - quantaRemaining + 1;
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
    public boolean isFlowingVertically(IBlockAccess world, int x, int y, int z)
    {
        return world.getBlockId(x, y - densityDir, z) == blockID ||
            (world.getBlockId(x, y, z) == blockID && canFlowInto(world, x, y - densityDir, z));
    }
    
    @Override
    public Vec3 getFlowVector(IBlockAccess world, int x, int y, int z)
    {
        Vec3 vec = world.getWorldVec3Pool().getVecFromPool(0.0D, 0.0D, 0.0D);
        int decay = quantaPerBlock - getQuantaValue(world, x, y, z);

        for (int side = 0; side < 4; ++side)
        {
            int x2 = x;
            int z2 = z;

            switch (side)
            {
                case 0: --x2; break;
                case 1: --z2; break;
                case 2: ++x2; break;
                case 3: ++z2; break;
            }

            int otherDecay = quantaPerBlock - getQuantaValue(world, x2, y, z2);
            if (otherDecay >= quantaPerBlock)
            {
                if (!world.getBlockMaterial(x2, y, z2).blocksMovement())
                {
                    otherDecay = quantaPerBlock - getQuantaValue(world, x2, y - 1, z2);
                    if (otherDecay >= 0)
                    {
                        int power = otherDecay - (decay - quantaPerBlock);
                        vec = vec.addVector((x2 - x) * power, (y - y) * power, (z2 - z) * power);
                    }
                }
            }
            else if (otherDecay >= 0)
            {
                int power = otherDecay - decay;
                vec = vec.addVector((x2 - x) * power, (y - y) * power, (z2 - z) * power);
            }
        }

        if (world.getBlockId(x, y + 1, z) == blockID)
        {
            boolean flag =
                isBlockSolid(world, x,     y,     z - 1, 2) ||
                isBlockSolid(world, x,     y,     z + 1, 3) ||
                isBlockSolid(world, x - 1, y,     z,     4) ||
                isBlockSolid(world, x + 1, y,     z,     5) ||
                isBlockSolid(world, x,     y - 1, z - 1, 2) ||
                isBlockSolid(world, x,     y - 1, z + 1, 3) ||
                isBlockSolid(world, x - 1, y - 1, z,     4) ||
                isBlockSolid(world, x + 1, y - 1, z,     5);

            if (flag)
            {
                vec = vec.normalize().addVector(0.0D, -6.0D, 0.0D);
            }
        }
        vec = vec.normalize();
        return vec;
    }
}
