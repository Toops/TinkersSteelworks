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
import tconstruct.blocks.logic.MultiServantLogic;
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

    public void checkValidPlacement ()
    {
        switch (getRenderDirection())
        {
        case 2: // +z
            alignInitialPlacement(xCoord, yCoord, zCoord + 2);
            break;
        case 3: // -z
            alignInitialPlacement(xCoord, yCoord, zCoord - 2);
            break;
        case 4: // +x
            alignInitialPlacement(xCoord + 2, yCoord, zCoord);
            break;
        case 5: // -x
            alignInitialPlacement(xCoord - 2, yCoord, zCoord);
            break;
        }
    }

    public void alignInitialPlacement (int x, int y, int z)
    {
        int northID = worldObj.getBlockId(x, y, z + 1);
        int southID = worldObj.getBlockId(x, y, z - 1);
        int eastID = worldObj.getBlockId(x + 1, y, z);
        int westID = worldObj.getBlockId(x - 1, y, z);

        Block northBlock = Block.blocksList[northID];
        Block southBlock = Block.blocksList[southID];
        Block eastBlock = Block.blocksList[eastID];
        Block westBlock = Block.blocksList[westID];

        if ((northBlock == null || northBlock.isAirBlock(worldObj, x, y, z + 1)) && (southBlock == null || southBlock.isAirBlock(worldObj, x, y, z - 1))
                && (eastBlock == null || eastBlock.isAirBlock(worldObj, x + 1, y, z)) && (westBlock == null || westBlock.isAirBlock(worldObj, x - 1, y, z)))
        {
            checkValidStructure(x, y, z);
        }

        else if ((northBlock != null && !northBlock.isAirBlock(worldObj, x, y, z + 1)) && (southBlock == null || southBlock.isAirBlock(worldObj, x, y, z - 1))
                && (eastBlock == null || eastBlock.isAirBlock(worldObj, x + 1, y, z)) && (westBlock == null || westBlock.isAirBlock(worldObj, x - 1, y, z)))
        {
            checkValidStructure(x, y, z - 1);
        }

        else if ((northBlock == null || northBlock.isAirBlock(worldObj, x, y, z + 1)) && (southBlock != null && !southBlock.isAirBlock(worldObj, x, y, z - 1))
                && (eastBlock == null || eastBlock.isAirBlock(worldObj, x + 1, y, z)) && (westBlock == null || westBlock.isAirBlock(worldObj, x - 1, y, z)))
        {
            checkValidStructure(x, y, z + 1);
        }

        else if ((northBlock == null || northBlock.isAirBlock(worldObj, x, y, z + 1)) && (southBlock == null || southBlock.isAirBlock(worldObj, x, y, z - 1))
                && (eastBlock != null && !eastBlock.isAirBlock(worldObj, x + 1, y, z)) && (westBlock == null || westBlock.isAirBlock(worldObj, x - 1, y, z)))
        {
            checkValidStructure(x - 1, y, z);
        }

        else if ((northBlock == null || northBlock.isAirBlock(worldObj, x, y, z + 1)) && (southBlock == null || southBlock.isAirBlock(worldObj, x, y, z - 1))
                && (eastBlock == null || eastBlock.isAirBlock(worldObj, x + 1, y, z)) && (westBlock != null && !westBlock.isAirBlock(worldObj, x - 1, y, z)))
        {
            checkValidStructure(x + 1, y, z);
        }

        //Not valid, sorry
    }

    public void checkValidStructure (int x, int y, int z)
    {
        int checkLayers = 0;
        if (checkSameLevel(x, y, z))
        {
            checkLayers++;
            checkLayers += recurseStructureUp(x, y + 1, z, 0);
            checkLayers += recurseStructureDown(x, y - 1, z, 0);
        }

        if (structureHasTop != structureHasBottom != validStructure || checkLayers != this.layers)
        {
            if (structureHasBottom && structureHasTop)
            {
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

    public boolean checkSameLevel (int x, int y, int z)
    {
        numBricks = 0;
        Block block;

        //Check inside
        for (int xPos = x - 1; xPos <= x + 1; xPos++)
        {
            for (int zPos = z - 1; zPos <= z + 1; zPos++)
            {
                block = Block.blocksList[worldObj.getBlockId(xPos, y, zPos)];
                if (block != null && !block.isAirBlock(worldObj, xPos, y, zPos))
                    return false;
            }
        }

        //Check outer layer
        for (int xPos = x - 2; xPos <= x + 2; xPos++)
        {
            numBricks += checkBricks(xPos, y, z - 2);
            numBricks += checkBricks(xPos, y, z + 2);
        }

        for (int zPos = z - 1; zPos <= z + 1; zPos++)
        {
            numBricks += checkBricks(x - 2, y, zPos);
            numBricks += checkBricks(x + 2, y, zPos);
        }
//        TSteelworks.loginfo("checkSameLevel", numBricks);
        if (numBricks == 16)
            return true;
        else
            return false;
    }

    public int recurseStructureUp (int x, int y, int z, int count)
    {
        numBricks = 0;
        //Check inside
        for (int xPos = x - 1; xPos <= x + 1; xPos++)
        {
            for (int zPos = z - 1; zPos <= z + 1; zPos++)
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
        for (int xPos = x - 2; xPos <= x + 2; xPos++)
        {
            numBricks += checkBricks(xPos, y, z - 2);
            numBricks += checkBricks(xPos, y, z + 2);
        }

        for (int zPos = z - 1; zPos <= z + 1; zPos++)
        {
            numBricks += checkBricks(x - 2, y, zPos);
            numBricks += checkBricks(x + 2, y, zPos);
        }

        if (numBricks != 16)
            return count;

        count++;
        return recurseStructureUp(x, y + 1, z, count);
    }

    public int recurseStructureDown (int x, int y, int z, int count)
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
                    if (validBlockID(blockID))
                        return validateBottom(x, y, z, count);
                    else
                        return count;
                }
            }
        }

        //Check outer layer
        for (int xPos = x - 2; xPos <= x + 2; xPos++)
        {
            numBricks += checkBricks(xPos, y, z - 2);
            numBricks += checkBricks(xPos, y, z + 2);
        }

        for (int zPos = z - 1; zPos <= z + 1; zPos++)
        {
            numBricks += checkBricks(x - 2, y, zPos);
            numBricks += checkBricks(x + 2, y, zPos);
        }

        if (numBricks != 16)
            return count;

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
            for (int zPos = z - 1; zPos <= (z + 1); zPos++)
                if (validBlockID(worldObj.getBlockId(xPos, y, zPos)) && 
                        (worldObj.getBlockMetadata(xPos, y, zPos) >= 1))
                    topBricks += checkBricks(xPos, y, zPos);
        
        //Check outer layer
        for (int xPos = x - 2; xPos <= x + 2; xPos++)
        {
            topBricks += checkBricks(xPos, y, z - 2);
            topBricks += checkBricks(xPos, y, z + 2);
        }
        
        for (int zPos = z - 1; zPos <= z + 1; zPos++)
        {
            topBricks += checkBricks(x - 2, y, zPos);
            topBricks += checkBricks(x + 2, y, zPos);
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
                if (validBlockID(worldObj.getBlockId(xPos, y, zPos)) && 
                        (worldObj.getBlockMetadata(xPos, y, zPos) >= 1) &&
                        (worldObj.getBlockMetadata(xPos, y, zPos) <= 12))
                    bottomBricks += checkBricks(xPos, y, zPos);
            }
        }
        //Check outer layer
        for (int xPos = x - 2; xPos <= x + 2; xPos++)
        {
            bottomBricks += checkBricks(xPos, y, z - 2);
            bottomBricks += checkBricks(xPos, y, z + 2);
        }

        for (int zPos = z - 1; zPos <= z + 1; zPos++)
        {
            bottomBricks += checkBricks(x - 2, y, zPos);
            bottomBricks += checkBricks(x + 2, y, zPos);
        }
        
