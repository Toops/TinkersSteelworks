package tsteelworks.blocks.logic;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
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
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import tconstruct.blocks.logic.LavaTankLogic;
import tconstruct.blocks.logic.MultiServantLogic;
import tconstruct.common.TContent;
import tconstruct.library.util.CoordTuple;
import tconstruct.library.util.IActiveLogic;
import tconstruct.library.util.IFacingLogic;
import tconstruct.library.util.IMasterLogic;
import tconstruct.library.util.IServantLogic;
import tsteelworks.common.TSContent;
import tsteelworks.inventory.HighOvenContainer;
import tconstruct.library.blocks.InventoryLogic;
import tsteelworks.lib.crafting.SteelforgeCrafting;

public class HighOvenLogic extends InventoryLogic implements IActiveLogic, IFacingLogic, IFluidTank, IMasterLogic 
{	
    public boolean validStructure;		// Structure validation
    public boolean tempValidStructure;	// Temporary structure validation (check bottom)
    public boolean structureCapped;		// Temporary structure validation (check top)
    byte direction;						// Facing direction
    int internalTemp;					// Internal Temperature
    public int useTime;					// Usage time
    public int fuelGague;				// Fuel Gauge (display)
    public int fuelAmount;				// Fuel Amount
    boolean inUse;						// Currently smelting

    ArrayList<CoordTuple> lavaTanks;	// Array of lava tanks (Will be limited later)
    CoordTuple activeLavaTank;			// Active lava tank position
    public CoordTuple centerPos;		// Center position from bottom

    public int[] activeTemps;			// Active temperatures array
    public int[] meltingTemps;			// Item melting temperatures array 
    int tick;							// Time ticker

    public ArrayList<FluidStack> moltenMetal = new ArrayList<FluidStack>(); // Active fluids array

    int maxLiquid;						// Maximum liquid capacity
    int currentLiquid;					// Current liquid amount
    public int layers;					// Amount of layers

    int numBricks;						// Number of bricks comprising structure

    Random rand = new Random();			// RNG
    boolean needsUpdate;				// Update flag
    
    /**
     * Initialization
     */
	public HighOvenLogic() {
        super(0);
        lavaTanks = new ArrayList<CoordTuple>();
        activeTemps = new int[0];
        meltingTemps = new int[0];
	}

	/* ==================== Layers ==================== */
	
