package tsteelworks.structure;

import mantle.world.CoordTuple;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import tsteelworks.blocks.logic.HighOvenDuctLogic;
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
	 * The coordinates of the structure's absolute center position.
	 */
	private CoordTuple centerPos;

	/**
	 * The amount of blocks in the structure.
	 */
	private int numBricks;

	/**
	 * The amount of layers.
	 */
	private int nbLayers;

	private Random rand = new Random();

	/**
	 * Determine if structure is valid.
	 *
	 * @param x coordinate from controller
	 * @param y coordinate from controller
	 * @param z coordinate from controller
	 */
	public void checkValidStructure(final int x, final int y, final int z) {
		// TSteelworks.loginfo("HOL - checkValidStructure(x="+x+", y="+y+", z="+z+")");
        /*
         * store old validation variables
         */
		final boolean oldStructureHasBottom = this.structureHasBottom;
		final boolean oldStructureHasTop = this.structureHasTop;
		// boolean oldValidStructure = validStructure;

        /*
         * reset all validation variables
         */
		this.structureHasBottom = false;
		this.structureHasTop = false;
		// validStructure = false;

		int checkedLayers = 0;

		if (this.checkSameLevel(x, y, z)) {
			// TSteelworks.loginfo("HOL - checkValidStructure - same level ok");
			checkedLayers++;
			checkedLayers += this.recurseStructureUp(x, y + 1, z, 0);
			// TSteelworks.loginfo("HOL - checkValidStructure - up: "+checkedLayers);
			checkedLayers += this.recurseStructureDown(x, y - 1, z, 0);
			// TSteelworks.loginfo("HOL - checkValidStructure - down: "+checkedLayers);
		}

		// TSteelworks.loginfo("HOL - checkValidStructure - hasBottom: "+structureHasBottom);
		// TSteelworks.loginfo("HOL - checkValidStructure - oldHasBottom: "+oldStructureHasBottom);

		// TSteelworks.loginfo("HOL - checkValidStructure - hasTop: "+structureHasTop);
		// TSteelworks.loginfo("HOL - checkValidStructure - oldHasTop: "+oldStructureHasTop);

		// TSteelworks.loginfo("HOL - checkValidStructure - oldLayers: "+this.nbLayers);

		if ((oldStructureHasBottom != this.structureHasBottom) || (oldStructureHasTop != this.structureHasTop) || (this.nbLayers != checkedLayers)) {
			if (this.structureHasBottom && this.structureHasTop && (checkedLayers > 0)) {
				this.adjustLayers(checkedLayers, false);
				this.validStructure = true;
			} else {
				this.internalTemp = ROOM_TEMP;
				this.validStructure = false;
			}
			worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
		}
	}

	/**
	 * Begin structure alignment.
	 *
	 * @param x coordinate from controller
	 * @param y coordinate from controller
	 * @param z coordinate from controller
	 */
	public void alignInitialPlacement(final int x, final int y, final int z) {
		this.checkValidStructure(x, y, z);
	}

	/**
	 * Scan the controller layer of the structure for valid components.
	 *
	 * @param x coordinate from center
	 * @param y coordinate from center
	 * @param z coordinate from center
	 * @return block count
	 */
	public boolean checkSameLevel(final int x, final int y, final int z) {
		this.numBricks = 0;

		// Check inside
		for (int xPos = x; xPos <= x; xPos++) {
			for (int zPos = z; zPos <= z; zPos++) {
				if (!worldObj.isAirBlock(xPos, y, zPos)) {
					return false;
				}
			}
		}
		// Check outer layer
		// Scans in a swastica-like pattern
		for (int xPos = x - 1; xPos <= (x + 1); xPos++) {
			this.numBricks += this.checkBricks(xPos, y, z - 1);
			this.numBricks += this.checkBricks(xPos, y, z + 1);
		}
		for (int zPos = z; zPos <= z; zPos++) {
			this.numBricks += this.checkBricks(x - 1, y, zPos);
			this.numBricks += this.checkBricks(x + 1, y, zPos);
		}
		return this.numBricks == 8;
	}

	/**
	 * Scan up the structure for valid components.
	 *
	 * @param x     coordinate from center
	 * @param y     coordinate from center
	 * @param z     coordinate from center
	 * @param count current amount of blocks
	 * @return block count
	 */
	public int recurseStructureUp(final int x, final int y, final int z, final int count) {
		this.numBricks = 0;
		int increment = count;
		// Check inside
		for (int xPos = x; xPos <= x; xPos++) {
			for (int zPos = z; zPos <= z; zPos++) {
				final Block block = worldObj.getBlock(xPos, y, zPos);

				if (!block.isAir(worldObj, xPos, y, zPos)) {
					if (this.validBlockID(block)) {
						return this.validateTop(x, y, z, increment);
					}

					return increment;
				}
			}
		}

		// Check outer layer
		for (int xPos = x - 1; xPos <= (x + 1); xPos++) {
			this.numBricks += this.checkBricks(xPos, y, z - 1);
			this.numBricks += this.checkBricks(xPos, y, z + 1);
		}

		for (int zPos = z; zPos <= z; zPos++) {
			this.numBricks += this.checkBricks(x - 1, y, zPos);
			this.numBricks += this.checkBricks(x + 1, y, zPos);
		}

		if (this.numBricks != 8) {
			return increment;
		}

		increment++;
		return this.recurseStructureUp(x, y + 1, z, increment);
	}

	/**
	 * Scan down the structure for valid components.
	 *
	 * @param x     coordinate from center
	 * @param y     coordinate from center
	 * @param z     coordinate from center
	 * @param count current amount of blocks
	 * @return block count
	 */
	public int recurseStructureDown(final int x, final int y, final int z, final int count) {
		this.numBricks = 0;
		int increment = count;
		// Check inside
		for (int xPos = x; xPos <= x; xPos++) {
			for (int zPos = z; zPos <= z; zPos++) {
				final Block block = worldObj.getBlock(xPos, y, zPos);

				if ((block != null) && !block.isAir(worldObj, xPos, y, zPos)) {
					if (this.validBlockID(block)) {
						return this.validateBottom(x, y, z, increment);
					}

					return increment;
				}
			}
		}

		// Check outer layer X
		for (int xPos = x - 1; xPos <= (x + 1); xPos++) {
			this.numBricks += this.checkBricks(xPos, y, z - 1);
			this.numBricks += this.checkBricks(xPos, y, z + 1);
		}

		// Check outer layer Z
		for (int zPos = z; zPos <= z; zPos++) {
			this.numBricks += this.checkBricks(x - 1, y, zPos);
			this.numBricks += this.checkBricks(x + 1, y, zPos);
		}

		if (this.numBricks != 8) {
			return increment;
		}

		increment++;
		return this.recurseStructureDown(x, y - 1, z, increment);
	}

	/**
	 * Determine if layer is a valid top layer.
	 *
	 * @param x     coordinate from center
	 * @param y     coordinate from center
	 * @param z     coordinate from center
	 * @param count current amount of blocks
	 * @return block count
	 */
	public int validateTop(final int x, final int y, final int z, final int count) {
		int topBricks = 0;
		for (int xPos = x - 1; xPos <= (x + 1); xPos++) {
			for (int zPos = z - 1; zPos <= (z + 1); zPos++) {
				if (this.validBlockID(worldObj.getBlock(xPos, y, zPos)) && (worldObj.getBlockMetadata(xPos, y, zPos) >= 1)) {
					topBricks += this.checkBricks(xPos, y, zPos);
				}
			}
		}

		this.structureHasTop = topBricks == 9;
		return count;
	}

	/**
	 * Determine if layer is a valid bottom layer.
	 *
	 * @param x     coordinate from center
	 * @param y     coordinate from center
	 * @param z     coordinate from center
	 * @param count current amount of blocks
	 * @return block count
	 */
	public int validateBottom(final int x, final int y, final int z, final int count) {
		int bottomBricks = 0;
		for (int xPos = x - 1; xPos <= (x + 1); xPos++) {
			for (int zPos = z - 1; zPos <= (z + 1); zPos++) {
				if (this.validBlockID(this.worldObj.getBlock(xPos, y, zPos)) && (this.worldObj.getBlockMetadata(xPos, y, zPos) >= 1)) {
					bottomBricks += this.checkBricks(xPos, y, zPos);
				}
			}
		}

		this.structureHasBottom = bottomBricks == 9;
		if (this.structureHasBottom) {
			this.centerPos = new CoordTuple(x, y + 1, z);
		}

		return count;
	}

	/**
	 * Increments bricks, sets them as part of the structure.
	 *
	 * @param x coordinate
	 * @param y coordinate
	 * @param z coordinate
	 * @return int brick incement
	 */
	private int checkBricks(final int x, final int y, final int z) {
		int tempBricks = 0;
		final Block block = this.worldObj.getBlock(x, y, z);

		if (this.validBlockID(block)) {
			final TileEntity te = this.worldObj.getTileEntity(x, y, z);

			if (te == this) {
				tempBricks++;
			} else if (te instanceof IServantLogic) {
				final IServantLogic servant = (IServantLogic) te;

				if (servant.hasMaster() && servant.verifyMaster(this, worldObj)) {
					tempBricks++;
				} else if (servant.setMaster(this, worldObj)) {
					tempBricks++;
				}
			}
		}

		return tempBricks;
	}

	/**
	 * Determine if block is a valid highoven component.
	 *
	 * @param block the block
	 * @return valid
	 */
	private boolean validBlockID(final Block block) {
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
}