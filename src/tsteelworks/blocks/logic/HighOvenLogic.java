package tsteelworks.blocks.logic;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import tconstruct.blocks.logic.MultiServantLogic;
import tconstruct.library.util.CoordTuple;
import tconstruct.library.util.IActiveLogic;
import tconstruct.library.util.IFacingLogic;
import tconstruct.library.util.IMasterLogic;
import tconstruct.library.util.IServantLogic;
import tsteelworks.TSteelworks;
import tsteelworks.common.TSContent;
import tsteelworks.inventory.HighOvenContainer;
import tsteelworks.lib.blocks.InventoryLogic;
import tsteelworks.lib.crafting.AdvancedSmelting;

public class HighOvenLogic extends InventoryLogic implements IActiveLogic, IFacingLogic, IFluidTank, IMasterLogic
{
    public boolean               validStructure;
    public boolean               tempValidStructure;
    public boolean               structureCapped;
    byte                         direction;
    int                          internalTemp;
    public int                   useTime;
    
    public int                   fuelGague;
    public int                   fuelAmount;
    
    boolean                      inUse;
    public CoordTuple            centerPos;
    public int[]                 activeTemps;
    public int[]                 meltingTemps;
    int                          tick;
    public ArrayList<FluidStack> moltenMetal = new ArrayList<FluidStack>();
    int                          maxLiquid;
    int                          currentLiquid;
    public int                   layers;
    int                          numBricks;
    Random                       rand        = new Random();
    boolean                      needsUpdate;

    /**
     * Initialization
     */
    public HighOvenLogic ()
    {
        super(4);
        activeTemps = meltingTemps = new int[4];
    }

    /* ==================== Layers ==================== */
    /**
     * Adjust Layers for inventory containment
     * 
     * @param lay
     *            Layer
     * @param forceAdjust
     */
    void adjustLayers (int lay, boolean forceAdjust)
    {
        if ((lay != layers) || forceAdjust)
        {
            needsUpdate = true;
            layers = lay;
            maxLiquid = 20000 * lay;
            final int[] tempActive = activeTemps;
            activeTemps = new int[4 + lay];
            final int activeLength = tempActive.length > activeTemps.length ? activeTemps.length : tempActive.length;
            System.arraycopy(tempActive, 0, activeTemps, 0, activeLength);
            final int[] tempMelting = meltingTemps;
            meltingTemps = new int[4 + lay];
            final int meltingLength = tempMelting.length > meltingTemps.length ? meltingTemps.length : tempMelting.length;
            System.arraycopy(tempMelting, 0, meltingTemps, 0, meltingLength);
            final ItemStack[] tempInv = inventory;
            inventory = new ItemStack[4 + lay];
            final int invLength = tempInv.length > inventory.length ? inventory.length : tempInv.length;
            System.arraycopy(tempInv, 0, inventory, 0, invLength);
            if ((activeTemps.length > 0) && (activeTemps.length > tempActive.length))
            {
                for (int i = tempActive.length; i < activeTemps.length; i++)
                {
                    if (!this.validOreSlot(i)) continue;
                    activeTemps[i] = 20;
                    meltingTemps[i] = 20;
                }
            }
        }
    }

    /* ==================== Misc ==================== */
    
    @Override
    public String getDefaultName ()
    {
        return "crafters.HighOven";
    }