//        TSteelworks.loginfo("Bottom", bottomBricks);
        structureHasBottom = (bottomBricks == 25);
        if (structureHasBottom)
            centerPos = new CoordTuple(x, y + 1, z);
        return count;
    }

    /* Returns whether the brick is a lava tank or not.
     * Increments bricks, sets them as part of the structure, and adds tanks to the list.
     */
    int checkBricks (int x, int y, int z)
    {
        int tempBricks = 0;
        int blockID = worldObj.getBlockId(x, y, z);
        if (validBlockID(blockID) || validTankID(blockID))
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
        return tempBricks;
    }
    
    boolean validBlockID(int blockID)
    {
        return blockID == TSContent.highoven.blockID || blockID == TContent.smeltery.blockID || 
                blockID == TContent.smelteryNether.blockID;
    }
    
    boolean validTankID(int blockID)
    {
        return blockID == TContent.lavaTank.blockID || blockID == TContent.lavaTankNether.blockID 
                || blockID == TContent.clearGlass.blockID || blockID == TContent.stainedGlassClear.blockID;
    }
    
    boolean validCornerBlockID(int blockID)
    {
        return blockID == TSContent.highoven.blockID || blockID == TContent.smeltery.blockID || blockID == TContent.smelteryNether.blockID;
    }
    
    boolean validWallBlockID(int blockID)
    {
        return blockID == TContent.lavaTank.blockID || blockID == TContent.lavaTankNether.blockID;
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
