package tsteelworks.common.structure;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import tsteelworks.common.blocks.logic.HighOvenDuctLogic;
import tsteelworks.common.blocks.logic.HighOvenLogic;
import tsteelworks.common.core.TSContent;
import tsteelworks.lib.IServantLogic;

import java.util.ArrayList;
import java.util.List;

public class StructureHighOven implements IStructure {
	/**
	 * Used to determine if the structure has a bottom.
	 */
	private boolean structureHasBottom = false;

	/**
	 * Used to determine if the structure has a top.
	 */
	private boolean structureHasTop = false;

	/**
	 * Used to determine if the structure is valid.
	 */
	private boolean validStructure = false;

	/**
	 * The amount of layers.
	 */
	private int nbLayers = 0;

	private HighOvenLogic controller;

	/**
	 * The structure's output duct instance.
	 */
	private List<HighOvenDuctLogic> outputDucts = new ArrayList<>();

	public StructureHighOven(HighOvenLogic tile) {
		this.controller = tile;
	}

	/**
	 * Determine if structure is valid.
	 *
	 * @param x structure center x coordinate
	 * @param y controller y coordinate
	 * @param z structure center z coordinate
	 */
	@Override
	public void validateStructure(final int x, final int y, final int z) {
		final boolean wasValid = validStructure;
		final boolean structureHadBottom = structureHasBottom;
		final boolean structureHadTop = structureHasTop;
		final int oldNbLayers = nbLayers;

		nbLayers = 0;
		outputDucts.clear();
		if (checkHollowLayer(x, y, z)) {
			nbLayers++;

			structureHasTop = recurseStructureUp(x, y + 1, z);
			structureHasBottom = recurseStructureDown(x, y - 1, z);
		}

		validStructure = structureHasBottom && structureHasTop && nbLayers > 0;

		if (!wasValid && !validStructure) return;

		if (structureHadBottom != structureHasBottom || structureHadTop != structureHasTop || nbLayers != oldNbLayers) {
			controller.onStructureChange(this);
		}
	}

	/**
	 * Scan the controller layer of the structure for valid components.
	 *
	 * @param x coordinate of the center
	 * @param y y level of layer
	 * @param z coordinate of the center
	 * @return block count
	 */
	public boolean checkHollowLayer(final int x, final int y, final int z) {
		// Check the structure: (= unchecked, * checked)
		// ===
		// =*=
		// ===
		if (!controller.getWorldObj().isAirBlock(x, y, z)) {
			return false;
		}

		// ***
		// =*=
		// ***
		for (int xOffset = -1; xOffset <= 1; xOffset++) {
			if (!this.checkBricks(x + xOffset, y, z - 1) || !this.checkBricks(x + xOffset, y, z + 1))
				return false;
		}

		// ***
		// ***
		// ***
		return this.checkBricks(x - 1, y, z) && this.checkBricks(x + 1, y, z);
	}

	/**
	 * Determine if layer is a valid plain layer.
	 *
	 * @param x     coordinate of the center of the layer
	 * @param y     coordinate of the layer
	 * @param z     coordinate of the center of the layer
	 * @return the layer is valid
	 */
	public boolean checkPlainLayer(final int x, final int y, final int z) {
		for (int xOffset = -1; xOffset <= 1; xOffset++) {
			for (int zOffset = -1; zOffset <= 1; zOffset++) {
				if (controller.getWorldObj().getBlockMetadata(x + xOffset, y, z + zOffset) == 0 || !this.checkBricks(x + xOffset, y, z + zOffset))
					return false;
			}
		}

		return true;
	}

	/**
	 * Scan up the structure for valid components.
	 *
	 * @param x     coordinate from center
	 * @param y     coordinate from center
	 * @param z     coordinate from center
	 * @return the upper part of the structure is valid
	 */
	public boolean recurseStructureUp(final int x, final int y, final int z) {
		if (checkHollowLayer(x, y, z)) {
			nbLayers++;
			return this.recurseStructureUp(x, y + 1, z);
		}

		return checkPlainLayer(x, y, z);
	}

	/**
	 * Scan down the structure for valid components.
	 *
	 * @param x     coordinate from center
	 * @param y     coordinate from center
	 * @param z     coordinate from center
	 * @return the lower part of the structure is valid
	 */
	public boolean recurseStructureDown(final int x, final int y, final int z) {
		if (checkHollowLayer(x, y, z)) {
			nbLayers++;
			return this.recurseStructureUp(x, y - 1, z);
		}

		return checkPlainLayer(x, y, z);
	}

	/**
	 * Tells if the brick at the specified coordinates are valid for this structure
	 *
	 * @param x coordinate
	 * @param y coordinate
	 * @param z coordinate
	 * @return the brick is valid
	 */
	private boolean checkBricks(final int x, final int y, final int z) {
		final Block block = controller.getWorldObj().getBlock(x, y, z);

		if (!this.validBlock(block)) {
			return false;
		}

		final TileEntity te = controller.getWorldObj().getTileEntity(x, y, z);

		if (te == controller) {
			return true;
		}

		if (te instanceof IServantLogic) {
			final IServantLogic servant = (IServantLogic) te;

			if (servant.verifyMaster(controller, controller.getWorldObj()) || servant.setPotentialMaster(controller, controller.getWorldObj())) {
				if (servant instanceof HighOvenDuctLogic && ((HighOvenDuctLogic) servant).getMode() == HighOvenDuctLogic.MODE_OUTPUT) {
					addOutputDuct((HighOvenDuctLogic) servant);
				}

				return true;
			}
		}

		return false;
	}

	/**
	 * Determine if block is a valid highoven component.
	 *
	 * @param block the block
	 * @return valid
	 */
	private boolean validBlock(final Block block) {
		return block.equals(TSContent.highoven);
	}

	@Override
	public int getNbLayers() {
		return nbLayers;
	}

	@Override
	public boolean isValid() {
		return validStructure;
	}

	public void addOutputDuct(HighOvenDuctLogic duct) {
		if (outputDucts.contains(duct)) return;

		outputDucts.add(duct);
	}

	public void removeOutputDuct(HighOvenDuctLogic duct) {
		outputDucts.remove(duct);
	}

	public List<HighOvenDuctLogic> getOutputDucts() {
		return outputDucts;
	}
}