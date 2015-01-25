package toops.tsteelworks.common.plugins.minetweaker3.handler.mix;

import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import minetweaker.api.liquid.ILiquidStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import toops.tsteelworks.api.highoven.IMixAgentRegistry;

import static toops.tsteelworks.common.plugins.minetweaker3.MinetweakerPlugin.parseItem;
import static toops.tsteelworks.common.plugins.minetweaker3.MinetweakerPlugin.parseLiquid;

@ZenClass("mods.tsteelworks.mix")
public class MixWrapper {
	// Mixs
	@ZenMethod
	public static void addFluidMix(ILiquidStack input, String oxidizer, String reducer, String purifier, ILiquidStack output) {
		MineTweakerAPI.apply(new MixerHandler.Add(parseLiquid(input).getFluid(), oxidizer, reducer, purifier, parseLiquid(output)));
	}

	@ZenMethod
	public static void addSolidMix(ILiquidStack input, String oxidizer, String reducer, String purifier, IItemStack output) {
		MineTweakerAPI.apply(new MixerHandler.Add(parseLiquid(input).getFluid(), oxidizer, reducer, purifier, parseItem(output)));
	}

	@ZenMethod
	public static void removeMix(ILiquidStack input, String oxidizer, String reducer, String purifier) {
		MineTweakerAPI.apply(new MixerHandler.Remove(parseLiquid(input).getFluid(), oxidizer, reducer, purifier));
	}

	// Agents
	@ZenMethod
	public static void addOxidizer(String agent, int consumeChance) {
		MineTweakerAPI.apply(new MixAgentHandler.Add(agent, consumeChance, IMixAgentRegistry.AgentType.OXIDIZER));
	}

	@ZenMethod
	public static void addReducer(String agent, int consumeChance) {
		MineTweakerAPI.apply(new MixAgentHandler.Add(agent, consumeChance, IMixAgentRegistry.AgentType.REDUCER));
	}

	@ZenMethod
	public static void addPurifier(String agent, int consumeChance) {
		MineTweakerAPI.apply(new MixAgentHandler.Add(agent, consumeChance, IMixAgentRegistry.AgentType.PURIFIER));
	}

	@ZenMethod
	public static void removeAgent(String agent) {
		MineTweakerAPI.apply(new MixAgentHandler.Remove(agent));
	}
}
