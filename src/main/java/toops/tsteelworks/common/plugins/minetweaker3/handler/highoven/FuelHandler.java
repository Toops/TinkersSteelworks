package toops.tsteelworks.common.plugins.minetweaker3.handler.highoven;

import net.minecraft.item.ItemStack;
import toops.tsteelworks.api.highoven.IFuelRegistry;
import toops.tsteelworks.api.highoven.IFuelRegistry.IFuelData;
import toops.tsteelworks.common.plugins.minetweaker3.MinetweakerPlugin;

class FuelHandler {
	static class Add extends MinetweakerPlugin.Add<ItemStack, IFuelData> {
		public Add(final ItemStack fuel, final int burnTime, final int heatRate) {
			super(fuel, new IFuelRegistry.IFuelData() {
				@Override
				public int getBurnTime() {
					return burnTime;
				}

				@Override
				public int getHeatRate() {
					return heatRate;
				}
			});
		}

		@Override
		public void apply() {
			oldData = IFuelRegistry.INSTANCE.addFuel(key, newData.getBurnTime(), newData.getHeatRate());
		}

		@Override
		public void undo() {
			if (oldData == null)
				IFuelRegistry.INSTANCE.removeFuel(key);
			else
				IFuelRegistry.INSTANCE.addFuel(key, oldData.getBurnTime(), oldData.getHeatRate());
			
			oldData = null;
		}

		@Override
		public String describe() {
			return (oldData == null ? "Added " : "Replaced ") + key.getDisplayName() + " as valid High Oven fuel.";
		}
	}

	static class Remove extends MinetweakerPlugin.Remove<ItemStack, IFuelData> {
		public Remove(ItemStack stack) {
			super(stack);
		}

		@Override
		public void apply() {
			oldData = IFuelRegistry.INSTANCE.removeFuel(key);
		}

		@Override
		public void undo() {
			if (oldData == null) return;
			
			IFuelRegistry.INSTANCE.addFuel(key, oldData.getBurnTime(), oldData.getHeatRate());
		}

		@Override
		public String describe() {
			return "Removed " + key.getDisplayName() + " as valid High Oven fuel.";
		}
	}
}