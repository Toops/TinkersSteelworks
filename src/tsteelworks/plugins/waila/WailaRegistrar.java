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
	@SuppressWarnings ("static-access")
    public static void wailaCallback (IWailaRegistrar registrar)
	{
		TSteelworks.logger.info("[Waila-Compat] Got registrar: " + registrar);

		// Tanks
		registrar.registerBodyProvider(new HighOvenTankDataProvider(), DeepTankLogic.class);
		registrar.registerBodyProvider(new HighOvenTankDataProvider(), HighOvenLogic.class);
		registrar.registerBodyProvider(new SteamTurbineDataProvider(), TurbineLogic.class);
		
		//config
		registrar.addConfig("TinkersSteelworks", "tseelworks.showTotal", "Show Total");
		registrar.addConfig("TinkersSteelworks", "tseelworks.autoUnit", "Adjust bucket units");
	}

	public static String fluidNameHelper (FluidStack f)
	{
	    return StatCollector.translateToLocal(FluidRegistry.getFluidName(f));
	}

}
