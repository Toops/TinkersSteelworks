package tsteelworks.blocks.logic;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import tconstruct.library.util.IFacingLogic;
import tsteelworks.inventory.HighOvenDuctContainer;

// TODO: Lots
public class HighOvenDuctLogic extends TSMultiServantLogic implements IInventory, IFacingLogic
{
    byte direction;
    protected int mode;
    protected ItemStack[] inventory;
    protected String invName;
    protected int stackSizeLimit;
    
    public HighOvenDuctLogic ()
    {
        invName = "container.HighOvenDuct";
        
        setup(9, 64);
    }
    
    public void setup(int invSize, int maxStackSize)
    {
        inventory = new ItemStack[invSize];
        stackSizeLimit = maxStackSize;
        mode = 0;
    }
    
    public int getMode ()
    {
        return mode;
    }
    
    public void setMode (int newMode)
    {
        mode = (newMode < 6) ? newMode : 5; 
    }
    
    @Override
    public boolean canUpdate ()
    {
        return true;
    }
    
    @Override
    public void onInventoryChanged ()
    {
        updateEntity();
        super.onInventoryChanged();
    }
    
    @Override
    public void updateEntity ()
    {
        
    }
    
    @Override
    public int getSizeInventory ()
    {
        return inventory.length;
    }

