package tsteelworks.structure;

import mantle.world.CoordTuple;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import tsteelworks.blocks.logic.HighOvenDuctLogic;
import tsteelworks.blocks.logic.HighOvenLogic;
import tsteelworks.common.TSContent;
import tsteelworks.lib.IServantLogic;

import java.util.Random;

public class StructureHighOven {
	/**
	 * Used to determine if the structure has a bottom.
	 */
	private boolean structureHasBottom;

	/**
	 * Used to determine if the structure has a top.
	 */
	private boolean structureHasTop;

	/**
	 * Used to determine if the structure is valid.
	 */
	private boolean validStructure;

	/**
	 * The structure's output duct instance.
	 */
	private HighOvenDuctLogic outputDuct;

	/**
	 * The amount of layers.
	 */
	private int nbLayers;

	private Random rand = new Random();

	private HighOvenLogic controller;

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
	public void checkValidStructure(final int x, final int y, final int z) {
		final boolean structureHadBottom = structureHasBottom;
		final boolean structureHadTop = structureHasTop;
		final int oldNbLayers = nbLayers;

		nbLayers = 0;
		if (checkHollowLayer(x, y, z)) {
			nbLayers++;

			structureHasTop = recurseStructureUp(x, y + 1, z);
			structureHasBottom = recurseStructureDown(x, y - 1, z);
		}

		validStructure = structureHasBottom && structureHasTop && nbLayers > 0;

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

			if ((servant.hasMaster() && servant.verifyMaster(controller, controller.getWorldObj())) || servant.setMaster(controller, controller.getWorldObj())) {
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

	/**
	 * Gets the output duct.
	 *
	 * @return the output duct
	 */
	public final CoordTuple getOutputDuct() {
		return this.outputDuct;
	}

	/**
	 * Sets the output duct.
	 *
	 * @param duct the new output duct
	 */
	public final void setOutputDuct(final CoordTuple duct) {
		this.outputDuct = duct;
	}

	/**
	 * Adjust Layers for inventory containment.
	 *
	 * @param lay         Layer
	 * @param forceAdjust the force adjust
	 */
	private void adjustLayers(final int lay, final boolean forceAdjust) {
		if (lay == this.nbLayers && !forceAdjust)
			return;

		this.needsUpdate = true;

		this.nbLayers = lay;
		this.tank.setCapacity(FLUID_AMOUNT_PER_LAYER * lay);
		this.maxTemp = this.maxTempByLayer();

		final int[] tempActive = this.activeTemps;
		this.activeTemps = new int[SLOT_FIRST_MELTABLE + lay];
		final int activeLength = tempActive.length > this.activeTemps.length ? this.activeTemps.length : tempActive.length;
		System.arraycopy(tempActive, 0, this.activeTemps, 0, activeLength);

		final int[] tempMelting = this.meltingTemps;
		this.meltingTemps = new int[SLOT_FIRST_MELTABLE + lay];
		final int meltingLength = tempMelting.length > this.meltingTemps.length ? this.meltingTemps.length : tempMelting.length;
		System.arraycopy(tempMelting, 0, this.meltingTemps, 0, meltingLength);

		final ItemStack[] tempInv = this.smeltableInventory.stacks;
		this.smeltableInventory.stacks = new ItemStack[SLOT_FIRST_MELTABLE + lay];
		final int invLength = tempInv.length > this.smeltableInventory.stacks.length ? this.smeltableInventory.stacks.length : tempInv.length;
		System.arraycopy(tempInv, 0, this.smeltableInventory.stacks, 0, invLength);

		if (this.activeTemps.length > 0 && this.activeTemps.length > tempActive.length) {
			for (int i = tempActive.length; i < this.activeTemps.length; i++) {
				if (!this.isSmeltingSlot(i))
					continue;

				this.activeTemps[i] = ROOM_TEMP;
				this.meltingTemps[i] = ROOM_TEMP;
			}
		}

		if (tempInv.length > this.smeltableInventory.getSizeInventory()) {
			for (int i = this.smeltableInventory.getSizeInventory(); i < tempInv.length; i++) {
				final ItemStack stack = tempInv[i];
				if (stack != null) {
					final float jumpX = (this.rand.nextFloat() * 0.8F) + 0.1F;
					final float jumpY = (this.rand.nextFloat() * 0.8F) + 0.1F;
					final float jumpZ = (this.rand.nextFloat() * 0.8F) + 0.1F;
					int offsetX = 0;
					int offsetZ = 0;

					switch (this.getRenderDirection()) {
						case 2: // +z
							offsetZ = -1;
							break;
						case 3: // -z
							offsetZ = 1;
							break;
						case 4: // +x
							offsetX = -1;
							break;
						case 5: // -x
							offsetX = 1;
							break;
						default:
							break;
					}

					while (stack.stackSize > 0) {
						int itemSize = this.rand.nextInt(21) + 10;
						if (itemSize > stack.stackSize) {
							itemSize = stack.stackSize;
						}
						stack.stackSize -= itemSize;
						final EntityItem entityitem = new EntityItem(this.worldObj, this.xCoord + jumpX + offsetX, this.yCoord + jumpY, this.zCoord + jumpZ + offsetZ, new ItemStack(stack.getItem(), itemSize, stack.getItemDamage()));

						if (stack.hasTagCompound()) {
							entityitem.getEntityItem().setTagCompound((NBTTagCompound) stack.getTagCompound().copy());
						}
						final float offset = 0.05F;
						entityitem.motionX = (float) this.rand.nextGaussian() * offset;
						entityitem.motionY = ((float) this.rand.nextGaussian() * offset) + 0.2F;
						entityitem.motionZ = (float) this.rand.nextGaussian() * offset;
						this.worldObj.spawnEntityInWorld(entityitem);
					}
				}
			}
		}
	}

	public int getNbLayers() {
		return nbLayers;
	}

	public boolean isValid() {
		return validStructure;
	}

	public HighOvenDuctLogic getDuct() {
		return outputDuct;
	}
}