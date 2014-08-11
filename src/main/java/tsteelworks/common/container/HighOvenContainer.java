package tsteelworks.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import tsteelworks.common.blocks.logic.HighOvenLogic;

public class HighOvenContainer extends TSActiveContainer {
	private HighOvenLogic logic;

	public HighOvenContainer(InventoryPlayer inventoryplayer, HighOvenLogic highoven) {
		logic = highoven;

        /* HighOven Misc inventory */
		addSlotToContainer(new Slot(highoven, HighOvenLogic.SLOT_OXIDIZER, 55, 16)); // oxidizer
		addSlotToContainer(new Slot(highoven, HighOvenLogic.SLOT_REDUCER, 55, 34)); // reducer
		addSlotToContainer(new Slot(highoven, HighOvenLogic.SLOT_PURIFIER, 55, 52)); // purifier
		addSlotToContainer(new Slot(highoven, HighOvenLogic.SLOT_FUEL, 126, 52)); // fuel

	    /* HighOven Ore inventory */
		for (int y = 0; y < highoven.getSmeltableInventory().getSizeInventory(); y++)
			addDualSlotToContainer(new TSActiveSlot(highoven, HighOvenLogic.SLOT_FIRST_MELTABLE + y, 28, 7 + (y * 18), y < 6));

        /* Player inventory */
		for (int column = 0; column < 3; column++)
			for (int row = 0; row < 9; row++)
				addSlotToContainer(new Slot(inventoryplayer, row + (column * 9) + 9, 54 + (row * 18), 84 + (column * 18)));

		/* Player hotbar */
		for (int column = 0; column < 9; column++)
			addSlotToContainer(new Slot(inventoryplayer, column, 54 + (column * 18), 142));
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return logic.isUseableByPlayer(entityplayer);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
		ItemStack stack = null;
		final Slot slot = (Slot) inventorySlots.get(slotID);
		if ((slot != null) && slot.getHasStack()) {
			final ItemStack slotStack = slot.getStack();
			stack = slotStack.copy();

			if (slotID < logic.getSizeInventory()) {
				if (!mergeItemStack(slotStack, logic.getSizeInventory(), inventorySlots.size(), true))
					return null;
			} else if (!mergeItemStack(slotStack, 0, logic.getSizeInventory(), false))
				return null;
			if (slotStack.stackSize == 0)
				slot.putStack(null);
			else
				slot.onSlotChanged();
		}
		return stack;
	}

	@Override
	public void updateProgressBar(int id, int value) {
		if (id == 0)
			logic.setFuelBurnTime(value / 12);
	}
}
