package tsteelworks.plugins.nei;

import tsteelworks.TSteelworks;
import tsteelworks.plugins.ICompatPlugin;

public class NotEnoughItems implements ICompatPlugin
{
    /*
	   * (non-Javadoc)
	   * @see tsteelworks.plugins.ICompatPlugin#getModId()
	   */
    @Override
    public String getModId ()
    {
        return "NotEnoughItems";
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
        TSteelworks.logger.info("NotEnoughItems detected. Registering TSteelworks NEI plugin.");
        NEICompat.registerNEICompat();
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
