package tsteelworks.common.plugins.nei;

import tsteelworks.common.plugins.ModCompatPlugin;

public class NEIPlugin extends ModCompatPlugin {
	@Override
	public String getModId() {
		return "NotEnoughItems";
	}

	@Override
	public void init() {}

	@Override
	public void postInit() {
		NeiRegistrar.register();
	}

	@Override
	public void preInit() {}
}
