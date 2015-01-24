package toops.tsteelworks.common.plugins.mods;

import net.minecraft.item.ItemStack;
import nf.fr.ephys.cookiecore.helpers.DebugHelper;
import nf.fr.ephys.cookiecore.helpers.RegistryHelper;
import toops.tsteelworks.api.highoven.IFuelRegistry;
import toops.tsteelworks.common.core.TSLogger;
import toops.tsteelworks.common.plugins.ModCompatPlugin;

public class RailcraftPlugin extends ModCompatPlugin {
	@Override
	public String getModId() {
		return "Railcraft";
	}

	@Override
	public void preInit() {}

	@Override
	public void init() {
		ItemStack coalCoke = RegistryHelper.getItemStack("Railcraft:fuel.coke@0");
		ItemStack cokeBlock = RegistryHelper.getItemStack("Railcraft:tile.railcraft.cube@0");

		if (coalCoke == null || cokeBlock == null) {
			if (DebugHelper.debug) throw new RuntimeException("Could not load Railcraft coke fuel");

			TSLogger.warning("Could not load Railcraft coke fuel");
		}

		IFuelRegistry.INSTANCE.addFuel(coalCoke, 840, 10);
		IFuelRegistry.INSTANCE.addFuel(cokeBlock, 8400, 15);
	}

	@Override
	public void postInit() {}
}
