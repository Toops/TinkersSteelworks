package toops.tsteelworks.common.plugins.waila;

import cpw.mods.fml.common.event.FMLInterModComms;
import toops.tsteelworks.common.core.TSLogger;
import toops.tsteelworks.common.plugins.ModCompatPlugin;

public class WailaPlugin extends ModCompatPlugin {
	@Override
	public String getModId() {
		return "Waila";
	}

	@Override
	public void preInit() {}

	@Override
	public void init() {
		TSLogger.info("Waila detected. Registering TSteelworks with Waila registry.");
		FMLInterModComms.sendMessage("Waila", "register", "WailaRegistrar.wailaCallback");
	}

	@Override
	public void postInit() {}
}
