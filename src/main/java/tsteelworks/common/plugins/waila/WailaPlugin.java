package tsteelworks.common.plugins.waila;

import cpw.mods.fml.common.event.FMLInterModComms;
import tsteelworks.common.core.TSLogger;
import tsteelworks.common.plugins.ModCompatPlugin;

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
		FMLInterModComms.sendMessage("Waila", "register", "tsteelworks.common.plugins.waila.WailaRegistrar.wailaCallback");
	}

	@Override
	public void postInit() {}
}
