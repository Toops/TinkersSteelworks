package toops.tsteelworks;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import toops.tsteelworks.common.core.ConfigCore;
import toops.tsteelworks.common.core.TSCommonProxy;
import toops.tsteelworks.common.core.TSLogger;
import toops.tsteelworks.common.plugins.PluginController;
import toops.tsteelworks.common.plugins.chisel.ChiselPlugin;
import toops.tsteelworks.common.plugins.minetweaker3.MinetweakerPlugin;
import toops.tsteelworks.common.plugins.mods.FMPPlugin;
import toops.tsteelworks.common.plugins.mods.ThaumcraftPlugin;
import toops.tsteelworks.common.plugins.tconstruct.TConstructPlugin;
import toops.tsteelworks.common.plugins.waila.WailaPlugin;
import toops.tsteelworks.lib.TSRepo;

/**
 * <p>Tinkers' Construct Expansion: Tinkers' Steelworks.</p>
 * <p>Based heavily on preestablished code by SlimeKnights (https://github.com/SlimeKnights).</p>
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

	private SimpleNetworkWrapper netHandler = NetworkRegistry.INSTANCE.newSimpleChannel(TSRepo.MOD_ID);
	private PluginController pluginController = new PluginController();

	public TSteelworks() {
		TSLogger.printIntroMessage();

		pluginController.registerPlugin(new FMPPlugin());
		pluginController.registerPlugin(new WailaPlugin());
		//pluginController.registerPlugin(new RailcraftPlugin());
		pluginController.registerPlugin(new ThaumcraftPlugin());
		pluginController.registerPlugin(Plugins.TConstruct);
		pluginController.registerPlugin(new MinetweakerPlugin());
		pluginController.registerPlugin(new ChiselPlugin());
	}

	public static SimpleNetworkWrapper getNetHandler() {
		return instance.netHandler;
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ConfigCore.preInit(event.getSuggestedConfigurationFile());

		proxy.preInit();

		pluginController.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init();

		pluginController.init();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		ConfigCore.postInit();

		proxy.postInit();

		pluginController.postInit();

		// we don't need the plugins anymore, go go GC go
		pluginController = null;
	}

	/**
	 * Stores the plugins that are actually useful elsewhere in the code
	 */
	public static class Plugins {
		public static final TConstructPlugin TConstruct = new TConstructPlugin();
	}
}
