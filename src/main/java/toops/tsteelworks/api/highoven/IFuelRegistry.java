package toops.tsteelworks.api.highoven;

import net.minecraft.item.ItemStack;
import toops.tsteelworks.api.PluginFactory;
import toops.tsteelworks.api.event.IRegistry;

public interface IFuelRegistry extends IRegistry<ItemStack, IFuelRegistry.IFuelData> {
	public static final IFuelRegistry INSTANCE = (IFuelRegistry) PluginFactory.getInstance(IFuelRegistry.class);

	/**
	 * Gets the FuelData instance registered for a given ItemStack
	 *
	 * @param fuel The ItemStack
	 * @return the instance of FuelData registered for this ItemStack or null if none has been registered.
	 */
	public IFuelData getFuel(ItemStack fuel);

	/**
	 * Registers an itemstack as valid fuel. FuelData is replaced if already existing.
	 *
	 * @param fuel The ItemStack used as fuel
	 * @param burnTime The amount of time in ticks that this fuel is going to last
	 * @param heatRate The amount of heat this is going to provide at each burn
	 * @return the previously registered fueldata or null if none were registered.
	 */
	public IFuelData addFuel(ItemStack fuel, int burnTime, int heatRate);

	/**
	 * Unregisters an ItemStack as valid fuel
	 *
	 * @param fuel the itemstack to unregister
	 * @return the FuelData that was registered, or null if none were registered
	 */
	public IFuelData removeFuel(ItemStack fuel);

	/**
	 * Wrapper interface for fuel time and heat rate
	 */
	public static interface IFuelData {
		public int getBurnTime();
		public int getHeatRate();
	}
}