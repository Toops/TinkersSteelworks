package tsteelworks.plugins.nei;

import tsteelworks.plugins.ICompatPlugin;

public class NEI implements ICompatPlugin {
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
