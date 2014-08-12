package tsteelworks.structure;

import mantle.world.CoordTuple;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.oredict.OreDictionary;
import tconstruct.smeltery.TinkerSmeltery;
import tsteelworks.common.blocks.logic.DeepTankLogic;
import tsteelworks.common.blocks.logic.TSMultiServantLogic;
import tsteelworks.common.core.TSContent;
import tsteelworks.lib.ConfigCore;

import java.util.*;

public class StructureDeepTank implements IStructure {
	/**
	 * The structure is valid.
	 */
	private boolean validStructure;

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

	private ItemStack glassType = null;

	/**
	 * The valid glass blocks permitted in the structure.
	 */
	private static List<ItemStack> glassBlocks = getRegisteredGlassIDs();

	private DeepTankLogic logic;

	public StructureDeepTank(DeepTankLogic tile) {
		this.logic = tile;
	}

	/**
	 * Gets the registered glass i ds.
	 *
	 * @return the registered glass i ds
	 */
	public static List<ItemStack> getRegisteredGlassIDs() {
		List<ItemStack> glasses = new ArrayList<>();

		// todo: add seared glass
		glasses.add(new ItemStack(TinkerSmeltery.lavaTank));
		glasses.add(new ItemStack(TinkerSmeltery.lavaTankNether));
		glasses.add(new ItemStack(TinkerSmeltery.clearGlass));
		glasses.add(new ItemStack(TinkerSmeltery.stainedGlassClear));
		glasses.add(new ItemStack(Blocks.glass));
		glasses.add(new ItemStack(Blocks.stained_glass));

		Collections.addAll(glasses, ConfigCore.modTankGlassBlocks);

		ArrayList<ItemStack> oreDict = OreDictionary.getOres("glass");
		glasses.addAll(oreDict);

		return glasses;
	}

	@Override
	public void validateStructure(int x, int y, int z) {
		scanControllerLayer(x, y, z);

		boolean wasValid = validStructure;
		validStructure = borderPos != null && areLayersValid();

		// don't update if the structure wasn't valid and is still not
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

		return scanPlainLayer(y);
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
			for (int z = 0; z < xWidth; z++) {
				if (!isValidBlock(borderPos.x + x, y, borderPos.z + z))
					return false;
			}
		}

		return true;
	}

	private boolean isValidBlock(int x, int y, int z) {
		Block block = logic.getWorldObj().getBlock(x, y, z);
		int metadata = logic.getWorldObj().getBlockMetadata(x, y, z);

		if (glassType != null) {
			if (glassType.getItemDamage() == metadata && block.equals(Block.getBlockFromItem(glassType.getItem())))
				return true;
		} else if (isValidGlass(block, metadata)) {
			glassType = new ItemStack(block, metadata);

			return true;
		}

		TileEntity te = logic.getWorldObj().getTileEntity(x, y, z);
		if (te instanceof TSMultiServantLogic) {
			TSMultiServantLogic servant = (TSMultiServantLogic) te;

			if (servant.hasMaster() && servant.verifyMaster(logic, logic.getWorldObj()))
				return true;
			else if (servant.setMaster(logic, logic.getWorldObj())) {
				return true;
			}
		}

		return false;
	}

	private boolean isValidGlass(Block block, int meta) {
		for (ItemStack glass : glassBlocks) {
			if (glass.getItemDamage() == meta && block.equals(Block.getBlockFromItem(glass.getItem()))) {
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
		} while(isBlockValid(logic.getWorldObj().getBlock(x2, y, z1)));
		x2--; // this block is invalid, rollback

		xWidth += x2 - x1;

		x2 = x1;
		do {
			x2--;
		} while(isBlockValid(logic.getWorldObj().getBlock(x2, y, z1)));
		x2++; // this block is invalid, rollback

		xWidth += x1 - x2;

		if (xWidth == 1)
			return;

		// get the z width
		zWidth = 1; // the controller is the first block
		int z2 = z1;

		do {
			z2++;
		} while(isBlockValid(logic.getWorldObj().getBlock(x1, y, z2)));
		z2--;

		zWidth += z2 - z1;
		z2 = z1;

		do {
			z2--;
		} while(isBlockValid(logic.getWorldObj().getBlock(x1, y, z2)));
		z2++;

		zWidth += z1 - z2;

		borderPos = new CoordTuple(x2, y, z2);
	}

	/**
	 * @return the block is valid for this build
	 */
	private boolean isBlockValid(Block block) {
		return block.equals(TSContent.highoven);
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
}