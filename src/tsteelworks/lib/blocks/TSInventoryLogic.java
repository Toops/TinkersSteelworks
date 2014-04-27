/**
 * 
 */
package tsteelworks.lib.blocks;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/*
 * A simple logic class for storing items
 * Abstract to avoid instantiation
 */

public abstract class TSInventoryLogic extends TileEntity implements IInventory
{
    protected ItemStack[] inventory;
    protected String invName;
    protected int stackSizeLimit;

    public TSInventoryLogic(int invSize)
    {
        this(invSize, 64);
    }

    public TSInventoryLogic(int invSize, int maxStackSize)
    {
        inventory = new ItemStack[invSize];
        stackSizeLimit = maxStackSize;
    }

    /* Inventory management */

    public boolean canDropInventorySlot (int slot)
    {
        return true;
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

    public abstract Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z);

    /*
     * (non-Javadoc)
     * @see net.minecraft.inventory.IInventory#getInventoryStackLimit()
     */
    @Override
    public int getInventoryStackLimit ()
    {
        return stackSizeLimit;
    }

    /*
     * (non-Javadoc)
     * @see net.minecraft.inventory.IInventory#getInvName()
     */
    @Override
    public String getInvName ()
    {
        return isInvNameLocalized() ? invName : getDefaultName();
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

    /* Default implementations of hardly used methods */
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
     * @see net.minecraft.inventory.IInventory#isInvNameLocalized()
     */
    @Override
    public boolean isInvNameLocalized ()
    {
        return (invName != null) && (invName.length() > 0);
    }

    /*
     * (non-Javadoc)
     * @see net.minecraft.inventory.IInventory#isItemValidForSlot(int, net.minecraft.item.ItemStack)
     */
    @Override
    public boolean isItemValidForSlot (int slot, ItemStack itemstack)
    {
    	if(inventory == null || itemstack == null) return false;
    	
        if (slot >= 0 && slot < getSizeInventory())
            if ((itemstack.stackSize + inventory[slot].stackSize) <= getInventoryStackLimit())
                return true;
        return false;
    }

    public boolean isStackInSlot (int slot)
    {
    	if(inventory == null) return false;
    	if(slot >= 0 && slot < inventory.length)
    		return inventory[slot] != null;
    	return false;
    }

    /* Supporting methods */
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
     * @see net.minecraft.inventory.IInventory#openChest()
     */
    @Override
    public void openChest ()
    {
    }

    public void placeBlock (EntityLivingBase entity, ItemStack stack)
    {

    }

    /* NBT */
    /*
     * (non-Javadoc)
     * @see net.minecraft.tileentity.TileEntity#readFromNBT(net.minecraft.nbt.NBTTagCompound)
     */
    @Override
    public void readFromNBT (NBTTagCompound tags)
    {
        super.readFromNBT(tags);
        readInventoryFromNBT(tags);
    }

    public void readInventoryFromNBT (NBTTagCompound tags)
    {
        super.readFromNBT(tags);
        invName = tags.getString("InvName");
        final NBTTagList nbttaglist = tags.getTagList("Items");
        inventory = new ItemStack[getSizeInventory()];
        for (int iter = 0; iter < nbttaglist.tagCount(); iter++)
        {
            final NBTTagCompound tagList = (NBTTagCompound) nbttaglist.tagAt(iter);
            final byte slotID = tagList.getByte("Slot");
            if ((slotID >= 0) && (slotID < inventory.length))
                inventory[slotID] = ItemStack.loadItemStackFromNBT(tagList);
        }
    }

    public void removeBlock ()
    {

    }

    //uncheck ArrayIndexOutOfBounds
    //uncheck NullPointer
    /*
     * (non-Javadoc)
     * @see net.minecraft.inventory.IInventory#setInventorySlotContents(int, net.minecraft.item.ItemStack)
     */
    @Override
    public void setInventorySlotContents (int slot, ItemStack itemstack)
    {
    	if(inventory == null) return;
    	if(slot >= 0 && slot < inventory.length)
    	{
    		inventory[slot] = itemstack;
    		if ((itemstack != null) && (itemstack.stackSize > getInventoryStackLimit()))
    			itemstack.stackSize = getInventoryStackLimit();
    	}
    }

    public void setInvName (String name)
    {
        invName = name;
    }

    public void writeInventoryToNBT (NBTTagCompound tags)
    {
        if (invName != null)
            tags.setString("InvName", invName);
        final NBTTagList nbttaglist = new NBTTagList();
        for (int iter = 0; iter < inventory.length; iter++)
            if (inventory[iter] != null)
            {
                final NBTTagCompound tagList = new NBTTagCompound();
                tagList.setByte("Slot", (byte) iter);
                inventory[iter].writeToNBT(tagList);
                nbttaglist.appendTag(tagList);
            }

        tags.setTag("Items", nbttaglist);
    }

    /*
     * (non-Javadoc)
     * @see net.minecraft.tileentity.TileEntity#writeToNBT(net.minecraft.nbt.NBTTagCompound)
     */
    @Override
    public void writeToNBT (NBTTagCompound tags)
    {
        super.writeToNBT(tags);
        writeInventoryToNBT(tags);
    }

    protected abstract String getDefaultName ();
}
