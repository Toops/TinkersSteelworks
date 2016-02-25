package toops.tsteelworks.api.event;

/**
 * Implements this if you wish to be alerted of event changes
 *
 * @param <Key>   the object type registered by the registry
 * @param <Value> the data type associated with Key
 */
public interface IRegistryListener<Key, Value> {
	void onRegistryChange(IRegistry.IRegistryEvent<Key, Value> event);
}