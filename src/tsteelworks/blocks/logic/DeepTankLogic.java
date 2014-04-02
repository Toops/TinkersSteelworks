package tsteelworks.blocks.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
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
import tconstruct.TConstruct;
import tconstruct.blocks.logic.MultiServantLogic;
import tconstruct.blocks.logic.SmelteryDrainLogic;
import tconstruct.common.TContent;
import tconstruct.library.util.CoordTuple;
import tconstruct.library.util.IFacingLogic;
import tconstruct.library.util.IMasterLogic;
import tconstruct.library.util.IServantLogic;
import tsteelworks.TSteelworks;
import tsteelworks.common.TSContent;
import tsteelworks.inventory.DeepTankContainer;

public class DeepTankLogic extends TileEntity implements IFacingLogic, IFluidTank, IMasterLogic
{
    public ArrayList<FluidStack> fluidlist = new ArrayList<FluidStack>();
    boolean structureHasBottom;
    boolean structureHasTop;
    boolean needsUpdate;
    public boolean validStructure;
    byte direction;
    public CoordTuple centerPos;
    int tick;
    int maxLiquid;
    int currentLiquid;
    int numBricks;
    int innerMaxX;
    int innerMaxZ;
    public int layers;
    Random rand = new Random();

    public DeepTankLogic()
    {
        super();
    }
    
    public int layerFluidCapacity()
    {
        return (FluidContainerRegistry.BUCKET_VOLUME * 4) * 12;
    }
    
    void adjustLayers (int lay, boolean forceAdjust)
    {
        if (lay != layers || forceAdjust)
        {
            needsUpdate = true;
            layers = lay;
            maxLiquid = layerFluidCapacity() * lay;
        }
    }

    /* Misc */
    public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
    {
        return new DeepTankContainer(inventoryplayer, this);
    }
    
    public boolean isUseableByPlayer (EntityPlayer entityplayer)
    {
        if (worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) != this)
            return false;
        else
            return entityplayer.getDistance(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64D;
    }
    
