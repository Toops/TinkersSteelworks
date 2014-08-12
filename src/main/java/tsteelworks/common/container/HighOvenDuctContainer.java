package tsteelworks.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import tsteelworks.common.blocks.logic.HighOvenDuctLogic;

public class HighOvenDuctContainer extends Container {
	private HighOvenDuctLogic logic;

	public HighOvenDuctContainer(InventoryPlayer inventoryplayer, HighOvenDuctLogic duct) {
		logic = duct;

		for (int column = 0; column < 9; column++)
			addSlotToContainer(new Slot(logic, column, 54 + (column * 18), 16));

	     /* Player inventory */
		for (int column = 0; column < 3; column++)
			for (int row = 0; row < 9; row++)
				addSlotToContainer(new Slot(inventoryplayer, row + (column * 9) + 9, 54 + (row * 18), 84 + (column * 18)));
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

	public HighOvenDuctLogic getLogic() {
		return logic;
	}
}
