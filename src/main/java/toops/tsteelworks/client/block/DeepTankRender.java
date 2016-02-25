package toops.tsteelworks.client.block;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import mantle.world.CoordTuple;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.FluidStack;
import nf.fr.ephys.cookiecore.helpers.RenderHelper;
import nf.fr.ephys.cookiecore.util.MultiFluidTank;
import tconstruct.util.ItemHelper;
import toops.tsteelworks.common.blocks.logic.DeepTankLogic;
import toops.tsteelworks.common.structure.StructureDeepTank;

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

		StructureDeepTank structure = logic.getStructure();
		CoordTuple corner = logic.getStructure().getBorderPos();

		// get the luminosity of the inside of the tank. Edit: in a loaded chunk. Thus making the code overcomplicated
	/*	int lightX, lightZ;
		if (x == corner.x)
			lightZ = corner.z + 1;
		else if (x == corner.x + structure.getXWidth() - 1)
			lightZ = corner.z - 1;
		else if*/

		int luminosity = block.getMixedBrightnessForBlock(world, corner.x + 1, corner.y + 1, corner.z + 1);
		Tessellator tessellator = Tessellator.instance;
		tessellator.setBrightness(luminosity);
		tessellator.setColorOpaque_F(0.5F, 0.5F, 0.5F);

		renderer.enableAO = false;

		int maxHeight = structure.getNbLayers();

		float yOffset = 0;
		for (int i = 0; i < tank.getNbFluids() && yOffset <= maxHeight; i++) {
			FluidStack fluid = tank.getFluid(i);

			float height = (float) fluid.amount / tank.getCapacity() * logic.getStructure().getNbLayers();

			if (height + yOffset > maxHeight) {
				height = maxHeight - yOffset;
			}

			RenderHelper.renderFluidLayer(RenderHelper.getFluidTexture(fluid), corner.x + 1, corner.y + yOffset + 1, corner.z + 1, logic.getStructure().getXWidth() - 2, height, logic.getStructure().getZWidth() - 2, renderer);

			yOffset += height;
		}

		return true;
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
