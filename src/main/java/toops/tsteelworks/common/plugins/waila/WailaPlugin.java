package toops.tsteelworks.common.plugins.waila;

import cpw.mods.fml.common.event.FMLInterModComms;
import mcp.mobius.waila.api.IWailaRegistrar;
import toops.tsteelworks.common.blocks.logic.DeepTankLogic;
import toops.tsteelworks.common.blocks.logic.HighOvenLogic;
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
		FMLInterModComms.sendMessage("Waila", "register", "WailaPlugin.wailaCallback");
	}

	@Override
	public void postInit() {}

	public static void wailaCallback(IWailaRegistrar registrar) {
		TSLogger.info("[Waila-Compat] Got registrar: " + registrar);

		// Tanks
		registrar.registerBodyProvider(new HighOvenTankDataProvider(), DeepTankLogic.class);
		registrar.registerBodyProvider(new HighOvenTankDataProvider(), HighOvenLogic.class);

		//config
		registrar.addConfig("TinkersSteelworks", "tseelworks.showTotal");
		registrar.addConfig("TinkersSteelworks", "tseelworks.autoUnit");
	}
}
