package tsteelworks.plugins.waila;

import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import tsteelworks.TSteelworks;
import tsteelworks.blocks.logic.DeepTankLogic;
import tsteelworks.blocks.logic.HighOvenLogic;
import tsteelworks.blocks.logic.TurbineLogic;

public class WailaRegistrar
{
	public static void wailaCallback (IWailaRegistrar registrar)
	{
		TSteelworks.logger.info("[Waila-Compat] Got registrar: " + registrar);

		// Tanks
		registrar.registerBodyProvider(new HighOvenTankDataProvider(), DeepTankLogic.class);
		registrar.registerBodyProvider(new HighOvenTankDataProvider(), HighOvenLogic.class);
		registrar.registerBodyProvider(new SteamTurbineDataProvider(), TurbineLogic.class);
	}

	// needed too? Let's assume not for the moment.
	    public static String fluidNameHelper (FluidStack f)
	    {
	        return StatCollector.translateToLocal(FluidRegistry.getFluidName(f));
	    }

}
