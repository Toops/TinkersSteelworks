package tsteelworks.blocks.logic;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import tconstruct.library.util.CoordTuple;
import tconstruct.library.util.IFacingLogic;
import tsteelworks.lib.blocks.TSInventoryLogic;

public class TurbineLogic extends TSInventoryLogic implements IFacingLogic, IFluidHandler
{
    byte direction;
    public FluidTank tank;
    
    public TurbineLogic()
    {
        super(0);
        tank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME);
    }

    /* Updating */
    public boolean canUpdate ()
    {
        return true;
    }

    @Override
    public void updateEntity ()
    {
    }
    
    @Override
    public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
    {
        return null;
    }

    @Override
    public String getDefaultName ()
    {
        return "machines.Turbine";
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
        if (pitch > 45)
            direction = 1;
        else if (pitch < -45)
            direction = 0;
        else
        {
            final int facing = MathHelper.floor_double((yaw / 360) + 0.5D) & 3;
            switch (facing)
            {
            case 0: direction = 2; break;
            case 1: direction = 5; break;
            case 2: direction = 3; break;
            case 3: direction = 4; break;
            }
        }
    }

    @Override
    public int fill (ForgeDirection from, FluidStack resource, boolean doFill)
    {
        int amount = tank.fill(resource, doFill);
        if (amount > 0 && doFill)
        {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }

        return amount;
    }

    @Override
    public FluidStack drain (ForgeDirection from, FluidStack resource, boolean doDrain)
    {
        return null;
    }

    @Override
    public FluidStack drain (ForgeDirection from, int maxDrain, boolean doDrain)
    {
        FluidStack amount = tank.drain(maxDrain, doDrain);
        if (amount != null && doDrain)
        {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
        return amount;
    }

    @Override
    public boolean canFill (ForgeDirection from, Fluid fluid)
    {
        return false;
    }

    @Override
    public boolean canDrain (ForgeDirection from, Fluid fluid)
    {
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo (ForgeDirection from)
    {
        FluidStack fluid = null;
        if (tank.getFluid() != null)
            fluid = tank.getFluid().copy();
        return new FluidTankInfo[] { new FluidTankInfo(fluid, tank.getCapacity()) };
    }
    
    public boolean containsFluid ()
    {
        return tank.getFluid() != null;
    }
    
    @Override
    public void readFromNBT (NBTTagCompound tags)
    {
        super.readFromNBT(tags);
        direction = tags.getByte("Direction");
        if (tags.getBoolean("hasFluid"))
            tank.setFluid(new FluidStack(tags.getInteger("itemID"), tags.getInteger("amount")));
        else
            tank.setFluid(null);
    }
    
    @Override
    public void writeToNBT (NBTTagCompound tags)
    {
        super.writeToNBT(tags);
        tags.setByte("Direction", direction);
        FluidStack liquid = tank.getFluid();
        tags.setBoolean("hasFluid", liquid != null);
        if (liquid != null)
        {
            tags.setInteger("itemID", liquid.fluidID);
            tags.setInteger("amount", liquid.amount);
        }
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
