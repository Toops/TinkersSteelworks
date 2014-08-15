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
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.common.MinecraftForge;
import tsteelworks.common.core.*;
import tsteelworks.common.worldgen.TSBaseWorldGenerator;
import tsteelworks.plugins.PluginController;
import tsteelworks.plugins.fmp.CompatFMP;
import tsteelworks.plugins.waila.Waila;

/**
 * Tinkers' Construct Expansion: Tinkers' Steelworks
 * Based heavily on preestablished code by SlimeKnights (https://github.com/SlimeKnights)
 *
 * TSteelworks
 *
 * @author Toops
 */
@Mod(modid = TSRepo.MOD_ID, name = TSRepo.MOD_NAME, version = TSRepo.MOD_VER, dependencies = TSRepo.MOD_REQUIRE)
public class TSteelworks {
	public static final boolean DEBUG_MODE = false;

	@Instance(TSRepo.MOD_ID)
	public static TSteelworks instance;

	@SidedProxy(clientSide = TSRepo.MOD_CLIENT_PROXY, serverSide = TSRepo.MOD_SERV_PROXY)
	public static TSCommonProxy proxy;

	public static TSContent content;
	public static TSEventHandler events;
	public static boolean thermalExpansionAvailable;
	public static boolean railcraftAvailable;

	private SimpleNetworkWrapper netHandler = NetworkRegistry.INSTANCE.newSimpleChannel(TSRepo.MOD_ID);

	private PluginController pluginController = new PluginController();

	public TSteelworks() {
		pluginController.registerPlugin(new CompatFMP());
		pluginController.registerPlugin(new Waila());
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		TSLogger.introMessage();

		ConfigCore.preInit(event.getSuggestedConfigurationFile());

		proxy.preInit();

		content = new TSContent();

		events = new TSEventHandler();
		MinecraftForge.EVENT_BUS.register(events);

		content.oreRegistry();

		GameRegistry.registerWorldGenerator(new TSBaseWorldGenerator(), 8);

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());

		pluginController.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		pluginController.init();
		proxy.init();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		thermalExpansionAvailable = Loader.isModLoaded("ThermalExpansion");
		railcraftAvailable = Loader.isModLoaded("Railcraft");

		content.createEntities();
		content.addCraftingRecipes();
		content.modIntegration();
		content.registerMixerMaterials();

		pluginController.postInit();

		proxy.postInit();

		ConfigCore.postInit();
	}

	public static SimpleNetworkWrapper getNetHandler() {
		return instance.netHandler;
	}
}
