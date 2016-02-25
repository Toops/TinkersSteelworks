package toops.tsteelworks.common.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class TSActiveSlot extends Slot {
	protected boolean active;

	public TSActiveSlot(IInventory iinventory, int par2, int par3, int par4, boolean flag) {
		super(iinventory, par2, par3, par4);
		active = flag;
	}

	public boolean getActive() {
		return active;
	}

	public void setActive(boolean flag) {
		active = flag;
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}
}