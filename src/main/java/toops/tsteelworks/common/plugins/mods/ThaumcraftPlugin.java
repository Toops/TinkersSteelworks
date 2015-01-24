package toops.tsteelworks.common.plugins.mods;

import net.minecraft.item.ItemStack;
import nf.fr.ephys.cookiecore.helpers.DebugHelper;
import nf.fr.ephys.cookiecore.helpers.RegistryHelper;
import toops.tsteelworks.api.highoven.IFuelRegistry;
import toops.tsteelworks.common.core.TSLogger;
import toops.tsteelworks.common.plugins.ModCompatPlugin;

public class ThaumcraftPlugin extends ModCompatPlugin {
	@Override
	public String getModId() {
		return "Thaumcraft";
	}

	@Override
	public void preInit() {}

	@Override
	public void init() {
		ItemStack alumentum = RegistryHelper.getItemStack("Thaumcraft:ItemResource@0");

		if (alumentum == null) {
			if (DebugHelper.debug) throw new RuntimeException("Could not load Alumentum");

			TSLogger.warning("Could not load Alumentum");
		}

		IFuelRegistry.INSTANCE.addFuel(alumentum, 420 * 4, 4);
	}

	@Override
	public void postInit() {}
}
