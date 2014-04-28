package tsteelworks.plugins.waila;

import cpw.mods.fml.common.event.FMLInterModComms;
import tsteelworks.TSteelworks;
import tsteelworks.plugins.ICompatPlugin;

public class Waila implements ICompatPlugin
{
    @Override
    public String getModId ()
    {
        return "Waila";
    }

    @Override
    public void preInit ()
    {
        // Nothing
    }

    @Override
    public void init ()
    {
        TSteelworks.logger.info("Waila detected. Registering TSteelwork tank blocks with Waila registry.");

        FMLInterModComms.sendMessage("Waila", "register", "tsteelworks.plugins.waila.WailaRegistrar.wailaCallback");
    }

    @Override
    public void postInit ()
    {
        // Nothing
    }

}
