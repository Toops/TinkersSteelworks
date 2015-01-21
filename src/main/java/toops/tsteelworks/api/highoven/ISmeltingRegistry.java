package toops.tsteelworks.api.highoven;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import toops.tsteelworks.api.PluginFactory;

public interface ISmeltingRegistry {
	public static final ISmeltingRegistry INSTANCE = (ISmeltingRegistry) PluginFactory.getInstance(ISmeltingRegistry.class);

	/**
	 * Adds mappings between an input and its liquid.
	 *
	 * @param input             The item to liquify
	 * @param meltTemperature   How hot the block should be before liquifying
	 * @param output            The result of the process
	 * @param isOre             The itemstack is an ore
	 */
	public void addMeltable(ItemStack input, boolean isOre, int meltTemperature, FluidStack output);

	/**
	 * Adds all Items to the Smeltery based on the oreDictionary Name
	 *
	 * @param inputOre          oreDictionary name e.g. oreIron
	 * @param output            FluidStack to add to the high oven when the ore melts
	 * @param meltTemperature   How hot the ItemStacks should be before liquifying
	 */
	public void addDictionaryMeltable(String inputOre, FluidStack output, int meltTemperature);

	/**
	 * Returns melt informations about an itemstack
	 *
	 * @param stack The ItemStack to melt
	 * @return The melt information instance, or null if does not melt
	 */
	public IMeltData getMeltable(ItemStack stack);

	/**
	 * Sets an itemstack as non meltable.
	 *
	 * @param stack the itemstack to remove
	 * @return the previously registered melting informations
	 */
	public IMeltData removeMeltable(ItemStack stack);

	/**
	 * Hold information about a smeltable ItemStack
	 */
	public static interface IMeltData {
		public int getMeltingPoint();
		public FluidStack getResult();
		public boolean isOre();
	}
}
