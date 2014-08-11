package tsteelworks;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import mantle.lib.TabTools;
import net.minecraftforge.common.MinecraftForge;
import tconstruct.library.TConstructRegistry;
import tsteelworks.common.core.GuiHandler;
import tsteelworks.common.core.TSCommonProxy;
import tsteelworks.common.core.TSContent;
import tsteelworks.common.core.TSRepo;
import tsteelworks.lib.ConfigCore;
import tsteelworks.lib.TSFuelHandler;
import tsteelworks.lib.TSLogger;
import tsteelworks.lib.TSteelworksRegistry;
import tsteelworks.lib.crafting.AlloyInfo;
import tsteelworks.plugins.PluginController;
import tsteelworks.util.TSEventHandler;
import tsteelworks.worldgen.TSBaseWorldGenerator;

/**
 * Tinkers' Construct Expansion: Tinkers' Steelworks
 * Based heavily on preestablished code by SlimeKnights (https://github.com/SlimeKnights)
 *
 * TSteelworks
 *
 * @author Toops
 */
@Mod(modid = TSRepo.modId, name = TSRepo.modName, version = TSRepo.modVer, dependencies = TSRepo.modRequire)
public class TSteelworks {
	// Shared logger
	public static final boolean DEBUG_MODE = false; // for logging (change to false before release!)

	// todo: add getters
	public static TSLogger logger;

	@Instance(TSRepo.modId)
	public static TSteelworks instance;

	@SidedProxy(clientSide = TSRepo.modClientProxy, serverSide = TSRepo.modServProxy)
	public static TSCommonProxy proxy;

	public static TSContent content;
	public static TSEventHandler events;
	public static TSFuelHandler fuelHandler;
	public static boolean thermalExpansionAvailable;
	public static boolean railcraftAvailable;

	public TSteelworks() {
		PluginController.getController().registerBuiltins();
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = new TSLogger(event.getModLog(), DEBUG_MODE);
		logger.introMessage();

		ConfigCore.initProps(event.getSuggestedConfigurationFile());
		TSteelworksRegistry.SteelworksCreativeTab = new TabTools(TSRepo.modId);

		content = new TSContent();

		events = new TSEventHandler();
		MinecraftForge.EVENT_BUS.register(events);

		fuelHandler = new TSFuelHandler();

		content.oreRegistry();
		proxy.registerRenderer();
		proxy.readManuals();
		proxy.registerSounds();

		GameRegistry.registerWorldGenerator(new TSBaseWorldGenerator(), 8);

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());

		PluginController.getController().preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		PluginController.getController().init();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		thermalExpansionAvailable = Loader.isModLoaded("ThermalExpansion");
		railcraftAvailable = Loader.isModLoaded("Railcraft");

		content.createEntities();
		content.addCraftingRecipes();
		content.modIntegration();
		content.registerMixerMaterials();

		GameRegistry.registerFuelHandler(fuelHandler);
		PluginController.getController().postInit();

		// Initialize dealloying information at the last possible minute, to ensure that other
		// mods have a chance to get their alloying information to TCon.
		AlloyInfo.init();
	}
}
