package tsteelworks.common.blocks.logic;

import mantle.blocks.iface.IFacingLogic;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.IHopper;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;
import nf.fr.ephys.cookiecore.helpers.BlockHelper;
import nf.fr.ephys.cookiecore.helpers.InventoryHelper;
import nf.fr.ephys.cookiecore.helpers.NBTHelper;
import nf.fr.ephys.cookiecore.util.SizeableInventory;
import tsteelworks.common.core.TSRepo;
import tsteelworks.lib.ConfigCore;
import tsteelworks.lib.IRedstonePowered;

public class HighOvenDuctLogic extends TSMultiServantLogic implements IFacingLogic, IRedstonePowered, IHopper {
	public static final int MODE_OXIDIZER = HighOvenLogic.SLOT_OXIDIZER;
	public static final int MODE_REDUCER = HighOvenLogic.SLOT_REDUCER;
	public static final int MODE_PURIFIER = HighOvenLogic.SLOT_PURIFIER;
	public static final int MODE_FUEL = HighOvenLogic.SLOT_FUEL;
	public static final int MODE_MELTABLE = HighOvenLogic.SLOT_FIRST_MELTABLE;
	public static final int MODE_OUTPUT = 5;

	private byte direction = 0;

	/**
	 * The mode is used to determine if the duct is used
	 * to import item from the outer world to the high oven for modes between 0 and 4
	 * or to export item from the high oven to the outer world for mode 5.
	 *
	 * In case of import, the mode is also used to choose the destination slot of the items in high oven as follow:
	 * See class constants for values (MODE_)
	 */
	private int mode = MODE_OXIDIZER;
	private boolean redstoneActivated = false;

	private SizeableInventory inventory = new SizeableInventory(9);

	private int transferCooldown = -1;

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void updateEntity() {
		if (redstoneActivated || this.worldObj == null || this.worldObj.isRemote || !hasMaster()) return;

		--this.transferCooldown;

		if (transferCooldown > 0) return;

		if (insertToHighOven() | suckItemsIntoDuct()) {
			setTransferCooldown(0);
			markDirty();
		}

		setTransferCooldown(20);
	}

	/* ==================== Redstone Logic ==================== */

	/**
	 * Get the current state of redstone-connected power
	 *
	 * @return Redstone powered state
	 */
	@Override
	public boolean getRSmode() {
		return redstoneActivated;
	}

	/**
	 * Set the redstone powered state
	 *
	 * @param mode true: powered / false: not powered
	 */
	@Override
	public void setRSmode(boolean mode) {
		redstoneActivated = mode;
	}

	/* ==================== Duct Logic ==================== */

	/**
	 * @return the mode of the duct.
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * changing the transfer mode of the duct
	 *
	 * @param newMode the new mode to use
	 */
	public void setMode(int newMode) {
		mode = newMode % MODE_OUTPUT;

		if (mode == MODE_OUTPUT && !isOutputDuct())
			getHighOvenController().setOutputDuct(this);
		else if (mode != MODE_OUTPUT && isOutputDuct())
			getHighOvenController().setOutputDuct(null);
	}

	public HighOvenLogic getHighOvenController() {
		return (HighOvenLogic) getMaster();
	}

	public boolean isOutputDuct() {
		return getHighOvenController() != null && getHighOvenController().getOutputDuct() == this;
	}

	/**
	 * Trying to transfer one item from one of the internal stot into the High Oven
	 *
	 * @return an item has been transfered
	 */
	private boolean insertToHighOven() {
		if (mode == MODE_OUTPUT) return false;

		final IInventory masterInventory = getHighOvenController();

		if (masterInventory == null)
			return false;

		for (int slot = 0; slot < getSizeInventory(); slot++) {
			ItemStack stack = getStackInSlot(slot);

			if (stack == null) continue;

			ItemStack copy = stack.copy();
			copy.stackSize = 1;

			if (InventoryHelper.insertItem(masterInventory, new int[] { mode }, copy)) {
				decrStackSize(slot, 1);
				return true;
			}
		}

		return false;
	}

	/**
	 * Trying to transfer one item from attached inventory into one of the internal stot
	 *
	 * @return true: item transfered / false: no item transfered
	 */
	boolean suckItemsIntoDuct() {
		if (mode == MODE_OUTPUT)
			return false;

		int[] adjCoords = BlockHelper.getAdjacentBlock(xCoord, yCoord, zCoord, direction);

		IInventory inventory = InventoryHelper.getBlockInventoryAt(worldObj, adjCoords[0], adjCoords[1], adjCoords[2]);

		if (inventory == null && ConfigCore.enableDuctVacuum)
			inventory = InventoryHelper.getEntityInventoryAt(worldObj, adjCoords[0], adjCoords[1], adjCoords[2]);

		if (inventory == null) return false;

		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);

			if (stack == null) continue;

			ItemStack copy = stack.copy();
			copy.stackSize = 1;

			if (InventoryHelper.insertItem(this, copy)) {
				inventory.decrStackSize(i, 1);

				return true;
			}
		}

		return false;
	}

	/* ==================== TileEntity ==================== */

	/*
	 * (non-Javadoc)
	 * @see net.minecraft.tileentity.Hopper#getXPos()
	 */
	@Override
	public double getXPos() {
		return xCoord;
	}

	/*
	 * (non-Javadoc)
	 * @see net.minecraft.tileentity.Hopper#getYPos()
	 */
	@Override
	public double getYPos() {
		return yCoord;
	}

	/*
	 * (non-Javadoc)
	 * @see net.minecraft.tileentity.Hopper#getZPos()
	 */
	@Override
	public double getZPos() {
		return zCoord;
	}

	public void setTransferCooldown(int value) {
		transferCooldown = value;
	}

	/* ==================== IInventory ==================== */

	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventory.getStackInSlot(slot);
	}

	@Override
	public ItemStack decrStackSize(int slot, int quantity) {
		ItemStack stack = inventory.decrStackSize(slot, quantity);

		markDirty();

		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		ItemStack stack = inventory.getStackInSlotOnClosing(slot);

		markDirty();

		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack) {
		inventory.setInventorySlotContents(slot, itemstack);

		markDirty();
	}

	@Override
	public String getInventoryName() {
		return "container.HighOvenDuct";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this && entityplayer.getDistance(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64D;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
		return true;
	}

	/* ==================== IFacingLogic ==================== */

	@Override
	public byte getRenderDirection() {
		return direction;
	}

	@Override
	public ForgeDirection getForgeDirection() {
		return ForgeDirection.VALID_DIRECTIONS[direction];
	}

	@Override
	public void setDirection(int side) {
		direction = (byte) side;
	}

	@Override
	public void setDirection(float yaw, float pitch, EntityLivingBase player) {
		direction = (byte) BlockHelper.orientationToMetadataXZ(yaw);
	}

	/* ==================== NBT ==================== */

	@Override
	public void readCustomNBT(NBTTagCompound tags) {
		super.readCustomNBT(tags);
		inventory.readFromNBT(tags.getCompoundTag("inventory"));

		mode = tags.getInteger("Mode");
		redstoneActivated = tags.getBoolean("RedstoneActivated");
		setDirection(tags.getByte(TSRepo.NBTNames.direction));
	}

	@Override
	public void writeCustomNBT(NBTTagCompound tags) {
		super.writeCustomNBT(tags);
		NBTHelper.setWritable(tags, "inventory", inventory);

		tags.setByte("Direction", this.getRenderDirection());
		tags.setBoolean("RedstoneActivated", this.getRSmode());
		tags.setInteger("Mode", mode);
	}
}