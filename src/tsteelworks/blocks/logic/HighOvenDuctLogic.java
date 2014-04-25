package tsteelworks.blocks.logic;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
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
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.Hopper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import tconstruct.library.util.CoordTuple;
import tconstruct.library.util.IFacingLogic;
import tsteelworks.inventory.HighOvenDuctContainer;
import tsteelworks.lib.ConfigCore;

// TODO: Lots
public class HighOvenDuctLogic extends TSMultiServantLogic implements IFacingLogic, Hopper
{
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
	 * 0 = ?, 
	 * 1 = ?, 
	 * 2 = ?, 
	 * 3 = ?, 
	 * 4 = meltable (ex: iron ingot)
	 */
	int mode = MODE_OXIDIZER;
	boolean redstoneActivated = false;

	//why not using TSInventoryLogic to manage the internal inventory? because this class already extends TSMultiServantLogic? 
	//if so, We can use a delegated item that extends TSInventoryLogic 
	// => delegated TSInventoryLogic created on branch wisthy-0
	private ItemStack[] inventory = new ItemStack[9];

	private int transferCooldown = -1;

	/* ==================== Update ==================== */

	/*
	 * (non-Javadoc)
	 * @see net.minecraft.tileentity.TileEntity#updateEntity()
	 */
	@Override
	public void updateEntity ()
	{
		//        if (worldObj == null) return;
		//        --transferCooldown;
		//        updateDuct();
		if (this.worldObj != null && !this.worldObj.isRemote)
		{
			--this.transferCooldown;

			if (!this.isCoolingDown())
			{
				this.setTransferCooldown(0);
				this.updateDuct();
			}
		}
	}

	/**
	 * method used to manage the "tick" update of the duct
	 * @return true: something has been done / false: nothing happened
	 */
	public boolean updateDuct ()
	{
		if (this.worldObj != null && !this.worldObj.isRemote)
		{
			if (!isCoolingDown())
			{
				setTransferCooldown(0);

				boolean flag = insertItemToInventory();
				flag = suckItemsIntoDuct() || flag;

				if (flag)
				{
					setTransferCooldown(8);
					onInventoryChanged();
					return true;
				}   
				else
					return false;
			}
			else
				return false;
		}
		else
			return false;
	}

	/*
	 * (non-Javadoc)
	 * @see tsteelworks.blocks.logic.TSMultiServantLogic#canUpdate()
	 */
	@Override
	public boolean canUpdate ()
	{
		return true;
	}

	/* ==================== Container ==================== */

	public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
	{
		return new HighOvenDuctContainer(inventoryplayer, this);
	}

	/* ==================== Redstone Logic ==================== */

	/**
	 * Get the current state of redstone-connected power
	 * 
	 * @return Redstone powered state
	 */
	public boolean getRedstoneActive ()
	{
		return redstoneActivated;
	}

	/**
	 * Set the redstone powered state
	 * 
	 * @param flag
	 *          true: powered / false: not powered
	 */
	public void setRedstoneActive (boolean flag)
	{
		redstoneActivated = flag;
	}

	/* ==================== Duct Logic ==================== */

	/**
	 * @return the mode of the duct.
	 */
	public int getMode ()
	{
		return mode;
	}

	/**
	 * changing the transfer mode of the duct
	 * 
	 * @param newMode 
	 * 			the new mode to use
	 */
	public void setMode (int newMode)
	{
		mode = (newMode < 6) ? newMode : MODE_OUTPUT;
		if (mode == MODE_OUTPUT)
			getHighOvenController().outputDuct = new CoordTuple(xCoord, yCoord, zCoord);
	}


	public HighOvenLogic getHighOvenController ()
	{
		final int mx = getMasterPosition().x;
		final int my = getMasterPosition().y;
		final int mz = getMasterPosition().z;
		return (HighOvenLogic) worldObj.getBlockTileEntity(mx, my, mz);
	}

