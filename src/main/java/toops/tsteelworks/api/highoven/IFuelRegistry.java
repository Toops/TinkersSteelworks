package toops.tsteelworks.api.highoven;

import net.minecraft.item.ItemStack;
import toops.tsteelworks.api.PluginFactory;
import toops.tsteelworks.api.event.IRegistry;

public interface IFuelRegistry extends IRegistry<ItemStack, IFuelRegistry.IFuelData> {
	IFuelRegistry INSTANCE = (IFuelRegistry) PluginFactory.getInstance(IFuelRegistry.class);

	/**
	 * Gets the FuelData instance registered for a given ItemStack
	 *
	 * @param fuel The ItemStack
	 * @return the instance of FuelData registered for this ItemStack or null if none has been registered.
	 */
	IFuelData getFuel(ItemStack fuel);

	/**
	 * Registers an itemstack as a valid consumable fuel. FuelData is replaced if already existing.
	 *
	 * @param fuel     The ItemStack used as fuel
	 * @param burnTime The amount of time in ticks that this fuel is going to last
	 * @param heatRate The amount of heat this is going to provide at each burn
	 * @return the previously registered fueldata or null if none were registered.
	 */
	IFuelData addFuel(ItemStack fuel, int burnTime, int heatRate);

	/**
	 * Same as {@link IFuelRegistry#addFuel(ItemStack, int, int)} but allowing you to customise your {@link IFuelData}
	 *
	 * @param fuel     The ItemStack used as fuel.
	 * @param fuelData A custom fuel data handler.
	 * @return the previously registered fueldata or null if none were registered.
	 */
	IFuelData addFuel(ItemStack fuel, IFuelData fuelData);

	/**
	 * Unregisters an ItemStack as valid fuel
	 *
	 * @param fuel the itemstack to unregister
	 * @return the FuelData that was registered, or null if none were registered
	 */
	IFuelData removeFuel(ItemStack fuel);

	/**
	 * Wrapper interface for fuel time and heat rate
	 */
	interface IFuelData {
		@Deprecated
		int getBurnTime();

		@Deprecated
		int getHeatRate();

		/**
		 * Returns the amount of time in ticks that this fuel is going to last.
		 *
		 * @param item The item about to start burning.
		 * @return The burn time of the item.
		 */
		int getBurnTime(ItemStack item);

		/**
		 * Returns the amount of degrees to add to the high oven at every burn tick (once every 20 ticks)
		 *
		 * @param item The item about to start burning.
		 * @return The heat rate of the item.
		 */
		int getHeatRate(ItemStack item);

		/**
		 * Called when the high oven starts burning the item.
		 * Default behavior would be to decrement the item's stacksize. Externalized for addons.
		 *
		 * @param item The item supposed to burn.
		 */
		void onStartBurning(ItemStack item);
	}
}