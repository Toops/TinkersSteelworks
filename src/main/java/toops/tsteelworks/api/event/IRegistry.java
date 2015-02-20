package toops.tsteelworks.api.event;

public interface IRegistry<Key, Value> {
	public boolean addEventListener(IRegistryListener<Key, Value> e);
	public boolean removeEventListener(IRegistryListener<Key, Value> e);

	public static interface IRegistryEvent<Key, Value> {
		public enum TYPE{ADD, DELETE}

		/**
		 * @return the event type: ADD if an item has been registered or DELETE if it has been removed
		 */
		public TYPE getType();

		/**
		 * @return the deleted or added entry
		 */
		public Key getItem();

		/**
		 * @return the deleted or added entry informations
		 */
		public Value getData();
	}
}
