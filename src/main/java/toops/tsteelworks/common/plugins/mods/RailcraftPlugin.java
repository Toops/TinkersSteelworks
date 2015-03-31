package toops.tsteelworks.common.plugins.mods;

import net.minecraft.item.ItemStack;
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
		ItemStack[] coalCoke = RegistryHelper.getItemStacks("Railcraft:fuel.coke@0");
		ItemStack[] cokeBlock = RegistryHelper.getItemStacks("Railcraft:cube@0");

		if (coalCoke == null || cokeBlock == null || coalCoke.length == 0 || cokeBlock.length == 0) {
			TSLogger.warning("Could not load Railcraft coke fuel");
		} else {
			IFuelRegistry.INSTANCE.addFuel(coalCoke[0], 280, 10);
			IFuelRegistry.INSTANCE.addFuel(cokeBlock[0], 2800, 15);
		}
	}

	@Override
	public void postInit() {}
}
