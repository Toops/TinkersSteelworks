package tsteelworks.blocks.logic;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
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
    
    @Override
    public void updateEntity ()
    {
    }
    
    @Override
    public boolean canUpdate ()
    {
        return false;
    }
    
    @Override
    public void onInventoryChanged ()
    {
        updateEntity();
        super.onInventoryChanged();
    }
    
    public int getControllerLogicType ()
    {
        final int mx = getMasterPosition().x;
        final int my = getMasterPosition().y;
        final int mz = getMasterPosition().z;
        if (worldObj.getBlockTileEntity(mx,my, mz) instanceof HighOvenLogic)
            return 1;
        else if (worldObj.getBlockTileEntity(mx,my, mz) instanceof DeepTankLogic)
            return 2;
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

    @Override
    public ForgeDirection getForgeDirection ()
    {
        return ForgeDirection.VALID_DIRECTIONS[direction];
    }

    @Override
    public byte getRenderDirection ()
    {
        return direction;
    }

    @Override
    public void setDirection (int side)
    {
    }

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

    @Override
    public boolean canDrain (ForgeDirection from, Fluid fluid)
    {
        // Check that the drain is coming from the from the front of the block
        // and that the fluid to be drained is in the master.
        boolean containsFluid = fluid == null;
        if (fluid != null)
        {
            if (getControllerLogicType() == 1)
            {
                final HighOvenLogic logic = getHighOvenController();
                for (final FluidStack fstack : logic.moltenMetal)
                    if (fstack.fluidID == fluid.getID())
                    {
                        containsFluid = true;
                        break;
                    }
            }
            else
            {
                final DeepTankLogic logic = getDeepTankController();
                for (final FluidStack fstack : logic.fluidlist)
                    if (fstack.fluidID == fluid.getID())
                    {
                        containsFluid = true;
                        break;
                    }
            }
            

        }
        return containsFluid;
    }

    @Override
    public FluidStack drain (ForgeDirection from, FluidStack resource, boolean doDrain)
    {
        return null;
    }

    @Override
    public FluidStack drain (ForgeDirection from, int maxDrain, boolean doDrain)
    {
        if (hasValidMaster() && canDrain(from, null))
        {
            if (getControllerLogicType() == 1)
            {
                final HighOvenLogic logic = getHighOvenController();
                return logic.drain(maxDrain, doDrain);
            }
            else
            {
                final DeepTankLogic logic = getDeepTankController();
                return logic.drain(maxDrain, doDrain);
            }
        }
        else
            return null;
    }

    @Override
    public boolean canFill (ForgeDirection from, Fluid fluid)
    {
        return true;
    }
    
    @Override
    public int fill (ForgeDirection from, FluidStack resource, boolean doFill)
    {
        if (hasValidMaster() && (resource != null) && canFill(from, resource.getFluid()))
        {
            if (doFill)
            {
                if (getControllerLogicType() == 1)
                {
                    final HighOvenLogic logic = getHighOvenController();
                    return logic.fill(resource, doFill);
                }
                else
                {
                    final DeepTankLogic logic = getDeepTankController();
                    return logic.fill(resource, doFill);
                }
            }
            else
                return resource.amount;
        }
        else
            return 0;
    }
    
    @Override
    public FluidTankInfo[] getTankInfo (ForgeDirection from)
    {
        if (hasValidMaster() && ((from == getForgeDirection()) || (from == getForgeDirection().getOpposite()) || (from == ForgeDirection.UNKNOWN)))
        {
            if (getControllerLogicType() == 1)
            {
                final HighOvenLogic logic = getHighOvenController();
                return new FluidTankInfo[] { logic.getInfo() };
            }
            else
            {
                final DeepTankLogic logic = getDeepTankController();
                return new FluidTankInfo[] { logic.getInfo() };
            }
        }
        return null;
    }
    
    @Override
    public void readFromNBT (NBTTagCompound tags)
    {
        super.readFromNBT(tags);
        direction = tags.getByte("Direction");
    }
    
    @Override
    public void writeToNBT (NBTTagCompound tags)
    {
        super.writeToNBT(tags);
        tags.setByte("Direction", direction);
    }
    
    /* Packets */
    @Override
    public void onDataPacket (INetworkManager net, Packet132TileEntityData packet)
    {
        readFromNBT(packet.data);
        worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
    }
    
    @Override
    public Packet getDescriptionPacket ()
    {
        final NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag);
    }
}
