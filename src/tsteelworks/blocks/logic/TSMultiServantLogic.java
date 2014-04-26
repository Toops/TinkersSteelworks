package tsteelworks.blocks.logic;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tconstruct.library.util.CoordTuple;
import tconstruct.library.util.IMasterLogic;
import tconstruct.library.util.IServantLogic;

public class TSMultiServantLogic extends TileEntity implements IServantLogic
{
    boolean hasMaster;
    CoordTuple master;
    short masterID;
    byte masterMeta;

    /*
     * (non-Javadoc)
     * @see net.minecraft.tileentity.TileEntity#canUpdate()
     */
    @Override
    public boolean canUpdate ()
    {
        return false;
    }
    
    public boolean hasValidMaster ()
    {
        if (!hasMaster)
            return false;

        if ((worldObj.getBlockId(master.x, master.y, master.z) == masterID) && (worldObj.getBlockMetadata(master.x, master.y, master.z) == masterMeta))
            return true;

        else
        {
            hasMaster = false;
            master = null;
            return false;
        }
    }
    
    /*
     * (non-Javadoc)
     * @see tconstruct.library.util.IServantLogic#getMasterPosition()
     */
    @Override
    public CoordTuple getMasterPosition ()
    {
        return master;
    }

    public void overrideMaster (int x, int y, int z)
    {
        hasMaster = true;
        master = new CoordTuple(x, y, z);
        masterID = (short) worldObj.getBlockId(x, y, z);
        masterMeta = (byte) worldObj.getBlockMetadata(x, y, z);
    }
    
    public void removeMaster ()
    {
        hasMaster = false;
        master = null;
        masterID = 0;
        masterMeta = 0;
    }
    
    /*
     * (non-Javadoc)
     * @see tconstruct.library.util.IServantLogic#notifyMasterOfChange()
     */
    @Override
    public void notifyMasterOfChange ()
    {
        if (hasValidMaster())
        {
            final IMasterLogic logic = (IMasterLogic) worldObj.getBlockTileEntity(master.x, master.y, master.z);
            logic.notifyChange(this, xCoord, yCoord, zCoord);
        }
    }
    
    // I assume it's replace by the other verifyMaster
    /**
     * @see {@link TSMultiServantLogic#verifyMaster(IMasterLogic, World, int, int, int)}
     */
    @Deprecated
    public boolean verifyMaster (IMasterLogic logic, int x, int y, int z)
    {
        //return (master.equalCoords(x, y, z) && (worldObj.getBlockId(x, y, z) == masterID) && (worldObj.getBlockMetadata(x, y, z) == masterMeta));
    	return verifyMaster(logic, null, x, y, z);
    }   

    // not needed anymore? replaced by something else somewhere else?
    @Deprecated
    public boolean setMaster (int x, int y, int z)
    {
        if (!hasMaster || (worldObj.getBlockId(master.x, master.y, master.z) != masterID) || (worldObj.getBlockMetadata(master.x, master.y, master.z) != masterMeta))
        {
            overrideMaster(x, y, z);
            return true;
        }
        else
            return false;
    }

    /*
     * (non-Javadoc)
     * @see tconstruct.library.util.IServantLogic#setPotentialMaster(tconstruct.library.util.IMasterLogic, net.minecraft.world.World, int, int, int)
     */
    @Override
    public boolean setPotentialMaster (IMasterLogic master, World world, int x, int y, int z)
    {
        return !hasMaster;
    }

    /*
     * (non-Javadoc)
     * @see tconstruct.library.util.IServantLogic#verifyMaster(tconstruct.library.util.IMasterLogic, net.minecraft.world.World, int, int, int)
     */
    @Override
    public boolean verifyMaster (IMasterLogic logic, World world, int x, int y, int z)
    {
        if (hasMaster)
            return hasValidMaster();
        else
        {
            overrideMaster(x, y, z);
            return true;
        }
    }
    
    /*
     * (non-Javadoc)
     * @see tconstruct.library.util.IServantLogic#invalidateMaster(tconstruct.library.util.IMasterLogic, net.minecraft.world.World, int, int, int)
     */
    @Override
    public void invalidateMaster (IMasterLogic master, World world, int x, int y, int z)
    {
        hasMaster = false;
        master = null;
    }

    /*
     * (non-Javadoc)
     * @see net.minecraft.tileentity.TileEntity#readFromNBT(net.minecraft.nbt.NBTTagCompound)
     */
    @Override
    public void readFromNBT (NBTTagCompound tags)
    {
        super.readFromNBT(tags);
        readCustomNBT(tags);
    }
    
    public void readCustomNBT (NBTTagCompound tags)
    {
        hasMaster = tags.getBoolean("TiedToMaster");
        if (hasMaster)
        {
            final int xCenter = tags.getInteger("xCenter");
            final int yCenter = tags.getInteger("yCenter");
            final int zCenter = tags.getInteger("zCenter");
            master = new CoordTuple(xCenter, yCenter, zCenter);
            masterID = tags.getShort("MasterID");
            masterMeta = tags.getByte("masterMeta");
        }
    }
    
    /*
     * (non-Javadoc)
     * @see net.minecraft.tileentity.TileEntity#writeToNBT(net.minecraft.nbt.NBTTagCompound)
     */
    @Override
    public void writeToNBT (NBTTagCompound tags)
    {
        super.writeToNBT(tags);
        writeCustomNBT(tags);
    }
    
    public void writeCustomNBT (NBTTagCompound tags)
    {
        tags.setBoolean("TiedToMaster", hasMaster);
        if (hasMaster)
        {
            tags.setInteger("xCenter", master.x);
            tags.setInteger("yCenter", master.y);
            tags.setInteger("zCenter", master.z);
            tags.setShort("MasterID", masterID);
            tags.setByte("masterMeta", masterMeta);
        }
    }

    
    
    /* Packets */
    
    /*
     * (non-Javadoc)
     * @see net.minecraft.tileentity.TileEntity#onDataPacket(net.minecraft.network.INetworkManager, net.minecraft.network.packet.Packet132TileEntityData)
     */
    @Override
    public void onDataPacket (INetworkManager net, Packet132TileEntityData packet)
    {
        readCustomNBT(packet.data);
        worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
    }
    
    /*
     * (non-Javadoc)
     * @see net.minecraft.tileentity.TileEntity#getDescriptionPacket()
     */
    @Override
    public Packet getDescriptionPacket ()
    {
        final NBTTagCompound tag = new NBTTagCompound();
        writeCustomNBT(tag);
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag);
    }
}