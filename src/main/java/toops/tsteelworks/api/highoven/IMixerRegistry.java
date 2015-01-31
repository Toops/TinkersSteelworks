package toops.tsteelworks.api.highoven;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import toops.tsteelworks.api.PluginFactory;
import toops.tsteelworks.api.event.IRegistry;

import javax.annotation.Nullable;

public interface IMixerRegistry extends IRegistry<IMixerRegistry.IMixHolder, IMixerRegistry.IMixOutput> {
	public static final IMixerRegistry INSTANCE = (IMixerRegistry) PluginFactory.getInstance(IMixerRegistry.class);

	/**
	 * Registers a valid Oxidizer-reducer-purifier mix for an input fluid
	 *
	 * @param fluidOutput   The resulting fluid once mixed with ORPs
	 * @param solidOutput   The resulting item once the fluid is mixed with ORPs
	 * @param input         The fluid used in the mix
	 * @param oxidizer      A valid oxidizer
	 * @param reducer       A valid reducer
	 * @param purifier      A valid purifier
	 *
	 * @return What was previously registered for this mix's output, or null if none was
	 */
	public @Nullable IMixOutput registerMix(@Nullable FluidStack fluidOutput, @Nullable ItemStack solidOutput, Fluid input, @Nullable String oxidizer, @Nullable String reducer, @Nullable String purifier);

	/**
	 * Unregisters an Oxidizer-reducer-purifier mix
	 *
	 * @param input     The input fluid
	 * @param oxidizer  The oxidizer used in the mix
	 * @param reducer   The reducer used in the mix
	 * @param purifier  The purifier used in the mix
	 *
	 * @return the previously resulting ItemStack&FluidStack, or null if nothing was removed
	 */
	public @Nullable IMixOutput removeMix(Fluid input, @Nullable String oxidizer, @Nullable String reducer, @Nullable String purifier);

	/**
	 * Gets the resulting ItemStack & FluidStack for an Oxidizer-reducer-purifier mix
	 *
	 * @param input     The input fluid
	 * @param oxidizer  The oxidizer used in the mix
	 * @param reducer   The reducer used in the mix
	 * @param purifier  The purifier used in the mix
	 *
	 * @return the resulting mix output
	 */
	public @Nullable IMixOutput getMix(Fluid input, @Nullable ItemStack oxidizer, @Nullable ItemStack reducer, @Nullable ItemStack purifier);

	/**
	 * Holder for mix inputs
	 */
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

	/**
	 * Holder for mix results
	 */
	public static interface IMixOutput {
		/**
		 * @return resulting fluid
		 */
		public @Nullable FluidStack getFluidOutput();

		/**
		 * @return resulting itemstack
		 */
		public @Nullable ItemStack getSolidOutput();
	}
}