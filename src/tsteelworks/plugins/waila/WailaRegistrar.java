package tsteelworks.plugins.waila;

import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import tsteelworks.TSteelworks;


public class WailaRegistrar
{
    public static void wailaCallback (IWailaRegistrar registrar)
    {
        TSteelworks.logger.info("[Waila-Compat] Got registrar: " + registrar);

        // Tanks
        //registrar.registerBodyProvider(new SearedTankDataProvider(), LavaTankBlock.class);
        
        
    }

    // needed too?
    public static String fluidNameHelper (FluidStack f)
    {
        return StatCollector.translateToLocal(FluidRegistry.getFluidName(f));
    }

}
