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
	public static void addFluidMix(ILiquidStack input, String oxidizer, String purifier, String reducer, ILiquidStack output) {
		MineTweakerAPI.apply(new MixerHandler.Add(parseLiquid(input).getFluid(), oxidizer, purifier, reducer, parseLiquid(output)));
	}

	@ZenMethod
	public static void addSolidMix(ILiquidStack input, String oxidizer, String purifier, String reducer, IItemStack output) {
		MineTweakerAPI.apply(new MixerHandler.Add(parseLiquid(input).getFluid(), oxidizer, purifier, reducer, parseItem(output)));
	}

	@ZenMethod
	public static void removeMix(ILiquidStack input, String oxidizer, String purifier, String reducer) {
		MineTweakerAPI.apply(new MixerHandler.Remove(parseLiquid(input).getFluid(), oxidizer, purifier, reducer));
	}

	// Agents
	@ZenMethod
	public static void addOxidizer(String agent, int consumeChance, int consumeAmount) {
		MineTweakerAPI.apply(new MixAgentHandler.Add(agent, consumeAmount, consumeChance, IMixAgentRegistry.AgentType.OXIDIZER));
	}

	@ZenMethod
	public static void addReducer(String agent, int consumeChance, int consumeAmount) {
		MineTweakerAPI.apply(new MixAgentHandler.Add(agent, consumeAmount, consumeChance, IMixAgentRegistry.AgentType.REDUCER));
	}

	@ZenMethod
	public static void addPurifier(String agent, int consumeChance, int consumeAmount) {
		MineTweakerAPI.apply(new MixAgentHandler.Add(agent, consumeAmount, consumeChance, IMixAgentRegistry.AgentType.PURIFIER));
	}

	@ZenMethod
	public static void removeAgent(String agent) {
		MineTweakerAPI.apply(new MixAgentHandler.Remove(agent));
	}
}
