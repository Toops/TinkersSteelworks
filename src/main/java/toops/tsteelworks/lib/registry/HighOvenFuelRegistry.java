package toops.tsteelworks.lib.registry;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import nf.fr.ephys.cookiecore.util.HashedItemStack;
import toops.tsteelworks.api.highoven.IFuelRegistry;

import java.util.HashMap;

class HighOvenFuelRegistry extends BasicRegistry<ItemStack, IFuelRegistry.IFuelData> implements IFuelRegistry {
	private HashedItemStack proxy = new HashedItemStack(new ItemStack(Blocks.stone));
	private HashMap<HashedItemStack, FuelData> fuelCollection = new HashMap<>();

	public IFuelData getFuel(ItemStack fuel) {
		return fuelCollection.get(proxy.setItemStack(fuel));
	}

	public IFuelData addFuel(ItemStack fuel, int burnTime, int heatRate) {
		FuelData fueldata = new FuelData(burnTime, heatRate);
		FuelData oldData = fuelCollection.put(new HashedItemStack(fuel), fueldata);

		if (oldData != null) dispatchDeleteEvent(fuel, oldData);

		dispatchAddEvent(fuel, fueldata);
		return fueldata;
	}

	public IFuelData removeFuel(ItemStack fuel) {
		FuelData fuelData = fuelCollection.remove(new HashedItemStack(fuel));

		if (fuelData == null) return null;

		dispatchDeleteEvent(fuel, fuelData);
		return fuelData;
	}

	static final class FuelData implements IFuelData {
		private final int burnTime;
		private final int heatRate;

		public FuelData(int burnTime, int heatRate) {
			this.burnTime = burnTime;
			this.heatRate = heatRate;
		}

		public int getBurnTime() {
			return burnTime;
		}

		public int getHeatRate() {
			return heatRate;
		}
	}
}