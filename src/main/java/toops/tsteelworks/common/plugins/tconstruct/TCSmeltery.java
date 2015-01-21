package toops.tsteelworks.common.plugins.tconstruct;

import mantle.utils.ItemMetaWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import nf.fr.ephys.cookiecore.helpers.InventoryHelper;
import tconstruct.TConstruct;
import tconstruct.library.crafting.FluidType;
import tconstruct.library.crafting.Smeltery;
import toops.tsteelworks.api.highoven.ISmeltingRegistry;
import toops.tsteelworks.common.core.ConfigCore;
import toops.tsteelworks.common.core.TSRecipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TCSmeltery {
	private static List<Alloy> alloys = new ArrayList<>();

	public static void preInit() {
		adjustFluidValues();
	}

	private static void adjustFluidValues() {
		TSRecipes.INGOT_LIQUID_VALUE = TConstruct.ingotLiquidValue;
		TSRecipes.NUGGET_LIQUID_VALUE = TConstruct.nuggetLiquidValue;
		TSRecipes.BLOCK_LIQUID_VALUE = TConstruct.blockLiquidValue;
		TSRecipes.ORE_LIQUID_VALUE = (int) Math.round(TSRecipes.INGOT_LIQUID_VALUE * ConfigCore.ingotsPerOre);
	}

	public static void init() {
		copySmeltingList();
		registerAlloysDiffer();
	}

	private static void copySmeltingList() {
		Map<ItemMetaWrapper, FluidStack> smeltingList = Smeltery.getSmeltingList();
		Map<ItemMetaWrapper, Integer> temperatureList = Smeltery.getTemperatureList();

		ISmeltingRegistry localRegistry = ISmeltingRegistry.INSTANCE;

		for (Map.Entry<ItemMetaWrapper, FluidStack> set : smeltingList.entrySet()) {
			ItemStack key = new ItemStack(set.getKey().item, 1, set.getKey().meta);

			boolean isOre = InventoryHelper.itemIsOre(key);

			localRegistry.addMeltable(key, isOre, set.getValue().copy(), temperatureList.get(set.getKey()));
		}
	}

	public static void registerAlloy(FluidStack input1, FluidStack input2, FluidStack output) {
		alloys.add(new Alloy(input1, input2, output));
	}

	private static void registerAlloysDiffer() {
		for (Alloy alloy : alloys) {
			Smeltery.addAlloyMixing(alloy.output, alloy.f1, alloy.f2);
		}
	}

	public static void addDictionaryMeltable(String ore, String tcFluidName, int temp, int amount) {
		Smeltery.addDictionaryMelting(ore, FluidType.getFluidType(tcFluidName), temp, amount);
	}

	public static void addMeltable(ItemStack is, String tcFluidName, int temp, int amount) {
		Smeltery.addMelting(FluidType.getFluidType(tcFluidName), is, temp, amount);
	}

	private static class Alloy {
		private FluidStack f1;
		private FluidStack f2;
		private FluidStack output;

		public Alloy(FluidStack f1, FluidStack f2, FluidStack f3) {
			this.f1 = f1;
			this.f2 = f2;
			this.output = f3;
		}
	}

	public static Fluid getFluidForType(String fluidType) {
		FluidType type = FluidType.getFluidType(fluidType);

		if (type == null) return null;

		return FluidType.getFluidType(fluidType).fluid;
	}
}
