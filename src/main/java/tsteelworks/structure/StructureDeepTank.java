package tsteelworks.structure;

import mantle.world.CoordTuple;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import tconstruct.smeltery.TinkerSmeltery;
import tsteelworks.common.blocks.logic.DeepTankLogic;
import tsteelworks.common.core.TSContent;
import tsteelworks.lib.ConfigCore;

import java.util.*;

public class StructureDeepTank implements IStructure {
	/**
	 * The structure has bottom.
	 */
	private boolean structureHasBottom;

	/**
	 * The structure has top.
	 */
	private boolean structureHasTop;

	/**
	 * Update needed.
	 */
	private boolean needsUpdate;

	/**
	 * The structure is valid.
	 */
	private boolean validStructure;

	/**
	 * The bottom left corner of the tank (on a x-z axis)
	 */
	private CoordTuple borderPos;

	/**
	 * The number of blocks in the structure.
	 */
	private int numBricks;

	private int xWidth;
	private int zWidth;

	/**
	 * The amount of layers.
	 */
	private int layers;

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
		scanControllerLayer(x, y, z, logic.getRenderDirection());

		if (borderPos == null) {
			validStructure = false;
			return;
		}

		scanLayers();

		validStructure = true;

		logic.onStructureChange(this);
	}

	public void scanLayers() {

	}

	/**
	 * Used to get the structure width (x & z) and the position of the bottom left corner.
	 *
	 * @param x1 controller x coordinate
	 * @param y layer y pos
	 * @param z1 controller z coordinate
	 * @param orientation the controller orientation
	 */
	public void scanControllerLayer(int x1, int y, int z1, int orientation) {
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
		return layers;
	}

	public int getXWidth() {
		return xWidth;
	}

	public int getZWidth() {
		return zWidth;
	}
}