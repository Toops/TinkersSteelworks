package toops.tsteelworks.common.plugins.minetweaker3.handler;

import minetweaker.MineTweakerAPI;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import toops.tsteelworks.api.highoven.IMixAgentRegistry;
import toops.tsteelworks.api.highoven.IMixAgentRegistry.IMixAgent;
import toops.tsteelworks.common.plugins.minetweaker3.MinetweakerPlugin;

@ZenClass("mods.tsteelworks.mix")
public class MixAgentHandler {
	@ZenMethod
	public static void addOxidizer(String agent, int consumeChance, int consumeAmount) {
		MineTweakerAPI.apply(new Add(agent, consumeAmount, consumeChance, IMixAgentRegistry.AgentType.OXIDIZER));
	}

	@ZenMethod
	public static void addReducer(String agent, int consumeChance, int consumeAmount) {
		MineTweakerAPI.apply(new Add(agent, consumeAmount, consumeChance, IMixAgentRegistry.AgentType.REDUCER));
	}

	@ZenMethod
	public static void addPurifier(String agent, int consumeChance, int consumeAmount) {
		MineTweakerAPI.apply(new Add(agent, consumeAmount, consumeChance, IMixAgentRegistry.AgentType.PURIFIER));
	}

	@ZenMethod
	public static void removeAgent(String agent) {
		MineTweakerAPI.apply(new Remove(agent));
	}

	private static class Add extends MinetweakerPlugin.Add<String, IMixAgent> {
		public Add(final String agent, final int consumeAmount, final int consumeChance, final IMixAgentRegistry.AgentType agentType) {
			super(agent, new IMixAgent() {
				@Override
				public IMixAgentRegistry.AgentType getType() {
					return agentType;
				}

				@Override
				public int getConsumeAmount() {
					return consumeAmount;
				}

				@Override
				public int getConsumeChance() {
					return consumeChance;
				}
			});
		}

		@Override
		public void apply() {
			oldData = IMixAgentRegistry.INSTANCE.registerAgent(key, newData.getType(), newData.getConsumeAmount(), newData.getConsumeChance());
		}

		@Override
		public void undo() {
			if (oldData == null)
				IMixAgentRegistry.INSTANCE.unregisterAgent(key);
			else
				IMixAgentRegistry.INSTANCE.registerAgent(key, oldData.getType(), oldData.getConsumeAmount(), oldData.getConsumeChance());
		}

		@Override
		public String describe() {
			return (oldData == null ? "Added " : "Replaced ") + key + " as valid mixing agent.";
		}
	}

	private static class Remove extends MinetweakerPlugin.Remove<String, IMixAgent> {
		public Remove(String agent) {
			super(agent);
		}

		@Override
		public void apply() {
			oldData = IMixAgentRegistry.INSTANCE.unregisterAgent(key);
		}

		@Override
		public void undo() {
			if (oldData == null) return;
			
			IMixAgentRegistry.INSTANCE.registerAgent(key, oldData.getType(), oldData.getConsumeAmount(), oldData.getConsumeChance());
		}

		@Override
		public String describe() {
			return "Removed " + key + " as valid mixing agent.";
		}
	}
}
