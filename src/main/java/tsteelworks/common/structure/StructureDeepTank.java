package tsteelworks.common.structure;

import mantle.world.CoordTuple;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import tsteelworks.common.blocks.logic.DeepTankLogic;
import tsteelworks.common.blocks.logic.TSMultiServantLogic;
import tsteelworks.common.core.TSContent;
import tsteelworks.lib.registry.DeepTankGlassTypes;

public class StructureDeepTank implements IStructure {
	/**
	 * The structure is valid.
	 */
	private boolean validStructure = false;

	/**
	 * The bottom left corner of the tank (on a x-z axis)
	 */
	private CoordTuple borderPos;

	private int xWidth;
	private int zWidth;

	/**
	 * The amount of layers. This does not count the top & bottom
	 */
	private int nbLayers;

	private DeepTankGlassTypes.GlassType glassType = null;
	private int glassCapacity = 0;

	/**
	 * Used to check is a glass type is valid, so we don't instantiate 50k GlassType
	 */
	private DeepTankGlassTypes.GlassType glassChecker = new DeepTankGlassTypes.GlassType(null, 0);

	private DeepTankLogic logic;

	public StructureDeepTank(DeepTankLogic tile) {
		this.logic = tile;
	}

	@Override
	public void validateStructure(int x, int y, int z) {
		final boolean wasValid = validStructure;

		scanControllerLayer(x, y, z);

		validStructure = borderPos != null && areLayersValid();

		if (!validStructure) {
			nbLayers = 0;
		}

		// don't update if the structure wasn't valid and still is not
		if (wasValid || validStructure)
			logic.onStructureChange(this);
	}

	private boolean areLayersValid() {
		glassType = null;
		nbLayers = 0;

		return scanPlainLayer(borderPos.y) && (recursiveScanDown(borderPos.y - 1) || recursiveScanUp(borderPos.y + 1));
	}

	private boolean recursiveScanDown(int y) {
		if (scanHollowLayer(y)) {
			nbLayers++;
			return recursiveScanDown(y - 1);
		}

		boolean valid = scanPlainLayer(y);

		// replace the corner at the bottom of the structure
		if (valid)
			borderPos = new CoordTuple(borderPos.x, y, borderPos.z);

		return valid;
	}

	private boolean recursiveScanUp(int y) {
		if (scanHollowLayer(y)) {
			nbLayers++;
			return recursiveScanUp(y + 1);
		}

		return scanPlainLayer(y);
	}

	/**
	 * Checks the layer is hollow and the outer line is valid glass
	 */
	private boolean scanHollowLayer(int y) {
		// check inner section is empty
		for (int x = 1; x < xWidth - 1; x++) {
			for (int z = 1; z < zWidth - 1; z++) {
				Block block = logic.getWorldObj().getBlock(borderPos.x + x, y, borderPos.z + z);

				if (!block.getMaterial().equals(Material.air)) return false;
			}
		}

		// check outer section is filled with glass
		for (int x = 0; x < xWidth; x++) {
			if (!isValidBlock(borderPos.x + x, y, borderPos.z) || !isValidBlock(borderPos.x + x, y, borderPos.z + zWidth - 1))
				return false;
		}

		for (int z = 1; z < zWidth - 1; z++) {
			if (!isValidBlock(borderPos.x, y, borderPos.z + z) || !isValidBlock(borderPos.x + xWidth - 1, y, borderPos.z + z))
				return false;
		}

		return true;
	}

	/**
	 * Checks the whole layer is filled with valid bricks
	 */
	private boolean scanPlainLayer(int y) {
		for (int x = 0; x < xWidth; x++) {
			for (int z = 0; z < zWidth; z++) {
				if (!isValidBlock(borderPos.x + x, y, borderPos.z + z)) {
					return false;
				}
			}
		}

		return true;
	}

	private boolean isValidBlock(int x, int y, int z) {
		Block block = logic.getWorldObj().getBlock(x, y, z);
		int metadata = logic.getWorldObj().getBlockMetadata(x, y, z);

		glassChecker.setBlock(block);
		glassChecker.setMetadata(metadata);

		if (block.equals(TSContent.highoven)) {
			return verifyTile(x, y, z);
		}

		if (glassType != null && glassType.equals(glassChecker)) {
			return true;
		} else {
			Integer capacity = DeepTankGlassTypes.getBlockCapacity(glassChecker);

			if (capacity != null) {
				// the capacity is always equal to the capacity of the weakest glass
				if (glassType == null || capacity < glassCapacity) {
					glassType = new DeepTankGlassTypes.GlassType(glassChecker);
					glassCapacity = capacity;
				}

				return true;
			}
		}

		return false;
	}

