package tsteelworks.lib.crafting;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import tconstruct.library.crafting.FluidType;

import java.util.HashMap;

/**
 * @author Toops
 */
public class AdvancedSmelting {
	/** list of meltables items & blocks mapped to their result (fluidstack, melting point, etc) */
	private static final HashMap<Meltable, MeltData> meltingList = new HashMap<>();

	/** list of mix information, oredict itemstack to mix info (mix type, consume amount & chance) */
	private static final HashMap<String, MixData> mixItemList = new HashMap<>();

	private static final HashMap<MixCombo, FluidStack> fluidComboList = new HashMap<>();
	private static final HashMap<MixCombo, ItemStack> itemComboList = new HashMap<>();

    /* ========== Normal Smelting  ========== */

	/**
	 * Adds mappings between a block and its liquid.
	 *
	 * @param block         The instance of the block to liquify
	 * @param metadata      The metadata of the block to liquify
	 * @param temperature   How hot the block should be before liquifying. Max temp in the
	 *                      Smeltery is 800, other structures may vary
	 * @param output        The result of the process in liquid form
	 */
	public static void addMelting(Block block, int metadata, int temperature, FluidStack output) {
		addMelting(new ItemStack(block, 1, metadata), temperature, output);
	}

	/**
	 * Adds mappings between an item and its liquid.
	 *
	 * @param item          The instance of the item to liquify and render
	 * @param metadata      The metadata of the block to liquify and render
	 * @param temperature   How hot the block should be before liquifying. Max temp in the
	 *                      Smeltery is 800, other structures may vary
	 * @param output        The result of the process in liquid form
	 */
	public static void addMelting(Item item, int metadata, int temperature, FluidStack output) {
		addMelting(new ItemStack(item, 1, metadata), temperature, output);
	}

	/**
	 * Adds all Items to the Smeltery based on the oreDictionary Name
	 *
	 * @param oreName     oreDictionary name e.g. oreIron
	 * @param type        Type of Fluid
	 * @param tempDiff    Difference between FluidType BaseTemperature
	 * @param fluidAmount Amount of Fluid
	 */
	public static void addDictionaryMelting(String oreName, FluidType type, int tempDiff, int fluidAmount) {
		for (final ItemStack is : OreDictionary.getOres(oreName))
			addMelting(is, tempDiff, type, fluidAmount);
	}

	/**
	 * Adds a mapping between FluidType and ItemStack
	 *
	 * @param type        Type of Fluid
	 * @param input       The item to liquify
	 * @param tempDiff    Difference between FluidType BaseTemperature and the melting temperature
	 * @param fluidAmount Amount of Fluid
	 */
	public static void addMelting(ItemStack input, int tempDiff, FluidType type, int fluidAmount) {
		int temp = type.baseTemperature + tempDiff;

		if (temp <= 20)
			temp = 20;

		addMelting(input, temp, new FluidStack(type.fluid, fluidAmount));
	}

	/**
	 * Adds mappings between an input and its liquid. Renders with the given
	 * input's block ID and metadata.
	 *
	 * @param itemstack   : The item to liquify
	 * @param temperature : How hot the block should be before liquifying
	 * @param liquid      : The result of the process
	 */
	public static void addMelting(ItemStack itemstack, int temperature, FluidStack liquid) {
		meltingList.put(new Meltable(itemstack), new MeltData(temperature, liquid));
	}

	public static MeltData getMeltData(ItemStack stack) {
		return meltingList.get(new Meltable(stack));
	}

    /* ========== Combinitorial Smelting ========== */

	/**
	 * Adds a mapping between an item and its mix type and consume chance
	 * Stack size determines the amount required for consumption
	 */
	public static void registerMixItem(String oreName, MixData.MixType type, int consume, int chance) {
		mixItemList.put(oreName, new MixData(type, consume, chance));
	}

	public static MixData getMixItemData(ItemStack itemStack) {
		int ids[] = OreDictionary.getOreIDs(itemStack);

		for (int id : ids) {
			String name = OreDictionary.getOreName(id);
			if (mixItemList.containsKey(name))
				return mixItemList.get(name);
		}

		return null;
	}

	public static void registerMixComboForFluidOutput(FluidStack fluidout, Fluid fluidin, String ox, String red, String pur) {
		fluidComboList.put(new MixCombo(ox, red, pur, fluidin), fluidout);
	}

	public static void registerMixComboForSolidOutput(ItemStack stackout, Fluid fluidin, String i1, String i2, String i3) {
		itemComboList.put(new MixCombo(i1, i2, i3, fluidin), stackout);
	}

	/**
	 * Obtains items passed from slots, compares with the fluid combo list,
	 * and if matching returns the fluid type from the fluid combo list.
	 *
	 * @return FluiStack from fluidComboList on success, null otherwise
	 */
	public static FluidStack getMixFluidSmeltingResult(Fluid fluid, ItemStack oxidizer, ItemStack reducer, ItemStack purifier) {
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

					FluidStack result = fluidComboList.get(combo);

					if (result != null)
						return result.copy();
				}
			}
		}

		return null;
	}

	/**
	 * Obtains items passed from slots, compares with the item combo list,
	 * and if matching returns the fluid type from the item combo list.
	 *
	 * @return ItemStack from itemComboList on success, null otherwise
	 */
	public static ItemStack getMixItemSmeltingResult(Fluid fluid, ItemStack oxidizer, ItemStack reducer, ItemStack purifier) {
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

					ItemStack result = itemComboList.get(combo);

					if (result != null)
						return result.copy();
				}
			}
		}

		return null;
	}

	/**
	 * Only here to implement equals used by the HashMap.
	 * Thanks Mojang btw, implementing ItemStack#areItemsEquals but not ItemStack#equals. >_>
	 */
	public static class Meltable {
		private ItemStack itemStack;

		public Meltable(ItemStack itemStack) {
			this.itemStack = itemStack;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof Meltable && ItemStack.areItemStacksEqual(((Meltable) obj).itemStack, itemStack);
		}
	}

	/**
	 * Hold information (meltpoint & result) about something meltable
	 */
	public static class MeltData {
		private int meltingPoint;
		private FluidStack result;

		public MeltData(int meltingPoint, FluidStack result) {
			this.meltingPoint = meltingPoint;
			this.result = result;
		}

		public int getMeltingPoint() {
			return meltingPoint;
		}

		public FluidStack getResult() {
			return result;
		}
	}

	// ======================= mixs =========================

	/**
	 * Wrapper for a combo
	 */
	public static class MixCombo {
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

		public MixCombo() {}

		public void setFluidname(String fluidname) {
			this.fluidname = fluidname;
		}

		public void setPurifier(String purifier) {
			this.purifier = purifier;
		}

		public void setReducer(String reducer) {
			this.reducer = reducer;
		}

		public void setOxydizer(String oxydizer) {
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

	/**
	 * Hold information (mixer type, consume amount & consume chance) for a mix
	 */
	public static class MixData {
		public static enum MixType {
			OXYDIZER, REDUCER, PURIFIER
		}

		private MixType type;
		private int consumeAmount;
		private int consumeChance;

		public MixData(MixType type, int consumeAmount, int consumeChance) {
			this.type = type;
			this.consumeAmount = consumeAmount;
			this.consumeChance = consumeChance;
		}

		public MixType getType() {
			return type;
		}

		public int getConsumeAmount() {
			return consumeAmount;
		}

		public int getConsumeChance() {
			return consumeChance;
		}
	}
}