package tsteelworks;

import java.util.logging.Logger;

import tconstruct.TConstruct;
import tconstruct.library.util.TabTools;
import tsteelworks.common.TSCommonProxy;
import tsteelworks.common.TSContent;
import tsteelworks.lib.ConfigCore;
import tsteelworks.lib.Repo;
import tsteelworks.lib.TSFuelHandler;
import tsteelworks.lib.TSteelworksRegistry;
import tsteelworks.plugins.PluginController;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Tinkers' Construct Expansion: Tinkers' Steelworks 
 * Based heavily on preestablished code by SlimeKnights (https://github.com/SlimeKnights)
 * 
 * TSteelworks
 * 
 * @author Toops
 * @license Creative Commons Attribution 3.0 Unported (http://creativecommons.org/licenses/by/3.0/)
 */
@Mod(modid = Repo.modId, name = Repo.modName, version = Repo.modVer, dependencies = Repo.modRequire)
@NetworkMod(clientSideRequired = true, serverSideRequired = false, channels = (Repo.modChan), packetHandler = tsteelworks.network.TSPacketHandler.class)
public class TSteelworks
{
    // Shared logger
    public static final Logger logger = Logger.getLogger(Repo.modId);
    // Mod Instance
    @Instance(Repo.modId)
    public static TSteelworks instance;
    // Proxy
    @SidedProxy(clientSide = Repo.modClientProxy, serverSide = Repo.modServProxy)
    public static TSCommonProxy proxy;
    // Content Creator
    public static TSContent content;
    public static TSFuelHandler fuelHandler;

    public TSteelworks()
    {
        logger.setParent(FMLCommonHandler.instance().getFMLLogger());
        TConstruct.logger.info("TSteelworks, are you pondering what I'm pondering?");
        logger.info("I think so, TConstruct, but where are we going to find a duck and a hose at this hour?");

        PluginController.getController().registerBuiltins();
    }

    @EventHandler
    public void preInit (FMLPreInitializationEvent event)
    {
        ConfigCore.initProps(event.getSuggestedConfigurationFile());
        TSteelworksRegistry.SteelworksCreativeTab = new TabTools(Repo.modId);
        content = new TSContent();
        fuelHandler = new TSFuelHandler();
        proxy.registerRenderer();
        proxy.readManuals();
        proxy.registerSounds();
        NetworkRegistry.instance().registerGuiHandler(instance, proxy);
        // TODO: Make horses like sugar cubes :|

        PluginController.getController().preInit();
    }

    @EventHandler
    public void init (FMLInitializationEvent event)
    {
        PluginController.getController().init();
    }

    @EventHandler
    public void postInit (FMLPostInitializationEvent event)
    {
        content.createEntities();
        content.addCraftingRecipes();
        GameRegistry.registerFuelHandler(fuelHandler);
        PluginController.getController().postInit();
    }

    public static void loginfo (String desc)
    {
        logger.info(desc);
    }

    public static void loginfo (String desc, int value)
    {
        logger.info(desc + ": " + value);
    }

    public static void loginfo (String desc, String text)
    {
        logger.info(desc + ": " + text);
    }
}