	private boolean verifyTile(int x, int y, int z) {
		TileEntity te = logic.getWorldObj().getTileEntity(x, y, z);

		if (te == logic)
			return true;

		if (te == null) {
			te = TSMultiServantLogic.newInstance(logic.getWorldObj(), x, y, z);
		}

		if (te instanceof TSMultiServantLogic) {
			TSMultiServantLogic servant = (TSMultiServantLogic) te;

			if (servant.verifyMaster(logic, logic.getWorldObj()) || servant.setPotentialMaster(logic, logic.getWorldObj())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Used to get the structure width (x & z) and the position of the bottom left corner.
	 *
	 * @param x1 controller x coordinate
	 * @param y layer y pos
	 * @param z1 controller z coordinate
	 */
	private void scanControllerLayer(int x1, int y, int z1) {
		borderPos = null;

		// get the x width
		xWidth = 1; // the controller is the first block
		int x2 = x1;

		do {
			x2++;
		} while(isValidBlock(x2, y, z1));
		x2--; // this block is invalid, rollback

		xWidth += x2 - x1;

		x2 = x1;
		do {
			x2--;
		} while(isValidBlock(x2, y, z1));
		x2++; // this block is invalid, rollback

		xWidth += x1 - x2;

		if (xWidth < 3)
			return;

		// get the z width
		zWidth = 1; // the controller is the first block
		int z2 = z1;

		do {
			z2++;
		} while(isValidBlock(x1, y, z2));
		z2--;

		zWidth += z2 - z1;
		z2 = z1;

		do {
			z2--;
		} while(isValidBlock(x1, y, z2));
		z2++;

		zWidth += z1 - z2;

		if (zWidth < 3)
			return;

		borderPos = new CoordTuple(x2, y, z2);
	}

	@Override
	public boolean isValid() {
		return validStructure;
	}

	@Override
	public int getNbLayers() {
		return nbLayers;
	}

	public int getXWidth() {
		return xWidth;
	}

	public int getZWidth() {
		return zWidth;
	}

	public CoordTuple getBorderPos() {
		return borderPos;
	}

	/**
	 * Used to sync with clients
	 */
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("layers", nbLayers);
		nbt.setBoolean("isValid", validStructure);

		if (!validStructure) return;
		nbt.setInteger("xSize", xWidth);
		nbt.setInteger("zSize", zWidth);

		nbt.setInteger("capacity", glassCapacity);

		nbt.setInteger("borderX", borderPos.x);
		nbt.setInteger("borderY", borderPos.y);
		nbt.setInteger("borderZ", borderPos.z);
	}

	/**
	 * Used to sync with clients
	 */
	public void readFromNBT(NBTTagCompound nbt) {
		nbLayers = nbt.getInteger("layers");
		validStructure = nbt.getBoolean("isValid");

		xWidth = nbt.getInteger("xSize");
		zWidth = nbt.getInteger("zSize");

		glassCapacity = nbt.getInteger("capacity");

		int borderX = nbt.getInteger("borderX");
		int borderY = nbt.getInteger("borderY");
		int borderZ = nbt.getInteger("borderZ");

		borderPos = new CoordTuple(borderX, borderY, borderZ);
	}

	public int getGlassCapacity() {
		return glassCapacity;
	}

	/* ============== Structure check ============== */

	int blockPos = 0;

	public void checkBlock() {
		if (!validStructure || logic.getWorldObj().isRemote) return;

		int nbBlocksPerXYSlice = (nbLayers + 2) * xWidth;

		int zOffset = blockPos / nbBlocksPerXYSlice;

		int xySlice = blockPos % nbBlocksPerXYSlice;

		int yOffset = xySlice / xWidth;

		int xOffset = xySlice % xWidth;

		if (++blockPos >= xWidth * zWidth * (nbLayers + 2))
			blockPos = 0;

		boolean shouldBeFilled =
				xOffset == 0 || xOffset == (xWidth - 1) ||
				yOffset == 0 || yOffset == nbLayers + 1 ||
				zOffset == 0 || zOffset == (zWidth - 1);

		if ((shouldBeFilled && !isValidBlock(borderPos.x + xOffset, borderPos.y + yOffset, borderPos.z + zOffset))
				|| (!shouldBeFilled && !logic.getWorldObj().isAirBlock(borderPos.x + xOffset, borderPos.y + yOffset, borderPos.z + zOffset))) {
			invalidate();
		}
	}

	public void invalidate() {
		if (!validStructure) return;

		validStructure = false;
		nbLayers = 0;
		glassCapacity = 0;

		logic.onStructureChange(this);
	}
}