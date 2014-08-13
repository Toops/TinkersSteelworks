package tsteelworks.client.block;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import mantle.world.CoordTuple;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import nf.fr.ephys.cookiecore.util.MultiFluidTank;
import tconstruct.client.BlockSkinRenderHelper;
import tconstruct.util.ItemHelper;
import tsteelworks.common.blocks.logic.DeepTankLogic;

public class DeepTankRender implements ISimpleBlockRenderingHandler {
	public static final int DEEPTANK_MODEL = RenderingRegistry.getNextAvailableRenderId();

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		if (modelID == DEEPTANK_MODEL) {
			ItemHelper.renderStandardInvBlock(renderer, block, metadata);
		}
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelID, RenderBlocks renderer) {
		if (modelID == DEEPTANK_MODEL) {
			if (world.getBlockMetadata(x, y, z) == 13)
				return renderDeepTank(world, x, y, z, block, renderer);

			renderer.renderStandardBlock(block, x, y, z);
		}

		return true;
	}

	public boolean renderDeepTank(IBlockAccess world, int x, int y, int z, Block block, RenderBlocks renderer) {
		DeepTankLogic logic = (DeepTankLogic) world.getTileEntity(x, y, z);
		renderer.renderStandardBlock(block, x, y, z);

		if (!logic.isValid()) return true;

		MultiFluidTank tank = logic.getTank();

		if (tank.getCapacity() == 0) return true;

		CoordTuple corner = logic.getStructure().getBorderPos();
		float yOffset = 0;
		for (int i = 0; i < tank.getNbFluids(); i++) {
			FluidStack fluid = tank.getFluid(i);

			float height = fluid.amount / tank.getCapacity() * logic.getStructure().getNbLayers();

			renderFluidLayer(fluid.getFluid(), world, corner.x, corner.y + yOffset, corner.z, logic.getStructure().getXWidth(), height, logic.getStructure().getZWidth(), renderer);

			yOffset += height;
		}

		return true;
	}

	// todo: move to lib
	public static void renderFluidLayer(Fluid fluid, IBlockAccess world, int x, float y, int z, int width, double height, int length, RenderBlocks renderer) {
		/*
		 * concept (please don't delete this, this code is hard to understand.):
		 * We cannot render more than a block at a time, for the x & z axis that's easy: just draw the whole block multiple times
		 *
		 * For the y axis however, things are a bit more complex as the cube is now always plain
		 * - it can be less than a cube in height
		 * - it can start higher than the bottom
		 *
		 * So we need to calculate the bottom offset, the coordinate at which to render the block and the block height (max 1)
		 * And repeat for each block until we reached "height"
		 *
		 * the y coord is easy, just clip the y parameter to the grid (and as the grid is just integers, just round down)
		 * the offset is also easy, that's what's been thrown away when we clipped
		 *
		 *       vvvvv offset
		 * y = 3.45...
		 *     ^ y coord
		 *
		 * As for the height of the fluid:
		 * - for the bottom block, it's 1 - offset
		 * - for the others it's 1
		 * then we check if the height is above the "liquid height" left, if it is, just use that liquid height and we'll be fine
		 * (obviously the liquid height left is the received height param minus every height we already rendered)
		 *
		 * I'm saying it's easy but it took me like 20 minutes of hitting my head against the wall (and drawing schematics) to figure this out >_>
		 */
		// the Y position of the block to draw
		int yCoord = (int) Math.floor(y);

		// the offset in the block at which the fluid is (from the bottom)
		double blockYPos = y - yCoord;

		double liquidSize = 0;
		while (liquidSize < height) {
			// the height of the block: either the full block (except the bottom offset) or the amount of liquid left if there is less
			double renderHeight = Math.min(height - liquidSize, 1 - blockYPos);

			renderer.setRenderBounds(0, blockYPos, 0, 1, blockYPos + renderHeight, 1);

			for (int xOffset = 0; xOffset < width; x++) {
				for (int zOffset = 0; zOffset < length; z++) {
					if(fluid.canBePlacedInWorld()) {
						BlockSkinRenderHelper.renderMetadataBlock(fluid.getBlock(), 0, x + xOffset, yCoord, z + zOffset, renderer, world);
					} else {
						BlockSkinRenderHelper.renderLiquidBlock(fluid.getStillIcon(), fluid.getFlowingIcon(), x + xOffset, yCoord, z + zOffset, renderer, world);
					}
				}
			}

			liquidSize += renderHeight;

			// remove the bottom offset, as we no longer will be at the bottom
			blockYPos = 0.0F;
			yCoord++;
		}
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return DEEPTANK_MODEL;
	}
}
