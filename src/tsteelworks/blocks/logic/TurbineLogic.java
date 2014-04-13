package tsteelworks.blocks.logic;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import tconstruct.TConstruct;
import tconstruct.library.util.IActiveLogic;
import tconstruct.library.util.IFacingLogic;
import tsteelworks.TSteelworks;
import tsteelworks.lib.blocks.TSInventoryLogic;

public class TurbineLogic extends TSInventoryLogic implements IActiveLogic, IFacingLogic, IFluidHandler
{
    byte direction;
    int tick;
    public FluidTank tank;
    boolean active;
    boolean redstoneActivated;
    
    
    public TurbineLogic()
    {
        super(0);
        active = false;
        redstoneActivated = false;
        tank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME);
    }
    
    /* ==================== Redstone Logic ==================== */
    
    /**
     * Get the current state of redstone-connected power
     * 
     * @return Redstone powered state
     */
    public boolean getRedstoneActive ()
    {
        return redstoneActivated;
    }

    /**
     * Set the redstone powered state
     * 
     * @param flag
     *          true: powered / false: not powered
     */
    public void setRedstoneActive (boolean flag)
    {
        redstoneActivated = flag;
        if (redstoneActivated) setActive(flag);
    }
    
    // ========== IActiveLogic ==========
    @Override
    public boolean getActive ()
    {
        return active && this.redstoneActivated;
    }

    @Override
    public void setActive (boolean flag)
    {
        active = flag;
        activateTurbine(active);
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    void activateTurbine (boolean flag)
    {
        if (tank.getFluid() != null)
        {
            DeepTankLogic tankcontroller = getTankController();
            if (tankcontroller != null)
                tankcontroller.setTurbineAttached(flag);
        }
    }
    
    DeepTankLogic getTankController ()
    {
        int x = xCoord, z = zCoord;
        switch (getRenderDirection())
        {
        case 2:
            z--;
            break;
        case 3:
            z++;
            break;
        case 4:
            x--;    
            break;
        case 5:
            x++;
            break;
        }

        TileEntity drainte = worldObj.getBlockTileEntity(x, yCoord, z);
        if (drainte instanceof HighOvenDrainLogic)
        {
            if (((HighOvenDrainLogic) worldObj.getBlockTileEntity(x, yCoord, z)).getControllerLogicType() == 2)
            {
                DeepTankLogic tankcontroller = ((HighOvenDrainLogic) worldObj.getBlockTileEntity(x, yCoord, z)).getDeepTankController();
                return tankcontroller;
            }
        } 
        return null;
    }
    
    /* Updating */
    public boolean canUpdate ()
    {
        return true;
    }

    public void updateEntity ()
    {
        tick++;
        if (tick == 60)
            tick = 0;
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
//        if (resource.getFluid() != FluidRegistry.getFluid("Steam")) return 0;
        int amount = tank.fill(resource, doFill);
        if (amount > 0 && doFill)
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
        active = tags.getBoolean("Active");
        redstoneActivated = tags.getBoolean("RedstoneActivated");
        direction = tags.getByte("Direction");
        if (tags.getBoolean("hasFluid"))
            tank.setFluid(new FluidStack(tags.getInteger("itemID"), tags.getInteger("amount")));
        else
            tank.setFluid(null);
    }
    
    @Override
    public void writeToNBT (NBTTagCompound tags)
    {
        tags.setBoolean("Active", active);
        tags.setBoolean("RedstoneActivated", redstoneActivated);
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
