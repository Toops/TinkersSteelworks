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
import mantle.lib.TabTools;
import net.minecraft.item.ItemSpade;
import net.minecraftforge.common.MinecraftForge;
import tconstruct.TConstruct;
import tconstruct.tools.TinkerTools;
import tsteelworks.common.core.GuiHandler;
import tsteelworks.common.core.TSCommonProxy;
import tsteelworks.common.core.TSContent;
import tsteelworks.common.core.TSRepo;
import tsteelworks.lib.ConfigCore;
import tsteelworks.lib.TSLogger;
import tsteelworks.lib.TSteelworksRegistry;
import tsteelworks.plugins.PluginController;
import tsteelworks.common.core.TSEventHandler;
import tsteelworks.plugins.fmp.CompatFMP;
import tsteelworks.plugins.waila.Waila;
import tsteelworks.worldgen.TSBaseWorldGenerator;

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

	// todo: add getters
	public static TSLogger logger;

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
		logger = new TSLogger(event.getModLog(), DEBUG_MODE);
		logger.introMessage();

		ConfigCore.initProps(event.getSuggestedConfigurationFile());
		TSteelworksRegistry.SteelworksCreativeTab = new TabTools(TSRepo.MOD_ID);

		content = new TSContent();

		events = new TSEventHandler();
		MinecraftForge.EVENT_BUS.register(events);

		content.oreRegistry();
		proxy.registerRenderer();
		proxy.readManuals();
		proxy.registerSounds();

		GameRegistry.registerWorldGenerator(new TSBaseWorldGenerator(), 8);

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());

		pluginController.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		pluginController.init();
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
	}

	public static SimpleNetworkWrapper getNetHandler() {
		return instance.netHandler;
	}
}
