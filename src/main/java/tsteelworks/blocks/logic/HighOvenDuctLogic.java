package tsteelworks.blocks.logic;

import mantle.world.CoordTuple;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.IHopper;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import tsteelworks.common.TSRepo;
import tsteelworks.inventory.HighOvenDuctContainer;
import tsteelworks.lib.ConfigCore;
import tsteelworks.lib.IFacingLogic;
import tsteelworks.lib.IRedstonePowered;
import tsteelworks.util.InventoryHelper;

// TODO: Lots
public class HighOvenDuctLogic extends TSMultiServantLogic implements IFacingLogic, IRedstonePowered, IHopper {
	public static final int MODE_OXIDIZER = HighOvenLogic.SLOT_OXIDIZER;
	public static final int MODE_REDUCER = HighOvenLogic.SLOT_REDUCER;
	public static final int MODE_PURIFIER = HighOvenLogic.SLOT_PURIFIER;
	public static final int MODE_FUEL = HighOvenLogic.SLOT_FUEL;
	public static final int MODE_MELTABLE = HighOvenLogic.SLOT_FIRST_MELTABLE;
	public static final int MODE_OUTPUT = 5;

	byte direction = 0;

	/**
	 * The mode is used to determine if the duct is used
	 * to import item from the outer world to the high oven for modes between 0 and 4
	 * or to export item from the high oven to the outer world for mode 5.
	 *
	 * In case of import, the mode is also used to choose the destination slot of the items in high oven as follow:
	 * See class constants for values (MODE_)
	 */
	int mode = MODE_OXIDIZER;
	boolean redstoneActivated = false;

	//why not using TSInventoryLogic to manage the internal inventory? because this class already extends TSMultiServantLogic?
	//if so, We can use a delegated item that extends TSInventoryLogic - Could you elaborate, please?
	// TODO - Toops - 2014/04/26 - Check in wisthy-0 branch, I've done that on an "experimental" branch. If it suits you, I'll adapt it for the main branch and pull a request
	private ItemStack[] inventory = new ItemStack[9];

	private int transferCooldown = -1;

	/* ==================== Update ==================== */

	/*
	 * (non-Javadoc)
	 * @see net.minecraft.tileentity.TileEntity#updateEntity()
	 */
	@Override
	public void updateEntity() {
		if (this.worldObj != null && !this.worldObj.isRemote) {
			--this.transferCooldown;

			if (!this.isCoolingDown()) {
				this.setTransferCooldown(0);
				this.updateDuct();
			}
		}
	}

	/**
	 * method used to manage the "tick" update of the duct
	 *
	 * @return true: something has been done / false: nothing happened
	 */
	public boolean updateDuct() {
		boolean flag = false;
		if (this.worldObj != null && !this.worldObj.isRemote) {
			if (!isCoolingDown()) {
				setTransferCooldown(0);

				flag = insertItemToInventory();
				flag = suckItemsIntoDuct() || flag;

				if (flag) {
					setTransferCooldown(8);
					onInventoryChanged();
				}
			}
		}

		return flag;
	}

	/*
	 * (non-Javadoc)
	 * @see tsteelworks.blocks.logic.TSMultiServantLogic#canUpdate()
	 */
	@Override
	public boolean canUpdate() {
		return true;
	}

	/* ==================== Container ==================== */

	public Container getGuiContainer(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
		return new HighOvenDuctContainer(inventoryplayer, this);
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
		if (newMode == MODE_OUTPUT && !isOutputDuct())
			getHighOvenController().setOutputDuct(new CoordTuple(xCoord, yCoord, zCoord));
		else if (newMode != MODE_OUTPUT && isOutputDuct())
			getHighOvenController().setOutputDuct(null);

		mode = (newMode < MODE_OUTPUT) ? newMode : MODE_OUTPUT;
	}

	public HighOvenLogic getHighOvenController() {
		final CoordTuple pos = getMasterPosition();

		return (HighOvenLogic) worldObj.getTileEntity(pos.x, pos.y, pos.z);
	}

	boolean isOutputDuct() {
		CoordTuple ductPos = getHighOvenController().getOutputDuct();

		return ductPos != null && (mode == MODE_OUTPUT && ductPos.equalCoords(xCoord, yCoord, zCoord));
	}

