package tsteelworks.client.block;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import mantle.world.CoordTuple;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.FluidStack;
import nf.fr.ephys.cookiecore.helpers.RenderHelper;
import nf.fr.ephys.cookiecore.util.MultiFluidTank;
import tconstruct.util.ItemHelper;
import tsteelworks.common.blocks.logic.DeepTankLogic;

// TODO: transparent fluids should be transparent in the tank too
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
			renderer.renderStandardBlock(block, x, y, z);

			if (world.getBlockMetadata(x, y, z) == 13)
				return renderDeepTank(world, x, y, z, renderer, block);
		}

		return true;
	}

	public boolean renderDeepTank(IBlockAccess world, int x, int y, int z, RenderBlocks renderer, Block block) {
		DeepTankLogic logic = (DeepTankLogic) world.getTileEntity(x, y, z);

		if (!logic.isValid()) return true;

		MultiFluidTank tank = logic.getFluidTank();

		if (tank.getCapacity() == 0) return true;

		CoordTuple corner = logic.getStructure().getBorderPos();

		// get the luminosity of the inside of the tank
		int luminosity = block.getMixedBrightnessForBlock(world, corner.x + 1, corner.y + 1, corner.z + 1);
		Tessellator tessellator = Tessellator.instance;
		tessellator.setBrightness(luminosity);
		tessellator.setColorOpaque_F(0.5F, 0.5F, 0.5F);

		renderer.enableAO = false;

		int maxHeight = logic.getStructure().getNbLayers();

		float yOffset = 0;
		for (int i = 0; i < tank.getNbFluids() && yOffset <= maxHeight; i++) {
			FluidStack fluid = tank.getFluid(i);

			float height = (float) fluid.amount / tank.getCapacity() * logic.getStructure().getNbLayers();

			if (height + yOffset > maxHeight) {
				height = maxHeight - yOffset;
			}

			renderFluidLayer(RenderHelper.getFluidTexture(fluid), corner.x + 1, corner.y + yOffset + 1, corner.z + 1, logic.getStructure().getXWidth() - 2, height, logic.getStructure().getZWidth() - 2, renderer);

			yOffset += height;
		}

		return true;
	}

	/**
	 *  Renders an icon as a cuboid
	 *
	 * I wrote this, every part of it, I made it work by writing down schematics and math stuff
	 * And now I don't even understand how it works anymore
	 * And it was 5 minutes ago.
	 *
	 * @param icon      The icon to render
	 * @param x         The x coord of the fluid
	 * @param y         The y coord of the fluid
	 * @param z         The z coord of the fluid
	 * @param width     The x length of the fluid
	 * @param height    The y length of the fluid
	 * @param length    The z length of the fluid
	 * @param renderer  RenderBlocks instance
	 */
	public static void renderFluidLayer(IIcon icon, int x, float y, int z, int width, double height, int length, RenderBlocks renderer) {
		// the Y position of the block to draw
		int yCoord = (int) Math.floor(y);

		// the offset in the block at which the fluid is (from the bottom)
		double blockYPos = y - yCoord;

		renderer.setRenderBounds(0, blockYPos, 0, 1, height, 1);
		for (int xPos = 0; xPos < width; xPos++) {
			for (int zPos = 0; zPos < length; zPos++) {
				renderer.renderFaceYNeg(null, x + xPos, yCoord, z + zPos, icon);
				renderer.renderFaceYPos(null, x + xPos, y, z + zPos, icon);
			}
		}

		double liquidSize = 0;
		while (liquidSize < height) {
			// the height of the block: either the full block (except the bottom offset) or the amount of liquid left if there is less
			double renderHeight = Math.min(height - liquidSize, 1 - blockYPos);

			renderer.setRenderBounds(0, blockYPos, 0, 1, blockYPos + renderHeight, 1);

			for (int xPos = 0; xPos < width; xPos++) {
				renderer.renderFaceZNeg(null, x + xPos, yCoord, z, icon);
				renderer.renderFaceZPos(null, x + xPos, yCoord, z + length - 1, icon);
			}

			for (int zPos = 0; zPos < length; zPos++) {
				renderer.renderFaceXNeg(null, x , yCoord, z + zPos, icon);
				renderer.renderFaceXPos(null, x + width - 1, yCoord, z + zPos, icon);
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
