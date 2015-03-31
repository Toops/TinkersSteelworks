package toops.tsteelworks.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import toops.tsteelworks.api.highoven.IFuelRegistry;
import toops.tsteelworks.api.highoven.IMixAgentRegistry;
import toops.tsteelworks.api.highoven.IMixAgentRegistry.IMixAgent;
import toops.tsteelworks.api.highoven.ISmeltingRegistry;
import toops.tsteelworks.common.blocks.logic.HighOvenLogic;

public class HighOvenContainer extends Container {
	private final HighOvenLogic logic;

	public HighOvenContainer(InventoryPlayer inventoryplayer, HighOvenLogic highoven) {
		logic = highoven;

		IInventory baseInventory = highoven.getInventory();
		/* HighOven Misc inventory */
		addSlotToContainer(new Slot(baseInventory, HighOvenLogic.SLOT_OXIDIZER, 55, 16)); // oxidizer
		addSlotToContainer(new Slot(baseInventory, HighOvenLogic.SLOT_REDUCER, 55, 34)); // reducer
		addSlotToContainer(new Slot(baseInventory, HighOvenLogic.SLOT_PURIFIER, 55, 52)); // purifier
		addSlotToContainer(new Slot(baseInventory, HighOvenLogic.SLOT_FUEL, 126, 52)); // fuel

		/* HighOven Smeltable inventory */
		IInventory smeltableInventory = highoven.getSmeltableInventory();
		for (int slot = 0; slot < highoven.getSmeltableInventory().getSizeInventory(); slot++)
			addSlotToContainer(new TSActiveSlot(smeltableInventory, slot, 28, 7 + (slot * 18), slot < 6));

		/* Player inventory */
		for (int row = 0; row < 3; row++)
			for (int column = 0; column < 9; column++)
				addSlotToContainer(new Slot(inventoryplayer, column + (row * 9) + 9, 54 + (column * 18), 84 + (row * 18)));

		/* Player hotbar */
		for (int column = 0; column < 9; column++)
			addSlotToContainer(new Slot(inventoryplayer, column, 54 + (column * 18), 142));
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return logic.isUseableByPlayer(entityplayer);
	}

	@Override
	/**
	 * Transfers stack from sourceSlot to any other
	 */
	public ItemStack transferStackInSlot(EntityPlayer player, int sourceSlot) {
		final Slot slot = (Slot) inventorySlots.get(sourceSlot);
		if (slot == null || !slot.getHasStack()) return null;

		ItemStack sourceStack = slot.getStack();
		final ItemStack stack = sourceStack.copy();

		if (sourceSlot < logic.getSizeInventory()) { // is from TE inventory
			if (!mergeItemStack(sourceStack, logic.getSizeInventory(), inventorySlots.size(), true))
				return null;
		} else { // is from player inventory
			if (!mergeToTE(sourceStack)) return null;
		}

		if (sourceStack.stackSize == 0)
			slot.putStack(null);
		else
			slot.onSlotChanged();

		return stack;
	}

	private boolean mergeToTE(ItemStack sourceStack) {
		boolean merged = false;

		if (IFuelRegistry.INSTANCE.getFuel(sourceStack) != null) { // is fuel
			merged = mergeItemStack(sourceStack, HighOvenLogic.SLOT_FUEL, HighOvenLogic.SLOT_FUEL + 1, false);
		}

		if (sourceStack.stackSize == 0) return merged;

		// is mixAgent
		IMixAgent agent = IMixAgentRegistry.INSTANCE.getAgentData(sourceStack);
		if (agent != null) {
			switch (agent.getType()) {
				case OXIDIZER:
					merged = mergeItemStack(sourceStack, HighOvenLogic.SLOT_OXIDIZER, HighOvenLogic.SLOT_OXIDIZER + 1, false) || merged;
					break;
				case PURIFIER:
					merged = mergeItemStack(sourceStack, HighOvenLogic.SLOT_PURIFIER, HighOvenLogic.SLOT_PURIFIER + 1, false) || merged;
					break;
				case REDUCER:
					merged = mergeItemStack(sourceStack, HighOvenLogic.SLOT_REDUCER, HighOvenLogic.SLOT_REDUCER + 1, false) || merged;
			}
		}

		if (sourceStack.stackSize == 0) return merged;

		// TODO: replace this by an overwrite of mergeItemStack to respect maxStackSize.
		if (ISmeltingRegistry.INSTANCE.getMeltable(sourceStack) != null) { // is meltable
			IInventory smeltInventory = logic.getSmeltableInventory();
			for (int i = 0; i < smeltInventory.getSizeInventory(); i++) {
				if (smeltInventory.getStackInSlot(i) != null) continue;

				ItemStack toInsert = sourceStack.copy();
				toInsert.stackSize = 1;

				boolean inserted = mergeItemStack(toInsert,
						HighOvenLogic.SLOT_FIRST_MELTABLE + i,
						HighOvenLogic.SLOT_FIRST_MELTABLE + i + 1,
						false);

				if (inserted)
					sourceStack.stackSize--;

				merged = inserted || merged;
				if (sourceStack.stackSize == 0) return merged;
			}
		}

		return merged;
	}

	@Override
	public void updateProgressBar(int id, int value) {
		if (id == 0)
			logic.setFuelBurnTime(value / 12);
	}

	public HighOvenLogic getLogic() {
		return logic;
	}
}
