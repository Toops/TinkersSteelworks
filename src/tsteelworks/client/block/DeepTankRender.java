package tsteelworks.client.block;

import tconstruct.client.TProxyClient;
import tconstruct.client.block.BlockSkinRenderHelper;
import tconstruct.library.crafting.Smeltery;
import tsteelworks.blocks.logic.DeepTankLogic;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
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
        if (logic.validStructure)
        {
            int posX = logic.centerPos.x - 1, posY = logic.centerPos.y, posZ = logic.centerPos.z - 1;

            //Liquids
            float base = 0F;
            int yBase = 0;
            int liquidBase = 0;
            for (FluidStack liquid : logic.fluidlist)
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
                    for (int i = 0; i < 9; i++)
                    {
                        float minX = i % 3 == 0 ? -0.001F : 0F;
                        float minZ = i / 3 == 0 ? -0.001F : 0F;
                        float maxX = i % 3 == 2 ? 1.001F : 1F;
                        float maxZ = i / 3 == 2 ? 1.001F : 1F;
                        renderer.setRenderBounds(minX, renderBase, minZ, maxX, renderHeight, maxZ);
                        if (fluid.canBePlacedInWorld())
                            BlockSkinRenderHelper.renderMetadataBlock(Block.blocksList[fluid.getBlockID()], 0, posX + i % 3, posY + yBase, posZ + i / 3, renderer, world);
                        else
                            BlockSkinRenderHelper.renderLiquidBlock(fluid.getStillIcon(), fluid.getFlowingIcon(), posX + i % 3, posY + yBase, posZ + i / 3, renderer, world);
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
