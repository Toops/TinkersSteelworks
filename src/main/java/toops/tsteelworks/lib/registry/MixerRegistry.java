package toops.tsteelworks.lib.registry;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import toops.tsteelworks.api.highoven.IMixerRegistry;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class MixerRegistry extends BasicRegistry<IMixerRegistry.IMixHolder, IMixerRegistry.IMixOutput> implements IMixerRegistry {
	/* ========== IMixerRegistry ========== */
	private final Map<IMixHolder, IMixOutput> comboList = new HashMap<>();

	@Override
	@Nullable
	public IMixOutput registerMix(@Nullable FluidStack fluidout, @Nullable ItemStack itemout, Fluid fluidin, String ox, String red, String pur) {
		if (fluidout == null && itemout == null)
			throw new IllegalArgumentException("The fluid output & Item output cannot be both null");

		MixCombo mix = new MixCombo(ox, red, pur, fluidin);

		MixOutput newMix = new MixOutput(fluidout, itemout);
		IMixOutput oldMix = comboList.put(mix, newMix);

		if (oldMix != null) dispatchDeleteEvent(mix, oldMix);

		dispatchAddEvent(mix, newMix);

		return oldMix;
	}

	@Override
	@Nullable
	public IMixOutput removeMix(Fluid input, String oxidizer, String reducer, String purifier) {
		MixCombo mixInput = new MixCombo(oxidizer, reducer, purifier, input);
		IMixOutput oldMix = comboList.remove(mixInput);

		if (oldMix != null) dispatchDeleteEvent(mixInput, oldMix);

		return oldMix;
	}

	@Override
	public
	@Nullable
	IMixOutput getMix(Fluid input, @Nullable ItemStack oxidizer, @Nullable ItemStack reducer, @Nullable ItemStack purifier) {
		if (oxidizer == null && reducer == null && purifier == null) return null;

		int[] oxidIDs = oxidizer == null ? new int[]{-1} : OreDictionary.getOreIDs(oxidizer);
		int[] reduIDs = reducer == null ? new int[]{-1} : OreDictionary.getOreIDs(reducer);
		int[] puriIDs = purifier == null ? new int[]{-1} : OreDictionary.getOreIDs(purifier);

		MixCombo combo = new MixCombo();
		combo.setFluid(input);

		for (int oxidID : oxidIDs) {
			if (oxidID != -1) {
				String oxiName = OreDictionary.getOreName(oxidID);
				combo.setOxydizer(oxiName);
			}

			for (int reduID : reduIDs) {
				if (reduID != -1) {
					String reduName = OreDictionary.getOreName(reduID);
					combo.setReducer(reduName);
				}

				for (int puriID : puriIDs) {
					if (puriID != -1) {
						String puriName = OreDictionary.getOreName(puriID);
						combo.setPurifier(puriName);
					}

					IMixOutput result = comboList.get(combo);
					if (result != null) return result;
				}
			}
		}

		return null;
	}

	@Override
	public Iterator<Map.Entry<IMixHolder, IMixOutput>> iterator() {
		return comboList.entrySet().iterator();
	}

	private static class MixOutput implements IMixOutput {
		private final FluidStack fluidOutput;
		private final ItemStack itemOutput;

		private MixOutput(FluidStack fluidOutput, ItemStack itemOutput) {
			this.fluidOutput = fluidOutput;
			this.itemOutput = itemOutput;
		}

		@Nullable
		@Override
		public FluidStack getFluidOutput() {
			return fluidOutput;
		}

		@Nullable
		@Override
		public ItemStack getSolidOutput() {
			return itemOutput;
		}
	}

	private static class MixCombo implements IMixHolder {
		private String oxydizer;
		private String reducer;
		private String purifier;
		private Fluid fluid;

		public MixCombo(String oxydizer, String reducer, String purifier, Fluid fluid) {
			this.oxydizer = oxydizer;
			this.reducer = reducer;
			this.purifier = purifier;
			this.fluid = fluid;
		}

		private MixCombo() {
		}

		private void setFluid(Fluid fluid) {
			this.fluid = fluid;
		}

		private void setOxydizer(String oxydizer) {
			this.oxydizer = oxydizer;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			MixCombo mixCombo = (MixCombo) o;

			if (!fluid.equals(mixCombo.fluid))
				return false;
			if (oxydizer != null ? !oxydizer.equals(mixCombo.oxydizer) : mixCombo.oxydizer != null)
				return false;
			if (purifier != null ? !purifier.equals(mixCombo.purifier) : mixCombo.purifier != null)
				return false;
			if (reducer != null ? !reducer.equals(mixCombo.reducer) : mixCombo.reducer != null)
				return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = oxydizer != null ? oxydizer.hashCode() : 0;
			result = 31 * result + (reducer != null ? reducer.hashCode() : 0);
			result = 31 * result + (purifier != null ? purifier.hashCode() : 0);
			result = 31 * result + fluid.hashCode();
			return result;
		}

		@Override
		public String getOxidizer() {
			return oxydizer;
		}

		@Override
		public String getReducer() {
			return reducer;
		}

		private void setReducer(String reducer) {
			this.reducer = reducer;
		}

		@Override
		public String getPurifier() {
			return purifier;
		}

		private void setPurifier(String purifier) {
			this.purifier = purifier;
		}

		@Override
		public Fluid getInputFluid() {
			return fluid;
		}
	}
}
