package tsteelworks.blocks.logic;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import tconstruct.library.util.IFacingLogic;

public class HighOvenDrainLogic extends TSMultiServantLogic implements IFluidHandler, IFacingLogic
{
    byte direction;
    
    // ========== TileEntity ===========
    
    /*
     * (non-Javadoc)
     * @see tsteelworks.blocks.logic.TSMultiServantLogic#canUpdate()
     */
    @Override
    public boolean canUpdate () { return false; }
    
    // ========== HighOvenDrainLogic ===========
    
    public int getControllerLogicType ()
    {
        final int mx = getMasterPosition().x;
        final int my = getMasterPosition().y;
        final int mz = getMasterPosition().z;
        if (worldObj.getBlockTileEntity(mx, my, mz) instanceof HighOvenLogic){
        	return 1;
        }
        if (worldObj.getBlockTileEntity(mx, my, mz) instanceof DeepTankLogic){
        	return 2;
        }
        return 0;
    }
    
    public HighOvenLogic getHighOvenController()
    {
        final int mx = getMasterPosition().x;
        final int my = getMasterPosition().y;
        final int mz = getMasterPosition().z;
        return (HighOvenLogic) worldObj.getBlockTileEntity(mx, my, mz);
    }
    
    public DeepTankLogic getDeepTankController()
    {
        final int mx = getMasterPosition().x;
        final int my = getMasterPosition().y;
        final int mz = getMasterPosition().z;
        return (DeepTankLogic) worldObj.getBlockTileEntity(mx, my, mz);
    }

    // ========== IFacingLogic ===========
    
    /*
     * (non-Javadoc)
     * @see tconstruct.library.util.IFacingLogic#getRenderDirection()
     */
    @Override
    public byte getRenderDirection ()
    {
        return direction;
    }
    
    /*
     * (non-Javadoc)
     * @see tconstruct.library.util.IFacingLogic#getForgeDirection()
     */
    @Override
    public ForgeDirection getForgeDirection ()
    {
        return ForgeDirection.VALID_DIRECTIONS[direction];
    }

    /*
     * (non-Javadoc)
     * @see tconstruct.library.util.IFacingLogic#setDirection(int)
     */
    @Override
    public void setDirection (int side)
    {
    }

    /*
     * (non-Javadoc)
     * @see tconstruct.library.util.IFacingLogic#setDirection(float, float, net.minecraft.entity.EntityLivingBase)
     */
    @Override
    public void setDirection (float yaw, float pitch, EntityLivingBase player)
    {
        if (pitch > 45)
            direction = 1;
        else if (pitch < -45)
            direction = 0;
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

    // ========== IFluidHandler ===========
    
    /*
     * (non-Javadoc)
     * @see net.minecraftforge.fluids.IFluidHandler#canDrain(net.minecraftforge.common.ForgeDirection, net.minecraftforge.fluids.Fluid)
     */
    @Override
    public boolean canDrain (ForgeDirection from, Fluid fluid)
    {
        // Check that the drain is coming from the from the front of the block
        // and that the fluid to be drained is in the master.
        boolean containsFluid = fluid == null;
        if (fluid != null)
        {
            int type = getControllerLogicType();
            if (type == 1)
            {
                for (final FluidStack fstack : getHighOvenController().moltenMetal)
                    if (fstack.fluidID == fluid.getID())
                    {
                        containsFluid = true;
                        break;
                    }
            }
            if (type == 2)
            {
                for (final FluidStack fstack : getDeepTankController().getFluidlist())
                    if (fstack.fluidID == fluid.getID())
                    {
                        containsFluid = true;
                        break;
                    }
            }
        }
        return containsFluid;
    }

    /*
     * (non-Javadoc)
     * @see net.minecraftforge.fluids.IFluidHandler#drain(net.minecraftforge.common.ForgeDirection, net.minecraftforge.fluids.FluidStack, boolean)
     */
    @Override
    public FluidStack drain (ForgeDirection from, FluidStack resource, boolean doDrain)
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see net.minecraftforge.fluids.IFluidHandler#drain(net.minecraftforge.common.ForgeDirection, int, boolean)
     */
    @Override
    public FluidStack drain (ForgeDirection from, int maxDrain, boolean doDrain)
    {
        if (hasValidMaster() && canDrain(from, null))
        {
            int type = getControllerLogicType();
            if (type == 1) return getHighOvenController().drain(maxDrain, doDrain);
            if (type == 2) return getDeepTankController().drain(maxDrain, doDrain);
            return null;

        }
        else
        {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * @see net.minecraftforge.fluids.IFluidHandler#canFill(net.minecraftforge.common.ForgeDirection, net.minecraftforge.fluids.Fluid)
     */
    @Override
    public boolean canFill (ForgeDirection from, Fluid fluid)
    {
        return true;
    }
    
    /*
     * (non-Javadoc)
     * @see net.minecraftforge.fluids.IFluidHandler#fill(net.minecraftforge.common.ForgeDirection, net.minecraftforge.fluids.FluidStack, boolean)
     */
    @Override
    public int fill (ForgeDirection from, FluidStack resource, boolean doFill)
    {
        if (hasValidMaster() && (resource != null) && canFill(from, resource.getFluid()))
        {
            if (doFill)
            {
                int type = getControllerLogicType();
                if (type == 1) return getHighOvenController().fill(resource, doFill);
                if (type == 2) return getDeepTankController().fill(resource, doFill);
                return 0;
            }
            else
                return resource.amount;
        }
        else
            return 0;
    }
    
    /*
     * (non-Javadoc)
     * @see net.minecraftforge.fluids.IFluidHandler#getTankInfo(net.minecraftforge.common.ForgeDirection)
     */
    @Override
    public FluidTankInfo[] getTankInfo (ForgeDirection from)
    {
        if (hasValidMaster() && ((from == getForgeDirection()) || (from == getForgeDirection().getOpposite()) || (from == ForgeDirection.UNKNOWN)))
        {
            int type = getControllerLogicType();
            if (type == 1) return new FluidTankInfo[] { getHighOvenController().getInfo() };
            if (type == 2) return new FluidTankInfo[] { getDeepTankController().getInfo() };
            return null;
        }
        return null;
    }
    
    // ========== NBT ===========
    
    /*
     * (non-Javadoc)
     * @see tsteelworks.blocks.logic.TSMultiServantLogic#readFromNBT(net.minecraft.nbt.NBTTagCompound)
     */
    @Override
    public void readFromNBT (NBTTagCompound tags)
    {
        super.readFromNBT(tags);
        direction = tags.getByte("Direction");
    }
    
    /*
     * (non-Javadoc)
     * @see tsteelworks.blocks.logic.TSMultiServantLogic#writeToNBT(net.minecraft.nbt.NBTTagCompound)
     */
    @Override
    public void writeToNBT (NBTTagCompound tags)
    {
        super.writeToNBT(tags);
        tags.setByte("Direction", direction);
    }
    
    // ========== Packet Handling ===========
    
    /*
     * (non-Javadoc)
     * @see tsteelworks.blocks.logic.TSMultiServantLogic#onDataPacket(net.minecraft.network.INetworkManager, net.minecraft.network.packet.Packet132TileEntityData)
     */
    @Override
    public void onDataPacket (INetworkManager net, Packet132TileEntityData packet)
    {
        readFromNBT(packet.data);
        worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
    }
    
    /*
     * (non-Javadoc)
     * @see tsteelworks.blocks.logic.TSMultiServantLogic#getDescriptionPacket()
     */
    @Override
    public Packet getDescriptionPacket ()
    {
        final NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag);
    }
}