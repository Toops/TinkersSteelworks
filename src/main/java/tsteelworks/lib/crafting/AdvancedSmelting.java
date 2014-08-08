package tsteelworks.lib.crafting;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import tconstruct.library.crafting.FluidType;
import tsteelworks.util.InventoryHelper;

import java.util.*;
import java.util.Map.Entry;

/**
 * @author Toops
 */
public class AdvancedSmelting {
	/** list of meltables items & blocks mapped to their result (fluidstack, melting point, etc) */
	private static final HashMap<Meltable, MeltData> meltingList = new HashMap<>();

	private static final HashMap<String, List<Integer>> mixItemList = new HashMap<>();
	@SuppressWarnings("rawtypes")
	private static final Multimap<FluidType, List> fluidComboList = ArrayListMultimap.create();
	@SuppressWarnings("rawtypes")
	private static final Multimap<ItemStack, List> itemComboList = ArrayListMultimap.create();

    /* ========== Normal Smelting  ========== */

	/**
	 * Adds mappings between a block and its liquid.
	 *
	 * @param block     The ID of the block to liquify and render
	 * @param metadata    The metadata of the block to liquify and render
	 * @param temperature How hot the block should be before liquifying. Max temp in the
	 *                    Smeltery is 800, other structures may vary
	 * @param output      The result of the process in liquid form
	 */
	public static void addMelting(Block block, int metadata, int temperature, FluidStack output) {
		addMelting(new ItemStack(block, 1, metadata), temperature, output);
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

	/**
	 * Used to get the resulting ItemStack from a source Block
	 *
	 * @param block     : source block
	 * @param metadata  : source block metadata
	 * @return The result ItemStack
	 */
	public static FluidStack getMeltingResult(Block block, int metadata) {
		return getMeltingResult(new ItemStack(block, 1, metadata));
	}

	/**
	 * Used to get the resulting ItemStack from a source Block
	 *
	 * @param item      : source item
	 * @param metadata  : source block metadata
	 * @return The result ItemStack
	 */
	public static FluidStack getMeltingResult(Item item, int metadata) {
		return getMeltingResult(new ItemStack(item, 1, metadata));
	}

	/**
	 * Used to get the resulting ItemStack from a source ItemStack
	 *
	 * @param itemstack The Source ItemStack
	 * @return The result ItemStack
	 */
	public static FluidStack getMeltingResult(ItemStack itemstack) {
		if (itemstack == null) return null;

		FluidStack stack = meltingList.get(new Meltable(itemstack)).getResult();

		if (stack == null)
			return null;

		return stack.copy();
	}

    /* ========== Temperatures ========== */

	/**
	 * Used to get the resulting temperature from a source ItemStack
	 *
	 * @param itemstack The Source ItemStack
	 * @return The result temperature
	 */
	public static int getLiquifyTemperature(ItemStack itemstack) {
		if (itemstack == null)
			return 20;

		return meltingList.get(new Meltable(itemstack)).getMeltingPoint();
	}

	/**
	 * Used to get the resulting temperature from a source Block
	 *
	 * @return The result ItemStack
	 */
	public static Integer getLiquifyTemperature(Block block, int metadata) {
		return getLiquifyTemperature(new ItemStack(block, metadata));
	}

	/**
	 * Used to get the resulting temperature from a source Item
	 *
	 * @return The result ItemStack
	 */
	public static Integer getLiquifyTemperature(Item item, int metadata) {
		return getLiquifyTemperature(new ItemStack(item, metadata));
	}

    /* ========== Combinitorial Smelting ========== */

	/**
	 * Adds a mapping between an item and its mix type and consume chance
	 * Stack size determines the amount required for consumption
	 */
	public static void registerMixItem(String oreName, int type, int consume, int chance) {
		for (ItemStack is : OreDictionary.getOres(oreName))
			mixItemList.put(InventoryHelper.getOreDictionaryName(is), Arrays.asList(type, consume, chance));
	}

	@SuppressWarnings("unchecked")
	public static void registerMixComboForFluidOutput(FluidType fluidout, FluidType fluidin, String i1, String i2, String i3) {
		fluidComboList.put(fluidout, Arrays.asList(fluidin, i1, i2, i3));
	}

	@SuppressWarnings("unchecked")
	public static void registerMixComboForSolidOutput(ItemStack stackout, FluidType fluidin, String i1, String i2, String i3) {
		itemComboList.put(stackout, Arrays.asList(fluidin, i1, i2, i3));
	}

	public static void getMixComboForFluidOutput(FluidType fluidtype) {
		fluidComboList.get(fluidtype);
	}

	public static void getMixComboForSolidOutput(ItemStack itemstack) {
		itemComboList.get(itemstack);
	}

	/**
	 * Determine if item is in mixer list
	 */
	public static Boolean isMixItemListed(ItemStack itemstack) {
		return mixItemList.containsKey(InventoryHelper.getOreDictionaryName(itemstack));
	}

	/**
	 * Gets item mixer type (0: oxidizer, 1: reducer, 2: purifier)
	 */
	public static Integer getMixItemType(ItemStack itemstack) {
		final List<Integer> list = mixItemList.get(InventoryHelper.getOreDictionaryName(itemstack));
		return list.get(0);
	}

	/**
	 * Gets mixer item consumption amount
	 */
	public static Integer getMixItemConsumeAmount(ItemStack item) {
		final List<Integer> list = mixItemList.get(InventoryHelper.getOreDictionaryName(item));
		return list.get(1);
	}

	/**
	 * Gets mixer item consumption chance
	 */
	public static Integer getMixItemConsumeChance(ItemStack itemstack) {
		final List<Integer> list = mixItemList.get(InventoryHelper.getOreDictionaryName(itemstack));
		return list.get(2);
	}

	/**
	 * Determines whether a given itemstack meets or exceeds the required amount
	 * Returns true if itemstack is null, because some recipes do not require an
	 * item in a slot.
	 *
	 * @param itemstack : ItemStack to check against
	 */
	public static Boolean doesMixItemMeetRequirements(ItemStack itemstack) {
		if (itemstack == null)
			return true;

		if (!isMixItemListed(itemstack))
			return false;

		return (itemstack.stackSize >= getMixItemConsumeAmount(itemstack));
	}

	/**
	 * Obtains items passed from slots, compares with the fluid combo list,
	 * and if matching returns the fluid type from the fluid combo list.
	 *
	 * @param i1 Oxidizer
	 * @param i2 Reducer
	 * @param i3 Purifier
	 * @return FluidType from fluidComboList on success, null otherwise
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static FluidType getMixFluidSmeltingResult(FluidType f1, ItemStack i1, ItemStack i2, ItemStack i3) {
		String ox = InventoryHelper.getOreDictionaryName(i1);
		String re = InventoryHelper.getOreDictionaryName(i2);
		String pu = InventoryHelper.getOreDictionaryName(i3);
		final Collection<String> inputs = new ArrayList(Arrays.asList(f1, ox, re, pu));
		for (final Entry<FluidType, List> e : fluidComboList.entries()) {
			final FluidType key = e.getKey();
			final List value = e.getValue();
			if (value.equals(inputs))
				if (doesMixItemMeetRequirements(i1) && doesMixItemMeetRequirements(i2) && doesMixItemMeetRequirements(i3))
					return key;
		}
		return null;
	}

	/**
	 * Obtains items passed from slots, compares with the item combo list,
	 * and if matching returns the fluid type from the item combo list.
	 *
	 * @param i1 Oxidizer
	 * @param i2 Reducer
	 * @param i3 Purifier
	 * @return ItemStack from itemComboList on success, null otherwise
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static ItemStack getMixItemSmeltingResult(FluidType f1, ItemStack i1, ItemStack i2, ItemStack i3) {
		String ox = InventoryHelper.getOreDictionaryName(i1);
		String re = InventoryHelper.getOreDictionaryName(i2);
		String pu = InventoryHelper.getOreDictionaryName(i3);
		final Collection<String> inputs = new ArrayList(Arrays.asList(f1, ox, re, pu));
		for (final Entry<ItemStack, List> e : itemComboList.entries()) {
			final ItemStack key = e.getKey();
			final List value = e.getValue();
			if (value.equals(inputs))
				if (doesMixItemMeetRequirements(i1) && doesMixItemMeetRequirements(i2) && doesMixItemMeetRequirements(i3))
					return key.copy();
		}
		return null;
	}

    /* ========== Get Lists ========== */

	public static HashMap<Meltable, MeltData> getMeltingList() {
		return meltingList;
	}

	public static HashMap<String, List<Integer>> getMixItemsList() {
		return mixItemList;
	}

	@SuppressWarnings("rawtypes")
	public static Multimap<FluidType, List> getFluidCombosList() {
		return fluidComboList;
	}

	@SuppressWarnings("rawtypes")
	public static Multimap<ItemStack, List> getItemCombosList() {
		return itemComboList;
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
}