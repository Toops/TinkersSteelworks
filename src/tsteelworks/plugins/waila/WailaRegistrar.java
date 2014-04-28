package tsteelworks.plugins.waila;

import mcp.mobius.waila.api.IWailaRegistrar;
import tsteelworks.blocks.logic.DeepTankLogic;

public class WailaRegistrar
{
	public static void wailaCallback (IWailaRegistrar registrar)
	{
		//TSteelworks.logger.info("[Waila-Compat] Got registrar: " + registrar);

		// Tanks
		registrar.registerBodyProvider(new HighOvenTankDataProvider(), DeepTankLogic.class);
	}

	// needed too? Let's assume not for the moment.
	//    public static String fluidNameHelper (FluidStack f)
	//    {
	//        return StatCollector.translateToLocal(FluidRegistry.getFluidName(f));
	//    }

}
