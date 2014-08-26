package tsteelworks.lib;

import cpw.mods.fml.common.IFuelHandler;
import net.minecraft.item.ItemStack;

// todo: move to Cookiecore
public class FuelHandler implements IFuelHandler {
	private ItemStack fuel;
	private int burntime;

	public FuelHandler(ItemStack fuel, int burntime) {
		this.fuel = fuel;
		this.burntime = burntime;
	}

	@Override
	public int getBurnTime(ItemStack fuel) {
		if (this.fuel.isItemEqual(fuel))
			return burntime;

		return 0;
	}
}
