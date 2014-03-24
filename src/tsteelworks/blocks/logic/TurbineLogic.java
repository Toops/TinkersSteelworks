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
import net.minecraftforge.fluids.FluidStack;
import tconstruct.library.util.CoordTuple;
import tconstruct.library.util.IFacingLogic;
import tsteelworks.lib.blocks.TSInventoryLogic;

public class TurbineLogic extends TSInventoryLogic implements IFacingLogic
{
    byte direction;
    
    public TurbineLogic()
    {
        super(0);
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
    
//    @Override
//    public void readFromNBT (NBTTagCompound tags)
//    {
//        super.readFromNBT(tags);
//        direction = tags.getByte("Direction");
//    }
//    
//    @Override
//    public void writeToNBT (NBTTagCompound tags)
//    {
//        super.writeToNBT(tags);
//        tags.setByte("Direction", direction);
//    }
//    
//    /* Packets */
//    @Override
//    public void onDataPacket (INetworkManager net, Packet132TileEntityData packet)
//    {
//        readFromNBT(packet.data);
//        worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
//    }
//    
//    @Override
//    public Packet getDescriptionPacket ()
//    {
//        final NBTTagCompound tag = new NBTTagCompound();
//        writeToNBT(tag);
//        return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag);
//    }
}
