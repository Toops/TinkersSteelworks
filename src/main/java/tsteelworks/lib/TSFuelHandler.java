package tsteelworks.lib;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import tconstruct.tools.TinkerTools;
import tsteelworks.common.core.TSContent;

public class TSFuelHandler {
	/*
	 * Alumentum lasts a while but takes ages to produce enough heat
	 * Coal Coke produces more heat than charcoal and last longer too
	 */
	public int getHighOvenFuelBurnTime(ItemStack fuel) {
		if (fuel.isItemEqual(TSContent.charcoalBlock))
			return 4200;

		// Charcoal
		if ((fuel.getItem() == Items.coal) && fuel.getItemDamage() == 1)
			return 420;

		int[] oreIds = OreDictionary.getOreIDs(fuel);

		for (int oreId : oreIds) {
			String oreName = OreDictionary.getOreName(oreId);

			if (oreName.equals("fuelCoke"))
				return 840;
			else if (oreName.equals("blockCoke"))
				return 8400;
		}

		if (TinkerTools.thaumcraftAvailable) {
			if (fuel.isItemEqual(TSContent.thaumcraftAlumentum))
				return 420 * 4;
		}

		return 0;
	}

	public static int getHighOvenFuelHeatRate(ItemStack fuel) {
		if (fuel.isItemEqual(TSContent.charcoalBlock))
			return 7;

		// Charcoal
		if ((fuel.getItem() == Items.coal) && fuel.getItemDamage() == 1)
			return 4;

		int[] oreIds = OreDictionary.getOreIDs(fuel);

		for (int oreId : oreIds) {
			String oreName = OreDictionary.getOreName(oreId);

			if (oreName.equals("fuelCoke"))
				return 6;
			else if (oreName.equals("blockCoke"))
				return 10;
		}

		if (TinkerTools.thaumcraftAvailable) {
			if (fuel.isItemEqual(TSContent.thaumcraftAlumentum))
				return 1;
		}

		return 0;
	}
}
