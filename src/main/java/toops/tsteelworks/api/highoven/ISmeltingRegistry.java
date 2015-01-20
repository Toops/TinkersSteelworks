package toops.tsteelworks.api.highoven;

import toops.tsteelworks.api.PluginFactory;

public interface ISmeltingRegistry {
	public static final ISmeltingRegistry INSTANCE = (ISmeltingRegistry) PluginFactory.getInstance(ISmeltingRegistry.class);


}
