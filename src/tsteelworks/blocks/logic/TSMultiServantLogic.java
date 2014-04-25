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
    
    @Override
    public void notifyMasterOfChange ()
    {
        if (hasValidMaster())
        {
            final IMasterLogic logic = (IMasterLogic) worldObj.getBlockTileEntity(master.x, master.y, master.z);
            logic.notifyChange(this, xCoord, yCoord, zCoord);
        }
    }
    
    @Deprecated
    public boolean verifyMaster (IMasterLogic logic, int x, int y, int z)
    {
        return (master.equalCoords(x, y, z) && (worldObj.getBlockId(x, y, z) == masterID) && (worldObj.getBlockMetadata(x, y, z) == masterMeta));
    }   

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

    @Override
    public boolean setPotentialMaster (IMasterLogic master, World world, int x, int y, int z)
    {
        return !hasMaster;
    }

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
    
    @Override
    public void invalidateMaster (IMasterLogic master, World world, int x, int y, int z)
    {
        hasMaster = false;
        master = null;
    }

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
    
    @Override
    public void onDataPacket (INetworkManager net, Packet132TileEntityData packet)
    {
        readCustomNBT(packet.data);
        worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
    }
    
    @Override
    public Packet getDescriptionPacket ()
    {
        final NBTTagCompound tag = new NBTTagCompound();
        writeCustomNBT(tag);
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag);
    }
}
