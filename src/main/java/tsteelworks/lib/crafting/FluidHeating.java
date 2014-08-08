package tsteelworks.lib.crafting;

import net.minecraftforge.fluids.FluidStack;
import tsteelworks.lib.TSLogger;

import java.util.HashMap;
import java.util.Map;

public class FluidHeating {
	private static Map<Meltable, AdvancedSmelting.MeltData> fluidList = new HashMap<>();
	private static Map<Meltable, AdvancedSmelting.MeltData> gazList = new HashMap<>();

	public static void addFluid(FluidStack fluid, FluidStack gaz, int evaporationPoint) {
		if (evaporationPoint < 0) {
			TSLogger.warning("Cannot add an evaporation point for cold fluids. Sorry :(");
			return;
		}

		fluidList.put(new Meltable(fluid), new AdvancedSmelting.MeltData(evaporationPoint, gaz));
		gazList.put(new Meltable(gaz), new AdvancedSmelting.MeltData(evaporationPoint, fluid));
	}

	/**
	 * Returns the temperature at which a given liquid should turn into a gaz
	 * or -1 if there is no temperature for it
	 */
	public static int getEvaporationPoint(FluidStack stack) {
		AdvancedSmelting.MeltData data = fluidList.get(new Meltable(stack));
		return data == null ? -1 : data.getMeltingPoint();
	}

	/**
	 * Returns the temperature at which a given gaz should turn back into a liquid
	 * or -1 if there is no temperature for it
	 */
	public static int getLiqueficationPoint(FluidStack stack) {
		AdvancedSmelting.MeltData data = gazList.get(new Meltable(stack));
		return data == null ? -1 : data.getMeltingPoint();
	}

	/**
	 * Returns a liquid in it's gazeous version if it exists
	 */
	public static FluidStack getGazVersion(FluidStack stack) {
		AdvancedSmelting.MeltData data = fluidList.get(new Meltable(stack));
		return data == null ? null : data.getResult();
	}

	/**
	 * Returns a gaz in it's liquid version if it exists
	 */
	public static FluidStack getLiquidVersion(FluidStack stack) {
		AdvancedSmelting.MeltData data = gazList.get(new Meltable(stack));
		return data == null ? null : data.getResult();
	}

	public static AdvancedSmelting.MeltData getGazData(FluidStack stack) {
		return gazList.get(new Meltable(stack));
	}

	public static AdvancedSmelting.MeltData getFluidData(FluidStack stack) {
		return fluidList.get(new Meltable(stack));
	}

	/**
	 * Only here to implement equals used by the HashMap.
	 */
	public static class Meltable {
		private FluidStack stack;

		public Meltable(FluidStack stack) {
			this.stack = stack;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof Meltable && stack.isFluidEqual(((Meltable) obj).stack);
		}
	}
}