	/**
	 * Trying to transfer one item from one of the internal stot into the High Oven
	 *
	 * @return true: item transfered / false: no item transfered
	 */
	private boolean insertItemToInventory() {
		// 2014/4/25 - reverse redstone logic: active signal stops transfers
		if (!hasValidMaster() || redstoneActivated)
			return false;
		final IInventory masterInventory = getOutputInventory();

		if (masterInventory == null)
			return false;
		else {
			for (int slot = 0; slot < getSizeInventory(); slot++)
				if (getStackInSlot(slot) != null) {
					final ItemStack copyStack = getStackInSlot(slot).copy();
					final ItemStack outputStack = insertStack(masterInventory, decrStackSize(slot, 1), getRenderDirection());

					if ((outputStack == null) || (outputStack.stackSize == 0)) {
						//all the content of decrStackSize(slot, 1) has been moved inside masterInventory
						masterInventory.onInventoryChanged();
						return true;
					}

					//we didn't manage to insert the item from the slot into the masterInv.
					//Putting back the item back into the origin slot
					setInventorySlotContents(slot, copyStack);
				}
			return false;
		}
	}


	/**
	 * Trying to transfer one item from attached inventory into one of the internal stot
	 *
	 * @param localInventory the local inventory
	 * @return true: item transfered / false: no item transfered
	 */
	boolean suckItemsIntoDuct() {
		if (mode == MODE_OUTPUT || redstoneActivated)
			return false;
		final IInventory inventory = getExternalInventory(this.getRenderDirection());

		if (inventory != null) {
			final byte side = 0;

			if ((inventory instanceof ISidedInventory) && (side > -1)) {
				final ISidedInventory isidedinventory = (ISidedInventory) inventory;
				final int[] slots = isidedinventory.getAccessibleSlotsFromSide(side);

				for (final int slot : slots)
					if (pullStackFromInventory(inventory, slot, side))
						return true;
			} else {
				final int j = inventory.getSizeInventory();
				if (j == 0)
					return false;
				for (int k = 0; k < j; ++k)
					if (pullStackFromInventory(inventory, k, side))
						return true;
			}
		} else if (ConfigCore.enableDuctVacuum) {
			final EntityItem entityitem = InventoryHelper.getItemEntityAtLocation(this.getWorldObj(), this.getXPos(), getYPos(), getZPos(), this.getRenderDirection());

			if (entityitem != null)
				return pullStackFromEntity(entityitem, mode);
		}

		return false;
	}

	/**
	 * trying to insert a stack from an outside inventory into the internal inventory
	 *
	 * @param inventory    the source inventory where we will take the stack from
	 * @param slot         the slot of the source inventory
	 * @param side         ??
	 * @param transferMode the mode that will determine where to put the stack in the target inventory
	 * @return true: item transfered / false: no item transfered
	 */
	private boolean pullStackFromInventory(IInventory inventory, int slot, int side) {
		final ItemStack itemstack = inventory.getStackInSlot(slot);

		if ((itemstack != null) && InventoryHelper.canExtractItemFromInventory(inventory, itemstack, slot, side))
			;
		{
			if (itemstack == null)
				return false; //TODO: Figure out why we're crashing without this? o_O
			// only reason I can see is that the itemstack become null between the check and here. Maybe you need a synchronize?


			final ItemStack itemstack1 = itemstack.copy();
			final ItemStack outputStack = insertStack(this, inventory.decrStackSize(slot, 1), -1);

			if ((outputStack == null) || (outputStack.stackSize == 0)) {
				inventory.onInventoryChanged();
				return true;
			}

			inventory.setInventorySlotContents(slot, itemstack1);
		}

		return false;
	}

	/**
	 * trying to insert an outside entity "item" into the internal inventory
	 *
	 * @param item         the item entity
	 * @param transferMode the mode that will determine where to put the item in the target inventory
	 * @return true: item transfered / false: no item transfered
	 */
	public boolean pullStackFromEntity(EntityItem item, int transferMode) {
		boolean flag = false;

		if (item == null)
			return false;
		else {
			final ItemStack itemstack = item.getEntityItem().copy();
			final ItemStack itemstack1 = insertStack(this, itemstack, -1);

			if ((itemstack1 != null) && (itemstack1.stackSize != 0))
				item.setEntityItemStack(itemstack1);
			else {
				flag = true;
				item.setDead();
			}
			return flag;
		}
	}

	// can iiventory be something else than "this"? - yes, it can be the high oven's inventory
	// my bad, I've found that myself yesterday too.

