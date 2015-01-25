package toops.tsteelworks.common.plugins.minetweaker3.handler.mix;

import toops.tsteelworks.api.highoven.IMixAgentRegistry;
import toops.tsteelworks.api.highoven.IMixAgentRegistry.IMixAgent;
import toops.tsteelworks.common.plugins.minetweaker3.MinetweakerPlugin;

class MixAgentHandler {
	static class Add extends MinetweakerPlugin.Add<String, IMixAgent> {
		public Add(final String agent, final int consumeChance, final IMixAgentRegistry.AgentType agentType) {
			super(agent, new IMixAgent() {
				@Override
				public IMixAgentRegistry.AgentType getType() {
					return agentType;
				}

				@Override
				public int getConsumeChance() {
					return consumeChance;
				}
			});
		}

		@Override
		public void apply() {
			oldData = IMixAgentRegistry.INSTANCE.registerAgent(key, newData.getType(), newData.getConsumeChance());
		}

		@Override
		public void undo() {
			if (oldData == null)
				IMixAgentRegistry.INSTANCE.unregisterAgent(key);
			else
				IMixAgentRegistry.INSTANCE.registerAgent(key, oldData.getType(), oldData.getConsumeChance());
		}

		@Override
		public String describe() {
			return (oldData == null ? "Added " : "Replaced ") + key + " as valid mixing agent.";
		}
	}

	static class Remove extends MinetweakerPlugin.Remove<String, IMixAgent> {
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
			
			IMixAgentRegistry.INSTANCE.registerAgent(key, oldData.getType(), oldData.getConsumeChance());
		}

		@Override
		public String describe() {
			return "Removed " + key + " as valid mixing agent.";
		}
	}
}
