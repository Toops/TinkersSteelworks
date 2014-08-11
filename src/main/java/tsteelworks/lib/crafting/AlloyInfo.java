package tsteelworks.lib.crafting;

import net.minecraftforge.fluids.FluidStack;
import tconstruct.library.crafting.AlloyMix;
import tconstruct.library.crafting.Smeltery;

import java.util.List;

public final class AlloyInfo {
	/**
	 * Takes a FluidStack alloy, returns it's components. Should equal the reagents required to produce this
	 * function's input in a Tinker's Construct smeltery.
	 * Scaled to the size of our input stack.
	 *
	 * @param input The alloy fluidstack.
	 * @return A list of fluid stacks produced by the dealloying.
	 */
	public static FluidStack[] deAlloy(FluidStack input) {
		List<AlloyMix> alloys = Smeltery.getAlloyList();

		for (AlloyMix alloy : alloys) {
			if (alloy.result.isFluidEqual(input)) {
				List<FluidStack> components = alloy.mixers;

				float ratio = alloy.result.amount / (float) input.amount;

				FluidStack[] output = new FluidStack[components.size()];

				for (int i = 0; i < output.length; i++) {
					output[i] = components.get(i).copy();
					output[i].amount *= ratio;
				}

				return output;
			}
		}

		return null;
	}
}