	/**
	 * Insert item from stack inside the iinventory
	 *
	 * @param inventory    the destination inventory
	 * @param stack        the source stack
	 * @param side         ??
	 * @param transferMode the mode of the duct. See HighOvenDuctLogic.mode
	 * @return the remaining stack after items have been pulled off of it into iinventory
	 */
	public ItemStack insertStack(IInventory inventory, ItemStack stack, int side) {

		if ((inventory instanceof ISidedInventory) && (side > -1)) {
			final ISidedInventory isidedinventory = (ISidedInventory) inventory;
			final int[] slot = isidedinventory.getAccessibleSlotsFromSide(side);

			for (int i = 0; (i < slot.length) && (stack != null) && (stack.stackSize > 0); ++i)
				stack = sendItemsToLocation(inventory, stack, slot[i], side);
		} else if (mode == MODE_MELTABLE)
			//The transfer mode for "meltable" match the slot 4 and 5
			for (int slot = 4; (slot < inventory.getSizeInventory()) && (stack != null) && (stack.stackSize > 0); slot += 1)
				stack = sendItemsToLocation(inventory, stack, slot, side);
		else if (mode == MODE_OUTPUT) {
			//The transfer modes other than "meltable" correspond to the same slot number.
			int k = inventory.getSizeInventory();

			for (int slot = 0; slot < k && stack != null && stack.stackSize > 0; ++slot)
				stack = sendItemsToLocation(inventory, stack, slot, side);
		} else if (mode < MODE_MELTABLE) {
			stack = sendItemsToLocation(inventory, stack, mode, side);
		}
		if ((stack != null) && (stack.stackSize == 0))
			stack = null;

		return stack;
	}

	/**
	 * add as much item (from stack) as possible inside the iinventory
	 *
	 * @param iinventory the destination inventory
	 * @param stack      the source stack
	 * @param slot       the destination slot of the items
	 * @param side       ??
	 * @return the remaining stack after items have been pulled off of it into iinventory
	 */
	private ItemStack sendItemsToLocation(IInventory iinventory, ItemStack stack, int slot, int side) {
		final ItemStack masterStack = iinventory.getStackInSlot(slot);

		if (InventoryHelper.canInsertItemToInventory(iinventory, stack, slot, side)) {
			boolean flag = false;

			if (masterStack == null) {
				final int max = Math.min(stack.getMaxStackSize(), iinventory.getInventoryStackLimit());
				if (max >= stack.stackSize) {
					iinventory.setInventorySlotContents(slot, stack);
					stack = null;
				} else
					iinventory.setInventorySlotContents(slot, stack.splitStack(max));
				flag = true;
			} else if (InventoryHelper.areItemStacksEqualItem(masterStack, stack)) {
				final int max = Math.min(stack.getMaxStackSize(), iinventory.getInventoryStackLimit());
				if (max > masterStack.stackSize) {
					final int l = Math.min(stack.stackSize, max - masterStack.stackSize);
					stack.stackSize -= l;
					masterStack.stackSize += l;
					flag = l > 0;
				}
			}
			if (flag)
				iinventory.onInventoryChanged();
		}
		return stack;
	}

	private IInventory getOutputInventory() {
		return (mode == MODE_OUTPUT) ? getExternalInventory(this.getRenderDirection()) : getHighOvenController();
	}

	public IInventory getExternalInventory(byte facing) {
		double checkXPos = this.getXPos();
		double checkYPos = this.getYPos();
		double checkZPos = this.getZPos();

		switch (facing) {
			case 0: // Down
				checkYPos = this.getYPos() - 1.0D;
				break;
			case 1: // Up
				checkYPos = this.getYPos() + 1.0D;
				break;
			case 2: // North
				checkZPos = this.getZPos() - 1.0D;
				break;
			case 3: // South
				checkZPos = this.getZPos() + 1.0D;
				break;
			case 4: // West
				checkXPos = this.getXPos() - 1.0D;
				break;
			case 5: // East
				checkXPos = this.getXPos() + 1.0D;
				break;
			default:
				break;
		}

		return InventoryHelper.getInventoryAtLocation(this.getWorldObj(), checkXPos, checkYPos, checkZPos);
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

	public boolean isCoolingDown() {
		return transferCooldown > 0;
	}

	/* ==================== IInventory ==================== */

	/*
	 * (non-Javadoc)
	 * @see net.minecraft.tileentity.TileEntity#onInventoryChanged()
	 */
	@Override
	public void onInventoryChanged() {
		//updateEntity();
		super.onInventoryChanged();
	}

	/*
	 * (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#getSizeInventory()
	 */
	@Override
	public int getSizeInventory() {
		return inventory.length;
	}

	/*
	 * (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#getStackInSlot(int)
	 */
	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventory[slot];
	}

	/*
	 * (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#decrStackSize(int, int)
	 */
	@Override
	public ItemStack decrStackSize(int slot, int quantity) {
		if (inventory[slot] != null) {
			if (inventory[slot].stackSize <= quantity) {
				final ItemStack stack = inventory[slot];
				inventory[slot] = null;
				return stack;
			}
			final ItemStack split = inventory[slot].splitStack(quantity);
			if (inventory[slot].stackSize == 0)
				inventory[slot] = null;
			return split;
		} else
			return null;
	}

