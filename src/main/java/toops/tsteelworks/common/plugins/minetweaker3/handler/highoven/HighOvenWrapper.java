package toops.tsteelworks.common.plugins.minetweaker3.handler.highoven;

import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import minetweaker.api.liquid.ILiquidStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import static toops.tsteelworks.common.plugins.minetweaker3.MinetweakerPlugin.parseItem;
import static toops.tsteelworks.common.plugins.minetweaker3.MinetweakerPlugin.parseLiquid;

@ZenClass("mods.tsteelworks.highoven")
public class HighOvenWrapper {
	// fuel
	@ZenMethod
	public static void addFuel(final IItemStack fuel, final int burnTime, final int heatValue) {
		MineTweakerAPI.apply(new FuelHandler.Add(parseItem(fuel), burnTime, heatValue));
	}

	@ZenMethod
	public static void removeFuel(final IItemStack fuel) {
		MineTweakerAPI.apply(new FuelHandler.Remove(parseItem(fuel)));
	}

	// meltables
	@ZenMethod
	public static void addMeltable(final IItemStack meltable, final boolean isOre, final ILiquidStack output, final int meltTemp) {
		MineTweakerAPI.apply(new MeltingHandler.Add(parseItem(meltable), isOre, parseLiquid(output), meltTemp));
	}

	@ZenMethod
	public static void removeMeltable(final IItemStack meltable) {
		MineTweakerAPI.apply(new MeltingHandler.Remove(parseItem(meltable)));
	}
}