    public String getDefaultName ()
    {
        return "tank.DeepTank";
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
    {

    }

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

    /* Updating */
    public void updateEntity ()
    {
        /*if (worldObj.isRemote)
            return;*/

        tick++;
        if (tick % 20 == 0)
        {
            if (!validStructure)
                checkValidPlacement();

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

    boolean addFluidToTank (FluidStack liquid, boolean first)
    {
        needsUpdate = true;
        if (fluidlist.size() == 0)
        {
            fluidlist.add(liquid.copy());
            currentLiquid += liquid.amount;
            return true;
        }
        else
        {
            if (liquid.amount + currentLiquid > maxLiquid)
                return false;

            currentLiquid += liquid.amount;
            boolean added = false;
            for (int i = 0; i < fluidlist.size(); i++)
            {
                FluidStack l = fluidlist.get(i);
                if (l.isFluidEqual(liquid))
                {
                    l.amount += liquid.amount;
                    added = true;
                }
                if (l.amount <= 0)
                {
                    fluidlist.remove(l);
                    i--;
                }
            }
            if (!added)
            {
                if (first)
                    fluidlist.add(0, liquid.copy());
                else
                    fluidlist.add(liquid.copy());
            }
            return true;
        }
    }

    public void onInventoryChanged ()
    {
        updateEntity();
        super.onInventoryChanged();
        needsUpdate = true;
    }

    /* Multiblock */
    @Override
    public void notifyChange (IServantLogic servant, int x, int y, int z)
    {
        checkValidPlacement();
    }

//    public void checkValidPlacement ()
//    {
//        int initX = 0;
//        int initZ = 0;
//        int centerX = 0;
//        int centerZ = 0;
//        switch (getRenderDirection())
//        {
//        case 2: // +z
//            initZ = this.measureGlassBlocks();
//            centerZ = (initZ % 2 == 0) ? 0 : (initZ / 2 + 1); 
//            if (centerZ == 0) break;
////            TSteelworks.loginfo("Divied", centerZ);
//            alignControllerLayer(xCoord, yCoord, zCoord + 2);
//            break;
//        case 3: // -z
//            initZ = this.measureGlassBlocks();
//            centerZ = (initZ % 2 == 0) ? 0 : (initZ / 2 + 1); 
//            if (centerZ == 0) break;
//            alignControllerLayer(xCoord, yCoord, zCoord - 2);
//            break;
//        case 4: // +x
//            initX = this.measureGlassBlocks();
//            centerX = (initX % 2 == 0) ? 0 : (initX / 2 + 1); 
//            if (centerX == 0) break;
//            alignControllerLayer(xCoord + 2, yCoord, zCoord);
//            break;
//        case 5: // -x
//            initX = this.measureGlassBlocks();
//            centerX = (initX % 2 == 0) ? 0 : (initX / 2 + 1); 
//            if (centerX == 0) break;
//            alignControllerLayer(xCoord - 2, yCoord, zCoord);
//            break;
//        }
//    }
    public void checkValidPlacement ()
    {
        int[] center = scanGlassLayerCenter();
        alignControllerLayer(center[0], yCoord, center[1]);
    }

    private int[] scanGlassLayerCenter ()
    {
        int centerX = 0;
        int centerZ = 0;
        Block block;
        switch (getRenderDirection())
        {
        case 2: // +z
            // Scan to last block
            for (int z = zCoord + 1; z < zCoord + 10; z++)
            {
                block = Block.blocksList[worldObj.getBlockId(xCoord, yCoord, z)];
                if (block != null && validGlassID(block.blockID))
                    centerZ += checkBricks(xCoord, yCoord, z, true);
                else
                    break;
            }
            // Adjust depth scan to center
            if ((centerZ != 1) && (centerZ % 2 == 0))
                break;
            else 
            {
                innerMaxZ = centerZ;
                centerZ = (centerZ / 2 + 1); 
            }
            if (centerZ == 0) break;
            // Scan width from center
            for (int x = xCoord - 9; x < xCoord + 9; x++)
            {
                block = Block.blocksList[worldObj.getBlockId(x, yCoord, zCoord + centerZ)];
                if (block != null && validGlassID(block.blockID))
                    centerX += checkBricks(x, yCoord, zCoord + centerZ, true);
            }
            // Adjust width to center
            if ((centerX != 1) && (centerX % 2 == 0))
                break;
            else 
            {
                innerMaxX = centerX;
                centerX = (centerX / 2 + 1); 
            }
            if (centerX == 0) break;
            return new int[] {xCoord, zCoord + centerZ};
        case 3: // -z
            for (int z = zCoord - 1; z > zCoord - 10; z--)
            {
                block = Block.blocksList[worldObj.getBlockId(xCoord, yCoord, z)];
                if (block != null && validGlassID(block.blockID))
                {
                    centerZ += checkBricks(xCoord, yCoord, z, true);
                }
                else
                    break;
            }
            // Adjust depth scan to center
            if ((centerZ != 1) && (centerZ % 2 == 0))
                break;
            else 
            {
                innerMaxZ = centerZ;
                centerZ = (centerZ / 2 + 1); 
            }
            if (centerZ == 0) break;
            // Scan width from center
            for (int x = xCoord - 9; x < xCoord + 9; x++)
            {
                block = Block.blocksList[worldObj.getBlockId(x, yCoord, zCoord - centerZ)];
                if (block != null && validGlassID(block.blockID))
                    centerX += checkBricks(x, yCoord, zCoord - centerZ, true);
            }
            // Adjust width to center
            if ((centerX != 1) && (centerX % 2 == 0))
                break;
            else 
            {
                innerMaxX = centerX;
                centerX = (centerX / 2 + 1); 
            }
            if (centerX == 0) break;
            return new int[] {xCoord, zCoord - centerZ};
        case 4: // +x
            for (int x = xCoord + 1; x < xCoord + 10; x++)
            {
                block = Block.blocksList[worldObj.getBlockId(x, yCoord, zCoord)];
                if (block != null && validGlassID(block.blockID))
                    centerX += checkBricks(x, yCoord, zCoord, true);
                else
                    break;
            }
            // Adjust depth scan to center
            if ((centerX != 1) && (centerX % 2 == 0))
                break;
            else 
            {
                innerMaxX = centerX;
                centerX = (centerX / 2 + 1); 
            }
            if (centerX == 0) break;
            // Scan length from center
            for (int z = zCoord - 9; z < zCoord + 9; z++)
            {
                block = Block.blocksList[worldObj.getBlockId(xCoord + centerX, yCoord, zCoord)];
                if (block != null && validGlassID(block.blockID))
                    centerZ += checkBricks(xCoord + centerX, yCoord, z, true);
                else
                    break;
            }
            // Adjust length to center
            if ((centerZ != 1) && (centerZ % 2 == 0))
                break;
            else 
            {
                innerMaxZ = centerZ;
                centerZ = (centerZ / 2 + 1); 
            }
            if (centerZ == 0) break;
            return new int[] {xCoord + centerX, zCoord};
        case 5: // -x
            for (int x = xCoord - 1; x > xCoord - 10; x++)
            {
                block = Block.blocksList[worldObj.getBlockId(x, yCoord, zCoord)];
                if (block != null && validGlassID(block.blockID))
                    centerX += checkBricks(x, yCoord, zCoord, true);
                else
                    break;
            }
            // Adjust depth scan to center
            if ((centerX != 1) && (centerX % 2 == 0))
                break;
            else 
            {
                innerMaxX = centerX;
                centerX = (centerX / 2 + 1); 
            }
            if (centerX == 0) break;
            // Scan length from center
            for (int z = zCoord - 9; z < zCoord + 9; z++)
            {
                block = Block.blocksList[worldObj.getBlockId(xCoord - centerX, yCoord, zCoord)];
                if (block != null && validGlassID(block.blockID))
                    centerZ += checkBricks(xCoord - centerX, yCoord, z, true);
            }
            // Adjust length to center
            if ((centerZ != 1) && (centerZ % 2 == 0))
                break;
            else 
            {
                innerMaxZ = centerZ;
                centerZ = (centerZ / 2 + 1); 
            }
            if (centerZ == 0) break;
            return new int[] {xCoord - centerX, zCoord};
        }
        return new int[] {xCoord, yCoord};
    }
    
    void alignControllerLayer (int x, int y, int z)
    {
        if (x == xCoord || z == zCoord) return;

        int innerCenterX = (innerMaxX / 2) + 1;
        int innerCenterZ = (innerMaxZ / 2) + 1;
        
        int tempBricks = 0;
        int depthcenter = 0;
        Block block;
        
//        TSteelworks.loginfo("center", center);
        
        // Scan inner for glass blocks by adjusted X coordinate
        for (int xPos = x - innerCenterX; xPos <= x + innerCenterX; xPos++)
        {
            block = Block.blocksList[worldObj.getBlockId(xPos, y, z)];
            if (block != null && validGlassID(block.blockID))
                tempBricks += checkBricks(xPos, y, z, true);
        }
        TSteelworks.loginfo("tempBricks", tempBricks);
        
        if (false) return;
        
        if  (tempBricks > 9) return;
        tempBricks = 0;
        // Scan inner for glass blocks by adjusted Y coordinate
        for (int zPos = z - innerCenterZ; zPos <= z + innerCenterZ; zPos++)
        {
            block = Block.blocksList[worldObj.getBlockId(x, y, zPos)];
            if (block != null && validGlassID(block.blockID))
                tempBricks += checkBricks(x, y, zPos, true);
        }
        if  (tempBricks > 9) return;
        tempBricks = 0;
        // Invalid if glass is even or layer is larger than 9x9
        
        // Scan outter for brick/drain blocks by adjusted X coordinate
        for (int xPos = x - (innerMaxX + 1); xPos <= x + (innerMaxX + 1); xPos++)
        {
            for (int zPos = z - (innerMaxZ); zPos <= z + (innerMaxZ); zPos++)
            {
                block = Block.blocksList[worldObj.getBlockId(xPos, y, zPos)];
                if (block != null && validBlockID(block.blockID))
                    tempBricks += checkBricks(xPos, y, zPos, false);
            }
        }
//        TSteelworks.loginfo("innerWidth", innerWidth);
//        TSteelworks.loginfo("innerLength", innerLength);
//        TSteelworks.loginfo("tempBricks", tempBricks);
        setCenterPos(x, y, z);
        
//        TSteelworks.loginfo("tempBricks", tempBricks);
//        if (tempBricks >= 6)
//        {
            checkValidStructure(centerPos.x, centerPos.y, centerPos.z, tempBricks);
//            switch (getRenderDirection())
//            {
//            case 2: // +z
//                checkValidStructure(centerPos.x, yCoord, zCoord + (innerLength + 0), tempBricks);
//                break;
//            case 3: // -z
//                checkValidStructure(centerPos.x, yCoord, zCoord - (innerLength + 0), tempBricks);
//                break;
//            case 4: // +x
//                checkValidStructure(xCoord + (innerWidth + 0), yCoord, zCoord, tempBricks);
//                break;
//            case 5: // -x
//                checkValidStructure(xCoord - (innerWidth + 0), yCoord, zCoord, tempBricks);
//                break;
//            }
//        }
//        else
//            return;
    }

    public void setCenterPos (int x, int y, int z)
    {
      switch (getRenderDirection())
      {
      case 2: // +z
          centerPos = new CoordTuple(x, y, z + innerMaxZ);
          break;
      case 3: // -z
          centerPos = new CoordTuple(x, y, z - innerMaxZ);
          break;
      case 4: // +x
          centerPos = new CoordTuple(x + innerMaxX, y, z);
          break;
      case 5: // -x
          centerPos = new CoordTuple(x - innerMaxX, y, z);
          break;
      }
        
    }
    
    public void checkValidStructure (int x, int y, int z, int compareBricks)
    {
        int checkLayers = 0;
        if (checkSameLevel(x, y, z, compareBricks))
        {
            checkLayers++;
            int checkUp = recurseStructureUp(x, y + 1, z, 0, compareBricks);
            int checkDown = recurseStructureDown(x, y - 1, z, 0, compareBricks);
            
            checkLayers += checkUp;
            checkLayers += checkDown;
            
            if (checkUp > 0 && !structureHasBottom)
                validateBottom (x, y, z, 0);
            if (checkDown > 0 && !structureHasTop)
                validateTop (x, y, z, 0);
        }

        if (structureHasTop != structureHasBottom != validStructure || checkLayers != this.layers)
        {
            if (structureHasBottom && structureHasTop)
            {
                TSteelworks.loginfo("structureHasBottom", structureHasBottom);
                adjustLayers(checkLayers, false);
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                validStructure = true;
            }
            else
            {
                validStructure = false;
            }
        }
    }

    public boolean checkSameLevel (int x, int y, int z, int compareBricks)
    {
        numBricks = 0;
        Block block;

//        for (int xPos = x - blockX; xPos <= x + blockX; xPos++)
//        {
//            for (int zPos = z - blockZ; zPos <= z + blockZ; zPos++)
//            {
//                block = Block.blocksList[worldObj.getBlockId(xPos, y, zPos)];
//                if (block != null && !block.isAirBlock(worldObj, xPos, y, zPos) && validBlockID(block.blockID))
//                    numBricks += checkBricks(xPos, y, zPos, false);
//            }
//        }
        for (int xPos = x - innerMaxX; xPos <= x + innerMaxX; xPos++)
        {
            block = Block.blocksList[worldObj.getBlockId(xPos, y, z)];
            if (block != null && validGlassID(block.blockID))
                numBricks += checkBricks(xPos, y, z, true);
        }
        // Scan inner for glass blocks by adjusted Y coordinate
        for (int zPos = z - innerMaxZ; zPos <= z + innerMaxZ; zPos++)
        {
            block = Block.blocksList[worldObj.getBlockId(x, y, zPos)];
            if (block != null && validGlassID(block.blockID))
                numBricks += checkBricks(x, y, zPos, true);
        }
//        TSteelworks.loginfo("numBricks 1", numBricks);
        // Scan outter for brick/drain blocks by adjusted X coordinate
        for (int xPos = x - (innerMaxX + 1); xPos <= x + (innerMaxX + 1); xPos++)
        {
            for (int zPos = z - (innerMaxZ + 1); zPos <= z + (innerMaxZ + 1); zPos++)
            {
                block = Block.blocksList[worldObj.getBlockId(xPos, y, zPos)];
                if (block != null && validBlockID(block.blockID))
                    numBricks += checkBricks(xPos, y, zPos, false);
            }
        }
        
//        TSteelworks.loginfo("numBricks 2", numBricks);
        
        return (numBricks == compareBricks);
    }

    public int recurseStructureUp (int x, int y, int z, int count, int compareBricks)
    {
        numBricks = 0;
        //Check inside
        for (int xPos = x - innerMaxX; xPos <= x + innerMaxX; xPos++)
        {
            for (int zPos = z - innerMaxZ; zPos <= z + innerMaxZ; zPos++)
            {
                final int blockID = worldObj.getBlockId(xPos, y, zPos);
                final Block block = Block.blocksList[worldObj.getBlockId(xPos, y, zPos)];
                
                if ((block != null) && !block.isAirBlock(worldObj, xPos, y, zPos)) 
                {
                    
                    if (validBlockID(blockID))
                        return validateTop(x, y, z, count);
                    else
                        return count; 
                }
            }
        }

        //Check outer layer
        for (int xPos = x - innerMaxX; xPos <= x + innerMaxX; xPos++)
        {
            numBricks += checkBricks(xPos, y, z - innerMaxZ, true);
            numBricks += checkBricks(xPos, y, z + innerMaxZ, true);
        }
        for (int zPos = z - innerMaxZ; zPos <= z + innerMaxZ; zPos++)
        {
            numBricks += checkBricks(x - innerMaxX, y, zPos, true);
            numBricks += checkBricks(x + innerMaxX, y, zPos, true);
        }

//        TSteelworks.loginfo("numBricks", numBricks);
        
        if (numBricks != compareBricks)
            return count;

        count++;
        return recurseStructureUp(x, y + 1, z, count, compareBricks);
    }

    public int recurseStructureDown (int x, int y, int z, int count, int compareBricks)
    {
        numBricks = 0;
        //Check inside
        for (int xPos = x - 1; xPos <= x + 1; xPos++)
        {
            for (int zPos = z - 1; zPos <= z + 1; zPos++)
            {
                int blockID = worldObj.getBlockId(xPos, y, zPos);
                Block block = Block.blocksList[blockID];
                if (block != null && !block.isAirBlock(worldObj, xPos, y, zPos))
                {
                    if (validGlassID(blockID))
                        return validateBottom(x, y, z, count);
                    else
                        return count;
                }
            }
        }

        //Check outer layer
        for (int xPos = x - 2; xPos <= x + 2; xPos++)
        {
            numBricks += checkBricks(xPos, y, z - 2, true);
            numBricks += checkBricks(xPos, y, z + 2, true);
        }

        for (int zPos = z - 1; zPos <= z + 1; zPos++)
        {
            numBricks += checkBricks(x - 2, y, zPos, true);
            numBricks += checkBricks(x + 2, y, zPos, true);
        }

        if (numBricks != compareBricks)
            return count;

        count++;
        return recurseStructureDown(x, y - 1, z, count, compareBricks);
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
            for (int zPos = z - 1; zPos <= (z + 1); zPos++)
                if (validBlockID(worldObj.getBlockId(xPos, y, zPos)))
                    topBricks += checkBricks(xPos, y, zPos, false);
        
        //Check outer layer
        for (int xPos = x - 2; xPos <= x + 2; xPos++)
        {
            topBricks += checkBricks(xPos, y, z - 2, false);
            topBricks += checkBricks(xPos, y, z + 2, false);
        }
        
        for (int zPos = z - 1; zPos <= z + 1; zPos++)
        {
            topBricks += checkBricks(x - 2, y, zPos, false);
            topBricks += checkBricks(x + 2, y, zPos, false);
        }
        
//        TSteelworks.loginfo("Top", topBricks);
        structureHasTop = (topBricks == 25);
        return count;
    }
    
    public int validateBottom (int x, int y, int z, int count)
    {
        int bottomBricks = 0;
        for (int xPos = x - 1; xPos <= x + 1; xPos++)
        {
            for (int zPos = z - 1; zPos <= z + 1; zPos++)
            {
                if (validBlockID(worldObj.getBlockId(xPos, y, zPos)))
                    bottomBricks += checkBricks(xPos, y, zPos, false);
            }
        }
        //Check outer layer
        for (int xPos = x - 2; xPos <= x + 2; xPos++)
        {
            bottomBricks += checkBricks(xPos, y, z - 2, false);
            bottomBricks += checkBricks(xPos, y, z + 2, false);
        }

        for (int zPos = z - 1; zPos <= z + 1; zPos++)
        {
            bottomBricks += checkBricks(x - 2, y, zPos, false);
            bottomBricks += checkBricks(x + 2, y, zPos, false);
        }
        
        structureHasBottom = (bottomBricks == 25);
        if (structureHasBottom)
            centerPos = new CoordTuple(x, y + 1, z);
        return count;
    }

    /* Returns whether the brick is a lava tank or not.
     * Increments bricks, sets them as part of the structure, and adds tanks to the list.
     */
    int checkBricks (int x, int y, int z, boolean wall)
    {
        int tempBricks = 0;
        int blockID = worldObj.getBlockId(x, y, z);
        if (wall && (validGlassID(blockID) || validTankID(blockID)))
        {
            tempBricks++;
        }
        if (!wall && validBlockID(blockID))
        {
            TileEntity te = worldObj.getBlockTileEntity(x, y, z);
            if (te == this)
                tempBricks++;
            else if (te instanceof TSMultiServantLogic)
            {
                TSMultiServantLogic servant = (TSMultiServantLogic) te;
                if (servant.hasValidMaster())
                {
                    if (servant.verifyMaster(this, this.xCoord, this.yCoord, this.zCoord))
                        tempBricks++;
                }
                else if (servant.setMaster(this.xCoord, this.yCoord, this.zCoord))
                {
                    tempBricks++;
                }
            }
            else if (te instanceof MultiServantLogic)
            {
                if (!(te instanceof SmelteryDrainLogic))
                {
                    MultiServantLogic servant = (MultiServantLogic) te;
                    if (servant.hasValidMaster())
                    {
                        if (servant.verifyMaster(this, this.xCoord, this.yCoord, this.zCoord))
                            tempBricks++;
                    }
                    else if (servant.setMaster(this.xCoord, this.yCoord, this.zCoord))
                    {
                        tempBricks++;
                    }
                }
            }
        }
        return tempBricks;
    }
    
    boolean validBlockID(int blockID)
    {
        return (blockID == TSContent.highoven.blockID || blockID == TConstruct.content.smeltery.blockID);
    }
    
    boolean validTankID(int blockID)
    {
        return (blockID == TConstruct.content.lavaTank.blockID);
    }
    
    boolean validGlassID(int blockID)
    {
        return (blockID == Block.glass.blockID || blockID == TContent.stainedGlassClear.blockID || blockID == TContent.clearGlass.blockID);
    }

    public int getCapacity ()
    {
        return maxLiquid;
    }

    public int getTotalLiquid ()
    {
        return currentLiquid;
    }

    @Override
    public FluidStack drain (int maxDrain, boolean doDrain)
    {
        if (fluidlist.size() == 0)
            return null;

        FluidStack liquid = fluidlist.get(0);
        if (liquid != null)
        {
            if (liquid.amount - maxDrain <= 0)
            {
                FluidStack liq = liquid.copy();
                if (doDrain)
                {
                    //liquid = null;
                    fluidlist.remove(liquid);
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
        if (resource != null && currentLiquid < maxLiquid)//resource.amount + currentLiquid < maxLiquid)
        {
            if (resource.amount + currentLiquid > maxLiquid)
                resource.amount = maxLiquid - currentLiquid;
            int amount = resource.amount;

            if (amount > 0 && doFill)
            {
                addFluidToTank(resource, false);
//                if (addMoltenMetal(resource, false))
//                {
//                    ArrayList alloys = Smeltery.mixMetals(moltenMetal);
//                    for (int al = 0; al < alloys.size(); al++)
//                    {
//                        FluidStack liquid = (FluidStack) alloys.get(al);
//                        addMoltenMetal(liquid, true);
//                    }
//                }
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
        if (fluidlist.size() == 0)
            return null;
        return fluidlist.get(0);
    }
    public List<FluidStack> getAllFluids ()
    {
        return fluidlist;
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
        FluidTankInfo[] info = new FluidTankInfo[fluidlist.size() + 1];
        for (int i = 0; i < fluidlist.size(); i++)
        {
            FluidStack fluid = fluidlist.get(i);
            info[i] = new FluidTankInfo(fluid.copy(), fluid.amount);
        }
        info[fluidlist.size()] = new FluidTankInfo(null, maxLiquid - currentLiquid);
        return info;
    }

    /* NBT */

    @Override
    public void readFromNBT (NBTTagCompound tags)
    {
        layers = tags.getInteger("Layers");
        super.readFromNBT(tags);
        validStructure = tags.getBoolean("ValidStructure");
        int[] center = tags.getIntArray("CenterPos");
        if (center.length > 2)
            centerPos = new CoordTuple(center[0], center[1], center[2]);
        else
            centerPos = new CoordTuple(xCoord, yCoord, zCoord);
        direction = tags.getByte("Direction");
        currentLiquid = tags.getInteger("CurrentLiquid");
        maxLiquid = tags.getInteger("MaxLiquid");
        NBTTagList liquidTag = tags.getTagList("Liquids");
        fluidlist.clear();

        for (int iter = 0; iter < liquidTag.tagCount(); iter++)
        {
            NBTTagCompound nbt = (NBTTagCompound) liquidTag.tagAt(iter);
            FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
            if (fluid != null)
                fluidlist.add(fluid);
        }
    }

    @Override
    public void writeToNBT (NBTTagCompound tags)
    {
        super.writeToNBT(tags);
        tags.setBoolean("ValidStructure", validStructure);
        int[] center = new int[3];// { centerPos.x, centerPos.y, centerPos.z };
        if (centerPos == null)
            center = new int[] { xCoord, yCoord, zCoord };
        else
            center = new int[] { centerPos.x, centerPos.y, centerPos.z };
        tags.setIntArray("CenterPos", center);
        tags.setByte("Direction", direction);
        tags.setInteger("CurrentLiquid", currentLiquid);
        tags.setInteger("MaxLiquid", maxLiquid);
        tags.setInteger("Layers", layers);

        NBTTagList taglist = new NBTTagList();
        for (FluidStack liquid : fluidlist)
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
}
