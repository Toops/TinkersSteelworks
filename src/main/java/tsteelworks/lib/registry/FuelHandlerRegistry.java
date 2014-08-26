package tsteelworks.lib.registry;

import net.minecraft.item.ItemStack;

import java.util.HashMap;

public class FuelHandlerRegistry {
	private static AdvancedSmelting.HashedItemStack proxy = new AdvancedSmelting.HashedItemStack(null);
	private static HashMap<AdvancedSmelting.HashedItemStack, FuelData> fuelCollection = new HashMap<>();

	public static FuelData getHighOvenFuelData(ItemStack fuel) {
		return fuelCollection.get(proxy.setItemStack(fuel));
	}

	public static void addFuel(ItemStack fuel, int burnTime, int heatRate) {
		fuelCollection.put(new AdvancedSmelting.HashedItemStack(fuel), new FuelData(burnTime, heatRate));
	}

	public static void removeFuel(ItemStack fuel) {
		fuelCollection.remove(new AdvancedSmelting.HashedItemStack(fuel));
	}

	public static class FuelData {
		private int burnTime;
		private int heatRate;

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

		public void setBurnTime(int burnTime) {
			this.burnTime = burnTime;
		}

		public void setHeatRate(int heatRate) {
			this.heatRate = heatRate;
		}
	}
}