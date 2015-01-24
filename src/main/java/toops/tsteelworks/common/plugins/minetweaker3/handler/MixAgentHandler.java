package toops.tsteelworks.common.plugins.minetweaker3.handler;

import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import toops.tsteelworks.api.highoven.IMixAgentRegistry;

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

	private static class Add implements IUndoableAction {
		private final String agent;
		private final int consumeAmount;
		private final int consumeChance;
		private final IMixAgentRegistry.AgentType agentType;

		public Add(String agent, int consumeAmount, int consumeChance, IMixAgentRegistry.AgentType agentType) {
			this.consumeAmount = consumeAmount;
			this.consumeChance = consumeChance;
			this.agentType = agentType;
			this.agent = agent;
		}

		@Override
		public void apply() {
			IMixAgentRegistry.INSTANCE.registerAgent(agent, agentType, consumeAmount, consumeChance);
		}

		@Override
		public boolean canUndo() {
			return true;
		}

		@Override
		public void undo() {
			IMixAgentRegistry.INSTANCE.unregisterAgent(agent);
		}

		@Override
		public String describe() {
			return "Added " + agent + " as valid mixing agent.";
		}

		@Override
		public String describeUndo() {
			return "Removed " + agent + " as valid mixing agent.";
		}

		@Override
		public Object getOverrideKey() {
			return null;
		}
	}

	private static class Remove implements IUndoableAction {
		private final String agent;
		private int consumeAmount;
		private int consumeChance;
		private IMixAgentRegistry.AgentType agentType;

		public Remove(String agent) {
			this.agent = agent;
		}

		@Override
		public void apply() {
			IMixAgentRegistry.IMixAgent data = IMixAgentRegistry.INSTANCE.unregisterAgent(agent);

			consumeAmount = data.getConsumeAmount();
			consumeChance = data.getConsumeChance();
			agentType = data.getType();
		}

		@Override
		public boolean canUndo() {
			return true;
		}

		@Override
		public void undo() {
			IMixAgentRegistry.INSTANCE.registerAgent(agent, agentType, consumeAmount, consumeChance);
		}

		@Override
		public String describeUndo() {
			return "Added " + agent + " as valid mixing agent.";
		}

		@Override
		public String describe() {
			return "Removed " + agent + " as valid mixing agent.";
		}

		@Override
		public Object getOverrideKey() {
			return null;
		}
	}
}
