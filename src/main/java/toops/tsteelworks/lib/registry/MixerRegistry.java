package toops.tsteelworks.lib.registry;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import toops.tsteelworks.api.highoven.IMixerRegistry;

import java.util.HashMap;
import java.util.Map;

public class MixerRegistry extends BasicRegistry<IMixerRegistry.IMixHolder, Object> implements IMixerRegistry {
	/* ========== IMixerRegistry ========== */
	private final Map<MixCombo, FluidStack> fluidComboList = new HashMap<>();
	private final Map<MixCombo, ItemStack> itemComboList = new HashMap<>();

	@Override
	public FluidStack registerMix(FluidStack fluidout, Fluid fluidin, String ox, String red, String pur) {
		MixCombo mix = new MixCombo(ox, red, pur, fluidin);

		FluidStack oldFluid = fluidComboList.put(mix, fluidout);

		if (oldFluid != null) dispatchDeleteEvent(mix, oldFluid);

		dispatchAddEvent(mix, fluidout);

		return oldFluid;
	}

	@Override
	public ItemStack registerMix(ItemStack stackout, Fluid fluidin, String ox, String red, String pur) {
		MixCombo mix = new MixCombo(ox, red, pur, fluidin);

		ItemStack oldItem = itemComboList.put(mix, stackout);

		if (oldItem != null) dispatchDeleteEvent(mix, oldItem);

		dispatchAddEvent(mix, oldItem);

		return oldItem;
	}

	@Override
	public Object removeMix(Fluid input, String oxidizer, String reducer, String purifier) {
		FluidStack output = removeLiquidMix(input, oxidizer, reducer, purifier);
		ItemStack output2 = removeSolidMix(input, oxidizer, reducer, purifier);

		return output == null ? output2 : output;
	}

	@Override
	public ItemStack removeSolidMix(Fluid input, String oxidizer, String reducer, String purifier) {
		MixCombo mix = new MixCombo(oxidizer, reducer, purifier, input);

		ItemStack is = itemComboList.remove(mix);
		if (is != null) {
			dispatchDeleteEvent(mix, is);
			return is;
		}

		return null;
	}

	@Override
	public FluidStack removeLiquidMix(Fluid input, String oxidizer, String reducer, String purifier) {
		MixCombo mix = new MixCombo(oxidizer, reducer, purifier, input);

		FluidStack fs = fluidComboList.remove(mix);
		if (fs != null) {
			dispatchDeleteEvent(mix, fs);
		}

		return fs;
	}

	@Override
	public ItemStack getSolidMix(Fluid input, ItemStack oxidizer, ItemStack reducer, ItemStack purifier) {
		int[] oxidIDs = OreDictionary.getOreIDs(oxidizer);
		int[] reduIDs = OreDictionary.getOreIDs(reducer);
		int[] puriIDs = OreDictionary.getOreIDs(purifier);

		MixCombo combo = new MixCombo();
		combo.setFluid(input);

		for (int oxidID : oxidIDs) {
			String oxiName = OreDictionary.getOreName(oxidID);
			combo.setOxydizer(oxiName);

			for (int reduID : reduIDs) {
				String reduName = OreDictionary.getOreName(reduID);
				combo.setReducer(reduName);

				for (int puriID : puriIDs) {
					String puriName = OreDictionary.getOreName(puriID);
					combo.setPurifier(puriName);

					ItemStack resultIS = itemComboList.get(combo);
					if (resultIS != null)
						return resultIS.copy();
				}
			}
		}

		return null;
	}

	@Override
	public FluidStack getFluidMix(Fluid input, ItemStack oxidizer, ItemStack reducer, ItemStack purifier) {
		int[] oxidIDs = OreDictionary.getOreIDs(oxidizer);
		int[] reduIDs = OreDictionary.getOreIDs(reducer);
		int[] puriIDs = OreDictionary.getOreIDs(purifier);

		MixCombo combo = new MixCombo();
		combo.setFluid(input);

		for (int oxidID : oxidIDs) {
			String oxiName = OreDictionary.getOreName(oxidID);
			combo.setOxydizer(oxiName);

			for (int reduID : reduIDs) {
				String reduName = OreDictionary.getOreName(reduID);
				combo.setReducer(reduName);

				for (int puriID : puriIDs) {
					String puriName = OreDictionary.getOreName(puriID);
					combo.setPurifier(puriName);

					FluidStack resultFS = fluidComboList.get(combo);
					if (resultFS != null)
						return resultFS.copy();
				}
			}
		}

		return null;
	}

	@Override
	public Object getMix(Fluid fluid, ItemStack oxidizer, ItemStack reducer, ItemStack purifier) {
		FluidStack fluidOutput = getFluidMix(fluid, oxidizer, reducer, purifier);

		if (fluidOutput != null) return fluidOutput;

		return getSolidMix(fluid, oxidizer, reducer, purifier);
	}

	/**
	 * Wrapper for a combo
	 */
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

		private MixCombo() {}

		private void setFluid(Fluid fluid) {
			this.fluid = fluid;
		}

		private void setPurifier(String purifier) {
			this.purifier = purifier;
		}

		private void setReducer(String reducer) {
			this.reducer = reducer;
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

		@Override
		public String getPurifier() {
			return purifier;
		}

		@Override
		public Fluid getInputFluid() {
			return fluid;
		}
	}

}