	/**
	 * Adjust Layers for inventory containment
	 * 
	 * @param lay Layer
	 * @param forceAdjust
	 */
    void adjustLayers (int lay, boolean forceAdjust)
    {
        if (lay != layers || forceAdjust)
        {
            needsUpdate = true;
            layers = lay;
            maxLiquid = 20000 * lay;
            int[] tempActive = activeTemps;
            activeTemps = new int[lay];
            int activeLength = tempActive.length > activeTemps.length ? activeTemps.length : tempActive.length;
            System.arraycopy(tempActive, 0, activeTemps, 0, activeLength);

            int[] tempMelting = meltingTemps;
            meltingTemps = new int[lay];
            int meltingLength = tempMelting.length > meltingTemps.length ? meltingTemps.length : tempMelting.length;
            System.arraycopy(tempMelting, 0, meltingTemps, 0, meltingLength);

            ItemStack[] tempInv = inventory;
            inventory = new ItemStack[lay];
            int invLength = tempInv.length > inventory.length ? inventory.length : tempInv.length;
            System.arraycopy(tempInv, 0, inventory, 0, invLength);

            if (activeTemps.length > 0 && activeTemps.length > tempActive.length)
            {
                for (int i = tempActive.length; i < activeTemps.length; i++)
                {
                    activeTemps[i] = 20;
                    meltingTemps[i] = 20;
                }
            }

            if (tempInv.length > inventory.length)
            {
                for (int i = inventory.length; i < tempInv.length; i++)
                {
                    ItemStack stack = tempInv[i];
                    if (stack != null)
                    {
                        float jumpX = rand.nextFloat() * 0.8F + 0.1F;
                        float jumpY = rand.nextFloat() * 0.8F + 0.1F;
                        float jumpZ = rand.nextFloat() * 0.8F + 0.1F;

                        int offsetX = 0;
                        int offsetZ = 0;
                        switch (getRenderDirection())
                        {
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
                        }

                        while (stack.stackSize > 0)
                        {
                            int itemSize = rand.nextInt(21) + 10;

                            if (itemSize > stack.stackSize)
                            {
                                itemSize = stack.stackSize;
                            }

                            stack.stackSize -= itemSize;
                            EntityItem entityitem = new EntityItem(worldObj, (double) ((float) xCoord + jumpX + offsetX), (double) ((float) yCoord + jumpY),
                                    (double) ((float) zCoord + jumpZ + offsetZ), new ItemStack(stack.itemID, itemSize, stack.getItemDamage()));

                            if (stack.hasTagCompound())
                            {
                                entityitem.getEntityItem().setTagCompound((NBTTagCompound) stack.getTagCompound().copy());
                            }

                            float offset = 0.05F;
                            entityitem.motionX = (double) ((float) rand.nextGaussian() * offset);
                            entityitem.motionY = (double) ((float) rand.nextGaussian() * offset + 0.2F);
                            entityitem.motionZ = (double) ((float) rand.nextGaussian() * offset);
                            worldObj.spawnEntityInWorld(entityitem);
                        }
                    }
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
    public void setDirection (int side) { }

    @Override
    public void setDirection (float yaw, float pitch, EntityLivingBase player)
    {
        int facing = MathHelper.floor_double((double) (yaw / 360) + 0.5D) & 3;
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
        return validStructure;
    }

    @Override
    public void setActive (boolean flag)
    {
        needsUpdate = true;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    /**
     * Get fuel gauge scaled for display
     * @param scale
     * @return
     */
    public int getScaledFuelGague (int scale)
    {
        int ret = (fuelGague * scale) / 52;
        if (ret < 1)
            ret = 1;
        return ret;
    }

    /**
     * Get internal temperature for smelting
     * 
     * @return
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
        return activeTemps[slot];
    }

    /**
     * Get melting point for item in slot
     * 
     * @param slot
     * @return
     */
    public int getMeltingPointForSlot (int slot)
    {
        return meltingTemps[slot];
    }

    /* ==================== Updating ==================== */
    
    /** 
     * Update Tile Entity
     */
    public void updateEntity ()
    {
        tick++;
        
        if (tick % 4 == 0)
            heatItems();

        if (tick % 20 == 0)
        {
            if (!validStructure)
                checkValidPlacement();

            if (useTime > 0 && inUse)
                useTime -= 3;

            if (validStructure && useTime <= 0)
                updateFuelGague();

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
            for (int i = 0; i < layers; i++)
            {
                if (meltingTemps[i] > 20 && this.isStackInSlot(i))
                {
                    hasUse = true;
                    if (activeTemps[i] < internalTemp && activeTemps[i] < meltingTemps[i])
                    {
                        activeTemps[i] += 1;
                    }
                    else if (activeTemps[i] >= meltingTemps[i])
                    {
                        if (!worldObj.isRemote)
                        {
                            FluidStack result = getResultFor(inventory[i]);
                            if (result != null)
                            {
                                if (addMoltenMetal(result, false))
                                {
                                	if (inventory[i].stackSize >= 2)
                                		inventory[i].stackSize--;
                                	else
                                		inventory[i] = null;
                                    activeTemps[i] = 20;
                                    addMoltenMetal(result, true);
                                    onInventoryChanged();
                                }
                            }
                        }
                    }
                }
            else 
                activeTemps[i] = 20;
            }
            inUse = hasUse;
        }
    }
    
    /**
     * Add molen metal fluidstack
     * 
     * @param liquid
     * @param first
     * @return Success
     */
    boolean addMoltenMetal (FluidStack liquid, boolean first)
    {
        needsUpdate = true;
        if (moltenMetal.size() == 0)
        {
            moltenMetal.add(liquid.copy());
            currentLiquid += liquid.amount;
            return true;
        }
        else
        {
            if (liquid.amount + currentLiquid > maxLiquid)
                return false;

            currentLiquid += liquid.amount;
            boolean added = false;
            for (int i = 0; i < moltenMetal.size(); i++)
            {
                FluidStack l = moltenMetal.get(i);
                if (l.isFluidEqual(liquid))
                {
                    l.amount += liquid.amount;
                    added = true;
                }
                if (l.amount <= 0)
                {
                    moltenMetal.remove(l);
                    i--;
                }
            }
            if (!added)
            {
                if (first)
                    moltenMetal.add(0, liquid.copy());
                else
                    moltenMetal.add(liquid.copy());
            }
            return true;
        }
    }
    
    /* ==================== Updating ==================== */
    
    /**
     * Update melting temperatures for items
     */
	@SuppressWarnings("static-access")
	void updateTemperatures ()
    {
        inUse = true;
        for (int i = 0; i < layers; i++)
        {
            meltingTemps[i] = SteelforgeCrafting.instance.getLiquifyTemperature(inventory[i]);
        }
    }
    
	/**
	 * Update fuel gauge display
	 */
    public void updateFuelDisplay ()
    {
        if (activeLavaTank == null || useTime > 0)
            return;

        if (!worldObj.blockExists(activeLavaTank.x, activeLavaTank.y, activeLavaTank.z))
        {
            fuelAmount = 0;
            fuelGague = 0;
            return;
        }

        TileEntity tankContainer = worldObj.getBlockTileEntity(activeLavaTank.x, activeLavaTank.y, activeLavaTank.z);
        if (tankContainer == null)
        {
            fuelAmount = 0;
            fuelGague = 0;
            return;
        }
        if (tankContainer instanceof IFluidHandler)
        {
            needsUpdate = true;
            FluidStack liquid = ((IFluidHandler) tankContainer).drain(ForgeDirection.DOWN, 150, false);
            if (liquid != null && liquid.getFluid().getBlockID() == Block.lavaStill.blockID)
            {
                FluidTankInfo[] info = ((IFluidHandler) tankContainer).getTankInfo(ForgeDirection.DOWN);
                if (info.length > 0)
                {
                    int capacity = info[0].capacity;
                    fuelAmount = liquid.amount;
                    fuelGague = liquid.amount * 52 / capacity;
                }
            }
            else
            {
                fuelAmount = 0;
                fuelGague = 0;
            }
        }
    }
    
    /**
     * Update fuel gauge (keeping typo just cuz)
     */
    void updateFuelGague ()
    {
        if (activeLavaTank == null || useTime > 0)
            return;

        if (!worldObj.blockExists(activeLavaTank.x, activeLavaTank.y, activeLavaTank.z))
        {
            fuelAmount = 0;
            fuelGague = 0;
            return;
        }

        TileEntity tankContainer = worldObj.getBlockTileEntity(activeLavaTank.x, activeLavaTank.y, activeLavaTank.z);
        if (tankContainer == null)
        {
            fuelAmount = 0;
            fuelGague = 0;
            return;
        }
        if (tankContainer instanceof IFluidHandler)
        {
            needsUpdate = true;
            FluidStack liquid = ((IFluidHandler) tankContainer).drain(ForgeDirection.DOWN, 150, false);
            if (liquid != null && liquid.getFluid().getBlockID() == Block.lavaStill.blockID)
            {
                liquid = ((IFluidHandler) tankContainer).drain(ForgeDirection.DOWN, 150, true);
                useTime += liquid.amount;

                FluidTankInfo[] info = ((IFluidHandler) tankContainer).getTankInfo(ForgeDirection.DOWN);
                liquid = info[0].fluid;
                int capacity = info[0].capacity;
                if (liquid != null)
                {
                    fuelAmount = liquid.amount;
                    fuelGague = liquid.amount * 52 / capacity;
                }
                else
                {
                    fuelAmount = 0;
                    fuelGague = 0;
                }
            }
            else
            {
                boolean foundTank = false;
                int iter = 0;
                while (!foundTank)
                {
                    CoordTuple possibleTank = lavaTanks.get(iter);
                    TileEntity newTankContainer = worldObj.getBlockTileEntity(possibleTank.x, possibleTank.y, possibleTank.z);
                    if (newTankContainer instanceof IFluidHandler)
                    {
                        FluidStack newliquid = ((IFluidHandler) newTankContainer).drain(ForgeDirection.UNKNOWN, 150, false);
                        if (newliquid != null && newliquid.getFluid().getBlockID() == Block.lavaStill.blockID && newliquid.amount > 0)
                        {
                            foundTank = true;
                            activeLavaTank = possibleTank;
                            iter = lavaTanks.size();

                            FluidTankInfo[] info = ((IFluidHandler) tankContainer).getTankInfo(ForgeDirection.DOWN);
                            liquid = info[0].fluid;
                            int capacity = info[0].capacity;
                            if (liquid != null)
                            {
                                fuelAmount = liquid.amount;
                                fuelGague = liquid.amount * 52 / capacity;
                            }
                            else
                            {
                                fuelAmount = 0;
                                fuelGague = 0;
                            }
                        }
                    }
                    iter++;
                    if (iter >= lavaTanks.size())
                        foundTank = true;
                }
            }
        }
    }

    /** 
     * Get molten result for given item
     * 
     * @param stack ItemStack
     * @return FluidStack
     */
	@SuppressWarnings("static-access")
	public FluidStack getResultFor (ItemStack stack)
    {
		return SteelforgeCrafting.instance.getSmelteryResult(stack);
    }
    
	/** 
	 * Get (& Set) Inventory slot stack limit
	 * 
	 * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. 
	 */
    public int getInventoryStackLimit ()
    {
        return 27;
    }

    /** 
     * Called when an the contents of Inventory change
     */
    public void onInventoryChanged ()
    {
        updateTemperatures();
        updateEntity();
        super.onInventoryChanged();
        needsUpdate = true;
    }
    
    /* ==================== Multiblock ==================== */
    
    /** 
     * Called when servants change their state
     * 
     * @param servant Servant Tile Entity
     * @param x Servant X
     * @param y Servant Y
     * @param z Servant Z
     */
    @Override
    public void notifyChange (IServantLogic servant, int x, int y, int z) 
    {
        checkValidPlacement();
    }

    /**
     *  Check placement validation by facing direction
     */
    public void checkValidPlacement () {
        switch (getRenderDirection()) {
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
     * This needs to later search for the absolute center of the structure, 
     * rather than inheriting the coords directly behind the controller.
     * 
     * @param x coordinate from controller
     * @param y coordinate from controller
     * @param z coordinate from controller
     */
    public void alignInitialPlacement (int x, int y, int z) {
        checkValidStructure(x, y, z);
    }
    
    /**
     * Determine if structure is valid
     * 
     * @param x coordinate from controller
     * @param y coordinate from controller
     * @param z coordinate from controller
     */
    public void checkValidStructure (int x, int y, int z) {
        int checkLayers = 0;
        tempValidStructure = false;
        structureCapped = false;
        
        if (checkSameLevel(x, y, z)) {
            checkLayers++;
            checkLayers += recurseStructureUp(x, y + 1, z, 0);
            checkLayers += recurseStructureDown(x, y - 1, z, 0);
        }

        if (structureCapped != tempValidStructure != validStructure || checkLayers != this.layers) {
            if (tempValidStructure && structureCapped) {
                internalTemp = 800;
                activeLavaTank = lavaTanks.get(0);
                adjustLayers(checkLayers, false);
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                validStructure = true;
            }
            else {
                internalTemp = 20;
                validStructure = false;
            }
        }
    }

    /**
     * Scan the controller layer of the structure for valid components
     * 
     * @param x - coordinate from center
     * @param y - coordinate from center
     * @param z - coordinate from center
     * @param count - current amount of blocks
     * @return block count
     */
    public boolean checkSameLevel (int x, int y, int z) {
        numBricks = 0;
        lavaTanks.clear();
        
        Block block;
        // Check inside
        for (int xPos = x - 0; xPos <= x + 0; xPos++) {
            for (int zPos = z - 0; zPos <= z + 0; zPos++) {
                block = Block.blocksList[worldObj.getBlockId(xPos, y, zPos)];
                if (block != null && !block.isAirBlock(worldObj, xPos, y, zPos))
                    return false;
            }
        }
        // Check outer layer
        // Scans in a swastica-like pattern
        for (int xPos = x - 1; xPos <= x + 0; xPos++) {
            numBricks += checkBricks(xPos, y, z - 1);
            numBricks += checkBricks(xPos, y, z + 1);
        }
        for (int zPos = z - 1; zPos <= z + 0; zPos++) {
            numBricks += checkBricks(x - 1, y, zPos);
            numBricks += checkBricks(x + 1, y, zPos);
        }
        if (numBricks == 8 && lavaTanks.size() > 0)
            return true;
        else
            return false;
    }
    
    /**
     * Scan up the structure for valid components
     * 
     * @param x - coordinate from center
     * @param y - coordinate from center
     * @param z - coordinate from center
     * @param count - current amount of blocks
     * @return block count
     */
    public int recurseStructureUp (int x, int y, int z, int count) {
        numBricks = 0;
        //Check inside
        for (int xPos = x - 0; xPos <= x + 0; xPos++) {
            for (int zPos = z - 0; zPos <= z + 0; zPos++) {
            	int blockID = worldObj.getBlockId(xPos, y, zPos);
                Block block = Block.blocksList[worldObj.getBlockId(xPos, y, zPos)];
                if (block != null && !block.isAirBlock(worldObj, xPos, y, zPos)) {
                    if (validBlockID(blockID))
                        return validateTop(x, y, z, count);
                    else
                        return count;
                }
            }
        }
        //Check outer layer
        for (int xPos = x - 1; xPos <= x + 0; xPos++) {
            numBricks += checkBricks(xPos, y, z - 1);
            numBricks += checkBricks(xPos, y, z + 1);
        }
        for (int zPos = z - 1; zPos <= z + 0; zPos++) {
            numBricks += checkBricks(x - 1, y, zPos);
            numBricks += checkBricks(x + 1, y, zPos);
        }
        if (numBricks != 8)
            return count;
        count++;
        return recurseStructureUp(x, y + 1, z, count);
    }

    /**
     * Scan down the structure for valid components
     * 
     * @param x - coordinate from center
     * @param y - coordinate from center
     * @param z - coordinate from center
     * @param count - current amount of blocks
     * @return block count
     */
    public int recurseStructureDown (int x, int y, int z, int count) {
        numBricks = 0;
        //Check inside
        for (int xPos = x - 0; xPos <= x + 0; xPos++) {
            for (int zPos = z - 0; zPos <= z + 0; zPos++) {
                int blockID = worldObj.getBlockId(xPos, y, zPos);
                Block block = Block.blocksList[blockID];
                if (block != null && !block.isAirBlock(worldObj, xPos, y, zPos)) {
                    if (validBlockID(blockID))
                        return validateBottom(x, y, z, count);
                    else
                        return count;
                }
            }
        }
        //Check outer layer X
        for (int xPos = x - 1; xPos <= x + 0; xPos++) {
            numBricks += checkBricks(xPos, y, z - 1);
            numBricks += checkBricks(xPos, y, z + 1);
        }
        //Check outer layer Z
        for (int zPos = z - 1; zPos <= z + 0; zPos++) {
            numBricks += checkBricks(x - 1, y, zPos);
            numBricks += checkBricks(x + 1, y, zPos);
        }
        if (numBricks != 8)
            return count;
        count++;
        return recurseStructureDown(x, y - 1, z, count);
    }
    
    /**
     * Determine if layer is a valid top layer
     * 
     * @param x - coordinate from center
     * @param y - coordinate from center
     * @param z - coordinate from center
     * @param count - current amount of blocks
     * @return block count
     */
    public int validateTop (int x, int y, int z, int count) 
    {
        int topBricks = 0;
        for (int xPos = x - 1; xPos <= x + 1; xPos++) 
        {
            for (int zPos = z - 1; zPos <= z + 1; zPos++) 
            {
                if (validBlockID(worldObj.getBlockId(xPos, y, zPos)) 
                		&& (worldObj.getBlockMetadata(xPos, y, zPos) >= 1))
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
     * @param x - coordinate from center
     * @param y - coordinate from center
     * @param z - coordinate from center
     * @param count - current amount of blocks
     * @return block count
     */
    public int validateBottom (int x, int y, int z, int count) 
    {
        int bottomBricks = 0;
        for (int xPos = x - 1; xPos <= x + 1; xPos++) 
        {
            for (int zPos = z - 1; zPos <= z + 1; zPos++) 
            {
                if (validBlockID(worldObj.getBlockId(xPos, y, zPos)) 
                		&& (worldObj.getBlockMetadata(xPos, y, zPos) >= 2))
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
     * Returns whether the brick is a lava tank or not.
     * 
     * Increments bricks, sets them as part of the structure, and adds tanks to the list.
     */
    @SuppressWarnings("deprecation")
	int checkBricks (int x, int y, int z)
    {
        int tempBricks = 0;
        int blockID = worldObj.getBlockId(x, y, z);
        if (validBlockID(blockID) || validTankID(blockID))
        {
            TileEntity te = worldObj.getBlockTileEntity(x, y, z);
            if (te == this)
            {
                tempBricks++;
            }
            else if (te instanceof MultiServantLogic)
            {
                MultiServantLogic servant = (MultiServantLogic) te;
                if (servant.hasValidMaster())
                {
                    if (servant.verifyMaster(this, worldObj, this.xCoord, this.yCoord, this.zCoord))
                        tempBricks++;
                }
                else if (servant.setMaster(this.xCoord, this.yCoord, this.zCoord))
                {
                    tempBricks++;
                }

                if (te instanceof LavaTankLogic)
                {
                    lavaTanks.add(new CoordTuple(x, y, z));
                }
            }
        }
        return tempBricks;
    }
    
    /**
     * Determine if block is a valid steelforge component  
     * 
     * @param blockID
     * @return Success
     */
    boolean validBlockID(int blockID)
    {
        return blockID == TSContent.steelforge.blockID;
    }
    
    /**
     * Determine if block is a valid tank
     * 
     * @param blockID
     * @return Success
     */
    boolean validTankID(int blockID)
    {
        return blockID == TContent.lavaTank.blockID || blockID == TContent.lavaTankNether.blockID;
    }

    /* ==================== Fluid Handling ==================== */
    
    /**
     * Get max liquid capacity
     */
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
        if (moltenMetal.size() == 0)
            return null;

        FluidStack liquid = moltenMetal.get(0);
        if (liquid != null)
        {
            if (liquid.amount - maxDrain <= 0)
            {
                FluidStack liq = liquid.copy();
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
                if (doDrain && maxDrain > 0)
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
        {
            return new FluidStack(0, 0);
        }
    }

    @Override
    public int fill (FluidStack resource, boolean doFill)
    {
        if (resource != null && currentLiquid < maxLiquid)
        {
            if (resource.amount + currentLiquid > maxLiquid)
                resource.amount = maxLiquid - currentLiquid;
            int amount = resource.amount;

            if (amount > 0 && doFill)
            {
                if (addMoltenMetal(resource, false))
                {
                    FluidStack liquid = (FluidStack) resource;
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
        if (moltenMetal.size() == 0)
            return null;
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
        FluidTankInfo[] info = new FluidTankInfo[moltenMetal.size() + 1];
        for (int i = 0; i < moltenMetal.size(); i++)
        {
            FluidStack fluid = moltenMetal.get(i);
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
        inventory = new ItemStack[layers];
        super.readFromNBT(tags);

        internalTemp = tags.getInteger("InternalTemp");
        inUse = tags.getBoolean("InUse");

        int[] center = tags.getIntArray("CenterPos");
        if (center.length > 2)
            centerPos = new CoordTuple(center[0], center[1], center[2]);
        else
            centerPos = new CoordTuple(xCoord, yCoord, zCoord);

        direction = tags.getByte("Direction");
        useTime = tags.getInteger("UseTime");
        currentLiquid = tags.getInteger("CurrentLiquid");
        maxLiquid = tags.getInteger("MaxLiquid");
        meltingTemps = tags.getIntArray("MeltingTemps");
        activeTemps = tags.getIntArray("ActiveTemps");

        NBTTagList liquidTag = tags.getTagList("Liquids");
        moltenMetal.clear();

        for (int iter = 0; iter < liquidTag.tagCount(); iter++)
        {
            NBTTagCompound nbt = (NBTTagCompound) liquidTag.tagAt(iter);
            FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
            if (fluid != null)
                moltenMetal.add(fluid);
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
            center = new int[] { xCoord, yCoord, zCoord };
        else
            center = new int[] { centerPos.x, centerPos.y, centerPos.z };
        tags.setIntArray("CenterPos", center);

        tags.setByte("Direction", direction);
        tags.setInteger("UseTime", useTime);
        tags.setInteger("CurrentLiquid", currentLiquid);
        tags.setInteger("MaxLiquid", maxLiquid);
        tags.setInteger("Layers", layers);
        tags.setIntArray("MeltingTemps", meltingTemps);
        tags.setIntArray("ActiveTemps", activeTemps);

        NBTTagList taglist = new NBTTagList();
        for (FluidStack liquid : moltenMetal)
        {
            NBTTagCompound nbt = new NBTTagCompound();
            liquid.writeToNBT(nbt);
            taglist.appendTag(nbt);
        }
        tags.setTag("Liquids", taglist);
    }

    /* Packets */
    @Override
    public Packet getDescriptionPacket ()
    {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket (INetworkManager net, Packet132TileEntityData packet)
    {
        readFromNBT(packet.data);
        onInventoryChanged();
        worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
        this.needsUpdate = true;
    }

    // --- Not sure why we have to override these exactly, but whatever...
    
	@Override
	public int getSizeInventory() 
	{
		return inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) 
	{
		return inventory[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int quantity) 
	{
        if (inventory[slot] != null)
        {
            if (inventory[slot].stackSize <= quantity)
            {
                ItemStack stack = inventory[slot];
                inventory[slot] = null;
                return stack;
            }
            ItemStack split = inventory[slot].splitStack(quantity);
            if (inventory[slot].stackSize == 0)
            {
                inventory[slot] = null;
            }
            return split;
        }
        else
        {
            return null;
        }
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) 
	{
		return null;
	}

    @Override
    public void setInventorySlotContents (int slot, ItemStack itemstack)
    {
        inventory[slot] = itemstack;
        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit())
        {
            itemstack.stackSize = getInventoryStackLimit();
        }
    }

	@Override
    public String getInvName ()
    {
        return this.isInvNameLocalized() ? this.invName : getDefaultName();
    }

	@Override
    public boolean isInvNameLocalized ()
    {
        return this.invName != null && this.invName.length() > 0;
    }

    /* Supporting methods */
    @Override
    public boolean isUseableByPlayer (EntityPlayer entityplayer)
    {
        if (worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) != this)
            return false;
        else
            return entityplayer.getDistance((double) xCoord + 0.5D, (double) yCoord + 0.5D, (double) zCoord + 0.5D) <= 64D;
    }

	@Override
	public void openChest () { }

	@Override
	public void closeChest () { }

	@Override
    public boolean isItemValidForSlot (int slot, ItemStack itemstack)
    {
        if (slot < getSizeInventory())
        {
            if (inventory[slot] == null || itemstack.stackSize + inventory[slot].stackSize <= getInventoryStackLimit())
                return true;
        }
        return false;
    }

}
