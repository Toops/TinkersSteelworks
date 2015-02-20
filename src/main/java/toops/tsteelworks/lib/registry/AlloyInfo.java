package toops.tsteelworks.lib.registry;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.library.crafting.AlloyMix;
import tconstruct.library.crafting.Smeltery;
import toops.tsteelworks.common.core.ConfigCore;

import java.util.ArrayList;
import java.util.List;

public final class AlloyInfo {
	private static final List<FluidStack> whitelistedFluids = new ArrayList<>();

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

				FluidStack[] output = new FluidStack[components.size()];

				for (int i = 0; i < output.length; i++) {
					float ratio = (float)  components.get(i).amount / alloy.result.amount;

					output[i] = new FluidStack(components.get(i), (int) (input.amount * ratio));
				}

				return output;
			}
		}

		return null;
	}

	public static void generateDealloyList() {
		List<AlloyMix> alloys = Smeltery.getAlloyList();

		for (AlloyMix alloy : alloys) {
			if (!isBlackListed(alloy.result.getFluid()))
				whitelistedFluids.add(alloy.result);
		}
	}

	public static boolean isAlloy(FluidStack fluid) {
		return whitelistedFluids.contains(fluid);
	}

	private static boolean isBlackListed(Fluid fluid) {
		for (String name : ConfigCore.blacklistedAlloys) {
			if (name.equals(fluid.getName())) return true;
		}

		return false;
	}
}
