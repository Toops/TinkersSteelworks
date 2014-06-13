package tsteelworks.blocks.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
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
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.oredict.OreDictionary;
import tconstruct.common.TContent;
import tconstruct.library.util.CoordTuple;
import tconstruct.library.util.IFacingLogic;
import tconstruct.library.util.IServantLogic;
import tsteelworks.common.TSContent;
import tsteelworks.inventory.DeepTankContainer;
import tsteelworks.lib.ConfigCore;
import tsteelworks.lib.ITSMasterLogic;
import tsteelworks.lib.crafting.AlloyInfo;

// TODO: Auto-generated Javadoc
/**
 * The Class DeepTankLogic.
 */
public class DeepTankLogic extends TileEntity implements IFacingLogic, IFluidTank, ITSMasterLogic
{
    
    /** The fluidlist. */
    private List<FluidStack> fluidlist = new ArrayList<FluidStack>();
    
    /** The structure has bottom. */
    private boolean structureHasBottom;
    
    /** The structure has top. */
    private boolean structureHasTop;
    
    /** The needs update. */
    private boolean needsUpdate;
    
    /** The contains alloy. */
    private boolean containsAlloy;
    
    /** The active turbine attached. */
    private boolean activeTurbineAttached;
    
    /** The valid structure. */
    private boolean validStructure;
    
    /** The direction. */
    private byte direction;
    
    /** The center pos. */
    private CoordTuple centerPos;
    
    /** The tick. */
    private int tick;
    
    /** The max liquid. */
    private int maxLiquid;
    
    /** The current liquid. */
    private int currentLiquid;
    
    /** The num bricks. */
    private int numBricks;
    
    /** The inner max x. */
    private int innerMaxX;
    
    /** The inner max z. */
    private int innerMaxZ;
    
    /** The layers. */
    private int layers;
    
    /** The Constant innerMaxSpace. */
    private static final int innerMaxSpace = 9; // Max amount of blocks inside in X/Z direction, TODO: config option
    
    /** The rand. */
    Random rand = new Random();
    
    /** The glass blocks. */
    @SuppressWarnings ("rawtypes")
    private ArrayList glassBlocks = getRegisteredGlassIDs();

    /**
     * Instantiates a new deep tank logic.
     */
    public DeepTankLogic() {
        super();
        this.innerMaxX = 0;
        this.innerMaxZ = 0;
        containsAlloy = false;
        activeTurbineAttached = false;
    }
    
    /**
     * Checks if is valid structure.
     *
     * @return true, if is valid structure
     */
    public final boolean isValidStructure() {
        return validStructure;
    }
    
    /**
     * X distance to rim.
     *
     * @return the int
     */
    public final int xDistanceToRim() {
    	return (getInnerMaxX() / 2) + 1; 
    }
    
    /**
     * Z distance to rim.
     *
     * @return the int
     */
    public final int zDistanceToRim() {
    	return (getInnerMaxZ() / 2) + 1; 
    }
    
    /**
     * Inner space total.
     *
     * @return the int
     */
    public final int innerSpaceTotal() {
    	return getInnerMaxX() * getInnerMaxZ(); 
    }
    
    /**
     * Layer fluid capacity.
     *
     * @return the int
     */
    public final int layerFluidCapacity() {
    	return (FluidContainerRegistry.BUCKET_VOLUME * ConfigCore.deeptankCapacityMultiplier) * innerSpaceTotal(); 
    }


    
    /**
     * Adjust layers.
     *
     * @param lay the lay
     * @param forceAdjust the force adjust
     */
    final void adjustLayers(final int lay, final boolean forceAdjust) {
    	//TODO : manage lay < 0 safety check
        if (lay != layers || forceAdjust) {
            needsUpdate = true;
            layers = lay;
            maxLiquid = layerFluidCapacity() * lay;
            //TSteelworks.loginfo("DTL - adjustLayers - maxLiquid="+maxLiquid);
        }
    }

    /* Misc */
    /**
     * Gets the gui container.
     *
     * @param inventoryplayer the inventoryplayer
     * @param world the world
     * @param x the x
     * @param y the y
     * @param z the z
     * @return the gui container
     */
    public final Container getGuiContainer(final InventoryPlayer inventoryplayer, final World world, final int x, final int y, final int z)
    {
        return new DeepTankContainer(inventoryplayer, this);
    }
     
   	/**
	    * Gets the center pos.
	    *
	    * @return the centerPos
	    */
	public final CoordTuple getCenterPos() {
		return centerPos;
	}

	/**
	 * Gets the inner max x.
	 *
	 * @return the innerMaxX
	 */
	public final int getInnerMaxX() {
		return innerMaxX;
	}

	/**
	 * Gets the inner max z.
	 *
	 * @return the innerMaxZ
	 */
	public final int getInnerMaxZ() {
		return innerMaxZ;
	}

	/**
	 * Sets the inner max z.
	 *
	 * @param innerMaxZ the innerMaxZ to set
	 */
	private void setInnerMaxZ(final int innerMaxZ) {
		this.innerMaxZ = innerMaxZ;
	}

	/**
	 * Sets the inner max x.
	 *
	 * @param innerMaxX the innerMaxX to set
	 */
	private final void setInnerMaxX(final int innerMaxX) {
		this.innerMaxX = innerMaxX;
	}