    @Override
    public ItemStack getStackInSlot (int slot)
    {
        return inventory[slot];
    }

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
            {
                inventory[slot] = null;
            }
            return split;
        }
        else
            return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing (int slot)
    {
        return null;
    }

    @Override
    public void setInventorySlotContents (int slot, ItemStack itemstack)
    {
        inventory[slot] = itemstack;
        if ((itemstack != null) && (itemstack.stackSize > getInventoryStackLimit()))
        {
            itemstack.stackSize = getInventoryStackLimit();
        }
    }
    
    private boolean insertItemToInventory()
    {
        if (!hasValidMaster()) return false;
        final int mx = getMasterPosition().x;
        final int my = getMasterPosition().y;
        final int mz = getMasterPosition().z;
        final HighOvenLogic highoven = (HighOvenLogic) worldObj.getBlockTileEntity(mx, my, mz);
        IInventory iinventory = highoven;

        if (iinventory == null)
        {
            return false;
        }
        else
        {
            for (int i = 0; i < this.getSizeInventory(); ++i)
            {
                if (this.getStackInSlot(i) != null)
                {
                    ItemStack itemstack = this.getStackInSlot(i).copy();
                    ItemStack itemstack1 = insertStack(iinventory, this.decrStackSize(i, 1));

                    if (itemstack1 == null || itemstack1.stackSize == 0)
                    {
                        iinventory.onInventoryChanged();
                        return true;
                    }

                    this.setInventorySlotContents(i, itemstack);
                }
            }

            return false;
        }
    }
    /**
     * Inserts a stack into an inventory. Args: Inventory, stack. Returns leftover items.
     */
    public static ItemStack insertStack(IInventory par0IInventory, ItemStack par1ItemStack)
    {
        int k = par0IInventory.getSizeInventory();

        for (int l = 0; l < k && par1ItemStack != null && par1ItemStack.stackSize > 0; ++l)
        {
            par1ItemStack = func_102014_c(par0IInventory, par1ItemStack, l);
        }

        if (par1ItemStack != null && par1ItemStack.stackSize == 0)
        {
            par1ItemStack = null;
        }

        return par1ItemStack;
    }
    
    private static ItemStack func_102014_c(IInventory par0IInventory, ItemStack par1ItemStack, int par2)
    {
        ItemStack itemstack1 = par0IInventory.getStackInSlot(par2);

        if (canInsertItemToInventory(par0IInventory, par1ItemStack, par2))
        {
            boolean flag = false;

            if (itemstack1 == null)
            {
                int max = Math.min(par1ItemStack.getMaxStackSize(), par0IInventory.getInventoryStackLimit());
                if (max >= par1ItemStack.stackSize)
                {
                    par0IInventory.setInventorySlotContents(par2, par1ItemStack);
                    par1ItemStack = null;
                }
                else
                {
                    par0IInventory.setInventorySlotContents(par2, par1ItemStack.splitStack(max));
                }
                flag = true;
            }
            else if (areItemStacksEqualItem(itemstack1, par1ItemStack))
            {
                int max = Math.min(par1ItemStack.getMaxStackSize(), par0IInventory.getInventoryStackLimit());
                if (max > itemstack1.stackSize)
                {
                    int l = Math.min(par1ItemStack.stackSize, max - itemstack1.stackSize);
                    par1ItemStack.stackSize -= l;
                    itemstack1.stackSize += l;
                    flag = l > 0;
                }
            }

            if (flag)
            {
                par0IInventory.onInventoryChanged();
            }
        }

        return par1ItemStack;
    }
    
    /**
     * Args: inventory, item, slot, side
     */
    private static boolean canInsertItemToInventory(IInventory par0IInventory, ItemStack par1ItemStack, int par2)
    {
        return par0IInventory.isItemValidForSlot(par2, par1ItemStack);
    }
    
    private static boolean areItemStacksEqualItem(ItemStack par0ItemStack, ItemStack par1ItemStack)
    {
        return par0ItemStack.itemID != par1ItemStack.itemID ? false : (par0ItemStack.getItemDamage() != par1ItemStack.getItemDamage() ? false : (par0ItemStack.stackSize > par0ItemStack.getMaxStackSize() ? false : ItemStack.areItemStackTagsEqual(par0ItemStack, par1ItemStack)));
    }
    
    @Override
    public boolean isUseableByPlayer (EntityPlayer entityplayer)
    {
        if (worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) != this)
            return false;
        else
            return entityplayer.getDistance(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64D;
    }

    @Override
    public String getInvName ()
    {
        return isInvNameLocalized() ? invName : getDefaultName();
    }

    public String getDefaultName ()
    {
        return "container.HighOvenDuct";
    }
    
    public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
    {
        return new HighOvenDuctContainer(inventoryplayer, this);
    }
    
    @Override
    public boolean isInvNameLocalized ()
    {
        return (invName != null) && (invName.length() > 0);
    }


    @Override
    public int getInventoryStackLimit ()
    {
        return 64;
    }

    /* (non-Javadoc)
     * @see net.minecraft.inventory.IInventory#openChest()
     */
    @Override
    public void openChest ()
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see net.minecraft.inventory.IInventory#closeChest()
     */
    @Override
    public void closeChest ()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean isItemValidForSlot (int slot, ItemStack itemstack)
    {
        if (slot < getSizeInventory())
            if ((inventory[slot] == null) || ((itemstack.stackSize + inventory[slot].stackSize) <= getInventoryStackLimit())) 
                return true;
        return false;
    }

    @Override
    public byte getRenderDirection ()
    {
        return direction;
    }

    @Override
    public ForgeDirection getForgeDirection ()
    {
        return ForgeDirection.VALID_DIRECTIONS[direction];
    }

    @Override
    public void setDirection (int side)
    {}

    @Override
    public void setDirection (float yaw, float pitch, EntityLivingBase player)
    {
        if (pitch > 45)
        {
            direction = 1;
        }
        else
            if (pitch < -45)
            {
                direction = 0;
            }
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
    
    /* NBT */
    
    @Override
    public void readFromNBT (NBTTagCompound tags)
    {
        super.readFromNBT(tags);
        direction = tags.getByte("Direction");
        mode = tags.getInteger("Mode");
        readInventoryFromNBT(tags);
    }

    public void readInventoryFromNBT (NBTTagCompound tags)
    {
        this.invName = tags.getString("InvName");
        NBTTagList nbttaglist = tags.getTagList("Items");
        inventory = new ItemStack[getSizeInventory()];
        for (int iter = 0; iter < nbttaglist.tagCount(); iter++)
        {
            NBTTagCompound tagList = (NBTTagCompound) nbttaglist.tagAt(iter);
            byte slotID = tagList.getByte("Slot");
            if (slotID >= 0 && slotID < inventory.length)
            {
                inventory[slotID] = ItemStack.loadItemStackFromNBT(tagList);
            }
        }
    }

    @Override
    public void writeToNBT (NBTTagCompound tags)
    {
        super.writeToNBT(tags);
        tags.setByte("Direction", direction);
        tags.setInteger("Mode", mode);
        writeInventoryToNBT(tags);
    }

    public void writeInventoryToNBT (NBTTagCompound tags)
    {
        if (invName != null)
            tags.setString("InvName", invName);
        NBTTagList nbttaglist = new NBTTagList();
        for (int iter = 0; iter < inventory.length; iter++)
        {
            if (inventory[iter] != null)
            {
                NBTTagCompound tagList = new NBTTagCompound();
                tagList.setByte("Slot", (byte) iter);
                inventory[iter].writeToNBT(tagList);
                nbttaglist.appendTag(tagList);
            }
        }

        tags.setTag("Items", nbttaglist);
    }
}
