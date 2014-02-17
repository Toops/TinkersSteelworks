package tsteelworks;

import java.util.logging.Logger;

import tconstruct.TConstruct;
import tconstruct.library.util.TabTools;
import tsteelworks.common.TSCommonProxy;
import tsteelworks.common.TSContent;
import tsteelworks.lib.ConfigCore;
import tsteelworks.lib.Repo;
import tsteelworks.lib.TSteelworksRegistry;
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

/**
 * Tinkers' Construct Expansion: Tinkers' Steelworks
 * Based heavily on preestablished code by SlimeKnights (https://github.com/SlimeKnights)
 * 
 * TSteelworks
 * 
 * @author Toops
 * @license Creative Commons Attribution 3.0 Unported (http://creativecommons.org/licenses/by/3.0/)
 */
@Mod (modid = Repo.modId, name = Repo.modName, version = Repo.modVer, dependencies = Repo.modRequire)
@NetworkMod (clientSideRequired = true, serverSideRequired = false, channels = (Repo.modChan),
             packetHandler = tsteelworks.network.TSPacketHandler.class)
public class TSteelworks
{
    // Shared logger
    public static final Logger  logger = Logger.getLogger(Repo.modId);
    // Mod Instance
    @Instance (Repo.modId)
    public static TSteelworks   instance;
    // Proxy
    @SidedProxy (clientSide = Repo.modClientProxy, serverSide = Repo.modServProxy)
    public static TSCommonProxy proxy;

    /**
     * This is fired off as soon as the mod is loaded. Mainly useful for logging
     * and mod compatability checks.
     */
    public TSteelworks ()
    {
        logger.setParent(FMLCommonHandler.instance().getFMLLogger());
        TConstruct.logger.info("TSteelworks, are you pondering what I'm pondering?");
        logger.info("I think so, TConstruct, but where are we going to find a duck and a hose at this hour?");
    }

    /**
     * This is code that is executed prior to the mod being initialized into of
     * Minecraft
     * 
     * @param event
     *            The Forge ModLoader pre-initialization event
     */
    @EventHandler
    public void preInit (FMLPreInitializationEvent event)
    {
        ConfigCore.initProps(event.getSuggestedConfigurationFile());
        TSteelworksRegistry.SteelworksCreativeTab = new TabTools(Repo.modId);
        content = new TSContent();
        proxy.registerRenderers();
        proxy.registerSounds();
        NetworkRegistry.instance().registerGuiHandler(instance, proxy);
    }

    /**
     * This is code that is executed when the mod is being initialized in
     * Minecraft
     * 
     * @param event
     *            The Forge ModLoader initialization event
     */
    @EventHandler
    public void init (FMLInitializationEvent event)
    {}

    /**
     * This is code that is executed after all mods are initialized in Minecraft
     * This is a good place to execute code that interacts with other mods.
     * 
     * @param event
     *            The Forge ModLoader post-initialization event
     */
    @EventHandler
    public void postInit (FMLPostInitializationEvent event)
    {
    }

    public static TSContent      content;
}