	/*
	 * (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#getStackInSlotOnClosing(int)
	 */
	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#setInventorySlotContents(int, net.minecraft.item.ItemStack)
	 */
	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack) {
		inventory[slot] = itemstack;
		if ((itemstack != null) && (itemstack.stackSize > getInventoryStackLimit()))
			itemstack.stackSize = getInventoryStackLimit();
	}

	@Override
	public String getInventoryName() {
		return null;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#isUseableByPlayer(net.minecraft.entity.player.EntityPlayer)
	 */
	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		if (worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) != this)
			return false;
		else
			return entityplayer.getDistance(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64D;
	}

	@Override
	public void openInventory() {

	}

	@Override
	public void closeInventory() {

	}

	/*
	 * (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#getInvName()
	 */
	@Override
	public String getInvName() {
		return "container.HighOvenDuct";
	}

	/*
	 * (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#isInvNameLocalized()
	 */
	@Override
	public boolean isInvNameLocalized() {
		return (getInvName() != null) && (getInvName().length() > 0);
	}

	/*
	 * (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#getInventoryStackLimit()
	 */
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	/*
	 * (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#openChest()
	 */
	@Override
	public void openChest() {
	}

	/*
	 * (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#closeChest()
	 */
	@Override
	public void closeChest() {
	}

	/*
	 * (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#isItemValidForSlot(int, net.minecraft.item.ItemStack)
	 */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
		if (itemstack == null)
			return false;
		if (slot < getSizeInventory())
			if ((inventory[slot] == null) || ((itemstack.stackSize + inventory[slot].stackSize) <= getInventoryStackLimit()))
				return true;
		return false;
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
		if (pitch > 45)
			setDirection(1);
		else if (pitch < -45)
			setDirection(0);
		else {
			final int facing = MathHelper.floor_double((yaw / 360) + 0.5D) & 3;
			switch (facing) {
				case 0:
					setDirection(2);
					break;
				case 1:
					setDirection(5);
					break;
				case 2:
					setDirection(3);
					break;
				case 3:
					setDirection(4);
					break;
			}
		}
	}

	/* ==================== NBT ==================== */

	/*
	 * (non-Javadoc)
	 * @see tsteelworks.blocks.logic.TSMultiServantLogic#readFromNBT(net.minecraft.nbt.NBTTagCompound)
	 */
	@Override
	public void readFromNBT(NBTTagCompound tags) {
		mode = tags.getInteger("Mode");
		redstoneActivated = tags.getBoolean("RedstoneActivated");
		super.readFromNBT(tags);
		final NBTTagList itemList = tags.getTagList("Items");
		inventory = new ItemStack[getSizeInventory()];
		transferCooldown = tags.getInteger("TransferCooldown");
		setDirection(tags.getByte(TSRepo.NBTNames.direction));

		for (int iter = 0; iter < itemList.tagCount(); iter++) {
			final NBTTagCompound tagList = (NBTTagCompound) itemList.tagAt(iter);
			final byte slotID = tagList.getByte("Slot");
			if ((slotID >= 0) && (slotID < inventory.length))
				inventory[slotID] = ItemStack.loadItemStackFromNBT(tagList);
		}

	}

	/*
	 * (non-Javadoc)
	 * @see tsteelworks.blocks.logic.TSMultiServantLogic#writeToNBT(net.minecraft.nbt.NBTTagCompound)
	 */
	@Override
	public void writeToNBT(NBTTagCompound tags) {

		super.writeToNBT(tags);
		final NBTTagList nbttaglist = new NBTTagList();
		for (int iter = 0; iter < inventory.length; iter++)
			if (inventory[iter] != null) {
				final NBTTagCompound tagList = new NBTTagCompound();
				tagList.setByte("Slot", (byte) iter);
				inventory[iter].writeToNBT(tagList);
				nbttaglist.appendTag(tagList);
			}
		tags.setInteger("TransferCooldown", transferCooldown);
		tags.setTag("Items", nbttaglist);
		tags.setByte("Direction", this.getRenderDirection());
		tags.setBoolean("RedstoneActivated", this.getRSmode());
		tags.setInteger("Mode", mode);
	}

	/*
	 * (non-Javadoc)
	 * @see tsteelworks.blocks.logic.TSMultiServantLogic#getDescriptionPacket()
	 */
	@Override
	public Packet getDescriptionPacket() {
		final NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag);
	}

	/*
	 * (non-Javadoc)
	 * @see tsteelworks.blocks.logic.TSMultiServantLogic#onDataPacket(net.minecraft.network.INetworkManager, net.minecraft.network.packet.Packet132TileEntityData)
	 */
	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData packet) {
		readFromNBT(packet.data);
		onInventoryChanged();
		worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
	}


}