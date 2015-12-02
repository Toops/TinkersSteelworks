package toops.tsteelworks.common.plugins;

import toops.tsteelworks.common.core.TSLogger;

import java.util.ArrayList;
import java.util.List;

public class PluginController {
	private List<ICompatPlugin> plugins = new ArrayList<>();
	private Phase currPhase = Phase.PRELAUNCH;

	public void init() {
		currPhase = Phase.INIT;
		for (final ICompatPlugin plugin : plugins)
			plugin.init();
	}

	public void postInit() {
		currPhase = Phase.POSTINIT;
		for (final ICompatPlugin plugin : plugins)
			plugin.postInit();

		currPhase = Phase.DONE;
	}

	public void preInit() {
		currPhase = Phase.PREINIT;
		for (final ICompatPlugin plugin : plugins)
			plugin.preInit();
	}

	public void registerPlugin(ICompatPlugin plugin) {
		if (plugin.mayLoad()) {
			TSLogger.info("Registering compat plugin for " + plugin.getPluginName());
			plugins.add(plugin);

			if (currPhase != Phase.PRELAUNCH)
				throw new IllegalStateException("Compat plugins must be registered before preinit");
		}
	}

	private enum Phase {
		PRELAUNCH, PREINIT, INIT, POSTINIT, DONE
	}
}
