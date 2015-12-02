package toops.tsteelworks.api.highoven;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import toops.tsteelworks.api.PluginFactory;
import toops.tsteelworks.api.event.IRegistry;

public interface ISmeltingRegistry extends IRegistry<ItemStack, ISmeltingRegistry.IMeltData> {
	ISmeltingRegistry INSTANCE = (ISmeltingRegistry) PluginFactory.getInstance(ISmeltingRegistry.class);

	/**
	 * Adds mappings between an input and its liquid.
	 *
	 * @param input           The item to liquefy
	 * @param isOre           The itemstack is an ore
	 * @param output          The result of the process
	 * @param meltTemperature How hot the block should be before liquefying
	 * @return The previously registered information for this input or null if it wasn't already registered.
	 */
	IMeltData addMeltable(ItemStack input, boolean isOre, FluidStack output, int meltTemperature);

	/**
	 * Adds all Items to the Smeltery based on the oreDictionary Name.
	 * Beware that entries beginning with "ore" will be marked as being ores.
	 *
	 * @param inputOre        oreDictionary name e.g. oreIron
	 * @param output          FluidStack to add to the high oven when the ore melts
	 * @param meltTemperature How hot the ItemStacks should be before liquefying
	 */
	void addDictionaryMeltable(String inputOre, FluidStack output, int meltTemperature);

	/**
	 * Returns melt information about an itemstack
	 *
	 * @param stack The ItemStack to melt
	 * @return The melt information instance, or null if does not melt
	 */
	IMeltData getMeltable(ItemStack stack);

	/**
	 * Sets an itemstack as non meltable.
	 *
	 * @param stack the itemstack to remove
	 * @return the previously registered melting information
	 */
	IMeltData removeMeltable(ItemStack stack);

	/**
	 * Hold information about a meltable ItemStack
	 */
	interface IMeltData {
		int getMeltingPoint();

		FluidStack getResult();

		boolean isOre();
	}
}
