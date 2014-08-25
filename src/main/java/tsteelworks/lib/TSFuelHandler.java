package tsteelworks.lib;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import tconstruct.tools.TinkerTools;
import tsteelworks.common.core.ModsData;

public class TSFuelHandler {
	/*
	 * Alumentum lasts a while but takes ages to produce enough heat
	 * Coal Coke produces more heat than charcoal and last longer too
	 */
	public static int getHighOvenFuelBurnTime(ItemStack fuel) {
		if (fuel.isItemEqual(ModsData.Shared.charcoalBlock))
			return 4200;

		// Charcoal
		if ((fuel.getItem() == Items.coal) && fuel.getItemDamage() == 1)
			return 420;

		if (ModsData.Railcraft.isLoaded) {
			if (fuel.isItemEqual(ModsData.Railcraft.coalCoke))
				return 840;

			if (fuel.isItemEqual(ModsData.Railcraft.coalCokeBlock))
				return 8400;
		}

		if (TinkerTools.thaumcraftAvailable) {
			if (fuel.isItemEqual(ModsData.Thaumcraft.alumentum))
				return 420 * 4;
		}

		return 0;
	}

	public static int getHighOvenFuelHeatRate(ItemStack fuel) {
		if (fuel.isItemEqual(ModsData.Shared.charcoalBlock))
			return 7;

		// Charcoal
		if ((fuel.getItem() == Items.coal) && fuel.getItemDamage() == 1)
			return 4;

		if (ModsData.Railcraft.isLoaded) {
			if (fuel.isItemEqual(ModsData.Railcraft.coalCoke))
				return 6;

			if (fuel.isItemEqual(ModsData.Railcraft.coalCokeBlock))
				return 10;
		}

		if (TinkerTools.thaumcraftAvailable) {
			if (fuel.isItemEqual(ModsData.Thaumcraft.alumentum))
				return 1;
		}

		return 0;
	}
}
