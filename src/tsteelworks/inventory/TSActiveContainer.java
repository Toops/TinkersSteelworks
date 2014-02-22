package tsteelworks.inventory;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class TSActiveContainer extends Container
{
    public List<TSActiveSlot> activeInventorySlots = new ArrayList<TSActiveSlot>();

    @Override
    public boolean canInteractWith (EntityPlayer entityplayer)
    {
        return false;
    }

    protected TSActiveSlot addDualSlotToContainer (TSActiveSlot slot)
    {
        slot.activeSlotNumber = this.activeInventorySlots.size();
        this.activeInventorySlots.add(slot);
        this.addSlotToContainer(slot);
        return slot;
    }

}