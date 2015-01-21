package toops.tsteelworks.lib.registry;

import net.minecraft.item.ItemStack;
import nf.fr.ephys.cookiecore.util.HashedItemStack;
import toops.tsteelworks.api.highoven.IFuelRegistry;

import java.util.HashMap;

class HighOvenFuelRegistry implements IFuelRegistry {
	private HashedItemStack proxy = new HashedItemStack(null);
	private HashMap<HashedItemStack, FuelData> fuelCollection = new HashMap<>();

	public IFuelData getFuel(ItemStack fuel) {
		return fuelCollection.get(proxy.setItemStack(fuel));
	}

	public IFuelData addFuel(ItemStack fuel, int burnTime, int heatRate) {
		return fuelCollection.put(new HashedItemStack(fuel), new FuelData(burnTime, heatRate));
	}

	public IFuelData removeFuel(ItemStack fuel) {
		return fuelCollection.remove(new HashedItemStack(fuel));
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