    @Override
    public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
    {
        return new HighOvenContainer(inventoryplayer, this);
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

    @Override
    public boolean getActive ()
    {
        return validStructure && isBurning();
    }

    @Override
    public void setActive (boolean flag)
    {
        needsUpdate = true;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public boolean isUseableByPlayer (EntityPlayer entityplayer)
    {
        if (worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) != this)
            return false;
        else
            return entityplayer.getDistance(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64D;
    }
    
    /* ==================== Additive Materials ==================== */
    
    public boolean validMixers () 
    {
        return (this.hasMixers(0) && this.hasMixers(1) && this.hasMixers(2));
    }
    
    public boolean hasMixers(int slot)
    {
        ItemStack stack = this.inventory[slot];

        if (stack != null)
        {
            if ((AdvancedSmelting.instance.getMixerConsumeAmount(stack) <= stack.stackSize) && 
                    (AdvancedSmelting.instance.getMixerType(stack) == slot))
            {
                return true;
            }
        }

        //TSteelworks.logger.info("INFO: " + stack);
        return false;
               
    }
    
    /**
     * Remove additive materials by preset chance and amount
     */
    void removeMixers ()
    {
        Random rand1 = new Random();
        Random rand2 = new Random();
        Random rand3 = new Random();
        if (rand1.nextInt(100) <= AdvancedSmelting.instance.getMixerConsumeChance(inventory[0]))
            inventory[0].stackSize -= AdvancedSmelting.instance.getMixerConsumeAmount(inventory[0]);
        if (rand2.nextInt(100) <= AdvancedSmelting.instance.getMixerConsumeChance(inventory[1]))
            inventory[1].stackSize -= AdvancedSmelting.instance.getMixerConsumeAmount(inventory[1]);
        if (rand3.nextInt(100) <= AdvancedSmelting.instance.getMixerConsumeChance(inventory[2]))
            inventory[2].stackSize -= AdvancedSmelting.instance.getMixerConsumeAmount(inventory[2]);
    }
    
    /* ==================== Smelting ==================== */
    
    /**
     * Update Tile Entity
     */
    @Override
    public void updateEntity ()
    {
        tick++;
        if ((tick % 4) == 0)
        {
            heatItems();
        }
        if ((tick % 20) == 0)
        {
            if (!validStructure)
            {
                checkValidPlacement();
            }
            if ((useTime > 0) && inUse)
            {
                useTime -= 3;
            }
            if (validStructure && (useTime <= 0))
            {
                updateFuelGague();
            }
            if (needsUpdate)
            {
                needsUpdate = false;
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            }
        }
        if (tick == 60)
        {
            tick = 0;
        }
    }

    /**
     * Smelt items
     */
    void heatItems ()
    {
        if (useTime > 0)
        {
            boolean hasUse = false;
            
            for (int i = 4; i < layers + 4; i+= 1)
                //if (this.validOreSlot(i) && this.validAdditives() && meltingTemps[i] > 20 && this.isStackInSlot(i))
                if (meltingTemps[i] > 20 && this.isStackInSlot(i) && this.validMixers())
                {
                    hasUse = true;
                    if ((activeTemps[i] < internalTemp) && (activeTemps[i] < meltingTemps[i]))
                    {
                        activeTemps[i] += 1;
                    }
                    else
                        
                        if (activeTemps[i] >= meltingTemps[i]) if (!worldObj.isRemote)
                        {
                            final FluidStack result = getResultFor(inventory[i]);
                            if (result != null) if (addMoltenMetal(result, false))
                            {
                                //this.removeAdditives();
                                if (inventory[i].stackSize >= 2)
                                {
                                    inventory[i].stackSize--;
                                }
                                else
                                {
                                    inventory[i] = null;
                                }
                                activeTemps[i] = 20;
                                addMoltenMetal(result, true);
                                onInventoryChanged();
                            }
                        }
                }
                else
                {
                    activeTemps[i] = 20;
                }
            inUse = hasUse;
        }
    }

    /**
     * Get molten result for given item
     * 
     * @param stack
     *            ItemStack
     * @return FluidStack
     */
    public FluidStack getResultFor (ItemStack stack)
    {
        return AdvancedSmelting.instance.getSmelteryResult(stack);
    }
    
    /* ==================== Temperatures ==================== */
    
    /**
     * Get internal temperature for smelting
     * 
     * @return internal temperature value
     */
    public int getInternalTemperature ()
    {
        return internalTemp;
    }

    /**
     * Get current temperature for slot
     * 
     * @param slot
     * @return
     */
    public int getTempForSlot (int slot)
    {
        return (this.validOreSlot(slot)) ? activeTemps[slot] : 0;
    }

    /**
     * Get melting point for item in slot
     * 
     * @param slot
     * @return
     */
    public int getMeltingPointForSlot (int slot)
    {
        return (this.validOreSlot(slot)) ? meltingTemps[slot] : 0;
    }
    
    /**
     * Update melting temperatures for items
     */
    void updateTemperatures ()
    {
        inUse = true;
        for (int i = 0; i < layers + 4; i++)
        {
            if (!this.validOreSlot(i)) continue;
            meltingTemps[i] = AdvancedSmelting.instance.getLiquifyTemperature(inventory[i]);
        }
    }

    /* ==================== Fuel Handling ==================== */
    // TODO: Fix fuel display scaling
    /**
     * Get fuel gauge scaled for display
     * 
     * @param scale
     * @return scaled value
     */
    public int getScaledFuelGague (int scale)
    {
        int ret = (fuelGague * scale) / 52;
        if (ret < 1)
        {
            ret = 1;
        }
        return ret;
    }
    
    public boolean isBurning ()
    {
        return fuelAmount > 0;
    }
    
    public static int getItemBurnTime (ItemStack stack)
    {
        if (stack == null)
            return 0;
        else
        {
            if (stack.itemID == new ItemStack(Item.coal).itemID && stack.getItemDamage() == 1)
                return 210;
        }
        return 0;
    }
    
    /**
     * Update fuel gauge display
     */
    public void updateFuelDisplay ()
    {
      if (useTime > 0) return;

      if (inventory[3] == null)
      {
          fuelAmount = 0;
          fuelGague = 0;
          return;
      }
      if (this.getItemBurnTime(inventory[3]) > 0)
      {
          needsUpdate = true;
          final int capacity = inventory[3].stackSize;
          fuelAmount = this.getItemBurnTime(inventory[3]);
          fuelGague = (fuelAmount * 52) / 12;
      }
    }

    /**
     * Update fuel gauge (keeping typo just cuz)
     */
    void updateFuelGague ()
    {
        if (useTime > 0) return;

        if (inventory[3] == null)
        {
            fuelAmount = 0;
            fuelGague = 0;
            return;
        }
        if (this.getItemBurnTime(inventory[3]) > 0)
        {
            needsUpdate = true;
            useTime += this.getItemBurnTime(inventory[3]);
            final int capacity = inventory[3].stackSize;
            fuelAmount = this.getItemBurnTime(inventory[3]);
            fuelGague = (fuelAmount * 52) / 12;
            
            inventory[3].stackSize--;
            if (inventory[3].stackSize <= 0)
            {
                inventory[3] = null;
            }
        }
    }

    /* ==================== Misc Inventory ==================== */

    /**
     * Determine is slot is valid for 'ore' processing
     * 
     * @param slot
     * @return True if slot is valid
     */
    public boolean validOreSlot (int slot)
    {
        return (slot > 3);
    }
    
    /**
     * Get (& Set) Inventory slot stack limit Returns the maximum stack size for
     * a inventory slot.
     */
    @Override
    public int getInventoryStackLimit ()
    {
        return 64;
    }

    /**
     * Called when an the contents of Inventory change
     */
    @Override
    public void onInventoryChanged ()
    {
        updateTemperatures();
        updateEntity();
        super.onInventoryChanged();
        needsUpdate = true;
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

    @Override
    public String getInvName ()
    {
        return isInvNameLocalized() ? invName : getDefaultName();
    }

    @Override
    public boolean isInvNameLocalized ()
    {
        return (invName != null) && (invName.length() > 0);
    }
    
    @Override
    public boolean isItemValidForSlot (int slot, ItemStack itemstack)
    {
        if (slot < getSizeInventory())
            if ((inventory[slot] == null) || ((itemstack.stackSize + inventory[slot].stackSize) <= getInventoryStackLimit())) 
                return true;
        return false;
    }
    
    /* ==================== Multiblock ==================== */
    
    /**
     * Called when servants change their state
     * 
     * @param servant
     *            Servant Tile Entity
     * @param x
     *            Servant X
     * @param y
     *            Servant Y
     * @param z
     *            Servant Z
     */
    @Override
    public void notifyChange (IServantLogic servant, int x, int y, int z)
    {
        checkValidPlacement();
    }

    /**
     * Check placement validation by facing direction
     */
    public void checkValidPlacement ()
    {
        switch (getRenderDirection())
        {
            case 2: // +z
                alignInitialPlacement(xCoord, yCoord, zCoord + 1);
                break;
            case 3: // -z
                alignInitialPlacement(xCoord, yCoord, zCoord - 1);
                break;
            case 4: // +x
                alignInitialPlacement(xCoord + 1, yCoord, zCoord);
                break;
            case 5: // -x
                alignInitialPlacement(xCoord - 1, yCoord, zCoord);
                break;
        }
    }

    /**
     * Begin structure alignment
     * 
     * @param x
     *            coordinate from controller
     * @param y
     *            coordinate from controller
     * @param z
     *            coordinate from controller
     */
    public void alignInitialPlacement (int x, int y, int z)
    {
        // TODO: This needs to later search for the absolute center of the
        // structure,
        // rather than inheriting the coords directly behind the controller.
        checkValidStructure(x, y, z);
    }

    /**
     * Determine if structure is valid
     * 
     * @param x
     *            coordinate from controller
     * @param y
     *            coordinate from controller
     * @param z
     *            coordinate from controller
     */
    public void checkValidStructure (int x, int y, int z)
    {
        int checkLayers = 0;
        tempValidStructure = false;
        structureCapped = false;
        if (checkSameLevel(x, y, z))
        {
            checkLayers++;
            checkLayers += recurseStructureUp(x, y + 1, z, 0);
            checkLayers += recurseStructureDown(x, y - 1, z, 0);
        }
        if ((structureCapped != tempValidStructure != validStructure) || (checkLayers != layers))
            if (tempValidStructure && structureCapped)
            {
                internalTemp = 800;
                adjustLayers(checkLayers, false);
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                validStructure = true;
            }
            else
            {
                internalTemp = 20;
                validStructure = false;
            }
    }

    /**
     * Scan the controller layer of the structure for valid components
     * 
     * @param x
     *            coordinate from center
     * @param y
     *            coordinate from center
     * @param z
     *            coordinate from center
     * @param count
     *            current amount of blocks
     * @return block count
     */
    public boolean checkSameLevel (int x, int y, int z)
    {
        numBricks = 0;
        Block block;
        // Check inside
        for (int xPos = x - 0; xPos <= (x + 0); xPos++)
        {
            for (int zPos = z - 0; zPos <= (z + 0); zPos++)
            {
                block = Block.blocksList[worldObj.getBlockId(xPos, y, zPos)];
                if ((block != null) && !block.isAirBlock(worldObj, xPos, y, zPos)) return false;
            }
        }
        // Check outer layer
        // Scans in a swastica-like pattern
        for (int xPos = x - 1; xPos <= (x + 0); xPos++)
        {
            numBricks += checkBricks(xPos, y, z - 1);
            numBricks += checkBricks(xPos, y, z + 1);
        }
        for (int zPos = z - 1; zPos <= (z + 0); zPos++)
        {
            numBricks += checkBricks(x - 1, y, zPos);
            numBricks += checkBricks(x + 1, y, zPos);
        }
        if ((numBricks == 8))
            return true;
        else
            return false;
    }

    /**
     * Scan up the structure for valid components
     * 
     * @param x
     *            coordinate from center
     * @param y
     *            coordinate from center
     * @param z
     *            coordinate from center
     * @param count
     *            current amount of blocks
     * @return block count
     */
    public int recurseStructureUp (int x, int y, int z, int count)
    {
        numBricks = 0;
        // Check inside
        for (int xPos = x - 0; xPos <= (x + 0); xPos++)
        {
            for (int zPos = z - 0; zPos <= (z + 0); zPos++)
            {
                final int blockID = worldObj.getBlockId(xPos, y, zPos);
                final Block block = Block.blocksList[worldObj.getBlockId(xPos, y, zPos)];
                if ((block != null) && !block.isAirBlock(worldObj, xPos, y, zPos)) if (validBlockID(blockID))
                    return validateTop(x, y, z, count);
                else
                    return count;
            }
        }
        // Check outer layer
        for (int xPos = x - 1; xPos <= (x + 0); xPos++)
        {
            numBricks += checkBricks(xPos, y, z - 1);
            numBricks += checkBricks(xPos, y, z + 1);
        }
        for (int zPos = z - 1; zPos <= (z + 0); zPos++)
        {
            numBricks += checkBricks(x - 1, y, zPos);
            numBricks += checkBricks(x + 1, y, zPos);
        }
        if (numBricks != 8) return count;
        count++;
        return recurseStructureUp(x, y + 1, z, count);
    }

    /**
     * Scan down the structure for valid components
     * 
     * @param x
     *            coordinate from center
     * @param y
     *            coordinate from center
     * @param z
     *            coordinate from center
     * @param count
     *            current amount of blocks
     * @return block count
     */
    public int recurseStructureDown (int x, int y, int z, int count)
    {
        numBricks = 0;
        // Check inside
        for (int xPos = x - 0; xPos <= (x + 0); xPos++)
        {
            for (int zPos = z - 0; zPos <= (z + 0); zPos++)
            {
                final int blockID = worldObj.getBlockId(xPos, y, zPos);
                final Block block = Block.blocksList[blockID];
                if ((block != null) && !block.isAirBlock(worldObj, xPos, y, zPos)) if (validBlockID(blockID))
                    return validateBottom(x, y, z, count);
                else
                    return count;
            }
        }
        // Check outer layer X
        for (int xPos = x - 1; xPos <= (x + 0); xPos++)
        {
            numBricks += checkBricks(xPos, y, z - 1);
            numBricks += checkBricks(xPos, y, z + 1);
        }
        // Check outer layer Z
        for (int zPos = z - 1; zPos <= (z + 0); zPos++)
        {
            numBricks += checkBricks(x - 1, y, zPos);
            numBricks += checkBricks(x + 1, y, zPos);
        }
        if (numBricks != 8) return count;
        count++;
        return recurseStructureDown(x, y - 1, z, count);
    }

    /**
     * Determine if layer is a valid top layer
     * 
     * @param x
     *            coordinate from center
     * @param y
     *            coordinate from center
     * @param z
     *            coordinate from center
     * @param count
     *            current amount of blocks
     * @return block count
     */
    public int validateTop (int x, int y, int z, int count)
    {
        int topBricks = 0;
        for (int xPos = x - 1; xPos <= (x + 1); xPos++)
        {
            for (int zPos = z - 1; zPos <= (z + 1); zPos++)
                if (validBlockID(worldObj.getBlockId(xPos, y, zPos)) && (worldObj.getBlockMetadata(xPos, y, zPos) >= 1))
                {
                    topBricks++;
                }
        }
        if (topBricks == 9)
        {
            structureCapped = true;
        }
        return count;
    }

    /**
     * Determine if layer is a valid bottom layer
     * 
     * @param x
     *            coordinate from center
     * @param y
     *            coordinate from center
     * @param z
     *            coordinate from center
     * @param count
     *            current amount of blocks
     * @return block count
     */
    public int validateBottom (int x, int y, int z, int count)
    {
        int bottomBricks = 0;
        for (int xPos = x - 1; xPos <= (x + 1); xPos++)
        {
            for (int zPos = z - 1; zPos <= (z + 1); zPos++)
                if (validBlockID(worldObj.getBlockId(xPos, y, zPos)) && (worldObj.getBlockMetadata(xPos, y, zPos) >= 2))
                {
                    bottomBricks++;
                }
        }
        if (bottomBricks == 9)
        {
            tempValidStructure = true;
            centerPos = new CoordTuple(x, y + 1, z);
        }
        return count;
    }

    /**
     * Increments bricks, sets them as part of the structure.
     */
    int checkBricks (int x, int y, int z)
    {
        int tempBricks = 0;
        final int blockID = worldObj.getBlockId(x, y, z);
        if (validBlockID(blockID))
        {
            final TileEntity te = worldObj.getBlockTileEntity(x, y, z);
            if (te == this)
            {
                tempBricks++;
            }
            else
                if (te instanceof MultiServantLogic)
                {
                    final MultiServantLogic servant = (MultiServantLogic) te;
                    if (servant.hasValidMaster())
                    {
                        if (servant.verifyMaster(this, worldObj, xCoord, yCoord, zCoord))
                        {
                            tempBricks++;
                        }
                    }
                    else
                        if (servant.setMaster(xCoord, yCoord, zCoord))
                        {
                            tempBricks++;
                        }
                }
        }
        return tempBricks;
    }

    /**
     * Determine if block is a valid highoven component
     * 
     * @param blockID
     * @return Success
     */
    boolean validBlockID (int blockID)
    {
        return blockID == TSContent.highoven.blockID;
    }

    /* ==================== Fluid Handling ==================== */
    
    /**
     * Add molen metal fluidstack
     * 
     * @param liquid
     * @param first
     * @return Success
     */
    boolean addMoltenMetal (FluidStack liquid, boolean first)
    {
        // TODO: Limit fluid input to 1 fluid type only.
        needsUpdate = true;
        if (moltenMetal.size() == 0)
        {
            moltenMetal.add(liquid.copy());
            currentLiquid += liquid.amount;
            return true;
        }
        else
        {
            if ((liquid.amount + currentLiquid) > maxLiquid) return false;
            currentLiquid += liquid.amount;
            boolean added = false;
            for (int i = 0; i < moltenMetal.size(); i++)
            {
                final FluidStack l = moltenMetal.get(i);
                if (l.isFluidEqual(liquid))
                {
                    l.amount += liquid.amount;
                    added = true;
                }
                else
                    return false;
                if (l.amount <= 0)
                {
                    moltenMetal.remove(l);
                    i--;
                }
            }
            if (!added) if (first)
            {
                moltenMetal.add(0, liquid.copy());
            }
            else
            {
                moltenMetal.add(liquid.copy());
            }
            return true;
        }
    }
    
    /**
     * Get max liquid capacity
     */
    @Override
    public int getCapacity ()
    {
        return maxLiquid;
    }

    /**
     * Get current liquid amount
     * 
     * @return
     */
    public int getTotalLiquid ()
    {
        return currentLiquid;
    }

    @Override
    public FluidStack drain (int maxDrain, boolean doDrain)
    {
        if (moltenMetal.size() == 0) return null;
        final FluidStack liquid = moltenMetal.get(0);
        if (liquid != null)
        {
            if ((liquid.amount - maxDrain) <= 0)
            {
                final FluidStack liq = liquid.copy();
                if (doDrain)
                {
                    moltenMetal.remove(liquid);
                    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                    currentLiquid = 0;
                    needsUpdate = true;
                }
                return liq;
            }
            else
            {
                if (doDrain && (maxDrain > 0))
                {
                    liquid.amount -= maxDrain;
                    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                    currentLiquid -= maxDrain;
                    needsUpdate = true;
                }
                return new FluidStack(liquid.fluidID, maxDrain, liquid.tag);
            }
        }
        else
            return new FluidStack(0, 0);
    }

    @Override
    public int fill (FluidStack resource, boolean doFill)
    {
        if ((resource != null) && (currentLiquid < maxLiquid))
        {
            if ((resource.amount + currentLiquid) > maxLiquid)
            {
                resource.amount = maxLiquid - currentLiquid;
            }
            final int amount = resource.amount;
            if ((amount > 0) && doFill)
            {
                if (addMoltenMetal(resource, false))
                {
                    final FluidStack liquid = resource;
                    addMoltenMetal(liquid, true);
                }
                needsUpdate = true;
                worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
            }
            return amount;
        }
        else
            return 0;
    }

    @Override
    public FluidStack getFluid ()
    {
        if (moltenMetal.size() == 0) return null;
        return moltenMetal.get(0);
    }

    @Override
    public int getFluidAmount ()
    {
        return currentLiquid;
    }

    @Override
    public FluidTankInfo getInfo ()
    {
        return new FluidTankInfo(this);
    }

    public FluidTankInfo[] getMultiTankInfo ()
    {
        final FluidTankInfo[] info = new FluidTankInfo[moltenMetal.size() + 1];
        for (int i = 0; i < moltenMetal.size(); i++)
        {
            final FluidStack fluid = moltenMetal.get(i);
            info[i] = new FluidTankInfo(fluid.copy(), fluid.amount);
        }
        info[moltenMetal.size()] = new FluidTankInfo(null, maxLiquid - currentLiquid);
        return info;
    }

    /* ==================== NBT ==================== */
    
    @Override
    public void readFromNBT (NBTTagCompound tags)
    {
        layers = tags.getInteger("Layers");
        inventory = new ItemStack[4 + layers];
        super.readFromNBT(tags);
        internalTemp = tags.getInteger("InternalTemp");
        inUse = tags.getBoolean("InUse");
        final int[] center = tags.getIntArray("CenterPos");
        if (center.length > 2)
        {
            centerPos = new CoordTuple(center[0], center[1], center[2]);
        }
        else
        {
            centerPos = new CoordTuple(xCoord, yCoord, zCoord);
        }
        direction = tags.getByte("Direction");
        useTime = tags.getInteger("UseTime");
        currentLiquid = tags.getInteger("CurrentLiquid");
        maxLiquid = tags.getInteger("MaxLiquid");
        meltingTemps = tags.getIntArray("MeltingTemps");
        activeTemps = tags.getIntArray("ActiveTemps");
        final NBTTagList liquidTag = tags.getTagList("Liquids");
        moltenMetal.clear();
        for (int iter = 0; iter < liquidTag.tagCount(); iter++)
        {
            final NBTTagCompound nbt = (NBTTagCompound) liquidTag.tagAt(iter);
            final FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
            if (fluid != null)
            {
                moltenMetal.add(fluid);
            }
        }
    }

    @Override
    public void writeToNBT (NBTTagCompound tags)
    {
        super.writeToNBT(tags);
        tags.setInteger("InternalTemp", internalTemp);
        tags.setBoolean("InUse", inUse);
        int[] center = new int[3];
        if (centerPos == null)
        {
            center = new int[] { xCoord, yCoord, zCoord };
        }
        else
        {
            center = new int[] { centerPos.x, centerPos.y, centerPos.z };
        }
        tags.setIntArray("CenterPos", center);
        tags.setByte("Direction", direction);
        tags.setInteger("UseTime", useTime);
        tags.setInteger("CurrentLiquid", currentLiquid);
        tags.setInteger("MaxLiquid", maxLiquid);
        tags.setInteger("Layers", layers);
        tags.setIntArray("MeltingTemps", meltingTemps);
        tags.setIntArray("ActiveTemps", activeTemps);
        final NBTTagList taglist = new NBTTagList();
        for (final FluidStack liquid : moltenMetal)
        {
            final NBTTagCompound nbt = new NBTTagCompound();
            liquid.writeToNBT(nbt);
            taglist.appendTag(nbt);
        }
        tags.setTag("Liquids", taglist);
    }

    @Override
    public Packet getDescriptionPacket ()
    {
        final NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket (INetworkManager net, Packet132TileEntityData packet)
    {
        readFromNBT(packet.data);
        onInventoryChanged();
        worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
        needsUpdate = true;
    }

    /* ==================== Other ==================== */

    @Override
    public void openChest ()
    {}

    @Override
    public void closeChest ()
    {}

}
