package toops.tsteelworks.api.event;

import java.util.Map;

public interface IRegistry<Key, Value> extends Iterable<Map.Entry<Key, Value>> {
	boolean addEventListener(IRegistryListener<Key, Value> e);

	boolean removeEventListener(IRegistryListener<Key, Value> e);

	interface IRegistryEvent<Key, Value> {
		/**
		 * @return the event type: ADD if an item has been registered or DELETE if it has been removed
		 */
		TYPE getType();

		/**
		 * @return the deleted or added entry
		 */
		Key getItem();

		/**
		 * @return the deleted or added entry informations
		 */
		Value getData();

		enum TYPE {ADD, DELETE}
	}
}
