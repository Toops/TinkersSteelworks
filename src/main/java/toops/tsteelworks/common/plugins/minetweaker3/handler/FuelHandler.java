package toops.tsteelworks.common.plugins.minetweaker3.handler;

import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import toops.tsteelworks.api.highoven.IFuelRegistry;
import toops.tsteelworks.api.highoven.IFuelRegistry.IFuelData;
import toops.tsteelworks.common.plugins.minetweaker3.MinetweakerPlugin;

import static toops.tsteelworks.common.plugins.minetweaker3.MinetweakerPlugin.parseItem;

@ZenClass("mods.tsteelworks.highoven")
public class FuelHandler {
	@ZenMethod
	public static void addFuel(IItemStack fuel, final int burnTime, final int heatValue) {
		MineTweakerAPI.apply(new Add(parseItem(fuel), new IFuelData() {
			@Override
			public int getBurnTime() {
				return burnTime;
			}

			@Override
			public int getHeatRate() {
				return heatValue;
			}
		}));
	}

	@ZenMethod
	public static void removeFuel(IItemStack fuel) {
		MineTweakerAPI.apply(new Remove(parseItem(fuel)));
	}

	private static class Add extends MinetweakerPlugin.Add<ItemStack, IFuelData> {
		public Add(ItemStack fuel, IFuelData data) {
			super(fuel, data);
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

	private static class Remove extends MinetweakerPlugin.Remove<ItemStack, IFuelData> {
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