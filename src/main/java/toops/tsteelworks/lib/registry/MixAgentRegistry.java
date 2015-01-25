package toops.tsteelworks.lib.registry;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import toops.tsteelworks.api.highoven.IMixAgentRegistry;

import java.util.HashMap;
import java.util.Map;

public class MixAgentRegistry extends BasicRegistry<String, IMixAgentRegistry.IMixAgent> implements IMixAgentRegistry {
	/* ========== IMixAgentRegistry ========== */
	/** list of mix information, oredict itemstack to mix info (mix type, consume amount & chance) */
	private final Map<String, MixAgent> mixItemList = new HashMap<>();

	@Override
	public IMixAgent registerAgent(String oreName, IMixAgentRegistry.AgentType type, int chance) {
		MixAgent newAgent = new MixAgent(type, chance);
		MixAgent oldAgent = mixItemList.put(oreName, newAgent);

		if (oldAgent != null)
			dispatchDeleteEvent(oreName, oldAgent);

		dispatchAddEvent(oreName, newAgent);
		
		return oldAgent;
	}

	@Override
	public IMixAgentRegistry.IMixAgent getAgentData(ItemStack itemStack) {
		int ids[] = OreDictionary.getOreIDs(itemStack);

		for (int id : ids) {
			String name = OreDictionary.getOreName(id);
			if (mixItemList.containsKey(name))
				return mixItemList.get(name);
		}

		return null;
	}

	@Override
	public IMixAgentRegistry.IMixAgent unregisterAgent(String oreName) {
		MixAgent oldAgent = mixItemList.remove(oreName);

		if (oldAgent != null) dispatchDeleteEvent(oreName, oldAgent);

		return oldAgent;
	}

	private static class MixAgent implements IMixAgentRegistry.IMixAgent {
		private final IMixAgentRegistry.AgentType type;
		private final int consumeChance;

		public MixAgent(IMixAgentRegistry.AgentType type, int consumeChance) {
			this.type = type;
			this.consumeChance = consumeChance;
		}

		@Override
		public IMixAgentRegistry.AgentType getType() {
			return type;
		}

		@Override
		public int getConsumeChance() {
			return consumeChance;
		}
	}
}
