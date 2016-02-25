package toops.tsteelworks.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import toops.tsteelworks.common.blocks.logic.DeepTankLogic;

public class DeepTankContainer extends Container {
	public DeepTankLogic logic;
	public InventoryPlayer playerInv;

	public DeepTankContainer(InventoryPlayer inventoryplayer, DeepTankLogic tank) {
		logic = tank;
		playerInv = inventoryplayer;
	}

	@Override
	public boolean canInteractWith(EntityPlayer p_75145_1_) {
		return true;
	}
}