	/**
	 * Trying to transfer one item from one of the internal stot into the High Oven 
	 * 
	 * @return 
	 * 				true: item transfered / false: no item transfered
	 */
	private boolean insertItemToInventory ()
	{
		if (!hasValidMaster() || !redstoneActivated)
			return false;
		final IInventory destinationInventory = getOutputInventory();

		if (destinationInventory == null)
			return false;
		else
		{
			for (int slot = 0; slot < getSizeInventory(); slot++)
				if (getStackInSlot(slot) != null)
				{
					final ItemStack copyStack = getStackInSlot(slot).copy();
					//final ItemStack outputStack = insertStack(destinationInventory, decrStackSize(slot, 1), getRenderDirection(), mode);
					final ItemStack outputStack = insertStack(destinationInventory, decrStackSize(slot, 1), getRenderDirection());

					if ((outputStack == null) || (outputStack.stackSize == 0))
					{
						//all the content of decrStackSize(slot, 1) has been moved inside masterInventory
						destinationInventory.onInventoryChanged();
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
	 * @param localInventory
	 * 			the local inventory
	 * @return 
	 * 			true: item transfered / false: no item transfered
	 */
	public boolean suckItemsIntoDuct ()
	{
		if (mode == MODE_OUTPUT || !redstoneActivated)
			return false;
		final IInventory outsideInventory = getExternalInventory(direction);

		if (outsideInventory != null)
		{
			final byte side = 0;

			if ((outsideInventory instanceof ISidedInventory) && (side > -1))
			{
				final ISidedInventory isidedinventory = (ISidedInventory) outsideInventory;
				final int[] slots = isidedinventory.getAccessibleSlotsFromSide(side);

				for (final int slot : slots)
					if (insertStackFromInventory(this, outsideInventory, slot, side))
						return true;
			}
			else
			{
				final int j = outsideInventory.getSizeInventory();

				for (int k = 0; k < j; ++k)
					if (insertStackFromInventory(this, outsideInventory, k, side))
						return true;
			}
		}
		else if (ConfigCore.enableDuctVacuum)
		{
			final EntityItem entityitem = getExternalItemEntity(this, direction);
			

			if (entityitem != null)
				return insertStackFromEntity(entityitem);
		}

		return false;
	}

	/**
	 * trying to insert a stack from an outside inventory into the internal inventory
	 * @param destinationInventory the target inventory where we will try to insert the stack
	 * @param outsideInventory the source inventory where we will take the stack from
	 * @param slot the slot of the source inventory
	 * @param side ??
	 * @return true: item transfered / false: no item transfered
	 */
	private boolean insertStackFromInventory (Hopper destinationInventory, IInventory outsideInventory, int slot, int side)
	{
		final ItemStack itemstack = outsideInventory.getStackInSlot(slot);

		if ((itemstack != null) && canExtractItemFromInventory(outsideInventory, itemstack, slot, side))
		{
			final ItemStack itemstack1 = itemstack.copy();
			final ItemStack outputStack = insertStack(destinationInventory, outsideInventory.decrStackSize(slot, 1), -1);

			if ((outputStack == null) || (outputStack.stackSize == 0))
			{
				outsideInventory.onInventoryChanged();
				return true;
			}

			outsideInventory.setInventorySlotContents(slot, itemstack1);
		}

		return false;
	}

	
	/**
	 * trying to insert an outside entity "item" into the internal inventory
	 * @param localInventory the target inventory where we will try to insert the stack
	 * @param item the item entity
	 * @return true: item transfered / false: no item transfered
	 */
	public boolean insertStackFromEntity (EntityItem item)
	{
		boolean flag = false;

		if (item == null)
			return false;
		else
		{
			final ItemStack itemstack = item.getEntityItem().copy();
			final ItemStack itemstack1 = insertStack(this, itemstack, -1);

			if ((itemstack1 != null) && (itemstack1.stackSize != 0))
				item.setEntityItemStack(itemstack1);
			else
			{
				flag = true;
				item.setDead();
			}
			return flag;
		}
	}

	// destinationInventory == HigHOvenController. Can it be something else?
	/**
	 * Insert item from stack inside the iinventory
	 * @param iinventory the destination inventory
	 * @param stack the source stack
	 * @param side ??
	 * @return the remaining stack after items have been pulled off of it into iinventory
	 */
	public ItemStack insertStack (IInventory destinationIventory, ItemStack stack, int side)
	{
		if ((destinationIventory instanceof ISidedInventory) && (side > -1))
		{
			final ISidedInventory isidedinventory = (ISidedInventory) destinationIventory;
			final int[] slot = isidedinventory.getAccessibleSlotsFromSide(side);

			for (int i = 0; (i < slot.length) && (stack != null) && (stack.stackSize > 0); ++i)
				stack = sendItemsToLocation(destinationIventory, stack, slot[i], side);
		}
		else if (this.mode == MODE_MELTABLE)
			//The transfer mode for "meltable" match the slot 4 and 5 
			for (int slot = 4; (slot < destinationIventory.getSizeInventory()) && (stack != null) && (stack.stackSize > 0); slot += 1)
				stack = sendItemsToLocation(destinationIventory, stack, slot, side);
		else
			//The transfer modes other than "meltable" correspond to the same slot number.
			stack = sendItemsToLocation(destinationIventory, stack, this.mode, side);

		if ((stack != null) && (stack.stackSize == 0))
			stack = null;

		return stack;
	}

	private IInventory getOutputInventory ()
	{
		return (mode == MODE_OUTPUT) ? getExternalInventory(direction) : getHighOvenController();
	}

	public IInventory getExternalInventory (byte facing)
	{
		double checkXPos = this.getXPos();
		double checkYPos = this.getYPos();
		double checkZPos = this.getZPos();

		switch (facing)
		{
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

		return getInventoryAtLocation(this.getWorldObj(), checkXPos, checkYPos, checkZPos);
	}

	/* ==================== Helper Method ==================== */
	// move to an external dedicated helper class?
	
	/**
	 * add as much item (from stack) as possible inside the iinventory
	 * @param destinationInventory the destination inventory
	 * @param stack the source stack
	 * @param slot the destination slot of the items
	 * @param side ??
	 * @return the remaining stack after items have been pulled off of it into iinventory
	 */
	private static ItemStack sendItemsToLocation (IInventory destinationInventory, ItemStack stack, int slot, int side)
	{
		final ItemStack masterStack = destinationInventory.getStackInSlot(slot);

		if (canInsertItemToInventory(destinationInventory, stack, slot, side))
		{
			boolean flag = false;

			if (masterStack == null)
			{
				final int max = Math.min(stack.getMaxStackSize(), destinationInventory.getInventoryStackLimit());
				if (max >= stack.stackSize)
				{
					destinationInventory.setInventorySlotContents(slot, stack);
					stack = null;
				}
				else
					destinationInventory.setInventorySlotContents(slot, stack.splitStack(max));
				flag = true;
			}
			else if (areItemStacksEqualItem(masterStack, stack))
			{
				final int max = Math.min(stack.getMaxStackSize(), destinationInventory.getInventoryStackLimit());
				if (max > masterStack.stackSize)
				{
					final int l = Math.min(stack.stackSize, max - masterStack.stackSize);
					stack.stackSize -= l;
					masterStack.stackSize += l;
					flag = l > 0;
				}
			}
			if (flag)
				destinationInventory.onInventoryChanged();
		}
		return stack;
	}

	private static boolean canInsertItemToInventory (IInventory iiventory, ItemStack stack, int slot, int side)
	{
		return !iiventory.isItemValidForSlot(slot, stack) ? false : !(iiventory instanceof ISidedInventory) || ((ISidedInventory) iiventory).canInsertItem(slot, stack, side);
	}

	private static boolean canExtractItemFromInventory (IInventory iiventory, ItemStack stack, int slot, int side)
	{
		return !(iiventory instanceof ISidedInventory) || ((ISidedInventory) iiventory).canExtractItem(slot, stack, side);
	}
	
	public static EntityItem getExternalItemEntity (HighOvenDuctLogic block, byte facing){
		return getExternalItemEntity(block.getWorldObj(), block.getXPos(), block.getYPos(), block.getZPos(), facing);
	}

	public static EntityItem getExternalItemEntity (World world, double minX, double minY, double minZ, byte facing)
	{
		double x = minX;
		double maxX = minX;
		double y = minY;
		double maxY = minY;
		double z = minZ;
		double maxZ = minZ;
		switch (facing)
		{
		case 0: // Down
			y = minY - 1.0D;
			maxY = minY - 1.0D;
			break;
		case 1: // Up
			maxY = minY + 1.0D;
			break;
		case 2: // North
			z = minZ - 1.0D;
			maxZ = minZ - 1.0D;
			break;
		case 3: // South
			maxZ = minZ + 1.0D;
			break;
		case 4: // West
			x = minX - 1.0D;
			maxX = minX - 1.0D;
			break;
		case 5: // East
			maxX = minX + 1.0D;
			break;
		default:
			break;
		}
		final List<EntityItem> list = world.selectEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.getAABBPool().getAABB(x, y, z, maxX + 1.0D, maxY + 1.0D, maxZ + 1.0D), IEntitySelector.selectAnything);
		return list.size() > 0 ? (EntityItem) list.get(0) : null;
	}

	public static IInventory getInventoryAtLocation (World world, double minX, double minY, double maxX)
	{
		IInventory iinventory = null;
		final int i = MathHelper.floor_double(minX);
		final int j = MathHelper.floor_double(minY);
		final int k = MathHelper.floor_double(maxX);
		final TileEntity tileentity = world.getBlockTileEntity(i, j, k);

		if ((tileentity != null) && (tileentity instanceof IInventory))
		{
			iinventory = (IInventory) tileentity;
			if (iinventory instanceof TileEntityChest)
			{
				final int l = world.getBlockId(i, j, k);
				final Block block = Block.blocksList[l];

				if (block instanceof BlockChest)
					iinventory = ((BlockChest) block).getInventory(world, i, j, k);
			}
		}
		if (iinventory == null)
		{
			final List<IInventory> list = world.getEntitiesWithinAABBExcludingEntity((Entity) null, AxisAlignedBB.getAABBPool().getAABB(minX, minY, maxX, minX + 1.0D, minY + 1.0D, maxX + 1.0D),
					IEntitySelector.selectInventories);
			if ((list != null) && (list.size() > 0))
				iinventory = (IInventory) list.get(world.rand.nextInt(list.size()));
		}

		return iinventory;
	}

	public static boolean areItemStacksEqualItem (ItemStack stack1, ItemStack stack2)
	{
		return stack1.itemID != stack2.itemID ? false : (stack1.getItemDamage() != stack2.getItemDamage() ? false : (stack1.stackSize > stack1.getMaxStackSize() ? false : ItemStack
				.areItemStackTagsEqual(stack1, stack2)));
	}

	/* ==================== TileEntity ==================== */

	/**
	 * Gets the world X position for this hopper entity.
	 */
	/*
	 * (non-Javadoc)
	 * @see net.minecraft.tileentity.Hopper#getXPos()
	 */
	@Override
	public double getXPos ()
	{
		return xCoord;
	}

	/**
	 * Gets the world Y position for this hopper entity.
	 */
	/*
	 * (non-Javadoc)
	 * @see net.minecraft.tileentity.Hopper#getYPos()
	 */
	@Override
	public double getYPos ()
	{
		return yCoord;
	}

	/**
	 * Gets the world Z position for this hopper entity.
	 */
	/*
	 * (non-Javadoc)
	 * @see net.minecraft.tileentity.Hopper#getZPos()
	 */
	@Override
	public double getZPos ()
	{
		return zCoord;
	}

	public void setTransferCooldown (int value)
	{
		transferCooldown = value;
	}

	public boolean isCoolingDown ()
	{
		return transferCooldown > 0;
	}

	/* ==================== IInventory ==================== */

	/*
	 * (non-Javadoc)
	 * @see net.minecraft.tileentity.TileEntity#onInventoryChanged()
	 */
	@Override
	public void onInventoryChanged ()
	{
		//updateEntity();
		super.onInventoryChanged();
	}

	/*
	 * (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#getSizeInventory()
	 */
	@Override
	public int getSizeInventory ()
	{
		return inventory.length;
	}

	/*
	 * (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#getStackInSlot(int)
	 */
	@Override
	public ItemStack getStackInSlot (int slot)
	{
		return inventory[slot];
	}

	/*
	 * (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#decrStackSize(int, int)
	 */
	@Override
	public ItemStack decrStackSize (int slot, int quantity)
	{
		if (inventory[slot] != null)
		{
			if (inventory[slot].stackSize <= quantity)
			{
				final ItemStack stack = inventory[slot];
				inventory[slot] = null;
				return stack;
			}
			final ItemStack split = inventory[slot].splitStack(quantity);
			if (inventory[slot].stackSize == 0)
				inventory[slot] = null;
			return split;
		}
		else
			return null;
	}

	/*
	 * (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#getStackInSlotOnClosing(int)
	 */
	@Override
	public ItemStack getStackInSlotOnClosing (int slot)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#setInventorySlotContents(int, net.minecraft.item.ItemStack)
	 */
	@Override
	public void setInventorySlotContents (int slot, ItemStack itemstack)
	{
		inventory[slot] = itemstack;
		if ((itemstack != null) && (itemstack.stackSize > getInventoryStackLimit()))
			itemstack.stackSize = getInventoryStackLimit();
	}

	/*
	 * (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#isUseableByPlayer(net.minecraft.entity.player.EntityPlayer)
	 */
	@Override
	public boolean isUseableByPlayer (EntityPlayer entityplayer)
	{
		if (worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) != this)
			return false;
		else
			return entityplayer.getDistance(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64D;
	}

	/*
	 * (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#getInvName()
	 */
	@Override
	public String getInvName ()
	{
		return "container.HighOvenDuct";
	}

	/*
	 * (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#isInvNameLocalized()
	 */
	@Override
	public boolean isInvNameLocalized ()
	{
		return (getInvName() != null) && (getInvName().length() > 0);
	}

	/*
	 * (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#getInventoryStackLimit()
	 */
	@Override
	public int getInventoryStackLimit ()
	{
		return 64;
	}

	/*
	 * (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#openChest()
	 */
	@Override
	public void openChest ()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#closeChest()
	 */
	@Override
	public void closeChest ()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#isItemValidForSlot(int, net.minecraft.item.ItemStack)
	 */
	@Override
	public boolean isItemValidForSlot (int slot, ItemStack itemstack)
	{
		if (itemstack == null) return false;
		if (slot < getSizeInventory())
			if ((inventory[slot] == null) || ((itemstack.stackSize + inventory[slot].stackSize) <= getInventoryStackLimit()))
				return true;
		return false;
	}

	/* ==================== IFacingLogic ==================== */

	/*
	 * (non-Javadoc)
	 * @see tconstruct.library.util.IFacingLogic#getRenderDirection()
	 */
	@Override
	public byte getRenderDirection ()
	{
		return direction;
	}

	/*
	 * (non-Javadoc)
	 * @see tconstruct.library.util.IFacingLogic#getForgeDirection()
	 */
	@Override
	public ForgeDirection getForgeDirection ()
	{
		return ForgeDirection.VALID_DIRECTIONS[direction];
	}

	/*
	 * (non-Javadoc)
	 * @see tconstruct.library.util.IFacingLogic#setDirection(int)
	 */
	@Override
	public void setDirection (int side)
	{
	}

	/*
	 * (non-Javadoc)
	 * @see tconstruct.library.util.IFacingLogic#setDirection(float, float, net.minecraft.entity.EntityLivingBase)
	 */
	@Override
	public void setDirection (float yaw, float pitch, EntityLivingBase player)
	{
		if (pitch > 45)
			direction = 1;
		else if (pitch < -45)
			direction = 0;
		else
		{
			final int facing = MathHelper.floor_double((yaw / 360) + 0.5D) & 3;
			switch (facing)
			{
			case 0:
				direction = 2;
				break;
			case 1:
				direction = 5;
				break;
			case 2:
				direction = 3;
				break;
			case 3:
				direction = 4;
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
	public void readFromNBT (NBTTagCompound tags)
	{
		mode = tags.getInteger("Mode");
		redstoneActivated = tags.getBoolean("RedstoneActivated");
		super.readFromNBT(tags);
		final NBTTagList itemList = tags.getTagList("Items");
		inventory = new ItemStack[getSizeInventory()];
		transferCooldown = tags.getInteger("TransferCooldown");
		direction = tags.getByte("Direction");

		for (int iter = 0; iter < itemList.tagCount(); iter++)
		{
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
	public void writeToNBT (NBTTagCompound tags)
	{

		super.writeToNBT(tags);
		final NBTTagList nbttaglist = new NBTTagList();
		for (int iter = 0; iter < inventory.length; iter++)
			if (inventory[iter] != null)
			{
				final NBTTagCompound tagList = new NBTTagCompound();
				tagList.setByte("Slot", (byte) iter);
				inventory[iter].writeToNBT(tagList);
				nbttaglist.appendTag(tagList);
			}
		tags.setInteger("TransferCooldown", transferCooldown);
		tags.setTag("Items", nbttaglist);
		tags.setByte("Direction", direction);
		tags.setBoolean("RedstoneActivated", redstoneActivated);
		tags.setInteger("Mode", mode);
	}

	/*
	 * (non-Javadoc)
	 * @see tsteelworks.blocks.logic.TSMultiServantLogic#getDescriptionPacket()
	 */
	@Override
	public Packet getDescriptionPacket ()
	{
		final NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag);
	}

	/*
	 * (non-Javadoc)
	 * @see tsteelworks.blocks.logic.TSMultiServantLogic#onDataPacket(net.minecraft.network.INetworkManager, net.minecraft.network.packet.Packet132TileEntityData)
	 */
	@Override
	public void onDataPacket (INetworkManager net, Packet132TileEntityData packet)
	{
		readFromNBT(packet.data);
		worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
	}


}
