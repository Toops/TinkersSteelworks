package tsteelworks.client.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.client.TProxyClient;
import tconstruct.client.block.BlockSkinRenderHelper;
import tconstruct.library.util.CoordTuple;
import tsteelworks.blocks.logic.DeepTankLogic;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class DeepTankRender implements ISimpleBlockRenderingHandler
{
    public static int deeptankModel = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        if (modelID == deeptankModel)
        {
            TProxyClient.renderStandardInvBlock(renderer, block, metadata);
        }
    }

    @Override
    public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelID, RenderBlocks renderer)
    {
        if (modelID == deeptankModel)
        {
            if (world.getBlockMetadata(x, y, z) == 13)
                return renderDeepTank(world, x, y, z, block, modelID, renderer);
            else
                renderer.renderStandardBlock(block, x, y, z);
        }
        return true;
    }

    public boolean renderDeepTank (IBlockAccess world, int x, int y, int z, Block block, int modelID, RenderBlocks renderer)
    {
        renderer.renderStandardBlock(block, x, y, z);
        DeepTankLogic logic = (DeepTankLogic) world.getBlockTileEntity(x, y, z);
        if (logic.isStructureValid())
        {
            CoordTuple centerPos = logic.getCenterPos();
			int posX = centerPos.x - (logic.xDistanceToRim() - 1);
            int posY = centerPos.y;
            int posZ = centerPos.z - (logic.zDistanceToRim() - 1);
            //Liquids
            float base = 0F;
            int yBase = 0;
            int liquidBase = 0;
            for (FluidStack liquid : logic.getFluidlist())
            {
                int liquidSize = liquid.amount;
                while (liquidSize > 0)
                {
                    int room = logic.layerFluidCapacity() - liquidBase;
                    int countSize = liquidSize > room ? room : liquidSize;
                    liquidSize -= countSize;

                    float height = countSize > logic.layerFluidCapacity() ? 1.0F : countSize / (float)logic.layerFluidCapacity();
                    //renderer.setRenderBounds(0, base, 0, 1, height + base, 1);
                    float renderBase = base;
                    float renderHeight = height + base;
                    base += height;
                    liquidBase += countSize;

                    Fluid fluid = liquid.getFluid();
                    // This if statement is simply to save a little processing if it's symetrical
                    if (logic.getInnerMaxX() == logic.getInnerMaxZ())
                    {
                    
                        for (int i = 0; i < logic.innerSpaceTotal(); i++)
                        {
                            float minX = i % (logic.getInnerMaxX()) == 0 ? -0.001F : 0F;
                            float minZ = i / (logic.getInnerMaxX()) == 0 ? -0.001F : 0F;
                            float maxX = i % (logic.getInnerMaxZ()) == 2 ? 1.001F : 1F;
                            float maxZ = i / (logic.getInnerMaxZ()) == 2 ? 1.001F : 1F;
                            
                            renderer.setRenderBounds(minX, renderBase, minZ, maxX, renderHeight, maxZ);
                            int rx = posX + i % logic.getInnerMaxX();
                            int ry = posY + yBase;
                            int rz = posZ + i / logic.getInnerMaxZ();
                            if (fluid.canBePlacedInWorld())
                                BlockSkinRenderHelper.renderMetadataBlock(Block.blocksList[fluid.getBlockID()], 0, rx, ry, rz, renderer, world);
                            else
                                BlockSkinRenderHelper.renderLiquidBlock(fluid.getStillIcon(), fluid.getFlowingIcon(), rx, ry, rz, renderer, world);
                        }
                    }
                    else
                    {
                        for (int i = 0; i < logic.innerSpaceTotal(); i++)
                        {
                            int modZ = getRenderZOffset(logic.getInnerMaxX(), logic.getInnerMaxZ());
                            
                            float minX = i % (logic.getInnerMaxX()) == 0 ? -0.001F : 0F;
                            float minZ = i / (logic.getInnerMaxZ()) == 0 ? -0.001F : 0F;
                            float maxX = i % (logic.getInnerMaxX()) == 2 ? 1.001F : 1F;
                            float maxZ = i / (logic.getInnerMaxZ()) == 2 ? 1.001F : 1F;
                            
                            renderer.setRenderBounds(minX, renderBase, minZ, maxX, renderHeight, maxZ);
                            int rx = posX + i % logic.getInnerMaxX();
                            int ry = posY + yBase;
                            int rz = posZ + i / modZ;
                            if (fluid.canBePlacedInWorld())
                                BlockSkinRenderHelper.renderMetadataBlock(Block.blocksList[fluid.getBlockID()], 0, rx, ry, rz, renderer, world);
                            else
                                BlockSkinRenderHelper.renderLiquidBlock(fluid.getStillIcon(), fluid.getFlowingIcon(), rx, ry, rz, renderer, world);
                        }
                    }
                    if (countSize == room)
                    {
                        base = 0F;
                        yBase++;
                        liquidBase = 0;
                    }
                }
            }
        }
        return true;
    }
    
    // TODO: Write an algorithm for this arbitrary slop
    public int getRenderZOffset (int x, int z)
    {
        if (x == 1)
        {
            switch (z)
            {
            case 3: return z - 2; //1x3
            case 5: return z - 4; //1x5
            case 7: return z - 6; //1x7
            case 9: return z - 8; //1x9
            default: return z;
            }
        }
        if (x == 3)
        {
            switch (z)
            {
            case 1: return z + 4; //3x1
            case 5: return z - 2; //3x5
            case 7: return z - 4; //3x7
            case 9: return z - 6; //3x9
            default: return z;
            }
        }
        if (x == 5)
        {
            switch (z)
            {
            case 1: return z + 4; //5x1
            case 3: return z + 2; //5x3
            case 7: return z - 2; //5x7
            case 9: return z - 4; //5x9
            default: return z;
            }
        }
        if (x == 7)
        {
            switch (z)
            {
            case 1: return z + 6;
            case 3: return z + 4;
            case 5: return z + 2;
            case 9: return z - 2;
            default: return z;
            }
        }
        if (x == 9)
        {
            switch (z)
            {
            case 1: return z + 8;
            case 3: return z + 6;
            case 5: return z + 4;
            case 7: return z + 2;
            default: return z;
            }
        }
        return z; // This should never happen.
    }
    
    @Override
    public boolean shouldRender3DInInventory ()
    {
        return true;
    }

    @Override
    public int getRenderId ()
    {
        return deeptankModel;
    }
}
