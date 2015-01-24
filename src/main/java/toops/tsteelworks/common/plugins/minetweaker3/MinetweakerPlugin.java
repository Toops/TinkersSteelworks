package toops.tsteelworks.common.plugins.minetweaker3;

import minetweaker.MineTweakerAPI;
import toops.tsteelworks.common.plugins.ModCompatPlugin;
import toops.tsteelworks.common.plugins.minetweaker3.handler.FuelHandler;
import toops.tsteelworks.common.plugins.minetweaker3.handler.MixAgentHandler;
import toops.tsteelworks.common.plugins.minetweaker3.handler.MixerHandler;
import toops.tsteelworks.common.plugins.minetweaker3.handler.SmeltingHandler;

public class MinetweakerPlugin extends ModCompatPlugin {
	@Override
	public String getModId() {
		return "MineTweaker3";
	}

	@Override
	public void preInit() {}

	@Override
	public void init() {
		MineTweakerAPI.registerClass(FuelHandler.class);
		MineTweakerAPI.registerClass(MixerHandler.class);
		MineTweakerAPI.registerClass(MixAgentHandler.class);
		MineTweakerAPI.registerClass(SmeltingHandler.class);
	}

	@Override
	public void postInit() {}
}
