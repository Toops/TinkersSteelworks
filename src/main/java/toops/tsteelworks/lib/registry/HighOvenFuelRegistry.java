package toops.tsteelworks.lib.registry;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import nf.fr.ephys.cookiecore.util.HashedItemStack;
import toops.tsteelworks.api.highoven.IFuelRegistry;

import java.util.*;

class HighOvenFuelRegistry extends BasicRegistry<ItemStack, IFuelRegistry.IFuelData> implements IFuelRegistry {
	private HashedItemStack proxy = new HashedItemStack(new ItemStack(Blocks.stone));
	private Map<HashedItemStack, IFuelData> fuelCollection = new HashMap<>();

	public IFuelData getFuel(ItemStack fuel) {
		return fuelCollection.get(proxy.setItemStack(fuel));
	}

	public IFuelData addFuel(ItemStack fuel, int burnTime, int heatRate) {
		return addFuel(fuel, new FuelData(burnTime, heatRate));
	}

	@Override
	public IFuelData addFuel(ItemStack fuel, IFuelData fuelData) {
		IFuelData oldData = fuelCollection.put(new HashedItemStack(fuel), fuelData);

		if (oldData != null) dispatchDeleteEvent(fuel, oldData);

		dispatchAddEvent(fuel, fuelData);
		return fuelData;
	}

	public IFuelData removeFuel(ItemStack fuel) {
		IFuelData fuelData = fuelCollection.remove(new HashedItemStack(fuel));

		if (fuelData == null) return null;

		dispatchDeleteEvent(fuel, fuelData);
		return fuelData;
	}

	@Override
	public Iterator<Map.Entry<ItemStack, IFuelData>> iterator() {
		return new Iterator<Map.Entry<ItemStack, IFuelData>>() {
			private Iterator<HashedItemStack> keys = fuelCollection.keySet().iterator();

			@Override
			public boolean hasNext() {
				return keys.hasNext();
			}

			@Override
			public Map.Entry<ItemStack, IFuelData> next() {
				HashedItemStack key = keys.next();
				IFuelData output = fuelCollection.get(key);

				return new AbstractMap.SimpleImmutableEntry<>(key.getItemStack(), output);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	private static final class FuelData implements IFuelData {
		private final int burnTime;
		private final int heatRate;

		public FuelData(int burnTime, int heatRate) {
			this.burnTime = burnTime;
			this.heatRate = heatRate;
		}

		@Override
		@Deprecated
		public int getBurnTime() {
			return burnTime;
		}

		@Override
		@Deprecated
		public int getHeatRate() {
			return heatRate;
		}

		@Override
		public int getBurnTime(ItemStack item) {
			return burnTime;
		}

		@Override
		public int getHeatRate(ItemStack item) {
			return heatRate;
		}

		@Override
		public void onStartBurning(ItemStack item) {
			item.stackSize--;
		}
	}
}