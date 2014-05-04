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
import tsteelworks.TSteelworks;
import tsteelworks.lib.IMaster;

public class TSMultiServantLogic extends TileEntity implements IServantLogic
{    
    private IMaster imaster;

    /*
     * (non-Javadoc)
     * @see net.minecraft.tileentity.TileEntity#canUpdate()
     */
    @Override
    public boolean canUpdate ()
    {
        return false;
    }
    
    public boolean hasValidMaster()
    {
    	if(imaster == null) return false;
    	
    	CoordTuple coord = imaster.getCoord();
		if((worldObj.getBlockId(coord.x, coord.y, coord.z) == imaster.getBlockId()) && (worldObj.getBlockMetadata(coord.x, coord.y, coord.z) == imaster.getBlockMetadata()))
		{
			return imaster.isValid();
		}
		else
		{
			imaster = null;
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
        return (imaster != null)?imaster.getCoord():null;
    }
    
    public boolean overrideMaster(int x, int y, int z)
    {
    	final TileEntity te = worldObj.getBlockTileEntity(x, y, z);
    	if(te instanceof IMaster)
    	{
    		imaster = (IMaster)te;
    		return true;
    	}
    	else
    	{
    		// it's not normal
    		TSteelworks.loginfo("TSMSLogic - overrideMaster - it's not a IMaster - "+te.getClass());
    	}
    	return false;
    }
    
    public void removeMaster ()
    {
        imaster = null;
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
            CoordTuple masterCoord = imaster.getCoord();
			final IMasterLogic logic = (IMasterLogic) worldObj.getBlockTileEntity(masterCoord .x, masterCoord.y, masterCoord.z);
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
        if (!hasValidMaster())
            return overrideMaster(x, y, z);
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
        return imaster == null;
    }

    /*
     * (non-Javadoc)
     * @see tconstruct.library.util.IServantLogic#verifyMaster(tconstruct.library.util.IMasterLogic, net.minecraft.world.World, int, int, int)
     */
    @Override
    public boolean verifyMaster (IMasterLogic logic, World world, int x, int y, int z)
    {
        if (imaster != null)
            return hasValidMaster();
        else
            return overrideMaster(x, y, z);
    }
    
    /*
     * (non-Javadoc)
     * @see tconstruct.library.util.IServantLogic#invalidateMaster(tconstruct.library.util.IMasterLogic, net.minecraft.world.World, int, int, int)
     */
    @Override
    public void invalidateMaster (IMasterLogic master, World world, int x, int y, int z)
    {
        imaster = null;
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
    	boolean hasMaster = (imaster != null);
        tags.setBoolean("TiedToMaster", hasMaster);
        if (hasMaster)
        {
        	CoordTuple coord = imaster.getCoord();
            tags.setInteger("xCenter", coord.x);
            tags.setInteger("yCenter", coord.y);
            tags.setInteger("zCenter", coord.z);
            tags.setShort("MasterID", (short)imaster.getBlockId());
            tags.setByte("masterMeta", (byte)imaster.getBlockMetadata());
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