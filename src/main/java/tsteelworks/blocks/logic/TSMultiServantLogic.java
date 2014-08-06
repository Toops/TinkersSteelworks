package tsteelworks.blocks.logic;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tconstruct.library.util.CoordTuple;
import tsteelworks.lib.IMasterLogic;
import tsteelworks.lib.IServantLogic;

public class TSMultiServantLogic extends TileEntity implements IServantLogic
{    
    //private IMaster imaster;
    // Experimentation...
    CoordTuple master;
    boolean hasMaster;
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
    
//    public boolean hasValidMasterNEW()
//    {
//    	if (imaster == null || !hasWorldObj()) return false;
//    	CoordTuple coord = imaster.getCoord();
//    	final TileEntity te = worldObj.getBlockTileEntity(coord.x, coord.y, coord.z);
//    	if(te instanceof IMaster)
//    	{
//    		if((getWorldObj().getBlockId(coord.x, coord.y, coord.z) == imaster.getBlockId()) && (getWorldObj().getBlockMetadata(coord.x, coord.y, coord.z) == imaster.getBlockMetadata()))
//    		{
//    			return imaster.isValid();
//    		}
//    		else
//    		{
//    			imaster = null;
//    			return false;
//    		}
//    	}
//    	return false;
//    }
    
    public boolean hasValidMaster()
    {
        if (!hasMaster)
            return false;

        if ((worldObj.getBlockId(master.x, master.y, master.z) == masterID) && (worldObj.getBlockMetadata(master.x, master.y, master.z) == masterMeta))
        {
            final TileEntity te = worldObj.getBlockTileEntity(master.x, master.y, master.z);
            if(te != null && te instanceof IMasterLogic)
            {
            	IMasterLogic logic = (IMasterLogic)te;
            	return logic.isValid();
            }
            else
            {
            	return true;
            }
        }
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
//    public CoordTuple getMasterPositionNEW ()
//    {
//        return (imaster != null) ? imaster.getCoord():null;
//    }
    
    @Override
    public CoordTuple getMasterPosition ()
    {
        return master;
    }
    
    
//    public boolean overrideMasterNEW(int x, int y, int z)
//    {
//    	final TileEntity te = worldObj.getBlockTileEntity(x, y, z);
//    	if(te instanceof IMaster)
//    	{
//    		imaster = (IMaster)te;
//    		return true;
//    	}
//    	else
//    	{
//    	    imaster = null;
//    		TSteelworks.loginfo("TSMSLogic - overrideMaster - it's not a IMaster - "+te.getClass());
//    	}
//    	return false;
//    }
    
    public boolean overrideMaster (int x, int y, int z)
    {
        hasMaster = true;
        master = new CoordTuple(x, y, z);
        masterID = (short) worldObj.getBlockId(x, y, z);
        masterMeta = (byte) worldObj.getBlockMetadata(x, y, z);
        return true;
    }
    
    
//    public void removeMasterNEW ()
//    {
//        imaster = null;
//    }
    
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
//    @Override
//    public void notifyMasterOfChangeNEW ()
//    {
//        if (hasValidMaster())
//        {
//            CoordTuple masterCoord = imaster.getCoord();
//			final IMasterLogic logic = (IMasterLogic) getWorldObj().getBlockTileEntity(masterCoord .x, masterCoord.y, masterCoord.z);
//			if (logic == null) return;
//            logic.notifyChange(this, xCoord, yCoord, zCoord);
//        }
//    }
    
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
    public boolean verifyMaster (IMasterLogic logic, int x, int y, int z)
    {
        //return (master.equalCoords(x, y, z) && (worldObj.getBlockId(x, y, z) == masterID) && (worldObj.getBlockMetadata(x, y, z) == masterMeta));
    	return verifyMaster(logic, null, x, y, z);
    }   

    // not needed anymore? replaced by something else somewhere else?
//    @Deprecated
//    public boolean setMasterNEW (int x, int y, int z)
//    {
//        return !hasValidMasterNEW() ? overrideMaster(x, y, z) : false;
//    }
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
    //@Override
//    public boolean setPotentialMasterNEW (IMasterLogic master, World world, int x, int y, int z)
//    {
//        return imaster == null;
//    }

    @Override
    public boolean setPotentialMaster (IMasterLogic master, World world, int x, int y, int z)
    {
        return !hasMaster;
    }
    
    /*
     * (non-Javadoc)
     * @see tconstruct.library.util.IServantLogic#verifyMaster(tconstruct.library.util.IMasterLogic, net.minecraft.world.World, int, int, int)
     */
//    @Override
//    public boolean verifyMasterNEW (IMasterLogic logic, World world, int x, int y, int z)
//    {
//        if (imaster != null)
//            return hasValidMaster();
//        else
//            return overrideMaster(x, y, z);
//    }
    
    @Override
    public boolean verifyMaster (IMasterLogic logic, World world, int x, int y, int z)
    {
        return (hasMaster) ? hasValidMaster() : overrideMaster(x, y, z);
    }
    
    /*
     * (non-Javadoc)
     * @see tconstruct.library.util.IServantLogic#invalidateMaster(tconstruct.library.util.IMasterLogic, net.minecraft.world.World, int, int, int)
     */
//    @Override
//    public void invalidateMasterNEW (IMasterLogic master, World world, int x, int y, int z)
//    {
//        imaster = null;
//    }

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
    
    public void readCustomNBT_NEW (NBTTagCompound tags)
    {
        boolean hasMaster = tags.getBoolean("TiedToMaster");
        if (hasMaster)
        {
            final int xCenter = tags.getInteger("xCenter");
            final int yCenter = tags.getInteger("yCenter");
            final int zCenter = tags.getInteger("zCenter");

            overrideMaster(xCenter, yCenter, zCenter);
            
            /*master = new CoordTuple(xCenter, yCenter, zCenter);
            masterID = tags.getShort("MasterID");
            masterMeta = tags.getByte("masterMeta");*/
        }
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
    
//    public void writeCustomNBT_NEW (NBTTagCompound tags)
//    {
//    	boolean hasMaster = (imaster != null);
//        tags.setBoolean("TiedToMaster", hasMaster);
//        if (hasMaster)
//        {
//        	CoordTuple coord = imaster.getCoord();
//            tags.setInteger("xCenter", coord.x);
//            tags.setInteger("yCenter", coord.y);
//            tags.setInteger("zCenter", coord.z);
//            tags.setShort("MasterID", (short)imaster.getBlockId());
//            tags.setByte("masterMeta", (byte)imaster.getBlockMetadata());
//        }
//    }

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
        getWorldObj().markBlockForRenderUpdate(xCoord, yCoord, zCoord);
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
