package tsteelworks.plugins.waila;

import tsteelworks.TSteelworks;
import tsteelworks.plugins.ICompatPlugin;
import cpw.mods.fml.common.event.FMLInterModComms;

//forked from the one from TConstruct
public class Waila implements ICompatPlugin
{
	/*
	 * (non-Javadoc)
	 * @see tsteelworks.plugins.ICompatPlugin#getModId()
	 */
    @Override
    public String getModId ()
    {
        return "Waila";
    }

    /*
     * (non-Javadoc)
     * @see tsteelworks.plugins.ICompatPlugin#preInit()
     */
    @Override
    public void preInit ()
    {
        // Nothing
    }

    /*
     * (non-Javadoc)
     * @see tsteelworks.plugins.ICompatPlugin#init()
     */
    @SuppressWarnings ("static-access")
    @Override
    public void init ()
    {
        TSteelworks.logger.info("Waila detected. Registering TSteelworks with Waila registry.");
        FMLInterModComms.sendMessage("Waila", "register", "tsteelworks.plugins.waila.WailaRegistrar.wailaCallback");
    }  

    /*
     * (non-Javadoc)
     * @see tsteelworks.plugins.ICompatPlugin#postInit()
     */
    @Override
    public void postInit ()
    {
        // Nothing
    }

}
