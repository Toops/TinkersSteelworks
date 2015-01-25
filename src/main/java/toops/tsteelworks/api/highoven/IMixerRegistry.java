package toops.tsteelworks.api.highoven;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import toops.tsteelworks.api.PluginFactory;
import toops.tsteelworks.api.event.IRegistry;

public interface IMixerRegistry extends IRegistry<IMixerRegistry.IMixHolder, Object> {
	public static final IMixerRegistry INSTANCE = (IMixerRegistry) PluginFactory.getInstance(IMixerRegistry.class);

	/**
	 * Registers a valid Oxidizer-reducer-purifier mix for an input fluid
	 *
	 * @param output    The resulting fluid once mixed with ORPs
	 * @param input     The fluid used in the mix
	 * @param oxidizer  A valid oxidizer
	 * @param reducer   A valid reducer
	 * @param purifier  A valid purifier
	 *
	 * @return What was previously registered for this mix output, or null if none was
	 */
	public Object registerMix(FluidStack output, Fluid input, String oxidizer, String reducer, String purifier);

	/**
	 * Registers a valid Oxidizer-reducer-purifier mix for an input fluid
	 *
	 * @param output    The resulting ItemStack once the fluid is mixed with ORPs
	 * @param input     The fluid used in the mix
	 * @param oxidizer  A valid oxidizer
	 * @param reducer   A valid reducer
	 * @param purifier  A valid purifier
	 *
	 * @return What was previously registered for this mix output, or null if none was
	 */
	public Object registerMix(ItemStack output, Fluid input, String oxidizer, String reducer, String purifier);

	/**
	 * Unregisters an Oxidizer-reducer-purifier mix
	 *
	 * @param input     The input fluid
	 * @param oxidizer  The oxidizer used in the mix
	 * @param reducer   The reducer used in the mix
	 * @param purifier  The purifier used in the mix
	 *
	 * @return the former resulting ItemStack or FluidStack, or null if nothing was removed
	 */
	public Object removeMix(Fluid input, String oxidizer, String reducer, String purifier);

	/**
	 * Gets the resulting ItemStack or FluidStack for an Oxidizer-reducer-purifier mix
	 *
	 * @param input     The input fluid
	 * @param oxidizer  The oxidizer used in the mix
	 * @param reducer   The reducer used in the mix
	 * @param purifier  The purifier used in the mix
	 *
	 * @return the resulting ItemStack or FluidStack, or null if the mix is invalid
	 */
	public Object getMix(Fluid input, ItemStack oxidizer, ItemStack reducer, ItemStack purifier);

	public static interface IMixHolder {
		/**
		 * @return Oredict name for oxidizer
		 */
		public String getOxidizer();

		/**
		 * @return Oredict name for reducer
		 */
		public String getReducer();

		/**
		 * @return Oredict name for purifier
		 */
		public String getPurifier();

		/**
		 * @return input fluid
		 */
		public Fluid getInputFluid();
	}
}