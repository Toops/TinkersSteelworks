package toops.tsteelworks.lib.registry;

import toops.tsteelworks.api.event.IRegistry;
import toops.tsteelworks.api.event.IRegistryListener;

import java.util.ArrayList;
import java.util.List;

class BasicRegistry<Key, Value> implements IRegistry<Key, Value> {
	private List<IRegistryListener<Key, Value>> listeners = new ArrayList<>();

	@Override
	public boolean addEventListener(IRegistryListener<Key, Value> e) {
		return listeners.add(e);
	}

	@Override
	public boolean removeEventListener(IRegistryListener<Key, Value> e) {
		return listeners.remove(e);
	}

	protected void dispatchAddEvent(Key item, Value data) {
		if (listeners.size() == 0) return;

		IRegistryEvent<Key, Value> event = new BasicRegistryEvent<>(IRegistryEvent.TYPE.ADD, data, item);

		for (IRegistryListener<Key, Value> l : listeners) {
			l.onRegistryChange(event);
		}
	}

	protected void dispatchDeleteEvent(Key item, Value data) {
		if (listeners.size() == 0) return;

		IRegistryEvent<Key, Value> event = new BasicRegistryEvent<>(IRegistryEvent.TYPE.ADD, data, item);

		for (IRegistryListener<Key, Value> l : listeners) {
			l.onRegistryChange(event);
		}
	}

	private static class BasicRegistryEvent<Key, Value> implements IRegistryEvent<Key, Value> {
		private final TYPE type;
		private final Value data;
		private final Key item;

		private BasicRegistryEvent(TYPE type, Value data, Key key) {
			this.type = type;
			this.data = data;
			this.item = key;
		}

		@Override
		public TYPE getType() {
			return type;
		}

		@Override
		public Key getItem() {
			return item;
		}

		@Override
		public Value getData() {
			return data;
		}
	}
}