	/**
	 * Checks if is useable by player.
	 *
	 * @param entityplayer the entityplayer
	 * @return true, if is useable by player
	 */
	public final boolean isUseableByPlayer(final EntityPlayer entityplayer) {
		if (worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) != this) {
            return false;
		} else {
        
    	    if (worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) != this) {
    	        return false;
    	    } else {
    	        return entityplayer.getDistance(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64D;
    	    }
        }
	}
    
    /**
     * Gets the default name.
     *
     * @return the default name
     */
    public final String getDefaultName() {
    	return "tank.DeepTank"; 
    }
    
    
    /**
     * Checks if is structure valid.
     *
     * @return true structure is valid / false structure is not valid
     */
    public final boolean isStructureValid() {
        return this.validStructure;
    }

    /*
     * (non-Javadoc)
     * @see tconstruct.library.util.IFacingLogic#getRenderDirection()
     */
    @Override
    public final byte getRenderDirection() {
    	return direction; 
    }

    /*
     * (non-Javadoc)
     * @see tconstruct.library.util.IFacingLogic#getForgeDirection()
     */
    @Override
    public final ForgeDirection getForgeDirection() {
    	return ForgeDirection.VALID_DIRECTIONS[direction]; 
    }

    /*
     * (non-Javadoc)
     * @see tconstruct.library.util.IFacingLogic#setDirection(int)
     */
    @Override
    public void setDirection(final int side) {
    }

    /* (non-Javadoc)
     * @see tconstruct.library.util.IFacingLogic#setDirection(float, float, net.minecraft.entity.EntityLivingBase)
     */
    @Override
    public final void setDirection(final float yaw, final float pitch, final EntityLivingBase player)
    {
        int facing = MathHelper.floor_double((double) (yaw / 360) + 0.5D) & 3;
        switch (facing) {
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
        default:
        	break;
        }
    }
    
    /* (non-Javadoc)
     * @see net.minecraft.tileentity.TileEntity#updateEntity()
     */
    public final void updateEntity() {
        tick++;
        if (tick % 20 == 0) {
            if (!validStructure) {
                checkValidPlacement();
            }
            if (needsUpdate) {
                needsUpdate = false;
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            }
        }
        if (tick == 40) {
            if (activeTurbineAttached) {
                dealloyFluids();
            }
        }
        if (tick == 60) {
            tick = 0;
        }
    }

    /*
     * (non-Javadoc)
     * @see net.minecraft.tileentity.TileEntity#onInventoryChanged()
     */
    @Override
    public final void onInventoryChanged() {
        updateEntity();
        super.onInventoryChanged();
        needsUpdate = true;
    }

    
    
    /* Multiblock */
    
    /*
     * (non-Javadoc)
     * @see tconstruct.library.util.IMasterLogic#notifyChange(tconstruct.library.util.IServantLogic, int, int, int)
     */
    @Override
    public final void notifyChange(final IServantLogic servant, final int x, final int y, final int z)
    {
        this.checkValidPlacement();
    }

    /**
     * Check valid placement.
     */
    public final void checkValidPlacement() {
        final int[] center = this.scanGlassLayerCenter();
        this.alignControllerLayer(center[0], yCoord, center[1]);
    }
    
    /**
     * Align controller layer.
     *
     * @param x the x
     * @param y the y
     * @param z the z
     */
    final void alignControllerLayer(final int x, final int y, final int z) {
        // If new centralized coords didn't change, INVALID!
        if (x == xCoord && z == zCoord) {
        	return;
        }
        // Let's get those central points again...
        final int innerCenterX = this.xDistanceToRim();
        final int innerCenterZ = this.zDistanceToRim();
        // Just a little counter to pass along...
        int brickCounter = 0;
        int glassCounter = 0;
        // Set up a new block for scanning purposes
        Block block;
        // Scan inner for glass blocks by adjusted X/Z coordinates
        for (int xPos = x - innerCenterX; xPos <= x + innerCenterX; xPos++) { 
            for (int zPos = z - innerCenterZ; zPos <= z + innerCenterZ; zPos++) {
                block = Block.blocksList[worldObj.getBlockId(xPos, y, zPos)];
                if (block != null && this.validGlassID(block.blockID)) {
                    glassCounter += this.checkBricks(xPos, y, zPos, true);
                }
            }
        }
        
        // Scan outter for brick/drain blocks by adjusted X/Z coordinates
        for (int xPos = x - innerCenterX; xPos <= x + innerCenterX; xPos++) {
            brickCounter += this.checkBricks(xPos, y, z - innerCenterZ, false);
            brickCounter += this.checkBricks(xPos, y, z + innerCenterZ, false);
        }
        
        for (int zPos = z - (innerCenterZ - 1); zPos <= z + (innerCenterZ - 1); zPos++) {
            brickCounter += this.checkBricks(x - innerCenterX, y, zPos, false);
            brickCounter += this.checkBricks(x + innerCenterX, y, zPos, false);
        }
        
        if (!this.validateRimmedLayer(glassCounter, brickCounter)) {
        	return;
        }
        
        this.checkValidStructure(x, y, z, glassCounter + brickCounter);
    }
    
    
    // Wisthy - 2014/05/02 - solution for issue Toops#21, refactoring of the method
    /**
     * Check valid structure.
     *
     * @param x the x
     * @param y the y
     * @param z the z
     * @param compareBricks the compare bricks
     */
    public final void checkValidStructure(final int x, final int y, final int z, final int compareBricks) {
    	/*
    	 * store old validation variables
    	 */
    	final boolean oldStructureHasBottom = this.structureHasBottom;
    	final boolean oldStructureHasTop = this.structureHasTop;
    	
    	/*
    	 * reset all validation variables
    	 */
    	this.structureHasBottom = false;
    	this.structureHasTop = false;
    	
        int checkedLayers = 0;
        if (this.checkSameLevel(x, y, z, compareBricks)) {
            checkedLayers++;
            final int checkUp = this.recurseStructureUp(x, y + 1, z, 0, compareBricks);
            final int checkDown = this.recurseStructureDown(x, y - 1, z, 0, compareBricks);
            
            checkedLayers += checkUp;
            checkedLayers += checkDown;
            
            /*
             * count checkUp and checkDown work the same
             * it returns the number of layers without including the topLayer or bottomLayer
             * So, for a 3-high tank, the max value can be only 1.
             * So, the test below should be done "greater than 0" instead of "greater than 1" 
             */
            
            if (checkUp > 0 && !this.structureHasBottom) {
                this.validateBottom(x, y, z, 0, compareBricks);
            }
            if (checkDown > 0 && !this.structureHasTop) {
                this.validateTop(x, y, z, 0, compareBricks);
            }
        }
        
        if((oldStructureHasBottom != this.structureHasBottom) ||(oldStructureHasTop != this.structureHasTop) || (this.layers != checkedLayers))
        {
        	if (this.structureHasBottom && this.structureHasTop && checkedLayers > 0) {
        		// what if checkLayers == 0? <0?
        		// adjustLayers but set to validStructure = false?
        	    this.adjustLayers(checkedLayers, false);
        	    this.validStructure = true;
        	} else {
        	    this.validStructure = false;
        	}
        	worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }
    
    
    // trying to refactor the following methods to use only one - not ready yet
    // TODO wisthy - 2014/05/02 - check again the "recurse down" that use validateBottom instead of top
    // TODO wisthy - 2014/05/02 - use enum+switch to separate more clearly the distinct behavior
    

     /**
     * Check same level2.
     *
     * @param x the x
     * @param y the y
     * @param z the z
     * @param compareBricks the compare bricks
     * @return true, if successful
     */
    public final boolean checkSameLevel2(final int x, final int y, final int z, final int compareBricks) {
         return this.checkStructureGlobal(x, y, z, compareBricks, -1, 0, 0, 0) == 0;
     }

     /**
      * Recurse structure up2.
      *
      * @param x the x
      * @param y the y
      * @param z the z
      * @param count the count
      * @param compareBricks the compare bricks
      * @return the int
      */
     public final int recurseStructureUp2(final int x, final int y, final int z, final int count, final int compareBricks) {
         return this.checkStructureGlobal(x, y, z, compareBricks, count, -1, 1, -1); 
     }
     
     /**
      * Recurse structure down2.
      *
      * @param x the x
      * @param y the y
      * @param z the z
      * @param count the count
      * @param compareBricks the compare bricks
      * @return the int
      */
     public final int recurseStructureDown2(final int x, final int y, final int z, final int count, final int compareBricks) {
         return this.checkStructureGlobal(x, y, z, compareBricks, count, -1, -1, -1); 
     }

     /**
      * Check structure global.
      *
      * @param x the x
      * @param y the y
      * @param z the z
      * @param compareBricks the compare bricks
      * @param count the count
      * @param modX the mod x
      * @param modY the mod y
      * @param modZ the mod z
      * @return the int
      */
     public final int checkStructureGlobal(final int x, final int y, final int z, final int compareBricks, int count, final int modX, final int modY, final int modZ) {
         this.numBricks = 0;
         Block block = null;
         final int innerCenterX = this.xDistanceToRim();
         final int innerCenterZ = this.zDistanceToRim();
         
         final boolean isMiddleLayer = count > -1;

		 for (int xPos = x - (innerCenterX + modX); xPos <= x + (innerCenterX + modX); xPos++) {
			 for (int zPos = z - (innerCenterZ + modZ); zPos <= z + (innerCenterZ + modZ); zPos++) {
				 block = Block.blocksList[worldObj.getBlockId(xPos, y, zPos)];
				 if (block != null && this.validGlassID(block.blockID)) {
					 if (!isMiddleLayer) {
					     this.numBricks += this.checkBricks(xPos, y, zPos, false);
					 } else {
						 if ((block != null) && !block.isAirBlock(worldObj, xPos, y, zPos)) {
                            return (this.validGlassID(block.blockID)) ? this.validateTop(x, y, z, count, compareBricks) : count;
                        }
					 }
				 }
			 }
		 }
		 for (int xPos = x - innerCenterX; xPos <= x + innerCenterX; xPos++) {
		     this.numBricks += this.checkBricks(xPos, y, z - innerCenterZ, isMiddleLayer);
			 this.numBricks += this.checkBricks(xPos, y, z + innerCenterZ, isMiddleLayer);
		 }
		 for (int zPos = z - (innerCenterZ - 1); zPos <= z + (innerCenterZ - 1); zPos++) { 
		     this.numBricks += this.checkBricks(x - innerCenterX, y, zPos, isMiddleLayer);
		     this.numBricks += this.checkBricks(x + innerCenterX, y, zPos, isMiddleLayer);
        }
		 if (!isMiddleLayer) {
			 return (this.numBricks == compareBricks) ? 0 : 1;
		 } else {
			 if (this.numBricks != compareBricks - this.innerSpaceTotal()) {
                return count;
			 }
             return this.checkStructureGlobal(x, y + modY, z, compareBricks, count, modX, modY, modZ);
         }
     }
    
    
    // Redundancy at its finest.
    /**
     * Check same level.
     *
     * @param x the x
     * @param y the y
     * @param z the z
     * @param compareBricks the compare bricks
     * @return true, if successful
     */
    public final boolean checkSameLevel(final int x, final int y, final int z, final int compareBricks)
    {
        numBricks = 0;
        Block block;
        int innerCenterX = xDistanceToRim();
        int innerCenterZ = zDistanceToRim();
        for (int xPos = x - innerCenterX; xPos <= x + innerCenterX; xPos++)
        {
            for (int zPos = z - innerCenterZ; zPos <= z + innerCenterZ; zPos++)
            {
                block = Block.blocksList[worldObj.getBlockId(xPos, y, zPos)];
                if (block != null && validGlassID(block.blockID)) {
                    numBricks += this.checkBricks(xPos, y, zPos, true);
                }
            }
        }
        for (int xPos = x - innerCenterX; xPos <= x + innerCenterX; xPos++)
        {
            numBricks += this.checkBricks(xPos, y, z - (innerCenterZ), false);
            numBricks += this.checkBricks(xPos, y, z + (innerCenterZ), false);
        }
        for (int zPos = z - (innerCenterZ - 1); zPos <= z + (innerCenterZ - 1); zPos++)
        {
            numBricks += this.checkBricks(x - innerCenterX, y, zPos, false);
            numBricks += this.checkBricks(x + innerCenterX, y, zPos, false);
        }
        return (numBricks == compareBricks);
    }

    /**
     * Recurse structure up.
     *
     * @param x the x
     * @param y the y
     * @param z the z
     * @param count the count
     * @param compareBricks the compare bricks
     * @return the int
     */
    public final int recurseStructureUp(final int x, final int y, final int z, int count, final int compareBricks)
    {
        this.numBricks = 0;
        //Check inside
        final int innerCenterX = xDistanceToRim();
        final int innerCenterZ = zDistanceToRim();

        Block block;
        for (int xPos = x - (innerCenterX - 1); xPos <= x + (innerCenterX - 1); xPos++)
        {
            for (int zPos = z - (innerCenterZ - 1); zPos <= z + (innerCenterZ - 1); zPos++)
            {
                block = Block.blocksList[worldObj.getBlockId(xPos, y, zPos)];
                if ((block != null) && this.validGlassID(block.blockID)) {
                    // previous line: validGlassId(block) <==> next line: !block.isAirBlock
                    // so, it's a glass block that is not an air block. Is there any glass blocks that are air blocks?  
                    if ((block != null) && !block.isAirBlock(worldObj, xPos, y, zPos)) {
                        return (this.validGlassID(block.blockID)) ? this.validateTop(x, y, z, count, compareBricks) : count;
                    }
                }
            }
        }
        //Check outer layer
        for (int xPos = x - innerCenterX; xPos <= x + innerCenterX; xPos++) {
            numBricks += this.checkBricks(xPos, y, z - (innerCenterZ), true);
            numBricks += this.checkBricks(xPos, y, z + (innerCenterZ), true);
        }
        for (int zPos = z - (innerCenterZ - 1); zPos <= z + (innerCenterZ - 1); zPos++) {
            numBricks += this.checkBricks(x - innerCenterX, y, zPos, true);
            numBricks += this.checkBricks(x + innerCenterX, y, zPos, true);
        }

        if (numBricks != compareBricks - innerSpaceTotal()) {
            return count;
        }

        count++;
        return this.recurseStructureUp(x, y + 1, z, count, compareBricks);
    }

    /**
     * Recurse structure down.
     *
     * @param x the x
     * @param y the y
     * @param z the z
     * @param count the count
     * @param compareBricks the compare bricks
     * @return the int
     */
    public final int recurseStructureDown(final int x, final int y, final int z, int count, final int compareBricks)
    {
        numBricks = 0;
        //Check inside
        int innerCenterX = xDistanceToRim();
        int innerCenterZ = zDistanceToRim();
        Block block;
        for (int xPos = x - (innerCenterX - 1); xPos <= x + (innerCenterX - 1); xPos++)
        {
            for (int zPos = z - (innerCenterZ - 1); zPos <= z + (innerCenterZ - 1); zPos++)
            {
                block = Block.blocksList[worldObj.getBlockId(xPos, y, zPos)];
                if ((block != null) && validGlassID(block.blockID))
                {
                    if ((block != null) && !block.isAirBlock(worldObj, xPos, y, zPos))
                    {
                        return (validGlassID(block.blockID)) ? validateBottom(x, y, z, count, compareBricks) : count;
                    }
                }
            }
        }
        //Check outer layer
        for (int xPos = x - innerCenterX; xPos <= x + innerCenterX; xPos++)
        {
            numBricks += this.checkBricks(xPos, y, z - (innerCenterZ), true);
            numBricks += this.checkBricks(xPos, y, z + (innerCenterZ), true);
        }
        for (int zPos = z - (innerCenterZ - 1); zPos <= z + (innerCenterZ - 1); zPos++)
        {
            numBricks += this.checkBricks(x - innerCenterX, y, zPos, true);
            numBricks += this.checkBricks(x + innerCenterX, y, zPos, true);
        }
        if (numBricks != compareBricks - innerSpaceTotal())
        {
            return count;
        }

        count++;
        return recurseStructureDown(x, y - 1, z, count, compareBricks);
    }
    
    // TODO Wisthy - 2014/04/26 - can it be fitted in the "global" method too? Need more reflexion time on this
    /**
     * Validate top.
     *
     * @param x the x
     * @param y the y
     * @param z the z
     * @param count the count
     * @param compareBricks the compare bricks
     * @return the int
     */
    public final int validateTop(final int x, final int y, final int z, final int count, final int compareBricks)
    {
        int topBricks = 0;
        
        final int innerCenterX = this.xDistanceToRim();
        final int innerCenterZ = this.zDistanceToRim();
        
        for (int xPos = x - innerCenterX; xPos <= x + innerCenterX; xPos++) {
            for (int zPos = z - innerCenterZ; zPos <= z + innerCenterZ; zPos++) {
                if (this.validGlassID(worldObj.getBlockId(xPos, y, zPos))) {
                    topBricks += this.checkBricks(xPos, y, zPos, true);
                }
            }
        }
        
        //Check outer rim
        for (int xPos = x - innerCenterX; xPos <= x + innerCenterX; xPos++) {
            topBricks += this.checkBricks(xPos, y, z - innerCenterZ, false);
            topBricks += this.checkBricks(xPos, y, z + innerCenterZ, false);
        }
        for (int zPos = z - (innerCenterZ - 1); zPos <= z + (innerCenterZ - 1); zPos++){
            topBricks += this.checkBricks(x - innerCenterX, y, zPos, false);
            topBricks += this.checkBricks(x + innerCenterX, y, zPos, false);
        }
        this.structureHasTop = topBricks == compareBricks;
        return count;
    }
    
    // TODO Wisthy - 2014/04/26 - can it be fitted in the "global" method too? Need more reflexion time on this
    /**
     * Validate bottom.
     *
     * @param x the x
     * @param y the y
     * @param z the z
     * @param count the count
     * @param compareBricks the compare bricks
     * @return the int
     */
    public final int validateBottom(final int x, final int y, final int z, int count, final int compareBricks)
    {
        int bottomBricks = 0;
        final int innerCenterX = xDistanceToRim();
        final int innerCenterZ = zDistanceToRim();
        for (int xPos = x - innerCenterX; xPos <= x + innerCenterX; xPos++) {
            for (int zPos = z - innerCenterZ; zPos <= z + innerCenterZ; zPos++)
            {
                if (this.validGlassID(worldObj.getBlockId(xPos, y, zPos))) {
                    bottomBricks += this.checkBricks(xPos, y, zPos, true);
                }
            }
        }
        //Check outer layer
        for (int xPos = x - innerCenterX; xPos <= x + innerCenterX; xPos++) {
            bottomBricks += this.checkBricks(xPos, y, z - innerCenterZ, false);
            bottomBricks += this.checkBricks(xPos, y, z + innerCenterZ, false);
        }
        for (int zPos = z - (innerCenterZ - 1); zPos <= z + (innerCenterZ - 1); zPos++)
        {
            bottomBricks += this.checkBricks(x - innerCenterX, y, zPos, false);
            bottomBricks += this.checkBricks(x + innerCenterX, y, zPos, false);
        }
        this.structureHasBottom = bottomBricks == compareBricks;
        if (this.structureHasBottom) {
            this.centerPos = new CoordTuple(x, y + 1, z);
        }
        return count;
    }

    /* Returns whether the brick is a lava tank or not.
     * Increments bricks, sets them as part of the structure, and adds tanks to the list.
     */
    /**
     * Check bricks.
     *
     * @param x the x
     * @param y the y
     * @param z the z
     * @param glassOnly the glass only
     * @return the int
     */
    @SuppressWarnings("deprecation")
    final int checkBricks(final int x, final int y, final int z, final boolean glassOnly) {
        int tempBricks = 0;
        final int blockID = worldObj.getBlockId(x, y, z);
        if (glassOnly && (this.validGlassID(blockID))) {
            tempBricks++;
        }
        if (glassOnly && this.validBlockID(blockID)) {
            final TileEntity te = worldObj.getBlockTileEntity(x, y, z);
            if (te instanceof HighOvenDuctLogic) {
                return tempBricks++;
            } else if (te instanceof TSMultiServantLogic) {
                final TSMultiServantLogic servant = (TSMultiServantLogic) te;
                if (servant.hasValidMaster()) {
                    if (servant.verifyMaster(this, this.xCoord, this.yCoord, this.zCoord)) {
                        tempBricks++;
                    }
                } else if (servant.setMaster(this.xCoord, this.yCoord, this.zCoord)) {
                    tempBricks++;
                }
            }
        }
        if (!glassOnly && this.validBlockID(blockID)) {
            final TileEntity te = worldObj.getBlockTileEntity(x, y, z);
            if (te == this) {
                tempBricks++;
            }
            if (te instanceof HighOvenDuctLogic) {
                return tempBricks++;
            } else if (te instanceof TSMultiServantLogic) {
                if (te instanceof HighOvenDuctLogic) {
                    return tempBricks++;
                }
                final TSMultiServantLogic servant = (TSMultiServantLogic) te;
                if (servant.hasValidMaster()) {
                    if (servant.verifyMaster(this, this.xCoord, this.yCoord, this.zCoord)) {
                        tempBricks++;
                    }
                } else if (servant.setMaster(this.xCoord, this.yCoord, this.zCoord)) {
                    tempBricks++;
                }
            }
        }
        return tempBricks;
    }
    
    /**
     * Verify component.
     *
     * @param tileentity the tileentity
     * @return the int
     */
    @SuppressWarnings ("deprecation")
    final int verifyComponent(final TileEntity tileentity) {
        int tempBricks = 0;
        if (tileentity instanceof HighOvenDuctLogic) {
            return tempBricks++;
        } else if (tileentity instanceof TSMultiServantLogic) {
            final TSMultiServantLogic servant = (TSMultiServantLogic) tileentity;
            if (servant.hasValidMaster()) {
                if (servant.verifyMaster(this, this.xCoord, this.yCoord, this.zCoord)) {
                    tempBricks++;
                }
            } else if (servant.setMaster(this.xCoord, this.yCoord, this.zCoord)) {
                tempBricks++;
            }
        }
        return tempBricks;
    }
    
    // TODO: Clean this mess up.
    /**
     * Scan glass layer center.
     *
     * @return the int[]
     */
    private int[] scanGlassLayerCenter() {
        int centerX = 0;
        int centerZ = 0;
        Block block;
        switch (this.getRenderDirection()) {
        case 2: // +z
            // Scan to last block
            for (int z = zCoord + 1; z < zCoord + (innerMaxSpace + 1); z++) {
                block = Block.blocksList[worldObj.getBlockId(xCoord, yCoord, z)];
                if (block != null && this.validGlassID(block.blockID)) {
                    centerZ += this.checkBricks(xCoord, yCoord, z, true);
                } else {
                    break;
                }
            }
            // Adjust depth scan to center
            if ((centerZ != 1) && (centerZ % 2 == 0)) {
                break;
            } else {
                this.setInnerMaxZ(centerZ);
                centerZ = centerZ / 2 + 1; 
            }
            if (centerZ == 0) {
                break;
            }
            // Scan width from center
            for (int x = xCoord; x >= xCoord - (innerMaxSpace / 2); x--) {
                block = Block.blocksList[worldObj.getBlockId(x, yCoord, zCoord + centerZ)];
                if (block != null && this.validGlassID(block.blockID)) {
                    centerX += this.checkBricks(x, yCoord, zCoord + centerZ, true);
                } else {
                    break;
                }
            }
            for (int x = xCoord + 1; x <= xCoord + (innerMaxSpace / 2); x++) {
                block = Block.blocksList[worldObj.getBlockId(x, yCoord, zCoord + centerZ)];
                if (block != null && this.validGlassID(block.blockID)) {
                    centerX += this.checkBricks(x, yCoord, zCoord + centerZ, true);
                } else {
                    break;
                }
            }
            // Adjust width to center
            if ((centerX != 1) && (centerX % 2 == 0)) {
                break;
            } else {
                this.setInnerMaxX(centerX);
                centerX = centerX / 2 + 1; 
            }
            if (centerX == 0) {
                break;
            }
            return new int[] {xCoord, zCoord + centerZ};
        case 3: // -z
            for (int z = zCoord - 1; z > zCoord - (innerMaxSpace + 1); z--) {
                block = Block.blocksList[worldObj.getBlockId(xCoord, yCoord, z)];
                if (block != null && this.validGlassID(block.blockID)) {
                    centerZ += this.checkBricks(xCoord, yCoord, z, true);
                } else {
                    break;
                }
            }
            // Adjust depth scan to center
            if ((centerZ != 1) && (centerZ % 2 == 0)) {
                break;
            } else {
                this.setInnerMaxZ(centerZ);
                centerZ = centerZ / 2 + 1; 
            }
            if (centerZ == 0) {
                break;
            }
            // Scan width from center
            for (int x = xCoord; x >= xCoord - (innerMaxSpace / 2); x--) {
                block = Block.blocksList[worldObj.getBlockId(x, yCoord, zCoord - centerZ)];
                if (block != null && this.validGlassID(block.blockID)) {
                    centerX += this.checkBricks(x, yCoord, zCoord - centerZ, true);
                } else { 
                    break; 
                }
            }
            for (int x = xCoord + 1; x <= xCoord + (innerMaxSpace / 2); x++) {
                block = Block.blocksList[worldObj.getBlockId(x, yCoord, zCoord - centerZ)];
                if (block != null && this.validGlassID(block.blockID)) {
                    centerX += this.checkBricks(x, yCoord, zCoord - centerZ, true);
                } else { 
                    break; 
                }
            }
            
            // Adjust width to center
            if ((centerX != 1) && (centerX % 2 == 0)) {
                break;
            } else {
                this.setInnerMaxX(centerX);
                centerX = centerX / 2 + 1; 
            }
            if (centerX == 0) {
                break;
            }
            return new int[] {xCoord, zCoord - centerZ};
        case 4: // +x
            for (int x = xCoord + 1; x < xCoord + (innerMaxSpace + 1); x++) {
                block = Block.blocksList[worldObj.getBlockId(x, yCoord, zCoord)];
                if (block != null && this.validGlassID(block.blockID)) {
                    centerX += this.checkBricks(x, yCoord, zCoord, true);
                } else {
                    break;
                }
            }
            // Adjust depth scan to center
            if ((centerX != 1) && (centerX % 2 == 0)) {
                break;
            } else {
                this.setInnerMaxX(centerX);
                centerX = centerX / 2 + 1; 
            }
            if (centerX == 0) {
                break;
            }
            // Scan length from center
            for (int z = zCoord; z >= zCoord - (innerMaxSpace / 2); z--) {
                block = Block.blocksList[worldObj.getBlockId(xCoord + centerX, yCoord, z)];
                if (block != null && this.validGlassID(block.blockID)) {
                    centerZ += this.checkBricks(xCoord + centerX, yCoord, z, true);
                } else {
                    break;
                }
            }
            for (int z = zCoord + 1; z <= zCoord + (innerMaxSpace / 2); z++) {
                block = Block.blocksList[worldObj.getBlockId(xCoord + centerX, yCoord, z)];
                if (block != null && this.validGlassID(block.blockID)) {
                    centerZ += this.checkBricks(xCoord + centerX, yCoord, z, true);
                } else {
                    break;
                }
            }
            // Adjust length to center
            if ((centerZ != 1) && (centerZ % 2 == 0)) {
                break;
            } else {
                this.setInnerMaxZ(centerZ);
                centerZ = centerZ / 2 + 1; 
            }
            if (centerZ == 0) {
                break;
            }
            return new int[] {xCoord + centerX, zCoord};
        case 5: // -x
            for (int x = xCoord - 1; x > xCoord - (innerMaxSpace + 1); x--) {
                block = Block.blocksList[worldObj.getBlockId(x, yCoord, zCoord)];
                if (block != null && this.validGlassID(block.blockID)) {
                    centerX += this.checkBricks(x, yCoord, zCoord, true);
                } else {
                    break;
                }
            }
            // Adjust depth scan to center
            if ((centerX != 1) && (centerX % 2 == 0)) {
                break;
            } else {
                this.setInnerMaxX(centerX);
                centerX = (centerX / 2 + 1); 
            }
            if (centerX == 0) {
                break;
            }
            // Scan length from center
            for (int z = zCoord; z >= zCoord - (innerMaxSpace / 2); z--) {
                block = Block.blocksList[worldObj.getBlockId(xCoord - centerX, yCoord, z)];
                if (block != null && this.validGlassID(block.blockID)) {
                    centerZ += this.checkBricks(xCoord - centerX, yCoord, z, true);
                } else {
                    break;
                }
            }
            for (int z = zCoord + 1; z <= zCoord + (innerMaxSpace / 2); z++) {
                block = Block.blocksList[worldObj.getBlockId(xCoord - centerX, yCoord, z)];
                if (block != null && this.validGlassID(block.blockID)) {
                    centerZ += checkBricks(xCoord - centerX, yCoord, z, true);
                } else {
                    break;
                }
            }
            // Adjust length to center
            if ((centerZ != 1) && (centerZ % 2 == 0)) {
                break;
            } else {
                this.setInnerMaxZ(centerZ);
                centerZ = centerZ / 2 + 1; 
            }
            if (centerZ == 0) {
                break;
            }
            return new int[] {xCoord - centerX, zCoord};
            default:
                break;
        }
        return new int[] {xCoord, yCoord};
    }
    
    // TODO: Algorithms, man. 
    /**
     * Validate rimmed layer.
     *
     * @param innerbricks the innerbricks
     * @param outerbricks the outerbricks
     * @return true, if successful
     */
    public final boolean validateRimmedLayer(final int innerbricks, final int outerbricks)
    {
        final int total = innerbricks + outerbricks;
        if (this.getInnerMaxX() == 1 || this.getInnerMaxZ() == 1) {
            return Arrays.asList(9, 15, 21, 27, 33).contains(total);
        }
        else if (this.getInnerMaxX() == 3 || this.getInnerMaxZ() == 3) {
            return Arrays.asList(25, 35, 45, 55).contains(total);
        }
        else if (this.getInnerMaxX() == 5 || this.getInnerMaxZ() == 5) {
            return Arrays.asList(49, 63, 77).contains(total);
        }
        else if (this.getInnerMaxX() == 7 || this.getInnerMaxZ() == 7) {
            return Arrays.asList(81, 99).contains(total);
        }
        else if (this.getInnerMaxX() == 9 || this.getInnerMaxZ() == 9) {
            return total == 121;
        }
        return false;
    }
    
    /**
     * Valid block id.
     *
     * @param blockID the block id
     * @return true, if successful
     */
    final boolean validBlockID(final int blockID) {
        return blockID == TSContent.highoven.blockID;
    }

    /**
     * Valid glass id.
     *
     * @param blockID the block id
     * @return true, if successful
     */
    final boolean validGlassID(final int blockID) {
        return this.glassBlocks.contains(blockID);
    }
    
    /**
     * Gets the registered glass i ds.
     *
     * @return the registered glass i ds
     */
    @SuppressWarnings ({ "unchecked", "rawtypes" })
    final
    /*
     * Set up a list of glass blocks by preset, config, and oredictionary
     * Duplicate elements are removed from the list
     */
    ArrayList getRegisteredGlassIDs() {
    	final ArrayList<ItemStack> oreDict = OreDictionary.getOres("glass");
    	ArrayList glasses = new ArrayList();
    	
    	glasses.add(Block.blocksList[Block.glass.blockID]);
        glasses.add(Block.blocksList[TContent.clearGlass.blockID]);
        glasses.add(Block.blocksList[TContent.stainedGlassClear.blockID]);
        glasses.add(Block.blocksList[TContent.lavaTank.blockID]);
        
    	if (ConfigCore.modTankGlassBlocks.length >= 1) {
            for (int id : ConfigCore.modTankGlassBlocks) {
                glasses.add(Block.blocksList[id]);
            }
        }
        
    	if (!oreDict.isEmpty())	{
    		for (ItemStack glass : oreDict) {
    			glasses.add(Block.blocksList[glass.itemID].blockID);
    		}
            // Let's remove those duplicates
            HashSet temp = new HashSet();
            temp.addAll(glasses);
            glasses.clear();
            glasses.addAll(temp);
            
    		return glasses;
    	} else {
    		return oreDict;
    	}
    }
    
    /**
     * Gets the fluidlist.
     *
     * @return the fluidlist
     */
    public final List<FluidStack> getFluidlist() {
		return this.fluidlist;
	}

	/*
     * (non-Javadoc)
     * @see net.minecraftforge.fluids.IFluidTank#getCapacity()
     */
    @Override
    public final int getCapacity() {
        return this.maxLiquid;
    }

    /**
     * Gets the total liquid.
     *
     * @return the total liquid
     */
    public final int getTotalLiquid() {
        return this.currentLiquid;
    }

    /*
     * (non-Javadoc)
     * @see net.minecraftforge.fluids.IFluidTank#drain(int, boolean)
     */
    @Override
    public final FluidStack drain(final int maxDrain, final boolean doDrain) {
        if (!this.isStructureValid()) {
            //TSteelworks.loginfo("DTL - drain - invalid strucutre, refused");
            return null;
        } else {
          //TSteelworks.loginfo("DTL - drain - valid strucutre, allowed");
        }
        
        if (this.fluidlist.size() == 0) {
            return null;
        }

        final FluidStack liquid = this.fluidlist.get(0);
        if (liquid != null) {
            if (liquid.amount - maxDrain <= 0) {
                final FluidStack liq = liquid.copy();
                if (doDrain) {
                    //liquid = null;
                    this.fluidlist.remove(liquid);
                    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                    this.currentLiquid = 0;
                    this.needsUpdate = true;
                }
                this.containsAlloy = this.containsAlloy();
                return liq;
            } else {
                if (doDrain && maxDrain > 0) {
                    liquid.amount -= maxDrain;
                    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                    this.currentLiquid -= maxDrain;
                    this.needsUpdate = true;
                }
                this.containsAlloy = this.containsAlloy();
                return new FluidStack(liquid.fluidID, maxDrain, liquid.tag);
            }
        } else {
            this.containsAlloy = this.containsAlloy();
            return new FluidStack(0, 0);
        }
    }

    /*
     * (non-Javadoc)
     * @see net.minecraftforge.fluids.IFluidTank#fill(net.minecraftforge.fluids.FluidStack, boolean)
     */
    @Override
    public final int fill(final FluidStack resource, final boolean doFill) {
        if (!this.isStructureValid()) {
            //TSteelworks.loginfo("DTL - fill - invalid strucutre, refused");
            return 0;
        } else {
            //TSteelworks.loginfo("DTL - fill - valid strucutre, allowed");
        }
        if (resource == null) {
            return 0;
        }
        final FluidStack copy = resource.copy();
        this.addFluidToTank(copy, false);

        return resource.amount - copy.amount;
    }
    
    /**
     * Adds the fluid to tank.
     *
     * @param liquid the liquid
     * @param first the first
     * @return true, if successful
     */
    final boolean addFluidToTank(final FluidStack liquid, final boolean first) {
        if (!this.isStructureValid()) {
            //TSteelworks.loginfo("DTL - addFluidToTank - invalid strucutre, refused");
            return false;
        } else {
            //TSteelworks.loginfo("DTL - addFluidToTank - valid strucutre, allowed");
        }

        this.needsUpdate = true;
        if (this.fluidlist.size() == 0) {
            this.fluidlist.add(liquid.copy());
            this.currentLiquid += liquid.amount;
            this.containsAlloy = this.containsAlloy();
            return true;
        } else {
            if (liquid.amount + this.currentLiquid > this.maxLiquid) {
                return false;
            }
            this.currentLiquid += liquid.amount;
            boolean added = false;
            for (int i = 0; i < this.fluidlist.size(); i++) {
                FluidStack l = this.fluidlist.get(i);
                if (l.isFluidEqual(liquid)) {
                    l.amount += liquid.amount;
                    added = true;
                }
                if (l.amount <= 0) {
                    this.fluidlist.remove(l);
                    i--;
                }
            }
            if (!added) {
                if (first) {
                    this.fluidlist.add(0, liquid.copy());
                } else {
                    this.fluidlist.add(liquid.copy());
                }
            }
            this.containsAlloy = this.containsAlloy();
            return true;
        }
    }
    
    /*
     * (non-Javadoc)
     * @see net.minecraftforge.fluids.IFluidTank#getFluid()
     */
    @Override
    public final FluidStack getFluid() {
        if (!this.isStructureValid()) {
            //TSteelworks.loginfo("DTL - getFluid - invalid strucutre, refused");
            return null;
        } else {
            //TSteelworks.loginfo("DTL - getFluid - valid strucutre, allowed");
        }
        
        if (this.fluidlist.size() == 0) {
            return null;
        }
        
        return this.fluidlist.get(0);
    }
    
    /**
     * Gets the all fluids.
     *
     * @return the all fluids
     */
    public final List<FluidStack> getAllFluids() { return this.fluidlist; }
    
    /**
     * Gets the total fluid amount.
     *
     * @return the total fluid amount
     */
    public final int getTotalFluidAmount() {
        if (this.fluidlist.size() == 0) {
            return this.currentLiquid;
        }
        
        int amt = 0;
        
        for (int i = 0; i < this.fluidlist.size(); i++) {
            FluidStack l = this.fluidlist.get(i);
            amt += l.amount;
        }
        return amt;
    }
    
    /*
     * (non-Javadoc)
     * @see net.minecraftforge.fluids.IFluidTank#getFluidAmount()
     */
    @Override
    public final int getFluidAmount() { 
        return this.currentLiquid;
    }

    /**
     * Gets the fill ratio.
     *
     * @return the fill ratio
     */
    public final int getFillRatio() { 
        return this.currentLiquid <= 0 ? 0 : this.maxLiquid / this.getTotalFluidAmount();
    }
    
    /*
     * (non-Javadoc)
     * @see net.minecraftforge.fluids.IFluidTank#getInfo()
     */
    @Override
    public final FluidTankInfo getInfo() { 
        return new FluidTankInfo(this);
    }

    /**
     * Gets the multi tank info.
     *
     * @return the multi tank info
     */
    public final FluidTankInfo[] getMultiTankInfo() {
        final FluidTankInfo[] info = new FluidTankInfo[this.fluidlist.size() + 1];
        for (int i = 0; i < this.fluidlist.size(); i++) {
            final FluidStack fluid = this.fluidlist.get(i);
            info[i] = new FluidTankInfo(fluid.copy(), fluid.amount);
        }
        info[this.fluidlist.size()] = new FluidTankInfo(null, this.maxLiquid - this.currentLiquid);
        return info;
    }

    /**
     * Sets the turbine attached.
     *
     * @param flag the new turbine attached
     */
    public final void setTurbineAttached(final boolean flag) {
        this.activeTurbineAttached = flag;
    }
    
    /**
     * Checks if is turbine attached.
     *
     * @return true, if is turbine attached
     */
    public final boolean isTurbineAttached() {
        return this.activeTurbineAttached;
    }
    
    /**
     * Contains alloy.
     *
     * @return true, if successful
     */
    final boolean containsAlloy() {
        for (FluidStack fluid : this.fluidlist) {
            if (this.fluidIsAlloy(fluid)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Fluid is alloy.
     *
     * @param fluidstack the fluidstack
     * @return true, if successful
     */
    final boolean fluidIsAlloy(final FluidStack fluidstack) {
        for (int i = 0; i < AlloyInfo.alloys.size(); ++i) {
            if (fluidstack.getFluid() == AlloyInfo.alloys.get(i).result.copy().getFluid()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Dealloy fluids.
     */
    final void dealloyFluids() {
        if (!this.isStructureValid()) {
            //TSteelworks.loginfo("DTL - dealloyFluids - invalid strucutre, refused");
            return;
        } else {
            //TSteelworks.loginfo("DTL - dealloyFluids - valid strucutre, allowed");
        }
        
        if (!this.containsAlloy) {
            return;
        }
        for (int i = 0; i < this.fluidlist.size(); ++i) {
            final FluidStack alloy = this.fluidlist.get(i).copy();
            if (!this.fluidIsAlloy(alloy)) {
                continue;
            }
            
            final ArrayList<FluidStack> fluids = AlloyInfo.deAlloy(alloy);
            this.fluidlist.remove(i);
            
            for (int j = 0; j < fluids.size(); j++) {
                final FluidStack liquid = (FluidStack) fluids.get(j);
                this.addFluidToTank(liquid, true);
            }
        }
    }
    
    /* NBT */

    /*
     * (non-Javadoc)
     * @see net.minecraft.tileentity.TileEntity#readFromNBT(net.minecraft.nbt.NBTTagCompound)
     */
    @Override
    public final void readFromNBT(final NBTTagCompound tags) {
        this.layers = tags.getInteger("Layers");
        this.setInnerMaxX(tags.getInteger("InnerMaxX"));
        this.setInnerMaxZ(tags.getInteger("InnerMaxZ"));
        super.readFromNBT(tags);
        //validStructure = tags.getBoolean("ValidStructure");
        this.containsAlloy = tags.getBoolean("ContainsAlloy");
        final int[] center = tags.getIntArray("CenterPos");
        if (center.length > 2) {
            this.centerPos = new CoordTuple(center[0], center[1], center[2]);
        } else {
            this.centerPos = new CoordTuple(xCoord, yCoord, zCoord);
        }
        this.direction = tags.getByte("Direction");
        this.currentLiquid = tags.getInteger("CurrentLiquid");
        this.maxLiquid = tags.getInteger("MaxLiquid");
        final NBTTagList liquidTag = tags.getTagList("Liquids");
        this.fluidlist.clear();

        for (int iter = 0; iter < liquidTag.tagCount(); iter++) {
            final NBTTagCompound nbt = (NBTTagCompound) liquidTag.tagAt(iter);
            final FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
            if (fluid != null) {
                this.fluidlist.add(fluid);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see net.minecraft.tileentity.TileEntity#writeToNBT(net.minecraft.nbt.NBTTagCompound)
     */
    @Override
    public final void writeToNBT(final NBTTagCompound tags) {
        super.writeToNBT(tags);
//        tags.setBoolean("ValidStructure", validStructure);
        tags.setBoolean("ContainsAlloy", this.containsAlloy);
        int[] center = new int[3];
        if (this.centerPos == null) {
            center = new int[] { xCoord, yCoord, zCoord };
        } else {
            center = new int[] { this.centerPos.x, this.centerPos.y, this.centerPos.z };
        }
        tags.setIntArray("CenterPos", center);
        tags.setByte("Direction", this.direction);
        tags.setInteger("CurrentLiquid", this.currentLiquid);
        tags.setInteger("MaxLiquid", this.maxLiquid);
        tags.setInteger("InnerMaxZ", this.getInnerMaxZ());
        tags.setInteger("InnerMaxX", this.getInnerMaxX());
        tags.setInteger("Layers", this.layers);

        final NBTTagList taglist = new NBTTagList();
        for (FluidStack liquid : this.fluidlist) {
            final NBTTagCompound nbt = new NBTTagCompound();
            liquid.writeToNBT(nbt);
            taglist.appendTag(nbt);
        }

        tags.setTag("Liquids", taglist);
    }

    
    
    /* Packets */
    
    /*
     * (non-Javadoc)
     * @see net.minecraft.tileentity.TileEntity#getDescriptionPacket()
     */
    @Override
    public final Packet getDescriptionPacket() {
        final NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag);
    }

    /*
     * (non-Javadoc)
     * @see net.minecraft.tileentity.TileEntity#onDataPacket(net.minecraft.network.INetworkManager, net.minecraft.network.packet.Packet132TileEntityData)
     */
    @Override
    public final void onDataPacket(final INetworkManager net, final Packet132TileEntityData packet) {
        this.readFromNBT(packet.data);
        this.onInventoryChanged();
        worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
        this.needsUpdate = true;
    }
    
    
    /* =============== IMaster =============== */
    
    /*
     * (non-Javadoc)
     * @see tsteelworks.lib.IMaster#getCoord()
     */
    //@Override
    /**
     * Gets the coord.
     *
     * @return the coord
     */
    public final CoordTuple getCoord() {
        return new CoordTuple(xCoord, yCoord, zCoord);
    }

    /*
     * (non-Javadoc)
     * @see tsteelworks.lib.IMaster#isValid()
     */
    //@Override
    public final boolean isValid() {
        return this.validStructure;
    }

	/*
	 * (non-Javadoc)
	 * @see tsteelworks.lib.IMaster#getBlockId()
	 */
	//@Override
	/**
	 * Gets the block id.
	 *
	 * @return the block id
	 */
	public final int getBlockId() {
		return this.worldObj.getBlockId(xCoord, yCoord, zCoord);
	}
}
