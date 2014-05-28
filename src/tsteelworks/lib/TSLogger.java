package tsteelworks.lib;

import java.util.ArrayList;
import java.util.logging.Logger;

import net.minecraftforge.fluids.FluidStack;
import tconstruct.TConstruct;
import tsteelworks.TSteelworks;
import tsteelworks.lib.crafting.AlloyInfo;
import cpw.mods.fml.common.FMLCommonHandler;

public class TSLogger
{
    public static final Logger logger = Logger.getLogger(Repo.modId);
    public boolean debugMode;

    public TSLogger (boolean debug)
    {
        debugMode = debug;
        logger.setParent(FMLCommonHandler.instance().getFMLLogger());
    }
    
    public void introMessage ()
    {
        TConstruct.logger.info("TSteelworks, are you pondering what I'm pondering?");
        logger.info("I think so, TConstruct, but where are we going to find a duck and a hose at this hour?");
    }
    
    public static void info (String desc)
    {
        if (!TSteelworks.DEBUG_MODE) return;
        logger.info(desc);
    }

    public static void info (String desc, int value)
    {
        if (!TSteelworks.DEBUG_MODE) return;
        logger.info(desc + ": " + value);
    }

    public static void info (String desc, float value)
    {
        if (!TSteelworks.DEBUG_MODE) return;
        logger.info(desc + ": " + value);
    }

    public static void info (String desc, String text)
    {
        if (!TSteelworks.DEBUG_MODE) return;
        logger.info(desc + ": " + text);
    }

    public static void info (String desc, boolean flag)
    {
        if (!TSteelworks.DEBUG_MODE) return;
        logger.info(desc + ": " + flag);
    }
    
    public static void warning (String desc)
    {
        logger.warning(desc);
    }
    
    void logAlloyList ()
    {
        if (!TSteelworks.DEBUG_MODE) return;
        for (int i = 0; i < AlloyInfo.alloys.size(); ++i)
        {
            FluidStack f = AlloyInfo.alloys.get(i).result.copy();
            f.amount = 1000;
            ArrayList<FluidStack> result = AlloyInfo.deAlloy(f);

            System.out.println("Alloy " + AlloyInfo.alloys.get(i).result.getFluid().getName() + " produces:");
            for (int j = 0; j < result.size(); ++j)
            {
                System.out.println(result.get(j).amount + " mB of " + result.get(j).getFluid().getName());
            }
        }
    }
}
