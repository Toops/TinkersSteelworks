package tsteelworks.common.plugins.waila;

import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import tsteelworks.common.blocks.logic.DeepTankLogic;
import tsteelworks.common.blocks.logic.HighOvenLogic;
import tsteelworks.common.core.TSLogger;

public class WailaRegistrar {
	public static void wailaCallback(IWailaRegistrar registrar) {
		TSLogger.info("[Waila-Compat] Got registrar: " + registrar);

		// Tanks
		registrar.registerBodyProvider(new HighOvenTankDataProvider(), DeepTankLogic.class);
		registrar.registerBodyProvider(new HighOvenTankDataProvider(), HighOvenLogic.class);

		//config
		registrar.addConfig("TinkersSteelworks", "tseelworks.showTotal");
		registrar.addConfig("TinkersSteelworks", "tseelworks.autoUnit");
	}

	public static String fluidNameHelper(FluidStack f) {
		return StatCollector.translateToLocal(FluidRegistry.getFluidName(f));
	}
}
