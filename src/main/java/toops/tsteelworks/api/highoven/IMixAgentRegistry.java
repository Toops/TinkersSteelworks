package toops.tsteelworks.api.highoven;

import net.minecraft.item.ItemStack;
import toops.tsteelworks.api.PluginFactory;
import toops.tsteelworks.api.event.IRegistry;

public interface IMixAgentRegistry extends IRegistry<String, IMixAgentRegistry.IMixAgent> {
	public static final IMixAgentRegistry INSTANCE = (IMixAgentRegistry) PluginFactory.getInstance(IMixAgentRegistry.class);

	public static enum AgentType {
		OXIDIZER, REDUCER, PURIFIER
	}

	/**
	 * Adds every ItemStack registered as oreName in the oredictionnary as valid Agents
	 *
	 * @param oreName       The oredict name
	 * @param type          Agent type
	 * @param consumeChance Chance that an item will be consumed
	 * @return The previously registered information for this oreName or null if it wasn't already registered.
	 */
	public IMixAgent registerAgent(String oreName, AgentType type, int consumeChance);

	/**
	 * Removes an ItemStack as valid Agent
	 *
	 * @param oreName   The oredict entry to unregister
	 * @return the agent data, or null if it did not exist
	 */
	public IMixAgent unregisterAgent(String oreName);

	/**
	 * Gets informations about an agent
	 *
	 * @param itemStack     The agent itemstack
	 * @return the informations on the agent, or null if it does not exist
	 */
	public IMixAgent getAgentData(ItemStack itemStack);

	/**
	 * Hold information (mixer type, consume amount &amp; consume chance) for a mix agent
	 */
	public interface IMixAgent {
		public AgentType getType();

		public int getConsumeChance();
	}
}
