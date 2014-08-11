package tsteelworks.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

import java.util.ArrayList;
import java.util.List;

public class TSActiveContainer extends Container {
	public List<TSActiveSlot> activeInventorySlots = new ArrayList<>();

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return false;
	}

	protected TSActiveSlot addDualSlotToContainer(TSActiveSlot slot) {
		slot.activeSlotNumber = activeInventorySlots.size();
		activeInventorySlots.add(slot);
		addSlotToContainer(slot);

		return slot;
	}
}