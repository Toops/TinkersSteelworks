package tsteelworks.plugins.waila;

import cpw.mods.fml.common.event.FMLInterModComms;
import tsteelworks.lib.TSLogger;
import tsteelworks.plugins.ICompatPlugin;

public class Waila implements ICompatPlugin {
	@Override
	public String getModId() {
		return "Waila";
	}

	@Override
	public void preInit() {}

	@Override
	public void init() {
		TSLogger.info("Waila detected. Registering TSteelworks with Waila registry.");
		FMLInterModComms.sendMessage("Waila", "register", "tsteelworks.plugins.waila.WailaRegistrar.wailaCallback");
	}

	@Override
	public void postInit() {}
}
