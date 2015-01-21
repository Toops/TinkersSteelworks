package toops.tsteelworks.lib.registry;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import nf.fr.ephys.cookiecore.util.HashedItemStack;
import toops.tsteelworks.api.highoven.IMixAgentRegistry;
import toops.tsteelworks.api.highoven.IMixerRegistry;
import toops.tsteelworks.api.highoven.ISmeltingRegistry;

import java.util.HashMap;
import java.util.Map;

class AdvancedSmelting implements ISmeltingRegistry, IMixerRegistry, IMixAgentRegistry {
/* ========== ISmeltingRegistry  ========== */
	/** list of meltables items & blocks mapped to their result (fluidstack, melting point, etc) */
	private final Map<HashedItemStack, IMeltData> meltingList = new HashMap<>();

	@Override
	public void addDictionaryMeltable(final String inputOre, final FluidStack output, final int meltTemperature) {
		final boolean isOre = inputOre.startsWith("ore");

		for (final ItemStack stack : OreDictionary.getOres(inputOre))
			addMeltable(stack, isOre, meltTemperature, output);
	}

	@Override
	public void addMeltable(ItemStack input, boolean isOre, int meltTemperature, FluidStack output) {
		if (meltTemperature <= 20)
			meltTemperature = 20;

		meltingList.put(new HashedItemStack(input), new MeltData(meltTemperature, output, isOre));
	}

	@Override
	public IMeltData getMeltable(ItemStack stack) {
		return meltingList.get(new HashedItemStack(stack));
	}

	@Override
	public IMeltData removeMeltable(ItemStack stack) {
		return meltingList.remove(new HashedItemStack(stack));
	}

	private static final class MeltData implements IMeltData {
		private final int meltingPoint;
		private final FluidStack result;
		private final boolean isOre;

		public MeltData(int meltingPoint, FluidStack result, boolean isOre) {
			this.meltingPoint = meltingPoint;
			this.result = result;
			this.isOre = isOre;
		}

		@Override
		public int getMeltingPoint() {
			return meltingPoint;
		}

		@Override
		public FluidStack getResult() {
			return result;
		}

		@Override
		public boolean isOre() {
			return isOre;
		}
	}

/* ========== IMixerRegistry ========== */
	private final Map<MixCombo, FluidStack> fluidComboList = new HashMap<>();
	private final Map<MixCombo, ItemStack> itemComboList = new HashMap<>();

	@Override
	public boolean registerMix(FluidStack fluidout, Fluid fluidin, String ox, String red, String pur) {
		MixCombo mix = new MixCombo(ox, red, pur, fluidin);

		if (fluidComboList.containsKey(mix) || itemComboList.containsKey(mix))
			return false;

		fluidComboList.put(mix, fluidout);

		return true;
	}

	@Override
	public boolean registerMix(ItemStack stackout, Fluid fluidin, String ox, String red, String pur) {
		MixCombo mix = new MixCombo(ox, red, pur, fluidin);

		if (fluidComboList.containsKey(mix) || itemComboList.containsKey(mix))
			return false;

		itemComboList.put(mix, stackout);

		return true;
	}

	@Override
	public Object removeMix(Fluid input, String oxidizer, String reducer, String purifier) {
		MixCombo mix = new MixCombo(oxidizer, reducer, purifier, input);

		FluidStack fs = fluidComboList.remove(mix);
		if (fs != null) return fs;

		return itemComboList.remove(mix);
	}

	@Override
	public Object getMix(Fluid fluid, ItemStack oxidizer, ItemStack reducer, ItemStack purifier) {
		int[] oxidIDs = OreDictionary.getOreIDs(oxidizer);
		int[] reduIDs = OreDictionary.getOreIDs(reducer);
		int[] puriIDs = OreDictionary.getOreIDs(purifier);

		MixCombo combo = new MixCombo();
		combo.setFluidname(fluid.getName());

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

					ItemStack resultIS = itemComboList.get(combo);
					if (resultIS != null)
						return resultIS.copy();
				}
			}
		}

		return null;
	}

	/**
	 * Wrapper for a combo
	 */
	private static class MixCombo {
		private String oxydizer;
		private String reducer;
		private String purifier;
		private String fluidname;

		public MixCombo(String oxydizer, String reducer, String purifier, Fluid fluid) {
			this.oxydizer = oxydizer;
			this.reducer = reducer;
			this.purifier = purifier;
			this.fluidname = fluid.getName();
		}

		private MixCombo() {}

		private void setFluidname(String fluidname) {
			this.fluidname = fluidname;
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

			if (!fluidname.equals(mixCombo.fluidname))
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
			result = 31 * result + fluidname.hashCode();
			return result;
		}
	}

/* ========== IMixAgentRegistry ========== */
	/** list of mix information, oredict itemstack to mix info (mix type, consume amount & chance) */
	private final Map<String, MixAgent> mixItemList = new HashMap<>();

	@Override
	public void registerAgent(String oreName, IMixAgentRegistry.AgentType type, int consume, int chance) {
		mixItemList.put(oreName, new MixAgent(type, consume, chance));
	}

	@Override
	public IMixAgent getAgentData(ItemStack itemStack) {
		int ids[] = OreDictionary.getOreIDs(itemStack);

		for (int id : ids) {
			String name = OreDictionary.getOreName(id);
			if (mixItemList.containsKey(name))
				return mixItemList.get(name);
		}

		return null;
	}

	@Override
	public IMixAgent unregisterAgent(String oreName) {
		return mixItemList.remove(oreName);
	}

	private static class MixAgent implements IMixAgentRegistry.IMixAgent {
		private final IMixAgentRegistry.AgentType type;
		private final int consumeAmount;
		private final int consumeChance;

		public MixAgent(IMixAgentRegistry.AgentType type, int consumeAmount, int consumeChance) {
			this.type = type;
			this.consumeAmount = consumeAmount;
			this.consumeChance = consumeChance;
		}

		@Override
		public IMixAgentRegistry.AgentType getType() {
			return type;
		}

		@Override
		public int getConsumeAmount() {
			return consumeAmount;
		}

		@Override
		public int getConsumeChance() {
			return consumeChance;
		}
	}
}