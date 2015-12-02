package toops.tsteelworks.api.highoven;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import toops.tsteelworks.api.PluginFactory;
import toops.tsteelworks.api.event.IRegistry;

import javax.annotation.Nullable;

public interface IMixerRegistry extends IRegistry<IMixerRegistry.IMixHolder, IMixerRegistry.IMixOutput> {
	IMixerRegistry INSTANCE = (IMixerRegistry) PluginFactory.getInstance(IMixerRegistry.class);

	/**
	 * Registers a valid Oxidizer-reducer-purifier mix for an input fluid
	 *
	 * @param fluidOutput The resulting fluid once mixed with ORPs
	 * @param solidOutput The resulting item once the fluid is mixed with ORPs
	 * @param input       The fluid used in the mix
	 * @param oxidizer    A valid oxidizer
	 * @param reducer     A valid reducer
	 * @param purifier    A valid purifier
	 * @return What was previously registered for this mix's output, or null if none was
	 */
	@Nullable
	IMixOutput registerMix(@Nullable FluidStack fluidOutput, @Nullable ItemStack solidOutput, Fluid input, @Nullable String oxidizer, @Nullable String reducer, @Nullable String purifier);

	/**
	 * Unregisters an Oxidizer-reducer-purifier mix
	 *
	 * @param input    The input fluid
	 * @param oxidizer The oxidizer used in the mix
	 * @param reducer  The reducer used in the mix
	 * @param purifier The purifier used in the mix
	 * @return the previously resulting ItemStack &amp; FluidStack, or null if nothing was removed
	 */
	@Nullable
	IMixOutput removeMix(Fluid input, @Nullable String oxidizer, @Nullable String reducer, @Nullable String purifier);

	/**
	 * Gets the resulting ItemStack &amp; FluidStack for an Oxidizer-reducer-purifier mix
	 *
	 * @param input    The input fluid
	 * @param oxidizer The oxidizer used in the mix
	 * @param reducer  The reducer used in the mix
	 * @param purifier The purifier used in the mix
	 * @return the resulting mix output
	 */
	@Nullable
	IMixOutput getMix(Fluid input, @Nullable ItemStack oxidizer, @Nullable ItemStack reducer, @Nullable ItemStack purifier);

	/**
	 * Holder for mix inputs
	 */
	interface IMixHolder {
		/**
		 * @return Oredict name for oxidizer
		 */
		String getOxidizer();

		/**
		 * @return Oredict name for reducer
		 */
		String getReducer();

		/**
		 * @return Oredict name for purifier
		 */
		String getPurifier();

		/**
		 * @return input fluid
		 */
		Fluid getInputFluid();
	}

	/**
	 * Holder for mix results
	 */
	interface IMixOutput {
		/**
		 * @return resulting fluid
		 */
		@Nullable
		FluidStack getFluidOutput();

		/**
		 * @return resulting itemstack
		 */
		@Nullable
		ItemStack getSolidOutput();
	}
}