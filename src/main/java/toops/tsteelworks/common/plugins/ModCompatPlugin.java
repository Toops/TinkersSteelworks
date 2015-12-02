package toops.tsteelworks.common.plugins;

import cpw.mods.fml.common.Loader;

public abstract class ModCompatPlugin implements ICompatPlugin {
	/**
	 * @return The Mod ID this plugin handles.
	 */
	public abstract String getModId();

	@Override
	public String getPluginName() {
		return getModId();
	}

	@Override
	public boolean mayLoad() {
		return Loader.isModLoaded(getModId());
	}
}
