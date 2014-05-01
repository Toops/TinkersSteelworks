package tsteelworks;

import java.util.ArrayList;
import java.util.logging.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.TConstruct;
import tconstruct.library.util.TabTools;
import tsteelworks.common.TSCommonProxy;
import tsteelworks.common.TSContent;
import tsteelworks.lib.ConfigCore;
import tsteelworks.lib.Repo;
import tsteelworks.lib.TSFuelHandler;
import tsteelworks.lib.TSteelworksRegistry;
import tsteelworks.lib.crafting.AlloyInfo;
import tsteelworks.plugins.PluginController;
import tsteelworks.util.TSEventHandler;
import tsteelworks.worldgen.TSBaseWorldGenerator;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
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
    @Instance(Repo.modId)
    public static TSteelworks instance;
    @SidedProxy(clientSide = Repo.modClientProxy, serverSide = Repo.modServProxy)
    public static TSCommonProxy proxy;
    
    public static TSContent content;
    public static TSEventHandler events;
    public static TSFuelHandler fuelHandler;
    public static boolean thermalExpansionAvailable;
    public static boolean railcraftAvailable;

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
        
        events = new TSEventHandler();
        MinecraftForge.EVENT_BUS.register(events);
        
        fuelHandler = new TSFuelHandler();
        
        proxy.registerRenderer();
        proxy.readManuals();
        proxy.registerSounds();
        
        GameRegistry.registerWorldGenerator(new TSBaseWorldGenerator());
        
        NetworkRegistry.instance().registerGuiHandler(instance, proxy);

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
        thermalExpansionAvailable = Loader.isModLoaded("ThermalExpansion");
        railcraftAvailable = Loader.isModLoaded("Railcraft");
        
        content.createEntities();
        content.addCraftingRecipes();
        content.modIntegration();
        content.oreRegistry();
        content.registerMixerMaterials();
        
        GameRegistry.registerFuelHandler(fuelHandler);
        PluginController.getController().postInit();

        //Initialize dealloying information at the last possible minute, to ensure that other mods have a chance to get their alloying information to TCon.
        AlloyInfo.init();
//        logAlloyList();
    }

    public static void loginfo (String desc) { logger.info(desc); }
    public static void loginfo (String desc, int value)    { logger.info(desc + ": " + value); }
    public static void loginfo (String desc, float value)  { logger.info(desc + ": " + value); }
    public static void loginfo (String desc, String text)  { logger.info(desc + ": " + text); }
    public static void loginfo (String desc, boolean flag) { logger.info(desc + ": " + flag); }
    
    void logAlloyList ()
    {
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
