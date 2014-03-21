package tsteelworks.plugins;

import java.util.ArrayList;
import java.util.List;

import tsteelworks.TSteelworks;
import tsteelworks.plugins.fmp.ForgeMultiPart;
import cpw.mods.fml.common.Loader;

public class PluginController
{

    private enum Phase
    {
        PRELAUNCH, PREINIT, INIT, POSTINIT, DONE
    }

    private static PluginController instance;

    public static PluginController getController ()
    {
        if (instance == null)
            instance = new PluginController();
        return instance;
    }

    private final List<ICompatPlugin> plugins = new ArrayList<ICompatPlugin>();

    private Phase currPhase = Phase.PRELAUNCH;

    private PluginController()
    {
    }

    public void init ()
    {
        currPhase = Phase.INIT;
        for (final ICompatPlugin plugin : plugins)
            plugin.init();
    }

    public void postInit ()
    {
        currPhase = Phase.POSTINIT;
        for (final ICompatPlugin plugin : plugins)
            plugin.postInit();
        currPhase = Phase.DONE;
    }

    public void preInit ()
    {
        currPhase = Phase.PREINIT;
        for (final ICompatPlugin plugin : plugins)
            plugin.preInit();
    }

    public void registerBuiltins ()
    {
        registerPlugin(new ForgeMultiPart());
    }

    public void registerPlugin (ICompatPlugin plugin)
    {
        if (Loader.isModLoaded(plugin.getModId()))
        {
            TSteelworks.logger.info("Registering compat plugin for " + plugin.getModId());
            plugins.add(plugin);

            switch (currPhase)
            // Play catch-up if plugin is registered late
            {
            case DONE:
            case POSTINIT:
                plugin.preInit();
                plugin.init();
                plugin.postInit();
                break;
            case INIT:
                plugin.preInit();
                plugin.init();
                break;
            case PREINIT:
                plugin.preInit();
                break;
            default:
                break;
            }
        }
    }
}
