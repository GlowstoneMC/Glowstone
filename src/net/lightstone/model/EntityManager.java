package net.lightstone.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class EntityManager {

	private final Map<Integer, Entity> entities = new HashMap<Integer, Entity>();
	private final Map<Class<? extends Entity>, Collection<? extends Entity>> groupedEntities = new HashMap<Class<? extends Entity>, Collection<? extends Entity>>();
	private int nextId = 1;

	@SuppressWarnings("unchecked")
	public <T extends Entity> Collection<T> getAll(Class<T> type) {
		Collection<T> collection = (Collection<T>) groupedEntities.get(type);
		if (collection == null) {
			collection = new ArrayList<T>();
			groupedEntities.put(type, collection);
		}
		return collection;
	}

	public Entity getEntity(int id) {
		return entities.get(id);
	}

	@SuppressWarnings("unchecked")
	int allocate(Entity entity) {
		for (int id = nextId; id < Integer.MAX_VALUE; id++) {
			if (!entities.containsKey(id)) {
				entities.put(id, entity);
				((Collection<Entity>) getAll(entity.getClass())).add(entity);
				nextId = id + 1;
				return id;
			}
		}

		for (int id = Integer.MIN_VALUE; id < -1; id++) { // as -1 is used as a special value
			if (!entities.containsKey(id)) {
				entities.put(id, entity);
				getAll(entity.getClass()).remove(entity);
				nextId = id + 1;
				return id;
			}
		}

		throw new IllegalStateException("No free entity ids");
	}

	void deallocate(Entity entity) {
		entities.remove(entity.getId());
	}

}
